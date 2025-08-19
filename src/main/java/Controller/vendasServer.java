package Controller;


import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.naming.NamingException;

import org.json.JSONArray;
import org.json.JSONObject;

import com.mercadopago.MercadoPagoConfig;
import com.mercadopago.client.preference.PreferenceClient;
import com.mercadopago.client.preference.PreferenceItemRequest;
import com.mercadopago.client.preference.PreferenceRequest;
import com.mercadopago.resources.preference.Preference;

import Conexao.ConectionFactory;
import DAO.ClientesDAO;
import DAO.ConfigPagamentoDAO;
import DAO.EmpresaDAO;
import DAO.ProdutosDAO;
import DAO.UsuarioDAO;
import DAO.VendasDAO;
import DAO.itensVendaDAO;
import Model.Clientes;
import Model.ConfigPagamento;
import Model.Empresa;
import Model.ItensVenda;
import Model.Produtos;
import Model.Usuario;
import Model.Vendas;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.util.JRLoader;


/**
 * Servlet implementation class vendasServer
 */

@WebServlet(urlPatterns = { "/selecionarClienteProdutos", "/inserirItens", "/InseirVendaEintens", "/PeriodoVenda",
		"/dia", "/maisVendidos","/exibirRelatorio","/lucroVenda" ,"/lucroPeriodo","/desconto","/relVenda"})
@MultipartConfig

public class vendasServer extends HttpServlet {
	private static final long serialVersionUID = 1L;
	 private static final Logger LOGGER = Logger.getLogger(vendasServer.class.getName());

	double total, subtotal, preco, meuPreco;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public vendasServer() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// Obtendo a sessão
		HttpSession session = request.getSession();
		String empresa = (String) session.getAttribute("empresa"); // Exemplo de atributo de sessão

		// Agora, você pode usar o valor da "empresa" em qualquer parte do seu código
		if (empresa != null) {
			System.out.println("Empresa selecionada: " + empresa);
		} else {
			System.out.println("Nenhuma empresa selecionada.");
		}

