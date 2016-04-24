package ru.ifmo.pashaac;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import ru.ifmo.pashaac.common.Properties;

@SpringBootApplication
public class GeosegmentationAndGeoprofilingApplication {

	public static void main(String[] args) {
        SpringApplication.run(GeosegmentationAndGeoprofilingApplication.class, args);
        Thread properties = new Thread(new Properties(), "Properties");
        properties.start();
	}
}
