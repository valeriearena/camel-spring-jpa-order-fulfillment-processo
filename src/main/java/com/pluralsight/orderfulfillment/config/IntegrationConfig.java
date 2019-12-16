package com.pluralsight.orderfulfillment.config;

import java.util.*;

import javax.inject.*;

import com.pluralsight.orderfulfillment.order.OrderStatus;
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
//@ComponentScan("com.pluralsight.orderfulfillment")
public class IntegrationConfig extends CamelConfiguration {

   @Inject
   private Environment environment;

   @Inject
   private javax.sql.DataSource dataSource;

   /**
    * SQL Component instance used for routing orders from the orders database
    * and updating the orders database.
    *
    * @return
    */
   @Bean
   public org.apache.camel.component.sql.SqlComponent sql() {
      org.apache.camel.component.sql.SqlComponent sqlComponent = new org.apache.camel.component.sql.SqlComponent();
      sqlComponent.setDataSource(dataSource);
      return sqlComponent;
   }

   /**
    * Camel RouteBuilder for routing orders from the orders database. Routes any
    * orders with status set to new, then updates the order status to be in
    * process. The route sends the message exchange to a log component.
    *
    * @return
    */
   @Bean
   public org.apache.camel.builder.RouteBuilder newWebsiteOrderRoute() {
      return new org.apache.camel.builder.RouteBuilder() {

         @Override
         public void configure() throws Exception {
            // Route that sends message from the SQL component to the Log component.
            from( // SQL component
                    "sql:"
                            + "select id from pluralsightorder where status = '"+ OrderStatus.NEW.getCode() + "'"
                            + "?"
                            + "consumer.onConsume=update pluralsightorder set status = '" + OrderStatus.PROCESSING.getCode() + "' "
                            + " where id = :#id")
            .to( // Log component
                    "log:com.pluralsight.orderfulfillment.order?level=INFO");
         }
      };
   }

//   @Override
//   public List<RouteBuilder> routes() {
//      List<RouteBuilder> routeList = new ArrayList<RouteBuilder>();
//
//      routeList.add(new RouteBuilder() {
//
//         @Override
//         public void configure() throws Exception {
//            from( // calling this method creates a route
//                  "file://" // URI for file endpoint
//                        + environment
//                              .getProperty("order.fulfillment.center.1.outbound.folder")
//                        + "?noop=true")
//            .to(  // tells camel to route the message to the URI specified
//                    "file://" // URI for file endpoint
//                        + environment
//                              .getProperty("order.fulfillment.center.1.outbound.folder")
//                        + "/test");
//         }
//      });
//
//      return routeList;
//   }

}
