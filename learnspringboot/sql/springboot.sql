/*
Navicat MySQL Data Transfer

Source Server         : localhost
Source Server Version : 50624
Source Host           : localhost:3306
Source Database       : springboot

Target Server Type    : MYSQL
Target Server Version : 50624
File Encoding         : 65001

Date: 2019-04-01 16:04:18
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for t_product
-- ----------------------------
DROP TABLE IF EXISTS `t_product`;
CREATE TABLE `t_product` (
  `id` int(12) NOT NULL AUTO_INCREMENT COMMENT '编号',
  `product_name` varchar(255) NOT NULL COMMENT '产品名称',
  `stock` int(10) unsigned zerofill NOT NULL COMMENT '库存',
  `price` decimal(16,2) NOT NULL COMMENT '单价',
  `version` int(10) NOT NULL COMMENT '版本号',
  `note` varchar(255) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8 COMMENT='产品信息表';

-- ----------------------------
-- Records of t_product
-- ----------------------------
INSERT INTO `t_product` VALUES ('1', '深入浅出 springboot 2.x', '0000000094', '10.00', '0', '有了springboot，再也不担心那么多麻烦的配置啦');

-- ----------------------------
-- Table structure for t_purchase_record
-- ----------------------------
DROP TABLE IF EXISTS `t_purchase_record`;
CREATE TABLE `t_purchase_record` (
  `id` int(12) unsigned zerofill NOT NULL AUTO_INCREMENT,
  `user_id` int(12) unsigned zerofill NOT NULL COMMENT '用户编号',
  `product_id` int(12) unsigned zerofill NOT NULL COMMENT '产品编号',
  `price` decimal(16,2) NOT NULL COMMENT '价格',
  `quantity` int(12) unsigned zerofill NOT NULL COMMENT '数量',
  `sum` decimal(16,2) NOT NULL COMMENT '总价',
  `purchase_date` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00' ON UPDATE CURRENT_TIMESTAMP COMMENT '购买日期',
  `note` varchar(512) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8 COMMENT='购买信息表';

-- ----------------------------
-- Records of t_purchase_record
-- ----------------------------
INSERT INTO `t_purchase_record` VALUES ('000000000001', '000000000001', '000000000001', '10.00', '000000000003', '30.00', '2019-04-01 15:55:39', '购买日志，时间：1554105339915');
INSERT INTO `t_purchase_record` VALUES ('000000000002', '000000000001', '000000000001', '10.00', '000000000003', '30.00', '2019-04-01 16:03:47', '购买日志，时间：1554105827839');
