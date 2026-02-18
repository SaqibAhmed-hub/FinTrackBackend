package com.example.fintrack.config
//
//import org.springframework.boot.test.context.SpringBootTest
//import org.springframework.test.context.DynamicPropertyRegistry
//import org.springframework.test.context.DynamicPropertySource
//import org.testcontainers.containers.PostgreSQLContainer
//import org.testcontainers.junit.jupiter.Container
//import org.testcontainers.junit.jupiter.Testcontainers
//
//@Testcontainers
//@SpringBootTest
//abstract class PostgresContainerConfig {
//
//    companion object {
//
//        @Container
//        val postgres = PostgreSQLContainer<Nothing>("postgres:15").apply {
//            withDatabaseName("fintrack-test")
//            withUsername("postgres")
//            withPassword("osaahmed")
//        }
//
//        @JvmStatic
//        @DynamicPropertySource
//        fun configureProperties(registry: DynamicPropertyRegistry) {
//            registry.add("spring.datasource.url", postgres::getJdbcUrl)
//            registry.add("spring.datasource.username", postgres::getUsername)
//            registry.add("spring.datasource.password", postgres::getPassword)
//            registry.add("spring.jpa.hibernate.ddl-auto") { "create-drop" }
//        }
//    }
//}
