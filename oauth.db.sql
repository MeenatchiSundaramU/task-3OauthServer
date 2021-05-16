--
-- File generated with SQLiteStudio v3.3.3 on Mon May 17 04:08:58 2021
--
-- Text encoding used: System
--
PRAGMA foreign_keys = off;
BEGIN TRANSACTION;

-- Table: accesstoken
CREATE TABLE accesstoken(clientid text,uid integer,accesstoken text,timestamp text);
INSERT INTO accesstoken (clientid, uid, accesstoken, timestamp) VALUES ('jeabpwalpn', 15, 'awzrkcweqz', 'Mon May 17 02:23:37 IST 2021');

-- Table: developerdb
CREATE TABLE developerdb(clientid text primary key,clientsecret text,appname text,redirecturi text);
INSERT INTO developerdb (clientid, clientsecret, appname, redirecturi) VALUES ('ajoyhtdxjy', 'egkvdhmykr', 'mano', 'https://www.google.com');
INSERT INTO developerdb (clientid, clientsecret, appname, redirecturi) VALUES ('vpapdgomfu', 'erzvbfhrcb', 'mano', 'http://localhost:8080/webOauth/responseCheck');
INSERT INTO developerdb (clientid, clientsecret, appname, redirecturi) VALUES ('jeabpwalpn', 'fmedzqwmvd', 'Appa', 'http://localhost:8080/webOauth/response');

-- Table: grandcodelog
CREATE TABLE grandcodelog(clientid text,uid integer,grantcode text,timestamp text);
INSERT INTO grandcodelog (clientid, uid, grantcode, timestamp) VALUES ('jeabpwalpn', 16, 'vkdpisbvfq', 'Mon May 17 02:16:50 IST 2021');
INSERT INTO grandcodelog (clientid, uid, grantcode, timestamp) VALUES ('jeabpwalpn', 10, 'zjxqpouhvm', 'Mon May 17 03:39:11 IST 2021');

-- Table: refreshtoken
CREATE TABLE refreshtoken(clientid text,uid integer,refreshtoken text,tokenremain);
INSERT INTO refreshtoken (clientid, uid, refreshtoken, tokenremain) VALUES ('jeabpwalpn', 15, 'xmbtilulbu', 20);
INSERT INTO refreshtoken (clientid, uid, refreshtoken, tokenremain) VALUES ('jeabpwalpn', 16, 'batujybwhg', 20);
INSERT INTO refreshtoken (clientid, uid, refreshtoken, tokenremain) VALUES ('jeabpwalpn', 14, 'yrehvclskg', 20);

-- Table: scopetable
CREATE TABLE scopetable(uid integer primary key,profile text,location text);
INSERT INTO scopetable (uid, profile, location) VALUES (8, '1', '1');
INSERT INTO scopetable (uid, profile, location) VALUES (9, '1', '1');
INSERT INTO scopetable (uid, profile, location) VALUES (10, '1', '1');
INSERT INTO scopetable (uid, profile, location) VALUES (11, '1', '1');
INSERT INTO scopetable (uid, profile, location) VALUES (12, '1', '1');
INSERT INTO scopetable (uid, profile, location) VALUES (13, '1', '1');
INSERT INTO scopetable (uid, profile, location) VALUES (14, '1', '1');
INSERT INTO scopetable (uid, profile, location) VALUES (15, '1', '1');
INSERT INTO scopetable (uid, profile, location) VALUES (16, '1', '1');

-- Table: userinfo
CREATE TABLE userinfo(uid integer primary key autoincrement,name text,email text,phone text,password text,location text);
INSERT INTO userinfo (uid, name, email, phone, password, location) VALUES (1, 'Meenatchi Sundaram U', 'meenatchisundaram63@gmail.com', '9842779133', 'ManoNambi2', 'Tirunelveli');
INSERT INTO userinfo (uid, name, email, phone, password, location) VALUES (2, 'Reshma', 'reshma2001@gmail.com', '9360685327', 'ManoNambi2', 'Tirunelveli');
INSERT INTO userinfo (uid, name, email, phone, password, location) VALUES (3, 'dsfsdf', 'meenatchisundaram63@gmail.com', '4354657', 'fdddddddddddddd', 'dfsgdfgf');
INSERT INTO userinfo (uid, name, email, phone, password, location) VALUES (4, 'dfdsfsd', 'meenatchisundaram63@gmail.com', '778878787', 'sdfsdfsf', 'dassfds');
INSERT INTO userinfo (uid, name, email, phone, password, location) VALUES (5, 'sdfsdg', 'reshma2001@gmail.com', '325544657', 'ManoNambi2', 'Tirunelveli');
INSERT INTO userinfo (uid, name, email, phone, password, location) VALUES (6, 'Umapathi Sankar', 'sumapathi@gmail.com', '9360685327', 'ManoNambi2', 'Tirunelveli');
INSERT INTO userinfo (uid, name, email, phone, password, location) VALUES (7, 'Umapathi Sankar', 'sumapathi@gmail.com', '9360685327', 'Mano', 'Tirunelveli');
INSERT INTO userinfo (uid, name, email, phone, password, location) VALUES (8, 'Umapathi Sankar', 'sumapathi@gmail.com', '9360685327', '', 'Tirunelveli');
INSERT INTO userinfo (uid, name, email, phone, password, location) VALUES (9, 'Umapathi Sankar', 'sumapathi@gmail.com', '9842779133', 'ManoNambi2', 'Tirunelveli');
INSERT INTO userinfo (uid, name, email, phone, password, location) VALUES (10, 'Meenatchi Sundaram U', 'meenatchisundaram66@gmail.com', '9842779133', 'ManoNambi2', 'Tirunelveli');
INSERT INTO userinfo (uid, name, email, phone, password, location) VALUES (11, 'Raja', 'raj@gmail.com', '9842779133', 'ManoNambi2', 'Tirunelveli');
INSERT INTO userinfo (uid, name, email, phone, password, location) VALUES (12, 'Meenatchi Sundaram U', 'mano.us2001@gmail.com', '9842779133', 'ManoNambi2', 'Tirunelveli');
INSERT INTO userinfo (uid, name, email, phone, password, location) VALUES (13, 'ShunmugaSundari U', 'sdk@gmail.com', '123456', 'ManoNambi2', 'Tirunelveli');
INSERT INTO userinfo (uid, name, email, phone, password, location) VALUES (14, 'Helol', 'hello@gmail.com', '9842779133', 'ManoNambi2', 'Tirunelveli');
INSERT INTO userinfo (uid, name, email, phone, password, location) VALUES (15, 'hello', 'h@gmail.com', '9842779133', 'ManoNambi2', 'India');
INSERT INTO userinfo (uid, name, email, phone, password, location) VALUES (16, 'Meenatchi Sundaram U', 'meenatchisundaram65@gmail.com', '9842779133', 'ManoNambi2', 'Tirunelveli');

COMMIT TRANSACTION;
PRAGMA foreign_keys = on;
