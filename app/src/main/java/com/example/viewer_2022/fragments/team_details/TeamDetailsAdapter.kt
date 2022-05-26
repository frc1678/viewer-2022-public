package com.example.viewer_2022.fragments.team_details

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.util.TypedValue
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import com.example.viewer_2022.R
import com.example.viewer_2022.constants.Constants
import com.example.viewer_2022.constants.Translations
import com.example.viewer_2022.getTeamDataValue
import kotlinx.android.synthetic.main.team_details_cell.view.*
import java.lang.Float.parseFloat
import java.util.regex.Pattern
import android.widget.FrameLayout
import androidx.core.view.isVisible
import com.example.viewer_2022.MainViewerActivity
import com.example.viewer_2022.fragments.match_schedule.MatchScheduleFragment
import com.example.viewer_2022.fragments.notes.NotesFragment
import com.example.viewer_2022.fragments.team_ranking.TeamRankingFragment
import com.example.viewer_2022.getRankingTeam

// Custom list adapter class for each list view of the six teams featured in every MatchDetails display.
// TODO implement a type 'Team' object parameter to access the team data for the team number.
class TeamDetailsAdapter(
    private val context: FragmentActivity,
    private val datapointsDisplayed: List<String>,
    private val teamNumber: String
) : BaseAdapter() {

    private val inflater: LayoutInflater =
        context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

    // Return the size of the list of the data points to be displayed.
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

    // Populate the elements of the custom cell.
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View? {
        val graphsFragment = GraphsFragment()
        val graphsFragmentArguments = Bundle()
        val e = getItem(position)
        val regex: Pattern = Pattern.compile("-?" + "[0-9]+" + Regex.escape(".") + "[0-9]+")
        val rowView = inflater.inflate(R.layout.team_details_cell, parent, false)
        var isHeader = false
        rowView.tv_datapoint_name.text =
            Translations.ACTUAL_TO_HUMAN_READABLE[e]
                ?: e
        if (e in listOf(
                "Auto",
                "L4M Auto",
                "Tele",
                "L4M Tele",
                "Endgame",
                "L4M Endgame",
                "Pit Data",
                "See Matches",
                "Notes"
            )
        ) {
            isHeader = true
            rowView.tv_datapoint_name.setTextSize(TypedValue.COMPLEX_UNIT_SP, 28F)
            rowView.tv_datapoint_name.gravity = Gravity.CENTER_HORIZONTAL
            val noWidth = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, 0f)
            val allWidth = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, 1f)
            rowView.tv_ranking.layoutParams = noWidth
            rowView.tv_datapoint_name.layoutParams = allWidth
            rowView.tv_datapoint_value.layoutParams = noWidth
            if (e == "See Matches") {
                rowView.tv_datapoint_name.setBackgroundColor(
                    ContextCompat.getColor(
                        context,
                        R.color.MediumGray
                    )
                )
            } else {
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
            }
            rowView.tv_datapoint_value.text = ""

            if (e == "Notes") {
                if (Constants.USE_TEST_DATA) {
                    rowView.isVisible = false
                }
                Log.d("notes", "SETTING UP NOTES CELL IN TEAM DETAILS")
                rowView.tv_datapoint_name.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14F)
                rowView.tv_datapoint_name.setBackgroundColor(context.resources.getColor(R.color.Highlighter))
                rowView.setOnClickListener {
                    Log.d("notes", "notes button clicked in team details")
                    val notesFragment = NotesFragment()
                    val notesFragmentArgs = Bundle()
                    notesFragmentArgs.putString(Constants.TEAM_NUMBER, teamNumber)
                    notesFragment.arguments = notesFragmentArgs
                    val notesFragmentTransaction =
                        context.supportFragmentManager.beginTransaction()
                    notesFragmentTransaction?.addToBackStack(null).replace(
                        (it.rootView.findViewById(R.id.nav_host_fragment) as ViewGroup).id,
                        notesFragment
                    )?.commit()
                }
                if (MainViewerActivity.notesCache.containsKey(teamNumber)) {
                    rowView.tv_datapoint_name.text = MainViewerActivity.notesCache[teamNumber]
                } else {
                    Log.d("notes", "notesCache does not contain team $teamNumber")
                    rowView.tv_datapoint_name.text = ""
                }
            }
        } else {
            if (regex.matcher(
                    getTeamDataValue(
                        teamNumber,
                        e
                    )
                ).matches()
            ) {
                rowView.tv_datapoint_value.text = ("%.2f").format(
                    parseFloat(
                        getTeamDataValue(
                            teamNumber,
                            e
                        )
                    )
                )
            } else {
                rowView.tv_datapoint_value.text = getTeamDataValue(
                    teamNumber,
                    e
                )
            }
        }
        if (e in Constants.RANKABLE_FIELDS) {
            rowView.tv_ranking.text = if (e in Constants.PIT_DATA) "" else getRankingTeam(
                teamNumber,
                e
            ).placement.toString()
        }

        if (Constants.GRAPHABLE.contains(datapointsDisplayed[position]) or
            Constants.GRAPHABLE_BOOL.contains(datapointsDisplayed[position]) or
            Constants.GRAPHABLE_CLIMB_TIMES.contains(datapointsDisplayed[position])
        ) {
            rowView.setOnClickListener {
                graphsFragmentArguments.putString(Constants.TEAM_NUMBER, teamNumber)
                graphsFragmentArguments.putString("datapoint", datapointsDisplayed[position])
                graphsFragment.arguments = graphsFragmentArguments
                context.supportFragmentManager.beginTransaction()
                    .addToBackStack(null)
                    .replace(R.id.nav_host_fragment, graphsFragment, "graphs")
                    .commit()
            }
        }

        if (e == "See Matches") {
            rowView.setOnClickListener {
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
        }

        //Headers don't need on click handlers
        if (!isHeader) {
            //Some fields (eg drivetrain_motor_type) don't need to be rankable
            if (e in Constants.RANKABLE_FIELDS.keys) {
                rowView.setOnLongClickListener {
                    val teamRankingFragment = TeamRankingFragment()
                    val teamRankingFragmentArguments = Bundle()
                    val teamRankingFragmentTransaction =
                        context.supportFragmentManager.beginTransaction()

                    //add the data point key to the bundle
                    teamRankingFragmentArguments.putString(TeamRankingFragment.DATA_POINT, e)
                    //add the team number to the bundle
                    teamRankingFragmentArguments.putString(
                        TeamRankingFragment.TEAM_NUMBER,
                        teamNumber
                    )

                    //attach the bundle to the fragment
                    teamRankingFragment.arguments = teamRankingFragmentArguments

//                println((it.rootView.findViewById(R.id.nav_host_fragment) as ViewGroup))


                    teamRankingFragmentTransaction.addToBackStack(null).replace(
                        (it.rootView.findViewById(R.id.nav_host_fragment) as ViewGroup).id,
                        teamRankingFragment
                    ).commit()

                    println(e)
                    //return true says that the onLongClick was handled successfully so haptic feedback can happen correctly
                    return@setOnLongClickListener true
                }
            }

        }

        return rowView
    }
}