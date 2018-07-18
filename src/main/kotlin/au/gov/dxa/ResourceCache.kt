package au.gov.dxa

import java.time.LocalDateTime

class ResourceCache<T>(private val fetcher:URIFetcher, private val minutesToLive:Long, private val convert: (serial:String) -> T) {
    data class CacheItem<T>(var timestamp:LocalDateTime, var item:T)
    private var _cache = mutableMapOf<String, CacheItem<T>>()

    enum class LastOperationStatus {HIT,MISS,EXPIRED}
    var lastOperationStatus = LastOperationStatus.MISS

    fun expire(url:String){
        if(_cache.containsKey(url)) _cache[url]!!.timestamp = LocalDateTime.MIN
    }

    fun get(url:String):T{
        if(! _cache.containsKey(url)) return cacheMiss(url)

        val cacheItem = _cache[url]!!
        if(cacheItem.timestamp > LocalDateTime.now()) return cacheHit(url)

        try{
            return cacheMiss(url)
        }catch(e:Exception){
            return expiredCacheItem(url)
        }
    }


    private inline fun cacheMiss(url:String):T{
        _cache[url] = CacheItem(LocalDateTime.now().plusMinutes(minutesToLive), fetch(url))
        lastOperationStatus = LastOperationStatus.MISS
        return _cache[url]!!.item

    }

    private fun cacheHit(url:String):T{
        lastOperationStatus = LastOperationStatus.HIT
        return _cache[url]!!.item
    }

    private fun expiredCacheItem(url:String):T{
        lastOperationStatus = LastOperationStatus.EXPIRED
        return _cache[url]!!.item
    }

    private fun fetch(url:String):T = convert(fetcher.fetch(url).response)


}