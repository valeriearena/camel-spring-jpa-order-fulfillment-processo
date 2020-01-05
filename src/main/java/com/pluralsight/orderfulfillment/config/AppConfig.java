package com.pluralsight.orderfulfillment.config;

import com.pluralsight.orderfulfillment.order.DefaultOrderService;
import com.pluralsight.orderfulfillment.order.OrderService;
import com.pluralsight.orderfulfillment.order.fulfillment.FulfillmentCommand;
import com.pluralsight.orderfulfillment.order.fulfillment.FulfillmentProcessor;
import com.pluralsight.orderfulfillment.order.fulfillment.NewOrderRetrievalCommand;
import com.pluralsight.orderfulfillment.order.fulfillment.NewOrderSendToFulfillmentCenterOneCommand;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * Main application configuration for the order fulfillment processor.
 *
 * @author Michael Hoffman, Pluralsight
 */
@Configuration
@Import( {JMSConfig.class, DataConfig.class, WebConfig.class, IntegrationConfig.class})
public class AppConfig {

  // ************* Spring beans used by the code that was refactored and replaced by Camel routes. *************

  @Bean
  public OrderService orderService() {
    return new DefaultOrderService();
  }

  @Bean
  public FulfillmentProcessor fulfillmentProcessor() {
    return new FulfillmentProcessor();
  }

  @Bean
  public FulfillmentCommand newOrderRetrievalCommand() {
    return new NewOrderRetrievalCommand();
  }

  @Bean
  public FulfillmentCommand newOrderSendToFulfillmentCenterOneCommand() {
    return new NewOrderSendToFulfillmentCenterOneCommand();
  }

}
