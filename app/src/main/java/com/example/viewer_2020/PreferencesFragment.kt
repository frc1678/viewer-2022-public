package com.example.viewer_2020

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import kotlinx.android.synthetic.main.fragment_preferences.*
import kotlinx.android.synthetic.main.fragment_preferences.view.*
import com.example.viewer_2020.MainViewerActivity.UserDatapoints
import com.example.viewer_2020.constants.Constants
class PreferencesFragment: IFrag() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?


    ): View? {

        val root = inflater.inflate(R.layout.fragment_preferences, container, false)
        val versionNumber = this.getString(R.string.tv_version_num, Constants.VERSION_NUM)
        root.tv_version_num.text = versionNumber
        context?.let { createSpinner(it, root.spin_user, R.array.user_array) }

        val name = UserDatapoints.contents?.get("selected")?.asString?.toLowerCase()?.capitalize()
        val namePosition = resources.getStringArray(R.array.user_array).indexOf(name)
        root.spin_user.setSelection(namePosition)

        root.btn_user_pref_edit.setOnClickListener() {
            val userPreferencesFragment = UserPreferencesFragment()

            fragmentManager!!.beginTransaction().addToBackStack(null).replace(
                (view!!.parent as ViewGroup).id,
                userPreferencesFragment
            ).commit()
        }

        return root
    }

    private fun createSpinner(context: Context, spinner: Spinner, array: Int) {

        ArrayAdapter.createFromResource(
            context, array, R.layout.spinner_layout
        ).also { adapter ->
            adapter.setDropDownViewResource(R.layout.spinner_layout)
            spinner.adapter = adapter
        }


        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parentView: AdapterView<*>,
                selectedItemView: View?,
                position: Int,
                id: Long
            ) {
                var userName: String = spin_user.selectedItem.toString().toUpperCase()

                UserDatapoints.contents?.remove("selected")
                UserDatapoints.contents?.addProperty("selected", userName)
                UserDatapoints.write()
            }

            override fun onNothingSelected(parentView: AdapterView<*>) {
                return
            }
        }


    }
}
