package au.gov.api.serviceDescription

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class ServiceDescriptionService {

    @Autowired
    lateinit var repo: ServiceDescriptionRepository

    fun get(id:String): ServiceDescription? {
        return repo.get(id)
    }

    fun list(): List<ServiceListVM> = repo.list()

    fun flush(id:String) = repo.flush(id)
}
