-- phpMyAdmin SQL Dump
-- version 5.1.4deb1
-- https://www.phpmyadmin.net/
--
-- Host: localhost:3306
-- Tempo de geração: 28-Mar-2023 às 13:12
-- Versão do servidor: 8.0.32-0ubuntu0.22.10.2
-- versão do PHP: 8.1.7-1ubuntu3.3

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Banco de dados: `movesense`
--

-- --------------------------------------------------------

--
-- Estrutura da tabela `acc_table`
--

CREATE TABLE `acc_table` (
  `id` int NOT NULL,
  `id_on_phone` int NOT NULL,
  `user_id` varchar(255) NOT NULL,
  `x` varchar(25) NOT NULL,
  `y` varchar(25) NOT NULL,
  `z` varchar(25) NOT NULL,
  `timestamp` mediumint NOT NULL,
  `created_at` datetime NOT NULL,
  `uploaded_at` datetime DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- --------------------------------------------------------

--
-- Estrutura da tabela `answers`
--

CREATE TABLE `answers` (
  `id` int NOT NULL,
  `question_id` int NOT NULL,
  `user_survey_id` int NOT NULL,
  `text` varchar(255) NOT NULL,
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- --------------------------------------------------------

--
-- Estrutura da tabela `ecg_table`
--

CREATE TABLE `ecg_table` (
  `id` int NOT NULL,
  `id_on_phone` int NOT NULL,
  `user_id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `data` varchar(255) NOT NULL,
  `timestamp` int NOT NULL,
  `created_at` datetime NOT NULL,
  `uploaded_at` datetime DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- --------------------------------------------------------

--
-- Estrutura da tabela `gyro_table`
--

CREATE TABLE `gyro_table` (
  `id` int NOT NULL,
  `id_on_phone` int NOT NULL,
  `user_id` varchar(255) NOT NULL,
  `x` varchar(25) NOT NULL,
  `y` varchar(25) NOT NULL,
  `z` varchar(25) NOT NULL,
  `timestamp` int NOT NULL,
  `created_at` datetime NOT NULL,
  `uploaded_at` datetime DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- --------------------------------------------------------

--
-- Estrutura da tabela `hr_table`
--

CREATE TABLE `hr_table` (
  `id` int NOT NULL,
  `id_on_phone` int NOT NULL,
  `average` float NOT NULL,
  `rr_data` int NOT NULL,
  `user_id` varchar(255) NOT NULL,
  `created_at` datetime NOT NULL,
  `uploaded_at` datetime DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- --------------------------------------------------------

--
-- Estrutura da tabela `magn_table`
--

CREATE TABLE `magn_table` (
  `id` int NOT NULL,
  `id_on_phone` int NOT NULL,
  `user_id` varchar(255) NOT NULL,
  `x` varchar(25) NOT NULL,
  `y` varchar(25) NOT NULL,
  `z` varchar(25) NOT NULL,
  `timestamp` int NOT NULL,
  `created_at` datetime NOT NULL,
  `uploaded_at` datetime DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- --------------------------------------------------------

--
-- Estrutura da tabela `options`
--

CREATE TABLE `options` (
  `id` int NOT NULL,
  `text` text NOT NULL,
  `isLikert` tinyint(1) NOT NULL DEFAULT '0',
  `likertScale` int NOT NULL,
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- --------------------------------------------------------

--
-- Estrutura da tabela `organization`
--

CREATE TABLE `organization` (
  `id` int NOT NULL,
  `name` varchar(255) NOT NULL,
  `image_url` varchar(255) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- --------------------------------------------------------

--
-- Estrutura da tabela `questions`
--

CREATE TABLE `questions` (
  `id` int NOT NULL,
  `text` text NOT NULL,
  `question_type_id` int NOT NULL,
  `section_id` int NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- --------------------------------------------------------

--
-- Estrutura da tabela `question_options`
--

CREATE TABLE `question_options` (
  `id` int NOT NULL,
  `question_id` int NOT NULL,
  `option_id` int NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- --------------------------------------------------------

--
-- Estrutura da tabela `question_types`
--

CREATE TABLE `question_types` (
  `id` int NOT NULL,
  `name` varchar(255) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- --------------------------------------------------------

--
-- Estrutura da tabela `researchers`
--

CREATE TABLE `researchers` (
  `id` int NOT NULL,
  `user_id` varchar(255) NOT NULL,
  `study_id` int NOT NULL,
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- --------------------------------------------------------

--
-- Estrutura da tabela `sections`
--

CREATE TABLE `sections` (
  `id` int NOT NULL,
  `name` text NOT NULL,
  `survey_id` int NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- --------------------------------------------------------

--
-- Estrutura da tabela `studies`
--

CREATE TABLE `studies` (
  `id` int NOT NULL,
  `name` varchar(255) NOT NULL,
  `description` varchar(255) NOT NULL,
  `adminPassword` varchar(255) NOT NULL,
  `start_date` datetime NOT NULL,
  `end_date` datetime NOT NULL,
  `version` decimal(10,0) NOT NULL DEFAULT '1'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- --------------------------------------------------------

--
-- Estrutura da tabela `studies_organizations`
--

CREATE TABLE `studies_organizations` (
  `id` int NOT NULL,
  `study_id` int NOT NULL,
  `organization_id` int NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- --------------------------------------------------------

--
-- Estrutura da tabela `surveys`
--

CREATE TABLE `surveys` (
  `id` int NOT NULL,
  `study_id` int NOT NULL,
  `title` text NOT NULL,
  `description` text NOT NULL,
  `expected_time` int NOT NULL,
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- --------------------------------------------------------

--
-- Estrutura da tabela `temp_table`
--

CREATE TABLE `temp_table` (
  `id` int NOT NULL,
  `id_on_phone` int NOT NULL,
  `measurement` double NOT NULL,
  `timestamp` longtext NOT NULL,
  `user_id` varchar(255) NOT NULL,
  `created_at` datetime NOT NULL,
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- --------------------------------------------------------

--
-- Estrutura da tabela `users`
--

CREATE TABLE `users` (
  `id` varchar(255) NOT NULL,
  `username` varchar(255) NOT NULL,
  `password` varchar(255) NOT NULL,
  `isResearcher` tinyint(1) NOT NULL DEFAULT '0',
  `created_at` datetime NOT NULL,
  `last_login` datetime NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- --------------------------------------------------------

--
-- Estrutura da tabela `user_studies`
--

CREATE TABLE `user_studies` (
  `id` int NOT NULL,
  `user_id` varchar(255) NOT NULL,
  `study_id` int NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- --------------------------------------------------------

--
-- Estrutura da tabela `user_surveys`
--

CREATE TABLE `user_surveys` (
  `id` int NOT NULL,
  `user_id` varchar(255) NOT NULL,
  `survey_id` int NOT NULL,
  `start_time` datetime DEFAULT NULL,
  `end_time` datetime DEFAULT NULL,
  `isCompleted` tinyint(1) NOT NULL DEFAULT '0',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Índices para tabelas despejadas
--

--
-- Índices para tabela `acc_table`
--
ALTER TABLE `acc_table`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `user_id` (`user_id`,`id_on_phone`,`created_at`);

--
-- Índices para tabela `answers`
--
ALTER TABLE `answers`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `question_id_2` (`question_id`,`user_survey_id`,`created_at`),
  ADD KEY `user_survey_id` (`user_survey_id`),
  ADD KEY `question_id` (`question_id`);

--
-- Índices para tabela `ecg_table`
--
ALTER TABLE `ecg_table`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `user_id` (`user_id`,`id_on_phone`,`created_at`);

--
-- Índices para tabela `gyro_table`
--
ALTER TABLE `gyro_table`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `user_id` (`user_id`,`id_on_phone`,`created_at`);

--
-- Índices para tabela `hr_table`
--
ALTER TABLE `hr_table`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `user_id` (`user_id`,`id_on_phone`,`created_at`);

--
-- Índices para tabela `magn_table`
--
ALTER TABLE `magn_table`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `user_id` (`user_id`,`id_on_phone`,`created_at`);

--
-- Índices para tabela `options`
--
ALTER TABLE `options`
  ADD PRIMARY KEY (`id`);

--
-- Índices para tabela `organization`
--
ALTER TABLE `organization`
  ADD PRIMARY KEY (`id`);

--
-- Índices para tabela `questions`
--
ALTER TABLE `questions`
  ADD PRIMARY KEY (`id`),
  ADD KEY `question_type_id` (`question_type_id`),
  ADD KEY `section_id` (`section_id`);

--
-- Índices para tabela `question_options`
--
ALTER TABLE `question_options`
  ADD PRIMARY KEY (`id`),
  ADD KEY `question_id` (`question_id`),
  ADD KEY `option_id` (`option_id`);

--
-- Índices para tabela `question_types`
--
ALTER TABLE `question_types`
  ADD PRIMARY KEY (`id`);

--
-- Índices para tabela `researchers`
--
ALTER TABLE `researchers`
  ADD PRIMARY KEY (`id`),
  ADD KEY `user_id` (`user_id`),
  ADD KEY `study_id` (`study_id`);

--
-- Índices para tabela `sections`
--
ALTER TABLE `sections`
  ADD PRIMARY KEY (`id`),
  ADD KEY `survey_id` (`survey_id`);

--
-- Índices para tabela `studies`
--
ALTER TABLE `studies`
  ADD PRIMARY KEY (`id`);

--
-- Índices para tabela `studies_organizations`
--
ALTER TABLE `studies_organizations`
  ADD PRIMARY KEY (`id`),
  ADD KEY `study_id` (`study_id`),
  ADD KEY `organization_id` (`organization_id`);

--
-- Índices para tabela `surveys`
--
ALTER TABLE `surveys`
  ADD PRIMARY KEY (`id`),
  ADD KEY `study_id` (`study_id`);

--
-- Índices para tabela `temp_table`
--
ALTER TABLE `temp_table`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `user_id` (`user_id`,`id_on_phone`,`created_at`);

--
-- Índices para tabela `users`
--
ALTER TABLE `users`
  ADD PRIMARY KEY (`id`);

--
-- Índices para tabela `user_studies`
--
ALTER TABLE `user_studies`
  ADD PRIMARY KEY (`id`),
  ADD KEY `user_id` (`user_id`),
  ADD KEY `study_id` (`study_id`);

--
-- Índices para tabela `user_surveys`
--
ALTER TABLE `user_surveys`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `user_survey_times` (`user_id`,`survey_id`,`start_time`,`end_time`),
  ADD KEY `user_id` (`user_id`),
  ADD KEY `survey_id` (`survey_id`);

--
-- AUTO_INCREMENT de tabelas despejadas
--

--
-- AUTO_INCREMENT de tabela `acc_table`
--
ALTER TABLE `acc_table`
  MODIFY `id` int NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT de tabela `answers`
--
ALTER TABLE `answers`
  MODIFY `id` int NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT de tabela `ecg_table`
--
ALTER TABLE `ecg_table`
  MODIFY `id` int NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT de tabela `gyro_table`
--
ALTER TABLE `gyro_table`
  MODIFY `id` int NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT de tabela `hr_table`
--
ALTER TABLE `hr_table`
  MODIFY `id` int NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT de tabela `magn_table`
--
ALTER TABLE `magn_table`
  MODIFY `id` int NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT de tabela `options`
--
ALTER TABLE `options`
  MODIFY `id` int NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT de tabela `organization`
--
ALTER TABLE `organization`
  MODIFY `id` int NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT de tabela `questions`
--
ALTER TABLE `questions`
  MODIFY `id` int NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT de tabela `question_options`
--
ALTER TABLE `question_options`
  MODIFY `id` int NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT de tabela `question_types`
--
ALTER TABLE `question_types`
  MODIFY `id` int NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT de tabela `researchers`
--
ALTER TABLE `researchers`
  MODIFY `id` int NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT de tabela `sections`
--
ALTER TABLE `sections`
  MODIFY `id` int NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT de tabela `studies`
--
ALTER TABLE `studies`
  MODIFY `id` int NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT de tabela `studies_organizations`
--
ALTER TABLE `studies_organizations`
  MODIFY `id` int NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT de tabela `surveys`
--
ALTER TABLE `surveys`
  MODIFY `id` int NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT de tabela `temp_table`
--
ALTER TABLE `temp_table`
  MODIFY `id` int NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT de tabela `user_studies`
--
ALTER TABLE `user_studies`
  MODIFY `id` int NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT de tabela `user_surveys`
--
ALTER TABLE `user_surveys`
  MODIFY `id` int NOT NULL AUTO_INCREMENT;

--
-- Restrições para despejos de tabelas
--

--
-- Limitadores para a tabela `acc_table`
--
ALTER TABLE `acc_table`
  ADD CONSTRAINT `acc_table_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`);

--
-- Limitadores para a tabela `answers`
--
ALTER TABLE `answers`
  ADD CONSTRAINT `answers_ibfk_1` FOREIGN KEY (`user_survey_id`) REFERENCES `user_surveys` (`id`),
  ADD CONSTRAINT `answers_ibfk_2` FOREIGN KEY (`question_id`) REFERENCES `questions` (`id`);

--
-- Limitadores para a tabela `ecg_table`
--
ALTER TABLE `ecg_table`
  ADD CONSTRAINT `ecg_table_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`);

--
-- Limitadores para a tabela `gyro_table`
--
ALTER TABLE `gyro_table`
  ADD CONSTRAINT `gyro_table_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`);

--
-- Limitadores para a tabela `hr_table`
--
ALTER TABLE `hr_table`
  ADD CONSTRAINT `hr_table_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`);

--
-- Limitadores para a tabela `magn_table`
--
ALTER TABLE `magn_table`
  ADD CONSTRAINT `magn_table_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`);

--
-- Limitadores para a tabela `questions`
--
ALTER TABLE `questions`
  ADD CONSTRAINT `questions_ibfk_1` FOREIGN KEY (`question_type_id`) REFERENCES `question_types` (`id`),
  ADD CONSTRAINT `questions_ibfk_2` FOREIGN KEY (`section_id`) REFERENCES `sections` (`id`);

--
-- Limitadores para a tabela `question_options`
--
ALTER TABLE `question_options`
  ADD CONSTRAINT `question_options_ibfk_1` FOREIGN KEY (`question_id`) REFERENCES `questions` (`id`),
  ADD CONSTRAINT `question_options_ibfk_2` FOREIGN KEY (`option_id`) REFERENCES `options` (`id`);

--
-- Limitadores para a tabela `researchers`
--
ALTER TABLE `researchers`
  ADD CONSTRAINT `researchers_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`),
  ADD CONSTRAINT `researchers_ibfk_2` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`),
  ADD CONSTRAINT `researchers_ibfk_3` FOREIGN KEY (`study_id`) REFERENCES `studies` (`id`);

--
-- Limitadores para a tabela `sections`
--
ALTER TABLE `sections`
  ADD CONSTRAINT `sections_ibfk_1` FOREIGN KEY (`survey_id`) REFERENCES `surveys` (`id`);

--
-- Limitadores para a tabela `studies_organizations`
--
ALTER TABLE `studies_organizations`
  ADD CONSTRAINT `studies_organizations_ibfk_1` FOREIGN KEY (`study_id`) REFERENCES `studies` (`id`),
  ADD CONSTRAINT `studies_organizations_ibfk_2` FOREIGN KEY (`organization_id`) REFERENCES `organization` (`id`),
  ADD CONSTRAINT `studies_organizations_ibfk_3` FOREIGN KEY (`organization_id`) REFERENCES `organization` (`id`),
  ADD CONSTRAINT `studies_organizations_ibfk_4` FOREIGN KEY (`organization_id`) REFERENCES `organization` (`id`);

--
-- Limitadores para a tabela `surveys`
--
ALTER TABLE `surveys`
  ADD CONSTRAINT `surveys_ibfk_1` FOREIGN KEY (`study_id`) REFERENCES `studies` (`id`);

--
-- Limitadores para a tabela `temp_table`
--
ALTER TABLE `temp_table`
  ADD CONSTRAINT `temp_table_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`);

--
-- Limitadores para a tabela `user_studies`
--
ALTER TABLE `user_studies`
  ADD CONSTRAINT `user_studies_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`),
  ADD CONSTRAINT `user_studies_ibfk_2` FOREIGN KEY (`study_id`) REFERENCES `studies` (`id`),
  ADD CONSTRAINT `user_studies_ibfk_3` FOREIGN KEY (`study_id`) REFERENCES `studies` (`id`);

--
-- Limitadores para a tabela `user_surveys`
--
ALTER TABLE `user_surveys`
  ADD CONSTRAINT `user_surveys_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`),
  ADD CONSTRAINT `user_surveys_ibfk_2` FOREIGN KEY (`survey_id`) REFERENCES `surveys` (`id`);
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
