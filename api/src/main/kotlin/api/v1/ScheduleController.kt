package api.v1

import mu.KotlinLogging
import org.springframework.web.bind.annotation.*
import java.sql.*

@RestController
class ScheduleController {
    private val logger = KotlinLogging.logger {}

    @GetMapping(value = "/api/v1/schedule{phoneNumber}")
    fun get() {

    }

    @PostMapping(value = "/api/v1/schedule{phoneNumber}")
    fun post() {

    }

    @PutMapping(value = "/api/v1/schedule{phoneNumber}")
    fun put() {

    }

    @DeleteMapping(value = "/api/v1/schedule{phoneNumber}")
    fun delete() {

    }
}