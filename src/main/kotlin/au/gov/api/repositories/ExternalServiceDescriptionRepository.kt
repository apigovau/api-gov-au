package au.gov.api.repositories

import au.gov.api.models.IndexMetadata
import au.gov.api.models.ServiceDescriptionListItem
import au.gov.api.web.ResourceCache
import au.gov.api.web.URIFetcher
import com.beust.klaxon.Json
import com.beust.klaxon.Klaxon
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

@Component
class ExternalServiceDescriptionRepository(
        @Value("\${EXTERNAL_VICGOV_URI:}")
        val vicGovUrl: String,
        @Value("\${EXTERNAL_VICGOV_KEY:}")
        val vicGovKey: String
): IExternalServiceDescriptionRepository {

    val providers:MutableList<IExternalProvider> = mutableListOf()

    init {
        if (vicGovUrl.isNotEmpty() && vicGovKey.isNotEmpty()) {
            providers.add(
                VicGovAuExternalProvider(
                    url = vicGovUrl,
                    key = vicGovKey
                )
            )
        }
    }

    override fun list(): List<ServiceDescriptionListItem> {
        return providers.map { it.get() }
                .flatten()
    }
}

interface IExternalProvider {
    fun get(): List<ServiceDescriptionListItem>
}

class VicGovAuExternalProvider(val url: String, val key: String) : IExternalProvider {

    private val logger: Logger = LoggerFactory.getLogger(VicGovAuExternalProvider::class.java)

    private val apiOverviews = ResourceCache(
            fetcher = VicGovUrlFetcher(key),
            minutesToLive = 5,
            convert = { serial -> Klaxon().parseArray<ApiOverviewDTO>(serial)!! }
    )

    private val images = ResourceCache(
            fetcher = VicGovUrlFetcher(key),
            minutesToLive = 5,
            convert = { serial -> Klaxon().parse<ImageDTO>(serial)!! })

    override fun get(): List<ServiceDescriptionListItem> {
        var result: MutableList<ServiceDescriptionListItem> = mutableListOf()

        try {
            apiOverviews.get("$url/apis")
                    .forEach {
                        val serviceDescriptionUrl = it.links.first { linkDTO -> linkDTO.rel == "API" }.href

                        val imageUrl = images.get("$url/apis/${it.id}/image").imageUrl

                        result.add(
                                ServiceDescriptionListItem(
                                        name = it.name,
                                        agency = "",
                                        definition = it.description,
                                        domain = "",
                                        logoURI = imageUrl,
                                        metadata = IndexMetadata(
                                                agency = "",
                                                externalLocation = serviceDescriptionUrl,
                                                ingestSource = "",
                                                numberOfConversations = 0,
                                                space = "VICGOV",
                                                visibility = true
                                        ),
                                        openAPISpec = "",
                                        path = "",
                                        security = "",
                                        status = "",
                                        tags = mutableListOf(" Version - ${it.version}"),
                                        technology = ""
                                )
                        )
                    }
        } catch (e: Exception) {
            result = mutableListOf()
            logger.error("Exception encountered retrieving VIC GOV apis", e)
        }

        return result
    }

    data class ApiOverviewDTO(
            @Json(name = "id")
            val id:String,
            @Json(name = "api_name")
            val name:String,
            @Json(name = "description")
            val description:String,
            @Json(name = "version")
            val version:String,
            @Json(name = "_links")
            val links:List<LinkDTO>
    )

    data class ImageDTO(@Json(name = "image_url") val imageUrl:String)

    data class LinkDTO(@Json(name = "href") val href:String, @Json(name = "rel") val rel:String)
}

class VicGovUrlFetcher(val key: String) : URIFetcher {
    override fun fetch(uri: String): URIFetcher.Result {
        val result = khttp.get(
                url = uri,
                headers = mapOf(
                        Pair("accept", "application/json"),
                        Pair("apikey", key)
                )
        )

        return URIFetcher.Result(
                status = result.statusCode,
                response = result.text
        )

    }

}