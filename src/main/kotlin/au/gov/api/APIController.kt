
package au.gov.api

import au.gov.api.serviceDescription.ServiceDescriptionService
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.stereotype.Component
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.sql.Connection
import java.sql.SQLException
import javax.sql.DataSource

@RestController
class APIController {


    @Autowired
    private lateinit var feedback: Feedback

    @Autowired
    lateinit var serviceDescriptionService: ServiceDescriptionService

    @GetMapping("/api/feedback")
    fun feedback(@RequestParam path:String, @RequestParam upVotes:Int):String{
        return feedback.feedback(path, upVotes)
    }

    @GetMapping("/api/allFeedback")
    fun getFeedback(): Feedback.Feedback {
        return feedback.getFeedback()
    }

    @GetMapping("/api/pathFeedback")
    fun getFeedback(@RequestParam path:String): String {
        return feedback.getFeedback(path)
    }

    @GetMapping("/api/flush/{id}")
    fun flushServiceCache(@PathVariable id:String) = serviceDescriptionService.flush(id)

}
