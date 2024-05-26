package com.stevi.moneyminder

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class Application

fun main(args: Array<String>) {
	runApplication<Application>(*args)
}

// TODO:
// add db indexes
// improve select queries
// improve batch queries
// refactor code

