package com.pluralsight.orderfulfillment.routeBuilder;

import com.pluralsight.orderfulfillment.order.OrderStatus;
import org.apache.camel.builder.RouteBuilder;

/**
 *
 * Polls for any orders with status set to new, updates the order status to be in process, then route sends the orders to ActiveMQ endpoint.
 *
 * SQL component URI syntax: sql:[sql-statement][?options]
 *
 * Poll database for new orders.
 * Call the OrderItemMessageTranslator bean and execute the method for translation.
 * Send new orders to the ORDER_ITEM_PROCESSING queue.
 *
 * We are using beanRef for message transformation using Camel's ApplicationContextRegistry to look up objects. That's how Camel finds Spring beans.
 * We pass in the bean ID to beanRef and the method to call on the bean.
 * The beanRef method will automatically bind the body of the inbound message to the parameter we defined in the method.
 * The beanRef will read the orderId from the inbound message and will return XML (defined by the XSD).
 *
 * The exchange is transformed at each step.
 * The Log component logs the transformed message to a log file, which can be used to view the exchange and how it's transformed.
 *
 * The following is an example of a message transformation of an exchange sent to ABCFulfillmentCenter.
 *
 *  Exchange[ExchangePattern: InOnly, BodyType: org.springframework.util.LinkedCaseInsensitiveMap, Body: {id=1}]
 *  Exchange[ExchangePattern: InOnly, BodyType: String, Body: <?xml version="1.0" encoding="UTF-8" standalone="yes"?><Order xmlns="http://www.pluralsight.com/orderfulfillment/Order">    <OrderType>        <FirstName>Michael</FirstName>        <LastName>Hoffman</LastName>        <Email>mike@michaelhoffmaninc.com</Email>        <OrderNumber>1001</OrderNumber>        <TimeOrderPlaced>2019-12-17T06:39:50.987-05:00</TimeOrderPlaced>        <FulfillmentCenter>FulfillmentCenterOne</FulfillmentCenter>        <OrderItems>            <ItemNumber>44910432221</ItemNumber>            <Price>1.25000</Price>            <Quantity>10</Quantity>        </OrderItems>        <OrderItems>            <ItemNumber>078-1344200444</ItemNumber>            <Price>20.00000</Price>            <Quantity>1</Quantity>        </OrderItems>    </OrderType></Order>]
 *
 */
//@Component
public class NewOrderRouteBuilder extends RouteBuilder {

  @Override
  public void configure() throws Exception {

    getContext().setTracing(true);

    from("sql:" // from URI is using the SQL component.
        + "select id from pluralsightorder where status = '" + OrderStatus.NEW.getCode() + "'"
        + "?"
        + "onConsume=update pluralsightorder set status = '" + OrderStatus.PROCESSING.getCode() + "' where id = :#id&delay=5000") // Polls every 5000 ms.
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
