-- 在position_info表中添加line_code字段
ALTER TABLE `position_info` ADD COLUMN `line_code` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '线体编码' AFTER `sub_code`;
