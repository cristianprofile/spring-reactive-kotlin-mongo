package com.example.cromero

import com.example.cromero.dto.PizzaCreate
import mu.KotlinLogging
import org.springframework.data.domain.Pageable
import org.springframework.data.web.PageableDefault
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
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
    fun getPizzas() = pizzaService.getPizzas()


    @GetMapping("/paginated")
    fun getPizzasPaginated(@PageableDefault(size = 2, page =0) pageable: Pageable) = pizzaService.findAll(pageable)

    @GetMapping("{id}")
    fun getPizza(@PathVariable id: Long) = pizzaService.getPizza(id)
    

    @PostMapping
    fun post(@RequestBody pizza: PizzaCreate) = pizzaService.addPizza(pizza)

}

