# deer_wcs

#### 更新日志
1. 7厂项目需求 pro_position_content 增加pallet_state 字段  0-空托盘 1-托盘满 
2. 20250213 cell_info 增加type  0-正常 1-入库接驳 2-出库接驳 
3. 20250213 line_info 增加device_code 代表堆垛机编号

20250224
task_info type int 变为varchar
task_inf_his type int 变为varchar
job_info type int 变为varchar
job_info_his type int 变为varchar
path_info type int 变为varchar
path_info_his type int 变为varchar

0324
pro_position_content 增加 in_time 字段 入库时间

0428 
新增 position_record 表
pro_position_content 增加 ware_code 字段  仓库编号

250818
ware_info 增加 type   区分 堆垛机库和四向车库
增加 floor_info 表
cell_info 增加 is_move
line_info 增加 start_direction  model_data

251014
cell_info 增加rcs_car_id 字段  代表该cell被该车子占用
cell_info 增加sub_x 字段   
cell_info 增加sub_y 字段   
cell_info 增加sub_z 字段   

job_info 增加 rcs_car_id 字段  代表该作业属于该车子
job_info 增加 device_code 字段   设备编号 

**251026** 
task_define 增加 last_id  is_judge_step judge_branch_type position_x position_y  
job_info 增加 last_job_id 字段   is_judge_step judge_branch_type 

251030
task_info 增加 rcs_car_id 字段  代表该任务属于该车子

251104

cell_info增加字段 record_content

CREATE TABLE `cell_record` (
`id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
`cell_id` bigint(20) DEFAULT NULL COMMENT '库位ID',
`cell_code` varchar(50) DEFAULT NULL COMMENT '库位编码',
`ware_code` varchar(50) DEFAULT NULL COMMENT '仓库编码',
`content` text COMMENT '日志内容',
`create_time` varchar(50)     COMMENT '创建时间',
PRIMARY KEY (`id`)

) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='库位日志记录表';

251121

host_wcs_interface 表 新增字段 job_id  代表该接口属于该作业

