package com.example.cromero


import org.jeasy.random.EasyRandom
import org.jeasy.random.EasyRandomParameters
import org.junit.jupiter.api.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest
import org.springframework.context.annotation.Import
import reactor.kotlin.test.test
import reactor.test.StepVerifier
import java.nio.charset.Charset


@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DataMongoTest
@Import(value = [PizzaServiceImpl::class])
@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
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
    @Order(1)
    fun `should get all pizzas (Must be 3)`() {

        val pizza1 = easyRandom.nextObject(Pizza::class.java)
        val pizza2 = easyRandom.nextObject(Pizza::class.java)
        val pizza3 = easyRandom.nextObject(Pizza::class.java)
        pizzaService.addPizza(pizza1).block()
        pizzaService.addPizza(pizza2).block()
        pizzaService.addPizza(pizza3).block()
        val pizzas = pizzaService.getPizzas()

        pizzas.test() .expectNext(pizza1)
                .expectNext(pizza2)
                .expectNext(pizza3)
                .verifyComplete()
    }


    @Test
    @Order(2)
    fun `should add pizza and get by id`() {

        val pizza = easyRandom.nextObject(Pizza::class.java)
        val addedPizza = pizzaService.addPizza(pizza).block()
        val foundPizza = pizzaService.getPizza(addedPizza!!.id)
        foundPizza.test()
                .expectNext(addedPizza)
                .verifyComplete()
    }

    @Test
    @Order(3)
    fun `shouldn't add 2 pizza with the same name`() {

        val pizza = easyRandom.nextObject(Pizza::class.java)
        val block = pizzaService.addPizza(pizza).block()
        val duplicatedPizza = pizzaService.addPizza(pizza)

        duplicatedPizza.test()
                .expectError(PizzaDuplicatedException::class.java)
                .verify()

    }

    @Test
    @Order(4)
    fun `should add pizza and get by id must return not found`() {

        val pizza = easyRandom.nextObject(Pizza::class.java)
        pizzaService.addPizza(pizza).block()
        val foundPizza = pizzaService.getPizza(4444)

        foundPizza.test().expectError(PizzaDoesntExistException::class.java)
                .verify()
    }

    @Test
    @Order(4)
    fun `should add pizza and delete it by id`() {

        val pizza = easyRandom.nextObject(Pizza::class.java)
        pizzaService.addPizza(pizza).block()
        val deletedPizza = pizzaService.deletePizza(pizza.id)

        deletedPizza.test().verifyComplete()

    }





}

