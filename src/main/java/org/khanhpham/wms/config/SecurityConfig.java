package org.khanhpham.wms.config;

import lombok.RequiredArgsConstructor;
import org.khanhpham.wms.common.UserRole;
import org.khanhpham.wms.security.JwtAuthenticationEntryPoint;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig  {
    private final JwtAuthenticationEntryPoint authenticationEntryPoint;
    private static final String ROLE_ADMIN = UserRole.ROLE_ADMIN.name();
    private static final String ROLE_MODERATOR = UserRole.ROLE_MODERATOR.name();

    private static final String USER_API = "/api/v1/users/**";
    private static final String PRODUCT_API = "/api/v1/products/**";
    private static final String CATEGORY_API = "/api/v1/categories/**";
    private static final String SUPPLIER_API = "/api/v1/suppliers/**";
    private static final String CUSTOMER_API = "/api/v1/customers/**";
    private static final String WAREHOUSE_API = "/api/v1/warehouses/**";
    private static final String PURCHASE_ORDER_API = "/api/v1/purchase-orders/**";
    private static final String SALES_ORDER_API = "/api/v1/sales-orders/**";

    @Value("${app.jwt.singer-key}")
    private String base64EncodedSingerKey;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable)
                .cors(Customizer.withDefaults())
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers(HttpMethod.POST, "/api/v1/auth/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/swagger-ui/**",  "/v3/api-docs/**").permitAll()

                        .requestMatchers(
                                AntPathRequestMatcher.antMatcher(HttpMethod.GET, "api/v1/users"),
                                AntPathRequestMatcher.antMatcher(HttpMethod.POST, USER_API),
                                AntPathRequestMatcher.antMatcher(HttpMethod.DELETE, USER_API),
                                AntPathRequestMatcher.antMatcher(HttpMethod.DELETE, PRODUCT_API),
                                AntPathRequestMatcher.antMatcher(HttpMethod.DELETE, CATEGORY_API),
                                AntPathRequestMatcher.antMatcher(HttpMethod.DELETE, SUPPLIER_API),
                                AntPathRequestMatcher.antMatcher(HttpMethod.DELETE, CUSTOMER_API),
                                AntPathRequestMatcher.antMatcher(HttpMethod.DELETE, WAREHOUSE_API)
                        ).hasAuthority(ROLE_ADMIN)

                        .requestMatchers(
                                AntPathRequestMatcher.antMatcher(HttpMethod.POST, PURCHASE_ORDER_API),
                                AntPathRequestMatcher.antMatcher(HttpMethod.POST, SALES_ORDER_API),
                                AntPathRequestMatcher.antMatcher(HttpMethod.POST, PRODUCT_API),
                                AntPathRequestMatcher.antMatcher(HttpMethod.PUT, PRODUCT_API),
                                AntPathRequestMatcher.antMatcher(HttpMethod.POST, CATEGORY_API),
                                AntPathRequestMatcher.antMatcher(HttpMethod.PUT, CATEGORY_API),
                                AntPathRequestMatcher.antMatcher(HttpMethod.POST, SUPPLIER_API),
                                AntPathRequestMatcher.antMatcher(HttpMethod.PUT, SUPPLIER_API),
                                AntPathRequestMatcher.antMatcher(HttpMethod.POST, CUSTOMER_API),
                                AntPathRequestMatcher.antMatcher(HttpMethod.PUT, CUSTOMER_API),
                                AntPathRequestMatcher.antMatcher(HttpMethod.POST, WAREHOUSE_API),
                                AntPathRequestMatcher.antMatcher(HttpMethod.PUT, WAREHOUSE_API)
                        ).hasAnyAuthority(ROLE_ADMIN, ROLE_MODERATOR)

                        .anyRequest().authenticated()
                )
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(jwtConfigurer -> jwtConfigurer
                                .decoder(jwtDecoder())
                                .jwtAuthenticationConverter(jwtAuthenticationConverter())
                        ))
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint(authenticationEntryPoint))
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        return http.build();
    }

    @Bean
    public static PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    @Bean
    public JwtDecoder jwtDecoder() {
        byte[] decodedKey = Base64.getDecoder().decode(base64EncodedSingerKey);
        SecretKeySpec secretKeySpec = new SecretKeySpec(decodedKey, "HmacSHA512");
        return NimbusJwtDecoder
                .withSecretKey(secretKeySpec)
                .macAlgorithm(MacAlgorithm.HS512)
                .build();
    }

    @Bean
    JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtGrantedAuthoritiesConverter jwtGrantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();
        jwtGrantedAuthoritiesConverter.setAuthorityPrefix("");

        JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();
        jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(jwtGrantedAuthoritiesConverter);

        return jwtAuthenticationConverter;
    }
}
