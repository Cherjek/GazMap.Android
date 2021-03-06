USE gasstation;

DROP TRIGGER IF EXISTS update_point;
DROP TRIGGER IF EXISTS insert_point;

DROP TRIGGER IF EXISTS update_point_types;

DROP TRIGGER IF EXISTS change_insert;

delimiter //
CREATE TRIGGER update_point BEFORE UPDATE ON point
FOR EACH ROW
BEGIN
	SET NEW.modified = CURRENT_TIMESTAMP;
END;

CREATE TRIGGER insert_point BEFORE INSERT ON point
FOR EACH ROW
BEGIN
	SET NEW.created = CURRENT_TIMESTAMP;
END;

CREATE TRIGGER update_point_types BEFORE UPDATE ON point_types
FOR EACH ROW
BEGIN
	SET NEW.modified = CURRENT_TIMESTAMP;
END;

CREATE TRIGGER change_insert BEFORE INSERT ON changes
FOR EACH ROW
BEGIN
	SET NEW.created = CURRENT_TIMESTAMP;
END;//
delimiter ;