package com.company.onboarding;

import io.jmix.securityflowui.FlowuiSecurityConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@EnableWebSecurity
@Configuration
public class ApplicationSecurityConfiguration extends FlowuiSecurityConfiguration {

    @Override
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(registry -> registry.requestMatchers("/map-icons/**").permitAll());
        return super.securityFilterChain(http);
    }
}
