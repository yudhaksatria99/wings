package com.example.myassesment.listener

import com.example.myassesment.model.ProductModel

interface ProductLoadListener {
    fun onProductLoadSuccess(productModelList: List<ProductModel>?)
    fun onProductLoadFailed(message:String?)
}