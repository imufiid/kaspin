package com.example.kasirpintartest.ui.order

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.example.kasirpintartest.MyApplication
import com.example.kasirpintartest.R
import com.example.kasirpintartest.databinding.ActivityOrderBinding
import com.example.kasirpintartest.ui.checkout.CheckoutActivity
import com.example.kasirpintartest.viewmodel.ViewModelFactory
import com.google.firebase.database.*
import javax.inject.Inject

class OrderActivity : AppCompatActivity() {
    private lateinit var _bind: ActivityOrderBinding
    private lateinit var orderAdapter: OrderAdapter

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    private val viewModel by lazy {
        ViewModelProvider(this, viewModelFactory)[OrderViewModel::class.java]
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        (application as MyApplication).appComponent.inject(this)
        super.onCreate(savedInstanceState)
        _bind = DataBindingUtil.setContentView(this, R.layout.activity_order)
        supportActionBar?.title = "Order"
        _bind.viewmodel = viewModel
        _bind.lifecycleOwner = this

        initRV()
        getOrder()
    }

    private fun getOrder() {
        viewModel.getOrder()
        viewModel.order.observe(this, {
            orderAdapter.addData(it)
        })
    }

    private fun initRV() {
        orderAdapter = OrderAdapter { order, status, position ->
            if (status == OrderAdapter.LOAD) {
                startActivity(Intent(this, CheckoutActivity::class.java).apply {
                    putExtra(CheckoutActivity.ORDER_ID, order.id)
                    putExtra(CheckoutActivity.IS_LOCAL, false)
                })
                finish()
            } else if (status == OrderAdapter.DELETE) {
                val dialogMessage = getString(R.string.message_delete_product)
                val dialogTitle = getString(R.string.title_cancle_transaction)

                val alertDialogBuilder = AlertDialog.Builder(this)
                alertDialogBuilder.setTitle(dialogTitle)
                alertDialogBuilder
                    .setMessage(dialogMessage)
                    .setCancelable(false)
                    .setNegativeButton(getString(R.string.no)) { dialog, _ -> dialog.cancel() }
                    .setPositiveButton(getString(R.string.yes)) { _, _ ->
                        viewModel.removeOrder(order.id.toString())
                        orderAdapter.removeItem(position)
                    }
                val alertDialog = alertDialogBuilder.create()
                alertDialog.show()

            }

        }

        _bind.rvOrder.apply {
            setHasFixedSize(true)
            adapter = orderAdapter
        }
    }
}