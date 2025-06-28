package com.unir.books_catalogue;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.web.reactive.function.client.WebClient;

@SpringBootApplication
public class BooksCatalogueApplication {

	@LoadBalanced
	@Bean
	public WebClient.Builder webClient(){
		return WebClient.builder();
	}

	public static void main(String[] args) {
		SpringApplication.run(BooksCatalogueApplication.class, args);
	}
}
