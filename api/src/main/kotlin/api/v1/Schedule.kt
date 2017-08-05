package api.v1

data class Days(val start: String, val end: String)

data class Hours(val start: Int, val end: Int)

data class Schedule(val days: Days, val hours: Hours, val sources: List<String>)

data class ScheduleResponse(val total: Int, val schedules: List<Schedule>)