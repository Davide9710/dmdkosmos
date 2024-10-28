# DMD Kosmos

## About
DMD Kosmos is a library system that manages books across different universities, which we assume have different databases.
- Assume a scenario where one University, uses MySQL, and another university, which uses PostgreSQL, wants to have a joint digital library system.
- Both databases are used without modification.
- However, it behaves as a single system.

<img src="https://github.com/Davide9710/dmdkosmos/blob/main/database.png" width="500px">

## Install Prerequisites
DMD Kosmis is written in Java, and uses MySQL and PostgreSQL. So the following software is required to run it.
- [Oracle OpenJDK 20](https://jdk.java.net/)
- [MySQL 8.0](https://www.mysql.com/)
- [PostgreSQL 12.15](https://www.postgresql.org/)
- [IntelliJ IDEA](https://www.jetbrains.com/ja-jp/idea/) (recommended)

## System Configurations
The system configurations of DMD Kosmos are as follows.
- Web Framework
  - [Spring Boot 3.1.1] (https://spring.io/projects/spring-boot)
- Transaction Manager
  - [ScalarDB 3.13](https://scalar-labs.com/ja/products/scalardb)
- Databases
  - [MySQL 8.0](https://www.mysql.com/)
  - [PostgreSQL 12.15](https://www.postgresql.org/)

## How to Run
1. Open a terminal, go to your work directory, and clone this repository.
```shell
$ git clone https://github.com/Davide9710/dmdkosmos.git
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
            - ``BookController.java`` : Processing when ``index.html`` and ``search-results.html`` are loaded
            
        - ``models/``
            - 

        - ``scalardb/``
            - ``MyLibrary.java`` : ScalarDB business logic
            - ``MyLoadInitialData.java`` : Load initial data into ScalarDB, for testing purpose 

        - ``Main.java`` : Spring Boot main file

        - ``MainRunner.java`` : Configuration bean used for calling the initial data loader

        - ``WebSecurityConfig.java`` : Login configurations, users added for testing purpose

    - ``resources/``
        - ``static/css/`` : CSS
        - ``templates`` : HTML
        - ``scalardb.properties`` : ScalarDB settings
        - ``schema.json`` : ScalarDB schema

### ``scalardb.properties``
Before executing ``Main.java``, the user name and password for MySQL and PostgreSQL must be registered in ``scalardb.properties``.
1. MySQL
```properties
scalar.db.multi_storage.storages.mysql.username=example_username
scalar.db.multi_storage.storages.mysql.password=example_password
```
2. PostgreSQL
```properties
scalar.db.multi_storage.storages.postgresql.username=postgresql_username
scalar.db.multi_storage.storages.postgresql.password=postegresql_password
```

### ``WebSecurityConfig.java``
Login functionality is fixed, and the available users for testing purpose are loaded in this file

#### Example
- Username: ``davide``
- Password: ``davide``

#### Functionalities implemented
- login: the predifined users can login
- See reserved books : The user, on their index page, can see the books that he has reserved and he didn't return it yet. He can also see the due date of the return. Finally he can, directly from his main page, return the books he reserved.
- search books : The user can search some keywords on the search bar. The sesrch is a fulltext search on book name or book author. The found books will be displayed in the search-result page, with their availability.
- When ghe user found a book he wants to reserve, he can check the availability and, if available, book it through the nutton. After that, that book will be listed on his home page.

### Notes
- In our project researving and borrowing a book are the synonim. Also book.name and book.title refers to the same attribute
- We added some end-to-end tests on the package src/test. This tests call the endpoints eith a mocked user and check 
the authorization to perform some operations, and the logic of reserving and returning a book
- We took inspiration from the project JJE Bank for the organization of the project but the code is almost totally developed on our own, so we argue that our project is not a further development of the JJE Bank but another project. Thus we didn't for the repository.
