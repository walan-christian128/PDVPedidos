package DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.naming.NamingException;

import Conexao.ConectionDataBases;
import Model.Empresa;
import Model.HorarioFuncionamento;


public class EmpresaDAO {
	
	private Connection con;
	 private ConectionDataBases connectionFactory;

   public EmpresaDAO(String dataBaseNames) throws ClassNotFoundException, NamingException {
   	 this.connectionFactory = new ConectionDataBases(dataBaseNames);
	        try {
	            this.con = connectionFactory.getConectionDataBases();
	        } catch (SQLException e) {
	            e.printStackTrace(); // Trate a exceção conforme necessário
	        }
   }
	
	
	 public Empresa retornCompany(int codigo) throws SQLException {
	        String sql = "select e.id,e.nome, e.cnpj, e.endereco, e.logo " // Consulta apenas os dados da empresa
	                   + "from tb_empresa e "
	                   + "where e.id = ?";

	        Empresa empresa = null;

	        try (PreparedStatement stmt = con.prepareStatement(sql)) {
	            stmt.setInt(1, codigo);
	            try (ResultSet rs = stmt.executeQuery()) {
	                if (rs.next()) { // Não precisa de while se espera apenas 1 empresa
	                    empresa = new Empresa();
	                    empresa.setId(rs.getInt("id"));
	                    empresa.setNome(rs.getString("nome"));
	                    empresa.setCnpj(rs.getString("cnpj"));
	                    empresa.setEndereco(rs.getString("endereco"));
	                    empresa.setLogo(rs.getBytes("logo"));
	                }
	            }
	        }
	        return empresa;
	    }

	    // NOVO MÉTODO para retornar os horários de uma empresa específica
	    public List<HorarioFuncionamento> retornarHorariosPorEmpresa(int idEmpresa) throws SQLException {
	        String sql = "select h.dia_semana, h.hora_abertura, h.hora_fechamento, h.aberto, h.observacao "
	                   + "from tb_horarios_funcionamento h "
	                   + "where h.id_empresa = ?";

	        List<HorarioFuncionamento> horarios = new ArrayList<>();

	        try (PreparedStatement stmt = con.prepareStatement(sql)) {
	            stmt.setInt(1, idEmpresa);
	            try (ResultSet rs = stmt.executeQuery()) {
	                while (rs.next()) {
	                    HorarioFuncionamento hf = new HorarioFuncionamento(
	                        rs.getInt("dia_semana"),
	                        rs.getString("hora_abertura"),
	                        rs.getString("hora_fechamento"),
	                        rs.getBoolean("aberto"),
	                        rs.getString("observacao")
	                    );
	                    horarios.add(hf);
	                }
	            }
	        }
	        return horarios;
	    }
	    public void atualizarEmpresa(Empresa empresa) throws SQLException {
	        String sql = "UPDATE tb_empresa SET nome = ?, cnpj = ?, endereco = ?, logo = ? WHERE id = ?";

	        try (PreparedStatement stmt = con.prepareStatement(sql)) {
	            stmt.setString(1, empresa.getNome());
	            stmt.setString(2, empresa.getCnpj());
	            stmt.setString(3, empresa.getEndereco());
	            stmt.setBytes(4, empresa.getLogo()); // Se a logo for um byte array
	            stmt.setInt(5, empresa.getId()); // O ID é crucial para a atualização

	            stmt.executeUpdate();
	            System.out.println("Empresa com ID " + empresa.getId() + " atualizada com sucesso.");

	        } catch (SQLException e) {
	            System.err.println("Erro ao atualizar empresa: " + e.getMessage());
	            throw e; // Relança a exceção para que o chamador possa tratá-la
	        }
	    }

