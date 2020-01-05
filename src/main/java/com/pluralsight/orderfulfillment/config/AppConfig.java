package com.pluralsight.orderfulfillment.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * Main application configuration for the order fulfillment processor.
 *
 * @author Michael Hoffman, Pluralsight
 */
@Configuration
@Import({DataConfig.class, WebConfig.class, IntegrationConfig.class})
public class AppConfig {

}
