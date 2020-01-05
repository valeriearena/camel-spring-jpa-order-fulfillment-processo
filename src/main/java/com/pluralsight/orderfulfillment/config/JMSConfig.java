package com.pluralsight.orderfulfillment.config;

import javax.inject.Inject;
import javax.jms.ConnectionFactory;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.pool.PooledConnectionFactory;
import org.apache.camel.component.jms.JmsConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;

/**
 * JMS configuration.
 *
 * Both Spring can be configured via Java annotations or XML.
 * Java configuration is recommended:
 * 1. Java is more powerful.
 * 1. Java configurations gives type safety and can be checked at compile time. XML configuration is only checked at runtime.
 * 2. Easier to work with in IDE - code completion, refactoring, finding references, etc.
 * 3. Complex configurations in XML can be hard to read and maintain.
 *
 * NOTE: When Spring sees @Bean, it will execute the method and register the return value as a bean within Spring context.
 * By default, the bean name will be the same as the method name.
 *
 */
@Configuration
@PropertySource("classpath:order-fulfillment.properties")
public class JMSConfig {

  @Autowired
  private Environment environment;

  @Bean
  public JmsConfiguration jmsConfiguration() {
    JmsConfiguration jmsConfiguration = new JmsConfiguration();
    jmsConfiguration.setConnectionFactory(pooledConnectionFactory());
    return jmsConfiguration;
  }

  @Bean(initMethod = "start", destroyMethod = "stop")
  public PooledConnectionFactory pooledConnectionFactory() {
    PooledConnectionFactory factory = new PooledConnectionFactory();
    factory.setConnectionFactory(jmsConnectionFactory());
    factory.setMaxConnections(Integer.parseInt(environment.getProperty("pooledConnectionFactory.maxConnections")));
    return factory;
  }

  @Bean
  public ConnectionFactory jmsConnectionFactory() {
    return new ActiveMQConnectionFactory(environment.getProperty("activemq.broker.url"));
  }

}
