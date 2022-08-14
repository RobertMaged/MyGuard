package com.rmsr.myguard.data.utils

import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkCapabilities.NET_CAPABILITY_INTERNET
import android.net.NetworkCapabilities.NET_CAPABILITY_VALIDATED
import android.net.NetworkRequest
import android.os.Build
import androidx.lifecycle.LiveData
import com.rmsr.myguard.domain.utils.NetworkStatus

private fun buildVersionOrLater(requiredVersion: Int, execute: () -> Unit) {
    if (Build.VERSION.SDK_INT >= requiredVersion)
        execute()
}

class NetworkConnectionMonitorOld(private val connectivityManager: ConnectivityManager) :
    LiveData<NetworkStatus>(NetworkStatus.Offline) {
    private val TAG = "Rob_ConnectionMonitor"

    private val networkCallback by lazy { createNetworkCallback() }

    override fun onActive() {
        //
        postValue(value)
//         value = value

        registerMonitor()
    }

    override fun onInactive() = unregisterMonitor()


    private fun registerMonitor() {
        //networkCallback = createNetworkCallback()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            connectivityManager.registerDefaultNetworkCallback(networkCallback)
            return
        }

        val networkRequest = NetworkRequest.Builder().apply {
            addCapability(NET_CAPABILITY_INTERNET)
            buildVersionOrLater(Build.VERSION_CODES.M) {
                addCapability(NET_CAPABILITY_VALIDATED)
            }
        }.build()

        connectivityManager.registerNetworkCallback(networkRequest, networkCallback)
    }

    private fun unregisterMonitor() = connectivityManager.unregisterNetworkCallback(networkCallback)

    private fun createNetworkCallback() = object : ConnectivityManager.NetworkCallback() {

        override fun onCapabilitiesChanged(
            network: Network,
            networkCapabilities: NetworkCapabilities,
        ) {
            super.onCapabilitiesChanged(network, networkCapabilities)
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O)
                return

            val isInternet = networkCapabilities.hasCapability(NET_CAPABILITY_INTERNET)
            val isValidated = networkCapabilities.hasCapability(NET_CAPABILITY_VALIDATED)

            val status =
                if (isInternet && isValidated) NetworkStatus.Online else NetworkStatus.Offline
            postValue(status)
//            value = connectionState
        }

        // onAvailable() is only used if sdk less than Oreo,
        // else we depend on onCapabilitiesChanged()
        override fun onAvailable(network: Network) {
            super.onAvailable(network)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                return


            val networkCapabilities = connectivityManager.getNetworkCapabilities(network)

            val isInternet =
                networkCapabilities?.hasCapability(NET_CAPABILITY_INTERNET) ?: false

            val isValidated = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                networkCapabilities?.hasCapability(NET_CAPABILITY_VALIDATED)
            } else {
                with(connectivityManager.activeNetworkInfo) {
                    this?.isAvailable == true && isConnected
                }
            } ?: false

            val status =
                if (isInternet && isValidated) NetworkStatus.Online else NetworkStatus.Offline
            postValue(status)
//            value = connectionState
        }

        override fun onUnavailable() {
            super.onUnavailable()
            postValue(NetworkStatus.Offline)
        }

        override fun onLost(network: Network) {
            super.onLost(network)
            postValue(NetworkStatus.Offline)
//            value = NetworkStatus.Offline
        }

    }

}