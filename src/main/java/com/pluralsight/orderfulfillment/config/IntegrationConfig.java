package com.pluralsight.orderfulfillment.config;

import com.pluralsight.orderfulfillment.abcfulfillmentcenter.AbcFulfillmentProcessor;
import com.pluralsight.orderfulfillment.fulfillmentcenterone.service.FulfillmentCenterOneProcessor;
import com.pluralsight.orderfulfillment.order.OrderItemMessageProcessor;
import com.pluralsight.orderfulfillment.routeBuilder.AbcRouteBuilder;
import com.pluralsight.orderfulfillment.routeBuilder.Fc1RouteBuilder;
import com.pluralsight.orderfulfillment.routeBuilder.FulfillmentCenterRouteBuilder;
import com.pluralsight.orderfulfillment.routeBuilder.NewOrderRouteBuilder;
import javax.sql.DataSource;
import org.apache.activemq.camel.component.ActiveMQComponent;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.jms.JmsConfiguration;
import org.apache.camel.component.sql.SqlComponent;
import org.apache.camel.spring.javaconfig.CamelConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;

@Configuration
public class IntegrationConfig extends CamelConfiguration { // Configure Camel in Spring context.

  @Autowired
  private DataSource dataSource;

  @Autowired
  private JmsConfiguration jmsConfiguration;

  @Bean
  public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
    return new PropertySourcesPlaceholderConfigurer();
  }

  // ************* Camel Components *************

  /**
   * Camel SQL Component (camel-sql uses Spring JDBC library)
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
    activeMq.setConfiguration(jmsConfiguration);
    return activeMq;
  }

  // ************* Camel Routes *************
  // Routes defined as Spring beans are automatically scanned when loading the CamelContext into Spring.

  /**
   * Routes new orders to the ORDER_ITEM_PROCESSING queue.
   */
  @Bean
  public RouteBuilder newOrderRouteBuilder() {

    return new NewOrderRouteBuilder();

  }

  /**
   * Routes orders from the ORDER_ITEM_PROCESSING queue to the appropriate fulfillment center queue (ABC_FULFILLMENT_REQUEST or FC1_FULFILLMENT_REQUEST).
   */
  @Bean
  public RouteBuilder fulfillmentCenterRouteBuilder() {

    return new FulfillmentCenterRouteBuilder();
  }

  /**
   * Routes orders from the FC1_FULFILLMENT_REQUEST queue to /orderFulfillment/processOrders REST endpoint.
   */
  @Bean
  public RouteBuilder fc1RouteBuilder() {

    return new Fc1RouteBuilder();
  }

  /**
   * Routes orders from the ABC_FULFILLMENT_REQUEST queue to an FTP server.
   */
  @Bean
  public RouteBuilder abcRouteBuilder() {

    return new AbcRouteBuilder();
  }

  // ************* Camel Processors that transform the message *************
  @Bean
  public FulfillmentCenterOneProcessor fulfillmentCenterOneProcessor() {
    return new FulfillmentCenterOneProcessor();
  }

  @Bean
  public AbcFulfillmentProcessor abcFulfillmentProcessor() {
    return new AbcFulfillmentProcessor();
  }

  @Bean
  public OrderItemMessageProcessor orderItemMessageProcessor() {
    return new OrderItemMessageProcessor();
  }


  /**
   * Routes file to the /test directory.
   */
//  @Bean
//  public RouteBuilder fileRouteBuilder() {
//
//    return new ExampleRouteBuilder();
//
//  }

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

}
