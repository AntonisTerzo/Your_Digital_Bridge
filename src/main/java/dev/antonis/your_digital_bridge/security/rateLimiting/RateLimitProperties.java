package dev.antonis.your_digital_bridge.security.rateLimiting;


import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "app.rate-limit")
public class RateLimitProperties {
    private int maxTransfers = 5;
    private long windowSizeMinutes = 1;
    private boolean enabled = true;
}
