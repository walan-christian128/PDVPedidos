package Controller;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

import DAO.UsuarioDAO;
import Model.Clientepedido;


/**
 * Servlet implementation class clientePedidoServer
 */
@WebServlet("/clientePedidoServer")
public class clientePedidoServer extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public clientePedidoServer() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		RequestDispatcher rd = request.getRequestDispatcher("LoginPedido.jsp");
		rd.forward(request, response);
		response.getWriter().append("Served at: ").append(request.getContextPath());
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		 HttpSession session = request.getSession();
		// TODO Auto-generated method stub
		String email = request.getParameter("emailpedido");
	    String senha = request.getParameter("senhapedido");
	    String empresa = request.getParameter("empresapedido");
	    
	    if (empresa == null) {
	        empresa = (String) session.getAttribute("empresa"); // via session
	    }
	    
	    if (email == null || email.isEmpty() || senha == null || senha.isEmpty() || empresa == null || empresa.isEmpty()) {
	        request.setAttribute("erro", "Todos os campos devem ser preenchidos.");
	        RequestDispatcher rd = request.getRequestDispatcher("LoginPedido.jsp");
	        rd.forward(request, response);
	        return;
	    }
	    
	   
	    session.setAttribute("empresapedido", empresa);
	    
	    try {
	        UsuarioDAO dao = new UsuarioDAO(empresa);

	        // 🔹 Primeiro verifica se o login é válido
	        boolean loginValido = dao.efetuarLoginPedido(email, senha, empresa);
	        if (!loginValido) {
	            request.setAttribute("erro", "Usuário, senha incorretos.");
	            RequestDispatcher rd = request.getRequestDispatcher("LoginPedido.jsp");
	            rd.forward(request, response);
	            return;
	        }

	        // 🔹 Depois busca o ID do usuário
	        
	       
	     
	        Clientepedido usuarioObj = new Clientepedido();
	        usuarioObj.setEmail(email);
	        usuarioObj.setSenha(senha);
	        
	        int usuarioID = dao.cidcliPedido(usuarioObj, empresa);
	        Clientepedido nomeUser = dao.retornClipedido(usuarioObj, empresa, usuarioID);
	        
	        if (usuarioID > 0) {
	            session.setAttribute("usuarioID", usuarioID);
	            session.setAttribute("usuarioNome", nomeUser);
	            System.out.println("Usuário logado: " + usuarioID);
	            response.sendRedirect("ProdutosPedidos.jsp");
	        } else {
	            request.setAttribute("erro", "Erro ao buscar ID do usuário.");
	            RequestDispatcher rd = request.getRequestDispatcher("LoginPedido.jsp");
	            rd.forward(request, response);
	           
	        }
	        
	       
	      

	
	    } catch (Exception e) {
	        e.printStackTrace();
	        request.setAttribute("erro", "Ocorreu um erro ao processar a solicitação.");
	        RequestDispatcher rd = request.getRequestDispatcher("LoginPedido.jsp");
	        rd.forward(request, response);
	    }
	}

}
