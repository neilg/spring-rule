package io.meles.spring;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ThrowingConfig {

    @Bean
    public Object bad() {
        throw new RuntimeException("bad, bad, bad");
    }
}
