﻿CREATE TABLE products(
  id int GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
  code VARCHAR(30) NOT NULL UNIQUE,
  title VARCHAR(30) NOT NULL,
  last_purchase_price DECIMAL(19,4),
  last_sale_price DECIMAL(19,4)
);

INSERT INTO products(code, title, last_purchase_price, last_sale_price) 
values ('A12F-C001', 'Зеленый свитер', 1700, 1900);
INSERT INTO products(code, title, last_purchase_price, last_sale_price) 
values ('A12F-C002', 'Желтая дутая куртка', 1200.99, 2100.50);
INSERT INTO products(code, title, last_purchase_price, last_sale_price) 
values ('A12F-C003', 'Шляпа на все времена', 900.00, 1450.50);
INSERT INTO products(code, title, last_purchase_price, last_sale_price) 
values ('A12F-C004', 'Штаны с карманами', 1900.00, 2300.50);
INSERT INTO products(code, title, last_purchase_price, last_sale_price) 
values ('A12F-C005', 'Бомбический шлем для гольфа', 2900.00, 4200.00);
INSERT INTO products(code, title, last_purchase_price, last_sale_price) 
values ('A12F-C006', 'Ветровка с полосками', 750.00, 1100.00);
INSERT INTO products(code, title, last_purchase_price, last_sale_price) 
values ('A12F-C007', 'Модные спортивные штаны', 950.00, 1350.00);
INSERT INTO products(code, title, last_purchase_price, last_sale_price) 
values ('A12F-C008', 'Ковбойские сапоги', 1800.00, 2350.00);
INSERT INTO products(code, title, last_purchase_price, last_sale_price) 
values ('A12F-C009', 'Шорты прям для пляжа', 650.00, 990.00);
INSERT INTO products(code, title, last_purchase_price, last_sale_price) 
values ('A12F-C010', 'Невероятное платье', 4500.00, 7200.00);
INSERT INTO products(code, title, last_purchase_price, last_sale_price) 
values ('A12F-C011', 'Водолазка водолаза', 900.00, 1300.00);
INSERT INTO products(code, title, last_purchase_price, last_sale_price) 
values ('A12F-C012', 'Кофта с блестяшками', 990.00, 1450.00);
INSERT INTO products(code, title, last_purchase_price, last_sale_price) 
values ('A12F-C013', 'Обычная футболка', 350.00, 600.00);
INSERT INTO products(code, title, last_purchase_price, last_sale_price) 
values ('A12F-C014', 'Необычная футболка', 500.00, 700.00);
INSERT INTO products(code, title, last_purchase_price, last_sale_price) 
values ('A12F-C015', 'Джинсы в полоску', 800.00, 950.00);
INSERT INTO products(code, title, last_purchase_price, last_sale_price) 
values ('A12F-C016', 'Квадратная шапка', 990.00, 1300.00);
INSERT INTO products(code, title, last_purchase_price, last_sale_price) 
values ('A12F-C017', 'Легендарная футболка', 1300.00, 2000.00);
INSERT INTO products(code, title, last_purchase_price, last_sale_price) 
values ('A12F-C018', 'Штаны со знаком тигра', 2200.00, 2650.00);
INSERT INTO products(code, title, last_purchase_price, last_sale_price) 
values ('A12F-C019', 'Провоцирующая кепка', 540.00, 770.00);
INSERT INTO products(code, title, last_purchase_price, last_sale_price) 
values ('A12F-C020', 'Шапочка из фольги', 100.00, 230.00);

CREATE TABLE stores(
  id int GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
  title VARCHAR(30) NOT NULL
);

INSERT INTO stores(title) values('ТЦ Вербовский');
INSERT INTO stores(title) values('ТЦ Марс');
INSERT INTO stores(title) values('ТЦ Макс сити');
INSERT INTO stores(title) values('Проспект Ракетостроителей');
INSERT INTO stores(title) values('ТЦ Сфера');
INSERT INTO stores(title) values('ТЦ Социум-сокол');
INSERT INTO stores(title) values('ТЦ Савеловский');

CREATE TABLE stores_products(
  store_id int REFERENCES stores(id) ON DELETE CASCADE,
  product_id int NOT NULL REFERENCES products(id) ON DELETE CASCADE,
  amount int CHECK (amount > 0),
  PRIMARY KEY(store_id, product_id)
);

