package com.example.viewer_2022.fragments.team_ranking

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.LinearLayout
import androidx.fragment.app.FragmentActivity
import com.example.viewer_2022.R
import com.example.viewer_2022.TeamRankingItem
import com.example.viewer_2022.constants.Constants
import com.example.viewer_2022.getRankingTeam
import com.example.viewer_2022.getTeamDataValue
import kotlinx.android.synthetic.main.team_ranking_cell.view.*
import java.util.regex.Pattern

/**
 * Adapter for the team ranking list.
 */
class TeamRankingListAdapter(
    private val context: FragmentActivity,
    private val teamNumber: String?,
    private val dataPoint: String,
    private var items: List<TeamRankingItem>,
    private var pit: Boolean
) : BaseAdapter() {
    private val inflater: LayoutInflater =
        context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

    override fun getCount(): Int {
        return items.size
    }

    override fun getItem(position: Int): TeamRankingItem {
        return items[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val e = getItem(position)
        val rowView = inflater.inflate(R.layout.team_ranking_cell, parent, false)
        val regex: Pattern = Pattern.compile("-?" + "[0-9]+" + Regex.escape(".") + "[0-9]+")

        rowView.tv_team_number_ranking.text = e.teamNumber
        if (regex.matcher(e.value).matches()) {
            rowView.tv_value_ranking.text = ("%.2f").format(java.lang.Float.parseFloat(e.value))
        } else {
            rowView.tv_value_ranking.text = e.value
        }
        if (pit) {
            rowView.tv_team_ranking.text = ""
            rowView.tv_team_ranking.layoutParams =
                LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, 0f)
        } else if (e.value == Constants.NULL_CHARACTER) {
            rowView.tv_team_ranking.text = Constants.NULL_PREDICTED_SCORE_CHARACTER
        } else {
            rowView.tv_team_ranking.text =
                getRankingTeam(e.teamNumber, dataPoint).placement.toString()
        }

        if (e.teamNumber == teamNumber) {
            rowView.setBackgroundColor(context.resources.getColor(R.color.ElectricGreen))
        }


        return rowView
    }

    fun updateItems(newItems: List<TeamRankingItem>) {
        items = newItems
        this.notifyDataSetChanged()
    }
}

fun floatToString(float: Float): String {
    var str = float.toString()

    if (str.endsWith(".0")) {
        str = str.dropLast(2)
    }
    return str
}