		String action = request.getServletPath();
		switch (action) {
		case "/selecionarClienteProdutos":
			try {
				selecionarClienteProd(request, response);
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
			break;
		case "/inserirItens":
			inserirItens(request, response);
			break;
		case "/InseirVendaEintens":
			inserirVendas(request, response);
			break;
		case "/PeriodoVenda":
			vendaPorPeriodo(request, response);
			break;
		case "/dia":
			vendaPorDia(request, response);
			break;
		case "/maisVendidos":
			maisVendidos(request, response);
			break;
		case "/exibirRelatorio":
			try {
				gerarRelatorio(request, response);
			} catch (ClassNotFoundException | ServletException | IOException | NamingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			break;
		case "/relVenda":
			try {
				relVenda(request, response);
			} catch (ClassNotFoundException | ServletException | IOException | NamingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			break;
		case "/lucroVenda":
			lucroVenda(request, response);
			break;
		case "/lucroPeriodo":
			lucroPeriodo(request, response);
			break;
		case "/desconto":
			descontoVenda(request, response);
			break;
		default:
			response.getWriter().append("Ação não reconhecida.");
			break;
		}
	}
	protected void descontoVenda(HttpServletRequest request, HttpServletResponse response) {
	    try {
	        // Obtém os parâmetros do formulário
	        String descontotela = request.getParameter("desconto");
	        String totalVenda = request.getParameter("totalVendaAtualizado");

	        if (descontotela != null && totalVenda != null) {
	            try {
	                // Conversão de strings para double
	                double descontoValor = Double.parseDouble(descontotela);
	                double vendaTela = Double.parseDouble(totalVenda);

	                // Calcula o valor final com desconto
	                double descontoFinal = vendaTela - descontoValor;

	                // Atribui o resultado como atributo na requisição
	                HttpSession session = request.getSession();
	                session.setAttribute("totalVendaAtualizado", descontoFinal);



	                // Encaminha a requisição para o JSP
	                RequestDispatcher dispatcher = request.getRequestDispatcher("realizarVendas.jsp");
	                dispatcher.forward(request, response);

	            } catch (NumberFormatException e) {
	                e.printStackTrace();
	                response.getWriter().println("Erro: valores inválidos para cálculo.") ;
	            }
	        } else {
	            response.getWriter().println("Erro: parâmetros 'desconto' e 'totalVenda' não enviados.");
	        }
	    } catch (Exception e) {
	        e.printStackTrace();
	        try {
				response.getWriter().println("Erro interno no servidor: " + e.getMessage());
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
	    }
	}



	private void lucroPeriodo(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		String datalucroinicio = request.getParameter("dataIniciallucro");
		String datalucrofim = request.getParameter("dataFinallucro");

		HttpSession session = request.getSession();
		String empresa = (String) session.getAttribute("empresa");

		if (datalucroinicio != null && datalucrofim != null) {
			String fomatoData = "dd/MM/yyyy";
			SimpleDateFormat sdf = new SimpleDateFormat(fomatoData);

			try {
				Date datainicalFormata = sdf.parse(datalucroinicio);
				Date datafinalFormata = sdf.parse(datalucrofim);
				VendasDAO dao = new VendasDAO(empresa);


				double lucroPeriodo = dao.lucroPorPeriod(datainicalFormata, datafinalFormata);

				request.setAttribute("totalLucro", lucroPeriodo);

				RequestDispatcher dispatcher = request.getRequestDispatcher("Home.jsp");
				dispatcher.forward(request, response);

			} catch (Exception e) {
				// TODO: handle exception
			}

		}

	}
	protected void lucroVenda(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String codigoVenda = request.getParameter("CodigoVenda");
		int idVenda = Integer.parseInt(codigoVenda);

		HttpSession session = request.getSession();
		String empresa = (String) session.getAttribute("empresa");

		try {


			VendasDAO dao = new VendasDAO(empresa);
			double lucroVenda = dao.lucroVenda(idVenda);

			request.setAttribute("lucro", lucroVenda);
			request.setAttribute("vendaCodigo", idVenda);


			RequestDispatcher dispatcher = request.getRequestDispatcher("Home.jsp");
			dispatcher.forward(request, response);

		} catch (Exception e) {
			e.printStackTrace();
			// Handle exception appropriately
		}

	}

	@SuppressWarnings({ "unused"})
	protected void gerarRelatorio(HttpServletRequest request, HttpServletResponse response)
	        throws ServletException, IOException, ClassNotFoundException, NamingException {

	    HttpSession session = request.getSession();
	    String empresa = (String) session.getAttribute("empresa");

	    if (empresa == null) {
	        response.getWriter().write("Empresa não fornecida.");
	        LOGGER.log(Level.WARNING, "Empresa não fornecida.");
	        return;
	    }

	    try (Connection connection = new ConectionFactory().getConnection(empresa)) {
	        
	        // 🚀 Garante que os dados mais recentes estão gravados no banco
	      

	        // ⏳ Adiciona um pequeno atraso para evitar problemas de sincronização
	        Thread.sleep(1000);

	        // 🔄 Caminho do arquivo .jasper no classpath
	        String jasperPath = "RelatorioJasper/novoComprovante.jasper";
	        
	        // 📂 Obtém o arquivo compilado .jasper do classpath
	        InputStream jasperStream = getClass().getClassLoader().getResourceAsStream(jasperPath);
	        
	        if (jasperStream == null) {
	            response.getWriter().write("Arquivo JASPER não encontrado: " + jasperPath);
	            LOGGER.log(Level.SEVERE, "Arquivo JASPER não encontrado: {0}", jasperPath);
	            return;
	        }

	        // 📌 Garante que o JasperReport está atualizado
	        JasperReport jasperReport = (JasperReport) JRLoader.loadObject(jasperStream);

	        // 🎯 Parâmetros do relatório
	        Map<String, Object> parametros = new HashMap<>();
	        UsuarioDAO usuarioDAO = new UsuarioDAO(empresa);
	        VendasDAO vendasDAO = new VendasDAO(empresa);
	        
	        // 🔍 Obtém os valores mais recentes para o relatório
	        Empresa empresaObj = usuarioDAO.retornCompany(new Empresa(), empresa, 0);
	        int cdVenda = vendasDAO.retornaUltimaVenda();
	        int cdEmpresa = (empresaObj != null) ? empresaObj.getId() : 0;
	        
	        parametros.put("cdEmpresa", cdEmpresa);
	        parametros.put("cdVenda", cdVenda);

	        // 📄 Preenchendo o relatório com os dados mais recentes
	        JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parametros, connection);

	        // 🚀 Evita cache para garantir que novas alterações sejam refletidas
	    

	        // 📂 Gera o PDF na memória
	        ByteArrayOutputStream pdfOutputStream = new ByteArrayOutputStream();
	        JasperExportManager.exportReportToPdfStream(jasperPrint, pdfOutputStream);
	        
	        byte[] pdfBytes = pdfOutputStream.toByteArray();

	        // 📡 Configura a resposta HTTP para exibir o PDF no navegador
	        response.setContentType("application/pdf");
	        response.setHeader("Content-Disposition", "inline; filename=relatorio_venda.pdf");
	        response.setContentLength(pdfBytes.length);

	        try (OutputStream outStream = response.getOutputStream()) {
	            outStream.write(pdfBytes);
	            outStream.flush();
	        }

	    } catch (SQLException | JRException | NamingException | InterruptedException e) {
	        LOGGER.log(Level.SEVERE, "Erro ao gerar relatório para a empresa: " + empresa, e);
	        response.getWriter().write("Erro ao gerar relatório: " + e.getMessage());
	    }
	}
	
	protected void relVenda(HttpServletRequest request, HttpServletResponse response)
	        throws ServletException, IOException, ClassNotFoundException, NamingException {

	    HttpSession session = request.getSession();
	    String empresa = (String) session.getAttribute("empresa");

	    if (empresa == null) {
	        response.getWriter().write("Empresa não fornecida.");
	        LOGGER.log(Level.WARNING, "Empresa não fornecida.");
	        return;
	    }

	    // 📌 Obtém o ID da venda da URL (enviado pelo JSP)
	    String vendaIDParam = request.getParameter("vendaID");
	    int cdVenda = 0;

	    if (vendaIDParam != null && !vendaIDParam.isEmpty()) {
	        try {
	            cdVenda = Integer.parseInt(vendaIDParam);
	        } catch (NumberFormatException e) {
	            LOGGER.log(Level.WARNING, "ID da venda inválido: " + vendaIDParam, e);
	            response.getWriter().write("ID da venda inválido.");
	            return;
	        }
	    }

	    if (cdVenda == 0) {
	        response.getWriter().write("ID da venda não foi fornecido corretamente.");
	        LOGGER.log(Level.WARNING, "ID da venda não foi fornecido corretamente.");
	        return;
	    }

	    try (Connection connection = new ConectionFactory().getConnection(empresa)) {

	        // 🔄 Caminho do arquivo .jasper no classpath
	        String jasperPath = "RelatorioJasper/vendaSelecionada.jasper";
	        InputStream jasperStream = getClass().getClassLoader().getResourceAsStream(jasperPath);

	        if (jasperStream == null) {
	            response.getWriter().write("Arquivo JASPER não encontrado: " + jasperPath);
	            LOGGER.log(Level.SEVERE, "Arquivo JASPER não encontrado: {0}", jasperPath);
	            return;
	        }

	        // 📌 Garante que o JasperReport está atualizado
	        JasperReport jasperReport = (JasperReport) JRLoader.loadObject(jasperStream);

	        // 🎯 Parâmetros do relatório
	        Map<String, Object> parametros = new HashMap<>();
	        UsuarioDAO usuarioDAO = new UsuarioDAO(empresa);

	        // 🔍 Obtém os valores mais recentes para o relatório
	        Empresa empresaObj = usuarioDAO.retornCompany(new Empresa(), empresa, 0);
	        int cdEmpresa = (empresaObj != null) ? empresaObj.getId() : 0;

	        parametros.put("cdEmpresa", cdEmpresa);
	        parametros.put("cdVenda", cdVenda);

	        System.out.println("Empresa selecionada: " + empresa);
	        System.out.println("Código da venda: " + cdVenda);

	        // 📄 Preenchendo o relatório com os dados mais recentes
	        JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parametros, connection);

	        // 📂 Gera o PDF na memória
	        ByteArrayOutputStream pdfOutputStream = new ByteArrayOutputStream();
	        JasperExportManager.exportReportToPdfStream(jasperPrint, pdfOutputStream);

	        byte[] pdfBytes = pdfOutputStream.toByteArray();

	        // 📡 Configura a resposta HTTP para exibir o PDF no navegador
	        response.setContentType("application/pdf");
	        response.setHeader("Content-Disposition", "inline; filename=relatorio_venda.pdf");
	        response.setContentLength(pdfBytes.length);

	        try (OutputStream outStream = response.getOutputStream()) {
	            outStream.write(pdfBytes);
	            outStream.flush();
	        }

	    } catch (SQLException | JRException e) {
	        LOGGER.log(Level.SEVERE, "Erro ao gerar relatório para a empresa: " + empresa, e);
	        response.getWriter().write("Erro ao gerar relatório: " + e.getMessage());
	    }
	}






	private void maisVendidos(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		String dataVendainicio = request.getParameter("dataVendainicio");
		String dataVendafim = request.getParameter("dataVendafim");

		HttpSession session = request.getSession();
		String empresa = (String) session.getAttribute("empresa");

		if (dataVendainicio != null && dataVendafim != null) {
			String fomatoData = "dd/MM/yyyy";
			SimpleDateFormat sdf = new SimpleDateFormat(fomatoData);

			try {
				Date datainicalFormata = sdf.parse(dataVendainicio);
				Date datafinalFormata = sdf.parse(dataVendafim);
				VendasDAO dao = new VendasDAO(empresa);


				ArrayList<ItensVenda> lista_2 = (ArrayList<ItensVenda>) dao.maisVendidos(datainicalFormata,
						datafinalFormata);

				request.setAttribute("maisVendidos", lista_2);

				RequestDispatcher dispatcher = request.getRequestDispatcher("Home.jsp");
				dispatcher.forward(request, response);

			} catch (Exception e) {
				// TODO: handle exception
			}

		}

	}

	private void vendaPorDia(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		String data = request.getParameter("data");

		HttpSession session = request.getSession();
		String empresa = (String) session.getAttribute("empresa");
		

		try {
			SimpleDateFormat dataVenda = new SimpleDateFormat("dd/MM/yyyy");
			Date dataVendaInf = dataVenda.parse(data);

			VendasDAO dao = new VendasDAO(empresa);
			double totalVenda = dao.retornaTotalVendaPorData(dataVendaInf);

			request.setAttribute("totalVenda2", totalVenda);
			request.setAttribute("data", data);

			RequestDispatcher dispatcher = request.getRequestDispatcher("Home.jsp");
			dispatcher.forward(request, response);

		} catch (Exception e) {
			e.printStackTrace();
			// Handle exception appropriately
		}

	}

	private void vendaPorPeriodo(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		String dataInicial = request.getParameter("dataInicial");
		String dataFinal = request.getParameter("dataFinal");

		HttpSession session = request.getSession();
		String empresa = (String) session.getAttribute("empresa");

		if (dataInicial != null && dataFinal != null) {
			String fomatoData = "dd/MM/yyyy";
			SimpleDateFormat sdf = new SimpleDateFormat(fomatoData);

			try {
				Date datainicalFormata = sdf.parse(dataInicial);
				Date datafinalFormata = sdf.parse(dataFinal);
				VendasDAO dao = new VendasDAO(empresa);
				ArrayList<Vendas> lista_2 = (ArrayList<Vendas>) dao.totalPorPeriodo(datainicalFormata,
						datafinalFormata);
				request.setAttribute("periodo", lista_2);

				RequestDispatcher dispatcher = request.getRequestDispatcher("Home.jsp");
				dispatcher.forward(request, response);

			} catch (Exception e) {
				e.printStackTrace();

			}
		}
	}

	private void inserirVendas(HttpServletRequest request, HttpServletResponse response)
	        throws ServletException, IOException {

	    HttpSession session = request.getSession();
	    Connection con = (Connection) request.getServletContext().getAttribute("conexao");

	    try {
	        // 🔹 Nome da base de dados (para todos os DAOs)
	        String empresaBase = (String) session.getAttribute("empresa"); // ex: "distribuidora"
	        if (empresaBase == null || empresaBase.isEmpty()) {
	            System.err.println("Erro: Nome da base da empresa não definido na sessão.");
	            response.sendRedirect("erroPagamento.jsp?msg=empresa-base-nao-definida");
	            return;
	        }

	        // 🔹 Nome da empresa real (pode vir do formulário ou fallback)
	        String empresaNome = request.getParameter("empresaNome"); // ex: "Distribuidora LTDA"
	        if (empresaNome == null || empresaNome.isEmpty()) {
	            empresaNome = empresaBase; // fallback
	        }

	        // 🔹 Busca ID da empresa usando DAO
	        EmpresaDAO empresaDao = new EmpresaDAO(empresaBase);
	        Integer empresaId = 1;
	        if (empresaId == null || empresaId == 0) {
	            empresaId = empresaDao.buscarIdPorNome(empresaNome);
	            if (empresaId == null || empresaId == 0) {
	                System.err.println("Erro: ID da empresa não encontrado para " + empresaNome);
	                response.sendRedirect("erroPagamento.jsp?msg=empresa-id-nao-encontrado");
	                return;
	            }
	            session.setAttribute("empresaId", empresaId);
	        }

	        // 🔹 ID do usuário
	        Integer usuarioID = (Integer) session.getAttribute("usuarioID");

	        // 🔹 Coleta de parâmetros do formulário
	        String idCliStr = request.getParameter("cliId");
	        String totalVendaStr = request.getParameter("iserirtotal");
	        String descontoStr = request.getParameter("desconto");
	        String formaPagamento = request.getParameter("formaPagamento");

	        int idCli = (idCliStr != null && !idCliStr.isEmpty()) ? Integer.parseInt(idCliStr) : 0;
	        double totalVenda = (totalVendaStr != null && !totalVendaStr.isEmpty()) ? Double.parseDouble(totalVendaStr) : 0.0;
	        double desconto = (descontoStr != null && !descontoStr.isEmpty()) ? Double.parseDouble(descontoStr) : 0.0;

	        // 🔹 Criação do objeto Vendas
	        Vendas obj = new Vendas();
	        if (idCli > 0) {
	            Clientes objCli = new Clientes();
	            objCli.setId(idCli);
	            obj.setCliente(objCli);
	        }
	        obj.setData_venda(request.getParameter("data"));
	        obj.setTotal_venda(totalVenda);
	        obj.setObs(request.getParameter("observacao"));
	        obj.setDesconto(desconto);
	        obj.setFormaPagamento(formaPagamento);

	        if (usuarioID != null && usuarioID > 0) {
	            Usuario objUser = new Usuario();
	            objUser.setId(usuarioID);
	            obj.setUsuario(objUser);
	        }

	        // 🔹 Inserir venda no banco
	        VendasDAO dao = new VendasDAO(empresaBase);
	        dao.cadastrarVenda(obj);
	        obj.setId(dao.retornaUltimaVenda());

	        // 🔹 Processa itens da venda
	        JSONArray itensArray = (JSONArray) session.getAttribute("itens");
	        if (itensArray != null && itensArray.length() > 0) {
	            for (int i = 0; i < itensArray.length(); i++) {
	                JSONObject linha = itensArray.getJSONObject(i);

	                int idProdVenda = Integer.parseInt(linha.getString("idProd"));
	                int qtdProd = Integer.parseInt(linha.getString("qtdProd"));
	                double subItens = Double.parseDouble(linha.getString("subtotal"));

	                ProdutosDAO dao_produto = new ProdutosDAO(empresaBase);
	                itensVendaDAO daoitem = new itensVendaDAO(empresaBase);

	                Produtos objp = new Produtos();
	                ItensVenda itens = new ItensVenda();

	                itens.setVenda(obj);
	                objp.setId(idProdVenda);
	                itens.setProduto(objp);
	                itens.setQtd(qtdProd);
	                itens.setSubtotal(subItens);

	                // Atualiza estoque
	                int qtd_estoque = dao_produto.retornaEstoqueAtual(objp.getId());
	                dao_produto.baixarEstoque(objp.getId(), qtd_estoque - qtdProd);

	                // Insere item
	                daoitem.cadastraItem(itens);
	            }

	            // 🔹 Limpa sessão de itens e valores
	            session.removeAttribute("itens");
	            session.removeAttribute("desconto");
	            session.removeAttribute("totalVenda");
	            session.removeAttribute("totalVendaAtualizado");
	        }

	        // 🔹 Integração Mercado Pago
	        if ("MERCADOPAGO".equalsIgnoreCase(formaPagamento)) {
	            ConfigPagamentoDAO cfgDao = new ConfigPagamentoDAO(empresaBase);
	            ConfigPagamento cfg = cfgDao.buscarPorEmpresa(empresaId);

	            if (cfg == null || cfg.getAccessToken() == null || cfg.getAccessToken().isEmpty()) {
	                System.err.println("Erro: Configuração de pagamento inválida para empresaId " + empresaId);
	                response.sendRedirect("erroPagamento.jsp?msg=access-token-invalido");
	                return;
	            }

	            try {
	                MercadoPagoConfig.setAccessToken(cfg.getAccessToken());
	                PreferenceClient client = new PreferenceClient();

	                PreferenceItemRequest itemRequest = PreferenceItemRequest.builder()
	                        .title("Venda #" + obj.getId())
	                        .quantity(1)
	                        .unitPrice(new BigDecimal(obj.getTotal_venda()))
	                        .currencyId("BRL")
	                        .build();

	                PreferenceRequest prefRequest = PreferenceRequest.builder()
	                        .items(Collections.singletonList(itemRequest))
	                        .build();

	                Preference pref = client.create(prefRequest);
	                response.sendRedirect(pref.getInitPoint());
	                return;

	            } catch (Exception e) {
	                System.err.println("Erro na integração com Mercado Pago:");
	                e.printStackTrace();
	                response.sendRedirect("erroPagamento.jsp?msg=erro-geral-mp");
	                return;
	            }
	        }

	        // 🔹 Se não for Mercado Pago, redireciona para página de vendas
	        response.sendRedirect("realizarVendas.jsp");

	    } catch (NumberFormatException e) {
	        System.err.println("Erro de conversão de número. Verifique os parâmetros.");
	        e.printStackTrace();
	        response.sendRedirect("erroPagamento.jsp?msg=formato-numero-invalido");
	    } catch (Exception e) {
	        System.err.println("Erro geral no processo de vendas.");
	        e.printStackTrace();
	        response.sendRedirect("erroPagamento.jsp?msg=erro-geral");
	    }
	    
	    
	}


	private void inserirItens(HttpServletRequest request, HttpServletResponse response)
	        throws ServletException, IOException {
	    HttpSession session = request.getSession();

	    try {
	        BufferedReader reader = request.getReader();
	        StringBuilder sb = new StringBuilder();
	        String line;
	        while ((line = reader.readLine()) != null) {
	            sb.append(line);
	        }
	        JSONObject itemJson = new JSONObject(sb.toString());

	        String idProd = itemJson.getString("idProd");
	        String desProd = itemJson.getString("desProd");
	        String qtdProd = itemJson.getString("qtdProd");
	        String precoProd = itemJson.getString("precoProd");
	        String precoMeu = itemJson.getString("compraProd");

	        if (qtdProd != null) {
	            int qtdPrdo = Integer.parseInt(qtdProd);
	            double preco = Double.parseDouble(precoProd);
	            double meuPreco = Double.parseDouble(precoMeu);

	            double subtotal = qtdPrdo * preco;

	            double total = 0.0;
	            if (session.getAttribute("totalVendaAtualizado") != null) {
	                total = (double) session.getAttribute("totalVendaAtualizado");
	            }

	            total += subtotal;

	            String newRow = "<tr>" + "<td>" + idProd + "</td>" + "<td>" + desProd + "</td>" + "<td>" + qtdProd
	                    + "</td>" + "<td>" + precoProd + "</td>" + "<td>" + subtotal + "</td>" + "</tr>";

	            JSONObject newItem = new JSONObject();

	            newItem.put("idProd", idProd);
	            newItem.put("desProd", desProd);
	            newItem.put("qtdProd", qtdProd);
	            newItem.put("precoProd", precoProd);
	            newItem.put("subtotal", String.valueOf(subtotal));
	            newItem.put("totalVendaAtualizado", String.valueOf(total));

	            JSONArray itens = (JSONArray) session.getAttribute("itens");

	            if (itens == null) {
	                itens = new JSONArray();
	            }

	            itens.put(newItem);

	            session.setAttribute("itens", itens);
	            session.setAttribute("totalVendaAtualizado", total);

	            PrintWriter out = response.getWriter();
	            out.println(newRow);

	        }

	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	}
	private void selecionarClienteProd(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException, ClassNotFoundException {
		String cpfCli = request.getParameter("cliCpf");
		String idProdStr = request.getParameter("idProd");
		int idProd = Integer.parseInt(idProdStr);
		HttpSession session = request.getSession();
		String empresa = (String) session.getAttribute("empresa");




		try {

			Produtos prod = new Produtos();
			ProdutosDAO prodDAO = new ProdutosDAO(empresa);
			Clientes cli = new Clientes();
			ClientesDAO cliDAO = new ClientesDAO(empresa);

			cli = cliDAO.consultarClientesPorcpf(cpfCli);
			request.setAttribute("cliId", cli.getId());
			request.setAttribute("cliNome", cli.getNome());
			request.setAttribute("cliCpf", cli.getCpf());
			request.setAttribute("cliEndereco", cli.getEndereco());
			request.setAttribute("cliNumero", cli.getNumero());
			prod = prodDAO.consultarPorCodigo(idProd);
			request.setAttribute("idProd", prod.getId());
			request.setAttribute("desProd", prod.getDescricao());
			request.setAttribute("compraProd", prod.getPreco_de_compra());
			request.setAttribute("precoProd", prod.getPreco_de_venda());

			RequestDispatcher rd = request.getRequestDispatcher("realizarVendas.jsp");
			rd.forward(request, response);

		} catch (Exception e) {

		}

		session.isNew();

	}


	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		doGet(request, response);
	}
	

}