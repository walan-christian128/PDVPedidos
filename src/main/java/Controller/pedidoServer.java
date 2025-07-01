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
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime; // Não usado no código fornecido, mas bom ter se for usar.
import java.time.format.DateTimeFormatter; // Não usado no código fornecido, mas bom ter se for usar.
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import Model.Clientepedido;
import Model.Pedidos;
import Model.Produtos;
import DAO.ClientesPedidosDAO;
import DAO.PedidosDAO; // Certifique-se de que este DAO tem o método listarPedidosPorCliente
import DAO.ProdutosDAO;

/**
 * Servlet implementation class pedidoServer
 */
@WebServlet(urlPatterns = {"/pedidoServer", "/selecionarVendaCarrinho", "/finalizarPedidoServlet", "/listarPedidosCliente"}) // <-- ADICIONADO AQUI!
public class pedidoServer extends HttpServlet {
    private static final long serialVersionUID = 1L;

    public pedidoServer() {
        super();
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String acao = request.getParameter("acao");
        String servletPath = request.getServletPath(); // Pega o caminho exato que foi usado para acessar o servlet

        System.out.println("doGet - Servlet Path: " + servletPath + ", Ação: " + acao); // Para depuração

        if ("/listarPedidosCliente".equals(servletPath)) {
            // Nova lógica para listar os pedidos do cliente
            try {
                listarPedidosCliente(request, response);
            } catch (ClassNotFoundException | SQLException e) {
                e.printStackTrace();
                response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Erro ao carregar seus pedidos.");
            }
        } else if ("remover".equals(acao)) {
            String idParam = request.getParameter("id");
            if (idParam != null && !idParam.isEmpty()) {
                try {
                    int id = Integer.parseInt(idParam);
                    removerItemDoCarrinho(request, response, id);
                } catch (NumberFormatException e) {
                    response.sendError(HttpServletResponse.SC_BAD_REQUEST, "ID de produto inválido para remoção.");
                }
            } else {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "ID do produto não fornecido para remoção.");
            }
        } else if ("ver".equals(acao)) {
            exibirCarrinho(request, response);
        } else {
            // Ação padrão: adicionar um produto ao carrinho (ou quando não há "acao" explícita)
            String idParam = request.getParameter("id");
            String qtdParam = request.getParameter("qtd");

            if (idParam != null && !idParam.isEmpty() && qtdParam != null && !qtdParam.isEmpty()) {
                try {
                    int id = Integer.parseInt(idParam);
                    int qtd = Integer.parseInt(qtdParam);
                    adicionarOuAtualizarCarrinho(request, response, id, qtd);
                } catch (NumberFormatException | ClassNotFoundException e) {
                    e.printStackTrace(); // Logar a exceção
                    response.sendError(HttpServletResponse.SC_BAD_REQUEST, "ID ou quantidade inválidos para adicionar ao carrinho.");
                }
            } else {
                 // Pode ser uma requisição GET para /selecionarVendaCarrinho sem parâmetros esperados,
                 // ou uma requisição para /pedidoServer sem ação específica.
                 // Você pode optar por exibir o carrinho ou redirecionar para a página principal.
                 // Por enquanto, vamos renderizar o carrinho vazio ou com o conteúdo atual.
                 exibirCarrinho(request, response);
            }
        }
    }

    // --- Métodos de Carrinho (permanecem como estão) ---
    private void adicionarOuAtualizarCarrinho(HttpServletRequest request, HttpServletResponse response, int id, int qtd) throws ServletException, IOException, ClassNotFoundException {
        // ... (seu código atual) ...
        HttpSession session = request.getSession();
        String empresa = (String) session.getAttribute("empresa");

        ProdutosDAO dao = new ProdutosDAO(empresa);
        Produtos produto = dao.consultarPorCodigo(id);

        if (produto != null) {
            produto.setQtd_estoque(qtd);

            List<Produtos> carrinho = (List<Produtos>) session.getAttribute("carrinho");
            if (carrinho == null) {
                carrinho = new ArrayList<>();
            }

            boolean existe = false;
            for (Produtos p : carrinho) {
                if (p.getId() == id) {
                    p.setQtd_estoque(p.getQtd_estoque() + qtd);
                    existe = true;
                    break;
                }
            }

            if (!existe) {
                carrinho.add(produto);
            }

            session.setAttribute("carrinho", carrinho);
        }
        renderizarCarrinhoHTML(request, response);
    }

    private void removerItemDoCarrinho(HttpServletRequest request, HttpServletResponse response, int idParaRemover) throws ServletException, IOException {
        // ... (seu código atual) ...
        HttpSession session = request.getSession();
        List<Produtos> carrinho = (List<Produtos>) session.getAttribute("carrinho");

        if (carrinho != null) {
            carrinho.removeIf(p -> p.getId() == idParaRemover);
            session.setAttribute("carrinho", carrinho);
        }
        renderizarCarrinhoHTML(request, response);
    }

    private void exibirCarrinho(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // ... (seu código atual) ...
        renderizarCarrinhoHTML(request, response);
    }

    private void renderizarCarrinhoHTML(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // ... (seu código atual) ...
        response.setContentType("text/html; charset=UTF-8");
        PrintWriter out = response.getWriter();
        HttpSession session = request.getSession();

        List<Produtos> carrinhoAtualizado = (List<Produtos>) session.getAttribute("carrinho");
        double subtotal = 0.0;

        if (carrinhoAtualizado != null && !carrinhoAtualizado.isEmpty()) {
            for (Produtos p : carrinhoAtualizado) {
                double totalItem = p.getPreco_de_venda() * p.getQtd_estoque();
                subtotal += totalItem;

                out.println("<div id=\"itemCarrinho_" + p.getId() + "\" class=\"d-flex bg-dark text-white rounded mb-3 p-2 align-items-center\">");
                out.println("<img src=\"exibirImagemProduto?id=" + p.getId() + "\" alt=\"Imagem\" class=\"me-2 rounded\" style=\"width: 80px; height: 80px; object-fit: cover;\">");
                out.println("<div class=\"flex-grow-1\">");
                out.println("<h6 class=\"mb-1\">" + p.getDescricao() + "</h6>");
                out.println("<div class=\"d-flex justify-content-between align-items-center\">");
                out.println("<span>R$ " + String.format(Locale.getDefault(), "%.2f", p.getPreco_de_venda()).replace('.', ',') + "</span>");
                out.println("<span>Qtd: " + p.getQtd_estoque() + "</span>");
                out.println("</div>");
                out.println("</div>");
                out.println("<button type=\"button\" class=\"btn btn-danger btn-sm ms-2\" onclick=\"removerProduto(" + p.getId() + ")\">");
                out.println("<svg xmlns=\"http://www.w3.org/2000/svg\" width=\"16\" height=\"16\" fill=\"currentColor\" class=\"bi bi-trash\" viewBox=\"0 0 16 16\"> <path d=\"M5.5 5.5A.5.5 0 0 1 6 6v6a.5.5 0 0 1-1 0V6a.5.5 0 0 1 .5-.5zm2.5 0a.5.5 0 0 1 .5.5v6a.5.5 0 0 1-1 0V6a.5.5 0 0 1 .5-.5zm3 .5a.5.5 0 0 0-1 0v6a.5.5 0 0 0 1 0V6z\"/> <path d=\"M14.5 3a1 1 0 0 1-1 1H13v9a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2V4h-.5a1 1 0 0 1-1-1V2a1 1 0 0 1 1-1H6a1 1 0 0 1 1-1h2a1 1 0 0 1 1 1h3.5a1 1 0 0 1 1 1v1zM4.118 4 4 4.059V13a1 1 0 0 0 1 1h6a1 1 0 0 0 1-1V4.059L11.882 4H4.118zM2.5 3V2h11v1h-11z\"/></svg>");
                out.println("</button>");
                out.println("</div>");
            }
        } else {
            out.println("<p class=\"text-white\">Carrinho vazio.</p>");
        }

        out.println("<script>");
        out.println("document.getElementById('subtotalCarrinho').value = 'R$ " + String.format(Locale.getDefault(), "%.2f", subtotal).replace('.', ',') + "';");
        out.println("</script>");
    }

    // --- Nova lógica para listar pedidos do cliente ---
    private void listarPedidosCliente(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException, ClassNotFoundException, SQLException {
        response.setContentType("text/html; charset=UTF-8");
        PrintWriter out = response.getWriter();
        HttpSession session = request.getSession();

        String empresa = (String) session.getAttribute("empresa");
        Integer clienteId = (Integer) session.getAttribute("usuarioID");

        if (clienteId == null || empresa == null) {
            out.println("<p class=\"text-danger\">Sessão expirada ou cliente não logado. Por favor, faça login novamente.</p>");
            return;
        }

        PedidosDAO pedidoDAO = new PedidosDAO(empresa);
        List<Pedidos> listaPedidos = pedidoDAO.listarPedidosPorCliente(clienteId);

        if (listaPedidos != null && !listaPedidos.isEmpty()) {
            for (Pedidos pedido : listaPedidos) {
                out.println("<div class=\"card bg-secondary text-white mb-3 shadow-sm\">");
                out.println("<div class=\"card-header d-flex justify-content-between align-items-center\">");
                out.println("<h6 class=\"mb-0\">Pedido #" + pedido.getIdPedido() + " - Status: " + pedido.getStatus() + "</h6>");
                out.println("<small>Data: " + pedido.getDataPeedido() + "</small>");
                out.println("</div>");
                out.println("<div class=\"card-body\">");
                out.println("<p class=\"card-text mb-1\">Forma de Pagamento: " + pedido.getFormapagamento() + "</p>");
                if (pedido.getObservacoes() != null && !pedido.getObservacoes().isEmpty()) {
                    out.println("<p class=\"card-text mb-1\">Observações: " + pedido.getObservacoes() + "</p>");
                }
                // TODO: Adicionar lógica para exibir itens do pedido se estiverem disponíveis na classe Pedidos
                out.println("</div>"); // card-body
                out.println("</div>"); // card
            }
        } else {
            out.println("<p class=\"text-white\">Você ainda não tem nenhum pedido.</p>");
        }
    }


    // --- Outros Métodos (permanecem como estão) ---
    private void CadClientePedido(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException  {
        // ... (seu código atual) ...
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

    protected void finalizarPedido(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException, SQLException, ClassNotFoundException {
        // ... (seu código atual) ...
        HttpSession session = request.getSession();
	    String empresa = (String) session.getAttribute("empresa");
	    Integer clienteId = (Integer) session.getAttribute("usuarioID");

	    if (clienteId == null) {
	        response.sendError(HttpServletResponse.SC_FORBIDDEN, "Cliente não autenticado.");
	        return;
	    }

	    List<Produtos> carrinho = (List<Produtos>) session.getAttribute("carrinho");
	    if (carrinho == null || carrinho.isEmpty()) {
	        response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Carrinho vazio. Não é possível finalizar o pedido.");
	        return;
	    }

	    String observacoes = request.getParameter("observacoes");
	    String formaPagamento = request.getParameter("formaPagamento");

	    Pedidos novoPedido = new Pedidos();
	    Clientepedido cliente = new Clientepedido();
	    cliente.setId(clienteId);
	    novoPedido.setClientepedido(cliente);
	    novoPedido.setDataPeedido(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
	    novoPedido.setStatus("Pendente");
	    novoPedido.setObservacoes(observacoes);
	    novoPedido.setFormapagamento(formaPagamento);
	    //novoPedido.setItensPedido(carrinho); // <-- Certifique-se que o setter existe na classe Pedidos e é usado no DAO para salvar os itens!

	    PedidosDAO pedidoDAO = null;
	    try {
	        pedidoDAO = new PedidosDAO(empresa); // Instanciar o DAO aqui, dentro do try
	        //pedidoDAO.salvarPedido(novoPedido); // Chamar o método que salva o pedido e seus itens
	    } catch (ClassNotFoundException e) {
	        e.printStackTrace();
	        throw new ServletException("Erro de configuração do DAO de Pedidos.", e); // Lançar para o chamador
	    } 


		session.removeAttribute("carrinho");

		response.sendRedirect("ProdutosPedidos.jsp?abrirModalPedidos=true");
    }


    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getServletPath();

        System.out.println("doPost - Servlet Path: " + action);

        if (action.equals("/finalizarPedidoServlet")) {
            try {
				finalizarPedido(request, response);
			} catch (ClassNotFoundException | SQLException e) {
				e.printStackTrace(); // Logar o erro
				response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Erro interno ao finalizar o pedido.");
			}
        } else {
            System.err.println("doPost - Ação POST não reconhecida: " + action);
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Ação POST não reconhecida para " + action);
        }
    }
}