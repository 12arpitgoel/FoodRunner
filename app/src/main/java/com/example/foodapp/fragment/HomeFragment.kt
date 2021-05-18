package com.example.foodapp.fragment

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.*
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.foodapp.R
import com.example.foodapp.adapter.HomeRecyclerAdapter
import com.example.foodapp.model.Restaurants
import com.example.foodapp.util.ConnectionManager
import org.json.JSONException
import java.util.*
import kotlin.Comparator
import kotlin.collections.HashMap

/**
 * A simple [Fragment] subclass.
 */
class HomeFragment : Fragment() {

    lateinit var recyclerHome: RecyclerView
    lateinit var layoutManager: RecyclerView.LayoutManager
    lateinit var recyclerAdapter: HomeRecyclerAdapter
    var restaurantsInfoList = arrayListOf<Restaurants>()

    lateinit var progressLayout: RelativeLayout
    lateinit var progressBar: ProgressBar
   // var previousMenuItem:MenuItem?=null

    var ratingsComparator = Comparator<Restaurants> { Restaurant1, Restaurant2 ->

        if (Restaurant1.rating.compareTo(Restaurant2.rating, true) == 0) {
            Restaurant1.name.compareTo(Restaurant2.name, true)
        } else {
            Restaurant1.rating.compareTo(Restaurant2.rating, true)
        }
    }

    var costComparator = Comparator<Restaurants> { Restaurant1, Restaurant2 ->

        if (Restaurant1.cost_for_one.compareTo(Restaurant2.cost_for_one, true) == 0) {
            Restaurant1.name.compareTo(Restaurant2.name, true)
        } else {
            Restaurant1.cost_for_one.compareTo(Restaurant2.cost_for_one, true)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        setHasOptionsMenu(true)
        recyclerHome = view.findViewById(R.id.recyclerhome)
        progressLayout = view.findViewById(R.id.progressLayout)
        progressBar = view.findViewById(R.id.progressBar)
        progressLayout.visibility = View.VISIBLE

        val queue = Volley.newRequestQueue(activity as Context)

        val url = "http://13.235.250.119/v2/restaurants/fetch_result/"

        if (ConnectionManager().CheckConnectivity(activity as Context)) {
            if(activity!=null) {
                val jsonObjectRequest =
                    object : JsonObjectRequest(Method.GET, url, null, Response.Listener {
                        try {
                            progressLayout.visibility = View.GONE
                            //println("Response is $it")
                            val value = it.getJSONObject("data")
                            val success = value.getBoolean("success")

                            if (success) {
                                val data = value.getJSONArray("data")
                                for (i in 0 until data.length()) {
                                    val restaurantJsonObject = data.getJSONObject(i)
                                    val restaurantObject = Restaurants(
                                        restaurantJsonObject.getString("id"),
                                        restaurantJsonObject.getString("name"),
                                        restaurantJsonObject.getString("rating"),
                                        "\u20B9" + restaurantJsonObject.getString("cost_for_one") + "/person",
                                        restaurantJsonObject.getString("image_url")
                                    )
                                    restaurantsInfoList.add(restaurantObject)
                                }
                                layoutManager = LinearLayoutManager(activity)

                                recyclerAdapter =
                                    HomeRecyclerAdapter(activity as Context, restaurantsInfoList)

                                recyclerHome.adapter = recyclerAdapter
                                recyclerHome.layoutManager = layoutManager

                            } else {
                                Toast.makeText(
                                    activity as Context,
                                    "Some Error Occurred!!!",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        } catch (e: JSONException) {
                            Toast.makeText(
                                activity as Context,
                                "Some unexpected Error Occurred!!!",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }, Response.ErrorListener {
                        //println("Error is $it")
                        if (activity != null) {
                            Toast.makeText(
                                activity as Context,
                                "Volley Error Occurred!!!",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }) {
                        override fun getHeaders(): MutableMap<String, String> {
                            val headers = HashMap<String, String>()
                            headers["Content-type"] = "application/json"
                            headers["token"] = "f79d8d4e322024"
                            return headers
                        }
                    }
                queue.add(jsonObjectRequest)
            }
        } else {
            val dialog = AlertDialog.Builder(activity as Context)
            dialog.setTitle("Error")
            dialog.setMessage("Internet Connection is not Found")
            dialog.setPositiveButton("Open Settings") { _, _ ->
                val settingsIntent = Intent(Settings.ACTION_WIRELESS_SETTINGS)
                startActivity(settingsIntent)
                activity?.finish()

            }
            dialog.setNegativeButton("Exit") { _, _ ->
                ActivityCompat.finishAffinity(activity as Activity)
            }
            dialog.create()
            dialog.show()
        }


        return view
    }


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_my_home, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        val id = item.itemId
        item.isChecked=true
//        if (previousMenuItem != null) {
//            previousMenuItem?.isChecked = false
//        }
//        previousMenuItem=item
        when (id) {
            R.id.sortCostAsc -> {
                Collections.sort(restaurantsInfoList, costComparator)
            }
            R.id.sortCostDesc -> {
                Collections.sort(restaurantsInfoList, costComparator)
                restaurantsInfoList.reverse()
            }
            R.id.sortRating -> {
                Collections.sort(restaurantsInfoList, ratingsComparator)
                restaurantsInfoList.reverse()
            }

        }
        recyclerAdapter.notifyDataSetChanged()

        return super.onOptionsItemSelected(item)
    }


}
