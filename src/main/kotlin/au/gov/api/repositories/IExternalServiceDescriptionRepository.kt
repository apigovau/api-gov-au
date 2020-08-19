package au.gov.api.repositories

import au.gov.api.models.ServiceDescriptionListItem

interface IExternalServiceDescriptionRepository {

    fun list(): List<ServiceDescriptionListItem>
}