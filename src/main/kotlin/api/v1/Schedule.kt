package api.v1

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty

/**
 * Representation of days over which to receive articles. Weeks are Sunday-Saturday
 *
 * @property start The starting day of the week
 * @property end The ending day of the week
 */
data class Days @JsonCreator constructor(
        @JsonProperty("start")
        val start: String,

        @JsonProperty("end")
        val end: String
)

/**
 * Representation of hours over which to receive articles, in 24-hr format
 *
 * @property start The starting hour of the day
 * @property end The ending hour of the day
 */
data class Hours @JsonCreator constructor(
        @JsonProperty("start")
        val start: Int,

        @JsonProperty("end")
        val end: Int
)

/**
 * Representation of a schedule
 *
 * @property days The days over which to receive articles
 * @property hours The hours over which to receive articles
 * @property sources The sources from which to receive articles
 */
data class Schedule @JsonCreator constructor(
        @JsonProperty("days")
        val days: Days,

        @JsonProperty("hours")
        val hours: Hours,

        @JsonProperty("sources")
        val sources: List<String>
)

data class ScheduleResponse(val total: Int, val schedules: List<Schedule>)