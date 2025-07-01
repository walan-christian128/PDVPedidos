package DAO;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import Conexao.ConectionDataBases;
import Model.Clientepedido;
import Model.Pedidos;
import Model.Vendas;

import java.sql.Connection;
import java.sql.PreparedStatement;

import java.sql.ResultSet;

public class PedidosDAO {
	
	private Connection con;
	 private ConectionDataBases connectionFactory;
	public PedidosDAO(String dataBaseName) throws ClassNotFoundException {
		 this.connectionFactory = new ConectionDataBases(dataBaseName);
	        try {
	            this.con = connectionFactory.getConectionDataBases();
	        } catch (SQLException e) {
	            e.printStackTrace(); // Trate a exceção conforme necessário
	        }
	}
	
	public void cadastrarPedido(Pedidos obj) {
		
		String sql = "insert into pedidos(clientepedido_id,data_pedido,status,observacoes,forma_pagamento)values (?,?,?,?,?)";
		 
		try {
			PreparedStatement stmt = con.prepareStatement(sql);
			stmt.setInt(1, obj.getClientepedido().getId());
			stmt.setString(2, obj.getDataPeedido());
			stmt.setString(3, obj.getStatus());
			stmt.setString(4, obj.getObservacoes());
			stmt.setString(5, obj.getFormapagamento());
			
			stmt.execute();
			stmt.close();
			
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	   public List<Pedidos> listarPedidosPorCliente(int clienteId) {
	        List<Pedidos> listaPedidos = new ArrayList<>();
	        String sql;

	        // Formato da data atual para comparação (YYYY-MM-DD)
	        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	        String dataAtualStr = sdf.format(new Date()); // Obtém a data atual no formato YYYY-MM-DD

	        // **Ajuste na query SQL para filtrar pela data do dia**
	        // A função DATE(coluna_string) é comum em MySQL/SQLite para extrair a parte da data de uma string.
	        // Se for PostgreSQL, pode ser CAST(data_pedido AS DATE) ou to_char(data_pedido, 'YYYY-MM-DD').
	        // Se for SQL Server, pode ser CONVERT(DATE, data_pedido).
	        // Escolha a que se adequa ao seu SGBD.
	        sql = "SELECT id_pedido, data_pedido, status, observacoes, forma_pagamento " +
	              "FROM pedidos " +
	              "WHERE clientepedido_id = ? " +
	              "  AND DATE(data_pedido) = ? " + // <-- Adicionada esta condição!
	              "ORDER BY data_pedido DESC";

	        try (PreparedStatement stmt = con.prepareStatement(sql)) {
	            stmt.setInt(1, clienteId);
	            stmt.setString(2, dataAtualStr); // <-- Define a data atual como parâmetro
	            try (ResultSet rs = stmt.executeQuery()) {
	                while (rs.next()) {
	                    Pedidos pedido = new Pedidos();
	                    pedido.setIdPedido(rs.getInt("id_pedido"));
	                    pedido.setDataPeedido(rs.getString("data_pedido"));
	                    pedido.setStatus(rs.getString("status"));
	                    pedido.setObservacoes(rs.getString("observacoes"));
	                    pedido.setFormapagamento(rs.getString("forma_pagamento"));

	                    Clientepedido cliente = new Clientepedido();
	                    cliente.setId(clienteId);
	                    pedido.setClientepedido(cliente);

	                    listaPedidos.add(pedido);
	                }
	            }
	        } catch (SQLException e) {
	            System.err.println("Erro ao listar pedidos por cliente e data: " + e.getMessage());
	            e.printStackTrace();
	            // Considere relançar a exceção ou lidar com ela de forma mais robusta no servlet
	        }
	        return listaPedidos;
	    }
	}
