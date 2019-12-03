-- MySQL dump 10.13  Distrib 8.0.17, for Win64 (x86_64)
--
-- Host: localhost    Database: grampus_db
-- ------------------------------------------------------
-- Server version	8.0.17

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `activation_code`
--

DROP TABLE IF EXISTS `activation_code`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `activation_code` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `activate` bit(1) NOT NULL,
  `user_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=17 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `activation_code`
--

LOCK TABLES `activation_code` WRITE;
/*!40000 ALTER TABLE `activation_code` DISABLE KEYS */;
INSERT INTO `activation_code` VALUES (1,_binary '\0',1),(2,_binary '',2),(3,_binary '',3),(4,_binary '',4),(5,_binary '',5),(6,_binary '',6),(7,_binary '',7),(8,_binary '',8),(9,_binary '',9),(10,_binary '\0',10),(11,_binary '\0',11),(12,_binary '\0',12),(13,_binary '',13),(14,_binary '',14),(15,_binary '\0',15),(16,_binary '\0',16);
/*!40000 ALTER TABLE `activation_code` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `profiles`
--

DROP TABLE IF EXISTS `profiles`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `profiles` (
  `id` bigint(20) NOT NULL,
  `dislikes` bigint(20) DEFAULT NULL,
  `information` varchar(255) DEFAULT NULL,
  `likes` bigint(20) DEFAULT NULL,
  `profile_picture` varchar(255) DEFAULT NULL,
  `skills` varchar(255) DEFAULT NULL,
  `user_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK410q61iev7klncmpqfuo85ivh` (`user_id`),
  CONSTRAINT `FK410q61iev7klncmpqfuo85ivh` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `profiles`
--

LOCK TABLES `profiles` WRITE;
/*!40000 ALTER TABLE `profiles` DISABLE KEYS */;
INSERT INTO `profiles` VALUES (1,0,NULL,0,NULL,NULL,1),(2,0,NULL,0,NULL,NULL,2),(3,0,NULL,0,NULL,NULL,3),(4,0,NULL,0,NULL,NULL,4),(5,0,NULL,0,NULL,NULL,5),(6,0,NULL,0,NULL,NULL,6),(7,0,NULL,0,NULL,NULL,7),(8,0,NULL,0,'ftp://10.11.1.155/img/8.png',NULL,8),(9,0,NULL,0,'ftp://10.11.1.155/img/9.png',NULL,9),(10,0,NULL,0,NULL,NULL,10),(11,0,NULL,0,NULL,NULL,11),(12,0,NULL,0,NULL,NULL,12),(13,0,NULL,0,NULL,NULL,13),(14,0,NULL,0,'ftp://10.11.1.155/img/14.png',NULL,14),(15,0,NULL,0,NULL,NULL,15),(16,0,NULL,0,NULL,NULL,16);
/*!40000 ALTER TABLE `profiles` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `ratings`
--

DROP TABLE IF EXISTS `ratings`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `ratings` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `rating_source_username` varchar(255) DEFAULT NULL,
  `rating_type` varchar(255) DEFAULT NULL,
  `profile_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK43tps97riqqvehydac75rbosy` (`profile_id`),
  CONSTRAINT `FK43tps97riqqvehydac75rbosy` FOREIGN KEY (`profile_id`) REFERENCES `profiles` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `ratings`
--

LOCK TABLES `ratings` WRITE;
/*!40000 ALTER TABLE `ratings` DISABLE KEYS */;
INSERT INTO `ratings` VALUES (1,'777777@gmail.com','smart_mind',8),(2,'111111@gmail.com','smart_mind',8),(3,'222222@gmail.com','smart_mind',8),(4,'333333@gmail.com','smart_mind',8),(5,'444444@gmail.com','smart_mind',8),(6,'555555@gmail.com','smart_mind',8);
/*!40000 ALTER TABLE `ratings` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `users`
--

DROP TABLE IF EXISTS `users`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `users` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `email` varchar(255) DEFAULT NULL,
  `full_name` varchar(255) DEFAULT NULL,
  `job_title` varchar(255) DEFAULT NULL,
  `password` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_6dotkott2kjsp8vw4d0m25fb7` (`email`)
) ENGINE=InnoDB AUTO_INCREMENT=17 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `users`
--

