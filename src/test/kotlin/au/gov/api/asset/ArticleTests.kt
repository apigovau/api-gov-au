package au.gov.api.test.asset

import au.gov.api.asset.Article
import au.gov.api.asset.Metadata
import au.gov.api.asset.AssetService
import au.gov.api.asset.AssetRepository
import org.junit.Assert
import org.junit.Test

class ArticleTests {
    @Test
    fun can_match_metadata_item_to_tags(){
        val oneToFour = listOf("1","2","3","4")
        val evens = listOf("2","4")
        val odds = listOf("1","3")

        Assert.assertEquals(evens, oneToFour.intersect(evens).toList())

        val evenMetadata = Metadata(tags = evens)
        val oddMetadata = Metadata(tags = odds)

        Assert.assertEquals(true, evenMetadata.matchesTags(oneToFour))
        Assert.assertEquals(true, evenMetadata.matchesTags(evens))
        Assert.assertEquals(false, evenMetadata.matchesTags(odds))

        Assert.assertEquals(true, oddMetadata.matchesTags(oneToFour))
        Assert.assertEquals(true, oddMetadata.matchesTags(odds))
        Assert.assertEquals(false, oddMetadata.matchesTags(evens))
    }



    @Test
    fun can_get_articles_from_repo(){
        val oneToFour = listOf("1","2","3","4")
        val evens = listOf("2","4")
        val odds = listOf("1","3")

        val article1 = Article(metadata = Metadata(id = "article1", tags = evens)) 
        val article2 = Article(metadata = Metadata(id = "article2", tags = odds)) 


        val assetRepo = AssetRepository()
        val assetService = AssetService(assetRepo)

        assetRepo.load(article1)
        assetRepo.load(article2)

        val theArticle1 = assetService.getArticle(article1.metadata.id)
        val theArticle2 = assetService.getArticle(article2.metadata.id)


        Assert.assertEquals(article1, theArticle1)
        Assert.assertEquals(article2, theArticle2)

    }

}
