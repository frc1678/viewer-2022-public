package com.example.viewer_2022

import android.app.ActionBar
import android.app.ActivityOptions
import android.content.Intent
import android.view.KeyEvent
import androidx.appcompat.app.AppCompatActivity
import java.util.*
import java.text.SimpleDateFormat


//Super class of all activity based classes for this project.
//Used to implement class mechanisms that all activities should comprise of.
var lastUpdated: Date = Calendar.getInstance().time;

open class ViewerActivity : AppCompatActivity() {
    //When the back press is held down, this function will confirm the long click and then 'restart'
    //the app by sending it to the mode collection activity and resetting the mode.
    override fun onKeyLongPress(keyCode: Int, event: KeyEvent): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            MainViewerActivity.refreshManager.removeAllListeners()
            intentToMainViewerActivity()
        }
        return super.onKeyLongPress(keyCode, event)
    }

    //Begins the intent to the main activity when the long back press is clicked.
    private fun intentToMainViewerActivity() {
        startActivity(
            Intent(this, MainViewerActivity::class.java),
            ActivityOptions.makeSceneTransitionAnimation(this).toBundle()
        )
    }

    fun setToolbarText(view: ActionBar?, support: androidx.appcompat.app.ActionBar?) {
        val headerText = "Viewer"
        view?.title = headerText
        support?.title = headerText
        view?.show()
        support?.show()
    }

    fun getTimeText(): String {
        val sdf = SimpleDateFormat("MM/dd/yy hh:mm:ss a", Locale.getDefault())
        return sdf.format(lastUpdated)
    }
}