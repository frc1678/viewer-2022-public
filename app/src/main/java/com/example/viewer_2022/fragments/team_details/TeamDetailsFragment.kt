/*
* TeamDetailsFragment.kt
* viewer
*
* Created on 2/19/2020
* Copyright 2020 Citrus Circuits. All rights reserved.
*/

package com.example.viewer_2022.fragments.team_details

import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import com.example.viewer_2022.MainViewerActivity
import com.example.viewer_2022.R
import com.example.viewer_2022.constants.Constants
import com.example.viewer_2022.fragments.notes.NotesFragment
import com.example.viewer_2022.getTeamDataValue
import kotlinx.android.synthetic.main.team_details.*
import kotlinx.android.synthetic.main.team_details.view.*
import java.io.File

// The fragment class for the Team Details display that occurs when you click on a
// team in the match details page.
class TeamDetailsFragment : Fragment() {
    private var teamNumber: String? = null
    private var teamName: String? = null

    private var refreshId: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?

    ): View? {
        val root = inflater.inflate(R.layout.team_details, container, false)

        populateTeamDetailsEssentials(root)
        updateDatapointDisplayListView(root)
        robotPics(root)

        // This creates the on menu select listener for the TeamDetails fragment navigation bar.
        // The purpose of this navigation bar is to switch between the type of data that the
        // list view in team details displays. The list view adapter contents can be altered
        // in Constants.kt -> FIELDS_TO_BE_DISPLAYED_TEAM_DETAILS. FIELDS_TO_BE_DISPLAYED_TEAM_DETAILS
        // is a map of a string key to arraylist<string> value, with each string key being the menu
        // items and the contents of each arraylist<string> being the specific data points displayed
        // in each of the menu item's adapter settings sections.
        return root
    }


    // Prepare the TeamDetails page by populating each text view and other XML element
    // with its team specific information.
    private fun populateTeamDetailsEssentials(root: View) {
        // If a fragment intent (bundle arguments) exists from the previous activity (MainViewerActivity),
        // then set the team number display on TeamDetails to the team number provided with the intent.

        // If the team number from the MainViewerActivity's match schedule list view cell position
        // is null, the default display will show '0' for the team number on TeamDetails.
        arguments?.let {
            teamNumber = it.getString(Constants.TEAM_NUMBER, Constants.NULL_CHARACTER)
            teamName = getTeamDataValue(teamNumber!!, "team_name")
        }
        root.tv_team_number.text = teamNumber.toString()
        root.tv_team_name.text = teamName.toString()
    }

    // Updates the adapter for the list view of each team in the match details display.
    private fun updateDatapointDisplayListView(root: View) {
        // We set the adapter for their list view according to
        // the team number and the current section. We also include a list of the
        // data points we expect to be displayed on the TeamDetails list view.
        var dataDisplay = Constants.FIELDS_TO_BE_DISPLAYED_TEAM_DETAILS
        var isChecked = false
        val adapter = TeamDetailsAdapter(
            context = activity!!,
            datapointsDisplayed = Constants.FIELDS_TO_BE_DISPLAYED_TEAM_DETAILS,
            teamNumber = teamNumber!!
        )
        if (refreshId == null) {
            refreshId = MainViewerActivity.refreshManager.addRefreshListener {
                Log.d("data-refresh", "Updated: team-details")
                adapter.notifyDataSetChanged()
            }
        }
        root.lv_datapoint_display.adapter = adapter
// Repopulates the list view based on whether LFM is toggled or not
        root.btn_lfm.setOnClickListener {
            if (!isChecked) {
                isChecked = true
                dataDisplay = Constants.FIELDS_TO_BE_DISPLAYED_LFM
                btn_lfm.text = "To All Matches"

            } else {
                isChecked = false
                dataDisplay = Constants.FIELDS_TO_BE_DISPLAYED_TEAM_DETAILS
                btn_lfm.text = "To L4M"
            }

            val adapter = TeamDetailsAdapter(
                context = activity!!,
                datapointsDisplayed = dataDisplay,
                teamNumber = teamNumber!!
            )
            root.lv_datapoint_display.adapter = adapter
        }
    }

    private fun robotPics(root: View) {
        val robotPicFragmentArguments = Bundle()
        val robotPicFragment = RobotPicFragment()
        if (!File(
                "/storage/emulated/0/${Environment.DIRECTORY_DOWNLOADS}/",
                "${teamNumber}_full_robot_1.jpg"
            ).exists()
            && !File(
                "/storage/emulated/0/${Environment.DIRECTORY_DOWNLOADS}/",
                "${teamNumber}_full_robot_2.jpg"
            ).exists()
        ) {
            root.robot_pic_button.layoutParams = LinearLayout.LayoutParams(0, 0, 0f)
            root.tv_team_number.gravity = Gravity.CENTER_HORIZONTAL
            root.tv_team_name.gravity = Gravity.CENTER_HORIZONTAL
        } else {
            root.robot_pic_button.setOnClickListener {
                robotPicFragmentArguments.putString(Constants.TEAM_NUMBER, teamNumber)
                robotPicFragment.arguments = robotPicFragmentArguments
                activity!!.supportFragmentManager.beginTransaction()
                    .addToBackStack(null)
                    .replace(R.id.nav_host_fragment, robotPicFragment, "robot_pic")
                    .commit()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        MainViewerActivity.refreshManager.removeRefreshListener(refreshId)
    }
}