package ru.ifmo.pashaac;

import org.apache.log4j.Logger;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import ru.ifmo.pashaac.common.Properties;

@SpringBootApplication
public class GeosegmentationAndGeoprofilingApplication {

    private static final Logger LOG = Logger.getLogger(GeosegmentationAndGeoprofilingApplication.class);

	public static void main(String[] args) {
        SpringApplication.run(GeosegmentationAndGeoprofilingApplication.class, args);
        LOG.info("=============================================================================================");
        Thread properties = new Thread(new Properties(), "properties");
        properties.start();
	}
}
