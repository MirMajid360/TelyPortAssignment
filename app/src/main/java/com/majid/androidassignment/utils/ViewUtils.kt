package com.majid.androidassignment.utils

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.res.Resources
import android.graphics.PixelFormat
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.view.Gravity
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import com.google.android.material.snackbar.Snackbar
import com.majid.androidassignment.R
import com.majid.androidassignment.databinding.LayoutCustomSnackbarBinding


object ViewUtils {




        fun showSnackBar(context: Activity, view: View, text: String) {

            try {


                Snackbar.make(view, "This is a Snackbar", Snackbar.LENGTH_LONG)
                    .setBackgroundTint(ContextCompat.getColor(context, R.color.grey))
//                .setAction("Retry") { doSomething() }
                    .show()


            } catch (e: Exception) {
                e.printStackTrace()
            }
        }


        var snackbar = ArrayList<View>()
        fun customSnackBar(activity: Activity, message: String?, isError: Boolean) {
            val sb = LayoutCustomSnackbarBinding.inflate(activity.layoutInflater, null, false)

            sb.textView.text = message
            sb.imageView.setImageDrawable(
                ContextCompat.getDrawable(
                    activity, if (isError) R.drawable.ic_error else R.drawable.ic_green_check
                )
            )

            val lp = WindowManager.LayoutParams().apply {
                height = WindowManager.LayoutParams.WRAP_CONTENT
                width = WindowManager.LayoutParams.MATCH_PARENT
                format = PixelFormat.TRANSLUCENT
                gravity = Gravity.TOP
                flags = (WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH
                        or WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                        or WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL)
            }

            activity.windowManager.addView(sb.root, lp)
            sb.root.translationY = -sb.root.measuredHeight.toFloat()
            sb.root.visibility = View.VISIBLE
            sb.root.animate().setDuration(1000).translationX(0.0f).translationY(10f)

            sb.closeIV.setOnClickListener {
                sb.root.animate()
                    .setDuration(100)
                    .translationX(0.0f)
                    .translationY(-sb.root.measuredHeight.toFloat())
                    .withEndAction {
                        if (!activity.isDestroyed) {
                            sb.root.visibility = View.GONE
                            if (snackbar.contains(sb.root)) {
                                activity.windowManager.removeViewImmediate(sb.root)
                                snackbar.remove(sb.root)
                            }
                        }
                    }
            }

            val handler = Looper.myLooper()?.let { Handler(it) }
            handler?.postDelayed({
                sb.root.animate().setDuration(500).translationX(0.0f)
                    .translationY(-sb.root.measuredHeight.toFloat())
                    .withEndAction {
                        if (!activity.isDestroyed) {
                            sb.root.visibility = View.GONE
                            if (snackbar.contains(sb.root)) {
                                activity.windowManager.removeViewImmediate(sb.root)
                                snackbar.remove(sb.root)
                            }
                        }
                    }
            }, 2000)
        }




}