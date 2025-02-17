package dev.antonis.your_digital_bridge.security;

import dev.antonis.your_digital_bridge.security.jwt.AuthTokenFilter;
import dev.antonis.your_digital_bridge.security.jwt.JwtUtils;
import dev.antonis.your_digital_bridge.user.UserDetailsImpl;
import dev.antonis.your_digital_bridge.user.UserDetailsServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.http.ResponseCookie;

import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpMethod.POST;

@Configuration
@EnableMethodSecurity
public class Security {

    private final JwtUtils jtwUtils;
    private final UserDetailsServiceImpl userDetailsServiceImpl;

    public Security(JwtUtils jtwUtils, UserDetailsServiceImpl userDetailsServiceImpl) {
        this.jtwUtils = jtwUtils;
        this.userDetailsServiceImpl = userDetailsServiceImpl;
    }

    @Bean
    public AuthTokenFilter authJwtTokenFilter() {
        return new AuthTokenFilter(jtwUtils, userDetailsServiceImpl);
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authconfig) throws Exception {
        return authconfig.getAuthenticationManager();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();

        authProvider.setUserDetailsService(userDetailsServiceImpl);
        authProvider.setPasswordEncoder(passwordEncoder());

        return authProvider;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.
                csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(authorize ->
                        authorize
                                .requestMatchers(GET, "/").permitAll()
                                .requestMatchers(GET, "/login").permitAll()
                                .requestMatchers(GET, "/register").permitAll()
                                .requestMatchers(POST, "/api/auth/register").permitAll()
                                .requestMatchers(POST, "/api/auth/login").permitAll()
                                .requestMatchers(GET, "/me").authenticated()
                                .requestMatchers(POST, "/api/transactions/transfer").authenticated()
                                .requestMatchers(GET, "/transfer").authenticated()
                                .requestMatchers(POST, "/transfer").authenticated()
                                .anyRequest().authenticated())
                .formLogin(formLogin -> formLogin
                        .loginPage("/login")
                        .loginProcessingUrl("/login")
                        .successHandler((request, response, authentication) -> {
                            UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
                            ResponseCookie jwtCookie = jtwUtils.generateJwtCookie(userDetails);
                            response.addHeader(HttpHeaders.SET_COOKIE, jwtCookie.toString());
                            response.sendRedirect("/me");
                        })
                        .failureUrl("/login?error")
                        .permitAll())
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .exceptionHandling(exception ->
                        exception.authenticationEntryPoint(new CustomAuthenticationEntryPoint()));

        http.authenticationProvider(authenticationProvider());
        http.addFilterBefore(authJwtTokenFilter(), UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }
}
