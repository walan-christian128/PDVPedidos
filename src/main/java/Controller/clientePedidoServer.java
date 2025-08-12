package Controller;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.io.PrintWriter;

import DAO.ClientesPedidosDAO;
import DAO.UsuarioDAO;
import Model.Clientepedido;

@WebServlet(urlPatterns = {"/clientePedidoServer","/selecionacp","/cadClientePedido"})
public class clientePedidoServer extends HttpServlet {
    private static final long serialVersionUID = 1L;
       
    public clientePedidoServer() {
        super();
    }

    // Requisições GET
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getServletPath();
        System.out.println("Ação GET: " + action);
        
        if(action.equals("/selecionacp")) {
            selecionarCliente(request, response);
        } else if(action.equals("/cadClientePedido")) {
        	cadastrarClientePedido(request,response);
        	
        }
        
        else {
            // Default - mostra a tela de login
            RequestDispatcher rd = request.getRequestDispatcher("LoginPedido.jsp");
            rd.forward(request, response);
        }
    }

    private void cadastrarClientePedido(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

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

			RequestDispatcher rd = request.getRequestDispatcher("LoginPedido.jsp");

			rd.forward(request, response);

		} catch (Exception e) {

			e.printStackTrace();

			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
					"Erro ao cadastrar cliente: " + e.getMessage());

		}

	}

	// Método para buscar cliente e retornar JSON para modal
    private void selecionarCliente(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    	String idCli = request.getParameter("id");
        if (idCli != null) {
            try {
                int cliId = Integer.parseInt(idCli);
                HttpSession session = request.getSession();
                String empresa = (String) session.getAttribute("empresa");

                ClientesPedidosDAO cpDAO = new ClientesPedidosDAO(empresa);
                Clientepedido cp = cpDAO.selecionaClientePedido(cliId);

                // Coloca o objeto do cliente na requisição para que a JSP o utilize
                request.setAttribute("clienteParaModal", cp);
                session.setAttribute("clienteLogado", cp);
             

                // Faz o forward para a mesma página que contém o menu e o modal
                request.getRequestDispatcher("ProdutosPedidos.jsp").forward(request, response);

            } catch (Exception e) {
                e.printStackTrace();
                // Em caso de erro, redireciona de volta
                response.sendRedirect("ProdutosPedidos.jsp");
            }
        } else {
            response.sendRedirect("ProdutosPedidos.jsp");
        }
     }
    

    // Requisições POST (login)
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        
        String email = request.getParameter("emailpedido");
        String senha = request.getParameter("senhapedido");
        String empresa = request.getParameter("empresapedido");
        
        // Pega empresa da sessão se não veio do formulário
        if (empresa == null) {
            empresa = (String) session.getAttribute("empresa");
        }
        
        // Validação simples dos campos
        if (email == null || email.isEmpty() || senha == null || senha.isEmpty() || empresa == null || empresa.isEmpty()) {
            request.setAttribute("erro", "Todos os campos devem ser preenchidos ou estabelecimento fora do horário.");
            RequestDispatcher rd = request.getRequestDispatcher("LoginPedido.jsp");
            rd.forward(request, response);
            return;
        }
        
        session.setAttribute("empresa", empresa); // para manter empresa na sessão
        
        try {
            UsuarioDAO dao = new UsuarioDAO(empresa);

            // Verifica login válido
            boolean loginValido = dao.efetuarLoginPedido(email, senha, empresa);
            if (!loginValido) {
                request.setAttribute("erro", "Usuário ou senha incorretos.");
                RequestDispatcher rd = request.getRequestDispatcher("LoginPedido.jsp");
                rd.forward(request, response);
                return;
            }

            // Buscar ID do usuário e objeto completo
            Clientepedido usuarioObj = new Clientepedido();
            usuarioObj.setEmail(email);
            usuarioObj.setSenha(senha);

            int usuarioID = dao.cidcliPedido(usuarioObj, empresa);
            Clientepedido clienteCompleto = dao.retornClipedido(usuarioObj, empresa, usuarioID);

            if (usuarioID > 0 && clienteCompleto != null) {
                // SALVAR NA SESSÃO O OBJETO CLIENTE COMPLETO e ID
                session.setAttribute("clienteLogado", clienteCompleto);
                session.setAttribute("usuarioID", usuarioID);

                System.out.println("Usuário logado ID: " + usuarioID);
                response.sendRedirect("ProdutosPedidos.jsp"); // página após login
            } else {
                request.setAttribute("erro", "Erro ao buscar dados do usuário.");
                RequestDispatcher rd = request.getRequestDispatcher("LoginPedido.jsp");
                rd.forward(request, response);
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("erro", "Erro ao processar a solicitação.");
            RequestDispatcher rd = request.getRequestDispatcher("LoginPedido.jsp");
            rd.forward(request, response);
        }
    }
}
