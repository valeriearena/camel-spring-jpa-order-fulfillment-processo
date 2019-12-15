--drop table orderItem
--drop table [dbo].[order]
--drop table catalogitem
--drop table customer

create table customer (
	id bigint not null identity primary key,
	firstName text not null,
	lastName text not null,
	email text not null
)

/*
 * Table: CatalogItem
 * Description: Contains the catalog item
 */
create table catalogitem (
	id bigint not null identity primary key,
	itemNumber text not null,
	itemName text not null,
	itemType text not null
)

create table [dbo].[order](
	id bigint not null identity primary key,
	customer_id bigint not null,
	orderNumber text not null,
	timeOrderPlaced datetime not null,
	lastUpdate datetime not null,
	status text not null
)

alter table [dbo].[order] add constraint orders_fk_1 foreign key (customer_id) references customer (id);

create table orderItem (
	id bigint not null identity primary key,
	order_id bigint not null,
	catalogitem_id bigint not null,
	status text not null,
	price decimal(20,5),
	lastUpdate datetime not null,
	quantity integer not null
)

alter table orderItem add constraint orderItem_fk_1 foreign key (order_id) references  [dbo].[order] (id);
alter table orderItem add constraint orderItem_fk_2 foreign key (catalogitem_id) references catalogitem (id);


