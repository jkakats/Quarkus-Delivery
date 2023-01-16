-- noinspection SqlResolveForFile

--- CLIENTS ---

SET @johndoe2 = '4948b178-f325-4f5f-b8ea-0b4d64cd006c';
SET @eudim = RANDOM_UUID();
SET @elpap = RANDOM_UUID();
SET @iooi = RANDOM_UUID();
SET @nikg = RANDOM_UUID();
SET @kpappas = RANDOM_UUID();
SET @rinoula = RANDOM_UUID();

INSERT INTO "client" ("uuid", "username", "name", "email", "phone_number", "address_street",
                      "address_apartment", "address_city", "address_state", "address_zip_code",
                      "password")
VALUES (@johndoe2, 'johndoe2', 'John Doe', 'john@doe.com', '6987654321', 'Πατησίων', null,
        'Αθήνα', 'Αττική', 10434,
        '$argon2i$v=19$m=65536,t=4,p=2$U2NzQ0s3RlJ0cnhPU0d2aw$k3PoIYNTxEK3KekidDDq7g'),
       (@eudim, 'eudim', 'Ευαγγελία Δημητρίου', 'eu.dimitriou@gmail.com', '6986105289',
        '3ης Σεπτεμβρίου', '3.3', 'Αθήνα', 'Αττική', 10434,
        '$argon2i$v=19$m=65536,t=4,p=2$SUttVDNoVHlFVTRpclFkUg$4PScR5cTNb3q8+ljUTJG5A'),
       (@elpap, 'elpap', 'Ελένη Παπαδημητρίου', 'elpapad@gmail.com', '6976998713', 'Μαυρομματαίων',
        '5.1', 'Αθήνα', 'Αττική', 10434,
        '$argon2i$v=19$m=65536,t=4,p=2$SUttVDNoVHlFVTRpclFkUg$r190K5gIL9zyt22WnpEfnw'),
       (@iooi, 'io_oi', 'Ιωάννης Οικονόμου', 'io.oi@gmail.com', '6979169026', 'Λευκάδος', null,
        'Αθήνα', 'Αττική', 11362,
        '$argon2i$v=19$m=65536,t=4,p=2$SUttVDNoVHlFVTRpclFkUg$7HSxFtg0B1CKS5hQD74lRg'),
       (@nikg, 'nik-g', 'Νίκος Γεωργίου', 'nik-g@gmail.com', '6981549241', 'Σπετσών', '5.2',
        'Αθήνα', 'Αττική', 11362,
        '$argon2i$v=19$m=65536,t=4,p=2$SUttVDNoVHlFVTRpclFkUg$VMOdeVXQ9/7Xejy+kbjDZQ'),
       (@kpappas, 'k-pappas', 'Κώστας Παππάς', 'k.pappas@gmail.com', '6973642385', 'Κιμώλου', '2.1',
        'Αθήνα', 'Αττική', 11362,
        '$argon2i$v=19$m=65536,t=4,p=2$SUttVDNoVHlFVTRpclFkUg$7NYN5zNGHImZtQ28c2yd8A'),
       (@rinoula, 'rinoula', 'Ειρήνη Αλεξίου', 'rinoula@gmail.com', '6987057313', 'Αγίου Μελετίου',
        '4.2', 'Αθήνα', 'Αττική', 11361,
        '$argon2i$v=19$m=65536,t=4,p=2$SUttVDNoVHlFVTRpclFkUg$v5KzBSkKzMc2Pih+cMAa/A');

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
       (2, 'Αθήνα', 'Αττική', 11362),
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

--- ORDERS ---

SET @o1 = RANDOM_UUID();
SET @o2 = RANDOM_UUID();
SET @o3 = RANDOM_UUID();
SET @o4 = RANDOM_UUID();
SET @o5 = RANDOM_UUID();

INSERT INTO "order" ("uuid", "client_uuid", "store_id", "confirmed", "delivered", "ordered_time",
                     "delivered_time", "estimated_wait")
VALUES (@o1, @eudim, 4, true, true, '2022-12-09 20:39:28', '2022-12-09 21:00:36', 20),
       (@o2, @eudim, 4, true, true, '2022-12-08 12:24:42', '2022-12-08 12:55:03', 30),
       (@o3, @elpap, 4, true, true, '2022-12-10 12:31:12', '2022-12-10 12:45:37', 15),
       (@o4, @iooi, 1, true, true, '2022-12-15 13:04:38', '2022-12-15 13:50:10', 40),
       (@o5, @nikg, 1, false, false, '2022-12-17 16:28:10', null, null);

INSERT INTO "order_product" ("id", "order_id", "product_id", "quantity")
VALUES (1, @o1, 1, 2),
       (2, @o1, 4, 2),
       (3, @o2, 1, 3),
       (4, @o3, 6, 1),
       (5, @o4, 7, 2),
       (6, @o5, 10, 1),
       (7, @o5, 13, 1);

--- REVIEWS ---

INSERT INTO "order_review" ("id", "order_uuid", "rating", "comment")
VALUES (1, @o1, 4, null),
       (2, @o2, 5, 'Τέλεια'),
       (3, @o4, 2, 'Άργησε');

INSERT INTO "product_review" ("id", "parent", "product_id", "rating")
VALUES (1, 1, 1, 5),
       (2, 1, 2, 4);

--- Fix ID sequences ---

ALTER TABLE "store"
  ALTER COLUMN "id" RESTART WITH 8;
ALTER TABLE "product"
  ALTER COLUMN "id" RESTART WITH 16;
ALTER TABLE "order_product"
  ALTER COLUMN "id" RESTART WITH 8;
ALTER TABLE "order_review"
  ALTER COLUMN "id" RESTART WITH 4;
ALTER TABLE "product_review"
  ALTER COLUMN "id" RESTART WITH 3;
