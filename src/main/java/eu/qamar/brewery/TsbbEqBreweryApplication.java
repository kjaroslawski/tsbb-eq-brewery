package eu.qamar.brewery;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@EnableAsync
@SpringBootApplication
public class TsbbEqBreweryApplication {

    public static void main(String[] args) {
        SpringApplication.run(TsbbEqBreweryApplication.class, args);
    }

}
