package epam.security.config;

import epam.security.filter.BruteForceProtectionFilter;
import epam.security.jwt.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final BruteForceProtectionFilter bruteForceProtectionFilter;
    private final UserDetailsService userDetailsService;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
            http.csrf(AbstractHttpConfigurer::disable)
                .cors(Customizer.withDefaults())
                .sessionManagement(manager ->
                        manager.sessionCreationPolicy(STATELESS))
                .authorizeHttpRequests(request -> request
                        .requestMatchers("/api/auth/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/v1/trainees").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/v1/trainers").permitAll()
                        .requestMatchers("/swagger-ui.html",
                                "/swagger-ui/**",
                                "/v3/api-docs/**",
                                "/swagger-resources/**").permitAll()
                        .requestMatchers("/api/v1/trainings/types").permitAll()
                        .requestMatchers("/actuator/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/v1/trainees/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PATCH, "/api/v1/trainees/activation",
                                "/api/v1/trainers/activation").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/v1/trainees/**").hasAnyRole("TRAINEE", "ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/v1/trainees/**").hasAnyRole("TRAINEE", "ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/v1/trainers/**").hasAnyRole("TRAINER", "ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/v1/trainers/**").hasAnyRole("TRAINER", "ADMIN")
                        .requestMatchers(HttpMethod.POST, "/api/v1/trainings").hasAnyRole("TRAINER", "TRAINEE")
                        .requestMatchers(HttpMethod.GET, "/api/v1/trainings/trainer","/api/v1/trainers/profile")
                                 .hasRole("TRAINER")
                        .requestMatchers(HttpMethod.GET, "/api/v1/trainings/trainee","/api/v1/trainees/profile")
                                 .hasRole("TRAINEE")
                        .anyRequest().authenticated())
                .authenticationProvider(authenticationProvider())
                .addFilterBefore(bruteForceProtectionFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}
