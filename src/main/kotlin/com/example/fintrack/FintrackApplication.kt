package com.example.fintrack

import com.example.fintrack.security.JwtProperties
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication
import org.springframework.data.web.config.EnableSpringDataWebSupport

@SpringBootApplication
@EnableConfigurationProperties(JwtProperties::class)
@EnableSpringDataWebSupport(pageSerializationMode = EnableSpringDataWebSupport.PageSerializationMode.VIA_DTO)
class FintrackApplication

fun main(args: Array<String>) {
	runApplication<FintrackApplication>(*args)
}
