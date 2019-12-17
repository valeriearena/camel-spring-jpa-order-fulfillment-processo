package com.pluralsight.orderfulfillment.config;

import javax.inject.Inject;

import org.apache.activemq.camel.component.ActiveMQComponent;
import org.apache.activemq.pool.PooledConnectionFactory;
import org.apache.camel.component.jms.JmsConfiguration;
import org.apache.camel.spring.javaconfig.CamelConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import com.pluralsight.orderfulfillment.order.OrderStatus;
/**
 * Spring configuration for Apache Camel
 * 
 * @author Michael Hoffman, Pluralsight
 */
@Configuration
@ComponentScan("com.pluralsight.orderfulfillment")
public class IntegrationConfig extends CamelConfiguration {

   @Inject
   private javax.sql.DataSource dataSource;

   @Inject
   private Environment environment;

   @Bean
   public javax.jms.ConnectionFactory jmsConnectionFactory() {
      return new org.apache.activemq.ActiveMQConnectionFactory(environment.getProperty("activemq.broker.url"));
   }

   @Bean(initMethod = "start", destroyMethod = "stop")
   public org.apache.activemq.pool.PooledConnectionFactory pooledConnectionFactory() {
      PooledConnectionFactory factory = new PooledConnectionFactory();
      factory.setConnectionFactory(jmsConnectionFactory());
      factory.setMaxConnections(Integer.parseInt(environment
              .getProperty("pooledConnectionFactory.maxConnections")));
      return factory;
   }

   @Bean
   public org.apache.camel.component.jms.JmsConfiguration jmsConfiguration() {
      JmsConfiguration jmsConfiguration = new JmsConfiguration();
      jmsConfiguration.setConnectionFactory(pooledConnectionFactory());
      return jmsConfiguration;
   }

   @Bean
   public org.apache.activemq.camel.component.ActiveMQComponent activeMq() {
      ActiveMQComponent activeMq = new ActiveMQComponent();
      activeMq.setConfiguration(jmsConfiguration());
      return activeMq;
   }

   /**
    * SQL Component instance used for routing orders from the orders database
    * and updating the orders database.
    *
    * @return
    */
   @Bean
   public org.apache.camel.component.sql.SqlComponent sql() {
      org.apache.camel.component.sql.SqlComponent sqlComponent = new org.apache.camel.component.sql.SqlComponent();
      sqlComponent.setDataSource(dataSource);
      return sqlComponent;
   }

   /**
    * Camel RouteBuilder for routing orders from the orders database. Routes any
    * orders with status set to new, then updates the order status to be in
    * process. The route sends the message exchange to a log component.
    *
    * @return
    */
   @Bean
   public org.apache.camel.builder.RouteBuilder newWebsiteOrderRoute() {

      return new org.apache.camel.builder.RouteBuilder() {

         @Override
         public void configure() throws Exception {
            // Route that sends message from the SQL component to the Log component.
            from("sql:"
                            + "select id from pluralsightorder where status = '"+ OrderStatus.NEW.getCode() + "'"
                            + "?"
                            + "consumer.onConsume=update pluralsightorder set status = '" + OrderStatus.PROCESSING.getCode() + "' "
                            + " where id = :#id")
                    .beanRef("orderItemMessageTranslator", "transformToOrderItemMessage")
                    .to("activemq:queue:ORDER_ITEM_PROCESSING");
         }
      };


//      return new org.apache.camel.builder.RouteBuilder() {
//
//         @Override
//         public void configure() throws Exception {
//            // Route that sends message from the SQL component to the Log component.
//            from( // SQL component
//                    "sql:"
//                            + "select id from pluralsightorder where status = '"+ OrderStatus.NEW.getCode() + "'"
//                            + "?"
//                            + "consumer.onConsume=update pluralsightorder set status = '" + OrderStatus.PROCESSING.getCode() + "' "
//                            + " where id = :#id")
//            .beanRef("orderItemMessageTranslator", "transformToOrderItemMessage")
//            .to( // Log component
//                    "log:com.pluralsight.orderfulfillment.order?level=INFO");
//         }
//      };


      //   @Override
//   public List<RouteBuilder> routes() {
//      List<RouteBuilder> routeList = new ArrayList<RouteBuilder>();
//
//      routeList.add(new RouteBuilder() {
//
//         @Override
//         public void configure() throws Exception {
//            from( // calling this method creates a route
//                  "file://" // URI for file endpoint
//                        + environment
//                              .getProperty("order.fulfillment.center.1.outbound.folder")
//                        + "?noop=true")
//            .to(  // tells camel to route the message to the URI specified
//                    "file://" // URI for file endpoint
//                        + environment
//                              .getProperty("order.fulfillment.center.1.outbound.folder")
//                        + "/test");
//         }
//      });
//
//      return routeList;
//   }


   }


}
