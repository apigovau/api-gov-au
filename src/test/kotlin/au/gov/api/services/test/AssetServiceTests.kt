package au.gov.api.services.test

import au.gov.api.models.Article
import au.gov.api.models.ArticleMetadata
import au.gov.api.models.Space
import au.gov.api.repositories.AssetRepository
import au.gov.api.services.AssetService
import org.junit.Assert
import org.junit.Test

class AssetServiceTests {

    @Test
    fun can_get_articles_from_repo(){
        val assetService = AssetService(AssetRepository())

        val evens = listOf("2","4")
        val odds = listOf("1","3")

        val article1 = Article(articleMetadata = ArticleMetadata(id = "article1", tags = evens))
        val article2 = Article(articleMetadata = ArticleMetadata(id = "article2", tags = odds))

        assetService.upsertArticle(article1)
        assetService.upsertArticle(article2)

        val theArticle1 = assetService.getArticle(article1.articleMetadata.id)
        val theArticle2 = assetService.getArticle(article2.articleMetadata.id)

        Assert.assertEquals(article1, theArticle1)
        Assert.assertEquals(article2, theArticle2)
    }

    @Test
    fun can_get_space_from_service(){
        val space1 = Space(tag = "space1" )
        val space2 = Space(tag = "space2" )

        val assetService = AssetService(AssetRepository())

        assetService.upsertSpace(space1)
        assetService.upsertSpace(space2)

        val theSpace1 = assetService.getSpace(space1.tag)
        Assert.assertEquals(space1, theSpace1)

        val theSpace2 = assetService.getSpace(space2.tag)
        Assert.assertEquals(space2, theSpace2)
    }

    @Test
    fun test_space_parent_and_child_links(){
        val spaceA = Space(tag = "spaceA", childSpaces = listOf("space_A", "space_AB"))
        val spaceB = Space(tag = "spaceB", childSpaces = listOf("space_B", "space_AB"))

        val space_AB = Space(tag = "space_AB" )
        val space_A = Space(tag = "space_A" )
        val space_B = Space(tag = "space_B" )

        val assetService = AssetService(AssetRepository())

        assetService.upsertSpace(spaceA)
        assetService.upsertSpace(spaceB)
        assetService.upsertSpace(space_AB)
        assetService.upsertSpace(space_A)
        assetService.upsertSpace(space_B)

        Assert.assertEquals(listOf("spaceA"), assetService.parentsOfSpace("space_A"))
        Assert.assertEquals(listOf("spaceB"), assetService.parentsOfSpace("space_B"))
        Assert.assertEquals(listOf("spaceA", "spaceB"), assetService.parentsOfSpace("space_AB"))
    }

    @Test
    fun test_get_tags_at_different_heriarchy_points(){
        val spaceA = Space(tag = "spaceA", childSpaces = listOf("space_A", "space_AB"))
        val spaceB = Space(tag = "spaceB", childSpaces = listOf("space_B", "space_AB"))

        val space_AB = Space(tag = "space_AB")
        val space_A = Space(tag = "space_A")
        val space_B = Space(tag = "space_B")

        val assetService = AssetService(AssetRepository())

        assetService.upsertSpace(spaceA)
        assetService.upsertSpace(spaceB)
        assetService.upsertSpace(space_AB)
        assetService.upsertSpace(space_A)
        assetService.upsertSpace(space_B)

        Assert.assertEquals(listOf("spaceA", "space_A", "space_AB"), assetService.getTreeTags("spaceA"))
        Assert.assertEquals(listOf("spaceB", "space_B", "space_AB"), assetService.getTreeTags("spaceB"))
        Assert.assertEquals(listOf("spaceA", "space_A"), assetService.getTreeTags("space_A"))
        Assert.assertEquals(listOf("spaceB", "space_B"), assetService.getTreeTags("space_B"))
        Assert.assertEquals(listOf("spaceA", "spaceB", "space_AB"), assetService.getTreeTags("space_AB"))
    }
}
