/*
 Navicat Premium Data Transfer

 Source Server         : loc5.6
 Source Server Type    : MySQL
 Source Server Version : 50728
 Source Host           : localhost:3306
 Source Schema         : test2

 Target Server Type    : MySQL
 Target Server Version : 50728
 File Encoding         : 65001

 Date: 25/09/2020 14:22:46
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for a
-- ----------------------------
DROP TABLE IF EXISTS `a`;
CREATE TABLE `a` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) NOT NULL,
  `value` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=27 DEFAULT CHARSET=latin1;

-- ----------------------------
-- Records of a
-- ----------------------------
BEGIN;
INSERT INTO `a` VALUES (1, 'a', 'v1');
INSERT INTO `a` VALUES (2, 'a', 'v1');
INSERT INTO `a` VALUES (3, 'a', 'v1');
INSERT INTO `a` VALUES (4, 'd', 'v4');
INSERT INTO `a` VALUES (5, 'e', 'v5');
INSERT INTO `a` VALUES (6, 'f', 'v6');
INSERT INTO `a` VALUES (7, 'g', 'v7');
INSERT INTO `a` VALUES (8, 'h', 'v8');
INSERT INTO `a` VALUES (9, 'i', 'v9');
INSERT INTO `a` VALUES (10, 'j', 'v10');
INSERT INTO `a` VALUES (11, 'k', 'v11');
INSERT INTO `a` VALUES (12, 'l', 'v12');
INSERT INTO `a` VALUES (13, 'm', 'v13');
INSERT INTO `a` VALUES (14, 'n', 'v14');
INSERT INTO `a` VALUES (15, 'o', 'v15');
INSERT INTO `a` VALUES (16, 'p', 'v16');
INSERT INTO `a` VALUES (17, 'q', 'v17');
INSERT INTO `a` VALUES (18, 'r', 'v18');
INSERT INTO `a` VALUES (19, 's', 'v19');
INSERT INTO `a` VALUES (20, 't', 'v20');
INSERT INTO `a` VALUES (21, 'u', 'v21');
INSERT INTO `a` VALUES (22, 'v', 'v22');
INSERT INTO `a` VALUES (23, 'w', 'v23');
INSERT INTO `a` VALUES (24, 'x', 'v24');
INSERT INTO `a` VALUES (25, 'y', 'v25');
INSERT INTO `a` VALUES (26, 'z', 'v26');
COMMIT;

-- ----------------------------
-- Table structure for b
-- ----------------------------
DROP TABLE IF EXISTS `b`;
CREATE TABLE `b` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) NOT NULL,
  `value` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=27 DEFAULT CHARSET=latin1;

-- ----------------------------
-- Records of b
-- ----------------------------
BEGIN;
INSERT INTO `b` VALUES (1, 'a', 'v1');
INSERT INTO `b` VALUES (2, 'a', 'v1');
INSERT INTO `b` VALUES (3, 'a', 'v1');
INSERT INTO `b` VALUES (4, 'd', 'v4');
INSERT INTO `b` VALUES (5, 'e', 'v5');
INSERT INTO `b` VALUES (6, 'f', 'v6');
INSERT INTO `b` VALUES (7, 'g', 'v7');
INSERT INTO `b` VALUES (8, 'h', 'v8');
INSERT INTO `b` VALUES (9, 'i', 'v9');
INSERT INTO `b` VALUES (10, 'j', 'v10');
INSERT INTO `b` VALUES (11, 'k', 'v11');
INSERT INTO `b` VALUES (12, 'l', 'v12');
INSERT INTO `b` VALUES (13, 'm', 'v13');
INSERT INTO `b` VALUES (14, 'n', 'v14');
INSERT INTO `b` VALUES (15, 'o', 'v15');
INSERT INTO `b` VALUES (16, 'p', 'v16');
INSERT INTO `b` VALUES (17, 'q', 'v17');
INSERT INTO `b` VALUES (18, 'r', 'v18');
INSERT INTO `b` VALUES (19, 's', 'v19');
INSERT INTO `b` VALUES (20, 't', 'v20');
INSERT INTO `b` VALUES (21, 'u', 'v21');
INSERT INTO `b` VALUES (22, 'v', 'v22');
INSERT INTO `b` VALUES (23, 'w', 'v23');
INSERT INTO `b` VALUES (24, 'x', 'v24');
INSERT INTO `b` VALUES (25, 'y', 'v25');
INSERT INTO `b` VALUES (26, 'z', 'v26');
COMMIT;

-- ----------------------------
-- Table structure for c
-- ----------------------------
DROP TABLE IF EXISTS `c`;
CREATE TABLE `c` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) NOT NULL,
  `value` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=27 DEFAULT CHARSET=latin1;

-- ----------------------------
-- Records of c
-- ----------------------------
BEGIN;
INSERT INTO `c` VALUES (1, 'a', 'v1');
INSERT INTO `c` VALUES (2, 'a', 'v1');
INSERT INTO `c` VALUES (3, 'a', 'v1');
INSERT INTO `c` VALUES (4, 'd', 'v4');
INSERT INTO `c` VALUES (5, 'e', 'v5');
INSERT INTO `c` VALUES (6, 'f', 'v6');
INSERT INTO `c` VALUES (7, 'g', 'v7');
INSERT INTO `c` VALUES (8, 'h', 'v8');
INSERT INTO `c` VALUES (9, 'i', 'v9');
INSERT INTO `c` VALUES (10, 'j', 'v10');
INSERT INTO `c` VALUES (11, 'k', 'v11');
INSERT INTO `c` VALUES (12, 'l', 'v12');
INSERT INTO `c` VALUES (13, 'm', 'v13');
INSERT INTO `c` VALUES (14, 'n', 'v14');
INSERT INTO `c` VALUES (15, 'o', 'v15');
INSERT INTO `c` VALUES (16, 'p', 'v16');
INSERT INTO `c` VALUES (17, 'q', 'v17');
INSERT INTO `c` VALUES (18, 'r', 'v18');
INSERT INTO `c` VALUES (19, 's', 'v19');
INSERT INTO `c` VALUES (20, 't', 'v20');
INSERT INTO `c` VALUES (21, 'u', 'v21');
INSERT INTO `c` VALUES (22, 'v', 'v22');
INSERT INTO `c` VALUES (23, 'w', 'v23');
INSERT INTO `c` VALUES (24, 'x', 'v24');
INSERT INTO `c` VALUES (25, 'y', 'v25');
INSERT INTO `c` VALUES (26, 'z', 'v26');
COMMIT;

SET FOREIGN_KEY_CHECKS = 1;
