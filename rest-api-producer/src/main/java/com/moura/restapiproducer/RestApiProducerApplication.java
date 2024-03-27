package com.moura.restapiproducer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.kafka.annotation.EnableKafka;

@EnableKafka
@SpringBootApplication
public class RestApiProducerApplication {

	public static void main(String[] args) {
		SpringApplication.run(RestApiProducerApplication.class, args);
	}

}
