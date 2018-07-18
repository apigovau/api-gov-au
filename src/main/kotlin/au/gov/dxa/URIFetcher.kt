package au.gov.dxa

interface URIFetcher {

    data class Result(val status:Int, val response:String)
    fun fetch(uri:String):Result
}