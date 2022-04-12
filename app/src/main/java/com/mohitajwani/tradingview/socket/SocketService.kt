package com.mohitajwani.tradingview.socket

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.IOException
import java.net.Socket

class SocketService {
    companion object {
        suspend fun print() {
            withContext(Dispatchers.IO) {
                try {
                    val socket = Socket("https://ws-api-tickering.herokuapp.com", 55326)
                    val outputStream = socket.getOutputStream()

                    outputStream.use {
                        it.write("xxx".toByteArray())
                        Log.d("Socket Comm ---> ", socket.getInputStream().buffered().toString())
                        Log.d("Socket Comm ---> ", socket.getOutputStream().buffered().toString())
                    }
                } catch (e: IOException) {
                    //log your error
                    Log.d("Socket Comm ---> ", e.printStackTrace().toString())
                }
            }
        }
    }
}