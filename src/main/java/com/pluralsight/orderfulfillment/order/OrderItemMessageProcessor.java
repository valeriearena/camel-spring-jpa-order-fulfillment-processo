package com.pluralsight.orderfulfillment.order;

import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

public class OrderItemMessageProcessor {

  private static final Logger log = LoggerFactory
      .getLogger(OrderItemMessageProcessor.class);

  @Autowired
  private OrderService orderService;

  /**
   * Camel binds the body of the SQL component message to the Map parameter.
   *
   * The Log component logs the transformed message.
   * The log output can be used to identify message types.
   * You can also use the Camel component documention, which will specify the return type.
   * Exchange [ExchangePattern: InOnly, BodyType: org.springframework.util.LinkedCaseInsensitiveMap, Body: {id=1}]
   *
   * Log component URI syntax: log:[package][?options]
   * from("sql:"
   *  + "select id from pluralsightorder where status = '"+ OrderStatus.NEW.getCode() + "'"
   *  + "?"
   *  + "consumer.onConsume=update pluralsightorder set status = '" + OrderStatus.PROCESSING.getCode() + "' where id = :#id&consumer.delay=5000")
   *  .to( "log:com.pluralsight.orderfulfillment.order?level=INFO");
   *
   * NOTE: Depending on the component, message types can be specified as an option in the URI.
   *
   */
  public String transformToXML(Map<String, Object> orderIds) {
    String output = null;
    try {
      if (orderIds == null) {
        throw new Exception(
            "Order id was not bound to the method via integration framework.");
      }
      if (!orderIds.containsKey("id")) {
        throw new Exception("Could not find a valid key of 'id' for the order ID.");
      }
      if (orderIds.get("id") == null || !(orderIds.get("id") instanceof Long)) {
        throw new Exception("The order ID was not correctly provided or formatted.");

      }

      output = orderService.processCreateOrderMessage((Long) orderIds.get("id"));
    } catch (Exception e) {
      log.error("Order processing failed: " + e.getMessage(), e);
    }
    return output;
  }
}
