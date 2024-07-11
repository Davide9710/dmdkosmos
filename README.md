# JJE Bank

## About
DMD Kosmos is a banking system between different databases.
- Assume a scenario where Bank M, which uses MySQL, and Bank P, which uses PostgreSQL, merge.
- Both databases are used without modification.
- However, it behaves as a single system.

[//]: # (![UseCase]&#40;https://github.com/iamtatsuyamori/jjebank/assets/26143847/7cfd52d5-41cd-46d0-b25b-c0e3e6d0c8cf&#41;)
<img src="https://github.com/iamtatsuyamori/jjebank/assets/26143847/7cfd52d5-41cd-46d0-b25b-c0e3e6d0c8cf" width="500px">

## Install Prerequisites
JJE Bank is written in Java, and uses MySQL and PostgreSQL. So the following software is required to run it.
- [Oracle OpenJDK 20](https://jdk.java.net/)
- [MySQL 8.0](https://www.mysql.com/)
- [PostgreSQL 12.15](https://www.postgresql.org/)
- [IntelliJ IDEA](https://www.jetbrains.com/ja-jp/idea/) (recommended)

## System Configurations
The system configurations of JJE Bank are as follows.
- Web Framework
  - [Spring Boot 3.1.1](https://spring.io/projects/spring-boot)
- Transaction Manager
  - [ScalarDB 3.13](https://scalar-labs.com/ja/products/scalardb)
- Databases
  - [MySQL 8.0](https://www.mysql.com/)
  - [PostgreSQL 12.15](https://www.postgresql.org/)

## How to Run
1. Open a terminal, go to your work directory, and clone this repository.
```shell
$ git clone git@github.com:iamtatsuyamori/jjebank.git
```

2. Open a project in JetBrains' [IntelliJ IDEA](https://www.jetbrains.com/ja-jp/idea/).

3. Reload a project on ``pom.xml``.
4. Open the command prompt in the folder src/main/resources
5. Run the command: ``$ java -jar scalardb-schema-loader-3.13.0.jar --config scalardb.properties --schema-file schema.json --coordinator``, this will create the schemas

6. Run ``Main.java``.

7. Access on http://localhost:8080/.

## File Structure
- ``src/main/``
    - ``java/com/example/test/``

        - ``controllers/``
            - ``IndexController.java`` : Processing when ``index.html`` is loaded
            - ``RemoveController.java`` : Processing when ``remove_form.html`` and ``remove_result.html`` are loaded
            - ``TransactionController.java`` : Processing when ``transaction_form.html`` and ``transaction_result.html`` are loaded

        - ``models/``

        - ``scalardb/``
            - ``MyBank.java`` : ScalarDB processing
            - ``MyLoadInitialData.java`` : Load initial data into ScalarDB
            - ``MySchemaLoader.java`` : Load the schema into ScalarDB

        - ``Main.java`` : Main file

        - ``MainRunner.java`` : Run before Spring starts

        - ``WebSecurityConfig.java`` : Login information

    - ``resources/``
        - ``static/css/`` : CSS
        - ``templates`` : HTML
        - ``application.properties`` : Spring setting
        - ``scalardb.properties`` : ScalarDB setting
        - ``schema.json`` : ScalarDB schema

### ``scalardb.properties``
Before executing ``Main.java``, the user name and password for MySQL and PostgreSQL must be registered in ``scalardb.properties``.
1. MySQL
```properties
scalar.db.multi_storage.storages.mysql.username=root
scalar.db.multi_storage.storages.mysql.password=root
```
2. PostgreSQL
```properties
scalar.db.multi_storage.storages.postgresql.username=postgres
scalar.db.multi_storage.storages.postgresql.password=root
```

### ``WebSecurityConfig.java``
Since the implementation of the login function has not been completed, the login information is written in ``WebSecurityConfig.java``.

#### Example
- Username: ``davide``
- Password: ``davide``

### ``MyBank.java``
#### Note
- accountId is unique (in a table).
    - There can be user1 in mysql and postgres respectively.
- transactionId is unique.
    - fromId, toId, and date are not the same at the same time.
    - especially date is assumed not identical.
    - the transactionId is represented by a concatenation of fromId, toId, and date.
- the name of argument "table".
    - In the real world, "table" name is regarded as "bank" name.
- cannot deal with SimpleDateFormat well.
    - date should be appropriate format.
    - If date is not expressed in the proper format, unexpected behavior may occur.
- any errors which developer were unaware may exist.
    - throw errors for inappropriate operations as much as possible.
- why date is required?
    - to make it easy to check the debug.

#### Functions
- deposit() : Create an account with accountId and initialize its balance with amount, or if accountId account already exists, add amount to the balance.
- withdraw() : Withdraw amount from accountId account.
- transfer() : Transfer amount from fromId account to toId account.
- cancel() : If the conditions are met, cancel the transactionId transaction.
- getBalance() : Get information about balance of accountId account.