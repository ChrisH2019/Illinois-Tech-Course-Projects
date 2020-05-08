


create sequence company_id_seq
start with 1
increment by 1
nocache
nocycle;
/

create table companies (
	company_id varchar (50),
	company_name varchar (50),
	street_number integer,
	street_name varchar (20),
	apt_number varchar (10) default null,
	city varchar(20),
	state varchar(10),
	zip integer,
	phone varchar(20),
	primary key (company_id)
);

create or replace trigger companies_trigger
before insert on companies
for each row
begin
  select company_id_seq.nextval
  into :new.company_id
  from dual;
end;
/







create sequence brand_id_seq
start with 1
increment by 1
nocache
nocycle;
/

create table brands (
	brand_id varchar (50),
	brand_name varchar (50),
	primary key (brand_id)
);

create or replace trigger brands_trigger
before insert on brands
for each row
begin
  select brand_id_seq.nextval
  into :new.brand_id
  from dual;
end;
/






create sequence model_id_seq
start with 1
increment by 1
nocache
nocycle;
/

create table models (
	model_id varchar (50),
	model_name varchar (50),
	body_style varchar (10),
	primary key(model_id)
);

create or replace trigger models_trigger
before insert on models
for each row
begin
  select model_id_seq.nextval
  into :new.model_id
  from dual;
end;
/






create sequence option_id_seq
start with 1
increment by 1
nocache
nocycle;
/

create table options (
	option_id varchar (50),
	color varchar (20),
	engine varchar (20),
	transmission varchar (20),
	primary key (option_id)
);

create or replace trigger options_trigger
before insert on options
for each row
begin
  select option_id_seq.nextval
  into :new.option_id
  from dual;
end;
/






create sequence manufacturer_id_seq
start with 1
increment by 1
nocache
nocycle;
/

create table manufacturers (
  manufacturer_id varchar (50),
  manufacturer_name varchar (100),
  primary key (manufacturer_id)
);

create or replace trigger manufacturers_trigger
before insert on manufacturers
for each row
begin
  select manufacturer_id_seq.nextval
  into :new.manufacturer_id
  from dual;
end;
/






create sequence part_id_seq
start with 1
increment by 1
nocache
nocycle;
/

create table parts (
  part_id varchar (50),
  part_name varchar (50),
  primary key (part_id)
);

create or replace trigger parts_trigger
before insert on parts
for each row
begin
  select part_id_seq.nextval
  into :new.part_id
  from dual;
end;
/






create sequence supplier_id_seq
start with 1
increment by 1
nocache
nocycle;
/

create table suppliers (
  supplier_id varchar (50),
  supplier_name varchar (50),
  primary key (supplier_id)
);

create or replace trigger suppliers_trigger
before insert on suppliers
for each row
begin
  select supplier_id_seq.nextval
  into :new.supplier_id
  from dual;
end;
/







create sequence inventory_id_seq
start with 1
increment by 1
nocache
nocycle;
/

create table inventories (
	inventory_id varchar (50),
	amount integer check (amount >= 0),
	primary key (inventory_id)
);

create or replace trigger inventories_trigger
before insert on inventories
for each row
begin
  select inventory_id_seq.nextval
  into :new.inventory_id
  from dual;
end;
/






create sequence customer_id_seq
start with 1
increment by 1
nocache
nocycle;
/

create table customers (
	customer_id varchar (50),
	customer_name varchar (50),
	street__number integer,
	street_name varchar (20),
	apt_number varchar (10) default null,
	city varchar(20),
	state varchar(10),
	zip integer,
	phone varchar(20),
	annual_income numeric (8,2),
  email varchar (100),
  password varchar (100),
	primary key (customer_id)
);

create or replace trigger ocustomers_trigger
before insert on customers
for each row
begin
  select customer_id_seq.nextval
  into :new.customer_id
  from dual;
end;
/






create sequence dealer_id_seq
start with 1
increment by 1
nocache
nocycle;
/

create table dealers (
	dealer_id character varying(50),
	dealer_name character varying(50),
	street_number integer,
	street_name character varying(20),
	apt_number character varying(10) default null,
	city character varying(20),
	state character varying(10),
	zip integer,
	phone character varying(20),
	email varchar (100),
	password varchar (100),
	parking_spaces integer default 100,
  primary key (dealer_id)
);

create or replace trigger dealers_trigger
before insert on dealers
for each row
begin
  select dealer_id_seq.nextval
  into :new.dealer_id
  from dual;
end;
/





create table vehicles (
	vin varchar (50),
	brand_id varchar (50) references brands on delete cascade,
	model_id varchar (50) references models on delete cascade,
	option_id varchar (50) references options on delete cascade,
  manufacturer_id varchar (50) references manufacturers on delete cascade,
	customer_id varchar (50) references customers on delete set null,
	dealer_id varchar (50) references dealers on delete set null,
  primary key (vin)
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



create table has_manufacturers (
	option_id varchar (50),
	manufacturer_id varchar (50),
	primary key (option_id, manufacturer_id),
	foreign key (manufacturer_id) references manufacturers on delete cascade,
	foreign key (option_id) references options on delete cascade
);




create table has_suppliers (
  part_id varchar (50) references parts on delete cascade,
  supplier_id varchar (50) references suppliers on delete set NULL,
  manufacturer_id varchar (50) references manufacturers on delete set NULL,
  primary key (part_id, supplier_id, manufacturer_id)
);



create table has_vehicles (
	customer_id varchar (50),
	vin varchar (50),
	primary key (customer_id, vin),
	foreign key (customer_id) references customers on delete cascade,
	foreign key (vin) references vehicles on delete cascade
);



create table has_dealers (
	vin varchar (50),
	dealer_id varchar (50),
	primary key (vin),
	foreign key (vin) references vehicles on delete cascade,
	foreign key (dealer_id) references dealers on delete cascade
);




create table has_inventories (
  inventory_id character varying(50) references inventories(inventory_id) primary key,
	brand_id character varying(50) references brands(brand_id),
	model_id character varying(50) references models(model_id),
	option_id character varying(50) references options(option_id),
	dealer_id character varying(50) references dealers(dealer_id)
);
