package com.yqf.ws;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.server.standard.ServerEndpointExporter;
import org.testng.annotations.ITestOrConfiguration;

/**
 * @author yqf
 */
@Configuration
public class WebSocketConfig {
 
    @Bean
    public ServerEndpointExporter serverEndpointExporter() {
        return new ServerEndpointExporter();
    }
}