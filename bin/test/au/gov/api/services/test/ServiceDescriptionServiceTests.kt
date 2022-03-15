package au.gov.api.services.test

import au.gov.api.repositories.dao.ServiceDescriptionDAO
import au.gov.api.repositories.mock.MockEventRepository
import au.gov.api.repositories.mock.MockExternalServiceDescriptionRepository
import au.gov.api.repositories.mock.MockServiceDescriptionRepository
import au.gov.api.repositories.processors.PageProcessor
import au.gov.api.services.ServiceDescriptionService
import au.gov.api.web.mock.MockURIFetcher
import com.beust.klaxon.Klaxon
import org.junit.Assert
import org.junit.Test

class ServiceDescriptionServiceTests {

    val pageText = """
# Page Heading

some content

## SubHead 1

some content

## SubHead 2

some content

"""

    @Test
    fun test_get_heading() {
        val page = PageProcessor.processPage(pageText)

        Assert.assertEquals("Page Heading", page.title)
    }

    @Test
    fun test_get_subheadings() {
        val page = PageProcessor.processPage(pageText)

        Assert.assertEquals(listOf("SubHead 1", "SubHead 2"), page.subHeadings)
    }

    @Test
    fun test_markdown() {
        val fetcher = MockURIFetcher()

        val page = PageProcessor.processPage("This is **Sparta**")
        Assert.assertEquals("<p>This is <strong>Sparta</strong></p>\n", page.html())
    }

    @Test
    fun will_replace_defcat() {
        val baseRepoUri = "test/"
        val fetcher = MockURIFetcher()

        val testDTO = ServiceDescriptionDAO("name", "description", listOf("Use element defcat```edu/edu307``` here"))
        val testDTOString = Klaxon().toJsonString(testDTO)

        fetcher.map["${baseRepoUri}service/${testDTO.name}"] = testDTOString

        val serviceDescriptionService = ServiceDescriptionService(MockServiceDescriptionRepository(baseRepoUri, fetcher), MockExternalServiceDescriptionRepository(), MockEventRepository(fetcher))

        val preProcessed = "Use element Course Code here"

        val page = serviceDescriptionService.get("name")!!.pages.single()

        Assert.assertEquals(preProcessed, page.preProcessed)
    }

    @Test
    fun will_replace_defcat_attribute_specified() {
        val baseRepoUri = "test/"
        val fetcher = MockURIFetcher()

        val testDTO = ServiceDescriptionDAO("name", "description", listOf("The status of the element is: defcat```edu/edu307[status]```"))
        val testDTOString = Klaxon().toJsonString(testDTO)

        fetcher.map["${baseRepoUri}service/${testDTO.name}"] = testDTOString

        val serviceDescriptionService = ServiceDescriptionService(MockServiceDescriptionRepository(baseRepoUri, fetcher), MockExternalServiceDescriptionRepository(),  MockEventRepository(fetcher))

        val preProcessed = "The status of the element is: Standard"

        val page = serviceDescriptionService.get("name")!!.pages.single()

        Assert.assertEquals(preProcessed, page.preProcessed)
    }

    @Test
    fun will_replace_defcat_attribute_specified_link() {
        val baseRepoUri = "test/"
        val fetcher = MockURIFetcher()

        val testDTO = ServiceDescriptionDAO("name", "description", listOf("Use element defcat```edu/edu307[name;1]``` here"))
        val testDTOString = Klaxon().toJsonString(testDTO)

        fetcher.map["${baseRepoUri}service/${testDTO.name}"] = testDTOString

        val serviceDescriptionService = ServiceDescriptionService(MockServiceDescriptionRepository(baseRepoUri, fetcher), MockExternalServiceDescriptionRepository(), MockEventRepository(fetcher))

        val preProcessed = "Use element [Course Code](https://legacy.api.gov.au/definition/edu/edu307) here"

        val page = serviceDescriptionService.get("name")!!.pages.single()

        Assert.assertEquals(preProcessed, page.preProcessed)
    }

    @Test
    fun wont_replace_defcat_if_resolution_fails() {
        val baseRepoUri = "test/"
        val fetcher = MockURIFetcher()

        val testDTO = ServiceDescriptionDAO("name", "description", listOf("Use element defcat```edu/edu309``` here"))
        val testDTOString = Klaxon().toJsonString(testDTO)

        fetcher.map["${baseRepoUri}service/${testDTO.name}"] = testDTOString

        val serviceDescriptionService = ServiceDescriptionService(MockServiceDescriptionRepository(baseRepoUri, fetcher), MockExternalServiceDescriptionRepository(), MockEventRepository(fetcher))

        val preProcessed = "Use element ```edu/edu309``` here"

        val page = serviceDescriptionService.get("name")!!.pages.single()

        Assert.assertEquals(preProcessed, page.preProcessed)
    }

