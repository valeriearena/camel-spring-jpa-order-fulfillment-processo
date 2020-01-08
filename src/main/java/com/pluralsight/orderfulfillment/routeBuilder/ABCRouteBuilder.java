package com.pluralsight.orderfulfillment.routeBuilder;

import com.pluralsight.orderfulfillment.abcfulfillmentcenter.ABCFulfillmentCenterAggregationStrategy;
import com.pluralsight.orderfulfillment.generated.FulfillmentCenter;
import org.apache.camel.CamelExchangeException;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.builder.xml.Namespaces;
import org.springframework.beans.factory.annotation.Value;

/**
 * Routes the message from the ABC_FULFILLMENT_REQUEST queue to a SFTP server.
 *
 * The route takes messages from the ABC_FULFILLMENT_REQUEST queue and aggregates them.
 *
 * An aggregate processor is used to implement the aggregate processor.
 *  - The correlation expression uses xpath to evalute the messages to check if this is a message that needs to be aggregated.
 *  - The completion condition tells us when to stop.
 *  - The AggregationStrategy aggregates the messages.
 *  - Because the Aggregator router is a stateful processor, the aggregate must be persisted. In-memory persistence is the default and is what we are using.
 *
 * The aggregation is then processed by a bean that creates a list of maps for the orders.
 * The list of maps is then marshalled to CSV.
 * The CSV data is saved to a file and then SFTP'd to the server.
 *
 * An exception dead letter channel was defined for any exceptions that occur related to camel exchange processing.
 * - onException is the equivalent of a try/catch block.
 * - In this example, messages will be rerouted to an failure queue.
 * - Camel also provides support for the redelivery of messsages via a redelivery policy.
 *
 */

//@Component
public class ABCRouteBuilder extends RouteBuilder {

  @Value("${order.fulfillment.center.1.outbound.folder}")
  private String folder;

  @Override
  public void configure() throws Exception {

    // We need the namespace so that we can look up the XML element correctly in XPATH.
    Namespaces namespace = new org.apache.camel.builder.xml.Namespaces("o", "http://www.pluralsight.com/orderfulfillment/Order");

    onException(CamelExchangeException.class).to("activemq:queue:ABC_FULFILLMENT_ERROR");

    from("activemq:queue:ABC_FULFILLMENT_REQUEST")
        .aggregate(new ABCFulfillmentCenterAggregationStrategy())
          .xpath("//*[contains(text(), '" + FulfillmentCenter.ABC_FULFILLMENT_CENTER.value() + "')]", String.class, namespace)
          .completionInterval(10000)
        .beanRef("aBCFulfillmentProcessor", "processAggregate")
        .marshal()
          .csv()
        .to("file://" + folder + "?fileName=abc-fulfillment-center.csv")
        .setHeader("CamelFileName", constant("abc-fulfillment-center.csv"))
        .to("sftp://corp.mobileheartbeat.com:22?username=valerie.arena&password=august142010#");

  }
}
