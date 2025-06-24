package Controller;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

import Model.Clientepedido;
import DAO.ClientesPedidosDAO;

/**
 * Servlet implementation class pedidoServer
 */
@WebServlet(urlPatterns = {"/pedidoServer"})
public class pedidoServer extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public pedidoServer() {
        super();
    	
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		String action = request.getServletPath();
		System.out.println(action);
		HttpSession session = request.getSession();
		String empresa = (String) session.getAttribute("empresa");
		
		if(action.equals("/pedidoServer")) {
			CadClientePedido(request,response);
			
			
		}
	}

	private void CadClientePedido(HttpServletRequest request, HttpServletResponse response)  throws ServletException, IOException  {
		 String nome = request.getParameter("nome");
		 String telefone = request.getParameter("fone");
		 String endereco = request.getParameter("endereco");
		 String numero = request.getParameter("numero");
		 String bairro = request.getParameter("bairro");
		 String cidade = request.getParameter("cidade");
		 String estado = request.getParameter("estado");
		 String email = request.getParameter("email");
		 String senha = request.getParameter("senha");
		 
		 
		 try {
		   Clientepedido cpd = new Clientepedido();	
		   HttpSession session = request.getSession();
		   String empresa = (String) session.getAttribute("empresa");
		   ClientesPedidosDAO dao = new ClientesPedidosDAO(empresa);
		   cpd.setNome(nome);
		   cpd.setEndereco(endereco);
		   cpd.setTelefone(telefone);
		   cpd.setNumero(Integer.parseInt(numero));
		   cpd.setBairro(bairro);
		   cpd.setCidade(cidade);
		   cpd.setUf(estado);
		   cpd.setEmail(email);
		   cpd.setSenha(senha);
		   
		   dao.Clientepedido(cpd);
		   
		   RequestDispatcher rd = request.getRequestDispatcher("CadastroClientePedido.jsp");
			rd.forward(request, response);
		   
		   
		} catch (Exception e) {
			// TODO: handle exception
		}
		
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
