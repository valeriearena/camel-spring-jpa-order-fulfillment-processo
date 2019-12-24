package com.pluralsight.orderfulfillment.config;

import javax.inject.Inject;
import javax.jms.ConnectionFactory;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.camel.component.ActiveMQComponent;
import org.apache.activemq.pool.PooledConnectionFactory;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.builder.xml.Namespaces;
import org.apache.camel.component.jms.JmsConfiguration;
import org.apache.camel.component.sql.SqlComponent;
import org.apache.camel.spring.javaconfig.CamelConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import com.pluralsight.orderfulfillment.order.OrderStatus;

/**
 * Spring configuration for Apache Camel.
 * Configures the Camel context.
 *
 * A route tells Camel how a message will flow through a system.
 * https://camel.apache.org/components/latest/index.html
 *
 * We can use XML or DSL to define the routes.
 * https://camel.apache.org/manual/latest/scripting-languages.html
 *
 * Camel uses RouteBuilder for defining routes.
 * RouteBuilder is a Camel class that supports the Java DSL for building a route.
 *
 * 
 * @author Michael Hoffman, Pluralsight
 */
@Configuration
@ComponentScan("com.pluralsight.orderfulfillment")
public class IntegrationConfig extends CamelConfiguration { // Configure Camel in Spring context.

   @Inject
   private javax.sql.DataSource dataSource;

   @Inject
   private Environment environment;


   @Bean
   public SqlComponent sql() {
      SqlComponent sqlComponent = new org.apache.camel.component.sql.SqlComponent();
      sqlComponent.setDataSource(dataSource);
      return sqlComponent;
   }

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

   /**
    * Routes orders from the orders database.
    * Routes any orders with status set to new, then updates the order status to be in process.
    * The route sends the message to ActiveMQ endpoint.
    *
    * @return
    */
   @Bean
   public RouteBuilder newWebsiteOrderRoute() {

      return new RouteBuilder() {

         @Override
         public void configure() throws Exception {

            getContext().setTracing(true);

            from("sql:" // from endpoint tells Camel where to get the data from the URI specified.
                    + "select id from pluralsightorder where status = '"+ OrderStatus.NEW.getCode() + "'"
                    + "?"
                    + "consumer.onConsume=update pluralsightorder set status = '" + OrderStatus.PROCESSING.getCode() + "' "
                    + " where id = :#id")
            .beanRef("orderItemMessageTranslator", "transformToOrderItemMessage")
            .to("activemq:queue:ORDER_ITEM_PROCESSING"); // to endpoint tells Camel to route the message to the URI specified.
         }
      };


      /*
       * Routes orders from the orders database.
       * Routes any orders with status set to new, then updates the order status to be in process.
       * The route writes the message to a log.
       */
//      return new org.apache.camel.builder.RouteBuilder() {
//
//         @Override
//         public void configure() throws Exception {
//            // Route that sends message from the SQL component to the Log component.
//            from( "sql:"
//                            + "select id from pluralsightorder where status = '"+ OrderStatus.NEW.getCode() + "'"
//                            + "?"
//                            + "consumer.onConsume=update pluralsightorder set status = '" + OrderStatus.PROCESSING.getCode() + "' "
//                            + " where id = :#id")
//            .beanRef("orderItemMessageTranslator", "transformToOrderItemMessage")
//            .to( "log:com.pluralsight.orderfulfillment.order?level=INFO");
//         }
//      };


      /*
       * The route copies a file to the /test directory.
       */
//   @Override
//   public List<RouteBuilder> routes() {

//      List<RouteBuilder> routeList = new ArrayList<RouteBuilder>();
//
//      routeList.add(new RouteBuilder() {
//
//         @Override
//         public void configure() throws Exception {
//            from( "file://"
//                        + environment
//                              .getProperty("order.fulfillment.center.1.outbound.folder")
//                        + "?noop=true")
//            .to("file://"
//                        + environment
//                              .getProperty("order.fulfillment.center.1.outbound.folder")
//                        + "/test");
//         }
//      });
//
//      return routeList;
//   }


   }

   /**
    * Route builder to implement a Content-Based Router.
    * Routes the message from the ORDER_ITEM_PROCESSING queue to the appropriate queue
    * based on the fulfillment center element of the message.
    *
    * As the message from the ORDER_ITEM_PROCESSING queue is XML, a namespace is required.
    *
    * A Choice processor is used to realize the Content-Based Router.
    * When the Fulfillment Center element is equal to the value of the ABC fulfillment enter enumeration,
    *    the message will be routed to the ABC fulfillment center request queue.
    * When the Fulfillment Center element is equal to the value of the Fulfillment Center 1 enumeration value,
    *    the message will be routed to the Fulfillment Center 1 request queue.
    * If a message comes in with a Fulfillment Center element value that is unsupported,
    *    the message gets routed to an error queue.
    *
    * An XPath expression is used to lookup the fulfillment center value using the specified namespace.
    *
    * Below is a snippet of the XML returned by the ORDER_ITEM_PROCESSING queue.
    *
    * <Order xmlns="http://www.pluralsight.com/orderfulfillment/Order">
    * <OrderType> <FulfillmentCenter>ABCFulfillmentCenter</FulfillmentCenter>
    *
    * @return
    */
   @Bean
   public RouteBuilder fulfillmentCenterContentBasedRouter() {

      return new RouteBuilder() {

         @Override
         public void configure() throws Exception {

            getContext().setTracing(true);

            Namespaces namespace = new Namespaces("o","http://www.pluralsight.com/orderfulfillment/Order");

            // Send from the ORDER_ITEM_PROCESSING queue to the correct fulfillment center queue.
            from("activemq:queue:ORDER_ITEM_PROCESSING")
            .choice()
            .when()
            .xpath(
                "/o:Order/o:OrderType/o:FulfillmentCenter = '"
                + com.pluralsight.orderfulfillment.generated.FulfillmentCenter.ABC_FULFILLMENT_CENTER.value()
                + "'", namespace)
            .to("activemq:queue:ABC_FULFILLMENT_REQUEST")
            .when()
            .xpath(
               "/o:Order/o:OrderType/o:FulfillmentCenter = '"
               + com.pluralsight.orderfulfillment.generated.FulfillmentCenter.FULFILLMENT_CENTER_ONE.value()
               + "'", namespace)
            .to("activemq:queue:FC1_FULFILLMENT_REQUEST").otherwise()
            .to("activemq:queue:ERROR_FULFILLMENT_REQUEST");
         }
      };
   }


}
