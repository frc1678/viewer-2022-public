package com.example.viewer_2022.fragments.match_schedule

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.viewer_2022.MainViewerActivity
import com.example.viewer_2022.R
import com.example.viewer_2022.constants.Constants
import com.example.viewer_2022.data.Match
import com.example.viewer_2022.fragments.match_details.MatchDetailsFragment
import kotlinx.android.synthetic.main.fragment_match_schedule.view.*
import kotlinx.android.synthetic.main.match_schedule_cell.view.*
import com.example.viewer_2022.MainViewerActivity.StarredMatches

/**
 * Match schedule fragment with only starred matches
 */
class StarredMatchesFragment : MatchScheduleFragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_match_schedule, container, false)
        updateMatchScheduleListView(root, Constants.ScheduleType.STARRED_MATCHES)
        val matchDetailsFragmentTransaction = this.fragmentManager!!.beginTransaction()
        root.lv_match_schedule.setOnItemClickListener { _, _, position, _ ->
            val matchDetailsFragment = MatchDetailsFragment()
            val matchDetailsFragmentArguments = Bundle()
            matchDetailsFragmentArguments.putInt(
                Constants.MATCH_NUMBER,
                (root.lv_match_schedule.adapter.getItem(position) as Match).matchNumber.toInt()
            )
            matchDetailsFragment.arguments = matchDetailsFragmentArguments
            matchDetailsFragmentTransaction.setCustomAnimations(R.anim.fade_in, R.anim.fade_out)
            matchDetailsFragmentTransaction.addToBackStack(null).replace(
                (view!!.parent as ViewGroup).id,
                matchDetailsFragment
            ).commit()
        }
        // Mark matches as starred when long clicked.
        root.lv_match_schedule.setOnItemLongClickListener { _, _, position, _ ->
            val cell =
                root.lv_match_schedule.adapter.getView(position, null, root.lv_match_schedule)
            if (MainViewerActivity.starredMatches.contains(cell.tv_match_number.text.toString())) {
                // The match is already starred.
                MainViewerActivity.starredMatches.remove(cell.tv_match_number.text.toString())
                updateMatchScheduleListView(root, Constants.ScheduleType.STARRED_MATCHES)
            } else {
                // The match is not starred.
                MainViewerActivity.starredMatches.add(cell.tv_match_number.text.toString())
                updateMatchScheduleListView(root, Constants.ScheduleType.STARRED_MATCHES)
            }

            StarredMatches.input()

            return@setOnItemLongClickListener true
        }
        return root
    }
}
