package com.vsalescode.minierp;

import java.time.ZoneOffset;
import java.util.TimeZone;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class MiniErpApplication {

	public static void main(String[] args) {
		TimeZone.setDefault(TimeZone.getTimeZone(ZoneOffset.UTC));
		SpringApplication.run(MiniErpApplication.class, args);
	}

}
