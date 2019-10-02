package au.gov.api.services

import au.gov.api.models.Event
import au.gov.api.models.ServiceDescription
import au.gov.api.models.ServiceDescriptionListItem
import au.gov.api.repositories.IEventRepository
import au.gov.api.repositories.IServiceDescriptionRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class ServiceDescriptionService @Autowired constructor (private val serviceRepo: IServiceDescriptionRepository, private val eventRepo: IEventRepository){

    fun get(id:String): ServiceDescription? { return serviceRepo.get(id) }

    fun getLastEdited(id:String):String {
        return try {
            "Last edited: " + getEvents(id).firstOrNull()!!.timestamp
        } catch (e:Exception)
        {
            ""
        }
    }

    fun list(): List<ServiceDescriptionListItem> = serviceRepo.list()

    fun count(): Int = serviceRepo.list().size

    fun flush(id:String) = serviceRepo.flush(id)

    private fun getEvents(id:String) : List<Event> = eventRepo.get(id)
}