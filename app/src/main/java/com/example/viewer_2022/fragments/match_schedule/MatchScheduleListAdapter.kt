/*
* MatchScheduleListAdapter.kt
* viewer
*
* Created on 1/26/2020
* Copyright 2020 Citrus Circuits. All rights reserved.
*/

package com.example.viewer_2022.fragments.match_schedule

import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.Typeface.*
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import com.example.viewer_2022.*
import com.example.viewer_2022.data.Match
import com.example.viewer_2022.constants.Constants
import com.example.viewer_2022.R
import com.example.viewer_2022.fragments.match_details.MatchDetailsFragment
import java.lang.Float.parseFloat
import com.example.viewer_2022.MainViewerActivity.StarredMatches

// Custom list adapter class with Match object handling to display the custom cell for the match schedule.
class MatchScheduleListAdapter(
    private val context: FragmentActivity,
    private var matchContents: Map<String, Match>,
    private var scheduleType: Constants.ScheduleType,
    private var listView: ListView
) : BaseAdapter() {

    private val inflater = LayoutInflater.from(context)

    // Return the size of the match schedule.
    override fun getCount(): Int {
        return matchContents.size
    }

    // Return the Match object given the match number.
    override fun getItem(position: Int): Match? {
        return when (scheduleType) {
            Constants.ScheduleType.OUR_MATCHES, Constants.ScheduleType.STARRED_MATCHES ->
                matchContents[matchContents.keys.toList()[position]]
            else -> matchContents[(position + 1).toString()]
        }
    }

    fun updateData(newData: Map<String, Match>, oneTeam: Constants.ScheduleType) {
        matchContents = newData
        scheduleType = oneTeam
        notifyDataSetChanged()
    }

    // Return the position of the cell.
    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    // Populate the elements of the custom cell.
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var blueAct = false
        var redAct = false

        val viewHolder: ViewHolder
        val rowView: View?
        val matchNumber: String = when (scheduleType) {
            Constants.ScheduleType.OUR_MATCHES, Constants.ScheduleType.STARRED_MATCHES ->
                matchContents.keys.toList()[position]
            else -> (position + 1).toString()
        }


        if (convertView == null) {
            rowView = inflater.inflate(R.layout.match_schedule_cell, parent, false)
            viewHolder =
                ViewHolder(
                    rowView
                )
            rowView.tag = viewHolder
        } else {
            rowView = convertView
            viewHolder = rowView.tag as ViewHolder
        }

        if (getAllianceInMatchObjectByKey(
                Constants.PROCESSED_OBJECT.CALCULATED_PREDICTED_ALLIANCE_IN_MATCH.value,
                Constants.RED, matchNumber,
                "has_actual_data"
            ).toBoolean()
        ) {
            redAct = true
        }
        if (getAllianceInMatchObjectByKey(
                Constants.PROCESSED_OBJECT.CALCULATED_PREDICTED_ALLIANCE_IN_MATCH.value,
                Constants.BLUE, matchNumber,
                "has_actual_data"
            ).toBoolean()
        ) {
            blueAct = true
        }

        for (tv in listOf(
            viewHolder.tvRedTeamOne,
            viewHolder.tvRedTeamTwo,
            viewHolder.tvRedTeamThree
        )) {

            tv.paintFlags = 0
            tv.setTypeface(DEFAULT)

            tv.text = matchContents[matchNumber]!!.redTeams[listOf(
                viewHolder.tvRedTeamOne,
                viewHolder.tvRedTeamTwo,
                viewHolder.tvRedTeamThree
            ).indexOf(tv)]
        }
        for (tv in listOf(
            viewHolder.tvBlueTeamOne,
            viewHolder.tvBlueTeamTwo,
            viewHolder.tvBlueTeamThree
        )) {

            tv.paintFlags = 0
            tv.setTypeface(DEFAULT)

            tv.text = matchContents[matchNumber]!!.blueTeams[0 +
                    listOf(
                        viewHolder.tvBlueTeamOne,
                        viewHolder.tvBlueTeamTwo,
                        viewHolder.tvBlueTeamThree
                    ).indexOf(tv)]
        }
        viewHolder.tvMatchNumber.text = matchNumber

        if (blueAct && redAct) {
            if (getAllianceInMatchObjectByKey(
                    Constants.PROCESSED_OBJECT.CALCULATED_PREDICTED_ALLIANCE_IN_MATCH.value,
                    Constants.RED, matchNumber, "won_match"
                ).toBoolean()
            ) {

                for (tv in listOf(
                    viewHolder.tvRedTeamOne,
                    viewHolder.tvRedTeamTwo,
                    viewHolder.tvRedTeamThree
                )) {
                    tv.paintFlags = Paint.UNDERLINE_TEXT_FLAG
                    tv.setTypeface(Typeface.DEFAULT_BOLD)
                }
            } else {
                for (tv in listOf(
                    viewHolder.tvBlueTeamOne,
                    viewHolder.tvBlueTeamTwo,
                    viewHolder.tvBlueTeamThree
                )) {
                    tv.paintFlags = Paint.UNDERLINE_TEXT_FLAG
                    tv.setTypeface(Typeface.DEFAULT_BOLD)
                }
            }
        }

        val field: String

        if (redAct && blueAct) {
            field = "actual_rp"
            viewHolder.wholeLine.setBackgroundColor(
                ContextCompat.getColor(
                    context,
                    R.color.LightGray
                )
            )
        } else {
            field = "predicted_rp"
            viewHolder.wholeLine.setBackgroundColor(
                ContextCompat.getColor(
                    context,
                    R.color.White
                )
            )
        }

        if ((!blueAct or !redAct) && MainViewerActivity.matchCache[matchNumber]!!.bluePredictedScore != null) {
            viewHolder.tvBluePredictedScore.text =
                MainViewerActivity.matchCache[matchNumber]!!.bluePredictedScore.toString()
        } else if (blueAct && redAct && MainViewerActivity.matchCache[matchNumber]!!.blueActualScore != null) {
            viewHolder.tvBluePredictedScore.text =
                (if (blueAct) "%.0f" else "%.1f").format(MainViewerActivity.matchCache[matchNumber]!!.blueActualScore)
        } else {
            val value = if (blueAct && redAct) {
                getAllianceInMatchObjectByKey(
                    Constants.PROCESSED_OBJECT.CALCULATED_PREDICTED_ALLIANCE_IN_MATCH.value,
                    Constants.BLUE, matchNumber, "actual_score"
                )
            } else {
                getAllianceInMatchObjectByKey(
                    Constants.PROCESSED_OBJECT.CALCULATED_PREDICTED_ALLIANCE_IN_MATCH.value,
                    Constants.BLUE, matchNumber, "predicted_score"
                )
            }
            if (value != Constants.NULL_CHARACTER) {
                viewHolder.tvBluePredictedScore.text =
                    (if (blueAct) "%.0f" else "%.1f").format(value.toFloat())
                if (!blueAct or !redAct) {
                    MainViewerActivity.matchCache[matchNumber]!!.bluePredictedScore =
                        parseFloat(("%.1f").format(value.toFloat()))
                } else {
                    MainViewerActivity.matchCache[matchNumber]!!.blueActualScore =
                        parseFloat(("%.0f").format(value.toFloat()))
                }
            } else {
                viewHolder.tvBluePredictedScore.text =
                    Constants.NULL_PREDICTED_SCORE_CHARACTER
            }
        }

        if ((!redAct or !blueAct) && MainViewerActivity.matchCache[matchNumber]!!.redPredictedScore != null) {
            viewHolder.tvRedPredictedScore.text =
                MainViewerActivity.matchCache[matchNumber]!!.redPredictedScore.toString()
        } else if (redAct && blueAct && MainViewerActivity.matchCache[matchNumber]!!.redActualScore != null) {
            viewHolder.tvRedPredictedScore.text =
                (if (redAct) "%.0f" else "%.1f").format(MainViewerActivity.matchCache[matchNumber]!!.redActualScore)
        } else {
            val value = if (redAct && blueAct) {
                getAllianceInMatchObjectByKey(
                    Constants.PROCESSED_OBJECT.CALCULATED_PREDICTED_ALLIANCE_IN_MATCH.value,
                    Constants.RED, matchNumber, "actual_score"
                )
            } else {
                getAllianceInMatchObjectByKey(
                    Constants.PROCESSED_OBJECT.CALCULATED_PREDICTED_ALLIANCE_IN_MATCH.value,
                    Constants.RED, matchNumber, "predicted_score"
                )
            }
            if (value != Constants.NULL_CHARACTER) {
                viewHolder.tvRedPredictedScore.text =
                    (if (redAct) "%.0f" else "%.1f").format(value.toFloat())
                if (!redAct or !blueAct) {
                    MainViewerActivity.matchCache[matchNumber]!!.redPredictedScore =
                        parseFloat(("%.1f").format(value.toFloat()))
                } else {
                    MainViewerActivity.matchCache[matchNumber]!!.redActualScore =
                        parseFloat(("%.0f").format(value.toFloat()))
                }
            } else {
                viewHolder.tvRedPredictedScore.text =
                    Constants.NULL_PREDICTED_SCORE_CHARACTER
            }
        }

        for (tv in listOf(viewHolder.tvBluePredictedScore, viewHolder.tvRedPredictedScore)) {
            if (tv.text == Constants.NULL_PREDICTED_SCORE_CHARACTER) {
                tv.setTextColor(
                    ContextCompat.getColor(
                        context,
                        R.color.ElectricGreen
                    )
                )
            } else {
                tv.setTextColor(
                    ContextCompat.getColor(
                        context,
                        R.color.Black
                    )
                )
            }
        }
        red_predicted@ for (tv in listOf(
            viewHolder.tvRedPredictedRPOne,
            viewHolder.tvRedPredictedRPTwo
        )) {
            when (listOf(
                viewHolder.tvRedPredictedRPOne,
                viewHolder.tvRedPredictedRPTwo
            ).indexOf(tv)) {
                0 -> {
                    if (redAct && blueAct) {
                        if (MainViewerActivity.matchCache[matchNumber]!!.redActualRPOne != null &&
                            MainViewerActivity.matchCache[matchNumber]!!.redActualRPOne!!.toDouble() >
                            Constants.PREDICTED_RANKING_POINT_QUALIFICATION
                        ) {
                            tv.setImageResource(R.drawable.cargo_ball)
                            continue@red_predicted
                        }
                    } else if (MainViewerActivity.matchCache[matchNumber]!!.redPredictedRPOne != null &&
                        MainViewerActivity.matchCache[matchNumber]!!.redPredictedRPOne!!.toDouble() >
                        Constants.PREDICTED_RANKING_POINT_QUALIFICATION
                    ) {
                        tv.setImageResource(R.drawable.cargo_ball)
                        continue@red_predicted
                    }
                }
                1 -> {
                    if (redAct && blueAct) {
                        if (MainViewerActivity.matchCache[matchNumber]!!.redActualRPTwo != null &&
                            MainViewerActivity.matchCache[matchNumber]!!.redActualRPTwo!!.toDouble() >
                            Constants.PREDICTED_RANKING_POINT_QUALIFICATION
                        ) {
                            tv.setImageResource(R.drawable.pull_up_bars)
                            continue@red_predicted
                        }
                    } else if (MainViewerActivity.matchCache[matchNumber]!!.redPredictedRPTwo != null &&
                        MainViewerActivity.matchCache[matchNumber]!!.redPredictedRPTwo!!.toDouble() >
                        Constants.PREDICTED_RANKING_POINT_QUALIFICATION
                    ) {
                        tv.setImageResource(R.drawable.pull_up_bars)
                        continue@red_predicted
                    }
                }
            }
            val value = getAllianceInMatchObjectByKey(
                Constants.PROCESSED_OBJECT.CALCULATED_PREDICTED_ALLIANCE_IN_MATCH.value,
                Constants.RED, matchNumber, field +
                        "${
                            listOf(
                                viewHolder.tvRedPredictedRPOne,
                                viewHolder.tvRedPredictedRPTwo
                            ).indexOf(tv) + 1
                        }"
            )
            if (value != Constants.NULL_CHARACTER &&
                value.toDouble() > Constants.PREDICTED_RANKING_POINT_QUALIFICATION
            ) {
                when (listOf(
                    viewHolder.tvRedPredictedRPOne,
                    viewHolder.tvRedPredictedRPTwo
                ).indexOf(tv)) {
                    0 -> {
                        if (redAct && blueAct) {
                            MainViewerActivity.matchCache[matchNumber]!!.redActualRPOne =
                                parseFloat(("%.0f").format(value.toFloat()))
                        } else {
                            MainViewerActivity.matchCache[matchNumber]!!.redPredictedRPOne =
                                parseFloat(("%.1f").format(value.toFloat()))
                        }
                    }
                    1 -> {
                        if (redAct && blueAct) {
                            MainViewerActivity.matchCache[matchNumber]!!.redActualRPTwo =
                                parseFloat(("%.0f").format(value.toFloat()))
                        } else {
                            MainViewerActivity.matchCache[matchNumber]!!.redPredictedRPTwo =
                                parseFloat(("%.1f").format(value.toFloat()))
                        }
                    }
                }
            } else tv.setImageDrawable(null)
        }
        blue_predicted@ for (tv in listOf(
            viewHolder.tvBluePredictedRPOne,
            viewHolder.tvBluePredictedRPTwo
        )) {
            when (listOf(
                viewHolder.tvBluePredictedRPOne,
                viewHolder.tvBluePredictedRPTwo
            ).indexOf(tv)) {
                0 -> {
                    if (blueAct && redAct) {
                        if (MainViewerActivity.matchCache[matchNumber]!!.blueActualRPOne != null &&
                            MainViewerActivity.matchCache[matchNumber]!!.blueActualRPOne!!.toDouble() >
                            Constants.PREDICTED_RANKING_POINT_QUALIFICATION
                        ) {
                            tv.setImageResource(R.drawable.cargo_ball)
                            continue@blue_predicted
                        }
                    } else if (MainViewerActivity.matchCache[matchNumber]!!.bluePredictedRPOne != null &&
                        MainViewerActivity.matchCache[matchNumber]!!.bluePredictedRPOne!!.toDouble() >
                        Constants.PREDICTED_RANKING_POINT_QUALIFICATION
                    ) {
                        tv.setImageResource(R.drawable.cargo_ball)
                        continue@blue_predicted
                    }
                }
                1 -> {
                    if (blueAct && redAct) {
                        if (MainViewerActivity.matchCache[matchNumber]!!.blueActualRPTwo != null &&
                            MainViewerActivity.matchCache[matchNumber]!!.blueActualRPTwo!!.toDouble() >
                            Constants.PREDICTED_RANKING_POINT_QUALIFICATION
                        ) {
                            tv.setImageResource(R.drawable.pull_up_bars)
                            continue@blue_predicted
                        }
                    } else if (MainViewerActivity.matchCache[matchNumber]!!.bluePredictedRPTwo != null &&
                        MainViewerActivity.matchCache[matchNumber]!!.bluePredictedRPTwo!!.toDouble() >
                        Constants.PREDICTED_RANKING_POINT_QUALIFICATION
                    ) {
                        tv.setImageResource(R.drawable.pull_up_bars)
                        continue@blue_predicted
                    }
                }
            }
            val value = getAllianceInMatchObjectByKey(
                Constants.PROCESSED_OBJECT.CALCULATED_PREDICTED_ALLIANCE_IN_MATCH.value,
                Constants.BLUE, matchNumber, field +
                        "${
                            listOf(
                                viewHolder.tvBluePredictedRPOne,
                                viewHolder.tvBluePredictedRPTwo
                            ).indexOf(tv) + 1
                        }"
            )
            if (value != Constants.NULL_CHARACTER &&
                value.toDouble() > Constants.PREDICTED_RANKING_POINT_QUALIFICATION
            ) {
                when (listOf(
                    viewHolder.tvBluePredictedRPOne,
                    viewHolder.tvBluePredictedRPTwo
                ).indexOf(tv)) {
                    0 -> {
                        if (redAct && blueAct) {
                            MainViewerActivity.matchCache[matchNumber]!!.blueActualRPOne =
                                parseFloat(("%.0f").format(value.toFloat()))
                        } else {
                            MainViewerActivity.matchCache[matchNumber]!!.bluePredictedRPOne =
                                parseFloat(("%.1f").format(value.toFloat()))
                        }
                    }
                    1 -> {
                        if (redAct && blueAct) {
                            MainViewerActivity.matchCache[matchNumber]!!.blueActualRPTwo =
                                parseFloat(("%.0f").format(value.toFloat()))
                        } else {
                            MainViewerActivity.matchCache[matchNumber]!!.bluePredictedRPTwo =
                                parseFloat(("%.1f").format(value.toFloat()))
                        }
                    }
                }
            } else tv.setImageDrawable(null)
        }
        if (MainViewerActivity.starredMatches.contains(matchNumber)) {
            viewHolder.wholeLine.setBackgroundColor(
                ContextCompat.getColor(
                    context,
                    if (redAct && blueAct) R.color.DarkYellow
                    else R.color.Yellow
                )
            )
        }

        fun matchClick(it: View) {
            val matchDetailsFragment = MatchDetailsFragment()
            Log.d("data-refresh", "created MatchDetailsFragment in MatchSchedule")
            val matchDetailsFragmentArguments = Bundle()
            val matchDetailsFragmentTransaction = context.supportFragmentManager.beginTransaction()
            val matchSelected =
                if ((scheduleType == Constants.ScheduleType.OUR_MATCHES) or (scheduleType == Constants.ScheduleType.STARRED_MATCHES)) {
                    matchContents.keys.toList()[position].toInt()
                } else {
                    position + 1
                }
            matchDetailsFragmentArguments.putInt(Constants.MATCH_NUMBER, matchSelected)
            matchDetailsFragment.arguments = matchDetailsFragmentArguments
            matchDetailsFragmentTransaction.setCustomAnimations(R.anim.fade_in, R.anim.fade_out)
            matchDetailsFragmentTransaction.addToBackStack(null).replace(
                (it.rootView.findViewById(R.id.nav_host_fragment) as ViewGroup).id,
                matchDetailsFragment
            ).commit()
        }

        fun teamLongClick(selected: Button, it: View) {
            val teamNumber = selected.text.toString()
            val matchScheduleFragment = MatchScheduleFragment()
            val matchScheduleFragmentArguments = Bundle()
            val matchScheduleFragmentTransaction =
                context.supportFragmentManager.beginTransaction()
            matchScheduleFragmentArguments.putString(Constants.TEAM_NUMBER, teamNumber)
            matchScheduleFragment.arguments = matchScheduleFragmentArguments
            matchScheduleFragmentTransaction.addToBackStack(null).replace(
                (it.rootView.findViewById(R.id.nav_host_fragment) as ViewGroup).id,
                matchScheduleFragment
            ).commit()
        }

        // When an item click occurs, go to the MatchDetails fragment of the match item clicked.
        rowView!!.setOnClickListener {
            matchClick(it)
        }
        viewHolder.tvRedTeamOne.setOnClickListener {
            matchClick(it)
        }
        viewHolder.tvRedTeamTwo.setOnClickListener {
            matchClick(it)
        }
        viewHolder.tvRedTeamThree.setOnClickListener {
            matchClick(it)
        }
        viewHolder.tvBlueTeamOne.setOnClickListener {
            matchClick(it)
        }
        viewHolder.tvBlueTeamTwo.setOnClickListener {
            matchClick(it)
        }
        viewHolder.tvBlueTeamThree.setOnClickListener {
            matchClick(it)
        }

        viewHolder.tvRedTeamOne.setOnLongClickListener {
            teamLongClick(viewHolder.tvRedTeamOne, it)
            return@setOnLongClickListener true
        }
        viewHolder.tvRedTeamTwo.setOnLongClickListener {
            teamLongClick(viewHolder.tvRedTeamTwo, it)
            return@setOnLongClickListener true
        }
        viewHolder.tvRedTeamThree.setOnLongClickListener {
            teamLongClick(viewHolder.tvRedTeamThree, it)
            return@setOnLongClickListener true
        }
        viewHolder.tvBlueTeamOne.setOnLongClickListener {
            teamLongClick(viewHolder.tvBlueTeamOne, it)
            return@setOnLongClickListener true
        }
        viewHolder.tvBlueTeamTwo.setOnLongClickListener {
            teamLongClick(viewHolder.tvBlueTeamTwo, it)
            return@setOnLongClickListener true
        }
        viewHolder.tvBlueTeamThree.setOnLongClickListener {
            teamLongClick(viewHolder.tvBlueTeamThree, it)
            return@setOnLongClickListener true
        }

        // Mark matches as starred when long clicked.
        rowView.setOnLongClickListener {
            if (MainViewerActivity.starredMatches.contains(viewHolder.tvMatchNumber.text.toString())) {
                // The match is already starred.
                MainViewerActivity.starredMatches.remove(viewHolder.tvMatchNumber.text.toString())
                listView.invalidateViews()
            } else {
                // The match is not starred.
                MainViewerActivity.starredMatches.add(viewHolder.tvMatchNumber.text.toString())
                listView.invalidateViews()
            }
            StarredMatches.input()
            return@setOnLongClickListener true
        }
        return rowView
    }

    // View holder class to handle the elements used in the custom cells.
    private class ViewHolder(view: View?) {
        val tvMatchNumber = view?.findViewById(R.id.tv_match_number) as TextView
        val tvBluePredictedScore = view?.findViewById(R.id.tv_blue_predicted_score) as TextView
        val tvRedPredictedScore = view?.findViewById(R.id.tv_red_predicted_score) as TextView
        val tvBluePredictedRPOne = view?.findViewById(R.id.tv_blue_predicted_rp1) as ImageView
        val tvRedPredictedRPOne = view?.findViewById(R.id.tv_red_predicted_rp1) as ImageView
        val tvBluePredictedRPTwo = view?.findViewById(R.id.tv_blue_predicted_rp2) as ImageView
        val tvRedPredictedRPTwo = view?.findViewById(R.id.tv_red_predicted_rp2) as ImageView
        val tvBlueTeamOne = view?.findViewById(R.id.tv_blue_team_one) as Button
        val tvBlueTeamTwo = view?.findViewById(R.id.tv_blue_team_two) as Button
        val tvBlueTeamThree = view?.findViewById(R.id.tv_blue_team_three) as Button
        val tvRedTeamOne = view?.findViewById(R.id.tv_red_team_one) as Button
        val tvRedTeamTwo = view?.findViewById(R.id.tv_red_team_two) as Button
        val tvRedTeamThree = view?.findViewById(R.id.tv_red_team_three) as Button
        val wholeLine = view?.findViewById(R.id.whole_line) as LinearLayout
    }
}
