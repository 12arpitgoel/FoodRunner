package com.example.foodapp.fragment

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
import com.example.foodapp.adapter.OrderHistoryAdapter
import com.example.foodapp.model.OrderHistoryDetails
import com.example.foodapp.model.RestaurantDetails
import com.example.foodapp.util.ConnectionManager

/**
 * A simple [Fragment] subclass.
 */
class OrderHistoryFragment : Fragment() {

    lateinit var myPreviousOrderRecyclerView: RecyclerView
    lateinit var layoutManager: RecyclerView.LayoutManager
    lateinit var recyclerAdapter: OrderHistoryAdapter
    var orderHistoryInfoList = arrayListOf<OrderHistoryDetails>()


    lateinit var progressLayout: RelativeLayout
    lateinit var progressBar: ProgressBar

    lateinit var sharedPreferences: SharedPreferences

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_order_history, container, false)

        sharedPreferences = this.activity!!
            .getSharedPreferences(getString(R.string.preference_file_name), Context.MODE_PRIVATE)

        setHasOptionsMenu(true)
        myPreviousOrderRecyclerView = view.findViewById(R.id.myPreviousOrderRecyclerView)
        progressLayout = view.findViewById(R.id.progressLayout)
        progressBar = view.findViewById(R.id.progressBar)
        progressLayout.visibility = View.VISIBLE

        setUpAdapter()

        return view
    }

    private fun setUpAdapter() {
        val userId = sharedPreferences.getString("user_id", "null")
        val queue = Volley.newRequestQueue(activity as Context)

        val url = "http://13.235.250.119/v2/orders/fetch_result/$userId"

        if (ConnectionManager().CheckConnectivity(activity as Context)) {
            if(activity!=null) {
                val jsonObjectRequest =
                    object : JsonObjectRequest(Method.GET, url, null, Response.Listener {
                        try {
                            progressLayout.visibility = View.GONE
                            println("Response is $it")
                            val value = it.getJSONObject("data")
                            val success = value.getBoolean("success")

                            if (success) {
                                val data = value.getJSONArray("data")
                                for (i in 0 until data.length()) {
                                    val restaurantJsonObject = data.getJSONObject(i)

                                    val orderDetails =
                                        restaurantJsonObject.getJSONArray("food_items")
                                    val cartInfoList = arrayListOf<RestaurantDetails>()
                                    for (j in 0 until orderDetails.length()) {
                                        val itemInfo = orderDetails.getJSONObject(j)
                                        cartInfoList.add(
                                            RestaurantDetails(
                                                itemInfo.getString("food_item_id"),
                                                itemInfo.getString("name"),
                                                itemInfo.getString("cost")
                                            )
                                        )
                                    }

                                    val restaurantObject = OrderHistoryDetails(
                                        restaurantJsonObject.getString("restaurant_name"),
                                        restaurantJsonObject.getString("order_placed_at"),
                                        cartInfoList
                                    )
                                    orderHistoryInfoList.add(restaurantObject)
                                }
                                layoutManager = LinearLayoutManager(activity)

                                recyclerAdapter =
                                    OrderHistoryAdapter(activity as Context, orderHistoryInfoList)

                                myPreviousOrderRecyclerView.adapter = recyclerAdapter
                                myPreviousOrderRecyclerView.layoutManager = layoutManager

                            } else {
                                Toast.makeText(
                                    activity as Context,
                                    value.getString("errorMessage"),
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        } catch (e: Exception) {
                            Toast.makeText(
                                activity as Context,
                                e.message,
                                //"Some unexpected Error Occurred!!!",
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

    }


}
