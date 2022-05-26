package com.example.viewer_2022.fragments.team_details

import android.graphics.Canvas
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.viewer_2022.*
import com.example.viewer_2022.constants.Constants
import com.example.viewer_2022.constants.Translations
import com.example.viewer_2022.fragments.match_details.MatchDetailsFragment
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.listener.OnChartValueSelectedListener
import kotlinx.android.synthetic.main.fragment_graphs.view.*
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.renderer.XAxisRenderer
import com.github.mikephil.charting.utils.MPPointF
import com.github.mikephil.charting.utils.Utils

/**
 * Page that the graphs are displayed on
 */
class GraphsFragment() : Fragment() {
    private var teamNumber: String? = null
    private var datapoint: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_graphs, container, false)

        arguments?.let {
            teamNumber = it.getString(Constants.TEAM_NUMBER, Constants.NULL_CHARACTER)
            datapoint = it.getString("datapoint", Constants.NULL_CHARACTER)
        }

        root.tv_team_number.text = teamNumber
        root.tv_datapoint.text =
            "${Translations.ACTUAL_TO_HUMAN_READABLE[datapoint]} by ${Translations.TIM_TO_HUMAN_READABLE[datapoint!!]}"

        val timDatapoint = Translations.TIM_FROM_TEAM[datapoint!!]

        //get data
        val timDataMap: Map<String, String> = if (timDatapoint == "auto_line") {
            getTIMDataValue(
                teamNumber!!, timDatapoint,
                Constants.PROCESSED_OBJECT.CALCULATED_TBA_TEAM_IN_MATCH.value
            )
        } else if (timDatapoint == "played_defense") {
            getTIMDataValue(
                teamNumber!!, timDatapoint,
                Constants.PROCESSED_OBJECT.CALCULATED_SUBJECTIVE_TEAM_IN_MATCH.value
            )
        } else {
            getTIMDataValue(
                teamNumber!!, timDatapoint!!,
                Constants.PROCESSED_OBJECT.CALCULATED_OBJECTIVE_TEAM_IN_MATCH.value
            )
        }

        var timDataListClimbLevel: List<String>? = null
        if (Constants.GRAPHABLE_CLIMB_TIMES.contains(datapoint!!)) {
            timDataListClimbLevel = getTIMDataValue(
                teamNumber!!, "climb_level",
                Constants.PROCESSED_OBJECT.CALCULATED_OBJECTIVE_TEAM_IN_MATCH.value
            ).values.toList()
        }

        //add data to a list of BarEntries so it can be added to the chart
        val entries: ArrayList<BarEntry> = ArrayList()
        for ((index, timData) in timDataMap.values.withIndex()) {
            if ((datapoint == "matches_incap")) {
                if ((timData != "0") and (timData != Constants.NULL_CHARACTER)) {
                    entries.add(BarEntry(index.toFloat(), 1F))
                } else {
                    entries.add(BarEntry(index.toFloat(), 0F))
                }
            } else if (datapoint == "low_rung_successes") {
                if (timData.lowercase() == "low") {
                    entries.add(BarEntry(index.toFloat(), 1F))
                } else {
                    entries.add(BarEntry(index.toFloat(), 0F))
                }
            } else if (datapoint == "mid_rung_successes") {
                if (timData.lowercase() == "mid") {
                    entries.add(BarEntry(index.toFloat(), 1F))
                } else {
                    entries.add(BarEntry(index.toFloat(), 0F))
                }
            } else if (datapoint == "high_rung_successes") {
                if (timData.lowercase() == "high") {
                    entries.add(BarEntry(index.toFloat(), 1F))
                } else {
                    entries.add(BarEntry(index.toFloat(), 0F))
                }
            } else if (datapoint == "traversal_rung_successes") {
                if (timData.lowercase() == "traversal") {
                    entries.add(BarEntry(index.toFloat(), 1F))
                } else {
                    entries.add(BarEntry(index.toFloat(), 0F))
                }
            } else if (datapoint == "mode_climb_level") {
                when (timDataListClimbLevel!![index].lowercase()) {
                    "low" -> entries.add(BarEntry(index.toFloat(), 1F))
                    "mid" -> entries.add(BarEntry(index.toFloat(), 2F))
                    "high" -> entries.add(BarEntry(index.toFloat(), 3F))
                    "traversal" -> entries.add(BarEntry(index.toFloat(), 4F))
                    else -> entries.add(BarEntry(index.toFloat(), 0F))
                }
                root.bar_chart.axisLeft.axisMaximum = 4F
            } else if (datapoint == "mode_start_position") {
                when (timData.lowercase()) {
                    "zero" -> entries.add(BarEntry(index.toFloat(), 0F))
                    "one" -> entries.add(BarEntry(index.toFloat(), 1F))
                    "two" -> entries.add(BarEntry(index.toFloat(), 2F))
                    "three" -> entries.add(BarEntry(index.toFloat(), 3F))
                    "four" -> entries.add(BarEntry(index.toFloat(), 4F))
                    else -> entries.add(BarEntry(index.toFloat(), 0F))
                }
                root.bar_chart.axisLeft.axisMaximum = 4F
            } else if (datapoint == "position_zero_starts") {
                if (timData.lowercase() == "zero") {
                    entries.add(BarEntry(index.toFloat(), 1F))
                } else {
                    entries.add(BarEntry(index.toFloat(), 0F))
                }
            } else if (datapoint == "position_one_starts") {
                if (timData.lowercase() == "one") {
                    entries.add(BarEntry(index.toFloat(), 1F))
                } else {
                    entries.add(BarEntry(index.toFloat(), 0F))
                }
            } else if (datapoint == "position_two_starts") {
                if (timData.lowercase() == "two") {
                    entries.add(BarEntry(index.toFloat(), 1F))
                } else {
                    entries.add(BarEntry(index.toFloat(), 0F))
                }
            } else if (datapoint == "position_three_starts") {
                if (timData.lowercase() == "three") {
                    entries.add(BarEntry(index.toFloat(), 1F))
                } else {
                    entries.add(BarEntry(index.toFloat(), 0F))
                }
            } else if (datapoint == "position_four_starts") {
                if (timData.lowercase() == "four") {
                    entries.add(BarEntry(index.toFloat(), 1F))
                } else {
                    entries.add(BarEntry(index.toFloat(), 0F))
                }
            } else if (datapoint == "climb_percent_success") {
                if ((timData.lowercase() != "none") and (timData != Constants.NULL_CHARACTER)) {
                    entries.add(BarEntry(index.toFloat(), 1F))
                } else {
                    entries.add(BarEntry(index.toFloat(), 0F))
                }
            } else if (datapoint == "avg_climb_points") {
                when (timDataListClimbLevel!![index].lowercase()) {
                    "low" -> entries.add(BarEntry(index.toFloat(), 1F))
                    "mid" -> entries.add(BarEntry(index.toFloat(), 2F))
                    "high" -> entries.add(BarEntry(index.toFloat(), 3F))
                    "traversal" -> entries.add(BarEntry(index.toFloat(), 4F))
                    else -> entries.add(BarEntry(index.toFloat(), 0F))
                }
                root.bar_chart.axisLeft.axisMaximum = 4F
            } else if (datapoint == "climb_all_attempts") {
                when (timDataListClimbLevel!![index].lowercase()) {
                    "low" -> entries.add(BarEntry(index.toFloat(), 1F))
                    "mid" -> entries.add(BarEntry(index.toFloat(), 2F))
                    "high" -> entries.add(BarEntry(index.toFloat(), 3F))
                    "traversal" -> entries.add(BarEntry(index.toFloat(), 4F))
                    else -> entries.add(BarEntry(index.toFloat(), 0F))
                }
                root.bar_chart.axisLeft.axisMaximum = 4F
            } else if (Constants.GRAPHABLE_BOOL.contains(datapoint!!)) {
                if (timData == "true") {
                    entries.add(BarEntry(index.toFloat(), 1F))
                } else {
                    entries.add(BarEntry(index.toFloat(), 0F))
                }
            } else if (timData != Constants.NULL_CHARACTER) {
                entries.add(BarEntry(index.toFloat(), timData.toFloat()))
            } else {
                entries.add(BarEntry(index.toFloat(), 0F))
            }
        }

        //make the list of entries into a BarDataSet so it can be added to the chart
        val barDataSet = BarDataSet(entries, "")

        //chart the data
        val data = BarData(barDataSet)
        root.bar_chart.data = data

        //set color of bars
        barDataSet.setColors(
            ContextCompat.getColor(
                context!!,
                R.color.colorPrimaryLight
            )
        )

        //set text size of the numbers labelling the height of each bar
        barDataSet.valueTextSize = 12F

        //set labels to only be integers
        root.bar_chart.xAxis.isGranularityEnabled = true
        root.bar_chart.xAxis.granularity = 1f
        root.bar_chart.axisLeft.isGranularityEnabled = true
        root.bar_chart.axisLeft.granularity = 1f

        //add extra margins around the chart to accommodate increased text size of labels
        root.bar_chart.extraBottomOffset = 15F
        root.bar_chart.extraLeftOffset = 10F
        root.bar_chart.extraRightOffset = 10F

        //increase text size of labels (the numbers on the axes)
        root.bar_chart.xAxis.textSize = 15F
        root.bar_chart.axisLeft.textSize = 15F

        //put xAxis on the bottom instead of the top
        root.bar_chart.xAxis.position = XAxis.XAxisPosition.BOTTOM

        //set yAxis minimum to 0
        root.bar_chart.axisLeft.axisMinimum = 0F
        if ((datapoint!! in Constants.GRAPHABLE_BOOL) or (datapoint == "position_zero_starts") or (datapoint == "position_one_starts") or
            (datapoint == "position_two_starts") or (datapoint == "position_three_starts") or
            (datapoint == "position_four_starts")
        ) {
            root.bar_chart.axisLeft.axisMaximum = 1F
        }

        //Convert y-axis values to integers
        val intValueFormatter: ValueFormatter = object : ValueFormatter() {
            //value format here, here is the overridden method
            override fun getFormattedValue(value: Float): String {
                return "" + value.toInt()
            }
        }
        root.bar_chart.axisLeft.valueFormatter = intValueFormatter
        barDataSet.valueFormatter = intValueFormatter

        //draw x-axis labels, centered beneath the bars
        root.bar_chart.xAxis.setDrawLabels(true)
        root.bar_chart.xAxis.setCenterAxisLabels(true)

        //customize xAxisRenderer to display labels at specific positions
        val specificPositionLabelsXAxisRenderer: XAxisRenderer = object : XAxisRenderer(
            root.bar_chart.viewPortHandler,
            root.bar_chart.xAxis,
            root.bar_chart.getTransformer(YAxis.AxisDependency.LEFT)
        ) {
            override fun drawLabels(c: Canvas, pos: Float, anchor: MPPointF) {
                val specificLabelPositions: MutableList<Float> = mutableListOf()
                for (i in 0 until timDataMap.size) {
                    specificLabelPositions.add(i.toFloat())
                }
                val labelRotationAngleDegrees = mXAxis.labelRotationAngle
                val positions = FloatArray(specificLabelPositions.size * 2)
                for (i in positions.indices step 2) {
                    positions[i] = specificLabelPositions[i / 2]
                }

                mTrans.pointValuesToPixel(positions)

                for (i in positions.indices step 2) {
                    var x = positions[i]
                    if (mViewPortHandler.isInBoundsX(x)) {
                        val label = mXAxis.valueFormatter.getFormattedValue(
                            specificLabelPositions[i / 2],
                            mXAxis
                        )
                        if (mXAxis.isAvoidFirstLastClippingEnabled) {
                            // avoid clipping of the last
                            if (i == mXAxis.mEntryCount - 1 && mXAxis.mEntryCount > 1) {
                                val width = Utils.calcTextWidth(mAxisLabelPaint, label)
                                if (width > mViewPortHandler.offsetRight() * 2 && x + width > mViewPortHandler.chartWidth)
                                    x -= width / 2
                                // avoid clipping of the first
                            } else if (i == 0) {
                                val width = Utils.calcTextWidth(mAxisLabelPaint, label)
                                x += width / 2
                            }
                        }

                        drawLabel(c, label, x, pos, anchor, labelRotationAngleDegrees)
                    }
                }
            }
        }
        root.bar_chart.setXAxisRenderer(specificPositionLabelsXAxisRenderer)

        //format xAxis labels to be Q then the appropriate match number as an integer
        val xValueFormatter: ValueFormatter = object : ValueFormatter() {
            override fun getFormattedValue(value: Float): String {
                return "Q${timDataMap.keys.toList()[value.toInt()]}"
            }
        }
        root.bar_chart.xAxis.valueFormatter = xValueFormatter

        //set the number of labels so there won't be extra labels or missing labels
        root.bar_chart.xAxis.setLabelCount(barDataSet.entryCount, true)

        //show grid lines
        root.bar_chart.axisLeft.setDrawGridLines(true)
        root.bar_chart.xAxis.setDrawGridLines(true)
        root.bar_chart.xAxis.setDrawAxisLine(true)

        //disable right y-axis
        root.bar_chart.axisRight.isEnabled = false

        //remove legend
        root.bar_chart.legend.isEnabled = false

        //remove description label
        root.bar_chart.description.isEnabled = false

        //disable zooming
        root.bar_chart.isDoubleTapToZoomEnabled = false
        root.bar_chart.setPinchZoom(false)
        root.bar_chart.setScaleEnabled(false)

        //draw chart
        root.bar_chart.invalidate()

        //set up chart to detect clicking on different bars
        root.bar_chart.isHighlightFullBarEnabled = true

        //change the sensitivity of how close a click must be to a bar to register
        root.bar_chart.maxHighlightDistance = 11F

        //define function for what to do after a bar is clicked
        fun getOnChartValueSelectedListener(): OnChartValueSelectedListener {
            return object : OnChartValueSelectedListener {
                override fun onValueSelected(e: Entry?, h: Highlight?) {
                    root.bar_chart.setTouchEnabled(false)
                    //set match number to the x value of the entry selected
                    val matchNumberClicked: Int = timDataMap.keys.toList()[e!!.x.toInt()].toInt()
                    val matchDetailsFragment = MatchDetailsFragment()
                    val matchDetailsFragmentArguments = Bundle()
                    matchDetailsFragmentArguments.putInt(Constants.MATCH_NUMBER, matchNumberClicked)
                    matchDetailsFragment.arguments = matchDetailsFragmentArguments
                    fragmentManager!!.beginTransaction().addToBackStack(null).replace(
                        (view!!.parent as ViewGroup).id,
                        matchDetailsFragment
                    ).commit()
                }

                override fun onNothingSelected() {
                }
            }
        }

        //set on click listener to the function created
        root.bar_chart.setOnChartValueSelectedListener(getOnChartValueSelectedListener())

        return root
    }
}