/*
* Constants.kt
* viewer
*
* Created on 1/26/2020
* Copyright 2020 Citrus Circuits. All rights reserved.
*/

package com.example.viewer_2022.constants

//Class that contains a collection of Constant values, or final values that never change
class Constants {
    companion object {
        //Game specfific data.
        const val EVENT_KEY = "2022hop"
        const val MONGO_ATLAS = "mongodb-atlas"
        const val MY_TEAM_NUMBER = "1678"
        const val USE_TEST_DATA = true
        const val CARDINAL_KEY = ""
        const val REFRESH_INTERVAL = 120

        //Util.
        const val NULL_PREDICTED_SCORE_CHARACTER = "-"
        const val NULL_CHARACTER = "?"
        const val EMPTY_CHARACTER = ""
        const val PREDICTED_RANKING_POINT_QUALIFICATION = 0.65
        const val VERSION_NUM = "5.0.0"

        val FIELDS_TO_BE_DISPLAYED: List<String> = listOf(
            "processed",
            "raw"
        )

        val FIELDS_TO_BE_DISPLAYED_MATCH_DETAILS_PLAYED: List<String> = listOf(
            "Auto",
            "auto_line",
            "start_position",
            "auto_low_balls",
            "auto_high_balls",
            "Tele",
            "tele_low_balls",
            "tele_high_balls",
            "played_defense",
            "field_awareness_score",
            "quickness_score",
            "Endgame",
            "climb_level",
            "climb_attempts",
            "Other",
            "intakes",
            "incap"
        )

        val FIELDS_TO_BE_DISPLAYED_TEAM_DETAILS: List<String> = listOf(
            "See Matches",
            "Notes",
            "matches_played",
            "current_rps",
            "current_rank",
            "current_avg_rps",
            "predicted_rps",
            "predicted_rank",
            "first_pickability",
            "second_pickability",
            "Auto",
            "auto_line_successes",
            "mode_start_position",
            "position_zero_starts",
            "position_one_starts",
            "position_two_starts",
            "position_three_starts",
            "position_four_starts",
            "auto_avg_low_balls",
            "auto_avg_high_balls",
            "auto_avg_total_balls",
            "auto_max_low_balls",
            "auto_max_high_balls",
            "auto_sd_low_balls",
            "auto_sd_high_balls",
            "Tele",
            "tele_avg_low_balls",
            "tele_avg_high_balls",
            "tele_avg_total_balls",
            "tele_max_low_balls",
            "tele_max_high_balls",
            "tele_sd_low_balls",
            "tele_sd_high_balls",
            "avg_intakes",
            "avg_incap_time",
            "max_incap",
            "matches_incap",
            "driver_ability",
            "driver_field_awareness",
            "driver_quickness",
            "matches_played_defense",
            "Endgame",
            "climb_percent_success",
            "climb_all_attempts",
            "low_rung_successes",
            "mid_rung_successes",
            "high_rung_successes",
            "traversal_rung_successes",
            "mode_climb_level",
            "max_climb_level",
            "avg_climb_points",
            "Pit Data",
            "drivetrain",
            "drivetrain_motors",
            "drivetrain_motor_type",
            "can_climb",
            "has_ground_intake",
            "can_intake_terminal",
            "has_vision",
            "can_under_low_rung",
            "can_cheesecake"
        )

        val FIELDS_TO_BE_DISPLAYED_LFM: List<String> = listOf(
            "See Matches",
            "L4M Auto",
            "lfm_mode_start_position",
            "lfm_auto_avg_low_balls",
            "lfm_auto_avg_high_balls",
            "lfm_auto_max_low_balls",
            "lfm_auto_max_high_balls",
            "L4M Tele",
            "lfm_tele_avg_low_balls",
            "lfm_tele_avg_high_balls",
            "lfm_tele_max_low_balls",
            "lfm_tele_max_high_balls",
            "lfm_avg_incap_time",
            "lfm_max_incap",
            "lfm_matches_incap",
            "L4M Endgame",
            "lfm_climb_percent_success",
            "lfm_climb_all_attempts",
            "lfm_low_rung_successes",
            "lfm_mid_rung_successes",
            "lfm_high_rung_successes",
            "lfm_traversal_rung_successes",
            "lfm_max_climb_level",
        )

        val FIELDS_TO_BE_DISPLAYED_RANKING: List<String> = listOf(
            "current_rank",
            "current_avg_rps",
            "current_rps",
            "predicted_rps",
            "predicted_rank"
        )

        val FIELDS_TO_BE_DISPLAYED_MATCH_DETAILS_HEADER_NOT_PLAYED: List<String> = listOf(
            "predicted_score",
            "predicted_rp1",
            "predicted_rp2",
            "win_chance"
        )

        val FIELDS_TO_BE_DISPLAYED_MATCH_DETAILS_HEADER_PLAYED: List<String> = listOf(
            "actual_score",
            "actual_rp1",
            "actual_rp2",
            "win_chance"
        )

        val GRAPHABLE: List<String> = listOf(
            "auto_avg_low_balls",
            "auto_avg_high_balls",
            "auto_avg_total_balls",
            "tele_avg_total_balls",
            "avg_intakes",
            "tele_avg_low_balls",
            "tele_avg_high_balls",
            "avg_incap_time",
            "matches_incap",
            "matches_incap",
            "mode_start_position",
            "position_zero_starts",
            "position_one_starts",
            "position_two_starts",
            "position_three_starts",
            "position_four_starts",
            "avg_climb_points",
            "climb_all_attempts",
            "matches_played_defense"
        )

        val GRAPHABLE_BOOL: List<String> = listOf(
            "auto_line_successes",
            "climb_percent_success",
            "low_rung_successes",
            "mid_rung_successes",
            "high_rung_successes",
            "traversal_rung_successes",
            "matches_played_defense"
        )

        val GRAPHABLE_CLIMB_TIMES: List<String> = listOf(
            "climb_all_success_avg_time",
            "mode_climb_level",
            "avg_climb_points",
            "climb_all_attempts"
        )

        val DRIVER_DATA: List<String> = listOf(
            "driver_quickness",
            "driver_field_awareness",
            "driver_ability"
        )

        val PERCENT_DATA: List<String> = listOf(
            "climb_percent_success"
        )

        val PIT_DATA: List<String> = listOf(
            "drivetrain",
            "drivetrain_motors",
            "drivetrain_motor_type",
            "can_climb",
            "has_ground_intake",
            "can_intake_terminal",
            "has_vision",
            "can_under_low_rung",
            "can_cheesecake"
        )

        //List of rankable fields
        //The boolean is if it should be sorted descending
        //Not if it is rankable or not
        //True means descending and false means ascending
        //All items in the list are rankable no matter what
        val RANKABLE_FIELDS = mapOf<String, Boolean>(
            "current_rps" to true,
            "current_rank" to false,
            "predicted_rank" to false,
            "current_avg_rps" to true,
            "predicted_rps" to true,
            "first_pickability" to true,
            "second_pickability" to true,
            "auto_avg_low_balls" to true,
            "auto_avg_high_balls" to true,
            "auto_avg_total_balls" to true,
            "auto_line_successes" to true, //?????
            "auto_max_low_balls" to true,
            "auto_max_high_balls" to true,
            "auto_sd_low_balls" to true,
            "auto_sd_high_balls" to true,
            "tele_avg_low_balls" to true,
            "tele_avg_high_balls" to true,
            "tele_avg_total_balls" to true,
            "driver_ability" to true,
            "driver_field_awareness" to true,
            "driver_quickness" to true,
            "tele_max_low_balls" to true,
            "tele_max_high_balls" to true,
            "tele_sd_low_balls" to false,
            "tele_sd_high_balls" to false,
            "max_incap" to false,
            "avg_incap_time" to false,
            "matches_incap" to false,
            "climb_percent_success" to true,
            "climb_all_attempts" to true,
            "low_rung_successes" to true,
            "mid_rung_successes" to true,
            "high_rung_successes" to true,
            "traversal_rung_successes" to true,
            "lfm_auto_avg_low_balls" to true,
            "lfm_auto_avg_low_balls" to true,
            "lfm_auto_avg_high_balls" to true,
            "lfm_auto_max_low_balls" to true,
            "lfm_auto_max_high_balls" to true,
            "lfm_tele_avg_low_balls" to true,
            "lfm_tele_avg_low_balls" to true,
            "lfm_tele_avg_high_balls" to true,
            "lfm_tele_max_low_balls" to true,
            "lfm_tele_max_high_balls" to true,
            "lfm_max_incap" to false,
            "lfm_avg_incap_time" to false,
            "lfm_matches_incap" to false,
            "lfm_climb_percent_success" to true,
            "lfm_climb_all_attempts" to true,
            "lfm_low_rung_successes" to true,
            "lfm_mid_rung_successes" to true,
            "lfm_high_rung_successes" to true,
            "lfm_traversal_rung_successes" to true,
            "avg_intakes" to true,
            "drivetrain" to false,
            "drivetrain_motors" to false,
            "drivetrain_motor_type" to false,
            "can_climb" to false,
            "has_ground_intake" to false,
            "can_intake_terminal" to false,
            "has_vision" to false,
            "can_under_low_rung" to false,
            "can_cheesecake" to false,
            "avg_climb_points" to true,
            "matches_played_defense" to true,
        )

        val RANK_BY_PIT = mapOf<String, Int>(
            "swerve" to 1,
            "tank" to 2,
            "mecanum" to 3,
            "minicim" to 1,
            "cim" to 2,
            "neo" to 3,
            "falcon" to 4,
            "other" to 5,
            "true" to 1,
            "false" to 2
        )

        val CATEGORY_NAMES = listOf(
            "Auto",
            "Tele",
            "Endgame",
            "Fouls",
            "Other",
            "Pit Data",
            "L4M Auto",
            "L4M Tele",
            "L4M Endgame"
        )


        //String literal translations.
        const val TEAM_NUMBER = "teamNumber"
        const val MATCH_NUMBER = "matchNumber"
        const val BLUE = "blue"
        const val RED = "red"

    }

    enum class PROCESSED_OBJECT(val value: String) {
        CALCULATED_OBJECTIVE_TEAM_IN_MATCH("obj_tim"),
        CALCULATED_OBJECTIVE_TEAM("obj_team"),
        CALCULATED_SUBJECTIVE_TEAM("subj_team"),
        CALCULATED_PREDICTED_ALLIANCE_IN_MATCH("predicted_aim"),
        CALCULATED_PREDICTED_TEAM("predicted_team"),
        CALCULATED_TBA_TEAM("tba_team"),
        CALCULATED_PICKABILITY("pickability"),
        CALCULATED_TBA_TEAM_IN_MATCH("tba_tim"),
        CALCULATED_SUBJECTIVE_TEAM_IN_MATCH("subj_tim")
    }

    enum class ScheduleType {
        ALL_MATCHES,
        OUR_MATCHES,
        STARRED_MATCHES
    }


}
