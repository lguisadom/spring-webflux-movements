package com.nttdata.lagm.movements;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

@EnableEurekaClient
@SpringBootApplication
public class SpringWebfluxMovementsApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringWebfluxMovementsApplication.class, args);
	}

}
