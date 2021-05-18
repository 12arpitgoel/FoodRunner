package com.example.foodapp.adapter

import android.content.Context
import android.content.Intent
import android.os.AsyncTask
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.example.foodapp.R
import com.example.foodapp.activity.RestaurantMenu
import com.example.foodapp.database.FavResEntity
import com.example.foodapp.database.ResDatabase
import com.example.foodapp.model.Restaurants
import com.squareup.picasso.Picasso

class HomeRecyclerAdapter(val context: Context, private val restaurantsList: List<Restaurants>) :
    RecyclerView.Adapter<HomeRecyclerAdapter.HomeViewHolder>() {

    class HomeViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imRestaurantImage: ImageView = view.findViewById(R.id.imgRestaurantImage)
        val txtRestaurantName: TextView = view.findViewById(R.id.txtRestaurantName)
        val txtRestaurantPrice: TextView = view.findViewById(R.id.txtRestaurantPrice)
        val imgNoFavouriteIcon: ImageView = view.findViewById(R.id.imgNoFavouriteIcon)
        val imgFavouriteRedIcon: ImageView = view.findViewById(R.id.imgFavouriteRedIcon)
        val txtRestaurantRating: TextView = view.findViewById(R.id.txtRestaurantRating)

        val llContent: LinearLayout = view.findViewById(R.id.llContent)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.recycler_home_single_row, parent, false)
        return HomeViewHolder(view)
    }

    override fun getItemCount(): Int {
        return restaurantsList.size
    }

    override fun onBindViewHolder(holder: HomeViewHolder, position: Int) {
        val restaurantListItem = restaurantsList[position]
        Picasso.get().load(restaurantListItem.image_url).error(R.drawable.restaurant_img)
            .into(holder.imRestaurantImage);
        holder.txtRestaurantName.text = restaurantListItem.name
        holder.txtRestaurantPrice.text = restaurantListItem.cost_for_one
        holder.txtRestaurantRating.text = restaurantListItem.rating

        holder.llContent.setOnClickListener {
            //Toast.makeText(context,"Clicked on ${holder.txtRestaurantName.text}",Toast.LENGTH_SHORT).show()
            val intent = Intent(context, RestaurantMenu::class.java)
            intent.putExtra("restaurantId", restaurantListItem.id)
            intent.putExtra("restaurantName", restaurantListItem.name)
            context.startActivity(intent)
        }


        val favResEntity = FavResEntity(
            restaurantListItem.id.toInt(),
            restaurantListItem.name,
            restaurantListItem.rating,
            restaurantListItem.cost_for_one,
            restaurantListItem.image_url
        )
        val checkFav = DBAsyncTask(context, favResEntity, 1).execute()
        val isFav = checkFav.get()

        if (isFav) {
            holder.imgNoFavouriteIcon.visibility = View.GONE
            holder.imgFavouriteRedIcon.visibility = View.VISIBLE
        } else {
            holder.imgNoFavouriteIcon.visibility = View.VISIBLE
            holder.imgFavouriteRedIcon.visibility = View.GONE
        }

        holder.imgNoFavouriteIcon.setOnClickListener {
            val async = DBAsyncTask(context, favResEntity, 2).execute()
            val result = async.get()
            if (result) {
                Toast.makeText(
                    context,
                    "${restaurantListItem.name} added to Favourites",
                    Toast.LENGTH_SHORT
                ).show()
                holder.imgNoFavouriteIcon.visibility = View.GONE
                holder.imgFavouriteRedIcon.visibility = View.VISIBLE
            } else {
                Toast.makeText(
                    context,
                    "Some Error Occurred",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
        holder.imgFavouriteRedIcon.setOnClickListener {
            val async = DBAsyncTask(context, favResEntity, 3).execute()
            val result = async.get()
            if (result) {
                Toast.makeText(
                    context,
                    "${restaurantListItem.name} removed from Favourites",
                    Toast.LENGTH_SHORT
                ).show()
                holder.imgNoFavouriteIcon.visibility = View.VISIBLE
                holder.imgFavouriteRedIcon.visibility = View.GONE
            } else {
                Toast.makeText(
                    context,
                    "Some Error Occurred",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }


}

class DBAsyncTask(
    val context: Context,
    private val favResEntity: FavResEntity,
    private val mode: Int
) :
    AsyncTask<Void, Void, Boolean>() {
    /*
    Mode1->Check DB if tje book is favaourite or not
    MODE2->Save the book into DB as favourite
    MODE3->Remove the favourite book
     */

    private val db = Room.databaseBuilder(context, ResDatabase::class.java, "res-db").build()

    override fun doInBackground(vararg params: Void?): Boolean {

        when (mode) {
            1 -> {
                //Mode1->Check DB if the book is favourite or not
                val book: FavResEntity? =
                    db.resDao().getResById(favResEntity.res_id.toString())
                db.close()
                return book != null

            }
            2 -> {
                //MODE2->Save the book into DB as favourite
                db.resDao().insert(favResEntity)
                db.close()
                return true
            }
            3 -> {
                //MODE3->Remove the favourite book
                db.resDao().delete(favResEntity)
                db.close()
                return true
            }
        }
        return false
    }

}