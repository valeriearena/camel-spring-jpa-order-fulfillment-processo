package com.pluralsight.orderfulfillment.routeBuilder;

import com.pluralsight.orderfulfillment.abcfulfillmentcenter.AbcFulfillmentCenterAggregationStrategy;
import com.pluralsight.orderfulfillment.generated.FulfillmentCenter;
import org.apache.camel.CamelExchangeException;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.builder.xml.Namespaces;
import org.springframework.beans.factory.annotation.Value;

/**
 * Camel RouteBuilder for routing orders from ActiveMQ to an SFTP server.
 *
 * ActiveMQ Component URI syntax: activemq:[queue:|topic:]destinationName[?options]
 * File Component URI syntax: file://directoryName[?options]
 * SFTP Component URI syntax: sftp:host:port[/directoryName][options]
 *
 * NOTE: CSV Data Format uses Apache Commons CSV to handle CSV payloads.
 *
 * The route takes messages from the ABC_FULFILLMENT_REQUEST queue and aggregates them using an aggregate processor.
 * An aggregate processor is used to implement an Aggregator Router (which is a simple message router).
 * At the end of aggregation, the exchange inbound message has a list of XML messages.
 * The list of XML messages is then processed by AbcFulfillmentProcessor, which creates a list of maps for the orders (key = csv column, value = order info)
 * The list of maps is then marshalled into lines of CSV data. (marshal transforms the inbound message based on the format supplied, which is csv.)
 * The CSV data is saved to a file and then SFTP'd to the server after setting 'CamelFileName' header on the exchange, which will be the name of the file.
 *
 * An aggregate processor is used to implement an Aggregator Router .
 * - The correlation expression uses xpath to check if <FulfillmentCenter> element is equal to 'ABCFulfillmentCenter'.
 * - The completion condition tells the aggregate processor when to stop.
 * - The AggregationStrategy aggregates the messages.
 * - Because the Aggregator Router is a stateful processor, the aggregate must be persisted. In-memory persistence is the default and is what we are using.
 *
 * An exception channel was defined for any exceptions that occur related to camel exchange processing.
 * - onException clause is the equivalent of a try/catch block.
 * - onException is defined prior to the route.
 * - In this example, messages will be rerouted to an failure queue.
 * - Camel also provides support for the redelivery of messsages via a redelivery policy.
 */

//@Component
public class AbcRouteBuilder extends RouteBuilder {

  @Value("${order.fulfillment.center.1.outbound.folder}")
  private String folder;

  @Override
  public void configure() throws Exception {

    //getContext().setTracing(true);

    // Java DSL is easy to read and understand!!!

    // We need the namespace so that we can look up the XML element correctly in XPATH.
    Namespaces namespace = new org.apache.camel.builder.xml.Namespaces("o", "http://www.pluralsight.com/orderfulfillment/Order");

    onException(CamelExchangeException.class).to("activemq:queue:ABC_FULFILLMENT_ERROR");

    from("activemq:queue:ABC_FULFILLMENT_REQUEST")
        .aggregate(new AbcFulfillmentCenterAggregationStrategy())
          .xpath("//*[contains(text(), '" + FulfillmentCenter.ABC_FULFILLMENT_CENTER.value() + "')]", String.class, namespace)
          .completionInterval(10000)
        .beanRef("abcFulfillmentProcessor", "transformAggregate")
        .marshal()
        .csv()
        .to("file://" + folder + "?fileName=abc-fulfillment-center.csv")
        .setHeader("CamelFileName", constant("abc-fulfillment-center.csv"))
        .to("sftp://corp.mobileheartbeat.com:22?username=valerie.arena&password=august142010#");

  }
}
