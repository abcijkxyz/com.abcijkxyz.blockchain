/*
 Navicat Premium Data Transfer

 Source Server         : localhost_5432
 Source Server Type    : PostgreSQL
 Source Server Version : 130003
 Source Host           : localhost:5432
 Source Catalog        : account3
 Source Schema         : public

 Target Server Type    : PostgreSQL
 Target Server Version : 130003
 File Encoding         : 65001

 Date: 05/05/2022 18:56:53
*/


-- ----------------------------
-- Table structure for account
-- ----------------------------
DROP TABLE IF EXISTS "public"."account";
CREATE TABLE "public"."account"
(
    "address"    varchar(50) COLLATE "pg_catalog"."default" NOT NULL,
    "locked"     bool                                       NOT NULL DEFAULT false,
    "ecosystem"  int8,
    "memberName" varchar(255) COLLATE "pg_catalog"."default",
    "imageId"    int8,
    "memberInfo" text COLLATE "pg_catalog"."default",
    "createDate" date                                                DEFAULT now()
)
;
COMMENT ON COLUMN "public"."account"."locked" IS '默认不被锁定false，被锁定true';
COMMENT ON TABLE "public"."account" IS 'UTXO 用户账户';

-- ----------------------------
-- Records of account
-- ----------------------------
INSERT INTO "public"."account"
VALUES ('1111-1111-1111-1111-0000', 'f', NULL, NULL, NULL, NULL, '2022-04-22');

-- ----------------------------
-- Table structure for block
-- ----------------------------
DROP TABLE IF EXISTS "public"."block";
CREATE TABLE "public"."block"
(
    "hash"          varchar(64) COLLATE "pg_catalog"."default" NOT NULL,
    "prevBlockHash" varchar(64) COLLATE "pg_catalog"."default",
    "height"        int4                                       NOT NULL,
    "time"          int8,
    "data"          text COLLATE "pg_catalog"."default",
    "txNum"         int4,
    "merkleRoot"    varchar(64) COLLATE "pg_catalog"."default",
    "version"       int8
)
;
COMMENT ON TABLE "public"."block" IS 'UTXO 打包区块';

-- ----------------------------
-- Records of block
-- ----------------------------
INSERT INTO "public"."block"
VALUES ('0000000000000000000000000000000000000000000000000000000000000000',
        '0000000000000000000000000000000000000000000000000000000000000000', 0, NULL, NULL, NULL, NULL, NULL);

-- ----------------------------
-- Table structure for spent_info
-- ----------------------------
DROP TABLE IF EXISTS "public"."spent_info";
CREATE TABLE "public"."spent_info"
(
    "inputTxHash"    varchar(64) COLLATE "pg_catalog"."default",
    "inputIndex"     int2,
    "inputPublicKey" varchar(500) COLLATE "pg_catalog"."default",
    "outputTxHash"   varchar(64) COLLATE "pg_catalog"."default" NOT NULL,
    "outputIndex"    int2                                       NOT NULL,
    "outputAddress"  varchar(50) COLLATE "pg_catalog"."default" NOT NULL,
    "outputValue"    numeric(30, 0)                             NOT NULL,
    "scene"          varchar(50) COLLATE "pg_catalog"."default",
    "ecosystem"      int4,
    "contract"       varchar(100) COLLATE "pg_catalog"."default",
    "height"         int8,
    "asset"          varchar(50) COLLATE "pg_catalog"."default"
)
;
COMMENT ON COLUMN "public"."spent_info"."inputTxHash" IS '该余额输入到的交易的哈希';
COMMENT ON COLUMN "public"."spent_info"."inputIndex" IS '该余额输入到的交易的顺序';
COMMENT ON COLUMN "public"."spent_info"."outputTxHash" IS '该余额输出到的交易的哈希';
COMMENT ON COLUMN "public"."spent_info"."outputIndex" IS '该余额输出到的交易的顺序';
COMMENT ON COLUMN "public"."spent_info"."outputAddress" IS '用户钱包地址';
COMMENT ON COLUMN "public"."spent_info"."outputValue" IS '金额';
COMMENT ON COLUMN "public"."spent_info"."scene" IS '场景，utxo，nft';
COMMENT ON COLUMN "public"."spent_info"."ecosystem" IS '生态';
COMMENT ON COLUMN "public"."spent_info"."contract" IS '合约名称';
COMMENT ON COLUMN "public"."spent_info"."asset" IS '资产符号';

