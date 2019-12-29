package com.pluralsight.orderfulfillment.routeBuilder;

import com.pluralsight.orderfulfillment.abcfulfillmentcenter.ABCFulfillmentCenterAggregationStrategy;
import com.pluralsight.orderfulfillment.generated.FulfillmentCenter;
import java.text.SimpleDateFormat;
import org.apache.camel.CamelExchangeException;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.builder.xml.Namespaces;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class ABCRouteBuilder extends RouteBuilder {

  @Value("${order.fulfillment.center.1.outbound.folder}")
  private String folder;

  @Override
  public void configure() throws Exception {

    // Namespace is needed for XPath lookup
    Namespaces namespace = new org.apache.camel.builder.xml.Namespaces("o", "http://www.pluralsight.com/orderfulfillment/Order");
    SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd-hhmmss");
    String dateString = sdf.format(new java.util.Date(System.currentTimeMillis()));

    onException(CamelExchangeException.class).to("activemq:queue:ABC_FULFILLMENT_ERROR");

    // 1 - Route from the direct component to an ActiveMQ component
    from("direct:test").to("activemq:queue:ABC_FULFILLMENT_REQUEST");

    // 2 - Aggregate XML messages from the queue.
    from("activemq:queue:ABC_FULFILLMENT_REQUEST")
        .aggregate(new ABCFulfillmentCenterAggregationStrategy())
        .xpath("//*[contains(text(), '" + FulfillmentCenter.ABC_FULFILLMENT_CENTER.value() + "')]", String.class, namespace)
        .completionInterval(10000)
        .beanRef("aBCFulfillmentProcessor", "processAggregate")
        .marshal()
        .csv()
        .to("file://"+folder+"?fileName=abcfc-" + dateString + ".csv")
        .setHeader("CamelFileName", constant("abcfc-" + dateString + ".csv"))
        .to("sftp://corp.mobileheartbeat.com:22?username=valerie.arena&password=august142010#");

  }
}
