/*
 Navicat Premium Dump SQL

 Source Server         : localhost
 Source Server Type    : MySQL
 Source Server Version : 50720 (5.7.20-log)
 Source Host           : localhost:3306
 Source Schema         : deer_wcs_jxg_zk

 Target Server Type    : MySQL
 Target Server Version : 50720 (5.7.20-log)
 File Encoding         : 65001

 Date: 30/11/2025 15:28:48
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for rcs_car_info
-- ----------------------------
DROP TABLE IF EXISTS `rcs_car_info`;
CREATE TABLE `rcs_car_info`  (
  `id` bigint(11) NOT NULL AUTO_INCREMENT,
  `device_id` bigint(11) NULL DEFAULT NULL,
  `code` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '编码',
  `name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '名称',
  `ware_code` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `rcs_car_type_id` bigint(11) NULL DEFAULT NULL COMMENT '类型ID',
  `disable_state` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '禁用状态',
  `task_state` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '任务状态',
  `battery_level` int(11) NULL DEFAULT NULL COMMENT '电量',
  `is_charge` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '是否充电',
  `load_state` int(11) NULL DEFAULT 0 COMMENT '负载状态（0-空载/1-负载）',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `create_user_id` bigint(20) NULL DEFAULT NULL,
  `create_user_name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '创建人',
  `update_user_id` bigint(20) NULL DEFAULT NULL,
  `update_user_name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '更新人',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  `version` bigint(20) NULL DEFAULT 0,
  `is_connected` int(255) NULL DEFAULT NULL,
  `current_x` int(11) NULL DEFAULT NULL COMMENT '当前X坐标(mm，下位地图坐标)',
  `current_y` int(11) NULL DEFAULT NULL COMMENT '当前Y坐标(mm，下位地图坐标)',
  `current_z` int(11) NULL DEFAULT NULL COMMENT '当前Z坐标(mm，下位地图坐标)',
  `from_cell_code` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '起始库位编码',
  `to_cell_code` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '目标库位编码',
  `z` int(11) NULL DEFAULT NULL,
  `position_ratio` decimal(5, 4) NULL DEFAULT NULL COMMENT '位置插值系数(0-1)：0=起始库位，1=目标库位',
  `move_direction` int(11) NULL DEFAULT NULL COMMENT '移动方向：0-静止 1-右 2-左 3-上 4-下',
  `speed` decimal(10, 2) NULL DEFAULT NULL COMMENT '当前速度(mm/s)',
  `last_update_time` datetime NULL DEFAULT NULL COMMENT '位置最后更新时间',
  `ip` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `port` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_current_z`(`current_z`) USING BTREE,
  INDEX `idx_update_time`(`last_update_time`) USING BTREE,
  INDEX `idx_from_cell`(`from_cell_code`) USING BTREE,
  INDEX `idx_to_cell`(`to_cell_code`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 9 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '车' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of rcs_car_info
-- ----------------------------
INSERT INTO `rcs_car_info` VALUES (7, NULL, '4840', '智库小车-4840', 'sxc', NULL, '0', '0', 81, '0', 0, '2025-10-27 16:00:00', NULL, '系统自动创建', NULL, NULL, '2025-11-17 09:22:24', 0, 0, 1000, 999, 1, '1-8-6', '1-8-6', 1, 0.0000, NULL, NULL, '2025-11-21 15:28:51', '192.168.1.171', NULL);
INSERT INTO `rcs_car_info` VALUES (8, NULL, '4841', '智库小车-4841', 'sxc', NULL, '0', '0', 65, '0', 0, '2025-10-30 16:10:51', NULL, '系统自动创建', 1, 'admin', '2025-11-30 10:33:39', 0, 0, 7750, 999, 3, '1-7-2', '1-7-2', 1, 0.0000, NULL, NULL, '2025-11-21 15:28:51', '192.168.1.172', NULL);

SET FOREIGN_KEY_CHECKS = 1;
