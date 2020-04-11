package com.example.cromero

import org.springframework.data.domain.Pageable
import org.springframework.data.mongodb.repository.Query
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import org.springframework.data.repository.reactive.ReactiveSortingRepository
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono


@Repository
interface PizzaRepository : ReactiveCrudRepository<Pizza, Long> {

    // see https://stackoverflow.com/questions/46384618/how-apply-pagination-in-reactive-spring-data/60812472#60812472
    @Query("{ id: { \$exists: true }}")
    fun findAll(page: Pageable): Flux<Pizza>
    fun findByIdNotNull(page: Pageable): Flux<Pizza>
    fun findByName(name: String): Mono<Pizza>
    fun existsByName(name: String): Mono<Boolean>
    fun existsByDescription(description: String): Mono<Boolean>
    fun findByDescription(description: String): Mono<Pizza>
    fun existsByDescriptionNot(description: String): Mono<Boolean>
}