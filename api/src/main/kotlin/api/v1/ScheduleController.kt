package api.v1

import com.google.gson.*
import mu.KotlinLogging
import org.springframework.web.bind.annotation.*
import java.sql.*

@RestController
class ScheduleController {

    private val logger = KotlinLogging.logger {}

    private val GSON = Gson()

    private val US_PHONE_NUMBER_LENGTH = 10

    private val SCHEDULE_KEY = "schedule"
    private val DAYS_KEY = "days"
    private val HOURS_KEY = "hours"
    private val SOURCES_KEY = "sources"
    private val START_KEY = "start"
    private val END_KEY = "end"

    @GetMapping(value = "/api/v1/schedule/{phoneNumber}", produces = arrayOf("application/json"))
    fun get(@PathVariable("phoneNumber") phoneNumber: String): ScheduleResponse {
        validatePhoneNumber(phoneNumber)

        val schedules = queryDatabaseForSchedules(phoneNumber)

        return ScheduleResponse(schedules.size, schedules)
    }

    @PostMapping(value = "/api/v1/schedule/{phoneNumber}")
    fun post(@PathVariable("phoneNumber") phoneNumber: String, @RequestBody schedule: Schedule): ScheduleResponse {
        return ScheduleResponse(0, emptyList())
    }

    @PutMapping(value = "/api/v1/schedule/{phoneNumber}")
    fun put(@PathVariable("phoneNumber") phoneNumber: String, @RequestBody schedule: Schedule): ScheduleResponse {
        return ScheduleResponse(0, emptyList())
    }

    @DeleteMapping(value = "/api/v1/schedule/{phoneNumber}")
    fun delete(@PathVariable("phoneNumber") phoneNumber: String) {
    }

    private fun validatePhoneNumber(phoneNumber: String) {
        if (phoneNumber.length != US_PHONE_NUMBER_LENGTH) {
            val e = "Invalid phone number: [$phoneNumber]"
            throw IllegalArgumentException(e)
        }
    }

    private fun queryDatabaseForSchedules(phoneNumber: String): List<Schedule> {
        var connection: Connection? = null
        var statement: Statement? = null
        var result: ResultSet? = null

        try {
            Class.forName(JDBC_DRIVER)

            connection = DriverManager.getConnection(DB_URL, PG_USER, PG_PASS)
            logger.info { "Successfully connected to database" }

            statement = connection!!.createStatement()

            val query = "SELECT * FROM \"User\" WHERE number='$phoneNumber';"

            result = statement!!.executeQuery(query)

            return parseDatabaseResponseForSchedule(result)
        } catch (e: SQLException) {
            logger.error { "SQL error occured: ${e.printStackTrace()}" }
            throw SQLException()
        } catch (e: Exception) {
            logger.error { "Error occured: ${e.printStackTrace()}" }
            throw Exception()
        } finally {
            result?.close()
            statement?.close()
            connection?.close()
        }
    }

    private fun parseDatabaseResponseForSchedule(result: ResultSet?): List<Schedule> {
        val schedules: MutableList<Schedule> = mutableListOf()

        if (result != null) {
            while (result.next()) {
                val scheduleResult = GSON.fromJson(result.getString(SCHEDULE_KEY), JsonObject::class.java)

                scheduleResult ?: return emptyList()

                logger.info { "Found schedule: [$scheduleResult]" }

                schedules.add(createSchedule(scheduleResult))
            }
        }

        return schedules
    }

    private fun createSchedule(scheduleResult: JsonObject): Schedule {
        val dayStart = GSON.fromJson(scheduleResult.get(DAYS_KEY).asJsonObject.get(START_KEY), String::class.java)
        val dayEnd = GSON.fromJson(scheduleResult.get(DAYS_KEY).asJsonObject.get(END_KEY), String::class.java)

        val hourStart = GSON.fromJson(scheduleResult.get(HOURS_KEY).asJsonObject.get(START_KEY), Int::class.java)
        val hourEnd = GSON.fromJson(scheduleResult.get(HOURS_KEY).asJsonObject.get(END_KEY), Int::class.java)

        val sources = GSON.fromJson(scheduleResult.get(SOURCES_KEY), JsonArray::class.java).map { it -> GSON.fromJson(it, String::class.java) }

        return Schedule(Days(dayStart, dayEnd), Hours(hourStart, hourEnd), sources)
    }

}