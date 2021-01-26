-- MySQL dump 10.13  Distrib 5.7.30, for Win64 (x86_64)
--
-- Host: localhost    Database: mind_links_core
-- ------------------------------------------------------
-- Server version	5.7.30

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `links_authorith`
--

DROP TABLE IF EXISTS `links_authorith`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `links_authorith` (
  `links_authorith_id` bigint(20) DEFAULT NULL COMMENT '主键',
  `links_role_id` bigint(20) DEFAULT NULL COMMENT '逻辑外键-关联角色',
  `authorith` varchar(100) DEFAULT NULL COMMENT '权限',
  `authorith_info` varchar(100) DEFAULT NULL COMMENT '权限信息 说明',
  `authorith_name` varchar(100) DEFAULT NULL COMMENT '权限称呼',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='用户权限表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `links_authorith`
--

LOCK TABLES `links_authorith` WRITE;
/*!40000 ALTER TABLE `links_authorith` DISABLE KEYS */;
/*!40000 ALTER TABLE `links_authorith` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `links_role`
--

DROP TABLE IF EXISTS `links_role`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `links_role` (
  `links_role_id` bigint(20) NOT NULL COMMENT '主键',
  `role` varchar(100) DEFAULT NULL COMMENT '角色',
  `role_name` varchar(100) DEFAULT NULL COMMENT '角色称呼',
  `role_indo` varchar(100) DEFAULT NULL COMMENT '角色信息 说明',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`links_role_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `links_role`
--

LOCK TABLES `links_role` WRITE;
/*!40000 ALTER TABLE `links_role` DISABLE KEYS */;
/*!40000 ALTER TABLE `links_role` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `links_user`
--

DROP TABLE IF EXISTS `links_user`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `links_user` (
  `user_id` bigint(20) NOT NULL COMMENT '用户id',
  `username` varchar(100) DEFAULT NULL COMMENT '账号',
  `password` varchar(100) DEFAULT NULL COMMENT '密码',
  `name` varchar(100) DEFAULT NULL COMMENT '用户名称',
  `avatar` varchar(100) DEFAULT NULL COMMENT '头像',
  `phone` varchar(100) DEFAULT NULL COMMENT '手机',
  `sex` varchar(100) DEFAULT NULL COMMENT '性别',
  `province_code` varchar(100) DEFAULT NULL COMMENT '行政码 省',
  `city_code` varchar(100) DEFAULT NULL COMMENT '行政码 市',
  `area_code` varchar(100) DEFAULT NULL COMMENT '行政码',
  `town_code` varchar(100) DEFAULT NULL COMMENT '行政码 镇',
  `update_user` bigint(20) DEFAULT NULL COMMENT '更新人',
  `place` varchar(100) DEFAULT NULL COMMENT '地址',
  `is_delete` tinyint(4) DEFAULT NULL COMMENT '是否已删除',
  `enable` tinyint(4) DEFAULT NULL COMMENT '是否启用',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `links_user`
--

LOCK TABLES `links_user` WRITE;
/*!40000 ALTER TABLE `links_user` DISABLE KEYS */;
INSERT INTO `links_user` VALUES (1,'admin','$2a$10$2ckrk2lG6cJRR8wttELWVuN3T2zJZR8JfVMBn6NOCMKtD.BOlLese','admin','x.png','13229005200','0','44000','44000','44000','44000123',1,'详细地址',0,1,NULL,NULL);
/*!40000 ALTER TABLE `links_user` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `links_user_role`
--

DROP TABLE IF EXISTS `links_user_role`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `links_user_role` (
  `links_user_role_id` bigint(20) NOT NULL COMMENT '主键',
  `user_id` bigint(20) DEFAULT NULL COMMENT '用户id',
  `links_role_id` bigint(20) DEFAULT NULL COMMENT '角色id',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='用户和角色关系表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `links_user_role`
--

LOCK TABLES `links_user_role` WRITE;
/*!40000 ALTER TABLE `links_user_role` DISABLE KEYS */;
/*!40000 ALTER TABLE `links_user_role` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping routines for database 'mind_links_core'
--
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2021-01-26  8:05:19
