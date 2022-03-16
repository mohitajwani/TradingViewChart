package com.mohitajwani.tradingview.model

import com.tradingview.lightweightcharts.api.series.common.SeriesData
import com.tradingview.lightweightcharts.api.series.enums.SeriesType

data class Data(
    val list: List<SeriesData>,
    val type: SeriesType
)