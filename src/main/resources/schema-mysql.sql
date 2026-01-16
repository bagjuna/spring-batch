CREATE TABLE `customer2` (
                            `id` mediumint(8) unsigned NOT NULL auto_increment,
                            `first_name` varchar(255) default NULL,  -- 여기 수정
                            `last_name` varchar(255) default NULL,   -- 여기 수정
                            `birthdate` varchar(255),
                            PRIMARY KEY (`id`)
) AUTO_INCREMENT=1;
