/*
* MainViewerActivity.kt
* viewer
*
* Created on 1/26/2020
* Copyright 2020 Citrus Circuits. All rights reserved.
*/

package com.example.viewer_2020

import android.Manifest
import android.app.AlertDialog
import android.content.pm.PackageManager
import android.content.Intent
import android.content.res.Resources
import android.os.Bundle
import android.os.Environment
import android.os.PersistableBundle
import android.util.Log
import android.view.MenuItem
import android.widget.TextView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.content.ContextCompat
import androidx.core.app.ActivityCompat
import androidx.core.view.GravityCompat
import androidx.core.view.get
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.FragmentManager.POP_BACK_STACK_INCLUSIVE
import com.example.viewer_2020.constants.Constants
import com.example.viewer_2020.data.DatabaseReference
import com.example.viewer_2020.data.Match
import com.example.viewer_2020.data.Team
import com.example.viewer_2020.fragments.match_schedule.OurScheduleFragment
import com.example.viewer_2020.fragments.pickability.PickabilityFragment
import com.example.viewer_2020.fragments.pickability.PickabilityMode
import com.example.viewer_2020.fragments.ranking.PredRankingFragment
import com.example.viewer_2020.fragments.team_list.TeamListFragment
import com.google.android.material.navigation.NavigationView
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.mongodb_database_startup_splash_screen.*
import java.io.File


// Main activity class that handles the dual fragment view.
class MainViewerActivity : ViewerActivity() {

    lateinit var toggle: ActionBarDrawerToggle


    private var matchScheduleFragment = MatchScheduleFragment()
    private var ourScheduleFragment = OurScheduleFragment()
    private var rankingFragment = RankingFragment()
    private var firstPickabilityFragment = PickabilityFragment(PickabilityMode.FIRST)
    private var secondPickabilityFragment = PickabilityFragment(PickabilityMode.SECOND)
    private val teamListFragment = TeamListFragment()

    private val frags: List<IFrag> =
        listOf(
            matchScheduleFragment,
            ourScheduleFragment,
            rankingFragment,
            firstPickabilityFragment,
            secondPickabilityFragment,
            teamListFragment
        )

    companion object {
        var currentRankingMenuItem: MenuItem? = null
        var teamCache: HashMap<String, Team> = HashMap()
        var matchCache: MutableMap<String, Match> = HashMap()
        var teamList: List<String> = listOf()
    }


