package au.gov

import au.gov.dxa.URIFetcher

class MockURIFetcher:URIFetcher {

    var map = mutableMapOf<String, String>()

    override fun fetch(uri: String): URIFetcher.Result {
        return URIFetcher.Result(200, map[uri]!!)
    }
}