package com.bamboo.assur.partnerinsurersservice.core.infrastructure.configuration

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.web.DefaultSecurityFilterChain

@Configuration
@EnableWebSecurity
class SecurityConfig {

    @Bean
    fun securityWebFilterChain(http: HttpSecurity): DefaultSecurityFilterChain? {
        http
            .csrf { it.disable() }
            .authorizeHttpRequests { authorize ->
                authorize
                    .requestMatchers("/api/actuator/**").permitAll()
//                    .requestMatchers("/api/v1/partner-insurers/**").authenticated()
                    .anyRequest().permitAll()
            }
            .httpBasic { it.disable() }
            .formLogin { it.disable() }

        return http.build()
    }
}
