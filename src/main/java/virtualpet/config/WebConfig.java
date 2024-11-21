package virtualpet.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/*@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**") // Permet totes les rutes
                        .allowedOrigins("http://localhost:3000") // Només React
                        .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS") // Mètodes HTTP permesos
                        .allowedHeaders("*") // Tots els headers permesos
                        .allowCredentials(true) // Per permetre galetes o autenticació
                        .maxAge(3600); // Cache de la configuració CORS
            }
        };
    }
}*/