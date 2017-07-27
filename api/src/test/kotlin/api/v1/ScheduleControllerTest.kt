package api.v1

import io.kotlintest.Spec
import io.kotlintest.matchers.shouldBe
import io.kotlintest.matchers.shouldNotBe
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

            }

            "given an improper POST request" {

            }

            "given a proper PUT request" {

            }

            "given an improper PUT request" {

            }

            "given a proper DELETE request" {

            }

            "given an improper DELETE request" {

            }

        }

    }

}