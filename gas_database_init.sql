-- =============================================
-- 燃气管理系统数据库初始化脚本
-- 生成时间: 2026-04-02
-- 数据库名: gas
-- 说明: 包含表结构和初始化数据
-- =============================================

-- 创建数据库（如果不存在）
CREATE DATABASE IF NOT EXISTS `gas` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;

USE `gas`;

-- =============================================
-- 1. 燃气用户表
-- =============================================
DROP TABLE IF EXISTS `gas_user`;
CREATE TABLE `gas_user` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '用户ID',
  `user_no` varchar(50) NOT NULL COMMENT '用户户号（唯一）',
  `user_name` varchar(100) DEFAULT NULL COMMENT '户名',
  `user_type` varchar(50) DEFAULT NULL COMMENT '用户类型（居民用户，非居民用户）',
  `area_code` varchar(50) DEFAULT NULL COMMENT '行政区域编码，关联tbl_area_node.code',
  `area_name` varchar(200) DEFAULT NULL COMMENT '行政区域名称（冗余）',
  `address` varchar(500) DEFAULT NULL COMMENT '详细地址',
  `building_info` varchar(200) DEFAULT NULL COMMENT '楼栋门牌信息',
  `contact_phone` varchar(20) DEFAULT NULL COMMENT '联系电话',
  `id_card` varchar(18) DEFAULT NULL COMMENT '证件号码',
  `status` tinyint(4) DEFAULT '1' COMMENT '状态：1正常，0停用',
  `org_id` bigint(20) DEFAULT NULL COMMENT '所属组织ID',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `create_id` bigint(20) DEFAULT NULL COMMENT '创建人ID',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  `update_id` bigint(20) DEFAULT NULL COMMENT '修改人ID',
  `deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否删除（0-未删除，1-已删除）',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `idx_user_no` (`user_no`) USING BTREE COMMENT '用户户号唯一索引'
) ENGINE=InnoDB AUTO_INCREMENT=13800138029 DEFAULT CHARSET=utf8mb4 ROW_FORMAT=DYNAMIC COMMENT='燃气用户信息表';

-- 插入燃气用户数据
INSERT INTO `gas_user` VALUES (21,'10001','张三','RESIDENT','500000/500155/500155101','','重庆市梁平区双桂街道知德大道38号阳光·玫瑰城','1栋101','19130452635','',1,1,'2026-03-26 09:44:25',17,'2026-03-26 09:44:25',17,0);
INSERT INTO `gas_user` VALUES (22,'10002','李四','NON_RESIDENT','500000/500155/500155100','','重庆市梁平区梁山街道人民西路331号西池市场','西路115号','19165781245','',1,1,'2026-03-26 09:47:46',17,'2026-04-01 14:58:29',17,0);
INSERT INTO `gas_user` VALUES (23,'10003','王五','NON_RESIDENT','500000/500155/500155100','','重庆市梁平区梁山街道重百超市(重百梁平商场店)重百电器梁平店','东路115号','19165781245','',1,1,'2026-03-26 09:50:06',17,'2026-04-01 14:58:29',17,0);
INSERT INTO `gas_user` VALUES (24,'10005','钱七','NON_RESIDENT','500000/500155/500155100','','重庆市梁平区双桂街道明珠·上海城A区','南路115号','19165781345','',1,1,'2026-03-26 09:58:44',17,'2026-04-01 14:58:29',17,0);
INSERT INTO `gas_user` VALUES (25,'10006','钱九','RESIDENT','500000/500155/500155101','','重庆市梁平区双桂街道重庆张鸭子食品有限公司','南路115号','19165781343','',1,1,'2026-03-26 10:00:00',17,'2026-03-26 10:00:00',17,0);
INSERT INTO `gas_user` VALUES (26,'10007','李欢','NON_RESIDENT','500000/500155/500155100','','重庆市梁平区梁山街道梁平一中','南路115号','19165781343','',1,1,'2026-03-26 10:01:03',17,'2026-04-01 14:58:29',17,0);
INSERT INTO `gas_user` VALUES (27,'10008','李平','RESIDENT','500000/500155/500155100','','重庆市梁平区梁山街道梁平一中','南路115号','19165781343','',1,1,'2026-03-26 10:01:26',17,'2026-03-26 10:01:26',17,0);
INSERT INTO `gas_user` VALUES (28,'10009','李平','RESIDENT','500000/500155/500155100','','重庆市梁平区梁山街道石马路71号雪莱·翡翠城','噢路115号','19164781343','',1,27,'2026-03-26 10:02:45',17,'2026-03-26 15:16:34',17,0);
INSERT INTO `gas_user` VALUES (29,'100010','赵九','NON_RESIDENT','500000/500155/500155100','','重庆市两江新区鱼嘴镇双辉路','111111','19122651245','',1,27,'2026-03-26 11:19:35',0,'2026-04-01 14:58:29',0,0);
INSERT INTO `gas_user` VALUES (13800138022,'1111','张棚程','RESIDENT','500000/500103','重庆市/渝中区','重庆市渝中区解放碑街道新华路解放碑步行街','111','13800000000','510101199001011234',1,NULL,'2026-04-01 15:01:30',NULL,'2026-04-02 09:10:33',NULL,0);

-- =============================================
-- 2. 燃气巡检人员表
-- =============================================
DROP TABLE IF EXISTS `gas_worker`;
CREATE TABLE `gas_worker` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `name` varchar(30) DEFAULT NULL COMMENT '姓名',
  `pid` varchar(18) DEFAULT NULL COMMENT '身份证',
  `uuid` varchar(32) DEFAULT NULL COMMENT 'UUID',
  `sex` varchar(1) DEFAULT NULL COMMENT '性别',
  `photo` varchar(255) DEFAULT NULL COMMENT '照片地址',
  `birthday` date DEFAULT NULL COMMENT '生日',
  `school` varchar(50) DEFAULT NULL COMMENT '毕业院校',
  `degree` varchar(20) DEFAULT NULL COMMENT '学位',
  `tel` varchar(11) DEFAULT NULL COMMENT '电话',
  `address` varchar(200) DEFAULT NULL COMMENT '住址',
  `email` varchar(200) DEFAULT NULL COMMENT '电子邮箱',
  `job` varchar(20) DEFAULT NULL COMMENT '职务（主任医师、副主任医师等）',
  `remark` varchar(50) DEFAULT NULL COMMENT '备注',
  `description` varchar(255) DEFAULT NULL COMMENT '详细介绍',
  `hiredate` date DEFAULT NULL COMMENT '入职时间',
  `tag` varchar(60) DEFAULT NULL COMMENT '特点标签（小程序上用到）',
  `recommended` tinyint(4) DEFAULT NULL COMMENT '是否在小程序上推荐',
  `status` tinyint(4) DEFAULT NULL COMMENT '1在职，2离职，3退休，4隐藏（逻辑删除）',
  `create_time` datetime DEFAULT NULL COMMENT '记录时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC;

-- 插入巡检人员数据
INSERT INTO `gas_worker` VALUES (1,'程淳美','360201198609151112','2F0EB81AF9094277A958A41B59139DE1','女','/worker-1.jpg','1968-05-03','重庆电力专科学校','大专','13593812535','重庆市南岸区','chengchunmei@hospital.com','高工','重庆电力高专导师',NULL,'2004-02-15','[\"从业41年\",\"领域专家\",\"温暖贴心\"]',1,1,'2025-09-08 23:07:27');
INSERT INTO `gas_worker` VALUES (2,'秦欣源','460201197611302855','F1FDE764A9BB405596895722F1CCDB06','男','/worker-2.jpg','1959-05-03','重庆电力专科学校','大专','15179382777','重庆市巴南区','qinxinyuan@hospital.com','工程师','重庆电力高专导师',NULL,'2004-12-11','[\"从业46年\",\"领域专家\",\"快速回复\"]',1,1,'2025-09-08 23:07:27');
INSERT INTO `gas_worker` VALUES (3,'张棚程','500235200411040000','B25EDA15237B4238AD4AD2569E2E85CA','男','/worker-3.jpg','2026-03-01','重庆工程学院','大学','13896130589','111','1164758291@qq.com','工程师','222','111','2026-03-01','[\"工作认真\",\"学习能力强\",\"团队协作\",\"责任心强\"]',1,1,'2026-03-31 23:36:22');

-- =============================================
-- 3. 部门科室表
-- =============================================
DROP TABLE IF EXISTS `medical_dept`;
CREATE TABLE `medical_dept` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `name` varchar(50) DEFAULT NULL COMMENT '科室名称',
  `outpatient` tinyint(4) DEFAULT NULL,
  `description` varchar(500) DEFAULT NULL COMMENT '简介',
  `recommended` tinyint(4) DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC;

-- 插入部门数据
INSERT INTO `medical_dept` VALUES (1,'巡检一科',NULL,NULL,NULL);

-- =============================================
-- 4. 科室队组表
-- =============================================
DROP TABLE IF EXISTS `medical_dept_sub`;
CREATE TABLE `medical_dept_sub` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(50) DEFAULT NULL,
  `dept_id` int(11) DEFAULT NULL,
  `location` varchar(50) DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC;

-- 插入队组数据
INSERT INTO `medical_dept_sub` VALUES (1,'巡检一组',1,NULL);
INSERT INTO `medical_dept_sub` VALUES (2,'巡检二组',1,NULL);

-- =============================================
-- 5. 科室队组人员关联表
-- =============================================
DROP TABLE IF EXISTS `medical_dept_sub_and_worker`;
CREATE TABLE `medical_dept_sub_and_worker` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `dept_sub_id` int(11) DEFAULT NULL,
  `worker_id` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC;

-- 插入关联数据
INSERT INTO `medical_dept_sub_and_worker` VALUES (1,1,1);
INSERT INTO `medical_dept_sub_and_worker` VALUES (2,1,2);
INSERT INTO `medical_dept_sub_and_worker` VALUES (3,1,3);

-- =============================================
-- 6. 系统用户表
-- =============================================
DROP TABLE IF EXISTS `mis_user`;
CREATE TABLE `mis_user` (
  `id` int(11) NOT NULL,
  `username` varchar(50) DEFAULT NULL,
  `password` varchar(100) DEFAULT NULL,
  `name` varchar(20) DEFAULT NULL,
  `sex` varchar(1) DEFAULT NULL,
  `tel` varchar(30) DEFAULT NULL,
  `email` varchar(100) DEFAULT NULL,
  `dept_id` int(11) DEFAULT NULL,
  `job` varchar(20) DEFAULT NULL,
  `ref_id` int(11) DEFAULT NULL,
  `status` tinyint(4) DEFAULT NULL,
  `create_time` datetime DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC;

-- 插入系统用户数据
INSERT INTO `mis_user` VALUES (0,'admin','061575f43e456772015c0032c0531edf','超级管理员','男',NULL,NULL,NULL,NULL,NULL,1,'2025-09-09 03:48:48');
INSERT INTO `mis_user` VALUES (5,'zzzpc','35d28d39dd5ce6fdab0347ec01b33aab','自增长','男','12345678901','1234567890@qq.com',1,NULL,NULL,1,'2026-03-24 10:46:11');

-- =============================================
-- 7. 部门表
-- =============================================
DROP TABLE IF EXISTS `mis_dept`;
CREATE TABLE `mis_dept` (
  `id` int(11) NOT NULL,
  `name` varchar(60) DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC;

-- 插入部门数据
INSERT INTO `mis_dept` VALUES (1,'办公室');
INSERT INTO `mis_dept` VALUES (2,'人力资源部');
INSERT INTO `mis_dept` VALUES (3,'财务部');
INSERT INTO `mis_dept` VALUES (4,'保卫部');
INSERT INTO `mis_dept` VALUES (5,'后勤部');
INSERT INTO `mis_dept` VALUES (6,'工程部');
INSERT INTO `mis_dept` VALUES (7,'基建部');
INSERT INTO `mis_dept` VALUES (8,'物资部');
INSERT INTO `mis_dept` VALUES (9,'运营部');
INSERT INTO `mis_dept` VALUES (13,'科研教育部');
INSERT INTO `mis_dept` VALUES (14,'国际合作部');
INSERT INTO `mis_dept` VALUES (15,'信息中心');
INSERT INTO `mis_dept` VALUES (16,'公共关系部');
INSERT INTO `mis_dept` VALUES (20,'基础教育部');
INSERT INTO `mis_dept` VALUES (21,'生产部');

-- =============================================
-- 8. 角色表
-- =============================================
DROP TABLE IF EXISTS `mis_role`;
CREATE TABLE `mis_role` (
  `id` int(11) NOT NULL,
  `role_name` varchar(50) DEFAULT NULL,
  `remark` varchar(50) DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC;

-- 插入角色数据
INSERT INTO `mis_role` VALUES (0,'超级管理员','超级管理员用户不可修改和删除');
INSERT INTO `mis_role` VALUES (1,'经理','经理角色');
INSERT INTO `mis_role` VALUES (2,'人事员','人事员');
INSERT INTO `mis_role` VALUES (3,'维修员','维修员');
INSERT INTO `mis_role` VALUES (4,'测试员','测试');

-- =============================================
-- 9. 权限表
-- =============================================
DROP TABLE IF EXISTS `mis_permission`;
CREATE TABLE `mis_permission` (
  `id` int(10) unsigned NOT NULL COMMENT '主键',
  `permission_code` varchar(200) NOT NULL COMMENT '权限',
  `module_id` int(10) unsigned NOT NULL COMMENT '模块ID',
  `action_id` int(10) unsigned NOT NULL COMMENT '行为ID',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `unq_permission` (`permission_code`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC COMMENT='权限表';

-- 插入权限数据
INSERT INTO `mis_permission` VALUES (0,'ROOT',0,0);
INSERT INTO `mis_permission` VALUES (1,'MIS_USER:INSERT',1,1);
INSERT INTO `mis_permission` VALUES (2,'MIS_USER:DELETE',1,2);
INSERT INTO `mis_permission` VALUES (3,'MIS_USER:UPDATE',1,3);
INSERT INTO `mis_permission` VALUES (4,'MIS_USER:SELECT',1,4);
INSERT INTO `mis_permission` VALUES (13,'DEPT:INSERT',4,1);
INSERT INTO `mis_permission` VALUES (14,'DEPT:DELETE',4,2);
INSERT INTO `mis_permission` VALUES (15,'DEPT:UPDATE',4,3);
INSERT INTO `mis_permission` VALUES (16,'DEPT:SELECT',4,4);
INSERT INTO `mis_permission` VALUES (17,'MEDICAL_DEPT:INSERT',5,1);
INSERT INTO `mis_permission` VALUES (18,'MEDICAL_DEPT:DELETE',5,2);
INSERT INTO `mis_permission` VALUES (19,'MEDICAL_DEPT:UPDATE',5,3);
INSERT INTO `mis_permission` VALUES (20,'MEDICAL_DEPT:SELECT',5,4);
INSERT INTO `mis_permission` VALUES (21,'MEDICAL_DEPT_SUB:INSERT',6,1);
INSERT INTO `mis_permission` VALUES (22,'MEDICAL_DEPT_SUB:DELETE',6,2);
INSERT INTO `mis_permission` VALUES (23,'MEDICAL_DEPT_SUB:UPDATE',6,3);
INSERT INTO `mis_permission` VALUES (24,'MEDICAL_DEPT_SUB:SELECT',6,4);
INSERT INTO `mis_permission` VALUES (50,'MIS_ROLE:INSERT',15,1);
INSERT INTO `mis_permission` VALUES (51,'MIS_ROLE:DELETE',15,2);
INSERT INTO `mis_permission` VALUES (52,'MIS_ROLE:UPDATE',15,3);
INSERT INTO `mis_permission` VALUES (53,'MIS_ROLE:SELECT',15,4);
INSERT INTO `mis_permission` VALUES (54,'GAS_WORKER:INSERT',13,1);
INSERT INTO `mis_permission` VALUES (55,'GAS_WORKER:DELETE',13,2);
INSERT INTO `mis_permission` VALUES (56,'GAS_WORKER:UPDATE',13,3);
INSERT INTO `mis_permission` VALUES (57,'GAS_WORKER:SELECT',13,4);
INSERT INTO `mis_permission` VALUES (58,'GAS_USER:INSERT',12,1);
INSERT INTO `mis_permission` VALUES (59,'GAS_USER:DELETE',12,2);
INSERT INTO `mis_permission` VALUES (60,'GAS_USER:UPDATE',12,3);
INSERT INTO `mis_permission` VALUES (61,'GAS_USER:SELECT',12,4);

-- =============================================
-- 10. 角色权限关联表
-- =============================================
DROP TABLE IF EXISTS `mis_role_permission`;
CREATE TABLE `mis_role_permission` (
  `id` int(11) NOT NULL,
  `role_id` int(11) DEFAULT NULL,
  `permission_id` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC;

-- 插入角色权限关联数据
INSERT INTO `mis_role_permission` VALUES (5,5,1);
INSERT INTO `mis_role_permission` VALUES (6,5,2);
INSERT INTO `mis_role_permission` VALUES (7,5,3);
INSERT INTO `mis_role_permission` VALUES (8,5,4);
INSERT INTO `mis_role_permission` VALUES (54,5,50);
INSERT INTO `mis_role_permission` VALUES (55,5,51);
INSERT INTO `mis_role_permission` VALUES (56,5,52);
INSERT INTO `mis_role_permission` VALUES (57,5,53);
INSERT INTO `mis_role_permission` VALUES (331,1,1);
INSERT INTO `mis_role_permission` VALUES (332,1,2);
INSERT INTO `mis_role_permission` VALUES (333,1,3);
INSERT INTO `mis_role_permission` VALUES (334,1,4);
INSERT INTO `mis_role_permission` VALUES (335,1,50);
INSERT INTO `mis_role_permission` VALUES (336,1,51);
INSERT INTO `mis_role_permission` VALUES (337,1,52);
INSERT INTO `mis_role_permission` VALUES (338,1,53);
INSERT INTO `mis_role_permission` VALUES (339,1,54);
INSERT INTO `mis_role_permission` VALUES (340,1,55);
INSERT INTO `mis_role_permission` VALUES (341,1,56);
INSERT INTO `mis_role_permission` VALUES (342,1,57);
INSERT INTO `mis_role_permission` VALUES (343,1,13);
INSERT INTO `mis_role_permission` VALUES (344,1,14);
INSERT INTO `mis_role_permission` VALUES (345,1,15);
INSERT INTO `mis_role_permission` VALUES (346,1,16);
INSERT INTO `mis_role_permission` VALUES (347,1,17);
INSERT INTO `mis_role_permission` VALUES (348,1,18);
INSERT INTO `mis_role_permission` VALUES (349,1,19);
INSERT INTO `mis_role_permission` VALUES (350,1,20);
INSERT INTO `mis_role_permission` VALUES (351,1,21);
INSERT INTO `mis_role_permission` VALUES (352,1,22);
INSERT INTO `mis_role_permission` VALUES (353,1,23);
INSERT INTO `mis_role_permission` VALUES (354,1,24);
INSERT INTO `mis_role_permission` VALUES (397,1,58);
INSERT INTO `mis_role_permission` VALUES (398,1,59);
INSERT INTO `mis_role_permission` VALUES (399,1,60);
INSERT INTO `mis_role_permission` VALUES (400,1,61);
INSERT INTO `mis_role_permission` VALUES (367,3,3);
INSERT INTO `mis_role_permission` VALUES (368,3,4);
INSERT INTO `mis_role_permission` VALUES (369,4,1);
INSERT INTO `mis_role_permission` VALUES (370,4,2);
INSERT INTO `mis_role_permission` VALUES (371,4,3);
INSERT INTO `mis_role_permission` VALUES (372,4,4);
INSERT INTO `mis_role_permission` VALUES (373,4,50);
INSERT INTO `mis_role_permission` VALUES (374,4,51);
INSERT INTO `mis_role_permission` VALUES (375,4,52);
INSERT INTO `mis_role_permission` VALUES (376,4,53);
INSERT INTO `mis_role_permission` VALUES (377,4,54);
INSERT INTO `mis_role_permission` VALUES (378,4,55);
INSERT INTO `mis_role_permission` VALUES (379,4,56);
INSERT INTO `mis_role_permission` VALUES (380,4,57);
INSERT INTO `mis_role_permission` VALUES (381,4,58);
INSERT INTO `mis_role_permission` VALUES (382,4,59);
INSERT INTO `mis_role_permission` VALUES (383,4,60);
INSERT INTO `mis_role_permission` VALUES (384,4,61);
INSERT INTO `mis_role_permission` VALUES (385,4,13);
INSERT INTO `mis_role_permission` VALUES (386,4,14);
INSERT INTO `mis_role_permission` VALUES (387,4,15);
INSERT INTO `mis_role_permission` VALUES (388,4,16);
INSERT INTO `mis_role_permission` VALUES (389,4,17);
INSERT INTO `mis_role_permission` VALUES (390,4,18);
INSERT INTO `mis_role_permission` VALUES (391,4,19);
INSERT INTO `mis_role_permission` VALUES (392,4,20);
INSERT INTO `mis_role_permission` VALUES (393,4,21);
INSERT INTO `mis_role_permission` VALUES (394,4,22);
INSERT INTO `mis_role_permission` VALUES (395,4,23);
INSERT INTO `mis_role_permission` VALUES (396,4,24);

-- =============================================
-- 11. 用户角色关联表
-- =============================================
DROP TABLE IF EXISTS `mis_user_role`;
CREATE TABLE `mis_user_role` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `user_id` int(11) DEFAULT NULL,
  `role_id` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=87 DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC;

-- 插入用户角色关联数据
INSERT INTO `mis_user_role` VALUES (1,0,0);
INSERT INTO `mis_user_role` VALUES (85,5,3);
INSERT INTO `mis_user_role` VALUES (86,5,4);

-- =============================================
-- 12. 模块表
-- =============================================
DROP TABLE IF EXISTS `mis_module`;
CREATE TABLE `mis_module` (
  `id` int(11) NOT NULL,
  `module_code` varchar(60) DEFAULT NULL,
  `module_name` varchar(60) DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC;

-- 插入模块数据
INSERT INTO `mis_module` VALUES (1,'MIS_USER','MIS端用户管理');
INSERT INTO `mis_module` VALUES (4,'DEPT','部门管理');
INSERT INTO `mis_module` VALUES (5,'MEDICAL_DEPT','部门科室管理');
INSERT INTO `mis_module` VALUES (6,'MEDICAL_DEPT_SUB','科室队组管理');
INSERT INTO `mis_module` VALUES (12,'GAS_USER','燃气用户管理');
INSERT INTO `mis_module` VALUES (13,'GAS_WORKER','巡检人员管理');
INSERT INTO `mis_module` VALUES (14,'SYSTEM','系统管理');
INSERT INTO `mis_module` VALUES (15,'MIS_ROLE','角色管理');

-- =============================================
-- 13. 行为表
-- =============================================
DROP TABLE IF EXISTS `mis_action`;
CREATE TABLE `mis_action` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `action_code` varchar(60) DEFAULT NULL,
  `action_name` varchar(60) DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC;

-- 插入行为数据
INSERT INTO `mis_action` VALUES (1,'INSERT','添加');
INSERT INTO `mis_action` VALUES (2,'DELETE','删除');
INSERT INTO `mis_action` VALUES (3,'UPDATE','修改');
INSERT INTO `mis_action` VALUES (4,'SELECT','查询');
INSERT INTO `mis_action` VALUES (5,'APPROVAL','审批');
INSERT INTO `mis_action` VALUES (6,'EXPORT','导出');
INSERT INTO `mis_action` VALUES (7,'BACKUP','备份');
INSERT INTO `mis_action` VALUES (8,'ARCHIVE','归档');

-- =============================================
-- 14. 行政区域表
-- =============================================
DROP TABLE IF EXISTS `tbl_area_node`;
CREATE TABLE `tbl_area_node` (
  `code` varchar(50) DEFAULT NULL COMMENT '省市区编码',
  `area_name` varchar(50) DEFAULT NULL COMMENT '省市区名称',
  `parent_code` varchar(50) DEFAULT NULL COMMENT '父级编码',
  `level` int(11) DEFAULT NULL COMMENT '层级'
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC COMMENT='省市区编码';

-- 插入行政区域数据
INSERT INTO `tbl_area_node` VALUES ('500000','重庆市','0',1);
INSERT INTO `tbl_area_node` VALUES ('500155','梁平区','500000',2);
INSERT INTO `tbl_area_node` VALUES ('500155100','梁山街道','500155',3);
INSERT INTO `tbl_area_node` VALUES ('500155101','双桂街道','500155',3);
INSERT INTO `tbl_area_node` VALUES ('500155102','仁贤街道','500155',3);
INSERT INTO `tbl_area_node` VALUES ('500155103','金带街道','500155',3);
INSERT INTO `tbl_area_node` VALUES ('500155200','合兴镇','500155',3);
INSERT INTO `tbl_area_node` VALUES ('500155201','礼让镇','500155',3);
INSERT INTO `tbl_area_node` VALUES ('500155202','云龙镇','500155',3);
INSERT INTO `tbl_area_node` VALUES ('500155203','屏锦镇','500155',3);
INSERT INTO `tbl_area_node` VALUES ('500155204','袁驿镇','500155',3);
INSERT INTO `tbl_area_node` VALUES ('500155205','新盛镇','500155',3);
INSERT INTO `tbl_area_node` VALUES ('500155206','福禄镇','500155',3);
INSERT INTO `tbl_area_node` VALUES ('500155207','聚奎镇','500155',3);
INSERT INTO `tbl_area_node` VALUES ('500155208','明达镇','500155',3);
INSERT INTO `tbl_area_node` VALUES ('500155209','荫平镇','500155',3);
INSERT INTO `tbl_area_node` VALUES ('500155210','和林镇','500155',3);
INSERT INTO `tbl_area_node` VALUES ('500155211','回龙镇','500155',3);
INSERT INTO `tbl_area_node` VALUES ('500155212','碧山镇','500155',3);
INSERT INTO `tbl_area_node` VALUES ('500155213','虎城镇','500155',3);
INSERT INTO `tbl_area_node` VALUES ('500155214','七星镇','500155',3);
INSERT INTO `tbl_area_node` VALUES ('500155215','龙门镇','500155',3);
INSERT INTO `tbl_area_node` VALUES ('500155216','文化镇','500155',3);
INSERT INTO `tbl_area_node` VALUES ('500155217','石安镇','500155',3);
INSERT INTO `tbl_area_node` VALUES ('500155218','柏家镇','500155',3);
INSERT INTO `tbl_area_node` VALUES ('500155219','大观镇','500155',3);
INSERT INTO `tbl_area_node` VALUES ('500155220','竹山镇','500155',3);
INSERT INTO `tbl_area_node` VALUES ('500155221','蟠龙镇','500155',3);
INSERT INTO `tbl_area_node` VALUES ('500155222','星桥镇','500155',3);
INSERT INTO `tbl_area_node` VALUES ('500101','万州区','500000',2);
INSERT INTO `tbl_area_node` VALUES ('500102','涪陵区','500000',2);
INSERT INTO `tbl_area_node` VALUES ('500103','渝中区','500000',2);
INSERT INTO `tbl_area_node` VALUES ('500104','大渡口区','500000',2);
INSERT INTO `tbl_area_node` VALUES ('500105','江北区','500000',2);
INSERT INTO `tbl_area_node` VALUES ('500106','沙坪坝区','500000',2);
INSERT INTO `tbl_area_node` VALUES ('500107','九龙坡区','500000',2);
INSERT INTO `tbl_area_node` VALUES ('500108','南岸区','500000',2);
INSERT INTO `tbl_area_node` VALUES ('500109','北碚区','500000',2);
INSERT INTO `tbl_area_node` VALUES ('500110','渝北区','500000',2);
INSERT INTO `tbl_area_node` VALUES ('500111','巴南区','500000',2);
INSERT INTO `tbl_area_node` VALUES ('500112','长寿区','500000',2);
INSERT INTO `tbl_area_node` VALUES ('500113','江津区','500000',2);
INSERT INTO `tbl_area_node` VALUES ('500114','合川区','500000',2);
INSERT INTO `tbl_area_node` VALUES ('500115','永川区','500000',2);
INSERT INTO `tbl_area_node` VALUES ('500116','南川区','500000',2);
INSERT INTO `tbl_area_node` VALUES ('500117','綦江区','500000',2);
INSERT INTO `tbl_area_node` VALUES ('500118','大足区','500000',2);
INSERT INTO `tbl_area_node` VALUES ('500119','璧山区','500000',2);
INSERT INTO `tbl_area_node` VALUES ('500120','铜梁区','500000',2);
INSERT INTO `tbl_area_node` VALUES ('500151','潼南区','500000',2);
INSERT INTO `tbl_area_node` VALUES ('500152','荣昌区','500000',2);
INSERT INTO `tbl_area_node` VALUES ('500153','开州区','500000',2);
INSERT INTO `tbl_area_node` VALUES ('500154','江北区','500000',2);
INSERT INTO `tbl_area_node` VALUES ('500156','武隆区','500000',2);

-- =============================================
-- 数据库初始化完成
-- =============================================
