package com.example.cromero


import org.jeasy.random.EasyRandom
import org.jeasy.random.EasyRandomParameters
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest
import reactor.test.StepVerifier
import java.nio.charset.Charset

@DataMongoTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class PizzaServiceIntegrationTests {

    @Autowired
    private lateinit var pizzaService: PizzaService

    private lateinit var easyRandom: EasyRandom

    @BeforeAll
    fun setUp() {
        val parameters = EasyRandomParameters()
                .seed(123L)
                .objectPoolSize(100)
                .randomizationDepth(3)
                .charset(Charset.forName("UTF-8"))
                .overrideDefaultInitialization(true)
                .stringLengthRange(4, 15)
                .collectionSizeRange(1, 3)
        easyRandom = EasyRandom(parameters)
    }

    @Test
    fun `should get all pizzas (Must be 3)`() {

        val pizza = easyRandom.nextObject(Pizza::class.java).copy(id = 1)

        val addedPizza = pizzaService.addPizza(pizza).block()
        val foundPizza = pizzaService.getPizza(addedPizza!!.id)
        StepVerifier.create(foundPizza)
                .expectNext(addedPizza)
                .expectComplete()
                .verify()

    }

//    @Test
//    fun `should get pizza with id 1`() {
//
//        val pizza1 = easyRandom.nextObject(Pizza::class.java).copy(id = 1)
//
//        `when`(pizzaService.getPizza(1L))
//                .thenReturn(pizza1.toMono())
//
//        val pizza = webTestClient.get()
//                .uri("/pizza/{id}", pizza1.id)
//                .exchange()
//                .expectStatus().isOk
//                .returnResult<Pizza>().responseBody
//
//        StepVerifier.create(pizza)
//                .expectNext(pizza1)
//                .verifyComplete()
//    }
//
//
//    @Test
//    fun `should add pizza `() {
//
//        val pizza1 = easyRandom.nextObject(Pizza::class.java).copy(id = 1)
//
//        `when`(pizzaService.addPizza(pizza1))
//                .thenReturn(pizza1.toMono())
//
//        val pizza = webTestClient.post()
//                .uri("/pizza")
//                .bodyValue(pizza1)
//                .exchange()
//                .expectStatus().isOk
//                .returnResult<Pizza>().responseBody
//
//        StepVerifier.create(pizza)
//                .expectNext(pizza1)
//                .verifyComplete()
//    }
//
//    @Test
//    fun `should throw bad format trying to add new pizza `() {
//
//        webTestClient.post()
//                .uri("/pizza")
//                .bodyValue("asdadadsasd")
//                .exchange()
//                .expectStatus().isBadRequest
//                .returnResult<Pizza>().responseBody
//
//    }
}

