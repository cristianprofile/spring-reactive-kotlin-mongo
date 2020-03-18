package com.example.cromero

import mu.KotlinLogging
import org.jeasy.random.EasyRandom
import org.springframework.cloud.sleuth.annotation.NewSpan
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toFlux
import reactor.kotlin.core.publisher.toMono
import reactor.kotlin.extra.bool.not

interface PizzaRepository {
    fun getPizzas() : Flux<Pizza>
    fun getPizza(id:Long) : Mono<Pizza>
    fun findPizzaByName(name: String): Mono<Pizza>
    fun getPizzaByName(name: String): Mono<Pizza>
    fun existPizzaByName(name: String): Mono<Boolean>
    fun savePizza(pizza: Pizza): Mono<Pizza>
    fun notExistPizzaByName(name: String): Mono<Boolean>
    fun notExistPizzaByDescription(description: String): Mono<Boolean>
}

@Repository
class PizzaRepositoryImpl(easyRandom: EasyRandom): PizzaRepository {

    private val logger = KotlinLogging.logger {}

    private val pizzaList = mutableListOf(easyRandom.nextObject(Pizza::class.java).copy(id = 1),
            easyRandom.nextObject(Pizza::class.java).copy(id = 2),
            easyRandom.nextObject(Pizza::class.java).copy(id = 3))

    override fun getPizzas(): Flux<Pizza> {
        logger.info("init repository")
        logger.info("pizzas: $pizzaList")
        logger.info("end repository")
        return pizzaList.toFlux()
    }



    @NewSpan
    override fun getPizza(id: Long): Mono<Pizza> {
        logger.info("init repository")
        pizzaList.find { it.id==id }
        val pizzaFound = pizzaList.find { pizza -> pizza.id == id }
        return pizzaFound?.let {
            logger.info("Pizza found : $it")
            pizzaFound.toMono()
        } ?: Mono.empty()
    }


    @NewSpan
    override fun getPizzaByName(name: String): Mono<Pizza> {
        val pizzaFound = pizzaList.find { pizza -> pizza.name == name }
        return pizzaFound?.let {
            logger.info("Pizza found : $it")
            pizzaFound.toMono()
        } ?: Mono.empty()
    }


    @NewSpan
    override fun existPizzaByName(name: String): Mono<Boolean> {
        return getPizzaByName(name).hasElement();
    }

    @NewSpan
    override fun notExistPizzaByName(name: String): Mono<Boolean> {
        return getPizzaByName(name).hasElement().not();
    }

    @NewSpan
    override fun notExistPizzaByDescription(description: String): Mono<Boolean> {
       return  !pizzaList.any { it.description==description }.toMono()
    }


    override fun findPizzaByName(name: String): Mono<Pizza> {
        return pizzaList.find { it.name == name }?.let { it.toMono() } ?: Mono.empty()
    }

    override fun savePizza(pizza: Pizza): Mono<Pizza> {
        return pizzaList.add(pizza).toMono().thenReturn(pizza)
    }

}