package api.v1

import java.sql.*

fun resetDeletedSchedule() {
    var connection: Connection? = null
    var statement: Statement? = null
    var result: ResultSet? = null

    try {
        Class.forName(JDBC_DRIVER)

        connection = DriverManager.getConnection(DB_URL, PG_USER, PG_PASS)

        statement = connection!!.createStatement()

        val scheduleJson = "{\"days\": {\"start\": \"Sunday\", \"end\": \"Saturday\"}, \"hours\": {\"start\": 9, \"end\": 17}, \"sources\": [\"twilio-press\"]}"
        val insert = "INSERT INTO \"User\" VALUES('$TEST_NUMBER_WITH_SCHEDULE_TO_DELETE', '$scheduleJson');"

        statement!!.execute(insert)
    } catch (e: SQLException) {
        throw SQLException()
    } catch (e: Exception) {
        throw Exception()
    } finally {
        result?.close()
        statement?.close()
        connection?.close()
    }
}