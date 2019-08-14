package au.gov.api.services.test

import au.gov.api.models.Article
import au.gov.api.models.Metadata
import au.gov.api.repositories.AssetRepository
import au.gov.api.services.AssetService
import org.junit.Assert
import org.junit.Test

class ArticleTests {

    @Test
    fun can_get_articles_from_repo(){
        val assetService = AssetService(AssetRepository())

        val evens = listOf("2","4")
        val odds = listOf("1","3")

        val article1 = Article(metadata = Metadata(id = "article1", tags = evens))
        val article2 = Article(metadata = Metadata(id = "article2", tags = odds))

        assetService.upsertArticle(article1)
        assetService.upsertArticle(article2)

        val theArticle1 = assetService.getArticle(article1.metadata.id)
        val theArticle2 = assetService.getArticle(article2.metadata.id)

        Assert.assertEquals(article1, theArticle1)
        Assert.assertEquals(article2, theArticle2)
    }
}
