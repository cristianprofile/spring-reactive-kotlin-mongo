package com.example.cromero

import org.springframework.data.domain.PageRequest
import com.example.cromero.dto.PizzaCreate
import com.example.cromero.dto.PizzaOut
import mu.KotlinLogging
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toMono

@RestController
@RequestMapping("/pizza")
class PizzaController(val pizzaService: PizzaService) {

    private val logger = KotlinLogging.logger {}

    @ExceptionHandler(PizzaDoesntExistException::class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    fun handlePizzaNotFound(ex: PizzaDoesntExistException): Mono<String> {
        logger.error("Pizza Error Handler $ex")
        return "Pizza not found".toMono()
    }


    @GetMapping
    fun getPizzasPaginated(@RequestParam(value = "page")  page:Int?, @RequestParam(value = "count") size:Int?): Flux<PizzaOut> {
        return if (page!=null && size!=null)
        {
            val pageable=PageRequest.of(page, size)
            logger.info { "Page request: $pageable"}
            pizzaService.findAllPaginated(pageable)
        }
        else
        {
            pizzaService.findAll()
        }
    }


    @GetMapping("{id}")
    fun getPizza(@PathVariable id: Long) = pizzaService.getPizza(id)


    @PostMapping
    fun post(@RequestBody pizza: PizzaCreate) = pizzaService.addPizza(pizza)

}



