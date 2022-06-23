package org.paperplane.conference.config;

import lombok.Getter;
import lombok.Setter;
import org.paperplane.conference.model.Role;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Setter
@Getter
@Configuration
@ConfigurationProperties(prefix = "org.paperplane.conference.init-data")
public class DataInitializationConfigurationProperties {
    private boolean runOnStart = false;

    private List<InitialUser> users;

    @Getter
    @Setter
    public static class InitialUser {
        private String id = null;
        private String username;
        private String password;
        private String displayedName;
        private List<Role> roles;
    }
}
