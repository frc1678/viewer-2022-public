package com.example.viewer_2022.fragments.match_details

import android.content.Context
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import com.example.viewer_2022.R
import com.example.viewer_2022.constants.Constants
import com.example.viewer_2022.constants.Translations
import com.example.viewer_2022.getTIMDataValueByMatch
import com.example.viewer_2022.getTeamDataValue
import kotlinx.android.synthetic.main.match_details_cell.view.*

/**
 * Adapter for the match details datapoint list
 */
class MatchDetailsAdapter(
    private val context: FragmentActivity,
    private val datapointsDisplay: List<String>,
    private val matchNumber: Int,
    private val teamNumbers: List<String>,
    private val hasActualData: Boolean
) : BaseAdapter() {
    init {
        Log.d("data-refresh", "created match details adapter: teamNumbers: $teamNumbers")
    }

    private val inflater =
        context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

    /** Returns the size of the list of the data points to be displayed. */
    override fun getCount() = datapointsDisplay.size

    /** Returns the specific data point given the position of the data point. */
    override fun getItem(position: Int) = datapointsDisplay[position]

    /** Returns the position of the cell. */
    override fun getItemId(position: Int) = position.toLong()

    /** Populate the elements of the custom cell. */
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val rowView = inflater.inflate(R.layout.match_details_cell, parent, false)
        rowView.tv_datapoint_name.text =
            Translations.ACTUAL_TO_HUMAN_READABLE[datapointsDisplay[position]]
                ?: datapointsDisplay[position]
        if (datapointsDisplay[position] in Constants.CATEGORY_NAMES) {
            val noWidth = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, 0f)
            val allWidth = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, 1f)
            rowView.tv_datapoint_name.gravity = Gravity.CENTER_HORIZONTAL
            rowView.tv_datapoint_name.setTextColor(ContextCompat.getColor(context, R.color.Black))
            rowView.tv_datapoint_name.setBackgroundColor(
                ContextCompat.getColor(context, R.color.LightGray)
            )
            rowView.tv_datapoint_name.layoutParams = allWidth
            rowView.tv_team_one_md.layoutParams = noWidth
            rowView.tv_team_two_md.layoutParams = noWidth
            rowView.tv_team_three_md.layoutParams = noWidth
            rowView.tv_team_four_md.layoutParams = noWidth
            rowView.tv_team_five_md.layoutParams = noWidth
            rowView.tv_team_six_md.layoutParams = noWidth
            rowView.tv_team_one_md.text = ""
            rowView.tv_team_two_md.text = ""
            rowView.tv_team_three_md.text = ""
            rowView.tv_team_four_md.text = ""
            rowView.tv_team_five_md.text = ""
            rowView.tv_team_six_md.text = ""
        } else {
            val textViews = listOf<TextView>(
                rowView.tv_team_one_md, rowView.tv_team_two_md, rowView.tv_team_three_md,
                rowView.tv_team_four_md, rowView.tv_team_five_md, rowView.tv_team_six_md
            )
            for (i in 0..5) {
                textViews[i].text =
                    if (!hasActualData) getTeamValue(teamNumbers[i], datapointsDisplay[position])
                    else getTIMDataValueByMatch(
                        matchNumber.toString(),
                        teamNumbers[i],
                        datapointsDisplay[position]
                    )
            }
        }
        return rowView
    }

    private fun getTeamValue(teamNumber: String, field: String): String {
        // If the datafield is a float, round the datapoint.
        // Otherwise, get returned string from getTeamDataValue.
        val regex = Regex("-?" + "[0-9]+" + Regex.escape(".") + "[0-9]+")
        val dataValue = getTeamDataValue(teamNumber, field)
        return if (regex matches dataValue) {
            if (field in Constants.DRIVER_DATA) "%.2f".format(dataValue.toFloat())
            else "%.1f".format(dataValue.toFloat())
        } else getTeamDataValue(teamNumber, field)
    }
}
