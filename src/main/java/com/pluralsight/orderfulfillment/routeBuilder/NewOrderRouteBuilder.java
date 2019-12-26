package com.pluralsight.orderfulfillment.routeBuilder;

import com.pluralsight.orderfulfillment.order.OrderStatus;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

/**
 * Polls for any orders with status set to new, updates the order status to be in process, then route sends the orders to ActiveMQ endpoint.
 */
@Component
public class NewOrderRouteBuilder extends RouteBuilder {

  @Override
  public void configure() throws Exception {

    getContext().setTracing(true);

    from("sql:" // from endpoint tells Camel where to get the data from the URI specified.
        + "select id from pluralsightorder where status = '"+ OrderStatus.NEW.getCode() + "'"
        + "?"
        + "consumer.onConsume=update pluralsightorder set status = '" + OrderStatus.PROCESSING.getCode() + "' where id = :#id&consumer.delay=5000")
    .beanRef("orderItemMessageTranslator", "transformToOrderItemMessage")  // Call a bean that's registered with Camel and execute the method for translation.
    .to("activemq:queue:ORDER_ITEM_PROCESSING"); // to endpoint tells Camel to route the message to the URI specified.
  }

}
