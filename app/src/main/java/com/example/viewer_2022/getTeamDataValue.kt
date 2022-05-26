/*
* getTeamDataValue.kt
* viewer
*
* Created on 3/5/2020
* Copyright 2020 Citrus Circuits. All rights reserved.
*/

package com.example.viewer_2022

import com.example.viewer_2022.constants.Constants
import com.example.viewer_2022.constants.Translations
import java.lang.Exception

// Parses through every local database key to return the value of the given field.
fun getTeamDataValue(teamNumber: String, field: String): String {
    for (item in listOf(
        Constants.PROCESSED_OBJECT.CALCULATED_OBJECTIVE_TEAM.value,
        Constants.PROCESSED_OBJECT.CALCULATED_SUBJECTIVE_TEAM.value,
        Constants.PROCESSED_OBJECT.CALCULATED_PREDICTED_TEAM.value,
        Constants.PROCESSED_OBJECT.CALCULATED_TBA_TEAM.value,
        Constants.PROCESSED_OBJECT.CALCULATED_PICKABILITY.value,
        Constants.PROCESSED_OBJECT.CALCULATED_SUBJECTIVE_TEAM_IN_MATCH.value,
    )
    ) {
        try {
            if (getTeamObjectByKey(
                    item, teamNumber, field
                ) != Constants.NULL_CHARACTER
            ) {
                return if (field in Constants.PERCENT_DATA) {
                    (getTeamObjectByKey(
                        item, teamNumber, field
                    ).toFloat() * 100.0).toString()
                } else {
                    getTeamObjectByKey(
                        item, teamNumber, field
                    )
                }
            }
        } catch (e: Exception) {
            continue
        }
    }
    for (item in listOf(
        "raw_obj_pit"
//        "raw_subj_pit"
    )
    ) {
        try {
            if (getRawObjectByKey(
                    item, teamNumber, field
                ) != Constants.NULL_CHARACTER
            ) {
                when (field) {
                    "drivetrain" -> {
                        return Translations.DRIVETRAIN[getRawObjectByKey(
                            "raw_obj_pit", teamNumber, field
                        )] ?: Constants.NULL_CHARACTER
                    }
                    "drivetrain_motor_type" -> {
                        return Translations.DRIVETRAIN_MOTOR_TYPE[getRawObjectByKey(
                            "raw_obj_pit", teamNumber, field
                        )] ?: Constants.NULL_CHARACTER
                    }
                    else -> {
                        return getRawObjectByKey(
                            item, teamNumber, field
                        )
                    }
                }
            }
        } catch (e: Exception) {
            continue
        }
    }
    return Constants.NULL_CHARACTER
}