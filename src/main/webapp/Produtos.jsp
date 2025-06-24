<%@page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8" %>
<%@ page import="Model.Fornecedores"%>
<%@ page import="DAO.FornecedoresDAO"%>
<%@ page import="java.util.List"%>
<%@ page import="java.util.ArrayList"%>
<%@ page import="Model.Produtos"%>
<%@ page import="DAO.ProdutosDAO"%>
<%@ page import="Model.Vendas"%>
<%@ page import="DAO.VendasDAO"%>

<%
String empresa = (String) session.getAttribute("empresa");
if (empresa == null || empresa.isEmpty()) {
    RequestDispatcher rd = request.getRequestDispatcher("LoginExpirado.jsp");
    rd.forward(request, response);
    return; // Certifique-se de que o código pare de executar após o redirecionamento
}

List<Fornecedores> lista; // Declara a lista
FornecedoresDAO dao = new FornecedoresDAO(empresa);
lista = dao.listaFornecedores(); // Atribui o resultado da busca à lista exibida no select
%>

<%
List<Produtos> prodp; // Declara a lista
ProdutosDAO daop = new ProdutosDAO(empresa);
prodp = daop.listarProdutos(); // Atribui o resultado da busca à lista exibida na tabela
%>
<%
Produtos produtos = new Produtos();
%>
<%
Vendas vd = new Vendas();
%>
<%
List<Vendas> listaVenda;
VendasDAO Vdao = new VendasDAO(empresa);
listaVenda = Vdao.listarVendasdoDia();
%>


<!DOCTYPE html>
<html lang="pt-BR">
<head>
<meta charset="utf-8">
<title>Produtos</title>
<link rel="icon"
	href="img/2992655_click_computer_currency_dollar_money_icon.png">
<script src="scripts/buscaProduto.js"></script>



</head>
<body
	style="background-image: url('img/Gemini_Generated_Image_97a36f97a36f97a3.jpg'); background-size: auto auto; background-position: center; margin: 0; padding: 0; height: 100vh; width: 100vw;">
       
     <%@ include file="menu.jsp"%>
   <div class="container-fluid">
    <div class="row">
        <div class="col-12">
            <div id="table-container" class="container-sm overflow-auto"  style="overflow-y: auto; max-height: 400px;">
                <table id="tabela" class="table table-dark table-bordered table-hover overflow-visible" >
                    <!-- cabeçalho da tabela -->
                    <thead>
                        <tr>
                            <!-- Cabeçalhos das colunas -->
                            <th>Código</th>
                            <th>Descrição</th>
                            <th>Quantidade em Estoque</th>
                            <th>Preço de Compra</th>
                            <th>Preço de Venda</th>
                            <th>Margem</th>
                            <th>Fornecedor</th>
                            <th>Opções</th>
                        </tr>
                    </thead>
                    <!-- corpo da tabela -->
                    <tbody>
                       <% if (prodp != null && !prodp.isEmpty()) { %>
                        <% for (int i = 0; i < prodp.size(); i++) { %>
                        <tr id="row<%=prodp.get(i).getId()%>" class="linha-editar" data-id="<%=prodp.get(i).getId()%>">
                            <!-- células com dados -->
                            <td><%=prodp.get(i).getId()%></td>
                            <td><%=prodp.get(i).getDescricao()%></td>
                            <td><%=prodp.get(i).getQtd_estoque()%></td>
                            <td><%=prodp.get(i).getPreco_de_compra()%></td>
                            <td><%=prodp.get(i).getPreco_de_venda()%></td>
                            <td>
                                <script>
                                    var precoCompra = <%=prodp.get(i).getPreco_de_compra()%>;
                                    var precoVenda = <%=prodp.get(i).getPreco_de_venda()%>;
                                    var porcentagem = ((precoVenda - precoCompra) / precoCompra) * 100;
                                    var porcentagemArredondada = porcentagem.toFixed(2);
                                    document.write(porcentagemArredondada + '%');
                                </script>
                            </td>
                            <td>
                                <% for (Fornecedores fornecedor : lista) {
                                    if (fornecedor.getId() == prodp.get(i).getFornecedor().getId()) { %>
                                    <%=fornecedor.getNome()%>
                                <% } } %>
                            </td>
                            <td>
                                <a href="select?id=<%=prodp.get(i).getId()%>" class="btn btn-success">Editar</a>
                                <a href="delete?id=<%=prodp.get(i).getId()%>" class="btn btn-danger">Apagar</a>
                            </td>
                        </tr>
                        <% } %>
                        <%}else{ %>
                           <tr><td colspan="5">Nenhum produto cadastrado.</td></tr>
                           <%} %>
                    </tbody>
                </table>
            </div>
        </div>
    </div>
</div>

		<div id="form-container" class="container">
    <form name="cadastrarProduto" action="insert" enctype="multipart/form-data">
        <h2>Cadastro de Produtos</h2>

        <div class="mb-3">
            <label for="descricao" class="form-label">Descrição:</label>
            <input type="text" id="descricao" class="form-control" name="descricao" required>
        </div>

        <div class="mb-3">
            <label for="quantidade" class="form-label">Quantidade em Estoque:</label>
            <input type="text" id="quantidade" class="form-control" name="qtd_estoque" required>
        </div>

        <div class="mb-3">
            <label for="preco_compra" class="form-label">Preço de Compra:</label>
            <input type="text" id="preco_compra"  class="form-control" name="preco_de_compra" required>
        </div>

        <div class="mb-3">
            <label for="preco_venda" class="form-label">Preço de Venda:</label>
            <input type="text" class="form-control" id="preco_venda" name="preco_de_venda">
        </div>

			<div class="mb-3">
				<label for="fornecedor" class="form-label">Fornecedor:</label> <select
					name="for_id" class="form-select">
					<option value="" selected>Selecione o fornecedor</option>

					<%
                String forIdAttribute = (String) request.getAttribute("for_id");

                for (Fornecedores fornecedor : lista) {
                    int fornecedorId = fornecedor.getId();
                    String nomeFornecedorAtual = fornecedor.getNome();
                    boolean isSelected = (forIdAttribute != null && nomeFornecedorAtual.equals(forIdAttribute));

                %>
					<option value="<%= fornecedorId %>"
						<%= isSelected ? "selected" : "" %>>
						<%= nomeFornecedorAtual %>
					</option>
					<%
                }
                %>
				</select>
				<div class="mb-3">
					<label for="logo" class="form-label">Imagem:</label> <input
						type="file" class="form-control" id="logo" name="logo"
						accept="image/*">
				</div>
			</div>

			<input type="submit" value="Cadastrar Produto" class="btn btn-primary">
    </form>
</div>

	</div>
<script src="https://code.jquery.com/jquery-3.6.4.min.js"></script>
<script src="https://cdn.jsdelivr.net/npm/@popperjs/core@2.11.6/dist/umd/popper.min.js"></script>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.min.js"></script>
<script src="https://cdn.datatables.net/1.13.7/js/jquery.dataTables.js"></script>
<script src="https://cdnjs.cloudflare.com/ajax/libs/jquery.mask/1.14.11/jquery.mask.min.js"></script>
<script src="https://cdnjs.cloudflare.com/ajax/libs/jquery.inputmask/5.0.6/jquery.inputmask.min.js"></script>

</body>
</html>
