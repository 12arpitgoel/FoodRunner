package com.example.foodapp.activity

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.telephony.gsm.GsmCellLocation
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.foodapp.R
import com.example.foodapp.adapter.MenuRecyclerAdapter
import com.example.foodapp.model.RestaurantDetails
import com.example.foodapp.util.ConnectionManager
import com.google.gson.Gson
import org.json.JSONException

class RestaurantMenu : AppCompatActivity() {

    private lateinit var toolbar: androidx.appcompat.widget.Toolbar
    lateinit var recyclerHome: RecyclerView
    lateinit var layoutManager: RecyclerView.LayoutManager
    lateinit var recyclerAdapter: MenuRecyclerAdapter
    var dishInfoList = arrayListOf<RestaurantDetails>()
    var orderInfoList = arrayListOf<RestaurantDetails>()

    lateinit var progressLayout: RelativeLayout
    lateinit var progressBar: ProgressBar

    private var restaurantId: String? = "0"
    private var restaurantName: String? = "Restaurant"

    lateinit var btnProceedToCart: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_restaurant_menu)

        recyclerHome = findViewById(R.id.recyclerMenu)
        progressLayout = findViewById(R.id.progressLayout)
        progressBar = findViewById(R.id.progressBar)
        toolbar = findViewById(R.id.toolbar)
        progressLayout.visibility = View.VISIBLE
        btnProceedToCart = findViewById(R.id.btnProceedToCart)

        if (intent != null) {
            restaurantId = intent.getStringExtra("restaurantId")
            restaurantName = intent.getStringExtra("restaurantName")
        }
        setUpToolbar(restaurantName)

        if (restaurantId == "0") {
            Toast.makeText(this@RestaurantMenu, "Some error occurred", Toast.LENGTH_SHORT).show()
            finish()
        }

        setUpAdapter()

        btnProceedToCart.setOnClickListener {
            //println("order info List $orderInfoList")
            val foodItems = Gson().toJson(orderInfoList)
            val intent = Intent(this@RestaurantMenu, MyCart::class.java)
            intent.putExtra("restaurantId", restaurantId)
            intent.putExtra("restaurantName", restaurantName)
            intent.putExtra("foodItems", foodItems)
            startActivity(intent)
        }

    }

    private fun setUpAdapter() {
        val queue = Volley.newRequestQueue(this@RestaurantMenu)

        val url = "http://13.235.250.119/v2/restaurants/fetch_result/$restaurantId"

        if (ConnectionManager().CheckConnectivity(this@RestaurantMenu)) {
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
                                    val restaurantObject = RestaurantDetails(
                                        restaurantJsonObject.getString("id"),
                                        restaurantJsonObject.getString("name"),
                                        restaurantJsonObject.getString("cost_for_one")
                                    )
                                    dishInfoList.add(restaurantObject)
                                }
                                layoutManager = LinearLayoutManager(this@RestaurantMenu)

                                recyclerAdapter =
                                    MenuRecyclerAdapter(this@RestaurantMenu, dishInfoList,
                                        object : MenuRecyclerAdapter.OnItemClickListener {
                                            override fun onAddItemClick(menuItem: RestaurantDetails) {
                                                orderInfoList.add(menuItem)
                                                if (orderInfoList.size > 0) {
                                                    btnProceedToCart.visibility = View.VISIBLE
                                                }
                                            }

                                            override fun onRemoveItemClick(menuItem: RestaurantDetails) {
                                                orderInfoList.remove(menuItem)
                                                if (orderInfoList.isEmpty()) {
                                                    btnProceedToCart.visibility = View.GONE
                                                }
                                            }
                                        })

                                recyclerHome.adapter = recyclerAdapter
                                recyclerHome.layoutManager = layoutManager

                            } else {
                                Toast.makeText(
                                    this@RestaurantMenu,
                                    "Some Error Occured!!!",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        } catch (e: JSONException) {
                            Toast.makeText(
                                this@RestaurantMenu,
                                "Some unexpected Error Occured!!!",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }, Response.ErrorListener {
                        //println("Error is $it")
                        Toast.makeText(
                            this@RestaurantMenu,
                            "Volley Error Occurred!!!",
                            Toast.LENGTH_SHORT
                        ).show()
                    }) {
                        override fun getHeaders(): MutableMap<String, String> {
                            val headers = HashMap<String, String>()
                            headers["Content-type"] = "application/json"
                            headers["token"] = "f79d8d4e322024"
                            return headers
                        }
                    }
                queue.add(jsonObjectRequest)
        } else {
            val dialog = AlertDialog.Builder(this@RestaurantMenu)
            dialog.setTitle("Error")
            dialog.setMessage("Internet Connection is not Found")
            dialog.setPositiveButton("Open Settings") { _, _ ->
                val settingsIntent = Intent(Settings.ACTION_WIRELESS_SETTINGS)
                startActivity(settingsIntent)
                finish()

            }
            dialog.setNegativeButton("Exit") { _, _ ->
                ActivityCompat.finishAffinity(this@RestaurantMenu)
            }
            dialog.create()
            dialog.show()
        }
    }

    private fun setUpToolbar(restaurantName: String?) {
        setSupportActionBar(toolbar)
        supportActionBar?.title = restaurantName
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == android.R.id.home) {
            onBackPressed()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        if (!orderInfoList.isEmpty()) {
            val dialog = AlertDialog.Builder(this@RestaurantMenu)
            dialog.setTitle("Confirmation")
            dialog.setMessage("Going back will reset cart items. Do you still want to proceed?")
            dialog.setPositiveButton("YES") { _, _ ->
                finish()
            }
            dialog.setNegativeButton("NO") { _, _ ->
            }
            dialog.create()
            dialog.show()
        } else
            super.onBackPressed()
    }

}

