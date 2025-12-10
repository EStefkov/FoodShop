package bg.emiliyan.acc_backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class AccBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(AccBackendApplication.class, args);
    }

}
