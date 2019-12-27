package com.pluralsight.orderfulfillment.order;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import com.pluralsight.orderfulfillment.test.BaseJpaRepositoryTest;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.inject.Inject;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.jdbc.core.JdbcTemplate;

public class OrderItemRepositoryTest extends BaseJpaRepositoryTest {

   @Inject
   private JdbcTemplate jdbcTemplate;

   @Inject
   private OrderItemRepository orderItemRepository;

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
   public void test_findAllOrderItemsSuccess() throws Exception {
      List<OrderItemEntity> orderItems = orderItemRepository.findAll();
      assertNotNull(orderItems);
      assertFalse(orderItems.isEmpty());
   }

   @Test
   public void test_findOrderItemOrderCatalogItemSuccess() throws Exception {
      List<OrderItemEntity> orderItems = orderItemRepository.findAll();
      assertNotNull(orderItems);
      assertFalse(orderItems.isEmpty());
      OrderItemEntity orderItem = orderItems.get(0);
      assertNotNull(orderItem.getOrder());
      assertNotNull(orderItem.getCatalogItem());
   }

   @Test
   public void test_findByOrderIdSuccess() throws Exception {
      List<OrderItemEntity> orderItems = orderItemRepository.findByOrderId(1L);
      assertNotNull(orderItems);
      assertFalse(orderItems.isEmpty());
   }

   @Test
   public void test_updateStatusSuccess() throws Exception {
      List<Long> orderIds = new ArrayList<Long>();
      orderIds.add(1L);
      int updateCount = orderItemRepository.updateStatus(OrderStatus.PROCESSING.getCode(), new Date(System.currentTimeMillis()), orderIds);
      assertTrue(updateCount == 1);
   }
}
