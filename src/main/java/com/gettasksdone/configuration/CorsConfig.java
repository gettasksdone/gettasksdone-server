package com.gettasksdone.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig implements WebMvcConfigurer {

    @SuppressWarnings("null")
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
		        .allowedOriginPatterns("*") // Permitir peticiones desde cualquier origen
                .allowedMethods("GET", "POST", "PATCH", "PUT", "DELETE", "OPTIONS") // Métodos HTTP permitidos
                .allowedHeaders("*") // Cabeceras permitidas
                .allowCredentials(true) // Permitir credenciales (por ejemplo, cookies)
                .maxAge(3600); // Tiempo de validez de la preflight request en segundos
    }
}
