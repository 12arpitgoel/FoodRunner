package com.example.foodapp.fragment

import android.content.Context
import android.os.AsyncTask
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.RelativeLayout
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.example.foodapp.R
import com.example.foodapp.adapter.HomeRecyclerAdapter
import com.example.foodapp.database.FavResEntity
import com.example.foodapp.database.ResDatabase
import com.example.foodapp.model.Restaurants

/**
 * A simple [Fragment] subclass.
 */
class FavouritesFragment : Fragment() {

    private lateinit var recyclerFavourite: RecyclerView
    lateinit var progressLayout: RelativeLayout
    lateinit var progressBar: ProgressBar
    private lateinit var layoutManager: RecyclerView.LayoutManager
    private lateinit var recyclerAdapter: HomeRecyclerAdapter
    private lateinit var noFavScreen: RelativeLayout

    private var dbRestaurantsList = arrayListOf<Restaurants>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_favourites, container, false)

        recyclerFavourite = view.findViewById(R.id.recyclerFavourite)
        progressLayout = view.findViewById(R.id.progressLayout)
        noFavScreen = view.findViewById(R.id.noFavScreen)
        progressLayout.visibility = View.VISIBLE
        progressBar = view.findViewById(R.id.progressBar)
        progressBar.visibility = View.VISIBLE


        val dbList = RetrieveFavourites(activity as Context).execute().get()

        if (!dbList.isEmpty()) {
            for (i in dbList) {
                dbRestaurantsList.add(
                    Restaurants(
                        i.res_id.toString(),
                        i.resName,
                        i.rating,
                        i.cost_for_one,
                        i.image_url
                    )
                )
            }

            if (activity != null) {
                progressLayout.visibility = View.GONE
                layoutManager = LinearLayoutManager(activity as Context)
                recyclerAdapter = HomeRecyclerAdapter(activity as Context, dbRestaurantsList)
                recyclerFavourite.adapter = recyclerAdapter
                recyclerFavourite.layoutManager = layoutManager
            }
        } else {
            progressLayout.visibility = View.GONE
            noFavScreen.visibility = View.VISIBLE
        }

        return view
    }

    class RetrieveFavourites(val context: Context) : AsyncTask<Void, Void, List<FavResEntity>>() {
        override fun doInBackground(vararg params: Void?): List<FavResEntity> {

            val db = Room.databaseBuilder(context, ResDatabase::class.java, "res-db").build()

            return db.resDao().getAllFavRes()
        }
    }


}
