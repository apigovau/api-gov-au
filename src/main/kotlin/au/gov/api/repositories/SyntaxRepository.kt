package au.gov.api.repositories

import au.gov.api.config.Config
import au.gov.api.models.Syntax
import au.gov.api.web.NaiveAPICaller
import au.gov.api.web.ResourceCache
import com.beust.klaxon.Klaxon
import org.springframework.stereotype.Component

@Component
class SyntaxRepository : ISyntaxRepository {

    private val baseUri = Config.get("BaseRepoURI")
    private val syntaxes = ResourceCache(NaiveAPICaller(), 5, convert = { serial -> Klaxon().parse<Syntax>(serial)!! })

    override fun getSyntax(identifier: String): Syntax? = syntaxes.get(baseUri + "definitions/syntax?id=$identifier")
}