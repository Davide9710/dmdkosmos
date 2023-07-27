# JJE Bank

## About
JJE Bank is a banking system between different databases.
- Assume a scenario where Bank M, which uses MySQL, and Bank P, which uses PostgreSQL, merge.
- Both databases are used without modification.
- However, it behaves as a single system.

## Install Prerequisites
JJE Bank is written in Java, and uses MySQL and PostgreSQL. So the following software is required to run it.
- [Oracle OpenJDK 20](https://jdk.java.net/)
- [MySQL 8.0](https://www.mysql.com/)
- [PostgreSQL 12.15](https://www.postgresql.org/)
- [IntelliJ IDEA](https://www.jetbrains.com/ja-jp/idea/) (recommended)

## How to run
1. Open a terminal, go to your work directory, and clone this repository.
```
$ git clone git@github.com:iamtatsuyamori/jjebank.git
```

2. Open a project in JetBrains' [IntelliJ IDEA](https://www.jetbrains.com/ja-jp/idea/).

3. Reload a project on ``pom.xml``.

4. Run ``Main.java``.

5. Access on http://localhost:8080/.

## File Structure
- ``src/main/``
    - ``java/com/example/test/``
        - ``controllers/``
            - ``IndexController.java`` : Processing when index.html is loaded
            - ``RemoveController.java`` : Processing when remove_form.html and remove_result.html are loaded
            - ``TransactionController.java`` : Processing when transaction_form.html and transaction_result.html are loaded
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

### MyBank.java
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