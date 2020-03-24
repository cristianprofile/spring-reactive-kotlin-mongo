package com.example.cromero

import org.springframework.data.annotation.Id

data class Pizza(@Id val id: Long, val name:String, val description:String, val price:Integer)