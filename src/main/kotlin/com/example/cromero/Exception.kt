package com.example.cromero

class PizzaDoesntExistException(private val id: Long) : RuntimeException()
class PizzaDuplicatedException(private val name: String) : RuntimeException()