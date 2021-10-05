CREATE TABLE `t_dept`
(
    `id`       INT(11) NOT NULL AUTO_INCREMENT,
    `deptName` VARCHAR(30) DEFAULT NULL,
    `address`  VARCHAR(40) DEFAULT NULL,
    PRIMARY KEY (`id`)
) ENGINE = INNODB
  AUTO_INCREMENT = 1
  DEFAULT CHARSET = utf8;
CREATE TABLE `t_emp`
(
    `id`     INT(11) NOT NULL AUTO_INCREMENT,
    `name`   VARCHAR(20) DEFAULT NULL,
    `age`    INT(3)      DEFAULT NULL,
    `deptId` INT(11)     DEFAULT NULL,
    PRIMARY KEY (`id`),

    FOREIGN KEY (`deptId`) REFERENCES `t_dept` (`id`)
)
    ENGINE = INNODB
    AUTO_INCREMENT = 1
    DEFAULT CHARSET = utf8;

INSERT INTO t_dept(deptName, address)
VALUES ('华山', '华山');
INSERT INTO t_dept(deptName, address)
VALUES ('丐帮', '洛阳');
INSERT INTO t_dept(deptName, address)
VALUES ('峨眉', '峨眉山');
INSERT INTO t_dept(deptName, address)
VALUES ('武当', '武当山');
INSERT INTO t_dept(deptName, address)
VALUES ('明教', '光明顶');
INSERT INTO t_dept(deptName, address)
VALUES ('少林', '少林寺');
INSERT INTO t_emp(NAME, age, deptId)
VALUES ('风清扬', 90, 1);
INSERT INTO t_emp(NAME, age, deptId)
VALUES ('岳不群', 50, 1);
INSERT INTO t_emp(NAME, age, deptId)
VALUES ('令狐冲', 24, 1);
INSERT INTO t_emp(NAME, age, deptId)
VALUES ('洪七公', 70, 2);
INSERT INTO t_emp(NAME, age, deptId)
VALUES ('乔峰', 35, 2);
INSERT INTO t_emp(NAME, age, deptId)
VALUES ('灭绝师太', 70, 3);
INSERT INTO t_emp(NAME, age, deptId)
VALUES ('周芷若', 20, 3);
INSERT INTO t_emp(NAME, age, deptId)
VALUES ('张三丰', 100, 4);
INSERT INTO t_emp(NAME, age, deptId)
VALUES ('张无忌', 25, 5);
INSERT INTO t_emp(NAME, age, deptId)
VALUES ('韦小宝', 18, null);
