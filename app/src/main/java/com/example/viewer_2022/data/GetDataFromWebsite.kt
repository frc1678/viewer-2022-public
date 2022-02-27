package com.example.viewer_2022.data

import android.os.AsyncTask
import android.util.Log
import com.example.viewer_2022.MainViewerActivity
import com.example.viewer_2022.StartupActivity
import java.net.URL
import com.example.viewer_2022.StartupActivity.Companion.databaseReference
import com.example.viewer_2022.constants.Constants
import com.example.viewer_2022.data.*
import com.example.viewer_2022.getRankingList
import com.example.viewer_2022.lastUpdated
import com.google.gson.Gson
import java.io.*
import java.net.HttpURLConnection
import java.util.*

class GetDataFromWebsite(
    val onCompleted: () -> Unit = {},
    val onError: (error: String) -> Unit = {}
) :
    AsyncTask<String, String, String>() {

    override fun doInBackground(vararg p0: String?): String {
        try {

            val rawMatchSchedule: MutableMap<String, Website.WebsiteMatch> = Gson().fromJson(
                sendRequest("https://cardinal.citruscircuits.org/cardinal/api/match-schedule/2022week0/?format=json"),
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
                sendRequest("https://cardinal.citruscircuits.org/cardinal/api/teams-list/2022week0/?format=json"),
                WebsiteTeams
            )

            //Sets the name of the collections on the website
            var listOfCollectionNames: List<String> =
                listOf(
                    "raw_obj_pit",
                    "tba_tim",
                    "obj_tim",
                    "obj_team",
                    "subj_team",
                    "predicted_aim",
                    "predicted_team",
                    "tba_team",
                    "pickability",
                    "picklist"
                )

            //For each of the collections (make sure to change this number if the number of collections change),
            //pull the data from the website and then add it to the databaseReference variable
            for (x in 0..9) {
                val result =
                    sendRequest("https://cardinal.citruscircuits.org/cardinal/api/collection/${listOfCollectionNames[x]}/")
                when (x) {
                    0 -> databaseReference?.raw_obj_pit = Gson().fromJson(
                        result.toString(),
                        Array<DatabaseReference.ObjectivePit>::class.java
                    ).toMutableList()
                    1 -> databaseReference?.tba_tim = Gson().fromJson(
                        result.toString(),
                        Array<DatabaseReference.CalculatedTBATeamInMatch>::class.java
                    ).toMutableList()
                    2 -> databaseReference?.obj_tim = Gson().fromJson(
                        result.toString(),
                        Array<DatabaseReference.CalculatedObjectiveTeamInMatch>::class.java
                    ).toMutableList()
                    3 -> databaseReference?.obj_team = Gson().fromJson(
                        result.toString(),
                        Array<DatabaseReference.CalculatedObjectiveTeam>::class.java
                    ).toMutableList()
                    4 -> databaseReference?.subj_team = Gson().fromJson(
                        result.toString(),
                        Array<DatabaseReference.CalculatedSubjectiveTeam>::class.java
                    ).toMutableList()
                    5 -> databaseReference?.predicted_aim = Gson().fromJson(
                        result.toString(),
                        Array<DatabaseReference.CalculatedPredictedAllianceInMatch>::class.java
                    ).toMutableList()
                    6 -> databaseReference?.predicted_team = Gson().fromJson(
                        result.toString(),
                        Array<DatabaseReference.CalculatedPredictedTeam>::class.java
                    ).toMutableList()
                    7 -> databaseReference?.tba_team = Gson().fromJson(
                        result.toString(),
                        Array<DatabaseReference.CalculatedTBATeam>::class.java
                    ).toMutableList()
                    8 -> databaseReference?.pickability = Gson().fromJson(
                        result.toString(),
                        Array<DatabaseReference.CalculatedPickAbilityTeam>::class.java
                    ).toMutableList()
                    9 -> databaseReference?.picklist = Gson().fromJson(
                        result.toString(),
                        Array<DatabaseReference.PicklistTeam>::class.java
                    ).toMutableList()
                }
            }

            lastUpdated = Calendar.getInstance().time

            return ("finished")
        } catch (e: Throwable) {
            onError(e.toString())
            return ("error")
        }
    }

    override fun onPostExecute(result: String) {
        MainViewerActivity.leaderboardCache.clear()
        Constants.FIELDS_TO_BE_DISPLAYED_TEAM_DETAILS.forEach {
            if (it !in Constants.CATEGORY_NAMES) {
                getRankingList(it, false)
            }
        }
        onCompleted()
    }
}

private fun sendRequest(url: String): String {
    val result = StringBuilder()
    val requestUrl =
        URL(url)

    val urlConnection = requestUrl.openConnection() as HttpURLConnection
    urlConnection.setRequestProperty("Authorization", "Token ${Constants.CARDINAL_KEY}")

    try {
        val `in`: InputStream = BufferedInputStream(urlConnection.inputStream)

        val reader = BufferedReader(InputStreamReader(`in`))
        val line = reader.readText()
        result.append(line)
    } catch (e: Exception) {
        e.printStackTrace();
    } finally {
        urlConnection.disconnect();
    }
    return result.toString()
}

class PostRequestTask(val endpoint: String, val data: String) : AsyncTask<Unit, String, String>() {
    override fun doInBackground(vararg params: Unit?): String {
        val result = StringBuilder()

        val requestUrl = URL("https://cardinal.citruscircuits.org/cardinal/api/$endpoint")

        val urlConnection = requestUrl.openConnection() as HttpURLConnection
        urlConnection.setRequestProperty("Authorization", "Token ${Constants.CARDINAL_KEY}")
        urlConnection.setRequestProperty("Content-Type", "application/json");
        urlConnection.requestMethod = "POST";
        urlConnection.doOutput = true
        urlConnection.doInput = true
        urlConnection.setChunkedStreamingMode(0)

        try {
            val os = urlConnection.outputStream
            val bodyStream = BufferedWriter(OutputStreamWriter(os, "UTF-8"))
            bodyStream.write(data)
            bodyStream.flush()
            os.close()
            val status = urlConnection.responseCode;
            if (status != HttpURLConnection.HTTP_OK)  {
                val inputStream = urlConnection.errorStream;
                val reader = BufferedReader(InputStreamReader(inputStream))
                val line = reader.readText()
                Log.d("postRequest", "Line: $line")
                result.append(line)
            }
            else  {
                val inputStream = urlConnection.inputStream;
                val reader = BufferedReader(InputStreamReader(inputStream))
                val line = reader.readText()
                Log.d("postRequest", "Line: $line")
                result.append(line)
            }


        } catch (e: Exception) {
            e.printStackTrace();
        } finally {
            urlConnection.disconnect();
        }
        Log.d("postRequest", "Status code: ${urlConnection.responseCode}")
        Log.d("postRequest", "Status message: ${urlConnection.responseMessage}")
        Log.d("postRequest", "Response body: $result")
        return result.toString()
    }

}
