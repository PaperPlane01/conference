package org.paperplane.conference.config;

import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import lombok.SneakyThrows;
import org.paperplane.conference.security.CustomAuthenticationManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.oauth2.server.resource.OAuth2ResourceServerConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.security.web.SecurityFilterChain;

import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {
    @Autowired
    private JwtConfigurationProperties jwtConfigurationProperties;

    static {
        java.security.Security.addProvider(
                new org.bouncycastle.jce.provider.BouncyCastleProvider()
        );
    }


    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12);
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests((authorize) -> authorize
                        .antMatchers("/**").permitAll()
                )
                .csrf(AbstractHttpConfigurer::disable)
                .headers().frameOptions().disable()
                .and()
                .oauth2ResourceServer(OAuth2ResourceServerConfigurer::jwt).authenticationManager(customAuthenticationManager());
        return http.build();
    }

    @Bean
    public AuthenticationManager customAuthenticationManager() {
        return new CustomAuthenticationManager(jwtDecoder());
    }

    @Bean
    public JwtEncoder jwtEncoder() {
        var jwk = new RSAKey.Builder(publicKey()).privateKey(privateKey()).build();
        var jwks = new ImmutableJWKSet<>(new JWKSet(jwk));
        return new NimbusJwtEncoder(jwks);
    }

    @Bean
    public JwtDecoder jwtDecoder() {
        return NimbusJwtDecoder.withPublicKey(publicKey()).build();
    }

    @Bean
    @SneakyThrows
    public RSAPublicKey publicKey() {
        var publicKey = jwtConfigurationProperties.getPublicKey()
                .replace("-----BEGIN PUBLIC KEY-----", "")
                .replace("-----END PUBLIC KEY-----", "")
                .replace("\\n", "")
                .replace("\\r", "");
        var decoded = Base64.getDecoder().decode(publicKey);
        var rsaPublicKey = rsaKeyFactory().generatePublic(new X509EncodedKeySpec(decoded));
        return (RSAPublicKey) rsaPublicKey;
    }

    @Bean
    @SneakyThrows
    public RSAPrivateKey privateKey() {
        var privateKey = jwtConfigurationProperties.getPrivateKey()
                .replace("-----BEGIN RSA PRIVATE KEY-----", "")
                .replace("-----END RSA PRIVATE KEY-----", "")
                .replace("\\n", "")
                .replace("\\r", "");
        var decoded = Base64.getDecoder().decode(privateKey.getBytes(StandardCharsets.UTF_8));
        var keySpec = new PKCS8EncodedKeySpec(decoded);
        var rsaPrivateKey = rsaKeyFactory().generatePrivate(keySpec);
        return (RSAPrivateKey) rsaPrivateKey;
    }

    @Bean
    @SneakyThrows
    public KeyFactory rsaKeyFactory() {
        return KeyFactory.getInstance("RSA");
    }
}
