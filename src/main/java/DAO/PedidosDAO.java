package DAO;

import java.sql.SQLException;
import java.sql.Statement;
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
	
	public int cadastrarPedido(Pedidos obj) throws SQLException {
		
		 String sql = "INSERT INTO pedidos(clientepedido_id, data_pedido, status, observacoes, forma_pagamento) VALUES(?, NOW(), ?, ?, ?)"; // NOW() para data automática
	        int idGeradoit = -1; // Valor padrão para indicar falha

	        // Use Statement.RETURN_GENERATED_KEYS para obter o ID gerado
	        try (PreparedStatement stmt = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
	            stmt.setInt(1, obj.getClientepedido().getId());
	            stmt.setString(2, obj.getStatus());
	            stmt.setString(3, obj.getObservacoes());
	            stmt.setString(4, obj.getFormapagamento());

	            int linhasAfetadas = stmt.executeUpdate(); // Use executeUpdate para INSERT/UPDATE/DELETE

	            if (linhasAfetadas > 0) {
	                try (ResultSet rs = stmt.getGeneratedKeys()) {
	                    if (rs.next()) {
	                    	idGeradoit = rs.getInt(1); // O primeiro (e geralmente único) ID gerado
	                        obj.setIdPedido(idGeradoit); // Atualiza o ID no objeto Pedidos também
	                    }
	                }
	            }
	        } catch (SQLException e) {
	            System.err.println("Erro ao salvar pedido: " + e.getMessage());
	            throw e; // Relança a exceção para o Servlet tratar
	        }
	        return idGeradoit;
	    }
	


    public List<Pedidos> listarPedidosPorCliente(int clienteId) {
        List<Pedidos> listaPedidos = new ArrayList<>();
        String sql;

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String dataAtualStr = sdf.format(new Date());

        sql = "SELECT id_pedido," // CORREÇÃO: era id_pedido. agora é id_pedido,
                + "       DATE_FORMAT(data_pedido, '%d/%m/%Y %H:%i:%s') AS data_formatada,"
                + "       status,"
                + "       observacoes,"
                + "       forma_pagamento " // Espaço no final para concatenar corretamente
                + "FROM pedidos " // Espaço no final
                + "WHERE clientepedido_id = ? " // Espaço no final
                + "  AND DATE(data_pedido) = ? "
                + "ORDER BY data_pedido DESC";

        try (PreparedStatement stmt = con.prepareStatement(sql)) { // CORREÇÃO AQUI ABAIXO:
            stmt.setInt(1, clienteId);
            stmt.setString(2, dataAtualStr);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Pedidos pedido = new Pedidos();
                    pedido.setIdPedido(rs.getInt("id_pedido")); // Use o nome real da coluna ou o alias se for o caso
                    pedido.setDataPeedido(rs.getString("data_formatada"));
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
        }
        return listaPedidos;
    }


	}
