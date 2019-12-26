package com.pluralsight.orderfulfillment.config;

import com.pluralsight.orderfulfillment.routeBuilder.FileRouteBuilder;
import com.pluralsight.orderfulfillment.routeBuilder.FulfillmentCenterRouteBuilder;
import com.pluralsight.orderfulfillment.routeBuilder.NewOrderRouteBuilder;
import javax.inject.Inject;
import javax.jms.ConnectionFactory;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.camel.component.ActiveMQComponent;
import org.apache.activemq.pool.PooledConnectionFactory;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.jms.JmsConfiguration;
import org.apache.camel.component.sql.SqlComponent;
import org.apache.camel.spring.javaconfig.CamelConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

/**
 * Spring configuration for Apache Camel.
 *
 * Both Spring and Camel can be configured via Java annotations or XML.
 * Java configuration is recommended:
 *    1. Java configurations gives type safety and can be checked at compile time. XML configuration is only checked at runtime.
 *    2. Easier to work with in IDE - code completion, refactoring, finding references, etc.
 *    3. Complex configurations in XML can be hard to read and maintain.
 *
 * NOTE: When Spring sees @Bean, it will execute the method and register the return value as a bean within Spring context.
 * By default, the bean name will be the same as the method name.
 *
 */
@Configuration
@ComponentScan("com.pluralsight.orderfulfillment")
public class IntegrationConfig extends CamelConfiguration { // Configure Camel in Spring context.

   @Inject
   private javax.sql.DataSource dataSource;

   @Inject
   private Environment environment;

   /**
    * Camel SQL Component.
    */
   @Bean
   public SqlComponent sql() {
      SqlComponent sqlComponent = new SqlComponent();
      sqlComponent.setDataSource(dataSource);
      return sqlComponent;
   }

   /**
    * Camel ActiveMQ Component.
    */
   @Bean
   public ActiveMQComponent activeMq() {
      ActiveMQComponent activeMq = new ActiveMQComponent();
      activeMq.setConfiguration(jmsConfiguration());
      return activeMq;
   }

   @Bean
   public ConnectionFactory jmsConnectionFactory() {
      return new ActiveMQConnectionFactory(environment.getProperty("activemq.broker.url"));
   }

   @Bean(initMethod = "start", destroyMethod = "stop")
   public PooledConnectionFactory pooledConnectionFactory() {
      PooledConnectionFactory factory = new PooledConnectionFactory();
      factory.setConnectionFactory(jmsConnectionFactory());
      factory.setMaxConnections(Integer.parseInt(environment
              .getProperty("pooledConnectionFactory.maxConnections")));
      return factory;
   }

   @Bean
   public JmsConfiguration jmsConfiguration() {
      JmsConfiguration jmsConfiguration = new JmsConfiguration();
      jmsConfiguration.setConnectionFactory(pooledConnectionFactory());
      return jmsConfiguration;
   }

   /*
    * The route copies a file to the /test directory.
    *
    * If you wish to create a collection of RouteBuilder instances then implement the routes() method.
    * Keep in mind that if you don’t override routes() method, then CamelConfiguration will use
    * all RouteBuilder instances available in the Spring context.
    */
//   @Override
//   public List<RouteBuilder> routes() {
//
//      List<RouteBuilder> routeList = new ArrayList<RouteBuilder>();
//
//      routeList.add(new RouteBuilder() {
//
//         @Override
//         public void configure() throws Exception {
//            from( "file://" + environment.getProperty("order.fulfillment.center.1.outbound.folder") + "?noop=true")
//            .to("file://" + environment.getProperty("order.fulfillment.center.1.outbound.folder") +"/test");
//         }
//
//      });
//
//      return routeList;
//   }


   /**
    * Routes file to the /test directory.
    */
   //@Bean
   public RouteBuilder getCopyFileRouteBuilder() {

      String outFolder = environment.getProperty("order.fulfillment.center.1.outbound.folder");
      String testFolder = environment.getProperty("order.fulfillment.center.1.outbound.folder");

      return new FileRouteBuilder(outFolder, testFolder);

   }

   /**
    * Routes new orders to the ORDER_ITEM_PROCESSING queue.
    */
   //@Bean
   public RouteBuilder getWebsiteOrderRouteBuilder() {

      return new NewOrderRouteBuilder();

   }

   /**
    * Routes orders from the ORDER_ITEM_PROCESSING queue to the appropriate fulfillment center.
    */
   //@Bean
   public RouteBuilder getFulfillmentCenterContentBasedRouteBuilder() {

      return new FulfillmentCenterRouteBuilder();
   }


}
