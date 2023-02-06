package com.example.myassesment.listener

import com.example.myassesment.model.CartModel

interface CartLoadListener {
    fun onLoadCartSuccess(cartModelList: List<CartModel>)
    fun onLoadCartFailed(message:String?)
}