package ru.vsu.cs.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.vsu.cs.utils.CryptoTool;

@Configuration
public class NodeConfiguration {
    @Value("${salt}")
    private String salt;

    @Bean
    public CryptoTool cryptoTool() {
        return new CryptoTool(salt);
    }
}
