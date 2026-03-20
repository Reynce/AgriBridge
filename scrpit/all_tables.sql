-- 系统配置表 (sys_config)
CREATE TABLE IF NOT EXISTS `sys_config` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '配置ID',
    `config_name` VARCHAR(100) NOT NULL COMMENT '配置名称',
    `config_key` VARCHAR(100) NOT NULL UNIQUE COMMENT '配置键名',
    `config_value` VARCHAR(500) NOT NULL COMMENT '配置值',
    `config_type` TINYINT DEFAULT 1 COMMENT '系统内置（1是 0否）',
    `remark` VARCHAR(500) COMMENT '备注',
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统配置表';

-- 初始配置
INSERT IGNORE INTO `sys_config` (config_name, config_key, config_value, remark) VALUES 
('邮件发送单IP10分钟限制次数', 'mail.limit.ip.max', '10', '单个IP10分钟最多发送邮件次数'),
('邮件发送单邮箱10分钟限制次数', 'mail.limit.email.max', '5', '单个邮箱10分钟最多发送邮件次数'),
('邮件发送时间间隔', 'mail.limit.interval', '60', '两次发送邮件的最小时间间隔（秒）'),
('登录验证码开关', 'sys.user.captchaEnabled', 'true', '是否开启登录图形验证码（true开启 false关闭）');


