DROP TABLE IF EXISTS account;
DROP TABLE IF EXISTS book;
CREATE TABLE account (
    accountId varchar(10),
    accountName varchar(10),
    PRIMARY KEY (accountId)
);

CREATE TABLE book (
  bookId varchar(255),
  bookTitle varchar(255),
  isAvailable integer,
  author varchar(255),
  ISBN varchar(255),
  PRIMARY KEY (bookId)
);