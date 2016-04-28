/*
 * Copyright 2000-2014 Vaadin Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.vaadin.data.util.sqlcontainer;

import com.vaadin.data.util.sqlcontainer.query.generator.DefaultSQLGenerator;
import com.vaadin.data.util.sqlcontainer.query.generator.MSSQLGenerator;
import com.vaadin.data.util.sqlcontainer.query.generator.OracleGenerator;
import com.vaadin.data.util.sqlcontainer.query.generator.SQLGenerator;

public class SQLTestsConstants {

    /* Set the DB used for testing here! */
    public enum DB {
        HSQLDB, MYSQL, POSTGRESQL, MSSQL, ORACLE;
    }

    /* 0 = HSQLDB, 1 = MYSQL, 2 = POSTGRESQL, 3 = MSSQL, 4 = ORACLE */
    public static final DB db = DB.HSQLDB;

    /* Auto-increment column offset (HSQLDB = 0, MYSQL = 1, POSTGRES = 1) */
    public static int offset;
    /* Garbage table creation query (=three queries for oracle) */
    public static String createGarbage;
    public static String createGarbageSecond;
    public static String createGarbageThird;
    /* DB Drivers, urls, usernames and passwords */
    public static String dbDriver;
    public static String dbURL;
    public static String dbUser;
    public static String dbPwd;
    /* People -test table creation statement(s) */
    public static String peopleFirst;
    public static String peopleSecond;
    public static String peopleThird;
    /* Schema test creation statement(s) */
    public static String createSchema;
    public static String createProductTable;
    public static String dropSchema;
    /* Versioned -test table createion statement(s) */
    public static String[] versionStatements;
    /* SQL Generator used during the testing */
    public static SQLGenerator sqlGen;

    /* Set DB-specific settings based on selected DB */
    static {
        sqlGen = new DefaultSQLGenerator();
        switch (db) {
        case HSQLDB:
            offset = 0;
            createGarbage = "create table garbage (id integer generated always as identity, type varchar(32), PRIMARY KEY(id))";
            dbDriver = "org.hsqldb.jdbc.JDBCDriver";
            dbURL = "jdbc:hsqldb:mem:sqlcontainer";
            dbUser = "SA";
            dbPwd = "";
            peopleFirst = "create table people (id integer generated always as identity, name varchar(32), AGE INTEGER)";
            peopleSecond = "alter table people add primary key (id)";
            versionStatements = new String[] {
                    "create table versioned (id integer generated always as identity, text varchar(255), version tinyint default 0)",
                    "alter table versioned add primary key (id)" };
            // TODO these should ideally exist for all databases
            createSchema = "create schema oaas authorization DBA";
            createProductTable = "create table oaas.product (\"ID\" integer generated always as identity primary key, \"NAME\" VARCHAR(32))";
            dropSchema = "drop schema if exists oaas cascade";
            break;
        case MYSQL:
            offset = 1;
            createGarbage = "create table GARBAGE (ID integer auto_increment, type varchar(32), PRIMARY KEY(ID))";
            dbDriver = "com.mysql.jdbc.Driver";
            dbURL = "jdbc:mysql:///sqlcontainer";
            dbUser = "sqlcontainer";
            dbPwd = "sqlcontainer";
            peopleFirst = "create table PEOPLE (ID integer auto_increment not null, NAME varchar(32), AGE INTEGER, primary key(ID))";
            peopleSecond = null;
            versionStatements = new String[] {
                    "create table VERSIONED (ID integer auto_increment not null, TEXT varchar(255), VERSION tinyint default 0, primary key(ID))",
                    "CREATE TRIGGER upd_version BEFORE UPDATE ON VERSIONED"
                            + " FOR EACH ROW SET NEW.VERSION = OLD.VERSION+1" };
            break;
        case POSTGRESQL:
            offset = 1;
            createGarbage = "create table GARBAGE (\"ID\" serial PRIMARY KEY, \"TYPE\" varchar(32))";
            dbDriver = "org.postgresql.Driver";
            dbURL = "jdbc:postgresql://localhost:5432/test";
            dbUser = "postgres";
            dbPwd = "postgres";
            peopleFirst = "create table PEOPLE (\"ID\" serial primary key, \"NAME\" VARCHAR(32), \"AGE\" INTEGER)";
            peopleSecond = null;
            versionStatements = new String[] {
                    "create table VERSIONED (\"ID\" serial primary key, \"TEXT\" VARCHAR(255), \"VERSION\" INTEGER DEFAULT 0)",
                    "CREATE OR REPLACE FUNCTION zz_row_version() RETURNS TRIGGER AS $$"
                            + "BEGIN"
                            + "   IF TG_OP = 'UPDATE'"
                            + "       AND NEW.\"VERSION\" = old.\"VERSION\""
                            + "       AND ROW(NEW.*) IS DISTINCT FROM ROW (old.*)"
                            + "   THEN"
                            + "       NEW.\"VERSION\" := NEW.\"VERSION\" + 1;"
                            + "   END IF;" + "   RETURN NEW;" + "END;"
                            + "$$ LANGUAGE plpgsql;",
                    "CREATE TRIGGER \"mytable_modify_dt_tr\" BEFORE UPDATE"
                            + "   ON VERSIONED FOR EACH ROW"
                            + "   EXECUTE PROCEDURE \"public\".\"zz_row_version\"();" };
            createSchema = "create schema oaas";
            createProductTable = "create table oaas.product (\"ID\" serial primary key, \"NAME\" VARCHAR(32))";
            dropSchema = "drop schema oaas cascade";
            break;
        case MSSQL:
            offset = 1;
            createGarbage = "create table GARBAGE (\"ID\" int identity(1,1) primary key, \"TYPE\" varchar(32))";
            dbDriver = "com.microsoft.sqlserver.jdbc.SQLServerDriver";
            dbURL = "jdbc:sqlserver://localhost:1433;databaseName=tempdb;";
            dbUser = "sa";
            dbPwd = "sa";
            peopleFirst = "create table PEOPLE (\"ID\" int identity(1,1) primary key, \"NAME\" VARCHAR(32), \"AGE\" INTEGER)";
            peopleSecond = null;
            versionStatements = new String[] { "create table VERSIONED (\"ID\" int identity(1,1) primary key, \"TEXT\" VARCHAR(255), \"VERSION\" rowversion not null)" };
            sqlGen = new MSSQLGenerator();
            break;
        case ORACLE:
            offset = 1;
            createGarbage = "create table GARBAGE (\"ID\" integer primary key, \"TYPE\" varchar2(32))";
            createGarbageSecond = "create sequence garbage_seq start with 1 increment by 1 nomaxvalue";
            createGarbageThird = "create trigger garbage_trigger before insert on GARBAGE for each row begin select garbage_seq.nextval into :new.ID from dual; end;";
            dbDriver = "oracle.jdbc.OracleDriver";
            dbURL = "jdbc:oracle:thin:test/test@localhost:1521:XE";
            dbUser = "test";
            dbPwd = "test";
            peopleFirst = "create table PEOPLE (\"ID\" integer primary key, \"NAME\" VARCHAR2(32), \"AGE\" INTEGER)";
            peopleSecond = "create sequence people_seq start with 1 increment by 1 nomaxvalue";
            peopleThird = "create trigger people_trigger before insert on PEOPLE for each row begin select people_seq.nextval into :new.ID from dual; end;";
            versionStatements = new String[] {
                    "create table VERSIONED (\"ID\" integer primary key, \"TEXT\" VARCHAR(255), \"VERSION\" INTEGER DEFAULT 0)",
                    "create sequence versioned_seq start with 1 increment by 1 nomaxvalue",
                    "create trigger versioned_trigger before insert on VERSIONED for each row begin select versioned_seq.nextval into :new.ID from dual; end;",
                    "create sequence versioned_version start with 1 increment by 1 nomaxvalue",
                    "create trigger versioned_version_trigger before insert or update on VERSIONED for each row begin select versioned_version.nextval into :new.VERSION from dual; end;" };
            sqlGen = new OracleGenerator();
            break;
        }
    }

}
