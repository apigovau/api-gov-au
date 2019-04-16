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
                childSpaces = listOf("stp","tr"),
                name = "Australian Taxation Office",
                overview = "The ATO is the government's principal revenue collection agency with a large number of API-enabled services available and is bringing more online regularly. They are also responsible for managing the superannuation system and business registrations."
        ))
                 
        load(Article(metadata = Metadata(id = "Article1", tags = listOf("ato", "dhs")),
            title = "The Operating Framework is coming", 
            date = "01/01/1970",
            summary = "Here are the things you need to be doing now to remain compliant")
        )
        load(Article(metadata = Metadata(id = "Article3", tags = listOf("tr")),
            title = "FBT Dates are changing", 
            date = "02/01/1970",
            summary = "Has your software taken into account the new FBT dates this year")
        )
        load(Article(metadata = Metadata(id = "Article2", tags = listOf("stp")),
            title = "Single Touch Payroll information sessions", 
            date = "02/01/1970",
            summary = "Learn about what employers will need to do, and what your software can do to help them")
        )



        load(Space(tag = "dhs",
                name = "Department Of Human Services",
                overview = "DHS delivers welfare services to the Australian public, with many API-enabled services that help doctors and other health providers lodge claims on behalf of their patients."
        ))
                 
        load(Article(metadata = Metadata(id = "Article3", tags = listOf("dhs")),
            title = "WPIT, and what it means for software providers", 
            date = "03/01/1970",
            summary = "WPIT is nearly live, and it's now time to start thinking about how your businesses can take part")
        )

        load(Space("tr",
                name = "Tax Returns",
                overview = "Lodge the individual and company returns here"))
        load(Space("stp",
                name = "Single Touch Payroll",
                overview = "Single Touch Payroll (STP) changes the way employers report their employees' tax and super information to us."))

    }

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

        val thisSpace = getSpace(id)
        tagList.add(thisSpace.tag)

        thisSpace.childSpaces.forEach { tagList.add( getSpace(it).tag  ) }
        return tagList 
    }

    fun load(space:Space) = this.spaces.add(space)
    fun load(article:Article) = this.articles.add(article)

    fun getSpace(id:String) = spaces.first { it.tag == id }
    fun getArticle(id:String) = articles.first { it.metadata.id == id }


    fun parentsOfSpace(space:String):List<String> = spaces.filter { it.childSpaces.contains(space) }.map{ it.tag }

}
