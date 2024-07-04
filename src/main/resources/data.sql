TRUNCATE TABLE accounts;
TRUNCATE TABLE book;
INSERT INTO accounts(accountId, accountName)
values ('m1', 'Andy'),
       ('m2', 'Becky'),
       ('m3', 'Clare'),
       ('p1', 'Daniel'),
       ('p2', 'Elen'),
       ('p3', 'Francis');

INSERT INTO book(bookId, bookTitle, author, ISBN, isAvailable)
values ('1', 'the bible', 'God', '1', 1),
       ('2', 'the anti-bible', 'Devil', '2', 1);