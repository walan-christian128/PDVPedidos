package DAO;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
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
	    // Ajuste o SQL para selecionar colunas relevantes do pedido e, se possível, do cliente associado
	    // Se você quer também os ITENS do pedido, a query será mais complexa (JOIN com tabela itens_pedido)
	    // Por simplicidade, vamos pegar apenas os dados do pedido principal por enquanto.
	    String sql = "SELECT id, data_pedido, status, observacoes, forma_pagamento " +
	                 "FROM pedidos " +
	                 "WHERE clientepedido_id = ? ORDER BY data_pedido DESC"; // Ordena do mais recente para o mais antigo

	    try (PreparedStatement stmt = con.prepareStatement(sql)) {
	        stmt.setInt(1, clienteId);
	        try (ResultSet rs = stmt.executeQuery()) {
	            while (rs.next()) {
	                Pedidos pedido = new Pedidos();
	                pedido.setIdPedido(rs.getInt("id")); // Adicione o ID do pedido
	                pedido.setDataPeedido(rs.getString("data_pedido"));
	                pedido.setStatus(rs.getString("status"));
	                pedido.setObservacoes(rs.getString("observacoes"));
	                pedido.setFormapagamento(rs.getString("forma_pagamento"));
	                
	                // Opcional: Se você quer o objeto Clientepedido completo dentro de Pedidos,
	                // você precisaria de outro DAO ou um JOIN na query para buscar os detalhes do cliente.
	                // Por agora, o ID do cliente já está associado via clientepedido_id.
	                Clientepedido cliente = new Clientepedido(); // Ou Cliente, dependendo da sua classe
	                cliente.setId(clienteId);
	                pedido.setClientepedido(cliente);

	                listaPedidos.add(pedido);
	            }
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	        // Considere lançar uma exceção mais específica ou retornar null em caso de erro
	    }
	    return listaPedidos;
	}
}