    //Overrides back button to go back to last fragment.
    //Disables the back button and returns nothing when in the startup match schedule.
    override fun onBackPressed() {
        val drawerLayout: DrawerLayout = findViewById(R.id.container)
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) drawerLayout.closeDrawer(GravityCompat.START)
        if (supportFragmentManager.fragments.last().tag == "rankings") {
            supportFragmentManager.popBackStack(0, 0)
            supportFragmentManager.beginTransaction()
                .addToBackStack(null)
                .replace(R.id.nav_host_fragment, MatchScheduleFragment(), "matchSchedule")
                .commit()
        } else if (supportFragmentManager.backStackEntryCount > 1) supportFragmentManager.popBackStack()
    }

    fun reloadAllListViews(){
        frags.forEachIndexed { i, e ->
            Log.e("help", "refreshing $i")
            e.updateListView()
        }
    }

    override fun onResume() {
        super.onResume()
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
            try {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(
                        Manifest.permission.READ_EXTERNAL_STORAGE
                    ),
                    100
                )
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        supportActionBar?.hide()
        setToolbarText(actionBar, supportActionBar)

        val drawerLayout: DrawerLayout = findViewById(R.id.container)
        val navView: NavigationView = findViewById(R.id.navigation)
        navView.setCheckedItem(R.id.nav_menu_match_schedule)
        toggle = ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close)
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        val matchScheduleFragment = MatchScheduleFragment()
        val ourScheduleFragment = OurScheduleFragment()
        val rankingFragment = RankingFragment()
        val firstPickabilityFragment = PickabilityFragment(PickabilityMode.FIRST)
        val secondPickabilityFragment = PickabilityFragment(PickabilityMode.SECOND)
        val teamListFragment = TeamListFragment()

        updateNavFooter()

        //default screen when the viewer starts (after pulling data)
        supportFragmentManager.beginTransaction()
            .addToBackStack(null)
            .replace(R.id.nav_host_fragment, matchScheduleFragment, "matchSchedule")
            .commit()

        Log.e("ALL_DATA_FROM_WEBSITE", "${MongoDatabaseStartupActivity.databaseReference}")

        data_refresh_button.setOnClickListener {
            data_refresh_button.isEnabled = false
            if (Constants.USE_TEST_DATA) {
                GetDataFromFiles(this, {
                    data_refresh_button.isEnabled = true
                    Snackbar.make(container, "Refreshed Data!", 2500).show()
                    reloadAllListViews()
                    updateNavFooter()
                }, {
                    data_refresh_button.isEnabled = true
                    Snackbar.make(container, "Data Failed to load", 2500).show()
                }).execute()
            } else {
                GetDataFromWebsite({
                    data_refresh_button.isEnabled = true
                    Snackbar.make(container, "Refreshed Data!", 2500).show()
                    reloadAllListViews()
                    updateNavFooter()
                }, {
                    data_refresh_button.isEnabled = true
                    Snackbar.make(container, "Data Failed to load", 2500).show()
                }).execute()
            }
        }

        navView.setNavigationItemSelectedListener {
            if (drawerLayout.isDrawerOpen(GravityCompat.START)) drawerLayout.closeDrawer(
                GravityCompat.START
            )
            when (it.itemId) {

                R.id.nav_menu_match_schedule -> {
                    supportFragmentManager.popBackStack(0, 0)
                    supportFragmentManager.beginTransaction()
                        .addToBackStack(null)
                        .replace(R.id.nav_host_fragment, matchScheduleFragment, "matchSchedule")
                        .commit()
                }

                R.id.nav_menu_our_match_schedule -> {
                    val ft = supportFragmentManager.beginTransaction()
                    if (supportFragmentManager.fragments.last().tag != "ourSchedule") ft.addToBackStack(
                        null
                    )
                    ft.replace(R.id.nav_host_fragment, ourScheduleFragment, "ourSchedule")
                        .commit()
                }

                R.id.nav_menu_rankings -> {
                    supportFragmentManager.beginTransaction()
                        .addToBackStack(null)
                        .replace(R.id.nav_host_fragment, rankingFragment, "rankings")
                        .commit()
                }


                R.id.nav_menu_pickability_first -> {
                    val ft = supportFragmentManager.beginTransaction()
                    if (supportFragmentManager.fragments.last().tag != "pickabilityFirst") ft.addToBackStack(
                        null
                    )
                    ft.replace(R.id.nav_host_fragment, firstPickabilityFragment, "pickabilityFirst")
                        .commit()
                }

                R.id.nav_menu_pickability_second -> {
                    val ft = supportFragmentManager.beginTransaction()
                    if (supportFragmentManager.fragments.last().tag != "pickabilitySecond") ft.addToBackStack(
                        null
                    )
                    ft.replace(
                        R.id.nav_host_fragment,
                        secondPickabilityFragment,
                        "pickabilitySecond"
                    )
                        .commit()
                }

                R.id.nav_menu_team_list -> {
                    val ft = supportFragmentManager.beginTransaction()
                    if (supportFragmentManager.fragments.last().tag != "teamList") ft.addToBackStack(
                        null
                    )
                    ft.replace(R.id.nav_host_fragment, teamListFragment, "teamlist")
                        .commit()
                }

            }

            true
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        if (toggle.onOptionsItemSelected(item)) {

            return true

        }
        return super.onOptionsItemSelected(item)
    }

    fun updateNavFooter(){
        findViewById<TextView>(R.id.nav_footer).text =
            getString(R.string.last_updated, super.getTimeText())
    }
}