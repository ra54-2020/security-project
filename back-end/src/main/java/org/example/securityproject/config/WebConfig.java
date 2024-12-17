package org.example.securityproject.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.google.common.net.HttpHeaders;

@Configuration
@EnableWebMvc
public class WebConfig implements WebMvcConfigurer{
    // Za svrhe razvoja konfigurisemo dozvolu za CORS kako ne bismo morali @CrossOrigin anotaciju da koristimo nad svakim kontrolerom
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("http://localhost:4200") //povezivanje sa Angular
                .allowedMethods(HttpMethod.GET.name(),
                                HttpMethod.POST.name(),
                                HttpMethod.DELETE.name(),
                                HttpMethod.PATCH.name(),
                                HttpMethod.PUT.name())
                .allowedHeaders(HttpHeaders.CONTENT_TYPE, 
                                HttpHeaders.AUTHORIZATION, 
                                "Refresh-Token")
                .exposedHeaders(HttpHeaders.AUTHORIZATION, 
                                "Refresh-Token") // Ovo omogućava pristup ovim zaglavljima iz klijentske aplikacije
                .allowCredentials(true)
                .maxAge(3600);
    }
}
