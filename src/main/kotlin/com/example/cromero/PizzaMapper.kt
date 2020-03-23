package com.example.cromero

import com.example.cromero.dto.PizzaCreate
import com.example.cromero.dto.PizzaOut

fun Pizza.convertToPizzaOut() = PizzaOut(
        id = id,
        name = name,
        description = description)

fun PizzaCreate.convertToPizza() = Pizza(
        id = id,
        name = name,
        description = description)


