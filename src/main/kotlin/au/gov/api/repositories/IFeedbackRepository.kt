package au.gov.api.repositories

import au.gov.api.models.Feedback
import org.springframework.context.annotation.Bean
import java.sql.SQLException
import javax.sql.DataSource

interface IFeedbackRepository {
    fun upsertFeedback(path: String, upVotes: Int): String
    fun getFeedback(): Feedback
    fun getFeedback(path: String): String

    @Bean
    @Throws(SQLException::class)
    fun dataSource(): DataSource?
}