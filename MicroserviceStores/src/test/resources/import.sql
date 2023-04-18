-- noinspection SqlResolveForFile

--- STORES ---

INSERT INTO "store" ("id", "name", "type")
VALUES (1, 'Pizza Fan', 'Pizza'),
       (2, 'Domino''s Pizza', 'Pizza'),
       (3, 'L'' artigiano Pizza', 'Pizza'),
       (4, 'The Big Bad Wolf', 'Σουβλάκια'),
       (5, 'Mr Euro', 'Σουβλάκια'),
       (6, 'Ψητοσυνάντηση', 'Σουβλάκια'),
       (7, 'Goody''s Burger House', 'Burgers');

INSERT INTO "store_area" ("store_id", "city", "state", "zip_code")
VALUES (1, 'Αθήνα', 'Αττική', 11362),
       (1, 'Αθήνα', 'Αττική', 11361),
       (2, 'Αθήνα', 'Αττική', 10434),
       (2, 'Αθήνα', 'Αττική', 11361),
       (2, 'Αθήνα', 'Ατsτική', 11362),
       (2, 'Αθήνα', 'Αττική', 11257),
       (3, 'Αθήνα', 'Αττική', 11361),
       (4, 'Αθήνα', 'Αττική', 10434),
       (5, 'Αθήνα', 'Αττική', 11361),
       (5, 'Αθήνα', 'Αττική', 11362),
       (6, 'Αθήνα', 'Αττική', 10434),
       (6, 'Αθήνα', 'Αττική', 11361),
       (6, 'Αθήνα', 'Αττική', 11362),
       (6, 'Αθήνα', 'Αττική', 11472),
       (7, 'Αθήνα', 'Αττική', 10434);

--- PRODUCTS ---

INSERT INTO "product" ("id", "store_id", "name", "price", "comment")
VALUES (1, 4, 'Πίτα Γύρο Χοιρινό', 3.50, 'Απ'' όλα'),
       (2, 6, 'Πίτα Γύρο Χοιρινό', 3.40, 'Τζατζίκι, Κρεμμύδι, Ντομάτα, Πατάτες'),
       (3, 5, 'Πίτα Γύρο Χοιρινό', 3.00, null),
       (4, 4, 'Πίτα Γύρο Κοτόπουλο', 3.50, 'Chicken sauce, Ντομάτα, Μαρούλι'),
       (5, 6, 'Πίτα Γύρο Κοτόπουλο', 3.40, 'Σως, Ντομάτα, Μαρούλι'),
       (6, 4, 'Cheese Burger', 5.00, null),
       (7, 1, 'Cheese Burger', 5.50, null),
       (8, 7, 'Cheese Burger', 4.50, null),
       (9, 7, 'Special Burger', 6.00, null),
       (10, 1, 'Pizza Margarita', 7.00, null),
       (11, 2, 'Pizza Margarita', 6.50, null),
       (12, 3, 'Pizza Margarita', 7.50, null),
       (13, 1, 'Mexican Pizza', 7.50, null),
       (14, 2, 'Mexican Pizza', 8.00, null),
       (15, 3, 'Mexican Pizza', 8.50, null);


--- Fix ID sequences ---

ALTER TABLE "store"
  ALTER COLUMN "id" RESTART WITH 8;
ALTER TABLE "product"
  ALTER COLUMN "id" RESTART WITH 16;
