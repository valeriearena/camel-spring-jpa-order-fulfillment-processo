package com.pluralsight.orderfulfillment.test;

import org.springframework.jdbc.core.JdbcTemplate;

/**
 * Bean for creating and destroying the orders database inside Apache Derby.
 *
 * @author Michael Hoffman, Pluralsight
 */
public class DerbyDatabaseBean {

  private JdbcTemplate jdbcTemplate;

  /**
   * Called by Spring bean initialization. Creates the schema and database
   * table structure for the orders database.
   *
   * @throws Exception
   */
  public void create() throws Exception {

    try {
      jdbcTemplate.execute("drop table orderItem");
      jdbcTemplate.execute("drop table pluralsightorder");
      jdbcTemplate.execute("drop table catalogitem");
      jdbcTemplate.execute("drop table customer");
      jdbcTemplate.execute("drop schema orders");
    } catch (Throwable e) {
    }

    jdbcTemplate.execute("CREATE SCHEMA orders");
    jdbcTemplate
        .execute("create table customer (id integer not null, firstName varchar(200) not null, lastName varchar(200) not null, email varchar(200) not null, primary key (id))");
    jdbcTemplate
        .execute("create table catalogitem (id integer not null, itemNumber varchar(200) not null, itemName varchar(200) not null, itemType varchar(200) not null, primary key (id))");
    jdbcTemplate
        .execute("create table pluralsightorder (id integer not null, customer_id integer not null, orderNumber varchar(200) not null, timeOrderPlaced timestamp not null, lastUpdate timestamp not null, status varchar(200) not null, primary key (id))");
    jdbcTemplate
        .execute("alter table pluralsightorder add constraint orders_fk_1 foreign key (customer_id) references customer (id)");
    jdbcTemplate
        .execute("create table orderItem (id integer not null, order_id integer not null, catalogitem_id integer not null, status varchar(200) not null, price decimal(20,5), lastUpdate timestamp not null, quantity integer not null, primary key (id))");
    jdbcTemplate
        .execute("alter table orderItem add constraint orderItem_fk_1 foreign key (order_id) references pluralsightorder (id)");
    jdbcTemplate
        .execute("alter table orderItem add constraint orderItem_fk_2 foreign key (catalogitem_id) references catalogitem (id)");
  }

  /**
   * Tears down the orders database in Apache Derby as part of the Spring
   * container life-cycle.
   *
   * @throws Exception
   */
  public void destroy() throws Exception {

    try {
      jdbcTemplate.execute("drop table orderItem");
      jdbcTemplate.execute("drop table pluralsightorder");
      jdbcTemplate.execute("drop table catalogitem");
      jdbcTemplate.execute("drop table customer");
      jdbcTemplate.execute("drop schema orders");
    } catch (Throwable e) {
      // ignore
    }
  }

  /**
   * @param jdbcTemplate the jdbcTemplate to set
   */
  public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
    this.jdbcTemplate = jdbcTemplate;
  }

}
