package com.gettasksdone.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.gettasksdone.auth.OAuthService;
import com.gettasksdone.jwt.JwtAuthenticationFilter;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor

//Habilitamos si podemos o no llegar a un endpoint
@EnableMethodSecurity(
    securedEnabled = true,
    jsr250Enabled = true,
    prePostEnabled = true
)

/**
 * 
 *   prePostEnabled = true: Habilita @PreAuthorize y @PostAuthorize.
     securedEnabled = true: Habilita @Secured.
     jsr250Enabled = true: Habilita @RolesAllowed.
 */
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final AuthenticationProvider authenticationProvider;

    @Autowired
    private OAuthService oAuthService;

    private ClientRegistrationRepository altaRepo;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception
    {
        return http
            .csrf(csrf -> 
                csrf
                .disable())
            .authorizeHttpRequests(authRequest ->
              authRequest
                .requestMatchers("/auth/**").permitAll()
                .requestMatchers("/api/ping").permitAll()
                //.requestMatchers("/oauthlogin/**").permitAll()
                .anyRequest().authenticated() //Necesitamos estar autenticados para poder ver los diferentes endpoint
                )
            .sessionManagement(sessionManager -> 
                sessionManager
                    .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authenticationProvider(authenticationProvider)
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
            /*.oauth2Login(form -> form
                .loginPage("/auth/login") //localhost/login // -> src/main/resources/templates
                .userInfoEndpoint(userInfo -> userInfo
                .userService(oAuthService)))*/
            .build();
    }

}