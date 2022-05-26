/*
* MainViewerActivity.kt
* viewer
*
* Created on 1/26/2020
* Copyright 2020 Citrus Circuits. All rights reserved.
*/

package com.example.viewer_2022

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.Gravity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.LinearLayout
import android.widget.PopupWindow
import android.widget.TextView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.app.ActivityCompat
import androidx.core.net.toUri
import androidx.core.view.GravityCompat
import androidx.customview.widget.ViewDragHelper
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.FragmentManager
import com.example.viewer_2022.MainViewerActivity.StarredMatches.contents
import com.example.viewer_2022.constants.Constants
import com.example.viewer_2022.data.*
import com.example.viewer_2022.fragments.match_schedule.MatchScheduleFragment
import com.example.viewer_2022.fragments.match_schedule.OurScheduleFragment
import com.example.viewer_2022.fragments.match_schedule.StarredMatchesFragment
import com.example.viewer_2022.fragments.pickability.PickabilityFragment
import com.example.viewer_2022.fragments.pickability.PickabilityMode
import com.example.viewer_2022.fragments.preferences.PreferencesFragment
import com.example.viewer_2022.fragments.ranking.RankingFragment
import com.example.viewer_2022.fragments.team_list.TeamListFragment
import com.google.android.material.navigation.NavigationView
import com.google.gson.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.field_map_popup.view.*
import kotlinx.android.synthetic.main.field_map_popup.view.close_button
import kotlinx.android.synthetic.main.pit_map_popup.view.*
import java.io.*


// Main activity class that handles navigation.
class MainViewerActivity : ViewerActivity() {

    lateinit var toggle: ActionBarDrawerToggle

