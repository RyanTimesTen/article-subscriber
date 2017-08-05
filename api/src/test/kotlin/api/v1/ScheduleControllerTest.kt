package api.v1

import io.kotlintest.Spec
import io.kotlintest.matchers.shouldBe
import io.kotlintest.matchers.shouldThrow
import io.kotlintest.specs.ShouldSpec

class ScheduleControllerTest : ShouldSpec() {

    override fun interceptSpec(context: Spec, spec: () -> Unit) {
        if (TEST_PHONE_NUMBERS.size == TEST_PHONE_NUMBERS.filterNotNull().size) {
            spec()
            resetDeletedSchedule()
        } else {
            val e = "Not all test numbers were initialized"
            throw UninitializedPropertyAccessException(e)
        }
    }

    init {

        "The Schedule Controller" {

            "given a proper GET request" {

                "with a schedule Sunday-Saturday, 9AM-5PM schedule, with one source: twilio-press" {
                    should("respond with the corresponding schedule") {
                        val response = ScheduleController().get(TEST_NUMBER_WITH_SCHEDULE!!)

                        response.total shouldBe 1
                        response.schedules.size shouldBe 1

                        val days = response.schedules.first().days

                        days.start shouldBe SUNDAY
                        days.end shouldBe SATURDAY

                        val hours = response.schedules.first().hours

                        hours.start shouldBe NINE_AM
                        hours.end shouldBe FIVE_PM

                        val sources = response.schedules.first().sources

                        sources.size shouldBe 1
                        sources.first() shouldBe TWILIO_PRESS
                    }
                }

                "with no schedule registered for the given phone number" {
                    should("respond with no schedules") {
                        val response = ScheduleController().get(TEST_NUMBER_WITHOUT_SCHEDULE_NO_ADD!!)

                        response.total shouldBe 0
                        response.schedules.size shouldBe 0
                    }
                }

            }

            "given an improper GET request" {

                "with an invalid phone number" {
                    should("throw an invalid phone number exception") {
                        shouldThrow<IllegalArgumentException> {
                            ScheduleController().get(INVALID_PHONE_NUMBER!!)
                        }
                    }
                }

            }

            "given a proper POST request" {

                "with a Monday-Friday and 10AM-4PM schedule from two sources: time_tech and twilio_press" {
                    should("return that schedule as a response") {
                        val schedule = Schedule(Days(MONDAY, FRIDAY), Hours(TEN_AM, FOUR_PM), listOf(TIME_TECH, TWILIO_PRESS))
                        val response = ScheduleController().post(TEST_NUMBER_WITHOUT_SCHEDULE!!, schedule)

                        response.total shouldBe 1
                        response.schedules.size shouldBe 1

                        val days = response.schedules[0].days

                        days.start shouldBe MONDAY
                        days.end shouldBe FRIDAY

                        val hours = response.schedules[0].hours

                        hours.start shouldBe TEN_AM
                        hours.end shouldBe FOUR_PM

                        val sources = response.schedules[0].sources

                        sources.size shouldBe 2

                        sources.contains(TIME_TECH) shouldBe true
                        sources.contains(TWILIO_PRESS) shouldBe true
                    }
                }

            }

            "given an improper POST request" {

                "with an invalid phone number" {
                    should("throw an invalid phone number exception") {
                        val schedule = Schedule(Days(TUESDAY, THURSDAY), Hours(SIX_PM, ELEVEN_PM), listOf(TIME_TECH))
                        shouldThrow<IllegalArgumentException> {
                            ScheduleController().post(INVALID_PHONE_NUMBER!!, schedule)
                        }
                    }
                }

                "with an end day that comes before the start day" {
                    should("throw an illegal argument exception") {
                        val schedule = Schedule(Days(THURSDAY, WEDNESDAY), Hours(ONE_AM, SIX_AM), listOf(TIME_TECH))
                        shouldThrow<IllegalArgumentException> {
                            ScheduleController().post(TEST_NUMBER_WITHOUT_SCHEDULE!!, schedule)
                        }
                    }
                }

                "with an end hour that comes before the start hour" {
                    should("throw an illegal argument exception") {
                        val schedule = Schedule(Days(WEDNESDAY, SATURDAY), Hours(TWO_AM, TWELVE_AM), listOf(TIME_TECH))
                        shouldThrow<IllegalArgumentException> {
                            ScheduleController().post(TEST_NUMBER_WITHOUT_SCHEDULE!!, schedule)
                        }
                    }
                }

            }

            "given a proper PUT request" {

                "with a Tuesday-Satuday and 5AM-11AM schedule from facebook_blog" {
                    should("return that schedule as a response") {
                        val schedule = Schedule(Days(TUESDAY, SATURDAY), Hours(FIVE_AM, ELEVEN_AM), listOf(FACEBOOK_BLOG))
                        val response = ScheduleController().put(TEST_NUMBER_WITHOUT_SCHEDULE!!, schedule)

                        response.total shouldBe 1
                        response.schedules.size shouldBe 1

                        val days = response.schedules[0].days

                        days.start shouldBe TUESDAY
                        days.end shouldBe SATURDAY

                        val hours = response.schedules[0].hours
                        
                        hours.start shouldBe FIVE_AM
                        hours.end shouldBe ELEVEN_AM

                        val sources = response.schedules[0].sources

                        sources.size shouldBe 1

                        sources.contains(FACEBOOK_BLOG) shouldBe true
                    }
                }

            }

            "given an improper PUT request" {

                "with an invalid phone number" {
                    should("throw an invalid phone number exception") {
                        val schedule = Schedule(Days(TUESDAY, THURSDAY), Hours(SIX_PM, ELEVEN_PM), listOf(TIME_TECH))
                        shouldThrow<IllegalArgumentException> {
                            ScheduleController().put(INVALID_PHONE_NUMBER!!, schedule)
                        }
                    }
                }

                "with an end day that comes before the start day" {
                    should("throw an illegal argument exception") {
                        val schedule = Schedule(Days(SATURDAY, THURSDAY), Hours(ONE_AM, SIX_AM), listOf(TIME_TECH))
                        shouldThrow<IllegalArgumentException> {
                            ScheduleController().put(TEST_NUMBER_WITHOUT_SCHEDULE!!, schedule)
                        }
                    }
                }

                "with an end hour that comes before the start hour" {
                    should("throw an illegal argument exception") {
                        val schedule = Schedule(Days(WEDNESDAY, SATURDAY), Hours(TWO_AM, TWELVE_AM), listOf(TIME_TECH))
                        shouldThrow<IllegalArgumentException> {
                            ScheduleController().put(TEST_NUMBER_WITHOUT_SCHEDULE!!, schedule)
                        }
                    }
                }

            }

            "given a proper DELETE request" {

                "with a valid number with a schedule" {
                    should("delete the schedule associated with that number") {
                        ScheduleController().delete(TEST_NUMBER_WITH_SCHEDULE_TO_DELETE!!)

                        val scheduleResponse = ScheduleController().get(TEST_NUMBER_WITH_SCHEDULE_TO_DELETE)

                        scheduleResponse.total shouldBe 0
                        scheduleResponse.schedules.size shouldBe 0
                    }
                }

            }

            "given an improper DELETE request" {

                "with an invalid phone number" {
                    should("throw an invalid phone number exception") {
                        shouldThrow<IllegalArgumentException> {
                            ScheduleController().delete(INVALID_PHONE_NUMBER!!)
                        }
                    }
                }

                "with a non-existent schedule" {
                    should("throw some exception") {
                        shouldThrow<IllegalArgumentException> {
                            ScheduleController().delete(TEST_NUMBER_WITHOUT_SCHEDULE_NO_ADD!!)
                        }
                    }
                }

            }

        }

    }

}