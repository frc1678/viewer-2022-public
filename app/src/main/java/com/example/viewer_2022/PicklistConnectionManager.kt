package com.example.viewer_2022

import android.util.Log
import com.google.gson.JsonParser
import io.ktor.client.*
import io.ktor.client.features.*
import io.ktor.client.features.websocket.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.http.cio.websocket.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlin.io.readText
import kotlin.properties.Delegates

val client = HttpClient {
    install(WebSockets)
    defaultRequest {
        header("Authorization", "")
    }
}

object PicklistConnectionManager {
    private var webSocketSession: DefaultClientWebSocketSession? = null

    private var messageCallback: MutableList<((data: String) -> Unit)> = mutableListOf()
    private var connectionCallback: MutableList<((status: Boolean) -> Unit)> = mutableListOf()

    fun addMessageListener(cb: ((data: String) -> Unit)) {
        messageCallback.add(cb)
    }

    fun addConnectionListener(cb: ((status: Boolean) -> Unit)) {
        connectionCallback.add(cb)
    }

    var connected: Boolean by Delegates.observable(false) { _, _, new ->
        connectionCallback.forEach { it.invoke(new) }
    }

    suspend fun connect() {
        if (webSocketSession == null || !connected) {
            Log.d("picklist", "connecting to websocket...")
            try {
                client.wss(
                    host = "grosbeak.citruscircuits.org",
                    path = "/ws/picklist"
                ) {
                    webSocketSession = this
                    connected = true
                    while (true) {
                        val message = incoming.receive() as? Frame.Text ?: continue
                        val messageText = message.readText()
                        messageCallback.forEach { it.invoke(messageText) }

                    }
                }
            } catch (e: Throwable) {
                Log.e("picklist", "Error with picklist socket: $e")
                connected = false
            }

        } else {
            //TODO: DO THIS PLEASE
//            return webSocketSession!!.incoming.isClosedForReceive
        }
    }

    suspend fun send(data: String) {
        webSocketSession?.send(data)
    }
}