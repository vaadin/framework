package com.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;

import com.vaadin.spring.server.SpringVaadinServlet;

@SpringBootApplication
public class DemoApplication {

    public static final String CONTEXT = "/subcontext";

    public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class, args);
    }

    @Bean
    ServletRegistrationBean servlet() {
        return new ServletRegistrationBean(new SpringVaadinServlet(), false,
                CONTEXT + "/*", "/VAADIN/*");
    }

}
