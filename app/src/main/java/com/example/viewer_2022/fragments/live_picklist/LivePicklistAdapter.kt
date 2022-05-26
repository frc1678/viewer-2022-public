package com.example.viewer_2022.fragments.live_picklist

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.*
import com.example.viewer_2022.MainViewerActivity
import com.example.viewer_2022.PicklistConnectionManager
import com.example.viewer_2022.R
import com.example.viewer_2022.constants.Constants
import com.example.viewer_2022.databinding.LivePicklistCellBinding
import com.example.viewer_2022.fragments.team_details.TeamDetailsFragment
import com.google.gson.Gson
import kotlinx.coroutines.runBlocking

/**
 * Recycler adapter for live picklist
 */
class LivePicklistRecyclerAdapter(val context: LivePicklistFragment) :
    ListAdapter<String, LivePicklistRecyclerAdapter.LivePicklistViewHolder>(object :
        DiffUtil.ItemCallback<String>() {

        override fun areItemsTheSame(oldItem: String, newItem: String): Boolean {
            return oldItem.contentEquals(newItem)
        }

        override fun areContentsTheSame(oldItem: String, newItem: String): Boolean {
            return oldItem.contentEquals(newItem)
        }


    }) {
    inner class LivePicklistViewHolder(private val itemViewBinding: LivePicklistCellBinding) :
        RecyclerView.ViewHolder(itemViewBinding.root) {
        fun bindRoot(teamNumber: String) {
            if (context.order.contains(teamNumber)) {
                itemViewBinding.tvFirstRank.text =
                    context.order.indexOf(teamNumber).plus(1).toString()
                itemViewBinding.root.setBackgroundColor(context.resources.getColor(R.color.White))
            } else {
                itemViewBinding.root.setBackgroundColor(context.resources.getColor(R.color.Red))
                itemViewBinding.tvFirstRank.text = "-"
            }
            itemViewBinding.tvTeamNumber.text = teamNumber
            itemViewBinding.root.setOnClickListener { onClick(teamNumber) }

        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LivePicklistViewHolder {
        return LivePicklistViewHolder(

            LivePicklistCellBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )

        )
    }

    override fun onBindViewHolder(holder: LivePicklistViewHolder, position: Int) {

        val picklistItem = this.getItem(position)
        holder.bindRoot(picklistItem)
    }


    fun onClick(teamNumber: String) {
        if (teamNumber in MainViewerActivity.teamList) {
            val teamDetailsFragment = TeamDetailsFragment()

            // Put the arguments for the team details fragment.
            teamDetailsFragment.arguments = Bundle().also {
                it.putString(
                    Constants.TEAM_NUMBER,
                    teamNumber
                )
            }
            // Switch to the team details fragment.
            val ft = context.fragmentManager!!.beginTransaction()
            ft.setCustomAnimations(R.anim.fade_in, R.anim.fade_out)
            context.view?.rootView?.findViewById<ViewGroup>(R.id.nav_host_fragment)?.let {
                ft.addToBackStack(null).replace(it.id, teamDetailsFragment)
                    .commit()
            }
        }
    }

    fun handleOrderChange(from: Int, to: Int) {
        runBlocking {
            val data = UpdateDataRequest(
                to_place = to + 1,
                from_place = from + 1
            )
            val dataText = Gson().toJson(data)
            Log.d("picklist", "Sending order update with data: $dataText")
            PicklistConnectionManager.send(dataText)
        }
    }

    fun handleDNPToggle(position: Int) {
        val teamNumber = this.getItem(position)
        runBlocking {
            val data = ToggleDNPRequest(team_number = teamNumber.toInt())
            val dataText = Gson().toJson(data)
            Log.d("picklist", "Sending order update with data: $dataText")
            PicklistConnectionManager.send(dataText)
        }
    }
}

