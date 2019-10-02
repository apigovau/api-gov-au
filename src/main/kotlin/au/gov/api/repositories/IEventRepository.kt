package au.gov.api.repositories

import au.gov.api.models.Event

interface IEventRepository {

    fun get(id:String): List<Event>
}