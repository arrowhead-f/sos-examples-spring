package eu.arrowhead.application.skeleton.executor.security;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

import ai.aitia.arrowhead.application.library.config.DefaultSecurityConfig;

@Configuration
@EnableWebSecurity
public class ExecutorSecurityConfig extends DefaultSecurityConfig {

}
