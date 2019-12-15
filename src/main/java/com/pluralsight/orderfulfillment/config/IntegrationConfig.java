package com.pluralsight.orderfulfillment.config;

import java.util.*;

import javax.inject.*;

import org.apache.camel.builder.*;
import org.apache.camel.spring.javaconfig.*;
import org.springframework.context.annotation.*;
import org.springframework.core.env.*;

/**
 * Spring configuration for Apache Camel
 * 
 * @author Michael Hoffman, Pluralsight
 */
@Configuration
public class IntegrationConfig extends CamelConfiguration {

   @Inject
   private Environment environment;

   @Override
   public List<RouteBuilder> routes() {
      List<RouteBuilder> routeList = new ArrayList<RouteBuilder>();

      routeList.add(new RouteBuilder() {

         @Override
         public void configure() throws Exception {
            from(
                  "file://"
                        + environment
                              .getProperty("order.fulfillment.center.1.outbound.folder")
                        + "?noop=true")
                  .to("file://"
                        + environment
                              .getProperty("order.fulfillment.center.1.outbound.folder")
                        + "/test");
         }
      });

      return routeList;
   }

}