    companion object {
        var teamCache: HashMap<String, Team> = HashMap()
        var matchCache: MutableMap<String, Match> = HashMap()
        var timCache = object : HashSet<TeamInMatch>() {
            /**
             * The largest size the cache can have. If the cache size exceeds this, the cache will
             * be cleared.
             */
            val maxCacheSize = 6

            /**
             * Adds the given TIM object to the cache. If the cache size exceeds the
             * [max cache size][maxCacheSize], then the cache is cleared.
             * @see java.util.HashSet.add
             */
            override fun add(element: TeamInMatch): Boolean {
                if (this.size >= maxCacheSize) this.clear()
                return super.add(element)
            }

            /**
             * Adds the given TIM objects to the cache. If the cache size exceeds the
             * [max cache size][maxCacheSize], then the cache is cleared.
             * @see java.util.HashSet.addAll
             */
            override fun addAll(elements: Collection<TeamInMatch>): Boolean {
                if (this.size + elements.size > maxCacheSize) this.clear()
                return super.addAll(elements)
            }
        }
        var teamList: List<String> = listOf()
        var starredMatches: HashSet<String> = HashSet()
        val refreshManager = RefreshManager()
        val leaderboardCache: MutableMap<String, Leaderboard> = mutableMapOf()
        var notesCache: MutableMap<String, String> = mutableMapOf()
        var mapMode = 1
        var mapRotation = -90F

        fun updateNotesCache() {
            getAllNotes { notesList ->
                val newMap = mutableMapOf<String, String>()
                notesList.forEach {
                    newMap[it.team_number] = it.notes;
                }
                notesCache = newMap.toMutableMap()
                Log.d("notes", "updated notes cache")
            }
        }
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

    override fun onResume() {
        super.onResume()
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
            != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
            != PackageManager.PERMISSION_GRANTED
        ) {
            try {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                    ),
                    100
                )
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        // Creates the files for user datapoints and starred matches
        UserDatapoints.read(this)
        StarredMatches.read()

        // Pull the set of starred matches from the downloads file viewer_starred_matches.
        var jsonStarred = contents.get("starredMatches")?.asJsonArray

        if (jsonStarred != null) {
            for (starred in jsonStarred) {
                starredMatches.add(starred.asString)
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

        (Constants.FIELDS_TO_BE_DISPLAYED_TEAM_DETAILS + Constants.FIELDS_TO_BE_DISPLAYED_LFM).forEach {
            if (it !in Constants.CATEGORY_NAMES) {
                createLeaderboard(it)
            }
        }

        refreshManager.addRefreshListener {
            Log.d("data-refresh", "Updated: ranking")
            updateNavFooter()
        }

        if (!Constants.USE_TEST_DATA){
            updateNotesCache()
        }

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        val matchScheduleFragment = MatchScheduleFragment()
        val ourScheduleFragment = OurScheduleFragment()
        val starredMatchesFragment = StarredMatchesFragment()
        val rankingFragment = RankingFragment()
//        val livePicklistFragment = LivePicklistFragment()
        val firstPickabilityFragment = PickabilityFragment(PickabilityMode.FIRST)
        val secondPickabilityFragment = PickabilityFragment(PickabilityMode.SECOND)
        val teamListFragment = TeamListFragment()
        val preferencesFragment = PreferencesFragment()

        updateNavFooter()

        //default screen when the viewer starts (after pulling data)
        supportFragmentManager.beginTransaction()
            .addToBackStack(null)
            .replace(R.id.nav_host_fragment, matchScheduleFragment, "matchSchedule")
            .commit()

        container.addDrawerListener(NavDrawerListener(navView, supportFragmentManager))



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

                R.id.nav_menu_starred_matches -> {
                    val ft = supportFragmentManager.beginTransaction()
                    if (supportFragmentManager.fragments.last().tag != "starredMatches")
                        ft.addToBackStack(null)
                    ft.replace(R.id.nav_host_fragment, starredMatchesFragment, "starredMatches")
                        .commit()
                }

                R.id.nav_menu_rankings -> {
                    supportFragmentManager.beginTransaction()
                        .addToBackStack(null)
                        .replace(R.id.nav_host_fragment, rankingFragment, "rankings")
                        .commit()
                }

//                R.id.nav_menu_live_picklist -> {
//                    supportFragmentManager.beginTransaction()
//                        .addToBackStack(null)
//                        .replace(R.id.nav_host_fragment, livePicklistFragment, "livePicklist")
//                        .commit()
//                }

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

                R.id.nav_preferences -> {
                    val ft = supportFragmentManager.beginTransaction()
                    if (supportFragmentManager.fragments.last().tag != "preferences") ft.addToBackStack(
                        null
                    )
                    ft.replace(R.id.nav_host_fragment, preferencesFragment, "preferences")
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

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.toolbar, menu)
        val fieldMapItem: MenuItem = menu.findItem(R.id.field_map_button)
        val pitMapItem: MenuItem = menu.findItem(R.id.pit_map_button)
        val fieldButton = fieldMapItem.actionView
        val pitButton = pitMapItem.actionView

        pitButton.setOnClickListener {
            val popupView = View.inflate(this, R.layout.pit_map_popup, null)
            val width = LinearLayout.LayoutParams.MATCH_PARENT
            val height = LinearLayout.LayoutParams.MATCH_PARENT
            val popupWindow = PopupWindow(popupView, width, height, false)

            var mapFile = File(
                "/storage/emulated/0/${Environment.DIRECTORY_DOWNLOADS}/",
                "pit_map"
            )

            if (mapFile!!.exists()) {
                popupView.pit_map.setImageURI(mapFile.toUri())
            }

            mapRotation =
                this.getSharedPreferences("VIEWER", 0).getFloat("mapRotation", mapRotation)

            popupView.pit_map.rotation = mapRotation
            popupWindow.showAtLocation(it, Gravity.CENTER, 0, 0)

            popupView.rotate_view.setOnClickListener {
                popupView.pit_map.rotation += 90F
                mapRotation = popupView.pit_map.rotation
                this.getSharedPreferences("VIEWER", 0)?.edit()
                    ?.putFloat("mapRotation", mapRotation)?.apply()
            }

            popupView.close_button.setOnClickListener {
                popupWindow.dismiss()
            }
        }

        fieldButton.setOnClickListener {
            val popupView = View.inflate(this, R.layout.field_map_popup, null)
            val width = LinearLayout.LayoutParams.MATCH_PARENT
            val height = LinearLayout.LayoutParams.MATCH_PARENT
            val popupWindow = PopupWindow(popupView, width, height, false)
            popupWindow.showAtLocation(it, Gravity.CENTER, 0, 0)
            when (mapMode) {
                0 -> {
                    popupView.red_chip.isChecked = true
                    popupView.none_chip.isChecked = false
                    popupView.blue_chip.isChecked = false
                    popupView.field_map.setImageResource(R.drawable.field_map_red)
                }
                1 -> {
                    popupView.red_chip.isChecked = false
                    popupView.none_chip.isChecked = true
                    popupView.blue_chip.isChecked = false
                    popupView.field_map.setImageResource(R.drawable.field_map)
                }
                2 -> {
                    popupView.red_chip.isChecked = false
                    popupView.none_chip.isChecked = false
                    popupView.blue_chip.isChecked = true
                    popupView.field_map.setImageResource(R.drawable.field_map_blue)
                }
            }
            popupView.red_chip.setOnClickListener {
                popupView.red_chip.isChecked = true
            }
            popupView.blue_chip.setOnClickListener {
                popupView.blue_chip.isChecked = true
            }
            popupView.none_chip.setOnClickListener {
                popupView.none_chip.isChecked = true
            }
            popupView.chip_group.setOnCheckedChangeListener { _, checkedId ->
                when (checkedId) {
                    popupView.red_chip.id -> {
                        popupView.field_map.setImageResource(R.drawable.field_map_red)
                        mapMode = 0
                    }
                    popupView.none_chip.id -> {
                        popupView.field_map.setImageResource(R.drawable.field_map)
                        mapMode = 1
                    }
                    popupView.blue_chip.id -> {
                        popupView.field_map.setImageResource(R.drawable.field_map_blue)
                        mapMode = 2
                    }
                }
                return@setOnCheckedChangeListener
            }
            popupView.close_button.setOnClickListener {
                popupWindow.dismiss()
            }
        }
        return super.onCreateOptionsMenu(menu)
    }

    fun updateNavFooter() {
        val footer = findViewById<TextView>(R.id.nav_footer)
        if (Constants.USE_TEST_DATA) {
            footer.text = getString(R.string.test_data)

        } else {
            footer.text = getString(R.string.last_updated, super.getTimeText())
        }
    }

    object UserDatapoints {

        var contents: JsonObject? = null
        var gson = Gson()

        private val file =
            File("/storage/emulated/0/${Environment.DIRECTORY_DOWNLOADS}/viewer_user_data_prefs.json")

        fun read(context: Context) {
            if (!fileExists()) {
                copyDefaults(context)
            }
            try {
                contents = JsonParser.parseReader(FileReader(file)).asJsonObject
            } catch (e: Exception) {
                Log.e("UserDatapoints.read", "Failed to read user datapoints file")
            }
        }

        fun write() {
            var writer = FileWriter(file, false)
            gson.toJson(contents as JsonElement, writer)

            writer.close()
        }

        fun fileExists(): Boolean = file.exists()

        fun copyDefaults(context: Context) {
            val inputStream: InputStream = context.resources.openRawResource(R.raw.default_prefs)

            try {
                val outputStream: OutputStream = FileOutputStream(file)

                val buffer = ByteArray(1024)
                var len: Int? = null
                while (inputStream.read(buffer, 0, buffer.size).also({ len = it }) != -1) {
                    outputStream.write(buffer, 0, len!!)
                }
                inputStream.close()
                outputStream.close()

            } catch (e: Exception) {
                Log.e("copyDefaults", "Failed to copy default preferences to file, $e")
            }

        }
    }

    // Writes file to store the starred matches on the viewer
    object StarredMatches {

        var contents = JsonObject()
        var gson = Gson()

        // Creates a list that stores all of the match numbers that team 1678 is in
        val citrusMatches = matchCache.filter {
            return@filter it.value.blueTeams.contains("1678") or it.value.redTeams.contains("1678")
        }.map { return@map it.value.matchNumber }

        private val file =
            File("/storage/emulated/0/${Environment.DIRECTORY_DOWNLOADS}/viewer_starred_matches.json")

        fun read() {
            if (!fileExists()) {
                write()
            }
            try {
                contents = JsonParser.parseReader(FileReader(file)).asJsonObject
            } catch (e: Exception) {
                Log.e("StarredMatches.read", "Failed to read starred matches file")
            }
        }

        fun write() {
            var writer = FileWriter(file, false)
            gson.toJson(contents as JsonElement, writer)

            writer.close()
        }

        fun fileExists(): Boolean = file.exists()

        // Updates the file with the currently starred matches based on the companion object starredMatches
        fun input() {
            val starredJsonArray = JsonArray()
            for (starred in starredMatches) {
                starredJsonArray.add(starred)
            }

            contents.remove("starredMatches")
            contents.add("starredMatches", starredJsonArray)
            write()
        }

    }

}

class NavDrawerListener(
    private val navView: NavigationView,
    private val fragManager: FragmentManager
) : DrawerLayout.DrawerListener {
    override fun onDrawerSlide(drawerView: View, slideOffset: Float) {}
    override fun onDrawerOpened(drawerView: View) {}
    override fun onDrawerClosed(drawerView: View) {}
    override fun onDrawerStateChanged(newState: Int) {
        if (newState == ViewDragHelper.STATE_SETTLING) {
            when (fragManager.fragments.last().tag) {
                "matchSchedule" -> navView.setCheckedItem(R.id.nav_menu_match_schedule)
                "ourSchedule" -> navView.setCheckedItem(R.id.nav_menu_our_match_schedule)
                "starredMatches" -> navView.setCheckedItem(R.id.nav_menu_starred_matches)
                "rankings" -> navView.setCheckedItem(R.id.nav_menu_rankings)
//                "livePicklist" -> navView.setCheckedItem(R.id.nav_menu_live_picklist)
                "pickabilityFirst" -> navView.setCheckedItem(R.id.nav_menu_pickability_first)
                "pickabilitySecond" -> navView.setCheckedItem(R.id.nav_menu_pickability_second)
                "teamList" -> navView.setCheckedItem(R.id.nav_menu_team_list)
                "preferences" -> navView.setCheckedItem(R.id.nav_preferences)
            }
        }
    }
}
