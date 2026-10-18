package com.example.expenselimit.config;

import java.math.BigDecimal;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app")
public record AppProperties(String zoneId, BigDecimal defaultLimitUsd) {
}
