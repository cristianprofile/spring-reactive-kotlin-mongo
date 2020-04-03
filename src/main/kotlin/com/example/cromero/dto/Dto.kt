package com.example.cromero.dto

data class PizzaOut(val id: Long, val name:String, val description:String, val price: Int)
data class PizzaCreate(val id: Long, val name:String, val description:String, val price: Int)
data class PizzaUpdate(val id: Long, val name:String?, val description:String?, val price: Int?)