package com.pluralsight.orderfulfillment.customer;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

import com.pluralsight.orderfulfillment.order.OrderEntity;
import com.pluralsight.orderfulfillment.test.BaseJpaRepositoryTest;
import com.pluralsight.orderfulfillment.test.TestIntegration;
import java.util.List;
import java.util.Set;
import javax.inject.Inject;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {TestIntegration.class})
public class CustomerRepositoryTest extends BaseJpaRepositoryTest {

  @Autowired
  private JdbcTemplate jdbcTemplate;

  @Autowired
  private CustomerRepository customerRepository;

  @Before
  public void setUp() throws Exception {

    // Insert catalog and customer data
    jdbcTemplate
        .execute("insert into catalogitem (id, itemnumber, itemname, itemtype) "
            + "values (1, '078-1344200444', 'Build Your Own JavaScript Framework in Just 24 Hours', 'Book')");
    jdbcTemplate
        .execute("insert into customer (id, firstname, lastname, email) "
            + "values (1, 'Larry', 'Horse', 'larry@hello.com')");

    jdbcTemplate
        .execute("insert into customer (id, firstname, lastname, email) "
            + "values (2, 'Michael', 'Hoffman', 'mike@michaelhoffmaninc.com')");


    jdbcTemplate
        .execute("insert into pluralsightorder (id, customer_id, orderNumber, timeorderplaced, lastupdate, status) "
            + "values (1, 1, '1001', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'N')");

    jdbcTemplate
        .execute("insert into pluralsightorder (id, customer_id, orderNumber, timeorderplaced, lastupdate, status) "
            + "values (2, 2, '1002', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'N')");

    jdbcTemplate
        .execute("insert into orderitem (id, order_id, catalogitem_id, status, price, quantity, lastupdate) "
            + "values (1, 1, 1, 'N', 20.00, 1, CURRENT_TIMESTAMP)");


  }

  @After
  public void tearDown() throws Exception {

    jdbcTemplate.execute("delete from orderItem");
    jdbcTemplate.execute("delete from pluralsightorder");
    jdbcTemplate.execute("delete from catalogitem");
    jdbcTemplate.execute("delete from customer");
  }

  @Test
  public void test_findAllCustomersSuccess() throws Exception {
    List<CustomerEntity> customers = customerRepository.findAll();
    assertNotNull(customers);
    assertFalse(customers.isEmpty());
  }

  @Test
  public void test_findCustomerOrdersSuccess() throws Exception {
    List<CustomerEntity> customers = customerRepository.findAll();
    assertNotNull(customers);
    assertFalse(customers.isEmpty());
    CustomerEntity customer = customers.get(0);
    Set<OrderEntity> orders = customer.getOrders();
    assertNotNull(orders);
    assertFalse(orders.isEmpty());
  }

}
