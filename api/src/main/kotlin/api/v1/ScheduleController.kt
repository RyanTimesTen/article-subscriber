package api.v1

import com.google.gson.JsonObject
import mu.KotlinLogging
import org.springframework.web.bind.annotation.*
import java.sql.*

@RestController
class ScheduleController {

    private val logger = KotlinLogging.logger {}

    @GetMapping(value = "/api/v1/schedule{phoneNumber}")
    fun get(@PathVariable("phoneNumber") phoneNumber: String): ScheduleResponse {
        return ScheduleResponse(0, emptyList())
    }

    @PostMapping(value = "/api/v1/schedule{phoneNumber}")
    fun post(@PathVariable("phoneNumber") phoneNumber: String, @RequestBody schedule: Schedule): ScheduleResponse {
        return ScheduleResponse(0, emptyList())
    }

    @PutMapping(value = "/api/v1/schedule{phoneNumber}")
    fun put(@PathVariable("phoneNumber") phoneNumber: String, @RequestBody schedule: Schedule): ScheduleResponse {
        return ScheduleResponse(0, emptyList())
    }

    @DeleteMapping(value = "/api/v1/schedule{phoneNumber}")
    fun delete(@PathVariable("phoneNumber") phoneNumber: String) {
    }
}