package com.pluralsight.orderfulfillment.order;

import static org.junit.Assert.assertEquals;

import com.pluralsight.orderfulfillment.test.DerbyDatabaseBean;
import javax.inject.Inject;
import javax.sql.DataSource;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.sql.SqlComponent;
import org.apache.camel.spring.javaconfig.SingleRouteCamelConfiguration;
import org.apache.camel.test.spring.CamelSpringDelegatingTestContextLoader;
import org.apache.camel.test.spring.CamelSpringJUnit4ClassRunner;
import org.apache.commons.dbcp.BasicDataSource;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ContextConfiguration;

/**
 * Test case for testing the execution of the SQL component-based route for
 * routing orders from the orders database to a log component.
 * 
 * @author Michael Hoffman, Pluralsight
 * 
 */
@RunWith(CamelSpringJUnit4ClassRunner.class)
@ContextConfiguration(
    classes = { NewOrderRouteTest.TestConfig.class },
    loader = CamelSpringDelegatingTestContextLoader.class)
public class NewOrderRouteTest {

   @Inject
   private JdbcTemplate jdbcTemplate;

   @Configuration
   public static class TestConfig extends SingleRouteCamelConfiguration {

      @Inject
      private javax.sql.DataSource dataSource;

      @Bean
      public DataSource dataSource() {
         BasicDataSource dataSource = new BasicDataSource();
         dataSource.setDriverClassName("org.apache.derby.jdbc.EmbeddedDriver");
         dataSource.setUrl("jdbc:derby:memory:orders;create=true");
         return dataSource;
      }

      /**
       * Spring JDBC Template used for querying the Derby database.
       *
       * @return
       */
      @Bean
      public JdbcTemplate jdbcTemplate() {
         JdbcTemplate jdbc = new JdbcTemplate(dataSource());
         return jdbc;
      }

      @Bean
      public SqlComponent sql() {
         SqlComponent sqlComponent = new SqlComponent();
         sqlComponent.setDataSource(dataSource);
         return sqlComponent;
      }

      /**
       * Derby database bean used for creating and destroying the derby database as
       * part of the Spring container lifecycle. Note that the bean annotation sets
       * initMethod equal to the DerbyDatabaseBean method create and sets
       * destroyMethod to the DerbyDatabaseBean method destroy.
       *
       * @return
       */
      @Bean(initMethod = "create", destroyMethod = "destroy")
      public DerbyDatabaseBean derbyDatabaseBean() {
         DerbyDatabaseBean derby = new DerbyDatabaseBean();
         derby.setJdbcTemplate(jdbcTemplate());
         return derby;
      }

      @Bean
      @Override
      public RouteBuilder route() {
         return new RouteBuilder() {

            @Override
            public void configure() throws Exception {

               getContext().setTracing(true);

               from("sql:" // from endpoint tells Camel where to get the data from the URI specified.
                   + "select id from pluralsightorder where status = '"+ OrderStatus.NEW.getCode() + "'"
                   + "?"
                   + "consumer.onConsume=update pluralsightorder set status = '" + OrderStatus.PROCESSING.getCode() + "' where id = :#id&consumer.delay=5000")
               .to( "log:com.pluralsight.orderfulfillment.order?level=INFO");

            }
         };
      }
   }


   /**
    * Set up test fixture
    * 
    * @throws Exception
    */
   @Before
   public void setUp() throws Exception {
      // Insert catalog and customer data
      jdbcTemplate
            .execute("insert into catalogitem (id, itemnumber, itemname, itemtype) "
                  + "values (1, '078-1344200444', 'Build Your Own JavaScript Framework in Just 24 Hours', 'Book')");
      jdbcTemplate
            .execute("insert into customer (id, firstname, lastname, email) "
                  + "values (1, 'Larry', 'Horse', 'larry@hello.com')");
   }

   /**
    * Tear down all test data.
    * 
    * @throws Exception
    */
   @After
   public void tearDown() throws Exception {
      jdbcTemplate.execute("delete from orderItem");
      jdbcTemplate.execute("delete from pluralsightorder");
      jdbcTemplate.execute("delete from catalogitem");
      jdbcTemplate.execute("delete from customer");
   }

   /**
    * Test the successful routing of a new website order.
    * 
    * @throws Exception
    */
   @Test
   public void testNewWebsiteOrderRouteSuccess() throws Exception {
      jdbcTemplate
            .execute("insert into pluralsightorder (id, customer_id, orderNumber, timeorderplaced, lastupdate, status) "
                  + "values (1, 1, '1001', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'N')");

      jdbcTemplate
            .execute("insert into orderitem (id, order_id, catalogitem_id, status, price, quantity, lastupdate) "
                  + "values (1, 1, 1, 'N', 20.00, 1, CURRENT_TIMESTAMP)");

      Thread.sleep(10000);

      int total = jdbcTemplate.queryForObject("select count(id) from pluralsightorder where status = 'P'", Integer.class);

      assertEquals(1, total);

   }
}
