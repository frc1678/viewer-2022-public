package com.example.viewer_2022.fragments.user_preferences

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.viewer_2022.MainViewerActivity.UserDatapoints
import com.example.viewer_2022.R
import com.example.viewer_2022.constants.Constants
import com.google.gson.JsonArray
import com.google.gson.JsonParser
import kotlinx.android.synthetic.main.fragment_user_pref.view.*
import java.io.InputStreamReader

/**
 * Page to select the user's preference of datapoints to be displayed.
 */
class UserPreferencesFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val root = inflater.inflate(R.layout.fragment_user_pref, container, false)

        val userName = UserDatapoints.contents?.get("selected")?.asString

        root.user_datapoints_header.text = if (userName == "NONE") {
            "User's Datapoints"
        } else {
            "${userName?.toLowerCase()?.capitalize()}'s Datapoints"
        }

        updateUserDatapointsListView(root)

        root.reset_button.setOnClickListener() {
            val defaultsJsonArray: JsonArray = JsonParser.parseReader(
                InputStreamReader
                    (context?.resources?.openRawResource(R.raw.default_prefs))
            )
                .asJsonObject.get(userName).asJsonArray

            UserDatapoints.contents?.remove(userName)
            UserDatapoints.contents?.add(userName, defaultsJsonArray)
            UserDatapoints.write()

            root.lv_user_datapoints.invalidateViews()
            updateUserDatapointsListView(root)
        }
        return root
    }

    private fun updateUserDatapointsListView(root: View) {
        val datapointsDisplay = Constants.FIELDS_TO_BE_DISPLAYED_TEAM_DETAILS

        val adapter = UserPreferencesAdapter(
            context = activity!!,
            datapointsDisplayed = datapointsDisplay - listOf("See Matches", "Notes")

        )
        root.lv_user_datapoints.adapter = adapter

    }
}