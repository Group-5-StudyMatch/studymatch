package upt.group5.studymatch.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import upt.group5.studymatch.security.JwtAuthenticationFilter;

@Configuration
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
}
    
    @Value("${app.security.enabled:false}") //read security.enabled, if is not define, use false
    private boolean securityEnabled;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http)
            throws Exception {

        // disable csrf since the api uses stateless authentication
        http.csrf(csrf -> csrf.disable());

        // don't create or use http sessions for authentication
        http.sessionManagement(session ->
                session.sessionCreationPolicy(

                        //spring security dont use http for save autenthications
                        //each request has is own token
                        SessionCreationPolicy.STATELESS
                )
        );

        if (!securityEnabled) {

            // allow all requests while security is disabled
            http.authorizeHttpRequests(auth ->
                    auth.anyRequest().permitAll()
            );

        } 
        
        else {

            // return 401 when authentication is missing
            http.exceptionHandling(exceptions ->
                    exceptions.authenticationEntryPoint(
                            new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED)
                    )
            );

            // allow only the required public endpoints
            http.authorizeHttpRequests(auth -> auth
                    .requestMatchers(
                            HttpMethod.POST, "/api/auth/login"
                    ).permitAll()
                    .requestMatchers(
                            HttpMethod.GET, "/api/health"
                    ).permitAll()
                    .requestMatchers("/api/**").authenticated()
                    .anyRequest().permitAll()
            );

            // validate jwt tokens when security is enabled
            http.addFilterBefore(
                    jwtAuthenticationFilter,
                    UsernamePasswordAuthenticationFilter.class
            );
        }


        // disable default login mechanisms
        http.formLogin(form -> form.disable());
        http.httpBasic(basic -> basic.disable());

        return http.build();
    }


    @Bean
    public FilterRegistrationBean<JwtAuthenticationFilter> jwtFilterRegistration(
            JwtAuthenticationFilter jwtAuthenticationFilter) {

        // prevent the jwt filter from being registered automatically
        FilterRegistrationBean<JwtAuthenticationFilter> registration =
                new FilterRegistrationBean<>(jwtAuthenticationFilter);

        registration.setEnabled(false);

        return registration;
    }


}
