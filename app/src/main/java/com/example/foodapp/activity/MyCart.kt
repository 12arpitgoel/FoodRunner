package com.example.foodapp.activity

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.view.MenuItem
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.foodapp.R
import com.example.foodapp.adapter.CartRecyclerAdapter
import com.example.foodapp.model.RestaurantDetails
import com.example.foodapp.util.ConnectionManager
import com.google.gson.Gson
import org.json.JSONArray
import org.json.JSONObject

class MyCart : AppCompatActivity() {

    lateinit var toolbar: androidx.appcompat.widget.Toolbar
    lateinit var recyclerCart: RecyclerView
    lateinit var layoutManager: RecyclerView.LayoutManager
    lateinit var recyclerAdapter: CartRecyclerAdapter
    lateinit var txtRestName: TextView
    lateinit var btnPlaceOrder: Button
    private var cartInfoList = arrayListOf<RestaurantDetails>()

    private var restaurantId: String? = "0"
    private var restaurantName: String? = "Restaurant"
    private var foodItems: String? = ""

    lateinit var sharedPreferences: SharedPreferences

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedPreferences =
            getSharedPreferences(getString(R.string.preference_file_name), Context.MODE_PRIVATE)
        setContentView(R.layout.activity_my_cart)

        recyclerCart = findViewById(R.id.myCartRecyclerView)

        toolbar = findViewById(R.id.toolbar)
        txtRestName = findViewById(R.id.txtRestaurantName)
        btnPlaceOrder = findViewById(R.id.btnPlaceOrder)

        if (intent != null) {
            restaurantId = intent.getStringExtra("restaurantId")
            restaurantName = intent.getStringExtra("restaurantName")
            foodItems = intent.getStringExtra("foodItems")
        }

        if (restaurantId == "0") {
            Toast.makeText(this@MyCart, "Some error occurred", Toast.LENGTH_SHORT).show()
            finish()
        }
        cartInfoList.addAll(
            Gson().fromJson(foodItems, Array<RestaurantDetails>::class.java).asList()
        )
        //println("order info list $cartInfoList")
        setUpToolbar()
        txtRestName.text = restaurantName

        setUpAdapter()

        var totalPrice: Int = 0
        for (i in 0 until cartInfoList.size) {
            totalPrice += cartInfoList[i].DishPrice.toInt()
        }
        btnPlaceOrder.text = "Place Order(Total: Rs. $totalPrice )"

        btnPlaceOrder.setOnClickListener {

            val userId = sharedPreferences.getString("user_id", "null")
            val queue = Volley.newRequestQueue(this@MyCart)
            val url = "http://13.235.250.119/v2/place_order/fetch_result/"
            val jsonParams = JSONObject()
            jsonParams.put("user_id", userId.toString())
            jsonParams.put("restaurant_id", restaurantId.toString())
            jsonParams.put("total_cost", totalPrice.toString())
            val foodArray = JSONArray()
            for (i in 0 until cartInfoList.size) {
                val foodId = JSONObject()
                foodId.put("food_item_id", cartInfoList[i].DishId)
                foodArray.put(i, foodId)
            }
            jsonParams.put("food", foodArray)


            if (ConnectionManager().CheckConnectivity(this@MyCart)) {
                val jsonRequest =
                    object : JsonObjectRequest(
                        Method.POST,
                        url,
                        jsonParams,
                        Response.Listener {
                            try {
                                //println("Response is $it")
                                val data = it.getJSONObject("data")
                                val success = data.getBoolean("success")

                                if (success) {
                                    val intent = Intent(this@MyCart, ConfirmOrder::class.java)
                                    startActivity(intent)
                                    finish()
                                } else {
                                    Toast.makeText(
                                        this@MyCart,
                                        data.getString("errorMessage"),
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            } catch (e: Exception) {
                                Toast.makeText(
                                    this@MyCart,
                                    e.message,
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        },
                        Response.ErrorListener {
                            //println("Error is $it")
                            Toast.makeText(
                                this@MyCart,
                                "Volley Error $it",
                                Toast.LENGTH_SHORT
                            )
                                .show()
                        }) {
                        override fun getHeaders(): MutableMap<String, String> {
                            val headers = HashMap<String, String>()
                            headers["Content-type"] = "application/json"
                            headers["token"] = "f79d8d4e322024"
                            return headers
                        }
                    }

                queue.add(jsonRequest)
            } else {
                val dialog = AlertDialog.Builder(this@MyCart)
                dialog.setTitle("Error")
                dialog.setMessage("Internet Connection is not Found")
                dialog.setPositiveButton("Open Settings") { _, _ ->
                    val settingsIntent = Intent(Settings.ACTION_WIRELESS_SETTINGS)
                    startActivity(settingsIntent)
                    finish()

                }
                dialog.setNegativeButton("Exit") { _, _ ->
                    //closes the app
                    ActivityCompat.finishAffinity(this@MyCart)
                }
                dialog.create()
                dialog.show()
            }
        }


    }

    private fun setUpAdapter() {
        layoutManager = LinearLayoutManager(this@MyCart)

        recyclerAdapter =
            CartRecyclerAdapter(this@MyCart, cartInfoList)

        recyclerCart.adapter = recyclerAdapter
        recyclerCart.layoutManager = layoutManager

    }

    private fun setUpToolbar() {
        setSupportActionBar(toolbar)
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == android.R.id.home) {
            super.onBackPressed()
        }
        return super.onOptionsItemSelected(item)
    }
}



