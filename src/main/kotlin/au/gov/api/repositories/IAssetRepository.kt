package au.gov.api.repositories

import au.gov.api.models.Article
import au.gov.api.models.Space

interface IAssetRepository {
    fun getArticles(): List<Article>
    fun getArticlesForTags(tags: List<String>): List<Article>
    fun getArticlesForSpace(space: Space): List<Article>
    fun getTreeTags(id: String): List<String>
    fun upsertSpace(space: Space)
    fun upsertArticle(article: Article)
    fun getSpace(id: String): Space?
    fun getArticle(id: String): Article
    fun parentsOfSpace(space: String): List<String>
}