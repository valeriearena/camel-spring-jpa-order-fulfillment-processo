package com.pluralsight.orderfulfillment.customer;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

import com.pluralsight.orderfulfillment.order.OrderEntity;
import com.pluralsight.orderfulfillment.test.BaseJpaRepositoryTest;
import java.util.List;
import java.util.Set;
import javax.inject.Inject;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class CustomerRepositoryTest extends BaseJpaRepositoryTest {

   @Inject
   private CustomerRepository customerRepository;

   @Before
   public void setUp() throws Exception {
   }

   @After
   public void tearDown() throws Exception {
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
