package com.example.viewer_2022.fragments.notes

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.viewer_2022.MainViewerActivity
import com.example.viewer_2022.R
import com.example.viewer_2022.constants.Constants
import com.example.viewer_2022.data.GetRequestTask
import com.example.viewer_2022.data.PostRequestTask
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.fragment_notes.view.*
import java.lang.Exception
import java.lang.reflect.Type
import java.net.HttpURLConnection
import java.net.URL

/**
 * Page that displays strategist notes
 */
class NotesFragment : Fragment() {

    var mode = Mode.VIEW

    var teamNumber: String? = null

    var refreshId: String? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_notes, container, false)
        arguments?.let {
            teamNumber = it.getString(Constants.TEAM_NUMBER)
        }
        if (refreshId == null) {
            refreshId = MainViewerActivity.refreshManager.addRefreshListener {
                if (this.mode == Mode.VIEW) {
                    getNotes(root)
                }
            }
        }

        setupListeners(root)
        getNotes(root)

        return root
    }

    private fun setupListeners(root: View) {
        root.btn_edit_notes.setOnClickListener {
            mode = when (mode) {
                Mode.VIEW -> {
                    setupEditMode(root)
                    Mode.EDIT
                }
                Mode.EDIT -> {
                    setupViewMode(root)
                    Mode.VIEW
                }
            }
        }
    }

    private fun setupEditMode(root: View) {
        root.btn_edit_notes.setImageResource(R.drawable.ic_baseline_save_24)
        root.et_notes.isEnabled = true
    }

    private fun setupViewMode(root: View) {
        root.btn_edit_notes.setImageResource(R.drawable.ic_baseline_edit_24)
        root.et_notes.isEnabled = false
        val data = SetNotesData(teamNumber!!, root.et_notes.text.toString())
        Log.d("notes", Gson().toJson(data))
        root.btn_edit_notes.isEnabled = false
        try {
            MainViewerActivity.notesCache[teamNumber!!] = root.et_notes.text.toString()
            PostRequestTask("notes/", Gson().toJson(data)) {
                root.btn_edit_notes.isEnabled = true
            }.execute()
        } catch (e: Exception) {
            Log.e("NOTES", "FAILED TO SAVE NOTES. WE JUST LOST DATA. FIX IMMEDIATELY")
        }

    }

    private fun getNotes(root: View) {
        root.btn_edit_notes.isEnabled = false
        try {
            GetRequestTask("notes/$teamNumber") {
                try {
                    val resp = Gson().fromJson(it, GetNotesData::class.java)
                    if (resp.success) {
                        root.et_notes.setText(resp.notes)
                    }
                } catch (e: Exception) {
                    Log.e(
                        "notes",
                        "FAILED TO PARSE JSON FOR $teamNumber. THIS IS NOT GOOD. VERY VERY BAD. CARDINAL PROBABLY THREW A 500"
                    )
                }
                root.btn_edit_notes.isEnabled = true
            }.execute()
        } catch (e: Exception) {
            Log.e("notes", "FAILED TO FETCH NOTES FOR $teamNumber. THIS IS NOT GOOD. VERY VERY BAD")
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        MainViewerActivity.refreshManager.removeRefreshListener(refreshId)
    }

    enum class Mode {
        EDIT,
        VIEW
    }
}

data class NotesData(val team_number: String, val notes: String)
typealias SetNotesData = NotesData

data class GetNotesData(val success: Boolean, val notes: String)

val GetAllNotesData: Type = object : TypeToken<List<NotesData>>() {}.type