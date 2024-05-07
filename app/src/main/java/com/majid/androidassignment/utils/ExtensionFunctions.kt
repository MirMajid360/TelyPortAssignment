package com.majid.androidassignment.utils

import android.text.SpannableString
import android.text.Spanned
import android.text.style.UnderlineSpan
import android.view.View
import android.widget.TextView
import androidx.core.view.isVisible




fun String.capitalizeFirstLetter(): String {
    return if (isNotEmpty()) {
        this[0].uppercase() + substring(1)
    } else {
        this
    }
}
fun View.showVisibility() {
    try {


        if (!this.isVisible) {
            this.visibility = View.VISIBLE
        }


    } catch (e: Exception) {
        e.printStackTrace()
    }
}

fun View.invisible() {

    try {


        if (this.isVisible) {
            this.visibility = View.INVISIBLE
        }


    } catch (e: Exception) {
        e.printStackTrace()
    }
}


fun View.hideVisibility() {

    try {


        if (this.isVisible) {
            this.visibility = View.GONE
        }


    } catch (e: Exception) {
        e.printStackTrace()
    }

}

fun View.toggleVisibility() {

    try {


        if (this.isVisible) {
            this.visibility = View.GONE
        } else {
            this.visibility = View.VISIBLE
        }


    } catch (e: Exception) {
        e.printStackTrace()
    }
}

fun TextView.showUnderlined() {
    // Create a SpannableString with the text you want to underline
    val spannableString = SpannableString(text)

    // Apply UnderlineSpan to the SpannableString
    spannableString.setSpan(UnderlineSpan(), 0, text.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

    // Set the SpannableString to the TextView
    setText(spannableString, TextView.BufferType.SPANNABLE)
}