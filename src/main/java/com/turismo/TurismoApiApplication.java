package com.turismo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;




@SpringBootApplication
@EnableAutoConfiguration

public class TurismoApiApplication {

	public static void main(String[] args) {		
		SpringApplication.run(TurismoApiApplication.class, args);
	}

}
