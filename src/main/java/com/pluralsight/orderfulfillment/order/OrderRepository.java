package com.pluralsight.orderfulfillment.order;

import java.util.Date;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

/**
 * Repository for OrderEntity data - a collection of Orders.
 * <p>
 * Repositories are like DAOs.
 * DAOs abstract the implementation details of how to retrieve data.
 * Repositories abstract the implementation of how to retrieve a collection.
 * <p>
 * OrderRepository abstracts the implementation of how to retrieve a collection of Orders.
 * Repositories are collection DAOs like REST endpoints are collection resources.
 * <p>
 * CRUD logic is considered boiler-plate so the Spring Data framework defines the CrudRepository.
 * When it's extended, Spring dynamically provides the implementation.
 *
 * @author Michael Hoffman, Pluralsight
 */
public interface OrderRepository extends PagingAndSortingRepository<OrderEntity, Integer> {

  /**
   * Select all order for the page ordered by the timeOrderPlaced value in
   * ascending order.
   *
   * @param status
   * @param pageable
   * @return
   */
  @Query(value = "select o from OrderEntity o where o.status = ?1 order by o.timeOrderPlaced")
  Page<OrderEntity> findByStatus(String status, Pageable pageable);

  @Query("select o from OrderEntity o where o.id = ?1")
  OrderEntity findById(long orderId);

  /**
   * Update the status of all order IDs passed.
   *
   * @param code
   * @param orderIds
   * @return
   */
  @Modifying
  @Query("update OrderEntity o set o.status = ?1, o.lastUpdate = ?2 where o.id in (?3)")
  int updateStatus(String code, Date lastUpdate, List<Long> orderIds);

}
