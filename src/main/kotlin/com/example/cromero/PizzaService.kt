package com.example.cromero

import com.example.cromero.dto.PizzaCreate
import com.example.cromero.dto.PizzaOut
import mu.KotlinLogging
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toMono
import reactor.kotlin.extra.bool.logicalOr

interface PizzaService {
    fun addPizza(pizza: PizzaCreate): Mono<PizzaOut>
    fun findAll(): Flux<PizzaOut>
    fun getPizza(id: Long): Mono<PizzaOut>
    fun deletePizza(id: Long): Mono<Void>
    fun findAllPaginated(pageable: Pageable): Flux<PizzaOut>
}

@Service
class PizzaServiceImpl(val pizzaRepository: PizzaRepository) : PizzaService {

    private val logger = KotlinLogging.logger {}

    @Transactional
    override fun findAll(): Flux<PizzaOut> {
        return pizzaRepository.findAll()
                .doOnNext { pizza -> logger.info("Pizza Found $pizza") }
                .map { it.convertToPizzaOut() }
    }

    @Transactional
    override fun getPizza(id: Long): Mono<PizzaOut> {
        return pizzaRepository.findById(id)
                .doOnNext { pizza -> logger.info("Pizza Found $pizza") }
                .map { it.convertToPizzaOut() }
                .switchIfEmpty(PizzaDoesntExistException(id).toMono())
    }

    @Transactional
    override fun findAllPaginated(pageable: Pageable): Flux<PizzaOut> {
        return pizzaRepository.findByIdNotNull(pageable).map { it.convertToPizzaOut() }
    }

    @Transactional
    override fun addPizza(pizza: PizzaCreate): Mono<PizzaOut> {
        return pizzaRepository.existsByName(pizza.name).logicalOr(pizzaRepository.existsByDescription(pizza.description))
                .filter {!it}
                .map { pizza.convertToPizza() }
                .flatMap {
                    pizzaRepository.save(it)
                }
                .map { it.convertToPizzaOut() }
                .switchIfEmpty(PizzaDuplicatedException(pizza.name).toMono())
    }

    @Transactional
    override fun deletePizza(id: Long): Mono<Void> {
        return pizzaRepository.deleteById(id)
    }

}