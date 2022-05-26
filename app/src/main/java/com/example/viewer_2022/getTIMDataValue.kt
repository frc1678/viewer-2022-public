package com.example.viewer_2022

/**
 * Gets a value from the TIM data.
 * @param teamNumber The team number for the TIM Value
 * @param field The field to get the value for
 * @param path The collection of data to get the value from
 */
fun getTIMDataValue(teamNumber: String, field: String, path: String): Map<String, String> {
    val matchNumList = getMatchSchedule(listOf(teamNumber)).keys
    val result: MutableMap<String, String> = mutableMapOf()
    for (matchNumber in matchNumList) {
        for (`object` in getDirectField(StartupActivity.databaseReference!!, path)
                as List<*>) {
            if (getDirectField(`object`!!, "match_number").toString() == matchNumber &&
                getDirectField(`object`, "team_number").toString() == teamNumber
            ) {
                result[matchNumber] = getDirectField(`object`, field).toString()
            }
        }
    }
    return result
}