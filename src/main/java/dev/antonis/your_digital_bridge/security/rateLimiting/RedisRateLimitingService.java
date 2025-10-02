package dev.antonis.your_digital_bridge.security.rateLimiting;


import dev.antonis.your_digital_bridge.security.exceptions.RateLimitExceededException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
public class RedisRateLimitingService {
    private final RedisTemplate<String, String> redisTemplate;
    private final RateLimitProperties rateLimitProperties;
    private final Logger logger = LoggerFactory.getLogger(RedisRateLimitingService.class);

    public RedisRateLimitingService(RedisTemplate<String, String> redisTemplate,
                                    RateLimitProperties rateLimitProperties) {
        this.redisTemplate = redisTemplate;
        this.rateLimitProperties = rateLimitProperties;
    }

    public void checkRateLimit(Integer userId) {
        // check allows us to disable rate limiting instantly
        // if there are issues in production without deploying new code
        if (!rateLimitProperties.isEnabled()) {
            return;
        }

        String key = "transfer_rate_limit:" + userId;
        long windowSizeSeconds = rateLimitProperties.getWindowSizeMinutes() * 60;

        Long count = redisTemplate.opsForValue().increment(key);

       // Check for null to avoid NullPointerException
        if (count == null) {
            logger.error("Redis increment returned null for user {}", userId);
            throw new IllegalStateException("Failed to increment rate limit counter in Redis");
        }

        // We only set expiration on the first increment (count == 1)
        if (count == 1) {
            redisTemplate.expire(key, Duration.ofSeconds(windowSizeSeconds));
        }

        // Check limit after incrementing ensures we count the current request
        // and provides accurate remaining time information
        if (count > rateLimitProperties.getMaxTransfers()) {
            Long ttl = redisTemplate.getExpire(key);
            throw new RateLimitExceededException(
                    String.format("Rate limit exceeded. Maximum %d transfers allowed per %d minutes. Try again in %d seconds.",
                            rateLimitProperties.getMaxTransfers(),
                            rateLimitProperties.getWindowSizeMinutes(),
                            ttl != null ? ttl : 0)
            );
        }
    }
}
