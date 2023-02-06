package com.example.myassesment

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myassesment.adapter.MyProductAdapter
import com.example.myassesment.databinding.ActivityMainBinding
import com.example.myassesment.eventbus.UpdateCartEvent
import com.example.myassesment.listener.CartLoadListener
import com.example.myassesment.listener.ProductLoadListener
import com.example.myassesment.model.CartModel
import com.example.myassesment.model.ProductModel
import com.example.myassesment.ui.cart.CartActivity
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_main.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class MainActivity : AppCompatActivity(),CartLoadListener, ProductLoadListener {

    lateinit var productLoadListener: ProductLoadListener
    private lateinit var dbref : DatabaseReference
    private lateinit var userRecyclerview : RecyclerView
    private lateinit var userArrayList : ArrayList<ProductModel>
    lateinit var cartLoadListener: CartLoadListener


//    lateinit var binding: ActivityMainBinding
    override fun onStart() {
        super.onStart()
    EventBus.getDefault().register(this)
    }

    override fun onStop() {
        super.onStop()
        if (EventBus.getDefault().hasSubscriberForEvent(UpdateCartEvent::class.java))
            EventBus.getDefault().removeStickyEvent(UpdateCartEvent::class.java)
        EventBus.getDefault().unregister(this)
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
     fun onUpdateCartEvent(event: UpdateCartEvent){
        countCartFromFirebase()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(R.layout.activity_main)

        userRecyclerview = findViewById(R.id.recyler_product)
        userRecyclerview.layoutManager = LinearLayoutManager(this)
        userRecyclerview.setHasFixedSize(true)

        userArrayList = arrayListOf<ProductModel>()
//        getUserData()

        init()
        countCartFromFirebase()
        loadProductFromFirebase()


    }

    private fun countCartFromFirebase() {
        val  cartModels : MutableList<CartModel> = ArrayList()
        FirebaseDatabase.getInstance()
            .getReference("Cart")
            .child("UNIQUE_USER_ID")
            .addListenerForSingleValueEvent(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    for (cartSnapshot in snapshot.children){
                        val cartModel = cartSnapshot.getValue(CartModel::class.java)
                        cartModel!!.key = cartSnapshot.key
                        cartModels.add(cartModel)
                    }
                    cartLoadListener.onLoadCartSuccess(cartModels)
                }

                override fun onCancelled(error: DatabaseError) {
                    cartLoadListener.onLoadCartFailed(error.message)
                }

            })
    }

//    private fun getUserData() {
//        dbref = FirebaseDatabase.getInstance().getReference("Produk")
//        dbref.addValueEventListener(object : ValueEventListener {
//            override fun onDataChange(snapshot: DataSnapshot) {
//                if (snapshot.exists()) {
//                    for (userSnapshot in snapshot.children) {
//                        val user = userSnapshot.getValue(ProductModel::class.java)
//                         user!!.key = userSnapshot.key
//                        Log.i("keyUser","${user!!.key}")
//                        userArrayList.add(user!!)
//                    }
//                    userRecyclerview.adapter = MyProductAdapter(this@MainActivity,userArrayList)
//                }
//            }
//
//            override fun onCancelled(error: DatabaseError) {
//                TODO("Not yet implemented")
//            }
//        })
//    }


    private fun loadProductFromFirebase() {
        val productModels : MutableList<ProductModel> = ArrayList()
        FirebaseDatabase.getInstance()
            .getReference("Produk")
            .addListenerForSingleValueEvent(object:ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()){
                        for (productSnapshot in snapshot.children)
                        {
                            val productModel = productSnapshot.getValue(ProductModel::class.java)
                            productModel!!.key = productSnapshot.key
                            productModels.add(productModel)
                        }
                        productLoadListener.onProductLoadSuccess(productModels)

//                        recyler_product.adapter = MyProductAdapter(this,produkArrayList)
                    }else{
                        productLoadListener.onProductLoadFailed("Product Item Not Exist")
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    productLoadListener.onProductLoadFailed(error.message)
                }

            })
    }

    private fun init(){
        productLoadListener = this
        cartLoadListener = this
        btnCart.setOnClickListener { startActivity(Intent(this,CartActivity::class.java)) }
    }

    override fun onLoadCartSuccess(cartModelList: List<CartModel>) {
        var cartSum = 0
        for (cartModel in cartModelList) cartSum+= cartModel!!.quantity
        badge!!.setNumber(cartSum)
    }

    override fun onLoadCartFailed(message: String?) {
        Snackbar.make(mainLayout,message!!,Snackbar.LENGTH_SHORT)
    }

    override fun onProductLoadSuccess(productModelList: List<ProductModel>?) {
        val adapter = MyProductAdapter(this,productModelList!!,cartLoadListener)
        recyler_product.adapter= adapter
    }

    override fun onProductLoadFailed(message: String?) {
        Snackbar.make(mainLayout,message!!,Snackbar.LENGTH_SHORT)
    }
}