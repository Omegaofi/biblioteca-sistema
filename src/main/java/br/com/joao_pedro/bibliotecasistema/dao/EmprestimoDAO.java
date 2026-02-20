package br.com.joao_pedro.bibliotecasistema.dao;

import br.com.joao_pedro.bibliotecasistema.model.Emprestimo;
import br.com.joao_pedro.bibliotecasistema.model.Livro;
import br.com.joao_pedro.bibliotecasistema.model.Usuario;
import br.com.joao_pedro.bibliotecasistema.ConnectionFactory;

import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class EmprestimoDAO {

    private final LivroDAO livroDao = new LivroDAO();
    private final UsuarioDAO usuarioDao = new UsuarioDAO();

    public void emprestar(int usuarioId, int livroId) {
        // Verifica usuário
        List<Usuario> usuarios = usuarioDao.listarTodos();
        boolean usuarioExiste = usuarios.stream().anyMatch(u -> u.getId() == usuarioId);
        if (!usuarioExiste) {
            System.out.println("Usuário não encontrado.");
            return;
        }
        int ativos = contarEmprestimosAtivos(usuarioId);
        if (ativos >= 3) {
            System.out.println("Usuário já tem 3 empréstimos ativos. Limite atingido.");
            return;


        // resto do código igual (verifica usuário, livro, disponibilidade, insert, etc.)
    }

        // Verifica livro
        List<Livro> livros = livroDao.listarTodos();
        Livro livro = livros.stream()
                .filter(l -> l.getId() == livroId)
                .findFirst()
                .orElse(null);
        if (livro == null) {
            System.out.println("Livro não encontrado.");
            return;
        }
        if (!livro.isDisponivel()) {
            System.out.println("Livro indisponível para empréstimo.");
            return;
        }

        String sql = "INSERT INTO emprestimos (usuario_id, livro_id, data_emprestimo, data_devolucao_prevista) VALUES (?, ?, ?, ?)";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            LocalDate hoje = LocalDate.now();
            LocalDate prevista = hoje.plusDays(14);

            stmt.setInt(1, usuarioId);
            stmt.setInt(2, livroId);
            stmt.setDate(3, Date.valueOf(hoje));
            stmt.setDate(4, Date.valueOf(prevista));

            stmt.executeUpdate();

            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                System.out.println("Empréstimo realizado com ID: " + rs.getInt(1));
            }

            // Atualiza livro para indisponível
            livro.setDisponivel(false);
            atualizarDisponibilidadeLivro(livroId, false);

        } catch (SQLException e) {
            System.out.println("Erro ao emprestar: " + e.getMessage());
        }
    }

    private void atualizarDisponibilidadeLivro(int livroId, boolean disponivel) {
        String sql = "UPDATE livros SET disponivel = ? WHERE id = ?";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setBoolean(1, disponivel);
            stmt.setInt(2, livroId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public List<Emprestimo> listarAtrasados() {
        List<Emprestimo> atrasados = new ArrayList<>();
        String sql = "SELECT e.*, u.nome AS nome_usuario, l.titulo AS titulo_livro, DATEDIFF(CURDATE(), e.data_devolucao_prevista) AS dias_atraso " +
                     "FROM emprestimos e " +
                     "INNER JOIN usuarios u ON e.usuario_id = u.id " +
                     "INNER JOIN livros l ON e.livro_id = l.id " +
                     "WHERE e.data_devolucao_real IS NULL AND CURDATE() > e.data_devolucao_prevista";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Emprestimo e = new Emprestimo();
                e.setId(rs.getInt("id"));
                e.setUsuarioId(rs.getInt("usuario_id"));
                e.setLivroId(rs.getInt("livro_id"));
                e.setDataEmprestimo(rs.getDate("data_emprestimo"));
                e.setDataDevolucaoPrevista(rs.getDate("data_devolucao_prevista"));
                e.setDataDevolucaoReal(rs.getDate("data_devolucao_real"));

                // Pra exibir bonito no console
                System.out.println("Empréstimo atrasado ID: " + e.getId());
                System.out.println("Usuário: " + rs.getString("nome_usuario"));
                System.out.println("Livro: " + rs.getString("titulo_livro"));
                System.out.println("Dias de atraso: " + rs.getInt("dias_atraso"));
                System.out.println("-------------------");

                atrasados.add(e);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return atrasados;
    }
    public void devolver(int emprestimoId) {
        // Primeiro busca o livro_id do empréstimo pra depois marcar como disponível
        String sqlBusca = "SELECT livro_id FROM emprestimos WHERE id = ?";
        int livroId = -1;

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sqlBusca)) {

            stmt.setInt(1, emprestimoId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                livroId = rs.getInt("livro_id");
            } else {
                System.out.println("Empréstimo não encontrado.");
                return;
            }
        } catch (SQLException e) {
            System.out.println("Erro ao buscar empréstimo: " + e.getMessage());
            return;
        }

    // Marca o livro como disponível
        if (livroId > 0) {
            atualizarDisponibilidadeLivro(livroId, true);
        }

        // Exclui o empréstimo
        String sqlDelete = "DELETE FROM emprestimos WHERE id = ?";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sqlDelete)) {

            stmt.setInt(1, emprestimoId);
            int rows = stmt.executeUpdate();

            if (rows > 0) {
                System.out.println("Devolução registrada e empréstimo excluído com sucesso.");
            } else {
                System.out.println("Empréstimo não encontrado para exclusão.");
            }

    } catch (SQLException e) {
        System.out.println("Erro ao excluir empréstimo: " + e.getMessage());
    }
}

    private int buscarLivroIdPorEmprestimo(int emprestimoId) {
        String sql = "SELECT livro_id FROM emprestimos WHERE id = ?";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, emprestimoId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("livro_id");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }
    public int contarEmprestimosAtivos(int usuarioId) {
        String sql = "SELECT COUNT(*) FROM emprestimos WHERE usuario_id = ? AND data_devolucao_real IS NULL";
        int count = 0;

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, usuarioId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                count = rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return count;
    }
    // Listar empréstimos pendentes (sem devolução real)
    public List<Emprestimo> listarPendentes() {
        List<Emprestimo> emprestimos = new ArrayList<>();
        String sql = "SELECT * FROM emprestimos WHERE data_devolucao_real IS NULL";

        try (Connection conn = ConnectionFactory.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Emprestimo e = new Emprestimo();
                e.setId(rs.getInt("id"));
                e.setUsuarioId(rs.getInt("usuario_id"));
                e.setLivroId(rs.getInt("livro_id"));
                e.setDataEmprestimo(rs.getDate("data_emprestimo"));
                e.setDataDevolucaoPrevista(rs.getDate("data_devolucao_prevista"));
                e.setDataDevolucaoReal(rs.getDate("data_devolucao_real"));
                emprestimos.add(e);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return emprestimos;
    }
}