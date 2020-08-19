package au.gov.api.repositories.mock

import au.gov.api.models.ServiceDescriptionListItem
import au.gov.api.repositories.IExternalServiceDescriptionRepository

class MockExternalServiceDescriptionRepository : IExternalServiceDescriptionRepository {
    override fun list(): List<ServiceDescriptionListItem> {
        TODO("Not yet implemented")
    }
}