package com.example.cromero

import mu.KotlinLogging
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toMono


@RestController
@RequestMapping("/pizza")
class PizzaController (val pizzaService: PizzaService) {

    private val logger = KotlinLogging.logger {}

    @ExceptionHandler(PizzaDoesntExistException::class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    fun handlePizzaNotFound(ex: PizzaDoesntExistException): Mono<String> {
        logger.error("Pizza Error Handler $ex")
        return "Pizza not found".toMono()
    }

    @GetMapping("{id}")
    fun getPizza(@PathVariable id: Long) = pizzaService.getPizzaNonReactive(id)

    @GetMapping
    fun getPizzas()= pizzaService.getPizzas()

    @GetMapping("/reactive")
    fun getPizzasReactive()= pizzaService.getPizzasReactive()


    @PostMapping
    fun post(@RequestBody pizza: Pizza)= pizzaService.addPizza(pizza)


}

