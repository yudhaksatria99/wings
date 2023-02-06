package com.example.myassesment.ui.detail

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.myassesment.R
import kotlinx.android.synthetic.main.activity_cart.*
import kotlinx.android.synthetic.main.activity_detail_product.*

class DetailProduct : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_product)


        val namaProd = intent.getStringExtra("Extra_Name")
        val priceProd = intent.getStringExtra("Extra_Price")

        txtPriceProduk.setText(priceProd)
        txtNameProduk.setText(namaProd)


        btnbackDetail.setOnClickListener { finish() }
    }
}