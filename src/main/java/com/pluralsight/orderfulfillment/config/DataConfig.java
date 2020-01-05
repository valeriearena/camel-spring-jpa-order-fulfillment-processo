package com.pluralsight.orderfulfillment.config;

import java.util.HashMap;
import java.util.Map;
import javax.inject.Inject;
import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import org.apache.commons.dbcp.BasicDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.hibernate4.HibernateExceptionTranslator;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * Data configuration.
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

// Usee @ComponentScan annotation along with @Configuration annotation to specify the packages that we want to be scanned.
// @ComponentScan tells Spring to scan the current package and all of its sub-packages.
@Configuration
@EnableJpaRepositories(basePackages = {"com.pluralsight.orderfulfillment"})
@EnableTransactionManagement
@PropertySource("classpath:order-fulfillment.properties")
public class DataConfig {

  @Autowired
  private Environment environment;

  @Bean
  public DataSource dataSource() {
    BasicDataSource dataSource = new BasicDataSource();
    dataSource.setDriverClassName(environment.getProperty("db.driver"));
    dataSource.setUrl(environment.getProperty("db.url"));
    dataSource.setUsername(environment.getProperty("db.user"));
    dataSource.setPassword(environment.getProperty("db.password"));
    return dataSource;
  }

  @Bean
  public EntityManagerFactory entityManagerFactory() {
    final HibernateJpaVendorAdapter jpaVendorAdapter = new HibernateJpaVendorAdapter();
    jpaVendorAdapter.setDatabasePlatform(environment.getProperty("hibernate.dialect"));
    //jpaVendorAdapter.setGenerateDdl(true);
    jpaVendorAdapter.setShowSql(true);

    final Map<String, String> jpaProperties = new HashMap<String, String>();
    jpaProperties.put("hibernate.jdbc.batch_size", environment.getProperty("hibernate.jdbc.batch_size"));
    jpaProperties.put("hibernate.default_schema", environment.getProperty("hibernate.default_schema"));
    LocalContainerEntityManagerFactoryBean factory = new LocalContainerEntityManagerFactoryBean();
    factory.setPackagesToScan("com.pluralsight.orderfulfillment");
    factory.setJpaVendorAdapter(jpaVendorAdapter);
    factory.setDataSource(dataSource());
    factory.setJpaPropertyMap(jpaProperties);
    factory.afterPropertiesSet();
    return factory.getObject();
  }

  @Bean
  public PlatformTransactionManager transactionManager() {
    JpaTransactionManager transactionManager = new JpaTransactionManager();
    transactionManager.setEntityManagerFactory(entityManagerFactory());
    return transactionManager;
  }

  @Bean
  public HibernateExceptionTranslator hibernateExceptionTranslator() {
    return new HibernateExceptionTranslator();
  }

}
