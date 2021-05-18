package com.example.foodapp.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import androidx.core.app.ActivityCompat
import com.example.foodapp.R

class ConfirmOrder : AppCompatActivity() {

    private lateinit var btnOk: Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_confirm_order)

        btnOk = findViewById(R.id.btnOk)
        btnOk.setOnClickListener {
            val intent = Intent(this@ConfirmOrder, MainActivity::class.java)
            startActivity(intent)
            ActivityCompat.finishAffinity(this@ConfirmOrder)
        }
    }

    override fun onBackPressed() {
    }
}
