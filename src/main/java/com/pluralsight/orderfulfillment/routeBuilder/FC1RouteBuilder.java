package com.pluralsight.orderfulfillment.routeBuilder;

import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;

/**
 * Camel RouteBuilder for routing orders from ActiveMQ to a REST endpoint.
 *
 * ActiveMQ Component URI syntax: activemq:[queue:|topic:]destinationName[?options]
 * HTTP4 Component URI syntax: http4:hostname[:port][/resourceUri][?options]
 *
 * NOTE: HTTP4 Component uses Apache HttpClient 4.x
 *
 * This route will first consume a message from the FC1_FULFILLMENT_REQUEST ActiveMQ queue.
 * The message body will be in XML format.
 * The message will then be passed to the FulfillmentCenterOneProcessor where it will be transformed from XML to JSON.
 * Next, the message header content type will be set as JSON format and a message will be posted to the fulfillment center one REST endpoint.
 * If the response is success, the route will be complete. If not, the route will error out.
 *
 * Camel checks the following to determine the HTTP method:
 * - If method set on header, Camel uses that.
 * - If method not set on header, Camel checks if query string is set on header.
 * - If query string is set on header, Camel uses the GET method.
 * - If query string is not sent on header but the endpoint has a query string, Camel uses the GET method.
 * - If header or endpoint are not configured with a query string and message body is not null Camel uses POST. Otherwise it uses GET.
 *
 *  The response is received, the response is placed in the out message body.
 *  If the response code is 300 or greater, Camel considers a failure and throws an exception.
 *
 *
 * @return
 */

//@Component
public class FC1RouteBuilder extends RouteBuilder {

  @Override
  public void configure() throws Exception {

    //getContext().setTracing(true);

    // Java DSL is easy to read and understand!!!

    from("activemq:queue:FC1_FULFILLMENT_REQUEST")
        .beanRef("fulfillmentCenterOneProcessor", "transformToJSON")
        .setHeader(Exchange.CONTENT_TYPE, constant("application/json"))
        .to("http4://localhost:8090/services/orderFulfillment/processOrders");
  }

}