	    /**
	     * Verifica se um registro de horário de funcionamento existe para uma dada empresa e dia da semana.
	     *
	     * @param idEmpresa O ID da empresa.
	     * @param diaSemana O dia da semana (0-6).
	     * @return true se o registro existe, false caso contrário.
	     * @throws SQLException Se ocorrer um erro de banco de dados.
	     */
	    private boolean horarioExiste(int idEmpresa, int diaSemana) throws SQLException {
	        String sql = "SELECT COUNT(*) FROM tb_horarios_funcionamento WHERE id_empresa = ? AND dia_semana = ?";
	        try (PreparedStatement stmt = con.prepareStatement(sql)) {
	            stmt.setInt(1, idEmpresa);
	            stmt.setInt(2, diaSemana);
	            try (ResultSet rs = stmt.executeQuery()) {
	                if (rs.next()) {
	                    int count = rs.getInt(1);
	                    System.out.println("DEBUG (horarioExiste): Empresa ID " + idEmpresa + ", Dia " + diaSemana + " -> Count: " + count);
	                    return count > 0;
	                }
	            }
	        }
	        System.out.println("DEBUG (horarioExiste): Nenhum resultado para Empresa ID " + idEmpresa + ", Dia " + diaSemana);
	        return false;
	    }

	    /**
	     * Atualiza ou insere os horários de funcionamento de uma empresa específica.
	     * Para cada horário na lista, verifica se já existe um registro para aquele dia.
	     * Se existir, atualiza; caso contrário, insere.
	     *
	     * @param idEmpresa O ID da empresa cujos horários serão atualizados/inseridos.
	     * @param horarios A lista de HorarioFuncionamento a serem processados.
	     * @throws SQLException Se ocorrer um erro de banco de dados.
	     */
	    public void atualizarHorariosFuncionamento(int idEmpresa, List<HorarioFuncionamento> horarios) throws SQLException {
	        con.setAutoCommit(false);
	        try {
	            String insertSql = "INSERT INTO tb_horarios_funcionamento (id_empresa, dia_semana, hora_abertura, hora_fechamento, aberto, observacao) VALUES (?, ?, ?, ?, ?, ?)";
	            String updateSql = "UPDATE tb_horarios_funcionamento SET hora_abertura = ?, hora_fechamento = ?, aberto = ?, observacao = ? WHERE id_empresa = ? AND dia_semana = ?";

	            int insertedCount = 0;
	            int updatedCount = 0;

	            for (HorarioFuncionamento hf : horarios) {
	                if (horarioExiste(idEmpresa, hf.getDiaSemana())) {
	                    try (PreparedStatement updateStmt = con.prepareStatement(updateSql)) {
	                        updateStmt.setString(1, hf.getHoraAbertura());
	                        updateStmt.setString(2, hf.getHoraFechamento());
	                        updateStmt.setBoolean(3, hf.isAberto());
	                        updateStmt.setString(4, hf.getObservacao());
	                        updateStmt.setInt(5, idEmpresa);
	                        updateStmt.setInt(6, hf.getDiaSemana());
	                        int rowsAffected = updateStmt.executeUpdate();
	                        System.out.println("DEBUG (atualizarHorariosFuncionamento): UPDATE para Dia " + hf.getDiaSemana() + " afetou " + rowsAffected + " linhas.");
	                        if (rowsAffected > 0) updatedCount++;
	                    }
	                } else {
	                    try (PreparedStatement insertStmt = con.prepareStatement(insertSql)) {
	                        insertStmt.setInt(1, idEmpresa);
	                        insertStmt.setInt(2, hf.getDiaSemana());
	                        // Se 'aberto' for false, horaAbertura e horaFechamento estão vindo como null.
	                        // Certifique-se que suas colunas TIME no banco permitem NULL, o que geralmente é o caso.
	                        insertStmt.setString(3, hf.getHoraAbertura()); // Pode ser null
	                        insertStmt.setString(4, hf.getHoraFechamento()); // Pode ser null
	                        insertStmt.setBoolean(5, hf.isAberto());
	                        insertStmt.setString(6, hf.getObservacao());
	                        int rowsAffected = insertStmt.executeUpdate();
	                        System.out.println("DEBUG (atualizarHorariosFuncionamento): INSERT para Dia " + hf.getDiaSemana() + " afetou " + rowsAffected + " linhas.");
	                        if (rowsAffected > 0) insertedCount++;
	                    }
	                }
	            }

	            con.commit();
	            System.out.println("Horários de funcionamento da empresa " + idEmpresa + " finalizado: " + updatedCount + " atualizados, " + insertedCount + " inseridos.");

	        } catch (SQLException e) {
	            con.rollback();
	            System.err.println("Erro ao atualizar/inserir horários de funcionamento: " + e.getMessage());
	            throw e;
	        } finally {
	            con.setAutoCommit(true);
	        }
	    }

}
