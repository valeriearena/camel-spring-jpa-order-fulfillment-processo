--drop table orderItem
--drop table pluralsightorder
--drop table catalogitem
--drop table customer

create table customer (
	id bigint not null identity primary key,
	firstName nvarchar(200) not null,
	lastName nvarchar(200) not null,
	email nvarchar(200) not null
)

/*
 * Table: CatalogItem
 * Description: Contains the catalog item
 */
create table catalogitem (
	id bigint not null identity primary key,
	itemNumber nvarchar(200) not null,
	itemName nvarchar(200) not null,
	itemType nvarchar(200) not null
)

create table pluralsightorder(
	id bigint not null identity primary key,
	customer_id bigint not null,
	orderNumber nvarchar(200) not null,
	timeOrderPlaced datetime not null,
	lastUpdate datetime not null,
	status nvarchar(200) not null
)

alter table pluralsightorder add constraint orders_fk_1 foreign key (customer_id) references customer (id);

create table orderItem (
	id bigint not null identity primary key,
	order_id bigint not null,
	catalogitem_id bigint not null,
	status nvarchar(200) not null,
	price decimal(20,5),
	lastUpdate datetime not null,
	quantity integer not null
)

alter table orderItem add constraint orderItem_fk_1 foreign key (order_id) references pluralsightorder (id);
alter table orderItem add constraint orderItem_fk_2 foreign key (catalogitem_id) references catalogitem (id);