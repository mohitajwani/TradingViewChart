package com.mohitajwani.tradingview

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mohitajwani.tradingview.model.Data
import com.mohitajwani.tradingview.repository.DynamicRepository
import com.mohitajwani.tradingview.repository.SocketRepository
import com.mohitajwani.tradingview.repository.StaticRepository
import com.mohitajwani.tradingview.socket.WebServicesProvider
import com.tradingview.lightweightcharts.api.series.common.SeriesData
import com.tradingview.lightweightcharts.api.series.enums.SeriesType
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

@ExperimentalCoroutinesApi
@DelicateCoroutinesApi
class FirstFragmentViewModel: ViewModel() {

    private val staticRepository = StaticRepository()
    private val dynamicRepository = DynamicRepository()
    private val socketRepository = SocketRepository(webServicesProvider = WebServicesProvider())

    val seriesFlow: Flow<SeriesData>
        get() = dynamicRepository.getListSeriesData(data.value!!) {
            loadData()
        }

    val seriesData: LiveData<Data>
        get() = data

    private val data: MutableLiveData<Data> by lazy {
        MutableLiveData<Data>().also {
            loadData()
        }
    }

    private fun loadData() {
        viewModelScope.launch {
            val barData = staticRepository.getRealTimeEmulationSeriesData()
            data.postValue(Data(barData, SeriesType.CANDLESTICK))
        }
    }

    fun subscribeToSocketEvents() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                socketRepository.startSocket().consumeEach {
                    if (it.exception == null) {
                        println("Collecting : ${it.text}")
                    } else {
                        onSocketError(it.exception)
                    }
                }
            } catch (ex: java.lang.Exception) {
                onSocketError(ex)
            }
        }
    }

    private fun onSocketError(ex: Throwable) {
        println("Error occurred : ${ex.message}")
    }

    override fun onCleared() {
        socketRepository.closeSocket()
        super.onCleared()
    }
}