package com.example.foodapp.fragment

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.foodapp.R

/**
 * A simple [Fragment] subclass.
 */
class ProfileFragment : Fragment() {

    private lateinit var userName: TextView
    private lateinit var userMobileNumber: TextView
    private lateinit var userEmail: TextView
    private lateinit var userAddress: TextView
    private lateinit var sharedPreferences: SharedPreferences

    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_profile, container, false)

        sharedPreferences = this.activity!!
            .getSharedPreferences(getString(R.string.preference_file_name), Context.MODE_PRIVATE)

        userName = view.findViewById(R.id.txtUserName)
        userMobileNumber = view.findViewById(R.id.txtMobileNumber)
        userEmail = view.findViewById(R.id.txtEmail)
        userAddress = view.findViewById(R.id.txtAddress)

        userName.text = sharedPreferences.getString("name", "user name")
        val number = sharedPreferences.getString("mobile_number", "number")
        userMobileNumber.text = "+91-$number"
        userEmail.text = sharedPreferences.getString("email", "email")
        userAddress.text = sharedPreferences.getString("address", "address")


        return view
    }

}
