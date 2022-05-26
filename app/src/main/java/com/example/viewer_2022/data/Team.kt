/*
* Team.kt
* viewer
*
* Created on 3/7/2null19
* Copyright 2null2null Citrus Circuits. All rights reserved.
*/

package com.example.viewer_2022.data

// Data storage class for each individual team object.
data class Team(var team_number: Int?) {
    var team_name: String? = null
    var auto_line_successes: Int? = null
    var first_pickability: Float? = null
    var second_pickability: Float? = null
    var predicted_rps: Double? = null
    var predicted_rank: Int? = null
    var current_rps: Int? = null
    var current_rank: Int? = null
    var current_avg_rps: Float? = null
    var driver_quickness: Float? = null
    var driver_field_awareness: Float? = null
    var driver_ability: Float? = null
    var auto_avg_low_balls: Float? = null
    var auto_avg_high_balls: Float? = null
    var auto_avg_total_balls: Float? = null
    var tele_avg_low_balls: Float? = null
    var tele_avg_high_balls: Float? = null
    var tele_avg_total_balls: Float? = null
    var climb_all_attempts: Int? = null
    var avg_incap_time: Float? = null
    var climb_percent_success: Float? = null
    var match_number: Int? = null
    var auto_low_balls: Int? = null
    var auto_high_balls: Int? = null
    var tele_low_balls: Int? = null
    var tele_high_balls: Int? = null
    var incap: Int? = null
    var auto_max_low_balls: Int? = null
    var auto_max_high_balls: Int? = null
    var tele_max_low_balls: Int? = null
    var tele_max_high_balls: Int? = null
    var max_incap: Int? = null
    var auto_sd_low_balls: Float? = null
    var auto_sd_high_balls: Float? = null
    var mode_start_position: List<String>? = null
    var tele_sd_low_balls: Float? = null
    var tele_sd_high_balls: Float? = null
    var low_rung_successes: Int? = null
    var mid_rung_successes: Int? = null
    var high_rung_successes: Int? = null
    var traversal_rung_successes: Int? = null
    var matches_incap: Int? = null
    var mode_climb_level: List<String>? = null
    var max_climb_level: String? = null
    var max_opp_balls_scored: Int? = null
    var drivetrain: Int? = null
    var drivetrain_motors: Int? = null
    var drivetrain_motor_type: Int? = null
    var can_climb: Boolean? = null
    var can_ground_intake: Boolean? = null
    var can_intake_terminal: Boolean? = null
    var can_eject_terminaal: Boolean? = null
    var has_vision: Boolean? = null
    var can_cheesecake: Boolean? = null
    var can_under_low_rung: Boolean? = null
    var avg_climb_points: Float? = null
    var climb_attempts: Int? = null
    var matches_played_defense: Int? = null
}
