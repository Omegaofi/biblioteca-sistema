# Sistema de Gerenciamento de Biblioteca

Aplicação console em Java para controle de biblioteca: cadastro de livros e usuários, empréstimos, devoluções, limite de empréstimos por usuário, busca e relatório de atrasados.

Projeto feito para aprendizado e portfólio, com foco em programação orientada a objetos, JDBC e banco relacional.

## Tecnologias usadas

- Java 17+
- MySQL (banco de dados)
- JDBC (conexão com banco)
- Maven (gerenciamento de dependências)
- Git + GitHub

## Funcionalidades

- Cadastro e listagem de livros (com ISBN único)
- Cadastro e listagem de usuários (ALUNO ou ADMIN)
- Empréstimo de livro com validações:
  - Livro existe e está disponível
  - Usuário existe
  - Limite máximo de 3 livros por usuário
- Devolução com exclusão do registro de empréstimo e liberação do livro
- Busca de livros por título, autor ou ISBN
- Relatório de livros atrasados (com nome do usuário, título do livro e dias de atraso)

## Como rodar

1. Clone o repositório: https://github.com/Omegaofi/biblioteca-sistema
2. Abra o projeto no NetBeans (ou outra IDE com suporte a Maven).

3. Instale e configure o MySQL:
	- Crie o banco de dados (rode o script abaixo ou use Workbench).
	- Usuário padrão: root, sem senha (ou ajuste em ConnectionFactory.java).

4. Crie o banco e as tabelas (cole no MySQL):

create database biblioteca_db;
use biblioteca_db;
SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Banco de dados: `biblioteca_db`
--

-- --------------------------------------------------------

--
-- Estrutura para tabela `emprestimos`
--

CREATE TABLE `emprestimos` (
  `id` int(11) NOT NULL,
  `usuario_id` int(11) NOT NULL,
  `livro_id` int(11) NOT NULL,
  `data_emprestimo` date NOT NULL,
  `data_devolucao_prevista` date NOT NULL,
  `data_devolucao_real` date DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Despejando dados para a tabela `emprestimos`
--

INSERT INTO `emprestimos` (`id`, `usuario_id`, `livro_id`, `data_emprestimo`, `data_devolucao_prevista`, `data_devolucao_real`) VALUES
(4, 1, 1, '2026-02-19', '2026-03-05', NULL),
(5, 1, 2, '2026-02-19', '2026-03-05', NULL);

-- --------------------------------------------------------

--
-- Estrutura para tabela `livros`
--

CREATE TABLE `livros` (
  `id` int(11) NOT NULL,
  `titulo` varchar(200) NOT NULL,
  `autor` varchar(100) NOT NULL,
  `isbn` varchar(20) DEFAULT NULL,
  `ano_publicacao` int(11) DEFAULT NULL,
  `disponivel` tinyint(1) DEFAULT 1
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Despejando dados para a tabela `livros`
--

INSERT INTO `livros` (`id`, `titulo`, `autor`, `isbn`, `ano_publicacao`, `disponivel`) VALUES
(1, 'Jornada do leitor', 'Daniel Lopez', '9788595073432', 23, 0),
(2, 'O Guia do Mochileiro das Gal xias', 'Douglas Adams', '8530601491', 1979, 0);

-- --------------------------------------------------------

--
-- Estrutura para tabela `usuarios`
--

CREATE TABLE `usuarios` (
  `id` int(11) NOT NULL,
  `nome` varchar(100) NOT NULL,
  `email` varchar(100) NOT NULL,
  `tipo` enum('ALUNO','ADMIN') DEFAULT 'ALUNO'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Despejando dados para a tabela `usuarios`
--

INSERT INTO `usuarios` (`id`, `nome`, `email`, `tipo`) VALUES
(1, 'Jo o', 'shatkip.01@gmail.com', 'ADMIN');

--
-- Índices para tabelas despejadas
--

--
-- Índices de tabela `emprestimos`
--
ALTER TABLE `emprestimos`
  ADD PRIMARY KEY (`id`),
  ADD KEY `usuario_id` (`usuario_id`),
  ADD KEY `emprestimos_ibfk_2` (`livro_id`);

--
-- Índices de tabela `livros`
--
ALTER TABLE `livros`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `isbn` (`isbn`);

--
-- Índices de tabela `usuarios`
--
ALTER TABLE `usuarios`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `email` (`email`);

--
-- AUTO_INCREMENT para tabelas despejadas
--

--
-- AUTO_INCREMENT de tabela `emprestimos`
--
ALTER TABLE `emprestimos`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=6;

--
-- AUTO_INCREMENT de tabela `livros`
--
ALTER TABLE `livros`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=3;

--
-- AUTO_INCREMENT de tabela `usuarios`
--
ALTER TABLE `usuarios`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=2;

--
-- Restrições para tabelas despejadas
--

--
-- Restrições para tabelas `emprestimos`
--
ALTER TABLE `emprestimos`
  ADD CONSTRAINT `emprestimos_ibfk_1` FOREIGN KEY (`usuario_id`) REFERENCES `usuarios` (`id`),
  ADD CONSTRAINT `emprestimos_ibfk_2` FOREIGN KEY (`livro_id`) REFERENCES `livros` (`id`) ON DELETE CASCADE;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;

5. Rode o projeto:
	mvn clean compile exec:java
	Ou clique em Run no NetBeans.
6. Use o menu console:
	1 a 4: livros e usuários
	5 a 7: empréstimo, devolução, pendentes
	8: atrasados
	9: busca de livros
	0: sair
<image-card alt="Print do menu do sistema" src="https://github.com/Omegaofi/biblioteca-sistema/blob/master/image.png" ></image-card>


7. Estrutura do projeto: 
	biblioteca-sistema/
	├── src/
	│   ├── main/
	│   │   ├── java/
	│   │   │   ├── br.com.joao_pedro.bibliotecasistema/
	│   │   │   │   ├── dao/          → DAOs (acesso ao banco)
	│   │   │   │   ├── model/        → Classes Livro, Usuario, Emprestimo
	│   │   │   │   └── BibliotecaSistema.java  → main/menu e ConnectionFactory
	│   └── resources/
	└── pom.xml
	└── create_database.sql (script pra criar tabelas)

Melhorias futuras (planejadas)

	Login simples (admin vs aluno)
	Histórico de empréstimos (sem excluir ao devolver)
	Exportar relatórios pra CSV
	Interface gráfica (Swing ou JavaFX)

Contato
Feito por Omega/Shatkip (João Pedro)
Email: jpcarvalhotheis@gmail.com
LinkedIn: [se tiver, coloque]
Feito com dedicação pra mostrar o que consigo fazer em Java + banco de dados. >:)

