create sequence company_id_seq;

create table companies (
	company_id varchar (50) default nextval('company_id_seq'),
	company_name varchar (50),
	street__number integer,
	street_name varchar (20),
	apt_number varchar (10),
	city varchar(20),
	state varchar(10),
	zip integer,
	phone varchar(20),
	primary key (company_id)
);

create sequence brand_id_seq;
create table brands (
	brand_id varchar (50) default nextval('brand_id_seq'),
	brand_name varchar (50),
	primary key (brand_id)
);

create sequence model_id_seq;
create table models (
	model_id varchar (50) default nextval('model_id_seq'),
	model_name varchar (50),
	body_styple varchar (10),
	primary key(model_id)
);

create sequence option_id_seq;
create table options (
	option_id varchar (50) default nextval('option_id_seq'),
	color varchar (20),
	engine varchar (20),
	transmission varchar (20),
	primary key (option_id)
);

create sequence customer_id_seq;
create table customers (
	customer_id varchar (50) default nextval('customer_id_seq'),
	customer_name varchar (50),
	street__number integer,
	street_name varchar (20),
	apt_number varchar (10),
	city varchar(20),
	state varchar(10),
	zip integer,
	phone varchar(20),
	annual_income numeric (8,2),
	primary key (customer_id)
);

create sequence dealer_id_seq;
create table dealers (
	dealer_id character varying(50) default nextval('dealer_id_seq') primary key,
	dealer_name character varying(50),
	street_number integer,
	street_name character varying(20),
	apt_number character varying(10),
	city character varying(20),
	state character varying(10),
	zip integer,
	phone character varying(20),
	email text,
	password text,
	parking_spaces integer default 100
);


create sequence inventory_id_seq;
create table inventories (
	inventory_id varchar (50) default nextval('inventory_id_seq'),
	amount integer check (amount >= 0),
	primary key (inventory_id)
);

create table vehicles (
	vin varchar (50) primary key,
	brand_id varchar (50) references brands on delete cascade,
	model_id varchar (50) references models on delete cascade,
	option_id varchar (50) references options on delete cascade,
	customer_id varchar (50) references customers on delete set null,
	dealer_id varchar (50) references dealers on delete set null,
	inventory_id varchar (50) references inventories on delete set null
);

create table has_brands (
	company_id varchar (50),
	brand_id varchar (50),
	primary key (company_id, brand_id),
	foreign key (company_id) references companies on delete cascade,
	foreign key (brand_id) references brands on delete cascade
);

create table has_models (
	brand_id varchar (50),
	model_id varchar (50),
	primary key (brand_id, model_id),
	foreign key (brand_id) references brands on delete cascade,
	foreign key (model_id) references models on delete cascade
);

create table has_options (
	model_id varchar (50),
	option_id varchar (50),
	primary key (model_id, option_id),
	foreign key (model_id) references models on delete cascade,
	foreign key (option_id) references options on delete cascade
);


create table has_vehicles (
	customer_id varchar (50),
	vin varchar (50),
	primary key (customer_id, vin),
	foreign key (customer_id) references customers on delete cascade,
	foreign key (vin) references vehicles on delete set null
);

create table has_dealers (
	vin varchar (50),
	dealer_id varchar (50),
	primary key (vin),
	foreign key (vin) references vehicles on delete cascade,
	foreign key (dealer_id) references dealers on delete set null
);

create table has_inventories (
	brand_id character varying(50) not null references brands(brand_id),
	model_id character varying(50) references models(model_id),
	option_id character varying(50) references options(option_id),
	inventory_id character varying(50) references inventories(inventory_id) primary key,
	dealer_id character varying(50) references dealers(dealer_id)
);
