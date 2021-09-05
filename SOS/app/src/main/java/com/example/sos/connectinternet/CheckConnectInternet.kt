package com.example.sos.connectinternet

import android.content.Context
import android.net.ConnectivityManager
import androidx.lifecycle.LiveData
import com.example.sos.MainActivity

class CheckConnectInternet(private val context : MainActivity) : LiveData<Boolean>() {

    private val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE)
    private lateinit var  networkCallback : ConnectivityManager.NetworkCallback

//    fun connectivityManagerCallback() : ConnectivityManager.NetworkCallback{
//
//        return
//    }
}