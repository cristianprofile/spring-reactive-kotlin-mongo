package com.example.cromero

import mu.KotlinLogging
import org.springframework.cloud.sleuth.annotation.NewSpan
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toMono
import reactor.kotlin.extra.bool.logicalAnd

interface PizzaService
{
    fun getPizzas() : Flux<Pizza>
    fun getPizzaNonReactive(id:Long) : Mono<Pizza>
    fun getPizzasReactive(): Flux<Pizza>
    fun addPizza(pizza: Pizza): Mono<Pizza>
}

@Service
class PizzaServiceImpl(val pizzaRepository: PizzaRepository,val webClient: WebClient):PizzaService
{

    private val logger = KotlinLogging.logger {}

    @NewSpan
    override fun getPizzas()=pizzaRepository.getPizzas().doOnNext { pizza-> logger.info("Pizza Found $pizza") }

    @NewSpan
    override fun getPizzaNonReactive(id: Long): Mono<Pizza> = pizzaRepository.getPizza(id)
            .doOnNext { pizza -> logger.info("Pizza Found $pizza") }
            .switchIfEmpty(PizzaDoesntExistException(id).toMono())

    @NewSpan
    override fun getPizzasReactive(): Flux<Pizza> {
        logger.info { "pizza reactive controller" }
        return webClient.get().uri("/pizza").retrieve().bodyToFlux(Pizza::class.java)
    }

    @NewSpan
    override fun addPizza(pizza: Pizza): Mono<Pizza> {
        return pizzaRepository.getPizza(pizza.id)
                .flatMap {
                    pizzaRepository.notExistPizzaByName(pizza.name).logicalAnd(pizzaRepository.notExistPizzaByDescription(pizza.description))
                            .filter { it }
                            .then(pizzaRepository.savePizza(pizza))
                            .switchIfEmpty(PizzaDuplicatedException(pizza.name).toMono())
                }
                .switchIfEmpty(PizzaDoesntExistException(pizza.id).toMono())
    }

}