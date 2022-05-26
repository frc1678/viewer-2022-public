package com.example.viewer_2022

import com.example.viewer_2022.constants.Constants

/**
 * @param path The collection of data to get the value from
 * @param teamsList The list of teams to be converted to a map of current rankings
 */
fun convertToFilteredTeamsList(path: String, teamsList: List<String>): List<String> {
    val unsortedMap = HashMap<String, Double>()
    for (team in teamsList) {
        unsortedMap[team] =
            if (getTeamObjectByKey(path, team, "current_rank") != Constants.NULL_CHARACTER)
                getTeamObjectByKey(path, team, "current_rank").toDouble()
            else 1000.0
    }
    return unsortedMap.toList().sortedBy { (_, value) -> value }.toMap().keys.toList()
}