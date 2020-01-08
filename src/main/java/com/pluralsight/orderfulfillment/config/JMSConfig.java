package com.pluralsight.orderfulfillment.config;

import javax.jms.ConnectionFactory;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.pool.PooledConnectionFactory;
import org.apache.camel.component.jms.JmsConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

@Configuration
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
