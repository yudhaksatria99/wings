package com.example.myassesment.adapter

import android.content.Context
import android.content.Intent
import android.graphics.Paint
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.myassesment.R
import com.example.myassesment.eventbus.UpdateCartEvent
import com.example.myassesment.listener.CartLoadListener
import com.example.myassesment.listener.RecylerviewClickListener
import com.example.myassesment.model.CartModel
import com.example.myassesment.model.ProductModel
import com.example.myassesment.ui.detail.DetailProduct
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import org.greenrobot.eventbus.EventBus

class MyProductAdapter(
    private val  context: Context,
    private val  list: List<ProductModel>,
    private val cartListener: CartLoadListener
): RecyclerView.Adapter<MyProductAdapter.MyProductViewHolder>() {

    class MyProductViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView),
        View.OnClickListener {
        var imageView: ImageView? = null
        var textName: TextView? = null
        var textNormal_Price: TextView? = null
        var price: TextView? = null
        var button: Button? = null

        private var clickListener: RecylerviewClickListener? = null

        fun setClickListener(clickListener: RecylerviewClickListener) {
            this.clickListener = clickListener
        }

        init {
            imageView = itemView.findViewById(R.id.imageView) as ImageView
            textName = itemView.findViewById(R.id.txtName) as TextView
            textNormal_Price = itemView.findViewById(R.id.txtNormalPrice) as TextView
            price = itemView.findViewById(R.id.txtPrice) as TextView
            button = itemView.findViewById(R.id.btnBuy) as Button

            button!!.setOnClickListener(this)
            itemView.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
            clickListener!!.onItemClickListener(v, adapterPosition)
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyProductViewHolder {
        return MyProductViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.layout_product_item, parent, false)
        )
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: MyProductViewHolder, position: Int) {
        val currentitem = list[position]

        Glide.with(context)
            .load(list[position].image)
            .into(holder.imageView!!)
        holder.textName!!.text = StringBuilder().append(list[position].name)
        if(!currentitem.normal_price.equals("")){
            holder.textNormal_Price!!.visibility = View.VISIBLE
            holder.textNormal_Price!!.apply {
                paintFlags = Paint.STRIKE_THRU_TEXT_FLAG
            }
            holder.textNormal_Price!!.text =StringBuilder("Rp.").append(currentitem.normal_price)
        }
        holder.price!!.text = StringBuilder("Rp.").append(list[position].price)

        holder.button!!.setOnClickListener(object : RecylerviewClickListener, View.OnClickListener {
            override fun onItemClickListener(view: View?, position: Int) {
                Log.i("Hai", "Haiii Kepanggil 1")
            }

            override fun onClick(v: View?) {
                Log.i("Hai", "Haiii Kepanggil 2")
                addToCart(currentitem)
            }

        })

        holder.setClickListener(object : RecylerviewClickListener{
            override fun onItemClickListener(view: View?, position: Int) {
                val nama = currentitem.name.toString()
                val price = currentitem.price.toString()

                val intent = Intent(context, DetailProduct::class.java)
                intent.putExtra("Extra_Name", nama)
                intent.putExtra("Extra_Price", price)
                context.startActivity(intent)
            }

        })

    }

    private fun addToCart(currentitem: ProductModel) {
        val userCart = FirebaseDatabase.getInstance()
            .getReference("Cart")
            .child("UNIQUE_USER_ID")

            userCart.child(currentitem.key!!)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if (snapshot.exists()) {
                            val cartModel = snapshot.getValue(CartModel::class.java)
                            val updateData: MutableMap<String, Any> = HashMap()
                            cartModel!!.quantity = cartModel!!.quantity + 1
                            updateData["quantity"] = cartModel!!.quantity
                            updateData["totalPrice"] =
                                cartModel!!.quantity * cartModel.price!!.toFloat()

                            userCart.child(currentitem.key!!)
                                .updateChildren(updateData)
                                .addOnSuccessListener {
                                    EventBus.getDefault().postSticky(UpdateCartEvent())
                                    cartListener.onLoadCartFailed("Susscess add to cart")
                                }
                                .addOnFailureListener { e -> cartListener.onLoadCartFailed(e.message) }
                        } else {
                            val cartModel = CartModel()
                            cartModel.key = currentitem.key
                            cartModel.name = currentitem.name
                            cartModel.image = currentitem.image
                            cartModel.price = currentitem.price
                            cartModel.quantity = 1
                            cartModel.totalPrice = currentitem.price!!.toFloat()

                            userCart.child(currentitem.key!!)
                                .setValue(cartModel)
                                .addOnSuccessListener {
                                    EventBus.getDefault().postSticky(UpdateCartEvent())
                                    cartListener.onLoadCartFailed("Susscess add to cart")
                                }
                                .addOnFailureListener { e -> cartListener.onLoadCartFailed(e.message) }
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        cartListener.onLoadCartFailed(error.message)
                    }
                })



    }

}