    @Test
    fun can_get_deserialised_object_from_cache() {
        val baseRepoUri = "test/"
        val fetcher = MockURIFetcher()

        val testDTO = ServiceDescriptionDAO("name", "description", listOf("a", "b"))
        val testDTOString = Klaxon().toJsonString(testDTO)

        fetcher.map["${baseRepoUri}service/${testDTO.name}"] = testDTOString

        val serviceDescriptionService = ServiceDescriptionService(MockServiceDescriptionRepository(baseRepoUri, fetcher), MockExternalServiceDescriptionRepository(), MockEventRepository(fetcher))

        val fetchedDTO = serviceDescriptionService.get(testDTO.name)!!

        Assert.assertEquals(fetchedDTO.description, testDTO.description)
        Assert.assertEquals(fetchedDTO.name, testDTO.name)
        fetchedDTO.pages.forEach { it -> Assert.assertEquals(it, PageProcessor.processPage(it.preProcessed)) }
    }

    @Test
    fun test_nav() {
        val baseRepoUri = "test/"
        val fetcher = MockURIFetcher()

        val testDTO = ServiceDescriptionDAO("name", "", listOf(
                """
# p1h11
## p1h21
## p1h22
"""
                ,
                """
# p2h11
## p2h11
"""
        ))

        val testDTOString = Klaxon().toJsonString(testDTO)

        fetcher.map["${baseRepoUri}service/${testDTO.name}"] = testDTOString

        val serviceDescriptionService = ServiceDescriptionService(MockServiceDescriptionRepository(baseRepoUri, fetcher), MockExternalServiceDescriptionRepository(), MockEventRepository(fetcher))

        val fetchedDTO = serviceDescriptionService.get("name")!!

        Assert.assertEquals(2, fetchedDTO.navigation.size)
        Assert.assertEquals("p1h11", fetchedDTO.navigation.keys.first())
        Assert.assertEquals(listOf("p1h21", "p1h22"), fetchedDTO.navigation["p1h11"])
        Assert.assertEquals("p2h11", fetchedDTO.navigation.keys.last())
        Assert.assertEquals(listOf("p2h11"), fetchedDTO.navigation["p2h11"])
    }

    @Test
    fun prev_and_next_pages() {
        val baseRepoUri = "test/"
        val fetcher = MockURIFetcher()

        val testDTO = ServiceDescriptionDAO("name", "", listOf("# p1", "# p2", "# p3"))
        val testDTOString = Klaxon().toJsonString(testDTO)

        fetcher.map["${baseRepoUri}service/${testDTO.name}"] = testDTOString

        val serviceDescriptionService = ServiceDescriptionService(MockServiceDescriptionRepository(baseRepoUri, fetcher), MockExternalServiceDescriptionRepository(), MockEventRepository(fetcher))

        val fetchedDTO = serviceDescriptionService.get("name")!!

        val p1 = fetchedDTO.pages[0]
        val p2 = fetchedDTO.pages[1]
        val p3 = fetchedDTO.pages[2]

        Assert.assertEquals("p1", p1.title)
        Assert.assertEquals("p2", p2.title)
        Assert.assertEquals("p3", p3.title)

        Assert.assertNull(fetchedDTO.previous(p1))
        Assert.assertEquals(p2, fetchedDTO.next(p1))

        Assert.assertEquals(p1, fetchedDTO.previous(p2))
        Assert.assertEquals(p3, fetchedDTO.next(p2))

        Assert.assertEquals(p2, fetchedDTO.previous(p3))
        Assert.assertNull(fetchedDTO.next(p3))
    }

    @Test
    fun test_last_edited_with_invalid_event() {
        val baseRepoUri = "test/"
        val fetcher = MockURIFetcher()

        val testServiceDTO = ServiceDescriptionDAO("name", "description", listOf("a", "b"))
        val testServiceDTOString = Klaxon().toJsonString(testServiceDTO)

        fetcher.map["${baseRepoUri}service/${testServiceDTO.name}"] = testServiceDTOString

        val serviceDescriptionService = ServiceDescriptionService(MockServiceDescriptionRepository(baseRepoUri, fetcher), MockExternalServiceDescriptionRepository(), MockEventRepository(fetcher))

        val expectedLastEdited = ""
        val fetchedLastEdited = serviceDescriptionService.getLastEdited(testServiceDTO.name)

        Assert.assertEquals(expectedLastEdited, fetchedLastEdited)
    }
}