package api.v1

import com.fasterxml.jackson.annotation.JsonCreator
import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.JsonObject

data class Schedule @JsonCreator constructor(
        val days: JsonObject,
        val hours: JsonObject,
        val sources: JsonArray
)

data class ScheduleResponse(val total: Int, val schedules: List<Schedule>)