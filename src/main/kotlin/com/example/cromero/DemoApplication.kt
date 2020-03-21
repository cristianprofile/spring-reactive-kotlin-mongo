package com.example.cromero

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import reactor.blockhound.BlockHound

@SpringBootApplication
class DemoApplication

    fun main(args: Array<String>) {
        runApplication<DemoApplication>(*args)
    }