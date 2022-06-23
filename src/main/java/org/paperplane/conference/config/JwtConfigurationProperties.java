package org.paperplane.conference.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Setter
@Getter
@Configuration
@ConfigurationProperties(prefix = "org.paperplane.conference.jwt")
public class JwtConfigurationProperties {
    private String privateKey;
    private String publicKey;
}
