
package au.gov.api.controllers

import au.gov.api.models.Feedback
import au.gov.api.services.FeedbackService
import au.gov.api.services.ServiceDescriptionService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

import khttp.get

@RestController
class CoreRestController {

    @Autowired
    private lateinit var feedbackService: FeedbackService

    @Autowired
    private lateinit var serviceDescriptionService: ServiceDescriptionService

    @GetMapping("/api/feedback")
    fun feedback(@RequestParam path:String, @RequestParam upVotes:Int):String {
        return feedbackService.upsertFeedback(path, upVotes)
    }

    @GetMapping("/api/allFeedback")
    fun getFeedback(): Feedback {
        return feedbackService.getFeedback()
    }

    @GetMapping("/api/pathFeedback")
    fun getFeedback(@RequestParam path:String): String {
        return feedbackService.getFeedback(path)
    }

    @GetMapping("/api/flush/{id}")
    fun flushServiceCache(@PathVariable id:String) = serviceDescriptionService.flush(id)

    @GetMapping("/github")
    fun fromGitHub(@RequestParam path:String): String{
        val rsp = khttp.get("https://raw.githubusercontent.com/apigovau/api-descriptions/master/" + path.replace("~","/"), mapOf())
        return rsp.text
    }
}
