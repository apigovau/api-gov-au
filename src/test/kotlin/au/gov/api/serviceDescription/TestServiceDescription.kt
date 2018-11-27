package au.gov.serviceDescription

import au.gov.api.serviceDescription.ServiceDescription
import org.junit.Assert
import org.junit.Test

class TestServiceDescription {

    @Test
    fun test_nav(){
        val sd = ServiceDescription("","",listOf("""
# p1h11
## p1h21
## p1h22
""",
"""
# p2h11
## p2h11
"""))

        Assert.assertEquals(2, sd.navigation.size)
        Assert.assertEquals("p1h11", sd.navigation.keys.first())
        Assert.assertEquals(listOf("p1h21","p1h22"), sd.navigation["p1h11"])
        Assert.assertEquals("p2h11", sd.navigation.keys.last())
        Assert.assertEquals(listOf("p2h11"), sd.navigation["p2h11"])

    }


    @Test
    fun prev_and_next_pages(){
        val sd = ServiceDescription("","",listOf("# p1","# p2","# p3"))
        val p1 = sd.pages.get(0)
        val p2 = sd.pages.get(1)
        val p3 = sd.pages.get(2)

        Assert.assertEquals("p1", p1.title)
        Assert.assertEquals("p2", p2.title)
        Assert.assertEquals("p3", p3.title)

        Assert.assertNull(sd.previous(p1))
        Assert.assertEquals(p2, sd.next(p1))

        Assert.assertEquals(p1, sd.previous(p2))
        Assert.assertEquals(p3, sd.next(p2))

        Assert.assertEquals(p2, sd.previous(p3))
        Assert.assertNull(sd.next(p3))
    }

}

