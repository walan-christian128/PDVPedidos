 package DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import Conexao.ConectionDataBases;
import Model.ItensPedidos;

public class ItensPedidoDAO {
	private Connection con;
	 private ConectionDataBases connectionFactory;

   public ItensPedidoDAO(String dataBaseNames) throws ClassNotFoundException {
   	  // Inicialize a conexão com o banco de dados
       this.connectionFactory = new ConectionDataBases(dataBaseNames);
       try {
           this.con = connectionFactory.getConectionDataBases();
       } catch (SQLException e) {
           e.printStackTrace(); // Trate a exceção conforme necessário
       }

   }
   
   public void inserirItensPedidos(ItensPedidos itp) throws SQLException {
       String sql = "INSERT INTO itens_pedido(pedido_id, produto_id, quantidade, preco_unitario) VALUES(?,?,?,?)";

       try (PreparedStatement stmt = con.prepareStatement(sql)) {
           stmt.setInt(1, itp.getPedido().getIdPedido());
           stmt.setInt(2, itp.getProduto().getId());
           stmt.setInt(3, itp.getQuantidade());
           stmt.setDouble(4, itp.getPrecoUnitario());

           stmt.execute();
           // Não precisa de stmt.close() aqui se estiver usando try-with-resources
           // (PreparedStatement stmt = con.prepareStatement(sql)) já faz o auto-close
       } catch (SQLException e) {
           System.err.println("Erro ao inserir itens do pedido: " + e.getMessage());
           throw e; // Relançar para que o Servlet possa tratar
       }
   }

   // Adicione um método para fechar a conexão se você não a estiver gerenciando de outra forma
   public void closeConnection() {
       try {
           if (con != null && !con.isClosed()) {
               con.close();
           }
       } catch (SQLException e) {
           System.err.println("Erro ao fechar conexão do ItensPedidosDAO: " + e.getMessage());
           e.printStackTrace();
       }
   }
}



