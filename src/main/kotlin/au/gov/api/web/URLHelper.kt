package au.gov.api.web

import javax.servlet.http.HttpServletRequest

class URLHelper {

    fun getURL(request: HttpServletRequest?): String {
        if (request == null) {
            return ""
        }

        val scheme = request.scheme             // http
        val serverName = request.serverName     // hostname.com
        val serverPort = request.serverPort        // 80
        val contextPath = request.contextPath   // /mywebapp
        val servletPath = request.servletPath   // /servlet/MyServlet
        val pathInfo = request.pathInfo         // /a/b;c=123
        val queryString = request.queryString          // d=789

        // Reconstruct original requesting URL
        val url = StringBuilder()
        url.append(scheme).append("://").append(serverName)

        if (serverPort != 80 && serverPort != 443) {
            url.append(":").append(serverPort)
        }

        url.append(contextPath).append(servletPath)

        if (pathInfo != null) {
            url.append(pathInfo)
        }
        if (queryString != null) {
            url.append("?").append(queryString)
        }
        return url.toString()
    }

    fun convertURL(request: HttpServletRequest?, from: String): String {
        val requestAsURL = getURL(request)
        return _convertURL(requestAsURL, from)
    }

    /*
    Only works for urls with schemes: http://...
     */
    fun _convertURL(to: String, from: String): String {
        val toScheme = to.split("//")[0] + "//"
        val toServer = toScheme + to.removePrefix(toScheme).split("/")[0] + "/"

        val fromScheme = from.split("//")[0] + "//"
        val fromServer = fromScheme + from.removePrefix(fromScheme).split("/")[0] + "/"

        return toServer + from.removePrefix(fromServer)
    }
}