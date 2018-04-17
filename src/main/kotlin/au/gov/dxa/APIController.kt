
package au.gov.dxa

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
class APIController {

    @GetMapping("/feedback")
    fun feedback(@RequestParam path:String, @RequestParam score:Int){
        System.out.println("Page: '${path}' got score: ${score}")
    }
}
