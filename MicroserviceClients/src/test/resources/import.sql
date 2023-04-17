-- noinspection SqlResolveForFile

--- CLIENTS ---

delete from "client";

SET @johndoe2 = '4948b178-f325-4f5f-b8ea-0b4d60cd006c';
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
