package com.mohitajwani.tradingview

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.mohitajwani.tradingview.databinding.FragmentFirstBinding
import com.tradingview.lightweightcharts.api.options.models.CandlestickSeriesOptions
import com.tradingview.lightweightcharts.api.options.models.crosshairOptions
import com.tradingview.lightweightcharts.api.options.models.localizationOptions
import com.tradingview.lightweightcharts.api.series.enums.CrosshairMode
import com.tradingview.lightweightcharts.runtime.plugins.PriceFormatter
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collect
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.net.Socket
import java.util.concurrent.Semaphore

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
@DelicateCoroutinesApi
@ExperimentalCoroutinesApi
class FirstFragment : Fragment() {

    private var _binding: FragmentFirstBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private var realtimeDataJob: Job? = null

    private lateinit var viewModel: FirstFragmentViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentFirstBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(this)[FirstFragmentViewModel::class.java]
        viewModel.seriesData.observe(viewLifecycleOwner) { data ->
            binding.chartsView.api.apply {

                addCandlestickSeries(
                    options = CandlestickSeriesOptions(),
                    onSeriesCreated = { series ->
                        series.setData(data.list)
                        realtimeDataJob = lifecycleScope.launchWhenResumed {
                            viewModel.seriesFlow.collect(series::update)
                        }
                    }
                )

            }
        }

        binding.chartsView.api.applyOptions {
            crosshair = crosshairOptions {
                mode = CrosshairMode.NORMAL
            }
            localization = localizationOptions {
                // Rupees formatting
                priceFormatter = PriceFormatter(template = "\u20B9{price:#2:#2}")
            }
        }

        viewModel.subscribeToSocketEvents()

    }

    private fun createSocketConnection() {
        Log.d("Socket----> ", "Create Socket connection to Heroku")
        val client = Socket("https://ws-api-tickering.herokuapp.com", 55326)
        val clientOut = client.getOutputStream()
        val clientIn = client.getInputStream()

        val lock = Semaphore(0)
        val serverOut: OutputStream? = null
        val serverIn: InputStream? = null

        println("Waiting for lock")
        lock.acquire()
        println("Acquired lock")

        Log.d("Socket----> ", clientIn.buffered().toString())
    }

    @Throws(IOException::class)
    private fun write(out: OutputStream, str: String) {
        out.write(str.toByteArray())
        out.flush()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}