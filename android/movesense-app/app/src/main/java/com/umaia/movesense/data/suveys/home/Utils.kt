package com.umaia.movesense.data.suveys.home

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.core.content.ContextCompat.startActivity
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import java.text.SimpleDateFormat
import java.util.*

fun getLikertScale(input: String, index: Int): String {
    val pattern = Regex("(\\d)=\\s*(\\w+\\s\\w+)")
    val matches = pattern.findAll(input)
    val match = matches.elementAtOrNull(index)
    return match?.groups?.get(2)?.value!!
}


fun checkIntBoolean (boolean : Int): Boolean {
    return boolean === 1
}


fun convertDate(date: String): Date {
    val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
    val date = dateFormat.parse(date)
    return date
}
fun <T> LiveData<T>.observeOnce(lifecycleOwner: LifecycleOwner, observer: Observer<T>) {
    observe(lifecycleOwner, object : Observer<T> {
        override fun onChanged(t: T?) {
            observer.onChanged(t)
            removeObserver(this)
        }
    })
}

fun <T> LiveData<T>.observeOnceAtService(lifecycleService: LifecycleService, observer: Observer<T>) {
    observe(lifecycleService, object : Observer<T> {
        override fun onChanged(t: T?) {
            observer.onChanged(t)
            removeObserver(this)
        }
    })
}

fun <A : Activity> Activity.startNewActivityFromSplash(activity: Class<A>) {

    Handler(Looper.getMainLooper()).postDelayed({
        val intent = Intent(this, activity).also {
            it.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(it)

        }

    }, 1500)
}

fun <A : Activity> Activity.startNewActivity(activity: Class<A>) {

    val intent = Intent(this, activity).also {
        it.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(it)

    }

}

fun View.visible(isVisible: Boolean) {
    visibility = if (isVisible) View.VISIBLE else View.GONE
}

fun View.enable(enabled: Boolean) {
    isEnabled = enabled
    alpha = if (enabled) 1f else 0.5f
}

