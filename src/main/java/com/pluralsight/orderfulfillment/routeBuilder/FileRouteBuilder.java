package com.pluralsight.orderfulfillment.routeBuilder;

import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

/**
 * Polls for new files in the out folder and copies them to the test folder.
 */
@Component
public class FileRouteBuilder extends RouteBuilder {

  private String outFolder;
  private String testFolder;

  public FileRouteBuilder(){}

  public FileRouteBuilder(String outFolder, String testFolder){
    this.outFolder = outFolder;
    this.testFolder = testFolder;
  }

  @Override
  public void configure() throws Exception {

    getContext().setTracing(true);

    from( "file://" + outFolder + "?noop=true")
        .to("file://" + testFolder +"/test");

  }

}
