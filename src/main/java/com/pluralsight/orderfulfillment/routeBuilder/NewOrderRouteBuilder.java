package com.pluralsight.orderfulfillment.routeBuilder;

import com.pluralsight.orderfulfillment.order.OrderStatus;
import org.apache.camel.builder.RouteBuilder;

/**
 * Polls for any orders with status set to new, updates the order status to be in process, then route sends the orders to ActiveMQ endpoint.
 */
//@Component
public class NewOrderRouteBuilder extends RouteBuilder {

  @Override
  public void configure() throws Exception {

    getContext().setTracing(true);

    // SQL component URI syntax: sql:[sql-statement][?options]

    // Poll database for new orders.
    // Call the OrderItemMessageTranslator bean and execute the method for translation.
    // Send new orders to the ORDER_ITEM_PROCESSING queue.

    // We are using bean integration for message transformation using Camel's application context registry to look up objects. That's how Camel finds Spring beans.
    // We pass in the bean ID to beanRef and the method to call on the bean.
    // The beanRef method will automatically bind the body of the inbound message to the parameter we defined in the method.
    // The beanRef will read the orderId from the inbound message and will return XML (defined by the XSD).

    from("sql:" // from URI is using the SQL component.
        + "select id from pluralsightorder where status = '" + OrderStatus.NEW.getCode() + "'"
        + "?"
        + "onConsume=update pluralsightorder set status = '" + OrderStatus.PROCESSING.getCode() + "' where id = :#id&delay=5000")
        .beanRef("orderItemMessageTranslator", "transformToOrderItemMessage")
        .to("activemq:queue:ORDER_ITEM_PROCESSING");

  }


//  @Override
//  public void configure() throws Exception {
//
//    getContext().setTracing(true);
//
//    // Poll database for new orders
//    // Call the OrderItemMessageTranslator bean and execute the method for translation.
//    // Log the new orders.
//
//    from("sql:"
//        + "select id from pluralsightorder where status = '"+ OrderStatus.NEW.getCode() + "'"
//        + "?"
//        + "consumer.onConsume=update pluralsightorder set status = '" + OrderStatus.PROCESSING.getCode() + "' where id = :#id&consumer.delay=5000")
//    //.beanRef("orderItemMessageTranslator", "transformToOrderItemMessage")
//    .to( "log:com.pluralsight.orderfulfillment.order?level=INFO");
//  }

}