LOCK TABLES `users` WRITE;
/*!40000 ALTER TABLE `users` DISABLE KEYS */;
INSERT INTO `users` VALUES (1,'aaa@gmail.com','aaa@gmail.com','QA','$2a$10$V3VIfzsGotzxuDquzgmmd.PQtA7j1epxU2s3wJ4YF02R.nO773I9u'),(2,'123@gmail.com','aaa@gmail.com','architect','$2a$10$vo3jANG9u3gTRS9ImLQlMONCA4G2t75RfArkwaI.LRBmu/Cxbhirm'),(3,'111111@gmail.com','One','PM','$2a$10$J/y2A5K9UEmCvOmqDuQzPeJeuxsZOG3ES.wkeE80/wER4o6kQ3eEu'),(4,'222222@gmail.com','Two','sys admin','$2a$10$ytKQ6te.AEHO8eg26DUPbuuOoXyhXpUqdTe7QAqu.dnqzyqmeJs4u'),(5,'333333@gmail.com','Three','architect','$2a$10$uVEKZ8XexCSVYkIEVTArD.AQoS.RMaz3YFML4H7mWOcoQ4eh0HQma'),(6,'444444@gmail.com','Four','PM','$2a$10$0OwkoVUO2t2Or/KFKhnuCu4/xXsTwnzLzV4yp/HtMKnojUpjL.x1a'),(7,'555555@gmail.com','Five','owner','$2a$10$DvmDNFNx1JprkOQnb4n6tuKS50m7BvC6F6iO6noamZJtHgj0plp42'),(8,'666666@gmail.com','Six','CFO','$2a$10$qoA2.YeDLnqVCu0yGZ1y/eXWO7FDi1ORhaNu1KIaxsN5tEIkgsUBW'),(9,'777777@gmail.com','Seven','HR','$2a$10$0cWXmEWsIFz8RjUMpmbESeeHl38jnL1xaxn06e15Uxel2qJ2MLrme'),(10,'222@gmail.com','bbb@gmail.com','PM','$2a$10$FQ2Ag3XDa0U3uvNkH3afjeMqqLCL5hlcihN1UWsk71i.qVM2PbFc6'),(11,'333@gmail.com','333@gmail.com','architect','$2a$10$VGDw7ocZxgB6aQfx9isgVOgZdgyWr.YWJ20kD272c3u0I/P2uB.DG'),(12,'tural.trudo@gmail.com','Tommy','owner','$2a$10$TT0Yj9wJP6CV3wDXTnDcnu.MuB6cPf8FTtVDAOm.vzFdyJxL6SDJm'),(13,'444@gmail.com','444@gmail.com','QA','$2a$10$NbN3NdGcJevgm7L69WVCyusSfZHt3lhGACjYJgyJpsRE4Vxj/2Sgu'),(14,'888888@gmail.com','Eight','developer','$2a$10$Gjs0OhGBV0mL5HPm95pnD.hAiDakrZsx6Rt76GEnX8qnrF7rlG7/u'),(15,'aaa1@gmail.com','alena','PM','$2a$10$iL0tu0itLWhD/.CdTJnjMONE2zh63J5R4ECJsTw2Xqc6jmlk/PTGe'),(16,'hjdfkjhsd@gmail.com','Name','HR','$2a$10$IgJuQMASEpGlGn8MfiIkBOHOTRlCU0J1ePiP7sNRj8sFQ8j1c4B2O');
/*!40000 ALTER TABLE `users` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2019-11-28 16:22:07
