package org.igor.urly

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class UrlyApplication

fun main(args: Array<String>) {
	runApplication<UrlyApplication>(*args)
}
