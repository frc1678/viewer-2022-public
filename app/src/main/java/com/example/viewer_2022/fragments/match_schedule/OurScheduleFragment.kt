package com.example.viewer_2022.fragments.match_schedule

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.viewer_2022.MainViewerActivity
import com.example.viewer_2022.R
import com.example.viewer_2022.constants.Constants
import com.example.viewer_2022.fragments.match_details.MatchDetailsFragment
import com.example.viewer_2022.getMatchSchedule
import kotlinx.android.synthetic.main.fragment_match_schedule.view.*
import kotlinx.android.synthetic.main.match_schedule_cell.view.*

/**
 * Match schedule fragment with only our matches
 */
class OurScheduleFragment : MatchScheduleFragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_match_schedule, container, false)
        updateMatchScheduleListView(root, Constants.ScheduleType.OUR_MATCHES)
        val matchDetailsFragmentTransaction = this.fragmentManager!!.beginTransaction()
        root.lv_match_schedule.setOnItemClickListener { _, _, position, _ ->
            val matchDetailsFragment = MatchDetailsFragment()
            val matchDetailsFragmentArguments = Bundle()
            matchDetailsFragmentArguments.putInt(
                Constants.MATCH_NUMBER,
                getMatchSchedule(listOf(Constants.MY_TEAM_NUMBER), false).keys.elementAt(position)
                    .toInt()
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
                root.lv_match_schedule.invalidateViews()
            } else {
                // The match is not starred.
                MainViewerActivity.starredMatches.add(cell.tv_match_number.text.toString())
                root.lv_match_schedule.invalidateViews()
            }

            MainViewerActivity.StarredMatches.input()

            return@setOnItemLongClickListener true
        }
        return root
    }

}