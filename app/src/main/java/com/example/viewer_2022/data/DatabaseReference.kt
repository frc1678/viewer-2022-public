/*
* DatabaseReference.kt
* viewer
*
* Created on 2/2/2020
* Copyright 2020 Citrus Circuits. All rights reserved.
*/

package com.example.viewer_2022.data

//Database reference class to make a database object from MongoDB.
class DatabaseReference {
    data class CompetitionObject(
        var raw_obj_pit: MutableList<ObjectivePit> = mutableListOf(),
        var obj_tim: MutableList<CalculatedObjectiveTeamInMatch> = mutableListOf(),
        var obj_team: MutableList<CalculatedObjectiveTeam> = mutableListOf(),
        var subj_team: MutableList<CalculatedSubjectiveTeam> = mutableListOf(),
        var predicted_aim: MutableList<CalculatedPredictedAllianceInMatch> = mutableListOf(),
        var predicted_team: MutableList<CalculatedPredictedTeam> = mutableListOf(),
        var tba_team: MutableList<CalculatedTBATeam> = mutableListOf(),
        var pickability: MutableList<CalculatedPickAbilityTeam> = mutableListOf(),
        var tba_tim: MutableList<CalculatedTBATeamInMatch> = mutableListOf(),
        var picklist: MutableList<PicklistTeam> = mutableListOf(),
        var subj_tim: MutableList<CalculatedSubjectiveTeamInMatch> = mutableListOf()
    )

    data class ObjectivePit(
        var team_number: Int,
        var drivetrain: Int, //value is an enum in schema
        var drivetrain_motors: Int,
        var drivetrain_motor_type: Int, //value is an enum in schema
        var has_ground_intake: Boolean,
        var has_vision: Boolean,
        var can_cheesecake: Boolean,
        var can_intake_terminal: Boolean,
        var can_under_low_rung: Boolean,
        var can_climb: Boolean
    )


    data class CalculatedPredictedAllianceInMatch(
        var match_number: Int,
        var alliance_color_is_red: Boolean,
        var has_actual_data: Boolean,
        var actual_score: Int,
        var actual_rp1: Float,
        var actual_rp2: Float,
        var won_match: Boolean,
        var has_final_scores: Boolean,
        var final_predicted_score: Float,
        var final_predicted_rp1: Float,
        var final_predicted_rp2: Float,
        var predicted_score: Float,
        var predicted_rp1: Float,
        var predicted_rp2: Float,
        var win_chance: Float
    )

    data class CalculatedTBATeam(
        var team_number: Int,
        var team_name: String,
        var auto_line_successes: Int
    )

    data class CalculatedTBATeamInMatch(
        var match_number: Int,
        var team_number: Int,
        var auto_line: Boolean
    )

    data class CalculatedPickAbilityTeam(
        var team_number: Int,
        var first_pickability: Float,
        var second_pickability: Float
    )

    data class CalculatedPredictedTeam(
        var team_number: Int,
        var current_rank: Int,
        var current_rps: Int,
        var current_avg_rps: Float,
        var predicted_rps: Float,
        var predicted_rank: Int
    )

    data class CalculatedSubjectiveTeam(
        var team_number: Int,
        var driver_field_awareness: Float,
        var driver_quickness: Float,
        var driver_ability: Float
    )

    data class CalculatedObjectiveTeam(
        var team_number: Int,
        var auto_avg_low_balls: Float,
        var tele_avg_low_balls: Float,
        var auto_avg_high_balls: Float,
        var auto_avg_total_balls: Float,
        var tele_avg_high_balls: Float,
        var tele_avg_total_balls: Float,
        var avg_incap_time: Float,
        var avg_intakes: Float,
        var lfm_auto_avg_low_balls: Float,
        var lfm_tele_avg_low_balls: Float,
        var lfm_auto_avg_high_balls: Float,
        var lfm_tele_avg_high_balls: Float,
        var lfm_avg_incap_time: Float,
        var auto_sd_low_balls: Float,
        var auto_sd_high_balls: Float,
        var tele_sd_low_balls: Float,
        var tele_sd_high_balls: Float,
        var climb_all_attempts: Int,
        var low_rung_successes: Int,
        var mid_rung_successes: Int,
        var high_rung_successes: Int,
        var traversal_rung_successes: Int,
        var position_zero_starts: Int,
        var position_one_starts: Int,
        var position_two_starts: Int,
        var position_three_starts: Int,
        var position_four_starts: Int,
        var matches_played: Int,
        var matches_incap: Int,
        var lfm_climb_all_attempts: Int,
        var lfm_low_rung_successes: Int,
        var lfm_mid_rung_successes: Int,
        var lfm_high_rung_successes: Int,
        var lfm_traversal_rung_successes: Int,
        var lfm_matches_incap: Int,
        var matches_played_defense: Int,
        var auto_max_low_balls: Int,
        var auto_max_high_balls: Int,
        var tele_max_low_balls: Int,
        var tele_max_high_balls: Int,
        var max_incap: Int,
        var max_climb_level: String,
        var lfm_auto_max_low_balls: Int,
        var lfm_auto_max_high_balls: Int,
        var lfm_tele_max_low_balls: Int,
        var lfm_tele_max_high_balls: Int,
        var lfm_max_incap: Int,
        var lfm_max_climb_level: String,
        var mode_climb_level: List<String>,
        var mode_start_position: List<String>,
        var lfm_mode_start_position: List<String>,
        var climb_percent_success: Float,
        var lfm_climb_percent_success: Float,
        var avg_climb_points: Float,
    )

    data class CalculatedObjectiveTeamInMatch(
        var confidence_rating: Int,
        var team_number: Int,
        var match_number: Int,
        var auto_low_balls: Int,
        var tele_low_balls: Int,
        var intakes: Int,
        var climb_attempts: Int,
        var auto_high_balls: Int,
        var tele_high_balls: Int,
        var auto_total_balls: Int,
        var tele_total_balls: Int,
        var incap: Int,
        var climb_level: String,
        var start_position: String,
    )

    data class PicklistTeam(
        var team_number: Int,
        var first_rank: Int,
        var second_rank: Int
    )

    data class CalculatedSubjectiveTeamInMatch(
        var team_number: Int,
        var match_number: Int,
        var quickness_score: Int,
        var field_awareness_score: Int,
        var played_defense: Boolean,
    )
}
