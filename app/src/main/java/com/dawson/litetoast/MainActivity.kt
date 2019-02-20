package com.dawson.litetoast

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    fun showToast(view: View) {
        val builder = ToastBar.Builder(this)
        builder.setDuration(500)
            .setMessage("Hello, maybe you will be the one")
            .setOnAnimationListener(object : OnToastStatusListener {
                override fun onShow() {
                    Log.d("Toast", "start show toast")
                }

                override fun onDismiss() {
                    Log.d("Toast", "finish show toast")
                }

            })
            .show()
    }
}
