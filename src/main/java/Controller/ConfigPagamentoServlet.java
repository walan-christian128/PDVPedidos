package Controller;

import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import DAO.ConfigPagamentoDAO;
import Model.ConfigPagamento;

/**
 * Servlet para gerenciar as configurações de pagamento da empresa.
 * Esta versão não gerencia a conexão com o banco de dados diretamente,
 * delegando essa responsabilidade para a classe DAO.
 */
@WebServlet("/configpagamento")
public class ConfigPagamentoServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    /**
     * O método doPost é acionado para requisições de salvar/atualizar.
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
    	HttpSession session = request.getSession();
		String empresa = (String) session.getAttribute("empresa");
        
        String gateway = request.getParameter("gateway");
        String chavePix = request.getParameter("chavePix");
        String clientId = request.getParameter("clientId");
        String clientSecret = request.getParameter("clientSecret");
        String accessToken = request.getParameter("accessToken");
        
        // TODO: A lógica para obter o ID da empresa deve vir da sessão do usuário autenticado.
        int empresaId = 1;

        ConfigPagamento config = new ConfigPagamento();
        config.setEmpresaId(empresaId);
        config.setGateway(gateway);
        config.setChavePix(chavePix);
        config.setClientId(clientId);
        config.setClientSecret(clientSecret);
        config.setAccessToken(accessToken);

        // Instancia o DAO sem passar a conexão
        
        
        try {
        	ConfigPagamentoDAO dao = new ConfigPagamentoDAO(empresa);
            ConfigPagamento configExistente = dao.buscarPorEmpresa(empresaId);
            
            if (configExistente != null) {
                dao.atualizar(config);
                request.setAttribute("mensagem", "Configuração de pagamento atualizada com sucesso!");
            } else {
                dao.salvar(config);
                request.setAttribute("mensagem", "Configuração de pagamento salva com sucesso!");
            }
            
            response.sendRedirect(request.getContextPath() + "/Home.jsp");
            
        } catch (Exception e) {
            System.err.println("Erro ao salvar/atualizar configuração: " + e.getMessage());
            request.setAttribute("mensagemErro", "Erro ao salvar a configuração: " + e.getMessage());
            request.getRequestDispatcher("/erro.jsp").forward(request, response);
        }
    }

    /**
     * O método doGet é acionado para requisições de visualização.
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        // TODO: Obter o ID da empresa da sessão do usuário autenticado.
    	HttpSession session = request.getSession();
		String empresa = (String) session.getAttribute("empresa");
        int empresaId = 1;

        // Instancia o DAO sem passar a conexão
       
        
        try {
        	 ConfigPagamentoDAO dao = new ConfigPagamentoDAO(empresa);
            ConfigPagamento config = dao.buscarPorEmpresa(empresaId);
            
            request.setAttribute("configPagamento", config);
            
            request.getRequestDispatcher("/WEB-INF/configuracao.jsp").forward(request, response);
            
        } catch (Exception e) {
            System.err.println("Erro ao buscar configuração: " + e.getMessage());
            request.setAttribute("mensagemErro", "Erro ao carregar a configuração: " + e.getMessage());
            request.getRequestDispatcher("/erro.jsp").forward(request, response);
        }
    }
}
