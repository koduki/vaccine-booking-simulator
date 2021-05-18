CREATE TABLE `book` (
	`id` INT NOT NULL AUTO_INCREMENT,
	`date` DATE,
	`user` VARCHAR(128),
	`created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
	KEY `idx_date` (`date`) USING HASH,
	PRIMARY KEY (`id`)
);