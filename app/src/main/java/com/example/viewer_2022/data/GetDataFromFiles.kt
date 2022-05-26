package com.example.viewer_2022.data

import android.content.Context
import android.os.AsyncTask
import android.util.Log
import com.example.viewer_2022.MainViewerActivity
import com.example.viewer_2022.R
import com.example.viewer_2022.StartupActivity.Companion.databaseReference
import com.example.viewer_2022.data.*
import com.example.viewer_2022.lastUpdated
import com.google.gson.Gson
import java.io.*
import java.util.*


/**
 * Async task to get data from test data files
 */
class GetDataFromFiles(
    var context: Context,
    val onCompleted: () -> Unit = {},
    val onError: (error: String) -> Unit = {}
) :
    AsyncTask<String, String, String>() {

    override fun doInBackground(vararg p0: String?): String {
        try {
            //For each of the collections (make sure to change this number if the number of collections change),
            //pull the data from the website and then add it to the databaseReference variable
            for (x in 0..9) {
                when (x) {
                    0 -> databaseReference?.raw_obj_pit = Gson().fromJson(
                        readFile(context.getResources().openRawResource(R.raw.raw_obj_pit)),
                        Array<DatabaseReference.ObjectivePit>::class.java
                    ).toMutableList()
                    1 -> databaseReference?.tba_tim = Gson().fromJson(
                        readFile(context.getResources().openRawResource(R.raw.calc_tba_tim)),
                        Array<DatabaseReference.CalculatedTBATeamInMatch>::class.java
                    ).toMutableList()
                    2 -> databaseReference?.obj_tim = Gson().fromJson(
                        readFile(context.getResources().openRawResource(R.raw.calc_obj_tim)),
                        Array<DatabaseReference.CalculatedObjectiveTeamInMatch>::class.java
                    ).toMutableList()
                    3 -> databaseReference?.obj_team = Gson().fromJson(
                        readFile(context.getResources().openRawResource(R.raw.calc_obj_team)),
                        Array<DatabaseReference.CalculatedObjectiveTeam>::class.java
                    ).toMutableList()
                    4 -> databaseReference?.subj_team = Gson().fromJson(
                        readFile(context.getResources().openRawResource(R.raw.calc_subj_team)),
                        Array<DatabaseReference.CalculatedSubjectiveTeam>::class.java
                    ).toMutableList()
                    5 -> databaseReference?.predicted_aim = Gson().fromJson(
                        readFile(context.getResources().openRawResource(R.raw.calc_predicted_aim)),
                        Array<DatabaseReference.CalculatedPredictedAllianceInMatch>::class.java
                    ).toMutableList()
                    6 -> databaseReference?.predicted_team = Gson().fromJson(
                        readFile(context.getResources().openRawResource(R.raw.calc_predicted_team)),
                        Array<DatabaseReference.CalculatedPredictedTeam>::class.java
                    ).toMutableList()
                    7 -> databaseReference?.tba_team = Gson().fromJson(
                        readFile(context.getResources().openRawResource(R.raw.calc_tba_team)),
                        Array<DatabaseReference.CalculatedTBATeam>::class.java
                    ).toMutableList()
                    8 -> databaseReference?.pickability = Gson().fromJson(
                        readFile(context.getResources().openRawResource(R.raw.calc_pickability)),
                        Array<DatabaseReference.CalculatedPickAbilityTeam>::class.java
                    ).toMutableList()
                    9 -> databaseReference?.picklist = Gson().fromJson(
                        readFile(context.getResources().openRawResource(R.raw.picklist)),
                        Array<DatabaseReference.PicklistTeam>::class.java
                    ).toMutableList()
                }
                Log.e("databaseReference", "$databaseReference")
            }

            val rawMatchSchedule: MutableMap<String, Website.WebsiteMatch> = Gson().fromJson(
                readFile(context.getResources().openRawResource(R.raw.match_schedule)),
                WebsiteMatchSchedule
            )
            for (i in rawMatchSchedule) {
                val match = Match(i.key)
                for (j in i.value.teams) {
                    when (j.color) {
                        "red" -> {
                            match.redTeams.add(j.number.toString())
                        }
                        "blue" -> {
                            match.blueTeams.add(j.number.toString())
                        }
                    }
                }

                Log.e("parsedmap", match.toString())
                MainViewerActivity.matchCache[i.key] = match
            }
            MainViewerActivity.matchCache =
                MainViewerActivity.matchCache.toList().sortedBy { (k, v) -> v.matchNumber.toInt() }
                    .toMap().toMutableMap()

            MainViewerActivity.teamList = Gson().fromJson(
                readFile(context.getResources().openRawResource(R.raw.team_list)),
                WebsiteTeams
            )

            lastUpdated = Calendar.getInstance().time

            return ("finished")
        } catch (e: Throwable) {
            onError(e.toString())
            return ("error")
        }
    }

    override fun onPostExecute(result: String) {
        onCompleted()
    }
}

private fun readFile(file: InputStream): String {
    val `is`: InputStream = file
    val writer: Writer = StringWriter()
    val buffer = CharArray(1024)
    try {
        val reader: Reader = BufferedReader(InputStreamReader(`is`, "UTF-8"))
        var n: Int
        while (reader.read(buffer).also { n = it } != -1) {
            writer.write(buffer, 0, n)
        }
    } finally {
        `is`.close()
    }
    return writer.toString()
}