package com.pluralsight.orderfulfillment.catalog;

import com.pluralsight.orderfulfillment.order.OrderItemEntity;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

/**
 * Customer entity.
 *
 * @author Michael Hoffman
 */
@Entity
@Table(name = "catalogitem")
public class CatalogItemEntity implements Serializable {

  private static final long serialVersionUID = 4868640483823944904L;

  private long id;
  private String itemNumber;
  private String itemName;
  private String itemType;
  private Set<OrderItemEntity> orderItems = new HashSet<OrderItemEntity>(0);

  public CatalogItemEntity() {

  }

  public CatalogItemEntity(long id, String itemNumber, String itemName, String itemType) {
    this.id = id;
    this.itemNumber = itemNumber;
    this.itemName = itemName;
    this.itemType = itemType;
  }

  public CatalogItemEntity(long id, String itemNumber, String itemName, String itemType,
                           Set<OrderItemEntity> orderItems) {
    this.id = id;
    this.itemNumber = itemNumber;
    this.itemName = itemName;
    this.itemType = itemType;
    this.orderItems = orderItems;
  }

  /**
   * @return the id
   */
  @Id
  @Column(name = "id")
  public long getId() {
    return id;
  }

  /**
   * @param id the id to set
   */
  public void setId(long id) {
    this.id = id;
  }

  /**
   * @return the itemNumber
   */
  @Column(name = "itemNumber", nullable = false)
  public String getItemNumber() {
    return itemNumber;
  }

  /**
   * @param itemNumber the itemNumber to set
   */
  public void setItemNumber(String itemNumber) {
    this.itemNumber = itemNumber;
  }

  /**
   * @return the itemName
   */
  @Column(name = "itemName", nullable = false)
  public String getItemName() {
    return itemName;
  }

  /**
   * @param itemName the itemName to set
   */
  public void setItemName(String itemName) {
    this.itemName = itemName;
  }

  /**
   * @return the itemType
   */
  @Column(name = "itemType", nullable = false)
  public String getItemType() {
    return itemType;
  }

  /**
   * @param itemType the itemType to set
   */
  public void setItemType(String itemType) {
    this.itemType = itemType;
  }

  @OneToMany(fetch = FetchType.LAZY)
  public Set<OrderItemEntity> getOrderItems() {
    return this.orderItems;
  }

  public void setOrderItems(Set<OrderItemEntity> orderItems) {
    this.orderItems = orderItems;
  }

}
