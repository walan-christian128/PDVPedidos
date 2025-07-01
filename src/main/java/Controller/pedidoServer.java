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
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import Model.Clientepedido;
import Model.Pedidos;
import Model.Produtos;
import DAO.ClientesPedidosDAO;
import DAO.PedidosDAO;
import DAO.ProdutosDAO;

/**
 * Servlet implementation class pedidoServer
 */
@WebServlet(urlPatterns = {"/pedidoServer","/selecionarVendaCarrinho","/finalizarPedidoServlet"})
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
        String acao = request.getParameter("acao"); // Pega o parâmetro 'acao'

        if ("remover".equals(acao)) {
            // Lógica para remover um produto do carrinho
            // Você só precisa do 'id' aqui, não da 'qtd'

            String idParam = request.getParameter("id");
            if (idParam != null && !idParam.isEmpty()) {
                try {
                    int id = Integer.parseInt(idParam);
                    // Chame o método que remove o item do carrinho na sessão
                    removerItemDoCarrinho(request, response, id);
                } catch (NumberFormatException e) {
                    response.sendError(HttpServletResponse.SC_BAD_REQUEST, "ID de produto inválido para remoção.");
                }
            } else {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "ID do produto não fornecido para remoção.");
            }

        } else if ("ver".equals(acao)) {
            // Lógica para apenas exibir o carrinho (não adiciona nem remove)
            exibirCarrinho(request, response);

        } else {
            // Ação padrão: adicionar um produto ao carrinho
            // Aqui você precisa de 'id' e 'qtd'

            String idParam = request.getParameter("id");
            String qtdParam = request.getParameter("qtd");

            if (idParam != null && !idParam.isEmpty() && qtdParam != null && !qtdParam.isEmpty()) {
                try {
                    int id = Integer.parseInt(idParam);
                    int qtd = Integer.parseInt(qtdParam);
                    // Chame o método que adiciona/atualiza o item no carrinho
                    try {
						adicionarOuAtualizarCarrinho(request, response, id, qtd);
					} catch (ClassNotFoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (ServletException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
                } catch (NumberFormatException e) {
                    response.sendError(HttpServletResponse.SC_BAD_REQUEST, "ID ou quantidade inválidos para adicionar ao carrinho.");
                }
            } else {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "ID ou quantidade não fornecidos para adicionar ao carrinho.");
            }
        }
    }

    private void adicionarOuAtualizarCarrinho(HttpServletRequest request, HttpServletResponse response, int id, int qtd) throws ServletException, IOException, ClassNotFoundException {
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
        // Agora, após adicionar/atualizar, sempre exiba o carrinho atualizado
        renderizarCarrinhoHTML(request, response);
    }

    // Novo método para remover item do carrinho
    private void removerItemDoCarrinho(HttpServletRequest request, HttpServletResponse response, int idParaRemover) throws ServletException, IOException {
        HttpSession session = request.getSession();
        List<Produtos> carrinho = (List<Produtos>) session.getAttribute("carrinho");

        if (carrinho != null) {
            carrinho.removeIf(p -> p.getId() == idParaRemover);
            session.setAttribute("carrinho", carrinho);
        }
        // Após remover, sempre exiba o carrinho atualizado
        renderizarCarrinhoHTML(request, response);
    }

    // Novo método para apenas exibir o carrinho
    private void exibirCarrinho(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Apenas renderiza o HTML do carrinho atual, sem modificar o carrinho
        renderizarCarrinhoHTML(request, response);
    }


    // Método auxiliar para renderizar o HTML do carrinho (evita duplicação de código)
    private void renderizarCarrinhoHTML(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
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
                out.println("<span>R$ " + String.format(Locale.getDefault(), "%.2f", p.getPreco_de_venda()).replace('.', ',') + "</span>"); // Usar Locale.getDefault() ou Locale.forLanguageTag("pt-BR") para formatação correta.
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
        // Use Locale.getDefault() ou Locale.forLanguageTag("pt-BR") no Java para formatar corretamente para o Brasil
        out.println("document.getElementById('subtotalCarrinho').value = 'R$ " + String.format(Locale.getDefault(), "%.2f", subtotal).replace('.', ',') + "';");
        out.println("</script>");
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
	protected void finalizarPedido(HttpServletRequest request, HttpServletResponse response)
	        throws ServletException, IOException {

	    HttpSession session = request.getSession();
	    String empresa = (String) session.getAttribute("empresa");

	    if (empresa == null || empresa.isEmpty()) {
	        response.sendRedirect("LoginExpirado.jsp");
	        return;
	    }

	    int clienteId = 0; // Valor padrão inicial

	    try {
	        // Tenta obter o ID do cliente da sessão primeiro
	        Object usuarioIDObj = session.getAttribute("usuarioID"); // O nome do atributo que você usa no login
	        if (usuarioIDObj instanceof Integer) { // Verifica se é um Integer
	            clienteId = (Integer) usuarioIDObj;
	            System.out.println("ID do cliente obtido da sessão: " + clienteId);
	        } else {
	            System.out.println("ID do cliente não encontrado na sessão ou não é um Integer.");
	            // Se não encontrou na sessão, ou não é um Integer, ainda pode tentar do parâmetro
	            String clienteIdParam = request.getParameter("clienteId");
	            System.out.println("ID do cliente param do formulário: " + clienteIdParam);
	            if (clienteIdParam != null && !clienteIdParam.isEmpty()) {
	                clienteId = Integer.parseInt(clienteIdParam);
	            }
	        }

	        // Se após ambas as tentativas o clienteId ainda for 0 (ou inválido para o seu negócio)
	        if (clienteId <= 0) { // Mudamos para <=0 para garantir que não seja 0 ou negativo
	            throw new IllegalArgumentException("ID do cliente não fornecido ou inválido. Por favor, faça login novamente.");
	        }

	        // 2. Criar o objeto Cliente para associar ao Pedido
	        Clientepedido clientePedido = new Clientepedido(); // Certifique-se de usar a classe correta, Clientepedido
	        clientePedido.setId(clienteId);

	        // 3. Obter a data atual do pedido
	        LocalDateTime now = LocalDateTime.now();
	        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
	        String dataPedido = now.format(formatter);

	        // 4. Definir o status inicial do pedido
	        String status = "Pendente";

	        // 5. Obter observações
	        String observacoes = request.getParameter("observacoes");
	        if (observacoes == null) {
	            observacoes = "";
	        }
	        String pagamento = request.getParameter("formaPagamento");
	        if (pagamento == null) {
	        	pagamento = "";
	        }

	        // 6. Criar o objeto Pedidos
	        Pedidos pedido = new Pedidos();
	        pedido.setClientepedido(clientePedido);
	        pedido.setDataPeedido(dataPedido);
	        pedido.setStatus(status);
	        pedido.setObservacoes(observacoes);
	        pedido.setFormapagamento(pagamento);
	       

	        // 7. Chamar o DAO para cadastrar o pedido
	        PedidosDAO pedidoDAO = new PedidosDAO(empresa);
	        pedidoDAO.cadastrarPedido(pedido);

	        // --- Adicional: Cadastrar os Itens do Pedido ---
	        // Você vai precisar de um DAO e um método para cadastrar os itens do carrinho
	        // associados a este pedido.
	        List<Produtos> carrinho = (List<Produtos>) session.getAttribute("carrinho");
	        if (carrinho != null && !carrinho.isEmpty()) {
	            // Obtenha o ID do pedido recém-cadastrado (se seu DAO retornar o ID, use-o)
	            // Ou, se seu DAO de itens de pedido tiver um método que receba o pedido e o carrinho
	            
	            // Exemplo conceitual (você precisará implementar o DAO e a classe ItensPedido)
	            // ItensPedidoDAO itensPedidoDAO = new ItensPedidoDAO(empresa);
	            // for (Produtos p : carrinho) {
	            //     ItensPedido item = new ItensPedido();
	            //     item.setPedido(pedido); // O objeto Pedidos já tem o ID do pedido recém-criado
	            //     item.setProduto(p);
	            //     item.setQuantidade(p.getQtd_estoque()); // Qtd_estoque aqui é a qtd no carrinho
	            //     item.setPrecoUnitario(p.getPreco_de_venda());
	            //     itensPedidoDAO.cadastrarItemPedido(item);
	            // }
	        }

	        // 8. Limpar o carrinho da sessão após finalizar o pedido
	        session.removeAttribute("carrinho");

	        // 9. Redirecionar para uma página de sucesso
	        response.sendRedirect("ProdutosPedidos.jsp");

	    } catch (NumberFormatException e) {
	        e.printStackTrace();
	        response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Formato de ID do cliente inválido.");
	    } catch (IllegalArgumentException e) {
	        e.printStackTrace();
	        response.sendError(HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
	    } catch (Exception e) {
	        e.printStackTrace();
	        response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Erro inesperado ao finalizar o pedido: " + e.getMessage());
	    }
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	// Este é o método doPost do seu servlet pedidoServer.java
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	    // Captura o caminho da requisição (Servlet Path) para identificar a ação
	    String action = request.getServletPath(); 

	    System.out.println("doPost - Servlet Path: " + action); // Adicionado para depuração

	    // Verifica qual ação foi solicitada via POST
	    if (action.equals("/finalizarPedidoServlet")) { // Este é o mapeamento que você definiu para o formulário de finalizar pedido
	        finalizarPedido(request, response); // Chama o método para finalizar o pedido
	    } 
	    // Você pode adicionar outras condições 'else if' aqui se tiver outras ações POST
	    // que este servlet deve lidar. Por exemplo:
	    // else if (action.equals("/algumaOutraAcaoPost")) {
	    //    // Chame um método correspondente a essa ação
	    //    algumaOutraAcaoPost(request, response);
	    // }
	    else {
	        // Se a ação não for reconhecida, você pode enviar um erro ou uma mensagem
	        System.err.println("doPost - Ação POST não reconhecida: " + action);
	        response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Ação POST não reconhecida para " + action);
	    }
	}
}
