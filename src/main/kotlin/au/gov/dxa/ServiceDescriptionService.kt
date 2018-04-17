package au.gov.dxa

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class ServiceDescriptionService {

    @Autowired
    lateinit var service: ServiceDescriptionRepository


    fun get(id:String): ServiceDescription? {
        return service.get(id)
    }

    fun getFaq(id:String): String? {
        return service.getFaq(id)
    }

    fun list(): List<ServiceListVM> = service.list()
}