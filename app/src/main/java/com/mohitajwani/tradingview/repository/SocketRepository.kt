package com.mohitajwani.tradingview.repository

import com.mohitajwani.tradingview.socket.SocketUpdate
import com.mohitajwani.tradingview.socket.WebServicesProvider
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel

@DelicateCoroutinesApi
class SocketRepository constructor(private val webServicesProvider: WebServicesProvider) {

    @ExperimentalCoroutinesApi
    fun startSocket(): Channel<SocketUpdate> =
        webServicesProvider.startSocket()

    @ExperimentalCoroutinesApi
    fun closeSocket() {
        webServicesProvider.stopSocket()
    }
}