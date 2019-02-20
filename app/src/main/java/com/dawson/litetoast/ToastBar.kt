package com.dawson.litetoast

import android.app.Activity
import android.support.annotation.StringRes
import android.view.Gravity
import android.view.View
import android.view.ViewGroup

/**
 * create by Dawson.Gao
 * on 2018/8/14
 */
class ToastBar private constructor(private var context: Activity?, params: Params) {

    private var toastView: ToastView? = null

    init {
        if (context != null) {
            toastView = ToastView(context!!)
            toastView?.setParams(params)
        }
    }

    fun show() {
        if (toastView != null) {
            val decorView = context?.window?.decorView as ViewGroup?
            val content = decorView?.findViewById<View>(android.R.id.content) as ViewGroup?
            if (toastView?.parent == null) {
                if (toastView?.layoutGravity == Gravity.BOTTOM) {
                    content?.addView(toastView)
                } else {
                    decorView?.addView(toastView)
                }
            }
        }
    }

    fun destroy() {
        toastView?.destroy()
    }

    class Builder(var context: Activity?) {

        private val params = Params()

        fun setMessage(message: String): ToastBar.Builder {
            params.message = message
            return this
        }

        fun setMessage(@StringRes resId: Int): ToastBar.Builder {
            params.message = context?.getString(resId)
            return this
        }

        fun setDuration(duration: Long): ToastBar.Builder {
            params.duration = duration
            return this
        }

        fun setOnAnimationListener(onAnimationListener: OnToastStatusListener?): ToastBar.Builder {
            params.onToastStatusListener = onAnimationListener
            return this
        }

        fun create(): ToastBar {
            return ToastBar(context, params)
        }

        fun show(): ToastBar {
            val toastBar = create()
            toastBar.show()
            return toastBar
        }
    }
}

class Params {
    var message: String? = null
    var onToastStatusListener: OnToastStatusListener? = null
    var backgroundColor: Int = 0
    var messageColor: Int = 0
    var duration: Long = 2000
    var layoutGravity = Gravity.TOP
}

interface OnToastStatusListener {
    fun onShow()
    fun onDismiss()
}