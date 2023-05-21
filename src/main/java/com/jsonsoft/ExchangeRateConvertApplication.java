package com.jsonsoft;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages ={"com.jsonsoft.**"})
public class ExchangeRateConvertApplication {

	public static void main(String[] args) {
		SpringApplication.run(ExchangeRateConvertApplication.class, args);
	}

}
