package au.gov.api

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.context.annotation.Bean
import org.springframework.web.servlet.config.annotation.CorsRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter

@SpringBootApplication
open class Application {
    companion object {
        @JvmStatic fun main(args: Array<String>) {
            SpringApplication.run(Application::class.java, *args)
        }

        @Bean
        fun corsConfigurer(): WebMvcConfigurer {
            return object : WebMvcConfigurerAdapter() {
                override fun addCorsMappings(registry: CorsRegistry?) {
                    registry!!.addMapping("/swagger/**").allowedOrigins("*")
                    registry!!.addMapping("/artefacts/**").allowedOrigins("*")
                }
            }
        }

    }

}
