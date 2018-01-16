package au.gov.dxa

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import javax.servlet.http.HttpServletRequest
import org.springframework.web.bind.annotation.ModelAttribute


@Controller
class Controller {



    @RequestMapping("/")
    fun searchSubmit(): String  = "search"

}
