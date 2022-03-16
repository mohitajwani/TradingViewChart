package com.mohitajwani.tradingview

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.mohitajwani.tradingview.databinding.FragmentFirstBinding
import com.tradingview.lightweightcharts.api.options.models.CandlestickSeriesOptions
import com.tradingview.lightweightcharts.api.options.models.crosshairOptions
import com.tradingview.lightweightcharts.api.options.models.localizationOptions
import com.tradingview.lightweightcharts.api.series.enums.CrosshairMode
import com.tradingview.lightweightcharts.runtime.plugins.PriceFormatter
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collect

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class FirstFragment : Fragment() {

    private var _binding: FragmentFirstBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private var realtimeDataJob: Job? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentFirstBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.buttonFirst.setOnClickListener {
            findNavController().navigate(R.id.action_FirstFragment_to_SecondFragment)
        }

        val viewModel = ViewModelProvider(this)[FirstFragmentViewModel::class.java]
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

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}