package com.example.viewer_2022.fragments.team_list

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.viewer_2022.MainViewerActivity
import com.example.viewer_2022.R
import com.example.viewer_2022.constants.Constants
import com.example.viewer_2022.fragments.notes.NotesFragment
import com.example.viewer_2022.fragments.team_details.TeamDetailsFragment
import kotlinx.android.synthetic.main.fragment_team_list.view.*

// displays the team numbers in team list in numerical order
class TeamListFragment : Fragment() {
    private val teamDetailsFragment = TeamDetailsFragment()
    private val teamDetailsFragmentArguments = Bundle()
    private val list: List<String> = MainViewerActivity.teamList.sortedBy { it.toInt() }

    private var refreshId: String? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_team_list, container, false)

        root.lv_team_list.setOnItemClickListener { _, _, position, _ ->
            val teamListFragmentTransaction = this.fragmentManager!!.beginTransaction()
            teamDetailsFragmentArguments.putString(
                Constants.TEAM_NUMBER,
                list[position]
            )
            teamDetailsFragment.arguments = teamDetailsFragmentArguments
            teamListFragmentTransaction.setCustomAnimations(R.anim.fade_in, R.anim.fade_out)
            teamListFragmentTransaction.addToBackStack(null).replace(
                (view!!.parent as ViewGroup).id,
                teamDetailsFragment
            ).commit()
        }

        root.lv_team_list.setOnItemLongClickListener { _, _, position, _ ->
            val teamNumber = list[position]
            val notesFragment = NotesFragment()
            val notesFragmentArgs = Bundle()
            notesFragmentArgs.putString(Constants.TEAM_NUMBER, teamNumber)
            notesFragment.arguments = notesFragmentArgs
            val notesFragmentTransaction = this.fragmentManager?.beginTransaction()
            notesFragmentTransaction?.addToBackStack(null)?.replace(
                (view?.parent as ViewGroup).id,
                notesFragment
            )?.commit()
            return@setOnItemLongClickListener true
        }

        updateTeamListView(root)

        return root
    }

    private fun updateTeamListView(root: View) {
        val adapter = TeamListAdapter(
            context = activity!!,
            items = list
        )
        if (refreshId == null) {
            refreshId = MainViewerActivity.refreshManager.addRefreshListener {
                Log.d("data-refresh", "Updated: team-list")
                adapter.notifyDataSetInvalidated()
            }
        }
        root.lv_team_list.adapter = adapter
    }

    override fun onDestroy() {
        super.onDestroy()
        MainViewerActivity.refreshManager.removeRefreshListener(refreshId)
    }
}
