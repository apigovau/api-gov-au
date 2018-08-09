package au.gov.serviceDescription

import au.gov.dxa.serviceDescription.Page
import org.junit.Assert
import org.junit.Test

class TestPage {

    val pageText = """
# Page Heading

some content

## SubHead 1

some content

## SubHead 2

some content

"""

    @Test
    fun test_get_heading(){
        val page = Page(pageText)
        Assert.assertEquals("Page Heading", page.title)
    }

    @Test
    fun test_get_subheadings(){
        val page = Page(pageText)
        Assert.assertEquals(listOf("SubHead 1", "SubHead 2"), page.subHeaddings)
    }

    @Test
    fun test_markdown(){
        val page = Page("This is **Sparta**")
        Assert.assertEquals("<p>This is <strong>Sparta</strong></p>\n", page.html)
    }
}