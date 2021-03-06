package com.example.kasirpintartest.data

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.kasirpintartest.data.entity.Order
import com.example.kasirpintartest.data.entity.Product
import com.example.kasirpintartest.data.remote.RemoteDataSource
import com.example.kasirpintartest.vo.Resource
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.lang.Exception

class FakeRepository(private val local: LocalDataSource, private val remote: RemoteDataSource) :
    Repository {
    override suspend fun getProducts(): LiveData<List<Product>> {
        val result = MutableLiveData<List<Product>>()
        val local = local.getProducts()
        result.value = local
        return result
    }

    override suspend fun deleteProduct(product: Product): LiveData<Int> {
        val result = MutableLiveData<Int>()
        val localResult = local.deleteProduct(product)
        result.value = localResult
        return result
    }

    override suspend fun updateStock(product: Product, qty: Int): LiveData<Int> {
        val result = MutableLiveData<Int>()
        val productID = local.productByID(product.id.toString())
        val localResult = local.updateStock(productID, qty)
        result.value = localResult
        return result
    }

    override suspend fun updateProduct(product: Product): LiveData<Int> {
        val result = MutableLiveData<Int>()
        val localResult = local.updateProduct(product)
        result.value = localResult
        return result
    }

    override suspend fun insertProduct(product: Product): LiveData<Long> {
        val result = MutableLiveData<Long>()
        val localResult = local.insertProduct(product)
        result.value = localResult
        return result
    }

    override suspend fun getOrders(callback: (Resource<List<Order>>) -> Unit) {
        val orders: MutableList<Order> = mutableListOf()
        val dbRef = FirebaseDatabase.getInstance().getReference("Orders")
        dbRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    for (order in snapshot.children) {
                        val productItem = order.getValue(Order::class.java)
                        if (productItem != null) {
                            orders.add(productItem)
                        }
                    }
                    callback(Resource.success(orders))
                } else {
                    callback(Resource.error("not found", orders))
                }
            }

            override fun onCancelled(error: DatabaseError) {
                callback(Resource.error(error.message, orders))
            }
        })
    }

    override suspend fun removeOrder(id: String) {
        val dbRef = FirebaseDatabase.getInstance().getReference("Orders")
        try {
            dbRef.child(id).removeValue()
        } catch (e: Exception) {
            Log.d("MY_ERROR", e.message.toString())
        }
    }
}