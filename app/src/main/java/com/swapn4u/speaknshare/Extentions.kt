package com.swapn4u.speaknshare

import android.app.Activity
import android.content.Context
import android.net.ConnectivityManager


fun Activity.isActiveInternet() : Boolean {
    val connectivityManager= this.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val networkInfo=connectivityManager.activeNetworkInfo
    return  networkInfo!=null && networkInfo.isConnected
}