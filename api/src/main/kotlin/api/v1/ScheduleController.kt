package api.v1

import com.google.gson.*
import mu.KotlinLogging
import org.springframework.web.bind.annotation.*
import java.sql.*

@RestController
class ScheduleController {

    private val logger = KotlinLogging.logger {}

    private val GSON = Gson()

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
        var connection: Connection? = null
        var statement: Statement? = null
        var result: ResultSet? = null

        try {
            Class.forName(JDBC_DRIVER)

            connection = DriverManager.getConnection(DB_URL, PG_USER, PG_PASS)
            logger.info { "Successfully connected to database" }

            statement = connection!!.createStatement()

            val query = "SELECT EXISTS(SELECT FROM \"User\" WHERE number='$phoneNumber');"

            result = statement!!.executeQuery(query)

            if (result != null) {
                while (result.next()) {
                    val exists = result.getString("exists")

                    if (exists == "f") {
                        val e = "No schedule for phone number: [$phoneNumber]"
                        throw IllegalArgumentException(e)
                    }
                }
            }
        } catch (e: SQLException) {
            logger.error { "SQL error occured: ${e.printStackTrace()}" }
            throw SQLException()
        } finally {
            result?.close()
            statement?.close()
            connection?.close()
        }
    }

    private fun queryDatabaseForSchedules(phoneNumber: String): List<Schedule> {
        val schedules: MutableList<Schedule> = mutableListOf()

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

            if (result != null) {
                while (result.next()) {
                    val scheduleResult = GSON.fromJson(result.getString("schedule"), JsonObject::class.java)
                    logger.info { "Found schedule for number: [$phoneNumber]" }

                    val dayStart = GSON.fromJson(scheduleResult.get("days").asJsonObject.get("start"), String::class.java)
                    val dayEnd = GSON.fromJson(scheduleResult.get("days").asJsonObject.get("end"), String::class.java)

                    val hourStart = GSON.fromJson(scheduleResult.get("hours").asJsonObject.get("start"), Int::class.java)
                    val hourEnd = GSON.fromJson(scheduleResult.get("hours").asJsonObject.get("end"), Int::class.java)

                    val sources = GSON.fromJson(scheduleResult.get("sources"), JsonArray::class.java).map { it -> GSON.fromJson(it, String::class.java) }

                    val days = Days(dayStart, dayEnd)
                    val hours = Hours(hourStart, hourEnd)

                    schedules.add(Schedule(days, hours, sources))
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
            connection?.close()
        }

        return schedules
    }

}