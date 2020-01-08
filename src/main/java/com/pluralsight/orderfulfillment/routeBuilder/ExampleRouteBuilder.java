package com.pluralsight.orderfulfillment.routeBuilder;

import org.apache.camel.builder.RouteBuilder;
import org.springframework.beans.factory.annotation.Value;

/**
 * Polls for new files in the out folder and copies them to the test folder.
 */
//@Component
public class ExampleRouteBuilder extends RouteBuilder {

//  @Autowired
//  private Environment environment;

  @Value("${order.fulfillment.center.1.outbound.folder}")
  private String folder;

  @Override
  public void configure() throws Exception {

    getContext().setTracing(true);

    from("file://" + folder + "?noop=true&delay=5000")
        .to("file://" + folder + "/test");

  }

}
