package DAO; // Ajuste o pacote conforme a sua estrutura

import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import Conexao.ConectionDataBases; // Ajuste o pacote para onde sua classe ConectionDataBases está
import Model.Clientepedido; // Ajuste o pacote para onde sua classe Clientepedido está
import Model.Pedidos;     // Ajuste o pacote para onde sua classe Pedidos está
// import Model.Vendas; // Não usada na classe fornecida, pode ser removida se não for necessária

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class PedidosDAO {
	
	private Connection con;
	private ConectionDataBases connectionFactory;

	// Construtor: Inicializa a conexão
	public PedidosDAO(String dataBaseName) throws ClassNotFoundException, SQLException {
        this.connectionFactory = new ConectionDataBases(dataBaseName);
        System.out.println("DEBUG_PEDIDO_DAO: Construtor PedidosDAO iniciado para banco: " + dataBaseName); // <-- NOVO LOG
        try {
            this.con = connectionFactory.getConectionDataBases();
            if (this.con == null || this.con.isClosed()) {
               
            } else {
              
            }
        } catch (SQLException e) {
           
            throw e; 
        }
    }
	
	public int cadastrarPedido(Pedidos obj) throws SQLException {
		
		String sql = "INSERT INTO pedidos(clientepedido_id, data_pedido, status, observacoes, forma_pagamento, total_pedido,empresa_id) VALUES(?, NOW(), ?, ?, ?, ?,?)"; 
        int idGerado = -1; // Valor padrão para indicar falha

        try (PreparedStatement stmt = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, obj.getClientepedido().getId());
            stmt.setString(2, obj.getStatus());
            stmt.setString(3, obj.getObservacoes());
            stmt.setString(4, obj.getFormapagamento());
            stmt.setDouble(5, obj.getTotalPedido());
            stmt.setInt(6, obj.getEmpresa().getId());

            int linhasAfetadas = stmt.executeUpdate(); 

            if (linhasAfetadas > 0) {
                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) {
                    	idGerado = rs.getInt(1); 
                        obj.setIdPedido(idGerado); 
                        
                    }
                }
            }
        } catch (SQLException e) {
            
            e.printStackTrace(); 
            throw e; 
        } finally {
            // Mantenha a conexão aberta se outros métodos no mesmo DAO a utilizarem
            // Se cada método gerencia sua própria conexão, feche-a aqui.
            // Para um DAO, geralmente a conexão é fechada pelo serviço que o instanciou.
            // No entanto, para garantir, você pode fechar se tiver certeza que este é o último uso.
            // Por simplicidade, vou manter a lógica de fechamento no finally de cada método aqui,
            // mas o ideal é que a conexão seja gerenciada em um nível superior (ex: service layer).
            try {
                if (con != null && !con.isClosed()) {
                    // con.close(); // Se você quer fechar a conexão após CADA operação
                    // System.out.println("PedidosDAO: Conexão fechada após cadastrarPedido.");
                }
            } catch (SQLException e) {
                System.err.println("PedidosDAO: ERRO ao fechar a conexão após cadastrarPedido: " + e.getMessage());
                e.printStackTrace();
            }
        }
        return idGerado;
    }
	
    public List<Pedidos> listarPedidosPorCliente(int clienteId) {
        List<Pedidos> listaPedidos = new ArrayList<>();
        String sql;

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String dataAtualStr = sdf.format(new Date());

        sql = "SELECT id_pedido, " 
                + "       DATE_FORMAT(data_pedido, '%d/%m/%Y %H:%i:%s') AS data_formatada, "
                + "       status, "
                + "       observacoes, "
                + "       forma_pagamento " 
                + "FROM pedidos " 
                + "WHERE clientepedido_id = ? " 
                + "  AND DATE(data_pedido) = ? "
                + "ORDER BY data_pedido DESC";

        try (PreparedStatement stmt = con.prepareStatement(sql)) { 
            stmt.setInt(1, clienteId);
            stmt.setString(2, dataAtualStr);
       
            try (ResultSet rs = stmt.executeQuery()) {
                int count = 0;
                while (rs.next()) {
                    Pedidos pedido = new Pedidos();
                    pedido.setIdPedido(rs.getInt("id_pedido")); 
                    pedido.setDataPeedido(rs.getString("data_formatada")); // Verifique se é setDataPeedido ou setDataPedido
                    pedido.setStatus(rs.getString("status"));
                    pedido.setObservacoes(rs.getString("observacoes"));
                    pedido.setFormapagamento(rs.getString("forma_pagamento"));

                    Clientepedido cliente = new Clientepedido();
                    cliente.setId(clienteId); // Já temos o ID do cliente
                    pedido.setClientepedido(cliente);

                    listaPedidos.add(pedido);
                    count++;
                }
                System.out.println("PedidosDAO: Total de pedidos encontrados para cliente " + clienteId + " na data " + dataAtualStr + ": " + count);
            }
        } catch (SQLException e) {
            System.err.println("PedidosDAO: ERRO ao listar pedidos por cliente e data: " + e.getMessage());
            e.printStackTrace();
        } finally {
            try {
                if (con != null && !con.isClosed()) {
                    // con.close(); // Decida se quer fechar a conexão aqui
                    // System.out.println("PedidosDAO: Conexão fechada após listarPedidosPorCliente.");
                }
            } catch (SQLException e) {
               
                e.printStackTrace();
            }
        }
        return listaPedidos;
    }
    
    public List<Pedidos> listaTodosPedidosDoDia() {
        List<Pedidos> listaPedidos = new ArrayList<>();
        String sql;

        sql = "SELECT "
                + "ped.id_pedido as codigo_pedido, "
        		+ "ped.total_pedido as total, "
                + "cli.nome as nome_cliente, "
                + "ped.clientepedido_id as codigo_cliente, "
                + "DATE_FORMAT(ped.data_pedido, '%d/%m/%Y %H:%i:%s') AS data_formatada, "
                + "ped.status as status, "
                + "ped.observacoes as observacoes, "
                + "ped.forma_pagamento as forma_pagamento "
                + "FROM pedidos as ped "
                + "INNER JOIN tb_cliente_pedido as cli ON cli.id = ped.clientepedido_id "
                + "WHERE DATE(ped.data_pedido) = CURDATE()"
                + "AND ped.status = 'Pendente' "; 

        try (PreparedStatement stmt = con.prepareStatement(sql)) {
          
            
            try (ResultSet rs = stmt.executeQuery()) {
                int count = 0;
                while (rs.next()) {
                    Pedidos pedido = new Pedidos();
                    Clientepedido cliente = new Clientepedido();

                    pedido.setIdPedido(rs.getInt("codigo_pedido"));
                    cliente.setNome(rs.getString("nome_cliente"));
                    cliente.setId(rs.getInt("codigo_cliente")); 

                    pedido.setDataPeedido(rs.getString("data_formatada")); 
                    pedido.setStatus(rs.getString("status"));
                    pedido.setObservacoes(rs.getString("observacoes"));
                    pedido.setFormapagamento(rs.getString("forma_pagamento"));
                    pedido.setTotalPedido(rs.getDouble("total"));

                    pedido.setClientepedido(cliente); 

                    listaPedidos.add(pedido);
                    count++; 
                }
                System.out.println("DEBUG_PEDIDO_DAO: Total de pedidos encontrados em listaTodosPedidosDoDia: " + count);
            }
        } catch (SQLException e) {
            System.err.println("DEBUG_PEDIDO_DAO: ERRO ao listar todos os pedidos do dia: " + e.getMessage());
            e.printStackTrace();
        } finally {
            try {
                if (con != null && !con.isClosed()) {
                    con.close(); 
                    System.out.println("DEBUG_PEDIDO_DAO: Conexão fechada após listaTodosPedidosDoDia.");
                }
            } catch (SQLException e) {
                System.err.println("DEBUG_PEDIDO_DAO: ERRO ao fechar a conexão após listaTodosPedidosDoDia: " + e.getMessage());
                e.printStackTrace();
            }
        }
        return listaPedidos;
    }
    public int retornaUltimoPedido() {
    	// NOTE: É importante que a conexão 'con' esteja aberta ao chamar este método.
    	// Se você fechou a conexão em outros 'finally's, este método pode falhar.
    	// Uma abordagem melhor é ter uma conexão gerenciada que é aberta e fechada uma vez por requisição
    	// ou por ciclo de vida de um DAO, e não por método.
        try {
            int idPedido = 0;
            String sql = "SELECT MAX(id_pedido) as id FROM pedidos"; // Use MAX() para obter o maior ID

            try (PreparedStatement stmt = con.prepareStatement(sql);
                 ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    idPedido = rs.getInt("id"); // Obtém o ID do alias
                }
            } return idPedido;
        } catch (SQLException e) {
           
            e.printStackTrace();
            throw new RuntimeException("Erro ao buscar o último pedido: " + e.getMessage(), e);
        } finally {
            try {
                if (con != null && !con.isClosed()) {
                    // con.close(); // Decida se quer fechar a conexão aqui
                    // System.out.println("PedidosDAO: Conexão fechada após retornaUltimoPedido.");
                }
            } catch (SQLException e) {
             
                e.printStackTrace();
            }
        }
    } 
    
    public void atualizaPedido(Pedidos ped){
    String sql ="UPDATE pedidos set status=?,observacoes=? where id_pedido=?";   
    
    try {
		PreparedStatement stmt = con.prepareStatement(sql);
		
		stmt.setString(1, ped.getStatus());
		stmt.setString(2,ped.getObservacoes());
		stmt.setInt(3, ped.getIdPedido());
		
		stmt.executeUpdate();
		stmt.close();
	} catch (Exception e) {
		// TODO: handle exception
	}
    	
    	
    }
    public void atualizaPedidoStatus(Pedidos ped){
        String sql ="UPDATE pedidos set status=? where id_pedido=?";   
        
        try {
    		PreparedStatement stmt = con.prepareStatement(sql);
    		
    		stmt.setString(1, ped.getStatus());
    		stmt.setInt(2, ped.getIdPedido());
    		
    		stmt.executeUpdate();
    		stmt.close();
    	} catch (Exception e) {
    		// TODO: handle exception
    	}
        	
        	
        }
 // Supondo que 'con' seja sua Connection
    public List<Pedidos> pedidoEntregue() { // Removido 'Pedidos pedido' do parâmetro, pois você não precisa passar um objeto vazio
    	 List<Pedidos> listaPedidos = new ArrayList<>();
         String sql;

         sql = "SELECT "
                 + "ped.id_pedido as codigo_pedido, "
         		+ "ped.total_pedido as total, "
                 + "cli.nome as nome_cliente, "
                 + "ped.clientepedido_id as codigo_cliente, "
                 + "DATE_FORMAT(ped.data_pedido, '%d/%m/%Y %H:%i:%s') AS data_formatada, "
                 + "ped.status as status, "
                 + "ped.observacoes as observacoes, "
                 + "ped.forma_pagamento as forma_pagamento "
                 + "FROM pedidos as ped "
                 + "INNER JOIN tb_cliente_pedido as cli ON cli.id = ped.clientepedido_id "
                 + "where status = 'Entregue'"; 

         try (PreparedStatement stmt = con.prepareStatement(sql)) {
           
             
             try (ResultSet rs = stmt.executeQuery()) {
                 int count = 0;
                 while (rs.next()) {
                     Pedidos pedido = new Pedidos();
                     Clientepedido cliente = new Clientepedido();

                     pedido.setIdPedido(rs.getInt("codigo_pedido"));
                     cliente.setNome(rs.getString("nome_cliente"));
                     cliente.setId(rs.getInt("codigo_cliente")); 

                     pedido.setDataPeedido(rs.getString("data_formatada")); 
                     pedido.setStatus(rs.getString("status"));
                     pedido.setObservacoes(rs.getString("observacoes"));
                     pedido.setFormapagamento(rs.getString("forma_pagamento"));
                     pedido.setTotalPedido(rs.getDouble("total"));

                     pedido.setClientepedido(cliente); 

                     listaPedidos.add(pedido);
                     count++; 
                 }
                 System.out.println("DEBUG_PEDIDO_DAO: Total de pedidos encontrados em listaTodosPedidosDoDia: " + count);
             }
         } catch (SQLException e) {
             System.err.println("DEBUG_PEDIDO_DAO: ERRO ao listar todos os pedidos do dia: " + e.getMessage());
             e.printStackTrace();
         } finally {
             try {
                 if (con != null && !con.isClosed()) {
                     con.close(); 
                     System.out.println("DEBUG_PEDIDO_DAO: Conexão fechada após listaTodosPedidosDoDia.");
                 }
             } catch (SQLException e) {
                 System.err.println("DEBUG_PEDIDO_DAO: ERRO ao fechar a conexão após listaTodosPedidosDoDia: " + e.getMessage());
                 e.printStackTrace();
             }
         }
        return listaPedidos;
    }
    
    public List<Pedidos> pedidoPreparacao() { // Removido 'Pedidos pedido' do parâmetro, pois você não precisa passar um objeto vazio
   	 List<Pedidos> listaPedidos = new ArrayList<>();
        String sql;

        sql = "SELECT "
                + "ped.id_pedido as codigo_pedido, "
        		+ "ped.total_pedido as total, "
                + "cli.nome as nome_cliente, "
                + "ped.clientepedido_id as codigo_cliente, "
                + "DATE_FORMAT(ped.data_pedido, '%d/%m/%Y %H:%i:%s') AS data_formatada, "
                + "ped.status as status, "
                + "ped.observacoes as observacoes, "
                + "ped.forma_pagamento as forma_pagamento "
                + "FROM pedidos as ped "
                + "INNER JOIN tb_cliente_pedido as cli ON cli.id = ped.clientepedido_id "
                + "where status = 'Em Preparo'"; 

        try (PreparedStatement stmt = con.prepareStatement(sql)) {
          
            
            try (ResultSet rs = stmt.executeQuery()) {
                int count = 0;
                while (rs.next()) {
                    Pedidos pedido = new Pedidos();
                    Clientepedido cliente = new Clientepedido();

                    pedido.setIdPedido(rs.getInt("codigo_pedido"));
                    cliente.setNome(rs.getString("nome_cliente"));
                    cliente.setId(rs.getInt("codigo_cliente")); 

                    pedido.setDataPeedido(rs.getString("data_formatada")); 
                    pedido.setStatus(rs.getString("status"));
                    pedido.setObservacoes(rs.getString("observacoes"));
                    pedido.setFormapagamento(rs.getString("forma_pagamento"));
                    pedido.setTotalPedido(rs.getDouble("total"));

                    pedido.setClientepedido(cliente); 

                    listaPedidos.add(pedido);
                    count++; 
                }
                System.out.println("DEBUG_PEDIDO_DAO: Total de pedidos encontrados em listaTodosPedidosDoDia: " + count);
            }
        } catch (SQLException e) {
            System.err.println("DEBUG_PEDIDO_DAO: ERRO ao listar todos os pedidos do dia: " + e.getMessage());
            e.printStackTrace();
        } finally {
            try {
                if (con != null && !con.isClosed()) {
                    con.close(); 
                    System.out.println("DEBUG_PEDIDO_DAO: Conexão fechada após listaTodosPedidosDoDia.");
                }
            } catch (SQLException e) {
                System.err.println("DEBUG_PEDIDO_DAO: ERRO ao fechar a conexão após listaTodosPedidosDoDia: " + e.getMessage());
                e.printStackTrace();
            }
        }
       return listaPedidos;
   }
    public List<Pedidos> pedidoReprovados() { // Removido 'Pedidos pedido' do parâmetro, pois você não precisa passar um objeto vazio
      	 List<Pedidos> listaPedidos = new ArrayList<>();
           String sql;

           sql = "SELECT "
                   + "ped.id_pedido as codigo_pedido, "
           		+ "ped.total_pedido as total, "
                   + "cli.nome as nome_cliente, "
                   + "ped.clientepedido_id as codigo_cliente, "
                   + "DATE_FORMAT(ped.data_pedido, '%d/%m/%Y %H:%i:%s') AS data_formatada, "
                   + "ped.status as status, "
                   + "ped.observacoes as observacoes, "
                   + "ped.forma_pagamento as forma_pagamento "
                   + "FROM pedidos as ped "
                   + "INNER JOIN tb_cliente_pedido as cli ON cli.id = ped.clientepedido_id "
                   + "where status = 'Reprovado'"; 

           try (PreparedStatement stmt = con.prepareStatement(sql)) {
             
               
               try (ResultSet rs = stmt.executeQuery()) {
                   int count = 0;
                   while (rs.next()) {
                       Pedidos pedido = new Pedidos();
                       Clientepedido cliente = new Clientepedido();

                       pedido.setIdPedido(rs.getInt("codigo_pedido"));
                       cliente.setNome(rs.getString("nome_cliente"));
                       cliente.setId(rs.getInt("codigo_cliente")); 

                       pedido.setDataPeedido(rs.getString("data_formatada")); 
                       pedido.setStatus(rs.getString("status"));
                       pedido.setObservacoes(rs.getString("observacoes"));
                       pedido.setFormapagamento(rs.getString("forma_pagamento"));
                       pedido.setTotalPedido(rs.getDouble("total"));

                       pedido.setClientepedido(cliente); 

                       listaPedidos.add(pedido);
                       count++; 
                   }
                   System.out.println("DEBUG_PEDIDO_DAO: Total de pedidos encontrados em listaTodosPedidosDoDia: " + count);
               }
           } catch (SQLException e) {
               System.err.println("DEBUG_PEDIDO_DAO: ERRO ao listar todos os pedidos do dia: " + e.getMessage());
               e.printStackTrace();
           } finally {
               try {
                   if (con != null && !con.isClosed()) {
                       con.close(); 
                       System.out.println("DEBUG_PEDIDO_DAO: Conexão fechada após listaTodosPedidosDoDia.");
                   }
               } catch (SQLException e) {
                   System.err.println("DEBUG_PEDIDO_DAO: ERRO ao fechar a conexão após listaTodosPedidosDoDia: " + e.getMessage());
                   e.printStackTrace();
               }
           }
          return listaPedidos;
      }
    
    public List<Pedidos> pedidoEmRota() { // Removido 'Pedidos pedido' do parâmetro, pois você não precisa passar um objeto vazio
   	 List<Pedidos> listaPedidos = new ArrayList<>();
        String sql;

        sql = "SELECT "
                + "ped.id_pedido as codigo_pedido, "
        		+ "ped.total_pedido as total, "
                + "cli.nome as nome_cliente, "
                + "ped.clientepedido_id as codigo_cliente, "
                + "DATE_FORMAT(ped.data_pedido, '%d/%m/%Y %H:%i:%s') AS data_formatada, "
                + "ped.status as status, "
                + "ped.observacoes as observacoes, "
                + "ped.forma_pagamento as forma_pagamento "
                + "FROM pedidos as ped "
                + "INNER JOIN tb_cliente_pedido as cli ON cli.id = ped.clientepedido_id "
                + "where status = 'Em Rota de Entrega'"; 

        try (PreparedStatement stmt = con.prepareStatement(sql)) {
          
            
            try (ResultSet rs = stmt.executeQuery()) {
                int count = 0;
                while (rs.next()) {
                    Pedidos pedido = new Pedidos();
                    Clientepedido cliente = new Clientepedido();

                    pedido.setIdPedido(rs.getInt("codigo_pedido"));
                    cliente.setNome(rs.getString("nome_cliente"));
                    cliente.setId(rs.getInt("codigo_cliente")); 

                    pedido.setDataPeedido(rs.getString("data_formatada")); 
                    pedido.setStatus(rs.getString("status"));
                    pedido.setObservacoes(rs.getString("observacoes"));
                    pedido.setFormapagamento(rs.getString("forma_pagamento"));
                    pedido.setTotalPedido(rs.getDouble("total"));

                    pedido.setClientepedido(cliente); 

                    listaPedidos.add(pedido);
                    count++; 
                }
                System.out.println("DEBUG_PEDIDO_DAO: Total de pedidos encontrados em listaTodosPedidosDoDia: " + count);
            }
        } catch (SQLException e) {
            System.err.println("DEBUG_PEDIDO_DAO: ERRO ao listar todos os pedidos do dia: " + e.getMessage());
            e.printStackTrace();
        } finally {
            try {
                if (con != null && !con.isClosed()) {
                    con.close(); 
                    System.out.println("DEBUG_PEDIDO_DAO: Conexão fechada após listaTodosPedidosDoDia.");
                }
            } catch (SQLException e) {
                System.err.println("DEBUG_PEDIDO_DAO: ERRO ao fechar a conexão após listaTodosPedidosDoDia: " + e.getMessage());
                e.printStackTrace();
            }
        }
       return listaPedidos;
   }
    public List<Pedidos> pedidoPendentes() { // Removido 'Pedidos pedido' do parâmetro, pois você não precisa passar um objeto vazio
   	 List<Pedidos> listaPedidos = new ArrayList<>();
        String sql;

        sql = "SELECT "
                + "ped.id_pedido as codigo_pedido, "
        		+ "ped.total_pedido as total, "
                + "cli.nome as nome_cliente, "
                + "ped.clientepedido_id as codigo_cliente, "
                + "DATE_FORMAT(ped.data_pedido, '%d/%m/%Y %H:%i:%s') AS data_formatada, "
                + "ped.status as status, "
                + "ped.observacoes as observacoes, "
                + "ped.forma_pagamento as forma_pagamento "
                + "FROM pedidos as ped "
                + "INNER JOIN tb_cliente_pedido as cli ON cli.id = ped.clientepedido_id "
                + "where status = 'Pendente' "; 

        try (PreparedStatement stmt = con.prepareStatement(sql)) {
          
            
            try (ResultSet rs = stmt.executeQuery()) {
                int count = 0;
                while (rs.next()) {
                    Pedidos pedido = new Pedidos();
                    Clientepedido cliente = new Clientepedido();

                    pedido.setIdPedido(rs.getInt("codigo_pedido"));
                    cliente.setNome(rs.getString("nome_cliente"));
                    cliente.setId(rs.getInt("codigo_cliente")); 

                    pedido.setDataPeedido(rs.getString("data_formatada")); 
                    pedido.setStatus(rs.getString("status"));
                    pedido.setObservacoes(rs.getString("observacoes"));
                    pedido.setFormapagamento(rs.getString("forma_pagamento"));
                    pedido.setTotalPedido(rs.getDouble("total"));

                    pedido.setClientepedido(cliente); 

                    listaPedidos.add(pedido);
                    count++; 
                }
                System.out.println("DEBUG_PEDIDO_DAO: Total de pedidos encontrados em listaTodosPedidosDoDia: " + count);
            }
        } catch (SQLException e) {
            System.err.println("DEBUG_PEDIDO_DAO: ERRO ao listar todos os pedidos do dia: " + e.getMessage());
            e.printStackTrace();
        } finally {
            try {
                if (con != null && !con.isClosed()) {
                    con.close(); 
                    System.out.println("DEBUG_PEDIDO_DAO: Conexão fechada após listaTodosPedidosDoDia.");
                }
            } catch (SQLException e) {
                System.err.println("DEBUG_PEDIDO_DAO: ERRO ao fechar a conexão após listaTodosPedidosDoDia: " + e.getMessage());
                e.printStackTrace();
            }
        }
       return listaPedidos;
   }
    public List<Pedidos> todosEntregue() { // Removido 'Pedidos pedido' do parâmetro, pois você não precisa passar um objeto vazio
   	 List<Pedidos> listaPedidos = new ArrayList<>();
        String sql;

        sql = "SELECT "
                + "ped.id_pedido as codigo_pedido, "
        		+ "ped.total_pedido as total, "
                + "cli.nome as nome_cliente, "
                + "ped.clientepedido_id as codigo_cliente, "
                + "DATE_FORMAT(ped.data_pedido, '%d/%m/%Y %H:%i:%s') AS data_formatada, "
                + "ped.status as status, "
                + "ped.observacoes as observacoes, "
                + "ped.forma_pagamento as forma_pagamento "
                + "FROM pedidos as ped "
                + "INNER JOIN tb_cliente_pedido as cli ON cli.id = ped.clientepedido_id "; 

        try (PreparedStatement stmt = con.prepareStatement(sql)) {
          
            
            try (ResultSet rs = stmt.executeQuery()) {
                int count = 0;
                while (rs.next()) {
                    Pedidos pedido = new Pedidos();
                    Clientepedido cliente = new Clientepedido();

                    pedido.setIdPedido(rs.getInt("codigo_pedido"));
                    cliente.setNome(rs.getString("nome_cliente"));
                    cliente.setId(rs.getInt("codigo_cliente")); 

                    pedido.setDataPeedido(rs.getString("data_formatada")); 
                    pedido.setStatus(rs.getString("status"));
                    pedido.setObservacoes(rs.getString("observacoes"));
                    pedido.setFormapagamento(rs.getString("forma_pagamento"));
                    pedido.setTotalPedido(rs.getDouble("total"));

                    pedido.setClientepedido(cliente); 

                    listaPedidos.add(pedido);
                    count++; 
                }
                System.out.println("DEBUG_PEDIDO_DAO: Total de pedidos encontrados em listaTodosPedidosDoDia: " + count);
            }
        } catch (SQLException e) {
            System.err.println("DEBUG_PEDIDO_DAO: ERRO ao listar todos os pedidos do dia: " + e.getMessage());
            e.printStackTrace();
        } finally {
            try {
                if (con != null && !con.isClosed()) {
                    con.close(); 
                    System.out.println("DEBUG_PEDIDO_DAO: Conexão fechada após listaTodosPedidosDoDia.");
                }
            } catch (SQLException e) {
                System.err.println("DEBUG_PEDIDO_DAO: ERRO ao fechar a conexão após listaTodosPedidosDoDia: " + e.getMessage());
                e.printStackTrace();
            }
        }
       return listaPedidos;
   }
   
   
    public int codigoPedido() {
        int idPedido = 0;
        String sql = "SELECT id_pedido FROM pedidos ORDER BY id_pedido DESC LIMIT 1"; // pega o último ID
        
        try (PreparedStatement stmt = con.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            if (rs.next()) {
                idPedido = rs.getInt("id_pedido");
            }
        } catch (SQLException e) {
            e.printStackTrace(); // Melhor que deixar vazio
        }
        
        return idPedido;
    }

}