package au.gov.api.asset

import com.beust.klaxon.*
import org.springframework.stereotype.Component
import org.springframework.context.event.EventListener
import org.springframework.boot.context.event.ApplicationReadyEvent


@Component
class AssetRepository() {

    private val spaces:MutableList<Space> = mutableListOf()
    private val articles:MutableList<Article> = mutableListOf()


    @EventListener(ApplicationReadyEvent::class)
    fun insertContentOnStartup(){
        load(Space(tag = "ato",
                name = "Australian Taxation Office",
                overview = "The ATO is the government's principal revenue collection agency with a large number of API-enabled services available and is bringing more online regularly. They are also responsible for managing the superannuation system and business registrations.",
                childSpaces = listOf("super","abr","eInvoicing")
        ))
                 
        load(Article(metadata = Metadata(id = "Article1", tags = listOf("ato", "dhs")),
            title = "The Operating Framework", 
            date = "18/12/2018",
            summary = "All DSPs will need to meet the requirements of the new frameowrk. Read about them here.",
            markdown = """
The growth of our digital wholesale services increases productivity and community connectivity across the digital economy. This connectivity presents a range of service opportunities, business risks and security implications for the organisation and the Australian community.

The Digital service provider (DSP) Operational Framework is part of our response to these risks and establishes how we will provide access to and monitor the digital transfer of data through software.

Through consultation with DSPs, industry associations and across Government a position was established on each of the five issues of the DSP Operational Framework. From these positions we have developed the [Requirements for DSPs](https://softwaredevelopers.ato.gov.au/RequirementsforDSPs) to use our digital services and have updated the Operational Framework Questionnaire, this is now known as the [Digital Service Provider Operational Framework Security Questionnaire (DOCX, 826KB)](https://softwaredevelopers.ato.gov.au/sites/default/files/resource-attachments/Digital_Service_Provider_Operational_Framework_Security_Questionnaire.docx). The Questionnaire is used by DSPs to demonstrate how their product or service meets the requirements.

All DSPs wanting to use our digital services will need to meet the relevant requirements which can include, but is not limited to:

- Authentication
- Encryption
- Supply chain visibility
- Certification
- Data hosting
- Personnel security
- Encryption key management
- Security monitoring practices

A transition period has been established for DSPs who are already using our digital services to allow them time to meet the requirements. After consulting through the DSP Operational Framework working group, timeframes for [meeting the DSP requirements](https://softwaredevelopers.ato.gov.au/meeting_dsp_requirements) have been finalised.

            """

            )
        )

        load(Space(tag = "eInvoicing",
                name = "eInvoicing",
                overview = "Overview"
        ))
        load(Space(tag = "super",
                name = "Superannuation",
                overview = "Overview"
        ))
        load(Space(tag = "insolvency",
                name = "Australian Financial Security Authority",
                overview = "Overview of AFSA."
        ))
        load(Space(tag = "anzsic",
                name = "ABS",
                overview = "Overview of ABS."
        ))

        load(Space(tag = "afsa",
                name = "Australian Financial Security Authority",
                overview = "Overview of AFSA."
        ))
        load(Space(tag = "abs",
                name = "Australian Bureau of Statistics",
                overview = "Overview of ABS.",
                childSpaces = listOf("anzsic")
        ))
        load(Space(tag = "wofg",
                name = "Whole of Government",
                overview = "Overview of whole of government",
                childSpaces = listOf("apigovau")
        ))
                 
        load(Space(tag = "apigovau",
                name = "api.gov.au",
                overview = "Overview of api.gov.au"
        ))
                 
    }


	fun getArticles():List<Article> = articles

    fun getArticlesForTags(tags:List<String>): List<Article> {
        val allTags:MutableList<String> = mutableListOf()
        tags.forEach { allTags.addAll( getTreeTags(it) )  }
        return articles.filter { it.metadata.matchesTags(allTags) }
    }


    fun getArticlesForSpace(space:Space): List<Article>{
        return listOf()
    }

    fun getTreeTags(id:String):List<String> {
        // This isn't recursive. One up and One down only
        val tagList:MutableList<String> = mutableListOf()
        parentsOfSpace(id).forEach { tagList.add( it ) }

        val thisSpace = getSpace(id)!!
        tagList.add(thisSpace.tag)

        thisSpace.childSpaces.forEach { 
            val childSpace = getSpace(it)
            if(childSpace != null)tagList.add( childSpace.tag  ) 
        }
        return tagList 
    }

    fun load(space:Space) = this.spaces.add(space)
    fun load(article:Article) = this.articles.add(article)

    fun getSpace(id:String) = spaces.firstOrNull { it.tag == id }
    fun getArticle(id:String) = articles.first { it.metadata.id == id }


    fun parentsOfSpace(space:String):List<String> = spaces.filter { it.childSpaces.contains(space) }.map{ it.tag }

}
