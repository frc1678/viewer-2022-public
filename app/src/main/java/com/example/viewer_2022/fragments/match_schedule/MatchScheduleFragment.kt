/*
* MatchScheduleFragment.kt
* viewer
*
* Created on 1/26/2020
* Copyright 2020 Citrus Circuits. All rights reserved.
*/

package com.example.viewer_2022.fragments.match_schedule

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment
import com.example.viewer_2022.MainViewerActivity
import com.example.viewer_2022.R
import com.example.viewer_2022.constants.Constants
import com.example.viewer_2022.fragments.team_details.TeamDetailsFragment
import com.example.viewer_2022.getMatchSchedule
import kotlinx.android.synthetic.main.fragment_match_schedule.view.*

//The fragment of the match schedule 'view' that is one of the options of the navigation bar.
open class MatchScheduleFragment : Fragment() {
    private var teamNumber: String? = null
    private var refreshId: String? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        arguments?.let {
            teamNumber = it.getString(Constants.TEAM_NUMBER, null)
        }
        val root = inflater.inflate(R.layout.fragment_match_schedule, container, false)

        updateMatchScheduleListView(root, Constants.ScheduleType.ALL_MATCHES)

        return root
    }

    fun updateMatchScheduleListView(root: View, scheduleType: Constants.ScheduleType) {
        val adapter = MatchScheduleListAdapter(
            activity!!,
            (getMatchSchedule(
                (if (scheduleType == Constants.ScheduleType.OUR_MATCHES) listOf(Constants.MY_TEAM_NUMBER) else listOf()),
                scheduleType == Constants.ScheduleType.STARRED_MATCHES
            )
                    ),
            scheduleType,
            root.lv_match_schedule
        )
        if (refreshId == null) {
            refreshId = MainViewerActivity.refreshManager.addRefreshListener {
                Log.d("data-refresh", "Updated: match-schedule")
                adapter.notifyDataSetChanged()
            }
        }
        root.lv_match_schedule.adapter = adapter

        if (teamNumber != null) {
            root.match_search_bar.setText(teamNumber)
            val search = listOf(teamNumber!!)
            var matchesWanted = getMatchSchedule(
                search,
                false
            )
            if (!matchesWanted.isEmpty()) {
                (adapter as MatchScheduleListAdapter).updateData(
                    matchesWanted,
                    Constants.ScheduleType.OUR_MATCHES
                )
                Log.e("matchesWanted", "$matchesWanted")
            } else if (teamNumber!!.length == 0) {
                matchesWanted = getMatchSchedule((listOf()), true)
                (adapter as MatchScheduleListAdapter).updateData(
                    matchesWanted,
                    Constants.ScheduleType.STARRED_MATCHES
                )
            } else {
                (adapter as MatchScheduleListAdapter).updateData(
                    matchesWanted,
                    Constants.ScheduleType.OUR_MATCHES
                )
            }
        }

        root.match_search_bar.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                val search = mutableListOf(s.toString())
                if (scheduleType == Constants.ScheduleType.OUR_MATCHES) {
                    search.add(Constants.MY_TEAM_NUMBER)
                }
                var matchesWanted = getMatchSchedule(
                    search,
                    scheduleType == Constants.ScheduleType.STARRED_MATCHES
                )
                if (!matchesWanted.isEmpty()) {
                    (adapter as MatchScheduleListAdapter).updateData(
                        matchesWanted,
                        Constants.ScheduleType.OUR_MATCHES
                    )
                    Log.e("matchesWanted", "$matchesWanted")
                } else if (s.toString().length == 0) {
                    matchesWanted = getMatchSchedule(
                        (if (scheduleType == Constants.ScheduleType.OUR_MATCHES) listOf(Constants.MY_TEAM_NUMBER) else listOf()),
                        scheduleType == Constants.ScheduleType.STARRED_MATCHES
                    )
                    (adapter as MatchScheduleListAdapter).updateData(matchesWanted, scheduleType)
                } else {
                    (adapter as MatchScheduleListAdapter).updateData(
                        matchesWanted,
                        Constants.ScheduleType.OUR_MATCHES
                    )
                }
            }

            override fun afterTextChanged(s: Editable) {}
        })

        // This listener gets called when the keyboard enter button is pressed.
        // Opens the team details for the searched team.
        root.match_search_bar.setOnEditorActionListener { _, _, _ ->
            // If the searched team isn't valid, don't do anything
            if (!MainViewerActivity.teamList.contains(root.match_search_bar.text.toString())) {
                return@setOnEditorActionListener true
            }
            // Otherwise, go to the team details fragment
            fragmentManager!!.beginTransaction()
                .addToBackStack(null)
                .replace(R.id.nav_host_fragment, TeamDetailsFragment().apply {
                    // Put the team number into the arguments for the team details fragment to use
                    arguments = Bundle().apply {
                        putString(Constants.TEAM_NUMBER, root.match_search_bar.text.toString())
                    }
                })
                .commit()
            // Hide the keyboard once the new fragment has been created
            (context!!.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager)
                .hideSoftInputFromWindow(root.match_search_bar.windowToken, 0)
            // Return true to say that the enter action has been handled
            return@setOnEditorActionListener true
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        MainViewerActivity.refreshManager.removeRefreshListener(refreshId)
    }
}