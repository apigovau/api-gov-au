
package au.gov.api

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

@Component
class Feedback {


    //companion object {

        @Value("\${spring.datasource.url}")
        private var dbUrl: String? = null

        @Autowired
        private lateinit var dataSource: DataSource

        fun feedback(@RequestParam path: String, @RequestParam upVotes: Int):String {
            var output = ""
            var connection: Connection? = null
            try {
                connection = dataSource.connection
                val stmt = connection.createStatement()
                stmt.executeUpdate("CREATE TABLE IF NOT EXISTS feedback (path text, upVotes numeric, count numeric)")

                val lookupStmt = connection.prepareStatement("SELECT upVotes, count from feedback where path = ?")
                lookupStmt.setString(1, path)
                val rs = lookupStmt.executeQuery()
                var prevupVotes = 0
                if (rs.next()) {
                    prevupVotes = rs.getInt(1)
                    val count = rs.getInt(2)
                    val insertStmt = connection.prepareStatement("UPDATE feedback set upVotes = ?, count = ? where path = ?")
                    insertStmt.setInt(2, count + 1)
                    insertStmt.setString(3, path)
                    if (upVotes == 1) {
                        insertStmt.setInt(1, upVotes + prevupVotes)
                        output = "${upVotes + prevupVotes} of ${count + 1} people found this useful"
                    } else {
                        insertStmt.setInt(1, prevupVotes)
                        output = "${prevupVotes} of ${count + 1} people found this useful"
                    }
                    insertStmt.execute()
                } else {
                    val insertStmt = connection.prepareStatement("INSERT INTO feedback VALUES( ?, ?, 1)")
                    insertStmt.setString(1, path)
                    insertStmt.setInt(2, if(upVotes == 1) 1 else 0)
                    insertStmt.execute()
                    output = "${if(upVotes == 1) 1 else 0} of 1 people found this useful"
                }


            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                if (connection != null) connection.close()
            }
            return output
        }

        data class PathupVotes(val path: String, val upVotes: String)
        data class Feedback(val count: Int, val queries: List<PathupVotes>)

        fun getFeedback(): Feedback {
            var connection: Connection? = null
            try {
                connection = dataSource.connection
                val stmt = connection.createStatement()
                stmt.executeUpdate("CREATE TABLE IF NOT EXISTS feedback (path text, upVotes numeric, count numeric)")
                val rs = stmt.executeQuery("SELECT * from feedback")

                val output = mutableListOf<PathupVotes>()
                while (rs.next()) {
                    val count = rs.getInt("count")
                    val upVotes = rs.getInt("upVotes")
                    val words = "$upVotes out of $count people found this useful"
                    output.add(PathupVotes(rs.getString("path"), words))
                }
                return Feedback(output.size, output.toList())
            } catch (e: Exception) {
                e.printStackTrace()

            } finally {
                if (connection != null) connection.close()
            }

            return Feedback(0, listOf())
        }


        fun getFeedback(path: String): String {
            var connection: Connection? = null
            try {
                connection = dataSource.connection

                val stmt = connection.createStatement()
                stmt.executeUpdate("CREATE TABLE IF NOT EXISTS feedback (path text, upVotes numeric, count numeric)")

                val lookupStmt = connection.prepareStatement("SELECT upVotes, count from feedback where path = ?")
                lookupStmt.setString(1, path)
                val rs = lookupStmt.executeQuery()
                if (rs.next()) {
                    val upVotes = rs.getInt(1)
                    val count = rs.getInt(2)
                    return "$upVotes out of $count people found this useful"
                }

            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                if (connection != null) connection.close()
            }
            return ""
        }

        @Bean
        @Throws(SQLException::class)
        fun dataSource(): DataSource? {
            if (dbUrl?.isEmpty() ?: true) {
                return HikariDataSource()
            } else {
                val config = HikariConfig()
                config.jdbcUrl = dbUrl
                try {
                    return HikariDataSource(config)
                } catch (e: Exception) {
                    return null
                }
            }
        }

    //}
}
