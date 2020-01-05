package com.pluralsight.orderfulfillment.routeBuilder;

import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;

//@Component
public class FC1RouteBuilder extends RouteBuilder {

  @Override
  public void configure() throws Exception {
    from("activemq:queue:FC1_FULFILLMENT_REQUEST")
        .beanRef("fulfillmentCenterOneProcessor", "transformToOrderRequestMessage")
        .setHeader(Exchange.CONTENT_TYPE, constant("application/json"))
        .to("http4://localhost:8090/services/orderFulfillment/processOrders");
  }

}
