package com.example.viewer_2022

import com.example.viewer_2022.constants.Constants

fun convertToPredFilteredTeamsList(path: String, teamsList: List<String>): List<String> {
    val unsortedMap = HashMap<String, Double>()
    for (team in teamsList) {
        unsortedMap[team] = if (getTeamObjectByKey(path, team, "current_rank") != Constants.NULL_CHARACTER)
            getTeamObjectByKey(path, team, "predicted_rank").toDouble()
        else 1000.0
    }
    return unsortedMap.toList().sortedBy { (_, value) -> value}.toMap().keys.toList()
}