-- 用户表 (user)
CREATE TABLE `user` (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '用户ID，主键',
    username VARCHAR(100) NOT NULL COMMENT '用户名',
    account VARCHAR(100) NOT NULL UNIQUE COMMENT '登录账号',
    password VARCHAR(255) NOT NULL COMMENT '密码, 建议存储哈希值',
    email VARCHAR(100) UNIQUE COMMENT '邮箱',
    phone VARCHAR(20) UNIQUE COMMENT '电话号码',
    account_status TINYINT DEFAULT 1 COMMENT '状态: 激活/未激活/封禁',
		avatar VARCHAR(255) DEFAULT 'https://avatars.githubusercontent.com/u/9919?s=200&v=4' COMMENT '头像地址',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户信息表';


-- 管理员账号
INSERT INTO `user` (
    id,
    username,
    account,
    password,
    email,
    phone,
    account_status,
		avatar
) VALUES (
    1,
    '超级管理员',
    'admin',
    '$2a$10$KuR4r8/.nFYDVPQpQiQTuOy2EBnFAgHai2ETsCR9amjfQIE.tm2.y', -- 默认密码123123
    '2382358446@qq.com',
    '17750693980',
    1,
		'https://avatars.githubusercontent.com/u/9919?s=200&v=4'
);

-- 角色表 (role)
CREATE TABLE role (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '角色ID，主键',
    role_key VARCHAR(50) NOT NULL UNIQUE COMMENT '角色字符',
    role_name VARCHAR(100) NOT NULL COMMENT '角色显示名'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色表';

-- 用户_角色对应表 (user_role)
CREATE TABLE user_role (
    user_id BIGINT NOT NULL COMMENT '用户ID',
    role_id BIGINT NOT NULL COMMENT '角色ID',
    PRIMARY KEY (user_id, role_id),
    INDEX idx_user_id (user_id),
    INDEX idx_role_id (role_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户角色关联表';

-- 插入角色数据
INSERT INTO role (role_key, role_name) VALUES 
('ROLE_ADMIN', '管理员'),
('ROLE_PRODUCER', '生产者'),
('ROLE_CONSUMER', '消费者');

-- 分配角色给超级管理员（拥有所有角色）
INSERT INTO user_role (user_id, role_id) 
SELECT 1, id FROM role;

-- 用户地址表 (address)
CREATE TABLE address (
    id BIGINT UNSIGNED PRIMARY KEY AUTO_INCREMENT COMMENT '主键，自增，地址记录唯一标识',
    user_id BIGINT UNSIGNED NOT NULL COMMENT '用户ID，关联用户表，表示地址归属',
    recipient_name VARCHAR(50) NOT NULL COMMENT '收件人姓名',
    phone VARCHAR(20) NOT NULL COMMENT '收件人联系电话',
    province VARCHAR(32) NOT NULL COMMENT '省份信息',
    city VARCHAR(32) NOT NULL COMMENT '城市信息',
    district VARCHAR(32) NOT NULL COMMENT '区/县信息',
    address VARCHAR(128) NOT NULL COMMENT '详细地址（街道、门牌号等）',
    is_default TINYINT(1) DEFAULT 0 COMMENT '是否为默认地址，0-否 1-是',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '地址创建时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '地址更新时间',
    INDEX idx_user_id (user_id),
    INDEX idx_is_default (user_id, is_default)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户收货地址表';

-- 实名认证表 (real_name_auth)
CREATE TABLE real_name_auth (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    real_name VARCHAR(100) NOT NULL COMMENT '真实姓名',
    id_number VARCHAR(20) NOT NULL COMMENT '身份证号',
    verify_status TINYINT DEFAULT 0 COMMENT '认证状态: 审核中/已通过/未通过',
    reject_reason VARCHAR(255) COMMENT '拒绝原因',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    UNIQUE KEY uk_user_id (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='实名认证表';

-- 商家表 (merchant)
CREATE TABLE merchant (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '商家ID',
    user_id BIGINT NOT NULL COMMENT '店主用户ID',
    name VARCHAR(100) NOT NULL COMMENT '店铺名称',
    logo VARCHAR(255) COMMENT '店铺logo',
    description VARCHAR(255) COMMENT '店铺简介',
    status TINYINT DEFAULT 1 COMMENT '状态: 审核中(1)/已上线(2)/已拒绝（3）/已封禁(0)',
    license_image VARCHAR(255) COMMENT '营业执照图片',
    category_ids JSON COMMENT '经营类目ID列表，如 [1, 5, 12]',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商家/店铺表';

-- 商品分类表 (product_category)
CREATE TABLE product_category (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键',
    name VARCHAR(100) NOT NULL COMMENT '分类名称',
    description TEXT COMMENT '描述',
    parent_id BIGINT DEFAULT 0 COMMENT '父级ID, 支持多级分类'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商品分类表';

-- 商品表 (product)
CREATE TABLE product (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '商品ID，主键',
		merchant_id BIGINT NOT NULL COMMENT '商家id',
		saller_id BIGINT NOT NULL COMMENT '卖家id',
    category_id BIGINT NOT NULL COMMENT '分类ID',
    title VARCHAR(255) NOT NULL COMMENT '标题',
		brief VARCHAR(255) COMMENT '商品简介',
    description LONGTEXT COMMENT '富文本详情',
		sales INT DEFAULT 0 COMMENT '商品销量',
    product_status TINYINT DEFAULT 1 COMMENT '上架状态: 上架1/下架0'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商品表';


-- 商品图片表 (product_image)
CREATE TABLE product_image (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键',
    product_id BIGINT NOT NULL COMMENT '商品ID',
    url VARCHAR(255) NOT NULL COMMENT '图片URL',
    image_type TINYINT NOT NULL COMMENT '类型: 主图/副图, 0,1'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商品图片表';

-- 商品规格SKU表 (sku)
CREATE TABLE sku (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键',
    product_id BIGINT NOT NULL COMMENT '商品ID',
    specification VARCHAR(255) NOT NULL COMMENT '规格描述',
    sku_status TINYINT DEFAULT 0 COMMENT '上架状态: 上架/下架/没货, 0,1,2',
    price DECIMAL(10,2) NOT NULL COMMENT '价格',
    stock INT NOT NULL COMMENT '库存量',
		version INT NOT NULL DEFAULT 0 COMMENT '乐观锁版本号'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商品规格SKU表';

-- 农产品溯源信息表（与商品一对一）
CREATE TABLE product_traceability (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    product_id BIGINT NOT NULL UNIQUE COMMENT '关联的商品ID（与商品表一对一）',
    
    -- 种植环节
    farm_name VARCHAR(100) COMMENT '种植基地名称',
    farm_address VARCHAR(200) COMMENT '基地详细地址',
    farmer VARCHAR(50) COMMENT '负责人/农户姓名',
    farm_image_url VARCHAR(255) COMMENT '基地实景图URL（可为空）',
    
    -- 采摘环节
    harvest_date DATE COMMENT '采摘日期',
    harvester VARCHAR(100) COMMENT '采摘人员或团队',
    weather VARCHAR(20) COMMENT '采摘当日天气情况',
    harvest_image_url VARCHAR(255) COMMENT '采摘现场图URL（可为空）',
    
    -- 检测环节
    inspection_agency VARCHAR(100) COMMENT '检测机构名称',
    inspection_date DATE COMMENT '检测日期',
    is_passed TINYINT(1) DEFAULT 1 COMMENT '是否检测合格：1-合格，0-不合格',
    inspection_report_url VARCHAR(255) COMMENT '检测报告图片或PDF链接',
    
    -- 包装环节
    package_date DATE COMMENT '包装日期',
    package_spec VARCHAR(100) COMMENT '包装规格（如：5斤/礼盒、10kg/箱）',
		package_url VARCHAR(255) COMMENT '包装过程图片',
    
    -- 发货环节
    ship_date DATE COMMENT '发货日期',
    logistics VARCHAR(50) COMMENT '物流公司名称',
    
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '记录创建时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '记录最后更新时间'
) COMMENT='农产品溯源信息表';

-- 购物车表 (user_cart)
CREATE TABLE user_cart (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键',
    user_id BIGINT NOT NULL COMMENT '用户ID，关联用户表',
    sku_id BIGINT NOT NULL COMMENT '商品规格ID，关联SKU表',
    quantity INT NOT NULL DEFAULT 1 COMMENT '商品数量，默认为1',
    added_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '添加到购物车的时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_user_id (user_id) COMMENT '用户ID索引',
    INDEX idx_sku_id (sku_id) COMMENT 'SKU ID索引',
    UNIQUE KEY uk_user_sku (user_id, sku_id) COMMENT '用户和SKU的唯一组合键，防止重复添加'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户购物车表';


-- 订单表 (user_order)
CREATE TABLE user_order (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键',
		order_no VARCHAR(255) COMMENT '订单号',
    buyer_id BIGINT NOT NULL COMMENT '买家id',
    total_price DECIMAL(10,2) NOT NULL COMMENT '总价',
    order_status TINYINT DEFAULT 1 COMMENT '订单状态: 待发货1/已发货2/已完成3/已取消4',
    timeout_at TIMESTAMP COMMENT '订单超时时间',
    payment_status TINYINT DEFAULT 0 COMMENT '支付状态: 已支付1/未支付0',
    shipping_address TEXT NOT NULL COMMENT '收货地址,直接存储最终的收货地址,当用户删除自己的地址时不受影响',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单表';

-- 订单详情表 (order_detail)
CREATE TABLE order_detail (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键',
    order_id BIGINT NOT NULL COMMENT '订单ID',
		product_id BIGINT COMMENT '商品id',
		sku_id BIGINT COMMENT '规格id,用于释放库存',
		saller_id BIGINT NOT NULL COMMENT '卖家ID',
		
    -- 快照字段
    product_title VARCHAR(255) NOT NULL COMMENT '商品标题快照',
    sku_specification VARCHAR(255) NOT NULL COMMENT '规格描述快照（如 颜色:红;尺寸:L）',
    price DECIMAL(10,2) NOT NULL COMMENT '单价',
    quantity INT NOT NULL COMMENT '购买数量',
		product_main_image VARCHAR(255) COMMENT '商品主图快照URL',
		
		-- 物流相关字段
		shipped_status TINYINT DEFAULT 0 COMMENT '物流状态：0未发货，1已发货，2已签收',
		
		is_review TINYINT DEFAULT 0 COMMENT '是否评价',
		
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单详情表';


-- 物流信息表 (logistics_info)
CREATE TABLE logistics_info (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键',
		-- 用户维护信息
		tracking_number VARCHAR(50) COMMENT '快递单号',
    order_detail_id BIGINT NOT NULL COMMENT '订单项ID',
    logistics_company VARCHAR(100) COMMENT '物流公司',
		
		start_from VARCHAR(255) COMMENT '发货地点',
    current_location VARCHAR(255) COMMENT '当前位置',
		destination VARCHAR(255) COMMENT '目的地',
		shipped_at TIMESTAMP COMMENT '发货时间',
		delivered_at TIMESTAMP COMMENT '签收时间',
		
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='物流信息表';

-- 评价表 (product_review)
CREATE TABLE product_review (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键',
    user_id BIGINT NOT NULL COMMENT '评价用户',
    order_id BIGINT NOT NULL COMMENT '订单ID',
		order_detail_id BIGINT COMMENT '订单详情id',
    product_id BIGINT NOT NULL COMMENT '对商品的评价',
    content TEXT COMMENT '商品评价',
    rating TINYINT NOT NULL COMMENT '打分',
    review_images JSON COMMENT '评论图片数组，存储图片URL列表',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='评价表';


-- 收藏表 (user_favorite)
CREATE TABLE user_favorite (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键',
    user_id BIGINT NOT NULL COMMENT '用户id',
    product_id BIGINT NOT NULL COMMENT '商品id',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '收藏时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='收藏表';

-- 聊天记录表（chat_message)
CREATE TABLE `chat_message`  (
  `id` bigint(0) NOT NULL AUTO_INCREMENT COMMENT '消息ID',
  `sender_id` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '发送者ID',
  `receiver_id` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '接收者ID',
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '消息内容',
  `create_time` datetime(0) NOT NULL COMMENT '创建时间',
  `is_read` tinyint(1) NULL DEFAULT 0 COMMENT '是否已读(0:未读,1:已读)',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_sender_id`(`sender_id`) USING BTREE,
  INDEX `idx_receiver_id`(`receiver_id`) USING BTREE,
  INDEX `idx_create_time`(`create_time`) USING BTREE
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT = '聊天消息表' ;

-- 文章表 (article)
CREATE TABLE article (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '文章ID，主键',
    title VARCHAR(150) NOT NULL COMMENT '文章标题',
    summary VARCHAR(300) COMMENT '摘要/简介',
    content LONGTEXT NOT NULL COMMENT '富文本内容，支持HTML或Markdown',
    author_name VARCHAR(30) NOT NULL COMMENT '作者姓名',
    category VARCHAR(50) NOT NULL COMMENT '分类：tech/policy/market 等',
    article_status TINYINT DEFAULT 1 COMMENT '状态：0-草稿 1-已发布',
    published_at TIMESTAMP NULL COMMENT '发布时间（草稿时为NULL）',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='知识库文章表';

-- 求购主表
CREATE TABLE group_purchase_request (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    user_id BIGINT COMMENT '发起人用户ID（消费者）',
		order_id BIGINT COMMENT '订单关联id',
    title VARCHAR(100) NOT NULL COMMENT '商品名称',
    quantity INT NOT NULL COMMENT '需求数量（单位：斤）',
    max_total_price DECIMAL(10,2) NOT NULL COMMENT '最高总价预算（元）',
    region VARCHAR(50) COMMENT '收货地区（如"福建省厦门市"）',
    expire_time DATETIME NOT NULL COMMENT '报价截止时间',
    status TINYINT DEFAULT 1 COMMENT '状态：1-进行中，2-已成交，3-已过期',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
		updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) COMMENT='团购求购主表';

-- 商家报价表
CREATE TABLE group_purchase_quote (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    request_id BIGINT NOT NULL COMMENT '关联的求购请求ID',
    seller_id BIGINT NOT NULL COMMENT '报价商家用户ID',
    quoted_price DECIMAL(10,2) NOT NULL COMMENT '报价单价（元/斤）',
    delivery_desc VARCHAR(100) COMMENT '发货说明（如发货时间、物流方式等）',
		product_image VARCHAR(255) COMMENT '货品图片',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '报价创建时间',
		updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    FOREIGN KEY (request_id) REFERENCES group_purchase_request(id)
) COMMENT='商家对求购的报价记录表';

