-- -----------------------------------------------------
-- Schema resell_platform_v1
-- -----------------------------------------------------
CREATE SCHEMA IF NOT EXISTS `resell_platform_v1` DEFAULT CHARACTER SET utf8 ;
USE `resell_platform_v1` ;

-- -----------------------------------------------------
-- Table `resell_platform_v1`.`USER`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `resell_platform_v1`.`USER` (
                                             `id` INT NOT NULL AUTO_INCREMENT,
                                             `username` VARCHAR(45) NULL,
    `password` VARCHAR(70) NULL,
    `phoneNumber` VARCHAR(15) NULL,
    `name` VARCHAR(30) NULL,
    `nickname` VARCHAR(30) NULL,
    `email` VARCHAR(45) NULL,
    `shoeSize` VARCHAR(5) NULL,
    `role` ENUM('ROLE_USER', 'ROLE_ADMIN') NULL,
    PRIMARY KEY (`id`),
    UNIQUE INDEX `phonenumber_UNIQUE` (`phoneNumber` ASC) VISIBLE)
    ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `resell_platform_v1`.`ADDRESS`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `resell_platform_v1`.`ADDRESS` (
                                                `id` INT NOT NULL AUTO_INCREMENT,
                                                `userId` INT NOT NULL,
                                                `nickname` VARCHAR(45) NULL,
    `cityName` VARCHAR(30) NULL,
    `guName` VARCHAR(30) NULL,
    `streetName` VARCHAR(45) NULL,
    `addressDetail` VARCHAR(45) NULL,
    PRIMARY KEY (`id`, `userId`),
    CONSTRAINT `fk_ADDRESS_USER1`
    FOREIGN KEY (`userId`)
    REFERENCES `resell_platform_v1`.`USER` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
    ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `resell_platform_v1`.`BANK_ACCOUNT`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `resell_platform_v1`.`BANK_ACCOUNT` (
                                                     `id` INT NOT NULL AUTO_INCREMENT,
                                                     `userId` INT NOT NULL,
                                                     `bankName` ENUM('국민', '신한', '농협') NULL,
    `accountNumber` VARCHAR(45) NULL,
    PRIMARY KEY (`id`, `userId`),
    INDEX `fk_USER_ACCOUNT_USER1_idx` (`userId` ASC) VISIBLE,
    CONSTRAINT `fk_USER_ACCOUNT_USER1`
    FOREIGN KEY (`userId`)
    REFERENCES `resell_platform_v1`.`USER` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
    ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `resell_platform_v1`.`BRAND`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `resell_platform_v1`.`BRAND` (
                                              `id` INT NOT NULL,
                                              `name` VARCHAR(45) NULL,
    PRIMARY KEY (`id`))
    ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `resell_platform_v1`.`PRODUCT`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `resell_platform_v1`.`PRODUCT` (
                                                `id` INT NOT NULL AUTO_INCREMENT,
                                                `brandId` INT NOT NULL,
                                                `name` VARCHAR(45) NULL,
    `originalRetailPrice` INT NULL,
    `image` MEDIUMBLOB NULL,
    PRIMARY KEY (`id`, `brandId`),
    INDEX `fk_PRODUCT_BRAND1_idx` (`brandId` ASC) VISIBLE,
    CONSTRAINT `fk_PRODUCT_BRAND1`
    FOREIGN KEY (`brandId`)
    REFERENCES `resell_platform_v1`.`BRAND` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
    ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `resell_platform_v1`.`BUY_BID`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `resell_platform_v1`.`BUY_BID` (
                                                `id` INT NOT NULL AUTO_INCREMENT,
                                                `buyerId` INT NOT NULL,
                                                `productId` INT NOT NULL,
                                                `suggestPrice` INT NOT NULL,
                                                `deadline` DATE NOT NULL,
                                                `bidDate` DATE NULL,
                                                `size` VARCHAR(45) NULL,
    PRIMARY KEY (`id`, `buyerId`, `productId`),
    INDEX `fk_BID_PRODUCT1_idx` (`productId` ASC) VISIBLE,
    INDEX `fk_BID_USER1_idx` (`buyerId` ASC) VISIBLE,
    CONSTRAINT `fk_BID_PRODUCT1`
    FOREIGN KEY (`productId`)
    REFERENCES `resell_platform_v1`.`PRODUCT` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
    CONSTRAINT `fk_BID_USER1`
    FOREIGN KEY (`buyerId`)
    REFERENCES `resell_platform_v1`.`USER` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
    ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `resell_platform_v1`.`ORDER`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `resell_platform_v1`.`ORDER` (
                                              `id` INT NOT NULL AUTO_INCREMENT,
                                              `buyerId` INT NOT NULL,
                                              `sellerId` INT NOT NULL,
                                              `addressId` INT NOT NULL,
                                              `productId` INT NOT NULL,
                                              `price` INT NULL,
                                              `orderStatus` ENUM('입금_전', '입금_완료', '발송_전', '발송_완료', '배송_도착', '검수_중', '검수_완료', '거래_종료') NULL COMMENT 'ENUM(\'입금_전\', \'입금_완료\', \'발송_전\', \'발송_완료\', \'배송_도착\', \'검수_중\', \'검수_완료\', \'거래_종료\')',
  `size` VARCHAR(45) NOT NULL,
  `date` DATETIME NOT NULL,
  `deliveryCompany` ENUM('CJ대한통운', '롯데택배', '로젠택배') NULL,
  `trackingNumber` VARCHAR(45) NULL,
  PRIMARY KEY (`id`, `buyerId`, `sellerId`, `addressId`, `productId`),
  INDEX `fk_ORDER_PRODUCT1_idx` (`productId` ASC) VISIBLE,
  INDEX `fk_ORDER_ADDRESS1_idx` (`addressId` ASC) VISIBLE,
  INDEX `fk_ORDER_USER2_idx` (`sellerId` ASC) VISIBLE,
  INDEX `fk_ORDER_USER1_idx` (`buyerId` ASC) VISIBLE,
  CONSTRAINT `fk_ORDER_PRODUCT1`
    FOREIGN KEY (`productId`)
    REFERENCES `resell_platform_v1`.`PRODUCT` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_ORDER_ADDRESS1`
    FOREIGN KEY (`addressId`)
    REFERENCES `resell_platform_v1`.`ADDRESS` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_ORDER_USER2`
    FOREIGN KEY (`sellerId`)
    REFERENCES `resell_platform_v1`.`USER` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_ORDER_USER1`
    FOREIGN KEY (`buyerId`)
    REFERENCES `resell_platform_v1`.`USER` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `resell_platform_v1`.`INTEREST_PRODUCT`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `resell_platform_v1`.`INTEREST_PRODUCT` (
                                                         `id` INT NOT NULL AUTO_INCREMENT,
                                                         `userId` INT NOT NULL,
                                                         `productId` INT NOT NULL,
                                                         PRIMARY KEY (`id`, `productId`, `userId`),
    INDEX `fk_INTEREST_PRODUCT_USER1_idx` (`userId` ASC) VISIBLE,
    INDEX `fk_INTEREST_PRODUCT_PRODUCT1_idx` (`productId` ASC) VISIBLE,
    CONSTRAINT `fk_INTEREST_PRODUCT_USER1`
    FOREIGN KEY (`userId`)
    REFERENCES `resell_platform_v1`.`USER` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
    CONSTRAINT `fk_INTEREST_PRODUCT_PRODUCT1`
    FOREIGN KEY (`productId`)
    REFERENCES `resell_platform_v1`.`PRODUCT` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
    ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `resell_platform_v1`.`SELL_BID`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `resell_platform_v1`.`SELL_BID` (
                                                 `id` INT NOT NULL AUTO_INCREMENT,
                                                 `sellerId` INT NOT NULL,
                                                 `productId` INT NOT NULL,
                                                 `suggestPrice` INT NOT NULL,
                                                 `deadline` DATE NOT NULL,
                                                 `bidDate` DATE NULL,
                                                 `size` VARCHAR(45) NULL,
    PRIMARY KEY (`id`, `sellerId`, `productId`),
    INDEX `fk_BID_PRODUCT1_idx` (`productId` ASC) VISIBLE,
    INDEX `fk_BID_USER1_idx` (`sellerId` ASC) VISIBLE,
    CONSTRAINT `fk_BID_PRODUCT10`
    FOREIGN KEY (`productId`)
    REFERENCES `resell_platform_v1`.`PRODUCT` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
    CONSTRAINT `fk_BID_USER10`
    FOREIGN KEY (`sellerId`)
    REFERENCES `resell_platform_v1`.`USER` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
    ENGINE = InnoDB;