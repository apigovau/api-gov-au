package au.gov.api.repositories

import au.gov.api.models.Article
import au.gov.api.models.ArticleMetadata
import au.gov.api.models.Space
import au.gov.api.repositories.dto.ArticleDTO
import au.gov.api.repositories.processors.PageProcessor
import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component

@Component
class AssetRepository : IAssetRepository {

    private val spaces:MutableList<Space> = mutableListOf()
    private val articles:MutableList<ArticleDTO> = mutableListOf()

    //TODO Check still works after making private
    @EventListener(ApplicationReadyEvent::class)
    private fun insertContentOnStartup(){
        upsertSpace(Space(tag = "ato",
                name = "Australian Taxation Office",
                overview = "The ATO is the government's principal revenue collection agency with a large number of API-enabled services available and is bringing more online regularly. They are also responsible for managing the superannuation system and business registrations.",
                childSpaces = listOf("super","abr")
        ))
                 
        upsertArticle(Article(
            ArticleMetadata(
                id = "Article1",
                tags = listOf("ato", "dhs")),
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

        upsertSpace(Space(tag = "super",
                name = "Superannuation",
                overview = "Overview"
        ))

        upsertSpace(Space(tag = "insolvency",
                name = "Australian Financial Security Authority",
                overview = "Overview of AFSA."
        ))
        upsertSpace(Space(tag = "anzsic",
                name = "ABS",
                overview = "Overview of ABS.",
                logoURL = "https://www.abs.gov.au/ausstats/wmdata.nsf/activeotherresource/ABS_Logo_333/\$File/ABS_Logo_333.svg"
        ))

        upsertSpace(Space(tag = "abr",
                name = "Australian Business Register",
                overview = "The Australian Business Register (ABR) provides access to publicly available information supplied by businesses when they register for an Australian Business Number (ABN)."
        ))

        upsertSpace(Space(tag = "afsa",
                name = "Australian Financial Security Authority",
                overview = "Overview of AFSA."
        ))

        upsertSpace(Space(tag = "abs",
                name = "Australian Bureau of Statistics",
                overview = "Overview of ABS.",
                logoURL = "https://www.abs.gov.au/ausstats/wmdata.nsf/activeotherresource/ABS_Logo_333/\$File/ABS_Logo_333.svg",
                childSpaces = listOf("anzsic")
        ))

        upsertSpace(Space(tag = "wofg",
                name = "Whole of Government",
                overview = "Overview of whole of government",
                childSpaces = listOf("apigovau")
        ))
                 
        upsertSpace(Space(tag = "apigovau",
                name = "api.gov.au",
                overview = "Overview of api.gov.au"
        ))
    }

	override fun getArticles():List<Article> = articles.map { convertToArticle(it) }

    override fun getArticlesForTags(tags:List<String>): List<Article> {
        val allTags:MutableList<String> = mutableListOf()
        tags.forEach { allTags.addAll( getTreeTags(it) )  }
        return articles.filter { matchesTags(it.tags, allTags) }
                .map { convertToArticle(it) }
    }

    override fun getArticlesForSpace(space:Space): List<Article>{
        return articles.filter { it.tags.contains(space.tag) }
                .map { convertToArticle(it) }
    }

    override fun getTreeTags(id:String):List<String> {
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

    override fun upsertSpace(space:Space) {
        this.spaces.removeAll { it.tag == space.tag }
        this.spaces.add(space)
    }

    override fun upsertArticle(article:Article) {
        this.articles.removeAll { it.id == article.articleMetadata.id }
        this.articles.add(convertFromArticle(article))
    }

    override fun getSpace(id:String) = spaces.firstOrNull { it.tag == id }

    override fun getArticle(id:String) = convertToArticle(articles.first { it.id == id })

    override fun parentsOfSpace(space:String):List<String> = spaces.filter { it.childSpaces.contains(space) }.map{ it.tag }

    private fun convertToArticle(dto: ArticleDTO): Article = Article(ArticleMetadata(dto.id, dto.tags), dto.title, dto.date, dto.summary, dto.markdown, if (dto.markdown.isNotEmpty()) PageProcessor.processPage(dto.markdown) else null)

    private fun convertFromArticle(article: Article): ArticleDTO = ArticleDTO(article.articleMetadata.id, article.articleMetadata.tags, article.title, article.date, article.markdown)

    private fun matchesTags(tags:List<String>, otherTags:List<String>) = tags.intersect(otherTags).isNotEmpty()
}
