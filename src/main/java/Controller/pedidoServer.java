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
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import Model.Clientepedido;
import Model.Produtos;
import DAO.ClientesPedidosDAO;
import DAO.ProdutosDAO;

/**
 * Servlet implementation class pedidoServer
 */
@WebServlet(urlPatterns = {"/pedidoServer","/selecionarVendaCarrinho"})
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
        String action = request.getServletPath();
        String acao = request.getParameter("acao");
        
        System.out.println("Servlet Path: " + action);
        System.out.println("Acao: " + acao); // Adicionado para debug
        
        HttpSession session = request.getSession();
        String empresa = (String) session.getAttribute("empresa");

        if (action.equals("/pedidoServer")) {
            CadClientePedido(request, response);
        } else if (action.equals("/selecionarVendaCarrinho")) {
            // Usa o parâmetro 'acao' para decidir qual método chamar
            if ("remover".equals(acao)) {
                removerDoCarrinho(request, response);
            } else {
                try {
                    pedCarrinho(request, response);
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
    }

	private void pedCarrinho(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException, ClassNotFoundException {
	    HttpSession session = request.getSession();
	    String empresa = (String) session.getAttribute("empresa");

	    int id = Integer.parseInt(request.getParameter("id"));
	    int qtd = Integer.parseInt(request.getParameter("qtd"));

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

	    // Retorna o HTML completo do carrinho atualizado para o modal
	    response.setContentType("text/html; charset=UTF-8");
	    PrintWriter out = response.getWriter();

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
	            out.println("<span>R$ " + String.format("%.2f", p.getPreco_de_venda()).replace('.', ',') + "</span>");
	            out.println("<span>Qtd: " + p.getQtd_estoque() + "</span>");
	            out.println("</div>");
	            out.println("</div>");
	            // Adicionei o botão de remover aqui
	            out.println("<button type=\"button\" class=\"btn btn-danger btn-sm ms-2\" onclick=\"removerProduto(" + p.getId() + ")\">");
	            out.println("<svg xmlns=\"http://www.w3.org/2000/svg\" width=\"16\" height=\"16\" fill=\"currentColor\" class=\"bi bi-trash\" viewBox=\"0 0 16 16\"> <path d=\"M5.5 5.5A.5.5 0 0 1 6 6v6a.5.5 0 0 1-1 0V6a.5.5 0 0 1 .5-.5zm2.5 0a.5.5 0 0 1 .5.5v6a.5.5 0 0 1-1 0V6a.5.5 0 0 1 .5-.5zm3 .5a.5.5 0 0 0-1 0v6a.5.5 0 0 0 1 0V6z\"/> <path d=\"M14.5 3a1 1 0 0 1-1 1H13v9a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2V4h-.5a1 1 0 0 1-1-1V2a1 1 0 0 1 1-1H6a1 1 0 0 1 1-1h2a1 1 0 0 1 1 1h3.5a1 1 0 0 1 1 1v1zM4.118 4 4 4.059V13a1 1 0 0 0 1 1h6a1 1 0 0 0 1-1V4.059L11.882 4H4.118zM2.5 3V2h11v1h-11z\"/></svg>");
	            out.println("</button>");
	            out.println("</div>");
	        }
	    } else {
	        out.println("<p class=\"text-white\">Carrinho vazio.</p>");
	    }

	    // Adiciona o subtotal no final do HTML retornado
	    out.println("<script>");
	    out.println("document.getElementById('subtotalCarrinho').value = 'R$ " + String.format(Locale.US, "%.2f", subtotal).replace('.', ',') + "';");
	    out.println("</script>");
	}
	
	private void removerDoCarrinho(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	    HttpSession session = request.getSession();
	    int id = Integer.parseInt(request.getParameter("id"));

	    List<Produtos> carrinho = (List<Produtos>) session.getAttribute("carrinho");
	    if (carrinho != null) {
	        carrinho.removeIf(p -> p.getId() == id);
	        session.setAttribute("carrinho", carrinho);
	    }
	    
	    // Agora, retorne o HTML atualizado do carrinho novamente, assim como no método pedCarrinho
	    response.setContentType("text/html; charset=UTF-8");
	    PrintWriter out = response.getWriter();
	    
	    // Recalcule o subtotal
	    double subtotal = 0.0;
	    if (carrinho != null && !carrinho.isEmpty()) {
	        for (Produtos p : carrinho) {
	            double totalItem = p.getPreco_de_venda() * p.getQtd_estoque();
	            subtotal += totalItem;

	            out.println("<div id=\"itemCarrinho_" + p.getId() + "\" class=\"d-flex bg-dark text-white rounded mb-3 p-2 align-items-center\">");
	            out.println("<img src=\"exibirImagemProduto?id=" + p.getId() + "\" alt=\"Imagem\" class=\"me-2 rounded\" style=\"width: 80px; height: 80px; object-fit: cover;\">");
	            out.println("<div class=\"flex-grow-1\">");
	            out.println("<h6 class=\"mb-1\">" + p.getDescricao() + "</h6>");
	            out.println("<div class=\"d-flex justify-content-between align-items-center\">");
	            out.println("<span>R$ " + String.format("%.2f", p.getPreco_de_venda()).replace('.', ',') + "</span>");
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
	    out.println("document.getElementById('subtotalCarrinho').value = 'R$ " + String.format(Locale.US, "%.2f", subtotal).replace('.', ',') + "';");
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

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
