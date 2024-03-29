package com.example.cromero


import com.example.cromero.dto.PizzaCreate
import org.jeasy.random.EasyRandom
import org.jeasy.random.EasyRandomParameters
import org.junit.jupiter.api.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest
import org.springframework.context.annotation.Import
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.test.context.TestPropertySource
import reactor.kotlin.test.test
import java.nio.charset.Charset


@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DataMongoTest
@Import(value = [PizzaServiceImpl::class])
@TestPropertySource(properties = ["spring.mongodb.embedded.version=3.5.5"])
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
    fun `should get all pizzas paginated- should get all pizzas without using pagination`() {

        val pizza1 = easyRandom.nextObject(PizzaCreate::class.java).copy(name = "aoster", price = 1001)
        val pizza2 = easyRandom.nextObject(PizzaCreate::class.java).copy(name = "but", price = 1002)
        val pizza3 = easyRandom.nextObject(PizzaCreate::class.java).copy(name = "rolling", price = 0)
        val pizza1Created = pizzaService.addPizza(pizza1).block()
        val pizza2Created = pizzaService.addPizza(pizza2).block()
        val pizza3Created = pizzaService.addPizza(pizza3).block()

        val sort = Sort.by(Sort.Direction.ASC, "name")
        var pageable = PageRequest.of(0, 2, sort)

        var pizzas = pizzaService.findAllPaginated(pageable)

        pizzas.test()
                .expectNext(pizza1Created)
                .expectNext(pizza2Created)
                .verifyComplete()

        pageable = PageRequest.of(1, 2, sort)

        pizzas = pizzaService.findAllPaginated(pageable)
        pizzas.test()
                .expectNext(pizza3Created)
                .verifyComplete()

        pageable = PageRequest.of(2, 2, sort)

        pizzas = pizzaService.findAllPaginated(pageable)
        pizzas.test()
                .verifyComplete()


        pizzas = pizzaService.findAll()
        pizzas.test()
                .expectNext(pizza1Created)
                .expectNext(pizza2Created)
                .expectNext(pizza3Created)
                .verifyComplete()

        val allPizzaExpensive = pizzaService.allPizzaExpensive()
        allPizzaExpensive.test()
                .expectNext(false)
                .verifyComplete()

        val existAnyPizzaFree = pizzaService.existAnyPizzaFree()
        existAnyPizzaFree.test()
                .expectNext(true)
                .verifyComplete()

        pageable = PageRequest.of(0, 4, sort)
        val findAllPaginatedToList = pizzaService.findAllPaginatedToList(pageable)
        findAllPaginatedToList.test()
                .expectNext(mutableListOf(pizza1Created!!, pizza2Created!!, pizza3Created!!))
                .verifyComplete()

        val findAllPaginatedToSortedList = pizzaService.findAllPaginatedToSortedList(pageable)

        findAllPaginatedToSortedList.test()
                .expectNext(mutableListOf(pizza1Created!!, pizza2Created!!, pizza3Created!!))
                .verifyComplete()



        val findAllNotExistWithDescriptionUsingWhenOperator = pizzaService.findAllNotExistWithDescriptionUsingWhenOperator("description3")

        findAllNotExistWithDescriptionUsingWhenOperator.test()
                .expectNext(pizza1Created)
                .expectNext(pizza2Created)
                .expectNext(pizza3Created)
                .verifyComplete()

    }


    @Test
    @Order(2)
    fun `should add pizza and get by id`() {

        val pizza = easyRandom.nextObject(PizzaCreate::class.java)
        val addedPizza = pizzaService.addPizza(pizza).block()
        val foundPizza = pizzaService.getPizza(addedPizza!!.id)
        foundPizza.test()
                .expectNext(addedPizza)
                .verifyComplete()
    }

    @Test
    @Order(3)
    fun `shouldn't add 2 pizza with the same name`() {

        val pizza = easyRandom.nextObject(PizzaCreate::class.java)
        pizzaService.addPizza(pizza).block()
        val duplicatedPizza = pizzaService.addPizza(pizza.copy(description = "anotherDescription"))

        duplicatedPizza.test()
                .verifyError(PizzaDuplicatedException::class.java)

    }


    @Test
    @Order(3)
    fun `shouldn't add 2 pizza with the same description`() {

        val pizza = easyRandom.nextObject(PizzaCreate::class.java)
        pizzaService.addPizza(pizza).block()
        val duplicatedPizza = pizzaService.addPizza((pizza.copy(name = "anotherName")))

        duplicatedPizza.test()
                .verifyError(PizzaDuplicatedException::class.java)

    }

    @Test
    @Order(4)
    fun `should add pizza and get by id must return not found`() {

        val pizza = easyRandom.nextObject(PizzaCreate::class.java)
        pizzaService.addPizza(pizza).block()
        val foundPizza = pizzaService.getPizza(4444)

        foundPizza.test()
                .verifyError(PizzaDoesntExistException::class.java)
    }

    @Test
    @Order(4)
    fun `should add pizza and delete it by id`() {

        val pizza = easyRandom.nextObject(PizzaCreate::class.java)
        pizzaService.addPizza(pizza).block()
        val deletedPizza = pizzaService.deletePizza(pizza.id)

        deletedPizza.test().verifyComplete()

    }

    @Test
    @Order(4)
    fun `should add pizza returning mono boolean`() {

        val pizza = easyRandom.nextObject(PizzaCreate::class.java)
        val pizzaIsSaved = pizzaService.addPizzaReturnMonoBoolean(pizza)
        pizzaIsSaved.test().expectNext(true)
                .verifyComplete()
    }

    @Test
    @Order(4)
    fun `should add pizza returning mono void`() {

        val pizza = easyRandom.nextObject(PizzaCreate::class.java)
        val pizzaIsSaved = pizzaService.addPizzaReturnMonoVoid(pizza)
        pizzaIsSaved.test().verifyComplete()
    }

}
