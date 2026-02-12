package com.example.fintrack

import com.example.fintrack.security.JwtProperties
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication

@SpringBootApplication
@EnableConfigurationProperties(JwtProperties::class)
class FintrackApplication

fun main(args: Array<String>) {
	runApplication<FintrackApplication>(*args)
}
