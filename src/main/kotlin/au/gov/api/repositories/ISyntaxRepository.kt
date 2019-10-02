package au.gov.api.repositories

import au.gov.api.models.Syntax

interface ISyntaxRepository {
    fun getSyntax(identifier: String): Syntax?
}