package com.pluralsight.orderfulfillment.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.view.UrlBasedViewResolver;
import org.springframework.web.servlet.view.tiles2.TilesConfigurer;
import org.springframework.web.servlet.view.tiles2.TilesView;

/**
 * Web configuration.
 * <p>
 * Both Spring can be configured via Java annotations or XML.
 * Java configuration is recommended:
 * 1. Java is more powerful.
 * 1. Java configurations gives type safety and can be checked at compile time. XML configuration is only checked at runtime.
 * 2. Easier to work with in IDE - code completion, refactoring, finding references, etc.
 * 3. Complex configurations in XML can be hard to read and maintain.
 * <p>
 * Adding the @EnableWebMvc annotation to an @Configuration class imports the Spring MVC configuration, from WebMvcConfigurationSupport.
 * This is the main class providing the configuration behind the MVC Java config.
 * <p>
 * NOTE: When Spring sees @Bean, it will execute the method and register the return value as a bean within Spring context.
 * By default, the bean name will be the same as the method name.
 */
@Configuration
@EnableWebMvc
public class WebConfig extends WebMvcConfigurerAdapter {

  @Bean
  public ViewResolver viewResolver() {
    UrlBasedViewResolver viewResolver = new UrlBasedViewResolver();
    viewResolver.setViewClass(TilesView.class);
    return viewResolver;
  }

  /**
   * Configures Tiles at application startup.
   */
  @Bean
  public TilesConfigurer tilesConfigurer() {
    TilesConfigurer configurer = new TilesConfigurer();
    configurer.setDefinitions(new String[] {"/WEB-INF/jsp/tiles.xml"});
    configurer.setCheckRefresh(true);
    return configurer;
  }

  public void addResourceHandlers(ResourceHandlerRegistry registry) {
    registry.addResourceHandler("/resources/**").addResourceLocations("/resources/");
  }

}