INSERT INTO stores_products VALUES(1, 1, 4);
INSERT INTO stores_products VALUES(1, 3, 16);
INSERT INTO stores_products VALUES(1, 9, 7);
INSERT INTO stores_products VALUES(1, 12, 22);
INSERT INTO stores_products VALUES(1, 19, 5);
INSERT INTO stores_products VALUES(2, 2, 36);
INSERT INTO stores_products VALUES(2, 6, 21);
INSERT INTO stores_products VALUES(2, 7, 19);
INSERT INTO stores_products VALUES(2, 11, 8);
INSERT INTO stores_products VALUES(2, 12, 3);
INSERT INTO stores_products VALUES(2, 16, 16);
INSERT INTO stores_products VALUES(2, 19, 6);
INSERT INTO stores_products VALUES(3, 1, 13);
INSERT INTO stores_products VALUES(3, 2, 7);
INSERT INTO stores_products VALUES(3, 5, 33);
INSERT INTO stores_products VALUES(3, 9, 11);
INSERT INTO stores_products VALUES(3, 12, 2);
INSERT INTO stores_products VALUES(3, 17, 18);
INSERT INTO stores_products VALUES(3, 18, 9);
INSERT INTO stores_products VALUES(3, 19, 12);
INSERT INTO stores_products VALUES(4, 2, 3);
INSERT INTO stores_products VALUES(4, 4, 12);
INSERT INTO stores_products VALUES(4, 6, 38);
INSERT INTO stores_products VALUES(4, 8, 57);
INSERT INTO stores_products VALUES(4, 10, 17);
INSERT INTO stores_products VALUES(4, 11, 8);
INSERT INTO stores_products VALUES(4, 13, 11);
INSERT INTO stores_products VALUES(4, 14, 25);
INSERT INTO stores_products VALUES(4, 16, 29);
INSERT INTO stores_products VALUES(4, 19, 10);
INSERT INTO stores_products VALUES(5, 2, 15);
INSERT INTO stores_products VALUES(5, 4, 10);
INSERT INTO stores_products VALUES(5, 7, 5);
INSERT INTO stores_products VALUES(5, 13, 8);
INSERT INTO stores_products VALUES(5, 19, 5);
INSERT INTO stores_products VALUES(6, 1, 3);
INSERT INTO stores_products VALUES(6, 3, 25);
INSERT INTO stores_products VALUES(6, 4, 14);
INSERT INTO stores_products VALUES(6, 8, 19);
INSERT INTO stores_products VALUES(6, 11, 9);
INSERT INTO stores_products VALUES(6, 16, 21);
INSERT INTO stores_products VALUES(6, 17, 13);
INSERT INTO stores_products VALUES(6, 19, 41);
INSERT INTO stores_products VALUES(7, 2, 4);
INSERT INTO stores_products VALUES(7, 3, 13);
INSERT INTO stores_products VALUES(7, 4, 2);
INSERT INTO stores_products VALUES(7, 6, 12);
INSERT INTO stores_products VALUES(7, 9, 16);
INSERT INTO stores_products VALUES(7, 13, 18);
INSERT INTO stores_products VALUES(7, 17, 34);
INSERT INTO stores_products VALUES(7, 19, 16);

CREATE TABLE transaction_types(
  id int GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
  title VARCHAR(30) NOT NULL UNIQUE
);

INSERT INTO transaction_types(title) VALUES ('продажа');
INSERT INTO transaction_types(title) VALUES ('поступление');
INSERT INTO transaction_types(title) VALUES ('перемещение');

CREATE TABLE invoices(
  id int GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
  transaction_type int NOT NULL REFERENCES transaction_types(id) ON DELETE CASCADE
);

INSERT INTO invoices(transaction_type) VALUES(2);
INSERT INTO invoices(transaction_type) VALUES(2);
INSERT INTO invoices(transaction_type) VALUES(3);
INSERT INTO invoices(transaction_type) VALUES(2);
INSERT INTO invoices(transaction_type) VALUES(1);
INSERT INTO invoices(transaction_type) VALUES(1);
INSERT INTO invoices(transaction_type) VALUES(2);
INSERT INTO invoices(transaction_type) VALUES(2);
INSERT INTO invoices(transaction_type) VALUES(1);
INSERT INTO invoices(transaction_type) VALUES(2);
INSERT INTO invoices(transaction_type) VALUES(3);
INSERT INTO invoices(transaction_type) VALUES(1);

CREATE TABLE invoices_products(
  invoice_id int NOT NULL REFERENCES invoices(id) ON DELETE CASCADE,
  product_id int NOT NULL REFERENCES products(id) ON DELETE CASCADE,
  price DECIMAL(19,4), 
  amount int CHECK (amount >= 0),
  PRIMARY KEY (invoice_id, product_id)
);

