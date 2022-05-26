/*
* RankingFragment.kt
* viewer
*
* Created on 1/26/2020
* Copyright 2020 Citrus Circuits. All rights reserved.
*/

package com.example.viewer_2022.fragments.ranking

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.viewer_2022.MainViewerActivity
import com.example.viewer_2022.R
import com.example.viewer_2022.constants.Constants
import com.example.viewer_2022.constants.Translations
import com.example.viewer_2022.convertToFilteredTeamsList
import com.example.viewer_2022.fragments.team_details.TeamDetailsFragment
import kotlinx.android.synthetic.main.fragment_ranking.view.*


// The fragment of the ranking lists 'view' that is one of the options of the navigation bar.
// Disclaimer: This fragment contains another menu bar which is displayed directly above the
// main menu bar. This navigation/menu bar does not switch between fragments on each menu's selection
// like the main menu bar does. This navigation bar only receives the position/ID of the menu selected
// and then updated the adapter of the list view that is right above it.
class RankingFragment : Fragment() {

    private val teamDetailsFragment = TeamDetailsFragment()
    private val teamDetailsFragmentArguments = Bundle()

    private var refreshId: String? = null


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Usage of counter variable to increment at every iteration of the menu item forEach statement.
        // Used to find the index of the current selected menu in the menu item list because the
        // MenuItem object does not have an indexOf() return function.
        val root = inflater.inflate(R.layout.fragment_ranking, container, false)
        // This is a listener for the navigation bar onClick.
        // When a menu item of the navigation bar is clicked, it sends the MenuItem to
        // the handler activity (MainViewerActivity) to set the adapter of this fragment's
        // resource layout to the correct adapter given the new menu item that should be displayed.

        root.tv_ranking_header.text = "Rankings"
        root.btn_toggle.text = "To Pred. Rankings"

        root.tv_datapoint_two.text =
            Translations.ACTUAL_TO_HUMAN_READABLE[Constants.FIELDS_TO_BE_DISPLAYED_RANKING[1]]
        root.tv_datapoint_three.text =
            Translations.ACTUAL_TO_HUMAN_READABLE[Constants.FIELDS_TO_BE_DISPLAYED_RANKING[2]]
        root.tv_datapoint_four.text =
            Translations.ACTUAL_TO_HUMAN_READABLE[Constants.FIELDS_TO_BE_DISPLAYED_RANKING[3]]
        root.tv_datapoint_five.text =
            Translations.ACTUAL_TO_HUMAN_READABLE[Constants.FIELDS_TO_BE_DISPLAYED_RANKING[4]]
        val adapter = RankingListAdapter(
            activity!!, convertToFilteredTeamsList(
                Constants.PROCESSED_OBJECT.CALCULATED_PREDICTED_TEAM.value,
                MainViewerActivity.teamList
            )
        )
        if (refreshId == null) {
            refreshId = MainViewerActivity.refreshManager.addRefreshListener {
                Log.d("data-refresh", "Updated: ranking")
                adapter.notifyDataSetChanged()
            }
        }
        root.lv_ranking.adapter = adapter

        root.lv_ranking.setOnItemClickListener { _, _, position, _ ->
            val rankingFragmentTransaction = this.fragmentManager!!.beginTransaction()
            teamDetailsFragmentArguments.putString(
                Constants.TEAM_NUMBER, convertToFilteredTeamsList(
                    Constants.PROCESSED_OBJECT.CALCULATED_PREDICTED_TEAM.value,
                    MainViewerActivity.teamList
                )[position]
            )
            teamDetailsFragment.arguments = teamDetailsFragmentArguments
            rankingFragmentTransaction.setCustomAnimations(R.anim.fade_in, R.anim.fade_out)
            rankingFragmentTransaction.addToBackStack(null).replace(
                (view!!.parent as ViewGroup).id,
                teamDetailsFragment
            ).commit()
        }

        root.btn_toggle.setOnClickListener {
            toggleToPredicted()
        }
        return root
    }

    fun newInstance(): RankingFragment {
        return RankingFragment()
    }

    fun toggleToPredicted() {
        val predictedRankingFragment = PredRankingFragment()
        val ft = fragmentManager!!.beginTransaction()
        if (fragmentManager!!.fragments.last().tag != "predRankings") ft.addToBackStack(null)
        ft.replace(R.id.nav_host_fragment, predictedRankingFragment, "predRankings")
            .commit()
        return
    }

    override fun onDestroy() {
        super.onDestroy()
        MainViewerActivity.refreshManager.removeRefreshListener(refreshId)
    }
}