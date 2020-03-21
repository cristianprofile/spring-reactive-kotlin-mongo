package com.example.cromero

import mu.KotlinLogging
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toMono
import reactor.kotlin.extra.bool.logicalAnd

interface PizzaService {
    fun addPizza(pizza: Pizza): Mono<Pizza>
    fun getPizzas(): Flux<Pizza>
    fun getPizza(id: Long): Mono<Pizza>
    fun deletePizza(id: Long): Mono<Void>
}

@Service
class PizzaServiceImpl(val pizzaRepository: PizzaRepository, val webClient: WebClient) : PizzaService {

    private val logger = KotlinLogging.logger {}

    override fun getPizzas() = pizzaRepository.findAll().doOnNext { pizza -> logger.info("Pizza Found $pizza") }

    override fun getPizza(id: Long): Mono<Pizza> {
        return pizzaRepository.findById(id)
                .doOnNext { pizza -> logger.info("Pizza Found $pizza") }
                .switchIfEmpty(PizzaDoesntExistException(id).toMono())
    }

    override fun addPizza(pizza: Pizza): Mono<Pizza> {
        return getPizza(pizza.id)
                .flatMap {
                    pizzaRepository.existsByNameNot(pizza.name).logicalAnd(pizzaRepository.existsByDescriptionNot(pizza.description))
                            .filter { it }
                            .then(pizzaRepository.save(pizza))
                            .switchIfEmpty(PizzaDuplicatedException(pizza.name).toMono())
                }
                .switchIfEmpty(PizzaDoesntExistException(pizza.id).toMono())
    }

    override fun deletePizza(id: Long): Mono<Void> {
        return pizzaRepository.deleteById(id)
    }

}