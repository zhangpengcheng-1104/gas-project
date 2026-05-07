-- =============================================
-- 燃气巡检计划表初始化脚本
-- 说明: 包含巡检计划表结构和测试数据
-- =============================================

-- 创建巡检计划表（如果不存在）
CREATE TABLE IF NOT EXISTS `gas_worker_plan` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `worker_id` int(11) DEFAULT NULL COMMENT '巡检人员ID',
  `dept_sub_id` int(11) DEFAULT NULL COMMENT '班组ID',
  `date` date DEFAULT NULL COMMENT '巡检日期',
  `user_id` bigint(20) DEFAULT NULL COMMENT '燃气用户ID',
  `maximum` int(11) DEFAULT 1 COMMENT '最大人数',
  `num` int(11) DEFAULT NULL COMMENT '已预约人数',
  `description` varchar(255) DEFAULT NULL COMMENT '备注说明',
  `file_path` varchar(255) DEFAULT NULL COMMENT '附件路径',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `idx_date_dept` (`date`, `dept_sub_id`) USING BTREE COMMENT '日期和班组联合索引'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 ROW_FORMAT=DYNAMIC COMMENT='燃气巡检计划表';

-- 插入测试数据
INSERT INTO `gas_worker_plan` (`worker_id`, `dept_sub_id`, `date`, `user_id`, `maximum`, `description`) VALUES
(1, 1, CURDATE(), 21, 3, '日常巡检'),
(2, 1, DATE_ADD(CURDATE(), INTERVAL 1 DAY), 22, 3, '设备检查'),
(1, 1, DATE_ADD(CURDATE(), INTERVAL 2 DAY), 23, 2, '安全检查'),
(3, 1, DATE_ADD(CURDATE(), INTERVAL 3 DAY), NULL, 4, '定期巡检'),
(2, 2, CURDATE(), 24, 3, '专项检查');

-- 查询验证
SELECT 
    p.id,
    w.name AS worker_name,
    ds.name AS dept_sub_name,
    p.date,
    gu.user_name,
    p.maximum,
    p.description
FROM gas_worker_plan p
LEFT JOIN gas_worker w ON p.worker_id = w.id
LEFT JOIN medical_dept_sub ds ON p.dept_sub_id = ds.id
LEFT JOIN gas_user gu ON p.user_id = gu.id
ORDER BY p.date;
