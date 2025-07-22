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

import java.net.URLEncoder;

import java.sql.SQLException;

import java.text.DecimalFormat;

import java.text.DecimalFormatSymbols;

import java.text.SimpleDateFormat;

import java.time.LocalDateTime;

import java.time.format.DateTimeFormatter;

import java.util.ArrayList;

import java.util.Date;

import java.util.Iterator;

import java.util.List;

import java.util.Locale;

import org.json.JSONArray;

import org.json.JSONObject;

import com.google.gson.Gson;

import Model.Clientepedido;

import Model.ItemCarrinho;

import Model.ItensPedidos;

import Model.ItensVenda;

import Model.Pedidos;

import Model.Produtos;

import DAO.ClientesPedidosDAO;

import DAO.ItensPedidoDAO;

import DAO.PedidosDAO;

import DAO.ProdutosDAO;

import DAO.itensVendaDAO;

@WebServlet(urlPatterns = { "/pedidoServer", "/selecionarVendaCarrinho", "/finalizarPedidoServlet",
		"/listarPedidosCliente", "/listarPedidos","/selecionarPedido","/getPedidosEntreguesJson" })

public class pedidoServer extends HttpServlet {

	private static final long serialVersionUID = 1L;

	public pedidoServer() {

		super();

	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		String acao = request.getParameter("acao");

		String servletPath = request.getServletPath();

		System.out.println("doGet - Servlet Path: " + servletPath + ", Ação: " + acao);

		if ("/listarPedidosCliente".equals(servletPath)) {

			try {

				listarPedidosCliente(request, response);

			} catch (ClassNotFoundException | SQLException e) {

				e.printStackTrace();

				response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Erro ao carregar seus pedidos.");

			}

		}	else if ("/getPedidosEntreguesJson".equals(servletPath)) {

			listarPedidosEntregue(request, response);

		} 
		else if ("remover".equals(acao)) {

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

		} else if ("/listarPedidos".equals(acao)) {

			listapedidos(request, response);

		}
		else if ("/selecionarPedido".equals(servletPath)) {
		    selecionarPedido(request, response);
		}

		else {

			String idParam = request.getParameter("id");

			String qtdParam = request.getParameter("qtd");

			if (idParam != null && !idParam.isEmpty() && qtdParam != null && !qtdParam.isEmpty()) {

				try {

					int id = Integer.parseInt(idParam);

					int qtd = Integer.parseInt(qtdParam);

					adicionarOuAtualizarCarrinho(request, response, id, qtd);

				} catch (NumberFormatException | ClassNotFoundException e) {

					e.printStackTrace();

					response.sendError(HttpServletResponse.SC_BAD_REQUEST,
							"ID ou quantidade inválidos para adicionar ao carrinho.");

				}

			} else {

				exibirCarrinho(request, response);

			}

		}

	}

