package DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import javax.naming.NamingException;

import Conexao.ConectionDataBases;
import Model.Clientepedido;
import Model.PasswordUtil;

public class ClientesPedidosDAO {
	  private Connection con;
	    private ConectionDataBases connectionFactory;

	    public ClientesPedidosDAO(String dataBaseNames) throws ClassNotFoundException, NamingException {
	    	 // Inicialize a conexão com o banco de dados
	        this.connectionFactory = new ConectionDataBases(dataBaseNames);
	        try {
	            this.con = connectionFactory.getConectionDataBases();
	        } catch (SQLException e) {
	            e.printStackTrace(); // Trate a exceção conforme necessário
	        }
	    }
	   
	    public void Clientepedido(Clientepedido obj) {
	    
	    	String sql = "insert INTO tb_cliente_pedido(nome,telefone,endereco,numero,bairro,cidade,estado,email,senha)value(?,?,?,?,?,?,?,?,?)";
	    	
	    	try {
	    		PreparedStatement stmt = con.prepareStatement(sql);
	    		
	    		stmt.setString(1, obj.getNome());
	    		stmt.setString(2, obj.getTelefone());
	    		stmt.setString(3, obj.getEndereco());
	    		stmt.setInt(4, obj.getNumero());
	    		stmt.setString(5, obj.getBairro());
	    		stmt.setString(6, obj.getCidade());
	    		stmt.setString(7, obj.getUf());
	    		stmt.setString(8, obj.getEmail());
	    		stmt.setString(9, PasswordUtil.hashPassword(obj.getSenha()));
	    		
	    		
	    		stmt.execute();
	    		stmt.close();
	    		
	    		
	    		
			} catch (SQLException erro) {
				// TODO: handle exception
			}
	    	
	    	
	    }

}
