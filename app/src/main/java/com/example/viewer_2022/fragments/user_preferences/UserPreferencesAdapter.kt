package com.example.viewer_2022.fragments.user_preferences

import android.content.Context
import android.util.TypedValue
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import com.example.viewer_2022.MainViewerActivity.UserDatapoints
import com.example.viewer_2022.R
import com.example.viewer_2022.constants.Constants
import com.example.viewer_2022.constants.Translations
import com.google.gson.JsonArray
import kotlinx.android.synthetic.main.team_details_cell.view.*

/**
 * Adapter for the user preferences list
 */
class UserPreferencesAdapter(
    private val context: FragmentActivity,
    private val datapointsDisplayed: List<String>
) : BaseAdapter() {

    val fullDatapoints = Constants.FIELDS_TO_BE_DISPLAYED_TEAM_DETAILS
    var chosenDatapoints: MutableSet<String> = mutableSetOf()
    lateinit var intersectDatapoints: Set<String>
    var userName = UserDatapoints.contents?.get("selected")?.asString

    private val inflater: LayoutInflater =
        context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

    override fun getCount(): Int {
        return datapointsDisplayed.size
    }

    // Returns the specific data point given the position of the data point.
    override fun getItem(position: Int): String {
        return datapointsDisplayed[position]
    }

    // Returns the position of the cell.
    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {

        val datapointName = getItem(position)
        val rowView = inflater.inflate(R.layout.user_pref_cell, parent, false)

        var isGreen = false

        rowView.tv_datapoint_name.text =
            Translations.ACTUAL_TO_HUMAN_READABLE[datapointName]
                ?: datapointName
        if (datapointsDisplayed[position] in Constants.CATEGORY_NAMES) {
            rowView.tv_datapoint_name.setTextSize(TypedValue.COMPLEX_UNIT_SP, 28F)
            rowView.tv_datapoint_name.gravity = Gravity.CENTER_HORIZONTAL
            val allWidth = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, 1f)
            rowView.tv_datapoint_name.layoutParams = allWidth
            rowView.tv_datapoint_name.setBackgroundColor(
                ContextCompat.getColor(
                    context,
                    R.color.LightGray
                )
            )
            rowView.tv_datapoint_name.setTextColor(
                ContextCompat.getColor(
                    context,
                    R.color.Black
                )
            )

            chosenDatapoints.add(datapointName)
            rowView.isEnabled = false
        }

        val datapointsArray = UserDatapoints.contents?.get(userName)?.asJsonArray

        for (datapoint in datapointsArray!!) {
            if (datapoint.asString == datapointName) {
                chosenDatapoints.add(datapointName)
                rowView.setBackgroundColor(ContextCompat.getColor(context, R.color.ElectricGreen))
                isGreen = true
            }
        }

        rowView.setOnClickListener() {
            isGreen = if (!isGreen) {
                rowView.setBackgroundColor(ContextCompat.getColor(context, R.color.ElectricGreen))
                chosenDatapoints.add(datapointName)
                true
            } else {
                rowView.setBackgroundColor(ContextCompat.getColor(context, R.color.White))
                chosenDatapoints.remove(datapointName)
                false
            }

            intersectDatapoints = fullDatapoints intersect chosenDatapoints
            val jsonArray = JsonArray()
            for (datapoint in intersectDatapoints) {
                jsonArray.add(datapoint)
            }

            UserDatapoints.contents?.remove(userName)
            UserDatapoints.contents?.add(userName, jsonArray)
            UserDatapoints.write()
        }

        return rowView
    }
}