	private void listarPedidosEntregue(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	    response.setContentType("application/json");
	    response.setCharacterEncoding("UTF-8");
	    String empresa = (String) request.getSession().getAttribute("empresa");

	    // A variável 'ped' não é utilizada, pode ser removida.
	    // Pedidos ped = new Pedidos();

	    try {
	        // Validação inicial: verifica se a sessão da empresa está ativa
	        if (empresa == null || empresa.isEmpty()) {
	            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // HTTP 401 Unauthorized
	            response.getWriter().write("{\"error\": \"Sessão expirada ou empresa não definida. Faça login novamente.\"}");
	            System.err.println("Erro: Sessão expirada ou empresa não definida ao listar pedidos entregues.");
	            return; // Sai do método após enviar o erro
	        }

	        PedidosDAO pedidosDAO = new PedidosDAO(empresa);
	        List<Pedidos> pedidosEntregues = pedidosDAO.pedidoEntregue(); // Chama seu método DAO

	        Gson gson = new Gson();
	        String json = gson.toJson(pedidosEntregues); // Converte a lista para JSON

	        response.getWriter().write(json); // Envia o JSON como resposta
	        System.out.println("DEBUG: Pedidos entregues encontrados: " + pedidosEntregues.size());

	    } catch (SQLException e) {
	        // Captura exceções específicas de banco de dados
	        System.err.println("ERRO SQL ao listar pedidos entregues: " + e.getMessage());
	        e.printStackTrace(); // Imprime o stack trace para depuração no console do servidor
	        response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR); // HTTP 500 Internal Server Error
	        response.getWriter().write("{\"error\": \"Erro interno do servidor ao acessar o banco de dados. Tente novamente mais tarde.\"}");
	    } catch (Exception e) {
	        // Captura qualquer outra exceção inesperada
	        System.err.println("ERRO INESPERADO ao listar pedidos entregues: " + e.getMessage());
	        e.printStackTrace(); // Imprime o stack trace para depuração
	        response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR); // HTTP 500 Internal Server Error
	        response.getWriter().write("{\"error\": \"Ocorreu um erro inesperado no servidor. Por favor, tente novamente.\"}");
	    }
	}
	private void selecionarPedido(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	    String empresa = (String) request.getSession().getAttribute("empresa");
	    if (empresa == null) {
	        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
	        response.getWriter().write("Sessão expirada. Faça login novamente.");
	        return;
	    }

	    String idStr = request.getParameter("id");
	    if (idStr == null || idStr.isEmpty()) {
	        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
	        response.getWriter().write("ID do pedido não fornecido.");
	        return;
	    }

	    try {
	        int idPedido = Integer.parseInt(idStr);
	        ItensPedidoDAO dao = new ItensPedidoDAO(empresa);
	        List<ItensPedidos> itens = dao.detalhePedido(idPedido);

	        response.setContentType("application/json");
	        response.setCharacterEncoding("UTF-8");

	        if (itens == null || itens.isEmpty()) {
	            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
	            response.getWriter().write("{\"error\":\"Pedido sem itens ou não encontrado.\"}");
	        } else {
	            Gson gson = new Gson();
	            String json = gson.toJson(itens);
	            response.getWriter().write(json);
	        }
	    } catch (NumberFormatException e) {
	        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
	        response.getWriter().write("{\"error\":\"ID do pedido inválido.\"}");
	    } catch (Exception e) {
	        response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
	        response.getWriter().write("{\"error\":\"Erro interno ao buscar detalhes do pedido.\"}");
	    }
	}
	private void listapedidos(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		HttpSession session = request.getSession();

		String empresa = (String) session.getAttribute("empresa");

		if (empresa == null || empresa.isEmpty()) {

// Lide com o caso onde a empresa não está na sessão

			response.sendRedirect("login.jsp"); // Ou alguma página de erro/login

			return;

		}

		try {

			PedidosDAO pedidoDAO = new PedidosDAO(empresa); // Instancia a DAO

			List<Pedidos> listaPedidosDoDia = pedidoDAO.listaTodosPedidosDoDia(); // Chama o método atualizado

			request.setAttribute("listaPedidosDoDia", listaPedidosDoDia);

			if (listaPedidosDoDia != null) {

				System.out.println("Servlet: Lista de pedidos recebida da DAO. Tamanho: " + listaPedidosDoDia.size());

			} else {

				System.out.println("Servlet: Lista de pedidos recebida da DAO é nula.");

			}

// Nome do atributo para a JSP

			RequestDispatcher rd = request.getRequestDispatcher("Pedidos.jsp");

			rd.forward(request, response);

		} catch (Exception e) {

			System.err.println("Erro no servlet ao listar pedidos: " + e.getMessage());

			e.printStackTrace();

			request.setAttribute("errorMessage", "Erro ao carregar os pedidos: " + e.getMessage());

			RequestDispatcher rd = request.getRequestDispatcher("erro.jsp"); // Redirecione para uma página de erro

			rd.forward(request, response);

		}

	}

	private void adicionarOuAtualizarCarrinho(HttpServletRequest request, HttpServletResponse response, int idProduto,
			int quantidade) throws ServletException, IOException, ClassNotFoundException {

		HttpSession session = request.getSession();

		String empresa = (String) session.getAttribute("empresa");

		if (empresa == null || empresa.isEmpty()) {

			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

			response.getWriter().print("<p class='text-danger'>Sessão expirada. Faça login novamente.</p>");

			return;

		}

		ProdutosDAO dao = null;

		try {

			dao = new ProdutosDAO(empresa);

			Produtos produto = dao.consultarPorCodigo(idProduto);

			if (produto != null) {

				List<ItemCarrinho> carrinho = (List<ItemCarrinho>) session.getAttribute("carrinho");

				if (carrinho == null) {

					carrinho = new ArrayList<>();

					session.setAttribute("carrinho", carrinho);

				}

				boolean itemAtualizado = false;

				for (ItemCarrinho item : carrinho) {

					if (item.getProduto().getId() == idProduto) {

						item.setQuantidade(item.getQuantidade() + quantidade);

						itemAtualizado = true;

						break;

					}

				}

				if (!itemAtualizado) {

					carrinho.add(new ItemCarrinho(produto, quantidade));

				}

				renderizarCarrinhoHTML(request, response);

			} else {

				response.setStatus(HttpServletResponse.SC_NOT_FOUND);

				response.getWriter().print("<p class='text-danger'>Produto não encontrado.</p>");

			}

		} catch (Exception e) {

			e.printStackTrace();

			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);

			response.getWriter().print("<p class='text-danger'>Erro de banco de dados ao adicionar produto.</p>");

		}

	}

	private void removerItemDoCarrinho(HttpServletRequest request, HttpServletResponse response, int idParaRemover)
			throws ServletException, IOException {

		HttpSession session = request.getSession();

		List<ItemCarrinho> carrinho = (List<ItemCarrinho>) session.getAttribute("carrinho");

		if (carrinho != null) {

			Iterator<ItemCarrinho> iterator = carrinho.iterator();

			while (iterator.hasNext()) {

				ItemCarrinho item = iterator.next();

				if (item.getProduto().getId() == idParaRemover) {

					iterator.remove();

					break;

				}

			}

			session.setAttribute("carrinho", carrinho);

		}

		renderizarCarrinhoHTML(request, response);

	}

	private void exibirCarrinho(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		renderizarCarrinhoHTML(request, response);

	}

	private void renderizarCarrinhoHTML(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		response.setContentType("text/html; charset=UTF-8");

		PrintWriter out = response.getWriter();

		HttpSession session = request.getSession();

		List<ItemCarrinho> carrinhoAtualizado = (List<ItemCarrinho>) session.getAttribute("carrinho");

		double subtotalGeral = 0.0;

		DecimalFormatSymbols symbols = new DecimalFormatSymbols(new Locale("pt", "BR"));
		symbols.setCurrencySymbol("R$");
		symbols.setMonetaryDecimalSeparator(',');
		symbols.setGroupingSeparator('.');

		DecimalFormat df = new DecimalFormat("¤ #,##0.00", symbols);

		String valorFormatado = df.format(subtotalGeral);


		JSONArray itensParaFinalizar = new JSONArray();

		if (carrinhoAtualizado != null && !carrinhoAtualizado.isEmpty()) {

			for (ItemCarrinho item : carrinhoAtualizado) {

				subtotalGeral += item.getSubtotal();

				out.println("<div id=\"itemCarrinho_" + item.getProduto().getId()
						+ "\" class=\"d-flex bg-dark text-white rounded mb-3 p-2 align-items-center\">");

				out.println("<img src=\"exibirImagemProduto?id=" + item.getProduto().getId()
						+ "\" alt=\"Imagem\" class=\"me-2 rounded\" style=\"width: 80px; height: 80px; object-fit: cover;\">");

				out.println("<div class=\"flex-grow-1\">");

				out.println("<h6 class=\"mb-1\">" + item.getProduto().getDescricao() + "</h6>");

				out.println("<div class=\"d-flex justify-content-between align-items-center\">");

				out.println("<span>R$ " + df.format(item.getProduto().getPreco_de_venda()) + "</span>"); // Usando
																											// df.format
																											// aqui

				out.println("<span>Qtd: " + item.getQuantidade() + "</span>");

				out.println("</div>");

				out.println("</div>");

				out.println("<button type=\"button\" class=\"btn btn-danger btn-sm ms-2\" onclick=\"removerProduto("
						+ item.getProduto().getId() + ")\">");

				out.println(
						"<svg xmlns=\"http://www.w3.org/2000/svg\" width=\"16\" height=\"16\" fill=\"currentColor\" class=\"bi bi-trash\" viewBox=\"0 0 16 16\"> <path d=\"M5.5 5.5A.5.5 0 0 1 6 6v6a.5.5 0 0 1-1 0V6a.5.5 0 0 1 .5-.5zm2.5 0a.5.5 0 0 1 .5.5v6a.5.5 0 0 1-1 0V6a.5.5 0 0 1 .5-.5zm3 .5a.5.5 0 0 0-1 0v6a.5.5 0 0 0 1 0V6z\"/> <path d=\"M14.5 3a1 1 0 0 1-1 1H13v9a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2V4h-.5a1 1 0 0 1-1-1V2a1 1 0 0 1 1-1H6a1 1 0 0 1 1-1h2a1 1 0 0 1 1 1h3.5a1 1 0 0 1 1 1v1zM4.118 4 4 4.059V13a1 1 0 0 0 1 1h6a1 1 0 0 0 1-1V4.059L11.882 4H4.118zM2.5 3V2h11v1h-11z\"/></svg>");

				out.println("</button>");

				out.println("</div>");

				JSONObject itemJson = new JSONObject();

				itemJson.put("idProduto", item.getProduto().getId());

				itemJson.put("quantidade", item.getQuantidade());

				itemJson.put("precoUnitario", item.getProduto().getPreco_de_venda());

				itemJson.put("subtotal", item.getSubtotal());

				itensParaFinalizar.put(itemJson);

			}

		} else {

			out.println("<p class=\"text-white\">Carrinho vazio.</p>");

		}

		session.setAttribute("itens", itensParaFinalizar);

		out.println("<script>");

		out.println("document.getElementById('subtotalCarrinho').value = '" + df.format(subtotalGeral) + "';");

		out.println("</script>");

	}