INSERT INTO invoices_products(invoice_id, product_id, price, amount) VALUES(1, 1, 1000, 4);
INSERT INTO invoices_products(invoice_id, product_id, price, amount) VALUES(1, 3, 900, 8);
INSERT INTO invoices_products(invoice_id, product_id, price, amount) VALUES(1, 9, 650, 7);
INSERT INTO invoices_products(invoice_id, product_id, price, amount) VALUES(2, 2, 1200.99, 25);
INSERT INTO invoices_products(invoice_id, product_id, price, amount) VALUES(2, 6, 750, 13);
INSERT INTO invoices_products(invoice_id, product_id, price, amount) VALUES(2, 7, 950, 8);
INSERT INTO invoices_products(invoice_id, product_id, price, amount) VALUES(2, 16, 990, 13);
INSERT INTO invoices_products(invoice_id, product_id, price, amount) VALUES(3, 2, 0, 16);
INSERT INTO invoices_products(invoice_id, product_id, price, amount) VALUES(3, 12, 0, 3);
INSERT INTO invoices_products(invoice_id, product_id, price, amount) VALUES(4, 7, 950, 8);
INSERT INTO invoices_products(invoice_id, product_id, price, amount) VALUES(4, 13, 350, 14);
INSERT INTO invoices_products(invoice_id, product_id, price, amount) VALUES(4, 16, 990, 5);
INSERT INTO invoices_products(invoice_id, product_id, price, amount) VALUES(5, 4, 1450.5, 1);
INSERT INTO invoices_products(invoice_id, product_id, price, amount) VALUES(5, 6, 1100, 1);
INSERT INTO invoices_products(invoice_id, product_id, price, amount) VALUES(6, 15, 950, 3);
INSERT INTO invoices_products(invoice_id, product_id, price, amount) VALUES(7, 4, 1900, 8);
INSERT INTO invoices_products(invoice_id, product_id, price, amount) VALUES(7, 17, 1300, 15);
INSERT INTO invoices_products(invoice_id, product_id, price, amount) VALUES(8, 5, 2900, 6);
INSERT INTO invoices_products(invoice_id, product_id, price, amount) VALUES(8, 7, 950, 10);
INSERT INTO invoices_products(invoice_id, product_id, price, amount) VALUES(8, 15, 800, 8);
INSERT INTO invoices_products(invoice_id, product_id, price, amount) VALUES(9, 10, 7200, 1);
INSERT INTO invoices_products(invoice_id, product_id, price, amount) VALUES(10, 15, 800, 10);
INSERT INTO invoices_products(invoice_id, product_id, price, amount) VALUES(10, 19, 540, 18);
INSERT INTO invoices_products(invoice_id, product_id, price, amount) VALUES(11, 11, 0, 6);
INSERT INTO invoices_products(invoice_id, product_id, price, amount) VALUES(11, 16, 0, 3);
INSERT INTO invoices_products(invoice_id, product_id, price, amount) VALUES(12, 5, 4200, 2);
INSERT INTO invoices_products(invoice_id, product_id, price, amount) VALUES(12, 9, 990, 1);

CREATE TABLE incomes(
  id int GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
  store_id int NOT NULL REFERENCES stores(id) ON DELETE CASCADE,
  invoice_id int NOT NULL REFERENCES invoices(id) ON DELETE CASCADE
);

INSERT INTO incomes (store_id, invoice_id) VALUES(1, 1);
INSERT INTO incomes (store_id, invoice_id) VALUES(3, 2); 
INSERT INTO incomes (store_id, invoice_id) VALUES(6, 4);
INSERT INTO incomes (store_id, invoice_id) VALUES(4, 7);
INSERT INTO incomes (store_id, invoice_id) VALUES(2, 8);
INSERT INTO incomes (store_id, invoice_id) VALUES(3, 10);

CREATE TABLE sales(
  id int GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
  store_id int NOT NULL REFERENCES stores(id) ON DELETE CASCADE,
  invoice_id int NOT NULL REFERENCES invoices(id) ON DELETE CASCADE
);

INSERT INTO sales (store_id, invoice_id) VALUES(5, 5);
INSERT INTO sales (store_id, invoice_id) VALUES(2, 6);
INSERT INTO sales (store_id, invoice_id) VALUES(4, 9);
INSERT INTO sales (store_id, invoice_id) VALUES(7, 12);

CREATE TABLE movements(
  id int GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
  from_store_id int NOT NULL REFERENCES stores(id) ON DELETE CASCADE,
  to_store_id int NOT NULL REFERENCES stores(id) ON DELETE CASCADE,
  invoice_id int NOT NULL REFERENCES invoices(id) ON DELETE CASCADE
);

INSERT INTO movements (from_store_id, to_store_id, invoice_id) VALUES(1, 4, 3);
INSERT INTO movements (from_store_id, to_store_id, invoice_id) VALUES(6, 3, 11);