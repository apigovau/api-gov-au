package au.gov.api.models

import java.time.Duration
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

data class Comment(val username:String, val userImageURI:String, val created_at:String, var body:String) {
    fun postedOnString() : String {
        val dateCreated = LocalDateTime.parse(created_at, DateTimeFormatter.ISO_DATE_TIME)

        val duration = Duration.between(dateCreated, LocalDateTime.now())

        if (duration.toMinutes() < 60){
            return "posted ${duration.toMinutes()} minutes ago"
        }

        if (duration.toHours() < 24) {
            return "posted ${duration.toHours()} hours ago"
        }

        return "posted on the ${dateCreated.dayOfMonth}/${dateCreated.monthValue}/${dateCreated.year}"
    }
}