// --- Nova lógica para listar pedidos do cliente ---

	private void listarPedidosCliente(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException, ClassNotFoundException, SQLException {

		response.setContentType("text/html; charset=UTF-8");

		PrintWriter out = response.getWriter();

		HttpSession session = request.getSession();

		String empresa = (String) session.getAttribute("empresa");

		Integer clienteId = (Integer) session.getAttribute("usuarioID");

		if (clienteId == null || empresa == null) {

			out.println(
					"<p class=\"text-danger\">Sessão expirada ou cliente não logado. Por favor, faça login novamente.</p>");

			return;

		}

		PedidosDAO pedidoDAO = new PedidosDAO(empresa);

		List<Pedidos> listaPedidos = pedidoDAO.listarPedidosPorCliente(clienteId);

// Para formatar moeda (ex: R$ 123,45)

		DecimalFormatSymbols symbols = new DecimalFormatSymbols(new Locale("pt", "BR"));

		symbols.setCurrencySymbol("R$");

		@SuppressWarnings("unused")
		DecimalFormat df = new DecimalFormat("¤ #,##0.00", symbols);

		if (listaPedidos != null && !listaPedidos.isEmpty()) {

			for (Pedidos pedido : listaPedidos) {

				String statusImageUrl = "";

				String statusText = pedido.getStatus();

				if ("Pendente".equalsIgnoreCase(statusText)) {

					statusImageUrl = request.getContextPath() + "/img/progress-pedido-pendente.png";

				} else if ("Confirmado".equalsIgnoreCase(statusText)
						|| "Pagamento Confirmado".equalsIgnoreCase(statusText)) {

					statusImageUrl = request.getContextPath() + "/img/progress-em-preparacao.png";

				} else if ("Enviado".equalsIgnoreCase(statusText)) {

					statusImageUrl = request.getContextPath() + "/img/progress-pedido-enviado.png";

				} else if ("Entregue".equalsIgnoreCase(statusText)) {

					statusImageUrl = request.getContextPath() + "/img/progress-pedido-entregue.png";

				} else {

					statusImageUrl = request.getContextPath() + "/img/progress-default.png";

				}

				out.println("<div class=\"card bg-secondary text-white mb-3 shadow-sm\">");

				out.println("<div class=\"card-header d-flex justify-content-between align-items-center\">");

				out.println("<h6 class=\"mb-0\">Pedido #" + pedido.getIdPedido() + " - Status: " + pedido.getStatus()
						+ "</h6>");

				out.println("<small>Data: " + pedido.getDataPeedido() + "</small>");

				out.println("</div>"); // card-header

				out.println("<div class=\"card-body\">");

				out.println("<p class=\"card-text mb-1\">Forma de Pagamento: " + pedido.getFormapagamento() + "</p>");

				if (pedido.getObservacoes() != null && !pedido.getObservacoes().isEmpty()) {

					out.println("<p class=\"card-text mb-1\">Observações: " + pedido.getObservacoes() + "</p>");

				}

				out.println("<div class=\"status-progress-bar text-center mt-3\">");

				out.println("<img src=\"" + statusImageUrl + "\" alt=\"Status do Pedido\" class=\"img-fluid\">");

				out.println("</div>");



				out.println("</div>"); // card-body

				out.println("</div>"); // card

			}

		} else {

			out.println("<p class=\"text-white text-center\">Você ainda não tem nenhum pedido.</p>");

		}

	}

	@SuppressWarnings("unused")
	private void CadClientePedido(HttpServletRequest request, HttpServletResponse response)
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

	protected void finalizarPedido(HttpServletRequest request, HttpServletResponse response)
	        throws ServletException, IOException, SQLException, ClassNotFoundException {

	    HttpSession session = request.getSession();
	    String empresa = (String) session.getAttribute("empresa");
	    Integer clienteId = (Integer) session.getAttribute("usuarioID");

	    if (clienteId == null) {
	        response.sendRedirect(request.getContextPath() + "/LoginExpirado.jsp");
	        return;
	    }

	    JSONArray itensArray = (JSONArray) session.getAttribute("itens");
	    if (itensArray == null || itensArray.length() == 0) {
	        response.sendRedirect(request.getContextPath() + "/ProdutosPedidos.jsp?erro="
	                + URLEncoder.encode("Carrinho vazio. Não é possível finalizar o pedido.", "UTF-8"));
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

	    // ✅ Tratamento seguro do subtotal vindo da tela (formato brasileiro)
	    String subtotalStr = request.getParameter("subtotal");

	    if (subtotalStr != null && !subtotalStr.trim().isEmpty()) {
	        try {
	            double subtotal = Double.parseDouble(subtotalStr);
	            novoPedido.setTotalPedido(subtotal);
	        } catch (NumberFormatException e) {
	            System.err.println("Subtotal inválido: " + subtotalStr);
	            response.sendRedirect(request.getContextPath() + "/ProdutosPedidos.jsp?erro=" +
	                URLEncoder.encode("Subtotal inválido", "UTF-8"));
	            return;
	        }
	    }


	    // Inicialização dos DAOs
	    PedidosDAO pedidoDAO = null;
	    ProdutosDAO daoProd = null;
	    ItensPedidoDAO daoit = null;

	    try {
	        pedidoDAO = new PedidosDAO(empresa);
	        daoProd = new ProdutosDAO(empresa);
	        daoit = new ItensPedidoDAO(empresa);

	        pedidoDAO.cadastrarPedido(novoPedido);

	        for (int i = 0; i < itensArray.length(); i++) {
	            JSONObject linha = itensArray.getJSONObject(i);

	            int quantidade = linha.getInt("quantidade");
	            double precoUnitario = linha.getDouble("precoUnitario");
	            int cdProduto = linha.getInt("idProduto");

	            Produtos produto = new Produtos();
	            produto.setId(cdProduto);

	            ItensPedidos itp = new ItensPedidos();
	            itp.setPedido(novoPedido);
	            itp.setProduto(produto);
	            itp.setQuantidade(quantidade);
	            itp.setPrecoUnitario(precoUnitario);

	            daoit.inserirItensPedidos(itp);

	            int qtd_estoque_atual = daoProd.retornaEstoqueAtual(produto.getId());
	            int qtd_atualizada = qtd_estoque_atual - quantidade;
	            daoProd.baixarEstoque(produto.getId(), qtd_atualizada);
	        }

	        // Limpa o carrinho da sessão
	        session.removeAttribute("carrinho");
	        session.removeAttribute("itens");

	        response.sendRedirect(request.getContextPath() + "/ProdutosPedidos.jsp?abrirModalPedidos=true&mensagem=" +
	                URLEncoder.encode("Pedido finalizado com sucesso!", "UTF-8"));

	    } catch (SQLException | ClassNotFoundException e) {
	        e.printStackTrace();
	        response.sendRedirect(request.getContextPath() + "/ProdutosPedidos.jsp?erro=" +
	                URLEncoder.encode("Erro ao processar o pedido: " + e.getMessage(), "UTF-8"));
	    }
	}

	// ✅ Função auxiliar para converter valores monetários do formato BR para Java
	private double parseValorMonetarioBR(String valor) {
	    try {
	        valor = valor.replace(".", "").replace(",", ".").trim();
	        return Double.parseDouble(valor);
	    } catch (Exception e) {
	        System.err.println("Erro ao converter valor monetário: '" + valor + "'");
	        return -1;
	    }
	}


	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		String action = request.getServletPath();

		System.out.println("doPost - Servlet Path: " + action);

		if (action.equals("/finalizarPedidoServlet")) {

			try {

				finalizarPedido(request, response);

			} catch (ClassNotFoundException | SQLException e) {

				e.printStackTrace();

				response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Erro interno ao finalizar o pedido.");

			}

		} else {

			System.err.println("doPost - Ação POST não reconhecida: " + action);

			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Ação POST não reconhecida para " + action);

		}

	}

}