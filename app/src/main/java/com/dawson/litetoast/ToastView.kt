package com.dawson.litetoast

import android.app.Activity
import android.content.Context
import android.support.annotation.AttrRes
import android.support.v4.content.ContextCompat
import android.text.TextUtils
import android.util.AttributeSet
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.LinearLayout
import kotlinx.android.synthetic.main.layout_toast.view.*

/**
 * create by Dawson.Gao
 * on 2018/8/14
 */
internal class ToastView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

    private var slideInAnimation: Animation? = null
    private var slideOutAnimation: Animation? = null
    private var onToastStatusListener: OnToastStatusListener? = null
    private var activity: Activity? = null
    private var duration: Long = 6000
    private var actionDownY: Float = 0F
    var layoutGravity = Gravity.BOTTOM

    init {
        initViews(context)
    }

    private fun initViews(context: Context) {
        View.inflate(getContext(), R.layout.layout_toast, this)
        activity = context as Activity
        toastRootView?.setOnTouchListener { _, event ->
            when (event?.action) {
                MotionEvent.ACTION_DOWN -> {
                    actionDownY = event.y
                }
                MotionEvent.ACTION_UP -> {
                    if (event.y < actionDownY) {
                        dismiss()
                    }
                }
                MotionEvent.ACTION_CANCEL -> {
                    if (event.y < actionDownY) {
                        dismiss()
                    }
                }
                else -> {
                }
            }
            super.onTouchEvent(event)
        }
        toastRootView?.setOnClickListener {
            dismiss()
        }
    }

    fun setParams(params: Params?) {
        if (params != null) {
            onToastStatusListener = params.onToastStatusListener
            duration = params.duration
            layoutGravity = params.layoutGravity

            //Message
            if (!TextUtils.isEmpty(params.message)) {
                tvMessage?.visibility = View.VISIBLE
                tvMessage?.text = params.message
                if (params.messageColor != 0) {
                    tvMessage?.setTextColor(ContextCompat.getColor(context, params.messageColor))
                }
            }

/*            if (params.actionIcon != 0 && params.onToastStatusListener != null) {
                btnAction?.visibility = View.GONE
                btnActionWithIcon?.visibility = View.VISIBLE
                btnActionWithIcon?.setBackgroundResource(params.actionIcon)
                btnActionWithIcon?.setOnClickListener {
                    params.onToastStatusListener?.onClick()
                    dismiss()
                }
            }*/

            //Background
            if (params.backgroundColor != 0) {
                toastRootView?.setBackgroundColor(ContextCompat.getColor(context, params.backgroundColor))
            }

            createInAnim()
            createOutAnim()
        }
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        if (layoutGravity == Gravity.TOP) {
            super.onLayout(changed, l, 0, r, toastRootView?.measuredHeight ?: 0)
        } else {
            super.onLayout(changed, l, t, r, b)
        }
    }

    private fun createInAnim() {
        slideInAnimation = AnimationUtils.loadAnimation(
            context,
            if (layoutGravity == Gravity.BOTTOM) R.anim.slide_in_from_bottom else R.anim.slide_in_from_top
        )
        slideInAnimation?.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation) {
                onToastStatusListener?.onShow()
            }

            override fun onAnimationEnd(animation: Animation) {
                postDelayed({ dismiss() }, duration)
            }

            override fun onAnimationRepeat(animation: Animation) {

            }
        })

        animation = slideInAnimation
    }

    private fun createOutAnim() {
        slideOutAnimation = AnimationUtils.loadAnimation(
            context,
            if (layoutGravity == Gravity.BOTTOM) R.anim.slide_out_to_bottom else R.anim.slide_out_to_top
        )
    }

    fun dismiss() {
        slideOutAnimation?.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation) {}

            override fun onAnimationEnd(animation: Animation) {
                onToastStatusListener?.onDismiss()
                destroyWithDelay()
            }

            override fun onAnimationRepeat(animation: Animation) {}
        })
        startAnimation(slideOutAnimation)
    }

    fun destroy() {
        val parent = parent
        if (parent != null) {
            this@ToastView.clearAnimation()
            (parent as ViewGroup).removeView(this@ToastView)
        }
    }

    private fun destroyWithDelay() {
        postDelayed({
            destroy()
        }, 200)
    }

    fun getColor(context: Context, @AttrRes attr: Int, defaultColor: Int = 0): Int {
        val a = context.theme.obtainStyledAttributes(intArrayOf(attr))
        try {
            return a.getColor(0, defaultColor)
        } finally {
            a.recycle()
        }
    }
}