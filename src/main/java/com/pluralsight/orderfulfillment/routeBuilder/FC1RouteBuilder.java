package com.pluralsight.orderfulfillment.routeBuilder;

import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;

/**
 * Routes the message from the FC1_FULFILLMENT_REQUEST queue to a RESTful web service.
 *
 * This route will first consume a message from the FC1_FULFILLMENT_REQUEST ActiveMQ queue.
 * The message body will be an order in XML format.
 * The message will then be passed to the fulfillment center one processor where it will be transformed from the XML to JSON format.
 * Next, the message header content type will be set as JSON format and a message will be posted to the fulfillment center one RESTful web service.
 * If the response is success, the route will be complete. If not, the route will error out.
 *
 * HTTP Method:
 *  - If method set on header, Camel uses that.
 *  - If method not set on header, Camel checks if query string is set on header.
 *  - If query string is set on header, Camel uses the GET method.
 *  - If query string is not sent on header but the endpoint has a query string, Camel uses the GET method.
 *  - If header or endpoint are not configured with a query string and message body is not null Camel uses POST. Otherwise it uses GET.
 *
 *  The response is received, the response is placed in the out message body.
 *  If the response code is 300 or greater, Camel considers a failure and throws an exception.
 *
 * @return
 */

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
