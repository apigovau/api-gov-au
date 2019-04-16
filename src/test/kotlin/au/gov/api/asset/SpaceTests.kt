package au.gov.api.test.asset

import au.gov.api.asset.Article
import au.gov.api.asset.Metadata
import au.gov.api.asset.Space
import au.gov.api.asset.AssetService
import au.gov.api.asset.AssetRepository
import org.junit.Assert
import org.junit.Test

class SpaceTests {
    @Test
    fun can_get_space_from_service(){
        val space1 = Space(tag = "space1" )
        val space2 = Space(tag = "space2" )

        val assetRepo = AssetRepository()
        val assetService = AssetService(assetRepo)

        assetRepo.load(space1)
        assetRepo.load(space2)

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

        val assetRepo = AssetRepository()
        val assetService = AssetService(assetRepo)

        assetRepo.load(spaceA)
        assetRepo.load(spaceB)
        assetRepo.load(space_AB)
        assetRepo.load(space_A)
        assetRepo.load(space_B)


        Assert.assertEquals(listOf("spaceA"), assetRepo.parentsOfSpace("space_A"))
        Assert.assertEquals(listOf("spaceB"), assetRepo.parentsOfSpace("space_B"))
        Assert.assertEquals(listOf("spaceA", "spaceB"), assetRepo.parentsOfSpace("space_AB"))

    }

    @Test
    fun test_get_tags_at_different_heriarchy_points(){
        val spaceA = Space(tag = "spaceA", childSpaces = listOf("space_A", "space_AB"))
        val spaceB = Space(tag = "spaceB", childSpaces = listOf("space_B", "space_AB"))

        val space_AB = Space(tag = "space_AB")
        val space_A = Space(tag = "space_A")
        val space_B = Space(tag = "space_B")

        val assetRepo = AssetRepository()
        val assetService = AssetService(assetRepo)

        assetRepo.load(spaceA)
        assetRepo.load(spaceB)
        assetRepo.load(space_AB)
        assetRepo.load(space_A)
        assetRepo.load(space_B)


        Assert.assertEquals(listOf("spaceA", "space_A", "space_AB"), assetRepo.getTreeTags("spaceA"))
        Assert.assertEquals(listOf("spaceB", "space_B", "space_AB"), assetRepo.getTreeTags("spaceB"))
        Assert.assertEquals(listOf("spaceA", "space_A"), assetRepo.getTreeTags("space_A"))
        Assert.assertEquals(listOf("spaceB", "space_B"), assetRepo.getTreeTags("space_B"))
        Assert.assertEquals(listOf("spaceA", "spaceB", "space_AB"), assetRepo.getTreeTags("space_AB"))

    }

}
