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
            title = "SAMPLE: The Operating Framework is coming", 
            date = "01/01/1970",
            summary = "Here are the things you need to be doing now to remain compliant")
        )
        load(Article(metadata = Metadata(id = "Article2", tags = listOf("eInvoicing")),
            title = "SAMPLE: eInvoicing information sessions", 
            date = "02/01/1970",
            summary = "Learn about what employers will need to do, and what your software can do to help them")
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
                 
                 
                 
        load(Space("stp",
                name = "Single Touch Payroll",
                overview = "Single Touch Payroll (STP) changes the way employers report their employees' tax and super information to us."))

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
