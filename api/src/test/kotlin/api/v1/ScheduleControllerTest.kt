package api.v1

import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import io.kotlintest.matchers.shouldBe
import io.kotlintest.matchers.shouldThrow
import io.kotlintest.specs.ShouldSpec

class ScheduleControllerTest : ShouldSpec() {

    private val TEST_NUMBER_WITH_SCHEDULE = "1234567890"
    private val TEST_NUMBER_WITHOUT_SCHEUDLE = "9999999999"
    private val INVALID_PHONE_NUMBER = "1"

    private val SUNDAY = "Sunday"
    private val MONDAY = "Monday"
    private val TUESDAY = "Tuesday"
    private val WEDNESDAY = "Wednesday"
    private val THURSDAY = "Thursday"
    private val FRIDAY = "Friday"
    private val SATURDAY = "Saturday"

    private val TWELVE_AM = 0
    private val ONE_AM = 1
    private val TWO_AM = 2
    private val THREE_AM = 3
    private val FOUR_AM = 4
    private val FIVE_AM = 5
    private val SIX_AM = 6
    private val SEVEN_AM = 7
    private val EIGHT_AM = 8
    private val NINE_AM = 9
    private val TEN_AM = 10
    private val ELEVEN_AM = 11
    private val TWELVE_PM = 12
    private val ONE_PM = 13
    private val TWO_PM = 14
    private val THREE_PM = 15
    private val FOUR_PM = 16
    private val FIVE_PM = 17
    private val SIX_PM = 18
    private val SEVEN_PM = 19
    private val EIGHT_PM = 20
    private val NINE_PM = 21
    private val TEN_PM = 22
    private val ELEVEN_PM = 23

    private val DAY_START_KEY = "start"
    private val DAY_END_KEY = "end"

    private val HOUR_START_KEY = "start"
    private val HOUR_END_KEY = "end"

    val GSON = Gson()

