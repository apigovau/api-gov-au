package au.gov.api.services

import au.gov.api.config.Config
import au.gov.api.models.Event
import au.gov.api.models.ServiceDescription
import au.gov.api.models.ServiceDescriptionListItem
import au.gov.api.models.ServiceRevisionListItem
import au.gov.api.repositories.IEventRepository
import au.gov.api.repositories.IExternalServiceDescriptionRepository
import au.gov.api.repositories.IServiceDescriptionRepository
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


@Service
class ServiceDescriptionService @Autowired constructor(
        private val serviceRepo: IServiceDescriptionRepository,
        private val externalServiceRepo: IExternalServiceDescriptionRepository,
        private val eventRepo: IEventRepository
) {

    fun get(id: String): ServiceDescription? {
        return serviceRepo.get(id)
    }

    fun getLastEdited(id: String): String {
        return try {

            val revUri = "${Config.get("BaseRepoURI")}service/$id/revisions"
            val res = khttp.get(revUri)
            val result = mutableListOf<ServiceRevisionListItem>()

            ObjectMapper().readValue(res.text, List::class.java).forEach {
                val lhm = it as LinkedHashMap<String, String>
                result.add(ServiceRevisionListItem(lhm.getOrDefault("id", ""), LocalDateTime.parse(lhm.getOrDefault("timestamp", ""))))
            }

            result.sortedBy { it.timestamp }
            val fmt = DateTimeFormatter.ofPattern("dd MMM yyyy, HH:mm:ss")
            "Last edited: " + result.last().timestamp.format(fmt)
        } catch (e: Exception) {
            ""
        }
    }

    fun list(): List<ServiceDescriptionListItem> = listOf(
            serviceRepo.list(),
            externalServiceRepo.list()
    ).flatten()

    fun count(): Int = serviceRepo.list().size

    fun flush(id: String) = serviceRepo.flush(id)

    private fun getEvents(id: String): List<Event> = eventRepo.get(id)
}