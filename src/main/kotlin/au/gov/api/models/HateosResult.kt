package au.gov.api.models

data class HateosResult<T>(val content: T, val links: List<Link>)