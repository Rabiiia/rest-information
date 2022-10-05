CREATE TABLE `address` (
  `address_id` int NOT NULL AUTO_INCREMENT,
  `street` varchar(45) NOT NULL,
  `zipcode` int NOT NULL,
  PRIMARY KEY (`address_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `person` (
  `person_id` int NOT NULL AUTO_INCREMENT,
  `first_name` varchar(45) NOT NULL,
  `last_name` varchar(45) NOT NULL,
  `email` varchar(45) NOT NULL,
  `address_id` int NOT NULL,
  PRIMARY KEY (`person_id`),
  KEY `fk_person_address_idx` (`address_id`),
  CONSTRAINT `fk_person_address` FOREIGN KEY (`address_id`) REFERENCES `address` (`address_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `hobby` (
  `hobby_id` int NOT NULL AUTO_INCREMENT,
  `name` varchar(255) NOT NULL,
  `wikiLink` varchar(255) NOT NULL,
  `category` varchar(255) NOT NULL,
  `type` varchar(255) NOT NULL,
  PRIMARY KEY (`hobby_id`)
) ENGINE=InnoDB AUTO_INCREMENT=452 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `hobby_person` (
  `hobby_id` int NOT NULL,
  `person_id` int NOT NULL,
  KEY `fk_HOBBY_PERSON_HOBBY1_idx` (`hobby_id`),
  KEY `fk_HOBBY_PERSON_person1_idx` (`person_id`),
  CONSTRAINT `fk_HOBBY_PERSON_HOBBY1` FOREIGN KEY (`hobby_id`) REFERENCES `hobby` (`hobby_id`),
  CONSTRAINT `fk_HOBBY_PERSON_person1` FOREIGN KEY (`person_id`) REFERENCES `person` (`person_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;


CREATE TABLE `phone` (
  `number` int NOT NULL,
  `person_id` int NOT NULL,
  `description` varchar(45) DEFAULT NULL,
  PRIMARY KEY (`number`),
  KEY `fk_phone_person1_idx` (`person_id`),
  CONSTRAINT `fk_phone_person1` FOREIGN KEY (`person_id`) REFERENCES `person` (`person_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
