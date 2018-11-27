package au.gov.web

import au.gov.api.web.URIFetcher

class MockURIFetcher: URIFetcher {

    var map = mutableMapOf<String, String>()

    override fun fetch(uri: String): URIFetcher.Result {
        return URIFetcher.Result(200, map[uri]!!)
    }
}
