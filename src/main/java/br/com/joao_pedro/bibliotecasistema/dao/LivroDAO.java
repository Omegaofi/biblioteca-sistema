package br.com.joao_pedro.bibliotecasistema.dao;

import br.com.joao_pedro.bibliotecasistema.model.Livro;
import br.com.joao_pedro.bibliotecasistema.ConnectionFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class LivroDAO {

    // Cadastrar novo livro
   public void cadastrar(Livro livro) {
    String sql = "INSERT INTO livros (titulo, autor, isbn, ano_publicacao, disponivel) VALUES (?, ?, ?, ?, ?)";
    try (Connection conn = ConnectionFactory.getConnection();
         PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

        stmt.setString(1, livro.getTitulo());
        stmt.setString(2, livro.getAutor());
        stmt.setString(3, livro.getIsbn());
        stmt.setInt(4, livro.getAnoPublicacao());
        stmt.setBoolean(5, livro.isDisponivel());

        stmt.executeUpdate();

        ResultSet rs = stmt.getGeneratedKeys();
        if (rs.next()) {
            livro.setId(rs.getInt(1));
        }

        System.out.println("Livro cadastrado com ID: " + livro.getId());
    } catch (SQLException e) {
    if (e.getMessage().contains("Duplicate entry")) {
        System.out.println("Erro: ISBN já existe no banco. Tente outro.");
    } else {
        System.out.println("Erro ao cadastrar livro: " + e.getMessage());
    }
}
}
   public List<Livro> buscarPorTermo(String termo) {
        List<Livro> resultados = new ArrayList<>();
        String sql = "SELECT * FROM livros WHERE titulo LIKE ? OR autor LIKE ? OR isbn LIKE ?";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            String busca = "%" + termo + "%";
            stmt.setString(1, busca);
            stmt.setString(2, busca);
            stmt.setString(3, busca);

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Livro l = new Livro();
                l.setId(rs.getInt("id"));
                l.setTitulo(rs.getString("titulo"));
                l.setAutor(rs.getString("autor"));
                l.setIsbn(rs.getString("isbn"));
                l.setAnoPublicacao(rs.getInt("ano_publicacao"));
                l.setDisponivel(rs.getBoolean("disponivel"));
                resultados.add(l);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return resultados;
    }
    // Listar todos os livros
    public List<Livro> listarTodos() {
        List<Livro> livros = new ArrayList<>();
        String sql = "SELECT * FROM livros";

        try (Connection conn = ConnectionFactory.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Livro livro = new Livro();
                livro.setId(rs.getInt("id"));
                livro.setTitulo(rs.getString("titulo"));
                livro.setAutor(rs.getString("autor"));
                livro.setIsbn(rs.getString("isbn"));
                livro.setAnoPublicacao(rs.getInt("ano_publicacao"));
                livro.setDisponivel(rs.getBoolean("disponivel"));

                livros.add(livro);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return livros;
    }

    // (depois a gente adiciona buscarPorId, atualizar, deletar se quiser)
}