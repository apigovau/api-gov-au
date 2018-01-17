package au.gov

import au.gov.dxa.ServiceDescriptionRepository
import org.junit.Assert
import org.junit.Test

class TestServicesRepository{

    val mock = mutableListOf("""
        {
  "name":"Definitions Catalogue",
  "id":"definitions-catalogue",
  "subpages":[
    {
        "title":"Getting Started",
        "markdown": "# Getting Started\n\nThe Definitions Catalogue contains the definitions of the data that Government exchange with others.\n",
        "subpages":[
            {
                "title":"Getting Started2",
                "markdown": "# Getting Started2\n\nThe Definitions Catalogue contains the definitions of the data that Government exchange with others.\n"
            }
        ]
    }
  ]
}
        """)

    @Test
    fun testing(){
        var repository = ServiceDescriptionRepository(mock)
        var serviceDescription = repository.get("definitions-catalogue")
        Assert.assertNotNull(serviceDescription)
        Assert.assertEquals("Definitions Catalogue", serviceDescription!!.name)
        Assert.assertEquals(1, serviceDescription!!.subpages.size)
        Assert.assertEquals("Getting Started", serviceDescription!!.subpages[0].title)
        Assert.assertEquals(1, serviceDescription!!.subpages[0].subpages!!.size)
        Assert.assertEquals("Getting Started2", serviceDescription!!.subpages!![0].subpages!![0].title)
    }

}