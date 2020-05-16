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
    fun findAllPaginatedToList(pageable: Pageable): Mono<MutableList<PizzaOut>>
    fun findAllPaginatedToSortedList(pageable: Pageable): Mono<MutableList<PizzaOut>>
    fun findAllToMapByName(): Mono<MutableMap<String, PizzaOut>>
    fun allPizzaExpensive(): Mono<Boolean>
    fun existAnyPizzaFree(): Mono<Boolean>
    @Transactional
    fun findAllNotExistWithDescriptionUsingWhenOperator(s: String): Flux<PizzaOut>
    @Transactional
    fun addPizzaReturnMonoBoolean(pizza: PizzaCreate): Mono<Boolean>
    @Transactional
    fun addPizzaReturnMonoVoid(pizza: PizzaCreate): Mono<Void>
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
    override fun findAllPaginatedToList(pageable: Pageable): Mono<MutableList<PizzaOut>> {
        return pizzaRepository.findByIdNotNull(pageable).map { it.convertToPizzaOut() }.collectList()
    }

    @Transactional
    override fun findAllPaginatedToSortedList(pageable: Pageable): Mono<MutableList<PizzaOut>> {
        return pizzaRepository.findByIdNotNull(pageable).map { it.convertToPizzaOut() }.collectSortedList { o1, o2 -> o1.name.compareTo(o2.name) }
    }

    @Transactional
    override fun findAllToMapByName(): Mono<MutableMap<String, PizzaOut>> {
        return pizzaRepository.findAll().map { it.convertToPizzaOut() }.collectMap { it.name }
    }

    @Transactional
    override fun allPizzaExpensive(): Mono<Boolean> {
        return pizzaRepository.findAll().map { it.convertToPizzaOut() }.all { pizza->pizza.price>1000 }
    }

    @Transactional
    override fun existAnyPizzaFree(): Mono<Boolean> {
        return pizzaRepository.findAll().map { it.convertToPizzaOut() }.any { pizza->pizza.price==0 }
    }

    @Transactional
    override fun findAllNotExistWithDescriptionUsingWhenOperator(description: String): Flux<PizzaOut> {
        return pizzaRepository.findAll().filterWhen { it: Pizza? -> pizzaRepository.existsByDescriptionNot(description)}
                .map { it.convertToPizzaOut() }
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
    override fun addPizzaReturnMonoBoolean(pizza: PizzaCreate): Mono<Boolean> {
        return addPizza(pizza).hasElement()
    }

    @Transactional
    // we don't want to return information of the operation ->only that operation ends ok
    override fun addPizzaReturnMonoVoid(pizza: PizzaCreate): Mono<Void> {
        return addPizza(pizza).then()
    }


    @Transactional
    override fun deletePizza(id: Long): Mono<Void> {
        return pizzaRepository.deleteById(id)
    }

}