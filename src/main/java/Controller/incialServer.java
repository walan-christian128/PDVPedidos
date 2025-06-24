package Controller;

import java.awt.Image;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Properties;

import javax.imageio.ImageIO;
import javax.naming.NamingException;

import DAO.UsuarioDAO;
import DAO.VendasDAO;
import DAO.createData;
import DAO.itensVendaDAO;
import Model.Empresa;
import Model.ItensVenda;
import Model.Usuario;
import Model.Vendas;
import jakarta.mail.Message.RecipientType;
import jakarta.mail.MessagingException;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.Part;

@WebServlet(urlPatterns = { "/selecionarVenda", "/totalVendas", "/CadastroUserEmpresa", "/RecuperaSenhaServlet",
		"/AtualizaçãoSenha" })
@MultipartConfig(
		fileSizeThreshold = 1024 * 1024 * 2, // 2MB - Tamanho do arquivo na memória antes de gravar no disco
	    maxFileSize = 1024 * 1024 * 5, // 5MB - Tamanho máximo do arquivo permitido
	    maxRequestSize = 1024 * 1024 * 10 // 10MB - Tamanho máximo da requisição (arquivo + outros dados)
		)
public class incialServer extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection con;

	public incialServer() {
		super();
		
		
	}

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		String action = request.getServletPath();
		System.out.println("Ação recebida: " + action);
		if (action.equals("/selecionarVenda")) {
			itensPorvenda(request, response);
		} else if (action.equals("/CadastroUserEmpresa")) {
			try {
				createBase(request, response);
			} catch (ServletException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		} else if (action.equals("/RecuperaSenhaServlet")) {
			try {
				enviarEmail(request, response);
			} catch (ServletException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		} else if (action.equals("/AtualizaçãoSenha")) {
			try {
				try {
					atualizaSenha(request, response);
				} catch (NamingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} catch (ServletException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
	}

	private void atualizaSenha(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException, SQLException, ClassNotFoundException, NamingException {
		String senha = request.getParameter("senha");
		String senha2 = request.getParameter("senha2");
		String email = request.getParameter("email");
		String empresa = request.getParameter("empresa");

		if (senha != null && !senha.trim().isEmpty()) {
			Usuario uso = new Usuario();

			uso.setSenha(senha);
			uso.setEmail(email);

			if (senha .equals(senha2) ) {
				UsuarioDAO dao = new UsuarioDAO(empresa);
				dao.recuperaSenha(senha, email, empresa);
				request.setAttribute("ok", "senha alterada com sucesso");
				RequestDispatcher rd = request.getRequestDispatcher("Login.jsp");
				rd.forward(request, response);

			} else if (senha != senha2) {

				request.setAttribute("erro","Campo confirmação de senha diferente do campo nova senha verifique o valor e digite novamente");
				RequestDispatcher rd = request.getRequestDispatcher("RedefinirSenha.jsp");
				rd.forward(request, response);
			}
		}

	}

	private void enviarEmail(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException, SQLException, ClassNotFoundException {
		String email = request.getParameter("email");
		String empresa = request.getParameter("empresa");

		try {
			// Verifica se o email existe no banco de dados
			UsuarioDAO usuarioDAO = new UsuarioDAO(empresa);
			boolean emailExiste = usuarioDAO.enviaEmail(email, empresa);

			if (emailExiste) {
				// Enviar OTP por e-mail
				String to = email;
				String resetLink = "http://192.168.1.2:8080/PDV/RedefinirSenha.jsp"; // Gera o
																												// OTP
																												// aqui

				// Configurações do servidor de e-mail
				Properties props = new Properties();
				props.put("mail.smtp.host", "smtp.gmail.com");
				props.put("mail.smtp.port", "587");
				props.put("mail.smtp.auth", "true");
				props.put("mail.smtp.starttls.enable", "true");

				// Autenticação para envio de e-mail
				Session session = Session.getInstance(props, new jakarta.mail.Authenticator() {
					@Override
					protected PasswordAuthentication getPasswordAuthentication() {
						return new PasswordAuthentication("walancristiano@gmail.com", "kjtd hzzx syze ysvo"); // Use o
																												// seu
																												// e-mail
																												// e
																												// senha
					}
				});

				try {
					// Criando a mensagem de e-mail
					MimeMessage message = new MimeMessage(session);
					message.setFrom(new InternetAddress("walancristiano@gmail.com")); // E-mail do remetente
					message.addRecipient(RecipientType.TO, new InternetAddress(to)); // E-mail do
																									// destinatário
					message.setSubject("Recuperação de Senha");
					message.setText("Click no link para redefinição de senha: " + resetLink);

					// Enviando o e-mail
					Transport.send(message);
					System.out.println("E-mail enviado com sucesso");

					// Define a mensagem de sucesso na requisição
					request.setAttribute("ok", "Email enviado com sucesso.");

					// Encaminha a requisição para o JSP
					RequestDispatcher rd = request.getRequestDispatcher("RecuperarSenha.jsp");
					rd.forward(request, response);

				} catch (MessagingException e) {
					e.printStackTrace();
					request.setAttribute("erro", "Falha ao enviar o e-mail.");
					RequestDispatcher rd = request.getRequestDispatcher("RecuperarSenha.jsp");
					rd.forward(request, response);
				}

			} else {
				// Caso o e-mail ou a empresa estejam incorretos
				request.setAttribute("erro", "Email ou empresa incorretos.");
				RequestDispatcher rd = request.getRequestDispatcher("RecuperarSenha.jsp");
				rd.forward(request, response);
			}

		} catch (Exception e) {
			e.printStackTrace();
			request.setAttribute("erro", "Ocorreu um erro ao processar a solicitação.");
			RequestDispatcher rd = request.getRequestDispatcher("RecuperarSenha.jsp");
			rd.forward(request, response);
		}
	}

	@SuppressWarnings("static-access")
	 protected void createBase(HttpServletRequest request, HttpServletResponse response)
	            throws ServletException, IOException {

	        String empresa = request.getParameter("base");

	        if (empresa != null && !empresa.trim().isEmpty()) {
	            try {
	                // Criar banco e tabelas
	                createData data = new createData(empresa);

	                // Coletar dados do usuário
	                String nomeUsuario = request.getParameter("nome");
	                String usuarioTelefone = request.getParameter("telefone");
	                String usuarioEmail = request.getParameter("email");
	                String usuarioSenha = request.getParameter("senha");
	                String empresaNome = request.getParameter("nomeEmpresa");
	                String empresaCnpj = request.getParameter("empresaCnpj");
	                String empresaEndereco = request.getParameter("empresaEdereco");

	                byte[] logoBytes = null;
	                Image logoImage = null;

	                // Verifica se há uma imagem na requisição
	                if (request.getContentType() != null && request.getContentType().toLowerCase().startsWith("multipart/")) {
	                    Part filePart = request.getPart("logo");

	                    if (filePart != null && filePart.getSize() > 0) {
	                        InputStream inputStream = filePart.getInputStream();
	                        logoBytes = inputStream.readAllBytes(); // Converte para byte[]
	                        logoImage = converterImagem(logoBytes); // Converte para java.awt.Image
	                    }
	                }

	                if (nomeUsuario != null && !nomeUsuario.trim().isEmpty()) {
	                    // Criar objetos Empresa e Usuário
	                    Usuario uso = new Usuario();
	                    Empresa emp = new Empresa();

	                    uso.setNome(nomeUsuario);
	                    uso.setTelefone(usuarioTelefone);
	                    uso.setEmail(usuarioEmail);
	                    uso.setSenha(usuarioSenha);

	                    if (empresaNome != null && !empresaNome.trim().isEmpty()) {
	                        emp.setNome(empresaNome);
	                        emp.setCnpj(empresaCnpj);
	                        emp.setEndereco(empresaEndereco);
	                        emp.setLogo(logoBytes);
	                    }

	                    // Inserir empresa e usuário no banco
	                    data.inserirEmpresaUsuario(emp, uso);
	                }

	                // Passar imagem para JasperReports se necessário
	                HashMap<String, Object> parametros = new HashMap<>();
	                parametros.put("logo", logoImage);

	                // Redireciona para a página de login
	                RequestDispatcher rd = request.getRequestDispatcher("Login.jsp");
	                rd.forward(request, response);

	            } catch (Exception e) {
	                e.printStackTrace();
	                request.setAttribute("errorMessage", "Ocorreu um erro ao criar o banco de dados e/ou inserir o usuário.");
	                RequestDispatcher rd = request.getRequestDispatcher("error.jsp");
	                rd.forward(request, response);
	            }
	        } else {
	            request.setAttribute("errorMessage", "O nome da base de dados não pode ser vazio.");
	            RequestDispatcher rd = request.getRequestDispatcher("error.jsp");
	            rd.forward(request, response);
	        }
	    }

	    /**
	     * Converte um array de bytes em um objeto java.awt.Image
	     */
	    private Image converterImagem(byte[] imagemBytes) {
	        try {
	            ByteArrayInputStream is = new ByteArrayInputStream(imagemBytes);
	            return ImageIO.read(is);
	        } catch (Exception e) {
	            e.printStackTrace();
	            return null;
	        }
	    }

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doGet(request, response);
	}

	protected void Vendas(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		HttpSession session = request.getSession();
		String empresa = (String) session.getAttribute("empresa");

		if (empresa == null || empresa.isEmpty()) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Nome da empresa não fornecido.");
			return;
		}

		try {
			// Passe o nome da empresa para o DAO
			VendasDAO dao = new VendasDAO(empresa);
			ArrayList<Vendas> lista = (ArrayList<Vendas>) dao.listarVendasdoDia();
			request.setAttribute("Vendas", lista);
			RequestDispatcher rd = request.getRequestDispatcher("Home.jsp");
			rd.forward(request, response);
		} catch (Exception e) {
			e.printStackTrace(); // Imprimir a pilha de erros para depuração
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Erro ao processar a requisição.");
		}
	}

	private void itensPorvenda(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		HttpSession session = request.getSession();
		String empresa = (String) session.getAttribute("empresa");
		String idVenda = request.getParameter("id");
		int vendaID = Integer.parseInt(idVenda);
		if (idVenda != null) {

			try {
				itensVendaDAO itdao = new itensVendaDAO(empresa);
				ArrayList<ItensVenda> lista_2 = (ArrayList<ItensVenda>) itdao.listarItensPorVendao(vendaID);

				request.setAttribute("tableRows", lista_2);
				RequestDispatcher rd = request.getRequestDispatcher("DetalheVenda.jsp");
				rd.forward(request, response);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

}