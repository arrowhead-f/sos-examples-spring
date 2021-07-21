package eu.arrowhead.application.skeleton.consumer.security;

import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

import ai.aitia.arrowhead.application.library.config.DefaultSecurityConfig;

@Configuration
@ConditionalOnWebApplication
@EnableWebSecurity
public class ConsumerSecurityConfig extends DefaultSecurityConfig {
	
}
