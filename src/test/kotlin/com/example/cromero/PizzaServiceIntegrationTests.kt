package com.example.cromero


import org.jeasy.random.EasyRandom
import org.jeasy.random.EasyRandomParameters
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest
import org.springframework.context.annotation.Import
import reactor.test.StepVerifier
import java.nio.charset.Charset


@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DataMongoTest
@Import(value = [PizzaServiceImpl::class])
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

        val pizza = easyRandom.nextObject(Pizza::class.java)
        val addedPizza = pizzaService.addPizza(pizza).block()
        val foundPizza = pizzaService.getPizza(addedPizza!!.id)
        StepVerifier.create(foundPizza)
                .expectNext(addedPizza)
                .expectComplete()
                .verify()

    }
}

