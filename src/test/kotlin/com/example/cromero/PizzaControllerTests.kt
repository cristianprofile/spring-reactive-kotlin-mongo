package com.example.cromero


import com.example.cromero.dto.PizzaCreate
import com.example.cromero.dto.PizzaOut
import org.jeasy.random.EasyRandom
import org.jeasy.random.EasyRandomParameters
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.mockito.BDDMockito.`when`
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.test.web.reactive.server.returnResult
import reactor.kotlin.core.publisher.toFlux
import reactor.kotlin.core.publisher.toMono
import reactor.kotlin.test.test
import java.nio.charset.Charset

@WebFluxTest(controllers = [PizzaController::class])
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class PizzaControllerTests {

    @Autowired
    private lateinit var webTestClient: WebTestClient

    @MockBean
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

        val pizza1 = easyRandom.nextObject(PizzaOut::class.java).copy(id = 1)
        val pizza2 = easyRandom.nextObject(PizzaOut::class.java).copy(id = 2)
        val pizza3 = easyRandom.nextObject(PizzaOut::class.java).copy(id = 3)


        `when`(pizzaService.getPizzas())
                .thenReturn(listOf(pizza1, pizza2, pizza3).toFlux())

        val pizzas = webTestClient.get()
                .uri("/pizza")
                .exchange()
                .expectStatus().isOk
                .returnResult<PizzaOut>().responseBody

        pizzas.test()
                .expectNext(pizza1)
                .expectNext(pizza2)
                .expectNext(pizza3)
                .verifyComplete()
    }

    @Test
    fun `should get pizza with id 1`() {

        val pizza1 = easyRandom.nextObject(PizzaOut::class.java).copy(id = 1)

        `when`(pizzaService.getPizza(1L))
                .thenReturn(pizza1.toMono())

        val pizza = webTestClient.get()
                .uri("/pizza/{id}", pizza1.id)
                .exchange()
                .expectStatus().isOk
                .returnResult<PizzaOut>().responseBody

        pizza.test()
                .expectNext(pizza1)
                .verifyComplete()
    }


    @Test
    fun `should add pizza `() {

        val pizza = easyRandom.nextObject(PizzaCreate::class.java).copy(id = 1)
        val pizzaOut = PizzaOut(id=1,name=pizza.name,description = pizza.description)

        `when`(pizzaService.addPizza(pizza)).thenReturn(pizzaOut.toMono())

        val pizzaCreated = webTestClient.post()
                .uri("/pizza")
                .bodyValue(pizza)
                .exchange()
                .expectStatus().isOk
                .returnResult<PizzaOut>().responseBody

        pizzaCreated.test()
                .expectNext(pizzaOut)
                .verifyComplete()
    }

    @Test
    fun `should throw bad format trying to add new pizza `() {

        webTestClient.post()
                .uri("/pizza")
                .bodyValue("asdadadsasd")
                .exchange()
                .expectStatus().isBadRequest
                .returnResult<Pizza>().responseBody

    }
}
