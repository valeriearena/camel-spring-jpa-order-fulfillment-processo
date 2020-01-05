package com.pluralsight.orderfulfillment.routeBuilder;

import org.apache.camel.builder.RouteBuilder;
import org.springframework.beans.factory.annotation.Value;

/**
 * Polls for new files in the out folder and copies them to the test folder.
 */
//@Component
public class FileRouteBuilder extends RouteBuilder {

//  @Inject
//  private Environment environment;

  @Value("${order.fulfillment.center.1.outbound.folder}")
  private String folder;

  public FileRouteBuilder() {
  }

  @Override
  public void configure() throws Exception {

    getContext().setTracing(true);

    from("file://" + folder + "?noop=true")
        .to("file://" + folder + "/test");

  }

}
