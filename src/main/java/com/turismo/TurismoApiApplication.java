package com.turismo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@SpringBootApplication
@EnableAutoConfiguration
public class TurismoApiApplication {	
	
    private String devUrl = "http://localhost:4200";   

	public static void main(String[] args) {
		SpringApplication.run(TurismoApiApplication.class, args);
	}

	
	@Bean
	public WebMvcConfigurer corsConfigurer() {
		return new WebMvcConfigurer() {
			@Override
			public void addCorsMappings(CorsRegistry registry) {
				registry.addMapping("/Travel/**")
				.allowedOrigins(devUrl)
				.allowedMethods("*")
				.allowedHeaders("*");
				registry.addMapping("/City/**")
				.allowedOrigins(devUrl)
				.allowedMethods("*")
				.allowedHeaders("*");
				registry.addMapping("/Tourist/**")
				.allowedOrigins(devUrl)
				.allowedMethods("*")
				.allowedHeaders("*");;
				
			}
		};
	}

}
