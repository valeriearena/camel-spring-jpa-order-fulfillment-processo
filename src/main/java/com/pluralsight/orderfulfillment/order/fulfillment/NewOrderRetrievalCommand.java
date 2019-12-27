package com.pluralsight.orderfulfillment.order.fulfillment;

import com.pluralsight.orderfulfillment.order.Order;
import com.pluralsight.orderfulfillment.order.OrderService;
import com.pluralsight.orderfulfillment.order.OrderStatus;
import java.util.List;
import javax.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

/**
 * Command to retrieve the next set of order for processing. This includes
 * selecting the order, updating their status and putting them into the
 * context.
 *
 * @author Michael Hoffman, Pluralsight
 */
@Component("newOrderRetrievalCommand")
public class NewOrderRetrievalCommand implements FulfillmentCommand {

  private static final Logger log = LoggerFactory
      .getLogger(NewOrderRetrievalCommand.class);

  @Inject
  private Environment environment;

  @Inject
  private OrderService orderService;

  /*
   * (non-Javadoc)
   *
   * @see
   * com.pluralsight.orderfulfillment.order.FulfillmentCommand#execute(com.
   * pluralsight.orderfulfillment.order.FulfillmentContext)
   */
  @Override
  public void execute(FulfillmentContext context) {
    // 1 - Load the context
    try {
      loadContext(context);
    } catch (Exception e) {
      log.error(
          "An error occurred while retrieving new order: "
              + e.getMessage(), e);
    }
  }

  /**
   * Populate the context for this run of order fulfillment
   *
   * @param context
   */
  private void loadContext(FulfillmentContext context) throws Exception {
    // 1 - Determine how many order we process at a time
    int fetchSize = Integer.parseInt(environment
        .getProperty("neworderretrievalcommand.fetchsize"));

    // 2 - Get the order details for new order. This will retrieve the next
    // five order entries in order of the time it was placed.
    List<Order> newOrders = orderService.getOrderDetails(OrderStatus.NEW,
        fetchSize);

    // 3 - Mark the order as in progress so they are not retrieved again by a
    // new processor.
    orderService.processOrderStatusUpdate(newOrders, OrderStatus.PROCESSING);

    // 4 - Now update the context
    context.setOrderDetails(newOrders);
    context.setFulfillmentCenter1OutboundFolder(environment
        .getProperty("order.fulfillment.center.1.outbound.folder"));
    context.setFulfillmentCenter1FileName(environment
        .getProperty("order.fulfillment.center.1.filename"));
  }

}
