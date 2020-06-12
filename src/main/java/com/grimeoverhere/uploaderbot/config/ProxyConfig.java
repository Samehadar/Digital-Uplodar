package com.grimeoverhere.uploaderbot.config;

import com.typesafe.config.Optional;
import lombok.*;

import java.io.Serializable;

@Data @AllArgsConstructor @NoArgsConstructor
public class ProxyConfig implements Serializable {
    @NonNull
    String host;
    @NonNull
    Integer port;
    @Optional
    String username;
    @Optional
    String password;
}