    init {

        "The Schedule Controller" {

            "given a proper GET request" {

                "with a schedule Sunday-Saturday, 9AM-5PM schedule, with one source" {
                    should("respond with the corresponding schedule") {
                        val response = ScheduleController().get(TEST_NUMBER_WITH_SCHEDULE)

                        response.total shouldBe 1
                        response.schedules.size shouldBe 1

                        val days = response.schedules[0].days

                        days.has(DAY_START_KEY) shouldBe true
                        days.has(DAY_END_KEY) shouldBe true

                        days.get(DAY_START_KEY) shouldBe SUNDAY
                        days.get(DAY_END_KEY) shouldBe SATURDAY

                        val hours = response.schedules[0].hours

                        hours.has(HOUR_START_KEY) shouldBe true
                        hours.has(HOUR_END_KEY) shouldBe true

                        hours.get(HOUR_START_KEY) shouldBe NINE_AM
                        hours.get(HOUR_END_KEY) shouldBe FIVE_PM

                        response.schedules[0].sources.size() shouldBe 1
                    }
                }

                "with no schedule registered for the given phone number" {
                    should("respond with no schedules") {
                        val response = ScheduleController().get(TEST_NUMBER_WITHOUT_SCHEUDLE)

                        response.total shouldBe 0
                        response.schedules.size shouldBe 0
                    }
                }

            }

            "given an improper GET request" {

                "with an invalid phone number" {
                    should("throw an invalid phone number exception") {
                        shouldThrow<IllegalArgumentException> {
                            ScheduleController().get(INVALID_PHONE_NUMBER)
                        }
                    }
                }

            }

            "given a proper POST request" {

                "with a Monday-Friday and 10AM-4PM schedule from two sources: time_tech and twilio_press" {
                    should("return that schedule as a response") {
                        val schedule = Schedule(getDaysJson(MONDAY, FRIDAY), getHoursJson(TEN_AM, FOUR_PM), getSourcesJson(TIME_TECH, TWILIO_PRESS))
                        val response = ScheduleController().post(TEST_NUMBER_WITHOUT_SCHEUDLE, schedule)

                        response.total shouldBe 1
                        response.schedules.size shouldBe 1

                        val days = response.schedules[0].days

                        days.has(DAY_START_KEY) shouldBe true
                        days.has(DAY_END_KEY) shouldBe true

                        days.get(DAY_START_KEY) shouldBe MONDAY
                        days.get(DAY_END_KEY) shouldBe FRIDAY

                        val hours = response.schedules[0].hours

                        hours.has(HOUR_START_KEY) shouldBe true
                        hours.has(HOUR_END_KEY) shouldBe true

                        hours.get(HOUR_START_KEY) shouldBe TEN_AM
                        hours.get(HOUR_END_KEY) shouldBe FOUR_PM

                        val sources = response.schedules[0].sources

                        sources.size() shouldBe 2

                        sources.contains(GSON.fromJson(TIME_TECH, JsonElement::class.java)) shouldBe true
                        sources.contains(GSON.fromJson(TWILIO_PRESS, JsonElement::class.java)) shouldBe true
                    }
                }

            }

            "given an improper POST request" {

                "with an invalid phone number" {
                    should("throw an invalid phone number exception") {
                        val schedule = Schedule(getDaysJson(TUESDAY, THURSDAY), getHoursJson(SIX_PM, ELEVEN_PM), getSourcesJson(TIME_TECH))
                        shouldThrow<IllegalArgumentException> {
                            ScheduleController().post(INVALID_PHONE_NUMBER, schedule)
                        }
                    }
                }

                "with an end day that comes before the start day" {
                    should("throw an illegal argument exception") {
                        val schedule = Schedule(getDaysJson(WEDNESDAY, THURSDAY), getHoursJson(ONE_AM, SIX_AM), getSourcesJson(TIME_TECH))
                        shouldThrow<IllegalArgumentException> {
                            ScheduleController().post(TEST_NUMBER_WITHOUT_SCHEUDLE, schedule)
                        }
                    }
                }

                "with an end hour that comes before the start hour" {
                    should("throw an illegal argument exception") {
                        val schedule = Schedule(getDaysJson(WEDNESDAY, SATURDAY), getHoursJson(TWO_AM, TWELVE_AM), getSourcesJson(TIME_TECH))
                        shouldThrow<IllegalArgumentException> {
                            ScheduleController().post(TEST_NUMBER_WITHOUT_SCHEUDLE, schedule)
                        }
                    }
                }

            }

            "given a proper PUT request" {

                "with a Tuesday-Satuday and 5AM-11AM schedule from facebook_blog" {
                    should("return that schedule as a response") {
                        val schedule = Schedule(getDaysJson(TUESDAY, SATURDAY), getHoursJson(FIVE_AM, ELEVEN_AM), getSourcesJson(FACEBOOK_BLOG))
                        val response = ScheduleController().put(TEST_NUMBER_WITH_SCHEDULE, schedule)

                        response.total shouldBe 1
                        response.schedules.size shouldBe 1

                        val days = response.schedules[0].days

                        days.has(DAY_START_KEY) shouldBe true
                        days.has(DAY_END_KEY) shouldBe true

                        days.get(DAY_START_KEY) shouldBe TUESDAY
                        days.get(DAY_END_KEY) shouldBe SATURDAY

                        val hours = response.schedules[0].hours

                        hours.has(HOUR_START_KEY) shouldBe true
                        hours.has(HOUR_END_KEY) shouldBe true

                        hours.get(HOUR_START_KEY) shouldBe FIVE_AM
                        hours.get(HOUR_END_KEY) shouldBe ELEVEN_AM

                        val sources = response.schedules[0].sources

                        sources.size() shouldBe 1

                        sources.contains(GSON.fromJson(FACEBOOK_BLOG, JsonElement::class.java)) shouldBe true
                    }
                }

            }

            "given an improper PUT request" {

                "with an invalid phone number" {
                    should("throw an invalid phone number exception") {
                        val schedule = Schedule(getDaysJson(TUESDAY, THURSDAY), getHoursJson(SIX_PM, ELEVEN_PM), getSourcesJson(TIME_TECH))
                        shouldThrow<IllegalArgumentException> {
                            ScheduleController().put(INVALID_PHONE_NUMBER, schedule)
                        }
                    }
                }

                "with an end day that comes before the start day" {
                    should("throw an illegal argument exception") {
                        val schedule = Schedule(getDaysJson(WEDNESDAY, THURSDAY), getHoursJson(ONE_AM, SIX_AM), getSourcesJson(TIME_TECH))
                        shouldThrow<IllegalArgumentException> {
                            ScheduleController().put(TEST_NUMBER_WITHOUT_SCHEUDLE, schedule)
                        }
                    }
                }

                "with an end hour that comes before the start hour" {
                    should("throw an illegal argument exception") {
                        val schedule = Schedule(getDaysJson(WEDNESDAY, SATURDAY), getHoursJson(TWO_AM, TWELVE_AM), getSourcesJson(TIME_TECH))
                        shouldThrow<IllegalArgumentException> {
                            ScheduleController().put(TEST_NUMBER_WITHOUT_SCHEUDLE, schedule)
                        }
                    }
                }

            }

            "given a proper DELETE request" {

                "with a valid number with a schedule" {
                    should("delete the schedule associated with that number") {
                        ScheduleController().delete(TEST_NUMBER_WITH_SCHEDULE)

                        val scheduleResponse = ScheduleController().get(TEST_NUMBER_WITH_SCHEDULE)

                        scheduleResponse.total shouldBe 0
                        scheduleResponse.schedules.size shouldBe 0
                    }
                }

            }

            "given an improper DELETE request" {

                "with an invalid phone number" {
                    should("throw an invalid phone number exception") {
                        shouldThrow<IllegalArgumentException> {
                            ScheduleController().delete(INVALID_PHONE_NUMBER)
                        }
                    }
                }

                "with a non-existent schedule" {
                    should("throw some exception") {
                        shouldThrow<IllegalArgumentException> {
                            ScheduleController().delete(TEST_NUMBER_WITHOUT_SCHEUDLE)
                        }
                    }
                }

            }

        }

    }

    private fun getDaysJson(start: String, end: String) = GSON.fromJson("""{"start": $start,"end": $end}""", JsonObject::class.java)

    private fun getHoursJson(start: Int, end: Int) = GSON.fromJson("""{"start": $start,"end": $end}""", JsonObject::class.java)

    private fun getSourcesJson(vararg sources: String): JsonArray {
        val sourcesArray = GSON.fromJson("[]", JsonArray::class.java)
        sources.forEach { source ->
            sourcesArray.add(source)
        }
        return sourcesArray
    }
}