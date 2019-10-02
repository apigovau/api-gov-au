package au.gov.api.services

import au.gov.api.models.Article
import au.gov.api.models.Space
import au.gov.api.repositories.IAssetRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class AssetService @Autowired constructor(private val repo: IAssetRepository) {

    fun getArticles(): List<Article> = repo.getArticles()

    fun getArticlesForTags(tags:List<String>): List<Article> = repo.getArticlesForTags(tags)

    fun getArticlesForSpace(space: Space): List<Article> = repo.getArticlesForTags( repo.getTreeTags(space.tag) )

    fun getSpace(id:String):Space? = repo.getSpace(id)

    fun upsertSpace(space:Space) = repo.upsertSpace(space)

    fun getArticle(id:String):Article = repo.getArticle(id)

    fun upsertArticle(article: Article) = repo.upsertArticle(article)

    fun getTreeTags(id:String):List<String> = repo.getTreeTags(id)

    fun parentsOfSpace(space:String):List<String> = repo.parentsOfSpace(space)

    fun spaceHasParents(space:String):Boolean = parentsOfSpace(space).isNotEmpty()
}

