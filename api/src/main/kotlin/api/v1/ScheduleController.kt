package api.v1

import com.google.gson.*
import java.sql.*
import mu.KotlinLogging
import org.springframework.web.bind.annotation.*

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

    private val EXISTS_KEY = "exists"

    @GetMapping(value = "/api/v1/schedule/{phoneNumber}", produces = arrayOf("application/json"))
    fun get(@PathVariable("phoneNumber") phoneNumber: String): ScheduleResponse {
        validatePhoneNumber(phoneNumber)

        val schedules = queryDatabaseForSchedules(phoneNumber)

        return ScheduleResponse(schedules.size, schedules)
    }

    @PostMapping(value = "/api/v1/schedule/{phoneNumber}")
    fun post(@PathVariable("phoneNumber") phoneNumber: String, @RequestBody requestSchedule: Schedule): ScheduleResponse {
        validatePhoneNumber(phoneNumber)

        checkForErrors(requestSchedule)

        addScheduleToDatabase(phoneNumber, requestSchedule)

        val schedules = listOf(requestSchedule)

        return ScheduleResponse(schedules.size, schedules)
    }

    @PutMapping(value = "/api/v1/schedule/{phoneNumber}")
    fun put(@PathVariable("phoneNumber") phoneNumber: String, @RequestBody requestSchedule: Schedule): ScheduleResponse {
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

    private fun checkForErrors(schedule: Schedule) {
        if (schedule.days.end.integerRepresentation() < schedule.days.start.integerRepresentation()) {
            val e = "Start day must come before end day"
            throw IllegalArgumentException(e)
        }

        if (schedule.hours.end < schedule.hours.start) {
            val e = "Start hour must come before end hour"
            throw IllegalArgumentException(e)
        }
    }

    fun String.integerRepresentation() = when (this) {
        SUNDAY -> 0
        MONDAY -> 1
        TUESDAY -> 2
        WEDNESDAY -> 3
        THURSDAY -> 4
        FRIDAY -> 5
        SATURDAY -> 6
        else -> -1
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

            val query = "SELECT schedule FROM \"User\" WHERE number='$phoneNumber';"

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

    private fun addScheduleToDatabase(phoneNumber: String, schedule: Schedule) {
        var connection: Connection? = null
        var statement: Statement? = null

        try {
            Class.forName(JDBC_DRIVER)

            connection = DriverManager.getConnection(DB_URL, PG_USER, PG_PASS)
            logger.info { "Successfully connected to database" }

            statement = connection!!.createStatement()

            if (scheduleExists(connection, phoneNumber)) {
                logger.info { "Existing schedule found for phone number: [$phoneNumber]. Deleting..." }
                removeSchedule(connection, phoneNumber)
                logger.info { "Deleted schedule for phone number: [$phoneNumber]"}
            }

            val insert = "INSERT INTO \"User\" VALUES('$phoneNumber', '${GSON.toJson(schedule)}');"

            if (statement!!.execute(insert)) {
                logger.info { "Successfully inserted $schedule for $phoneNumber" }
            } else {
                logger.error { "Failed to insert $schedule for $phoneNumber" }
            }
        } catch (e: SQLException) {
            logger.error { "SQL error occured: ${e.printStackTrace()}" }
            throw SQLException()
        } catch (e: Exception) {
            logger.error { "Error occured: ${e.printStackTrace()}" }
            throw Exception()
        } finally {
            statement?.close()
            connection?.close()
        }
    }

    private fun scheduleExists(connection: Connection, phoneNumber: String): Boolean {
        var statement: Statement? = null
        var result: ResultSet? = null

        try {
            Class.forName(JDBC_DRIVER)

            statement = connection.createStatement()

            val query = "SELECT EXISTS(SELECT FROM \"User\" WHERE number='$phoneNumber');"

            result = statement!!.executeQuery(query)

            if (result != null) {
                while (result.next()) {
                    return result.getString(EXISTS_KEY).equals("t")
                }
            }
        } catch (e: SQLException) {
            logger.error { "SQL error occured: ${e.printStackTrace()}" }
            throw SQLException()
        } catch (e: Exception) {
            logger.error { "Error occured: ${e.printStackTrace()}" }
            throw Exception()
        } finally {
            result?.close()
            statement?.close()
        }

        return false
    }

    private fun removeSchedule(connection: Connection, phoneNumber: String) {
        var statement: Statement? = null
        var result: ResultSet? = null

        try {
            Class.forName(JDBC_DRIVER)

            statement = connection.createStatement()

            val delete = "DELETE FROM \"User\" WHERE number='$phoneNumber'"

            if (statement!!.execute(delete)) {
                logger.info { "Successfully deleted schedule for $phoneNumber "}
            } else {
                logger.error { "Failed to delete schedule for $phoneNumber" }
            }
        } catch (e: SQLException) {
            logger.error { "SQL error occured: ${e.printStackTrace()}" }
            throw SQLException()
        } catch (e: Exception) {
            logger.error { "Error occured: ${e.printStackTrace()}" }
            throw Exception()
        } finally {
            result?.close()
            statement?.close()
        }
    }

}