-- ----------------------------
-- Records of spent_info
-- ----------------------------
INSERT INTO "public"."spent_info"
VALUES (NULL, NULL, NULL, '0000000000000000000000000000000000000000000000000000000000000000', 0,
        '1111-1111-1111-1111-0000', 100000000, NULL, NULL, NULL, NULL, NULL);

-- ----------------------------
-- Table structure for transaction
-- ----------------------------
DROP TABLE IF EXISTS "public"."transaction";
CREATE TABLE "public"."transaction"
(
    "hash"      varchar(64) COLLATE "pg_catalog"."default" NOT NULL,
    "time"      int8,
    "height"    int4,
    "address"   varchar(50) COLLATE "pg_catalog"."default",
    "resultSQL" text COLLATE "pg_catalog"."default",
    "ecosystem" int8,
    "contract"  varchar(100) COLLATE "pg_catalog"."default",
    "version"   int8,
    "data"      text COLLATE "pg_catalog"."default"
)
;
COMMENT ON TABLE "public"."transaction" IS 'UTXO 打包的交易';

-- ----------------------------
-- Records of transaction
-- ----------------------------

-- ----------------------------
-- Table structure for tx_data
-- ----------------------------
DROP TABLE IF EXISTS "public"."tx_data";
CREATE TABLE "public"."tx_data"
(
    "hash"      varchar(64) COLLATE "pg_catalog"."default" NOT NULL,
    "from"      varchar(50) COLLATE "pg_catalog"."default",
    "to"        varchar(50) COLLATE "pg_catalog"."default",
    "amount"    int8,
    "timestamp" int8,
    "contract"  varchar(100) COLLATE "pg_catalog"."default",
    "data"      text COLLATE "pg_catalog"."default"
)
;
COMMENT ON TABLE "public"."tx_data" IS 'UTXO 接收交易 交易池';

-- ----------------------------
-- Records of tx_data
-- ----------------------------

-- ----------------------------
-- Uniques structure for table account
-- ----------------------------
ALTER TABLE "public"."account"
    ADD CONSTRAINT "t_account_account_key" UNIQUE ("address");

-- ----------------------------
-- Primary Key structure for table account
-- ----------------------------
ALTER TABLE "public"."account"
    ADD CONSTRAINT "account_pkey" PRIMARY KEY ("address");

-- ----------------------------
-- Primary Key structure for table block
-- ----------------------------
ALTER TABLE "public"."block"
    ADD CONSTRAINT "t_block_pkey" PRIMARY KEY ("height");

-- ----------------------------
-- Indexes structure for table spent_info
-- ----------------------------
CREATE INDEX "spent_info_outputAddress_idx" ON "public"."spent_info" USING btree (
                                                                                  "outputAddress"
                                                                                  COLLATE "pg_catalog"."default"
                                                                                  "pg_catalog"."text_ops" ASC NULLS LAST
    );

-- ----------------------------
-- Primary Key structure for table spent_info
-- ----------------------------
ALTER TABLE "public"."spent_info"
    ADD CONSTRAINT "spent_info_pkey" PRIMARY KEY ("outputTxHash", "outputAddress", "outputIndex");

-- ----------------------------
-- Primary Key structure for table transaction
-- ----------------------------
ALTER TABLE "public"."transaction"
    ADD CONSTRAINT "t_transaction_pkey" PRIMARY KEY ("hash");

-- ----------------------------
-- Primary Key structure for table tx_data
-- ----------------------------
ALTER TABLE "public"."tx_data"
    ADD CONSTRAINT "t_tx_queue_pkey" PRIMARY KEY ("hash");
