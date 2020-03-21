package com.example.cromero

import mu.KotlinLogging
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
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
class PizzaServiceImpl(val pizzaRepository: PizzaRepository) : PizzaService {

    private val logger = KotlinLogging.logger {}

    @Transactional
    override fun getPizzas() = pizzaRepository.findAll().doOnNext { pizza -> logger.info("Pizza Found $pizza") }

    @Transactional
    override fun getPizza(id: Long): Mono<Pizza> {
        return pizzaRepository.findById(id)
                .doOnNext { pizza -> logger.info("Pizza Found $pizza") }
                .switchIfEmpty(PizzaDoesntExistException(id).toMono())
    }

    @Transactional
    override fun addPizza(pizza: Pizza): Mono<Pizza> {
        return pizzaRepository.existsByNameNot(pizza.name)
                .flatMap {
                    pizzaRepository.save(pizza)
                }
                .switchIfEmpty(PizzaDuplicatedException(pizza.name).toMono())
    }

    @Transactional
    override fun deletePizza(id: Long): Mono<Void> {
        return pizzaRepository.deleteById(id)
    }

}