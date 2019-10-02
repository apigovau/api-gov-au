package au.gov.api.services

import au.gov.api.models.Feedback
import au.gov.api.repositories.FeedbackRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class FeedbackService @Autowired constructor (private val repo: FeedbackRepository) {

    fun getFeedback(): Feedback = repo.getFeedback()

    fun getFeedback(path:String): String = repo.getFeedback(path)

    fun upsertFeedback(path:String, upVotes:Int) = repo.upsertFeedback(path, upVotes)
}