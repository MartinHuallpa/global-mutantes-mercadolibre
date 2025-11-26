package org.global.mutantes_ds;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableAsync;

@EnableAsync // Habilita la ejecución asíncrona de métodos anotados con @Async
@EnableCaching // Habilita el uso de caché en toda la aplicación
@SpringBootApplication
public class MutantesDsApplication {

	public static void main(String[] args) {
		SpringApplication.run(MutantesDsApplication.class, args);
	}

}
