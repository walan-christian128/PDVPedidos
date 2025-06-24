<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="DAO.TokenServiceDAO" %>
<%@ page import="DAO.ProdutosDAO" %>
<%@ page import="Model.Produtos" %>
<%@ page import="java.util.ArrayList"%>
<%@ page import="java.util.List"%>

<%
String empresa = (String) session.getAttribute("empresa");


%>

<%
List<Produtos> prodp; // Declara a lista
ProdutosDAO daop = new ProdutosDAO(empresa);
prodp = daop.listarProdutos(); // Atribui o resultado da busca à lista exibida na tabela
%>


<!DOCTYPE html>
<html lang="pt-BR">
<head>
<meta charset="utf-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<title>Produtos para pedidos</title>
<link rel="icon" href="img/pedido-online.png">
<link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet"
    integrity="sha384-T3c6CoIi6uLrA9TneNEoa7RxnatzjcDSCmG1MXxSR1GAsXEV/Dwwykc2MPK8M2HN"
    crossorigin="anonymous">
<link rel="stylesheet" href="https://cdn.datatables.net/1.13.7/css/jquery.dataTables.css" />

<style>
td a {
    text-decoration: none;
    color: inherit;
    cursor: pointer;
}
</style>
</head>

<body style="background-image: url('img/Gemini_Generated_Image_kysa9wkysa9wkysa.png'); background-size: cover; background-position: center; margin: 0; padding: 0; height: 100vh;">

    <!-- Centraliza na tela inteira -->
   <div class="container mt-4">
    <div class="row justify-content-center">
        <%
        if (prodp != null && !prodp.isEmpty()) {
            for (int i = 0; i < prodp.size(); i++) {
                Produtos produto = prodp.get(i);
        %>
        <div class="col-12 col-sm-6 col-md-4 col-lg-3 mb-4">
            <div class="card h-100 shadow-sm text-center">
                <img src="exibirImagemProduto?id=<%=produto.getId()%>" class="card-img-top" alt="Imagem"
                     style="height: 180px; object-fit: cover;" onerror="this.src='img/padrao.png';">
                <div class="card-body d-flex flex-column justify-content-between">
                    <h5 class="card-title"><%=produto.getDescricao()%></h5>
                    <p class="card-text text-muted">R$ <%=String.format("%.2f", produto.getPreco_de_venda())%></p>

                    <div class="d-flex justify-content-center align-items-center gap-2 mb-2">
                        <button class="btn btn-outline-secondary btn-sm" type="button"
                                onclick="diminuirQuantidade(<%=produto.getId()%>)">-</button>
                        <input type="text" id="quantidade_<%=produto.getId()%>" value="1"
                               class="form-control form-control-sm text-center" style="width: 50px;" readonly>
                        <button class="btn btn-outline-secondary btn-sm" type="button"
                                onclick="aumentarQuantidade(<%=produto.getId()%>)">+</button>
                    </div>

                    <a href="selecionarVenda?id=<%=produto.getId()%>&qtd=1"
                       class="btn btn-dark btn-sm"
                       onclick="this.href = 'selecionarVenda?id=' + <%=produto.getId()%> + '&qtd=' + document.getElementById('quantidade_<%=produto.getId()%>').value;" data-bs-toggle="modal" data-bs-target="#carrinho">
                        Adicionar
                    </a>
                </div>
            </div>
        </div>
        <%
            }
        } else {
        %>
        <div class="col-12 text-center">
            <p class="text-muted">Nenhum produto encontrado.</p>
        </div>
        <%
        }
        %>
    </div>
</div>
        <!-- Área da tabela -->
 

    </div>
		<div class="modal fade" id="carrinho" data-bs-backdrop="static"
			data-bs-keyboard="false" tabindex="-1"
			aria-labelledby="staticBackdropLabel" aria-hidden="true">
			<div class="modal-dialog modal-sm">
				<div class="modal-content">
					<div class="modal-header">
						<h1 class="modal-title fs-5" id="staticBackdropLabel">Itens no carrinho</h1>
						<button type="button" class="btn-close" data-bs-dismiss="modal"
							aria-label="Close"></button>
					</div>
					<div class="modal-body">...</div>
					<div class="modal-footer">
						<button type="button" class="btn btn-secondary"
							data-bs-dismiss="modal">Voltar a pedidos</button>
						<button type="button" class="btn btn-primary">Finalizar</button>
					</div>
				</div>
			</div>
		</div>
		</div>
		</body>
<script src="https://code.jquery.com/jquery-3.6.4.min.js"></script>
<script src="https://cdn.jsdelivr.net/npm/@popperjs/core@2.11.6/dist/umd/popper.min.js"></script>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.min.js"></script>
<script src="https://cdn.datatables.net/1.13.7/js/jquery.dataTables.js"></script>
<script src="https://cdnjs.cloudflare.com/ajax/libs/jquery.mask/1.14.11/jquery.mask.min.js"></script>
<script src="https://cdnjs.cloudflare.com/ajax/libs/jquery.inputmask/5.0.6/jquery.inputmask.min.js"></script>
<script>
    function aumentarQuantidade(id) {
        const campo = document.getElementById("quantidade_" + id);
        let valor = parseInt(campo.value);
        campo.value = valor + 1;
    }

    function diminuirQuantidade(id) {
        const campo = document.getElementById("quantidade_" + id);
        let valor = parseInt(campo.value);
        if (valor > 1) {
            campo.value = valor - 1;
        }
    }
</script>


</html>
