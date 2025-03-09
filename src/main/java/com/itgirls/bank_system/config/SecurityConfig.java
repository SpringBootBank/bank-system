package com.itgirls.bank_system.config;

import com.itgirls.bank_system.enums.Role;
import com.itgirls.bank_system.model.Account;
import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.modelmapper.spi.MappingContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity
                .csrf(httpSecurityCsrfConfigurer -> httpSecurityCsrfConfigurer.disable())
                .authorizeHttpRequests((authorize) ->
                        authorize
                                .requestMatchers("/login").permitAll()
                                .requestMatchers(HttpMethod.GET, "/users").hasRole(Role.ADMIN.name())       
                                .requestMatchers(HttpMethod.POST, "/users").hasRole(Role.ADMIN.name())
                                .requestMatchers(HttpMethod.PUT, "/users").hasRole(Role.ADMIN.name())
                                .requestMatchers(HttpMethod.GET, "/transactions").hasRole(Role.ADMIN.name())
                                .requestMatchers(HttpMethod.GET, "/transactions/filter").hasRole(Role.ADMIN.name())
                                .requestMatchers(HttpMethod.POST, "/transactions").hasRole(Role.CLIENT.name())
                                .requestMatchers(HttpMethod.PUT, "/transactions/{id}").hasRole(Role.ADMIN.name())
                                .requestMatchers(HttpMethod.DELETE, "/transactions/{id}").hasRole(Role.ADMIN.name())
                                .requestMatchers(HttpMethod.GET, "/accounts").hasRole(Role.ADMIN.name())
                                .requestMatchers(HttpMethod.GET, "/accounts/{id}").hasRole(Role.ADMIN.name())
                                .requestMatchers(HttpMethod.PUT, "/accounts/{id}").hasRole(Role.ADMIN.name())
                                .requestMatchers(HttpMethod.POST, "/accounts").hasRole(Role.ADMIN.name())
                                .requestMatchers(HttpMethod.DELETE, "/accounts/{id}").hasRole(Role.ADMIN.name())
                                .requestMatchers(HttpMethod.GET, "/deposits").hasRole(Role.ADMIN.name())
                                .requestMatchers(HttpMethod.DELETE, "/deposits/{id}").hasRole(Role.ADMIN.name())
                                .requestMatchers(HttpMethod.GET, "/loans").hasRole(Role.ADMIN.name())
                                .requestMatchers(HttpMethod.GET, "/loans/").hasRole(Role.ADMIN.name())
                                .requestMatchers(HttpMethod.POST, "/loans").hasRole(Role.ADMIN.name())
                                .requestMatchers(HttpMethod.DELETE, "/loans/{id}").hasRole(Role.ADMIN.name())
                                .anyRequest().authenticated())
                .formLogin(Customizer.withDefaults())
                .httpBasic(Customizer.withDefaults())
                .logout(Customizer.withDefaults());
        return httpSecurity.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public ModelMapper modelMapper() {
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.addConverter(new Converter<Account, String>() {
            @Override
            public String convert(MappingContext<Account, String> context) {
                Account account = context.getSource();
                return account != null ? account.getAccountNumber() : null;
            }
        });
        return modelMapper;
    }
}