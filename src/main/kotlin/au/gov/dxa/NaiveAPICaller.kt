package au.gov.dxa

import java.net.URL

class NaiveAPICaller:URIFetcher {
    override fun fetch(uri: String): URIFetcher.Result {
        return URIFetcher.Result(200, URL(uri).readText())
    }
}