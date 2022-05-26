package com.example.viewer_2022

import android.util.Log
import com.example.viewer_2022.constants.Constants
import com.example.viewer_2022.data.GetDataFromWebsite
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import java.util.*
import kotlin.time.Duration

// Manages updates to the data and triggering refreshes in the UI
class RefreshManager {
    private val listeners = mutableMapOf<String, () -> Unit>()


    fun start(scope: CoroutineScope) {
        if (!Constants.USE_TEST_DATA) {
            tickerFlow(
                Duration.seconds(Constants.REFRESH_INTERVAL),
                Duration.seconds(Constants.REFRESH_INTERVAL)
            ).onEach {
                Log.d("data-refresh", "tick")
                try {
                    MainViewerActivity.updateNotesCache()
                } catch (e: Throwable) {
                    Log.e("data-refresh", "Error fetching notes data $it")
                }
                GetDataFromWebsite({
                    Log.i("data-refresh", "Fetched data from website successfully")

                    refresh()
                }, {
                    Log.e("data-refresh", "Error fetching data $it")
                }).execute()
            }.launchIn(scope)
        }

    }

    fun addRefreshListener(listener: () -> Unit): String {
        val id = UUID.randomUUID().toString()
        listeners[id] = listener
        Log.d("data-refresh", "Added id: $id")
        return id
    }

    fun removeRefreshListener(id: String? = null) {
        if (listeners.containsKey(id)) listeners.remove(id)
        Log.d("data-refresh", "Destroyed id: $id")
    }

    fun refresh() {
        listeners.forEach {
            it.value.invoke()
            Log.d("data-refresh", "refreshed: ${it.key}")
        }
    }

    fun removeAllListeners() {
        listeners.clear()
    }

    fun refresh(id: String) {
        listeners[id]?.let { it() }
    }
}


fun tickerFlow(period: Duration, initialDelay: Duration = Duration.ZERO) = flow {
    delay(initialDelay)
    while (true) {
        emit(Unit)
        delay(period)
    }
}