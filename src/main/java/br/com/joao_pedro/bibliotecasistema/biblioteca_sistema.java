package br.com.joao_pedro.bibliotecasistema;

import br.com.joao_pedro.bibliotecasistema.dao.LivroDAO;
import br.com.joao_pedro.bibliotecasistema.model.Livro;
import br.com.joao_pedro.bibliotecasistema.dao.UsuarioDAO;
import br.com.joao_pedro.bibliotecasistema.model.Usuario;
import br.com.joao_pedro.bibliotecasistema.dao.EmprestimoDAO;
import br.com.joao_pedro.bibliotecasistema.model.Emprestimo;
import java.util.List;
import java.util.Scanner;

public class biblioteca_sistema {
    
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        LivroDAO livroDao = new LivroDAO();
        UsuarioDAO usuarioDAO = new UsuarioDAO();
        EmprestimoDAO emprestimoDao = new EmprestimoDAO();
        
        while (true) {
            System.out.println("\n=== Sistema de Biblioteca ===");
            System.out.println("1 - Cadastrar livro");
            System.out.println("2 - Listar livros");
            System.out.println("3 - Cadastrar Usuario");
            System.out.println("4 - Listar Usuario");
            System.out.println("5 - Emprestar Livros");
            System.out.println("6 - Devolver Livros");
            System.out.println("7 - Listar empréstimos pendentes");
            System.out.println("8 - Relatório de atrasados");
            System.out.println("9 - Buscar livro");
            System.out.println("0 - Sair");
            System.out.print("Escolha: ");
            int opcao = scanner.nextInt();
            scanner.nextLine(); // limpa buffer
            
            if (opcao == 0) break;
            
            switch (opcao) {
                case 1:
                    Livro novo = new Livro();
                    System.out.print("Título: ");
                    novo.setTitulo(scanner.nextLine());
                    System.out.print("Autor: ");
                    novo.setAutor(scanner.nextLine());
                    System.out.print("ISBN (deixe vazio se não tiver): ");
                    String isbn = scanner.nextLine();
                    if (!isbn.isEmpty()) novo.setIsbn(isbn);
                    System.out.print("Ano de publicação: ");
                    novo.setAnoPublicacao(scanner.nextInt());
                    scanner.nextLine();
                    livroDao.cadastrar(novo);
                    break;
                case 2:
                    List<Livro> livros = livroDao.listarTodos();
                    if (livros.isEmpty()) {
                        System.out.println("Nenhum livro.");
                    } else {
                        for (Livro l : livros) {
                            System.out.println(l);
                        }
                    }
                    break;
                case 3:
                    Usuario novoUsuario = new Usuario();
                    System.out.print("Nome: ");
                    novoUsuario.setNome(scanner.nextLine());
                    System.out.print("Email: ");
                    novoUsuario.setEmail(scanner.nextLine());
                    System.out.print("Tipo (ALUNO ou ADMIN): ");
                    novoUsuario.setTipo(scanner.nextLine().toUpperCase());
                    usuarioDAO.cadastrar(novoUsuario);
                    break;
                case 4:
                    List<Usuario> usuarios = usuarioDAO.listarTodos();
                    if (usuarios.isEmpty()) {
                        System.out.println("Nenhum usuário cadastrado.");
                    } else {
                        for (Usuario u : usuarios) {
                            System.out.println(u);
                        }
                    }
                    break;
                case 5: 
                    System.out.print("ID do usuário: ");
                    int usuarioId = scanner.nextInt();
                    scanner.nextLine();
                    System.out.print("ID do livro: ");
                    int livroId = scanner.nextInt();
                    scanner.nextLine();

                    
                    emprestimoDao.emprestar(usuarioId, livroId);
                    break;

                case 6: 
                    System.out.print("ID do empréstimo: ");
                    int emprestimoId = scanner.nextInt();
                    scanner.nextLine();

                    emprestimoDao.devolver(emprestimoId);
                    break;

                case 7: 
                    List<Emprestimo> pendentes = emprestimoDao.listarPendentes();
                    if (pendentes.isEmpty()) {
                        System.out.println("Nenhum empréstimo pendente.");
                    } else {
                        for (Emprestimo e : pendentes) {
                            System.out.println(e);
                        }
                    }
                    break;
                case 8:
                    System.out.println("Relatório de atrasados:");
                    List<Emprestimo> atrasados = emprestimoDao.listarAtrasados();
                    if (atrasados.isEmpty()) {
                        System.out.println("Nenhum atrasado no momento.");
                    }
                    break;
                case 9:
                    System.out.print("Digite o termo para buscar (título, autor ou ISBN): ");
                    String termo = scanner.nextLine();

                    List<Livro> encontrados = livroDao.buscarPorTermo(termo);

                    if (encontrados.isEmpty()) {
                        System.out.println("Nenhum livro encontrado.");
                    } else {
                        System.out.println("Resultados da busca:");
                        for (Livro l : encontrados) {
                            System.out.println(l);
                        }
                    }
                    break;  
                default:
                    System.out.println("Opção inválida.");
            }
        }
        scanner.close();
    }
}