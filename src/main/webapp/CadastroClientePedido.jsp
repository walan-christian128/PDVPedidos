<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="DAO.TokenServiceDAO" %>
<%
String tokenRecebido = request.getParameter("token");
if (tokenRecebido == null || !TokenServiceDAO.validarToken(tokenRecebido)) {
    // Token inválido ou expirado, redireciona ou exibe uma mensagem de erro
    response.sendRedirect("LinkCadastroPedido.html"); // Crie uma página de erro
    return;
}

%>


<!DOCTYPE html>
<html lang="pt-BR">
<head>
<meta charset="utf-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<title>Cadastro de Clientes/Pedido</title>
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
   <div class="container mt-5">

    <h2 class="text-center mb-4">Cadastro de Clientes/Pedido</h2>

    <div class="d-flex">

        <!-- Formulário -->
        <div class="bg-body p-4 rounded shadow me-4" style="min-width: 1200px;">
            <form action="pedidoServer" method="get">
                <h5 class="text-center mb-3">Dados do Cadastro</h5>

            

                <div class="mb-3">
                    <label class="form-label">Nome:</label>
                    <input type="text" class="form-control" name="nome">
                </div>

                <div class="mb-3">
                    <label class="form-label">Telefone:</label>
                    <input type="text" class="form-control" name="fone">
                </div>
                
                <div class="mb-3">
                    <label class="form-label">Endereço:</label>
                    <input type="text" class="form-control" name="endereco">
                </div>
                   <div class="mb-3">
                    <label class="form-label">Numero:</label>
                    <input type="text" class="form-control" name="numero">
                </div>
                
                <div class="mb-3">
                    <label class="form-label">Bairro:</label>
                    <input type="text" class="form-control" name="bairro">
                </div>
                
                 <div class="mb-3">
                    <label class="form-label">Cidade:</label>
                    <input type="text" class="form-control" name="cidade">
                </div>
                
                   
               <div>
                <label for="fornecedor" class="form-label">Estado:</label> <select
						name="estado" class="form-select" id="estado">
						<option value="">Selecione o Estado</option>
						<option value="AC">Acre</option>
						<option value="AL">Alagoas</option>
						<option value="AP">Amapá</option>
						<option value="AM">Amazonas</option>
						<option value="BA">Bahia</option>
						<option value="CE">Ceará</option>
						<option value="DF">Distrito Federal</option>
						<option value="ES">Espírito Santo</option>
						<option value="GO">Goiás</option>
						<option value="MA">Maranhão</option>
						<option value="MT">Mato Grosso</option>
						<option value="MS">Mato Grosso do Sul</option>
						<option value="MG">Minas Gerais</option>
						<option value="PA">Pará</option>
						<option value="PB">Paraíba</option>
						<option value="PR">Paraná</option>
						<option value="PE">Pernambuco</option>
						<option value="PI">Piauí</option>
						<option value="RJ">Rio de Janeiro</option>
						<option value="RN">Rio Grande do Norte</option>
						<option value="RS">Rio Grande do Sul</option>
						<option value="RO">Rondônia</option>
						<option value="RR">Roraima</option>
						<option value="SC">Santa Catarina</option>
						<option value="SP">São Paulo</option>
						<option value="SE">Sergipe</option>
						<option value="TO">Tocantins</option>
						<option value="EX">Estrangeiro</option>
					</select>
               
               </div>

                <div class="mb-3">
                    <label class="form-label">Email:</label>
                    <input type="text" class="form-control" name="email">
                </div>

                <div class="mb-3">
                    <label class="form-label">Senha:</label>
                    <input type="password" class="form-control" name="senha">
                </div>

					<div class="text-center">
						<input type="button" class="btn btn-success"
							data-bs-toggle="modal" data-bs-target="#confirmacaoModal"
							value="Cadastrar">

						<div class="modal fade" tabindex="-1" id="confirmacaoModal">
							<div class="modal-dialog">
								<div class="modal-content">
									<div class="modal-header">
										<h5 class="modal-title">Confirma</h5>
										<button type="button" class="btn-close"
											data-bs-dismiss="modal" aria-label="Close"></button>
									</div>
									<div class="modal-body">
										<p>Os dados inseridos estão corretos?</p>
									</div>
									<div class="modal-footer">
										<button type="button" class="btn btn-secondary"
											data-bs-dismiss="modal">Não</button>
										<input type="submit" class="btn btn-primary" value="Sim">
									</div>
								</div>
							</div>
						</div>
					</div>
				</form>
        </div>

        <!-- Área da tabela -->
 

    </div>
		<div class="modal fade" id="editarcliente" data-bs-backdrop="static"
			data-bs-keyboard="false" tabindex="-1"
			aria-labelledby="staticBackdropLabel" aria-hidden="true">
			<div class="modal-dialog modal-dialog-centered modal-dialog-scrollable">
				<div class="modal-content">
					<div class="modal-header">
						<h1 class="modal-title fs-5" id="staticBackdropLabel">Editar Cliente</h1>
						<button type="button" class="btn-close" data-bs-dismiss="modal"
							aria-label="Close"></button>
					</div>
					<div class="modal-body">  <div class="bg-white p-4 rounded shadow me-4" style="min-width: 280px;">
            <form action="atualizaCliente" method="post">
                <h5 class="text-center mb-3">Dados do Cliente</h5>

                  <div class="mb-3">
                    <label class="form-label">Codigo do cliente:</label>
                    <input type="text" class="form-control" name="idCli">
                </div>

                
                
                <div class="mb-3">
                    <label class="form-label">Nome:</label>
                    <input type="text" class="form-control" name="nomemodal">
                </div>

                <div class="mb-3">
                    <label class="form-label">Endereço:</label>
                    <input type="text" class="form-control" name="enderecomodal">
                </div>
                
                <div class="mb-3">
                    <label class="form-label">N°:</label>
                    <input type="text" class="form-control" name="numeromodal">
                </div>
                
                <div class="mb-3">
                    <label class="form-label">CEP:</label>
                    <input type="text" class="form-control" name="cepmodal">
                </div>
                
               <div>
                <label for="fornecedor" class="form-label">Estado:</label> <select
						name="estadomodal" class="form-select" id="estadomodal">
						<option value="">Selecione o Estado</option>
						<option value="AC">Acre</option>
						<option value="AL">Alagoas</option>
						<option value="AP">Amapá</option>
						<option value="AM">Amazonas</option>
						<option value="BA">Bahia</option>
						<option value="CE">Ceará</option>
						<option value="DF">Distrito Federal</option>
						<option value="ES">Espírito Santo</option>
						<option value="GO">Goiás</option>
						<option value="MA">Maranhão</option>
						<option value="MT">Mato Grosso</option>
						<option value="MS">Mato Grosso do Sul</option>
						<option value="MG">Minas Gerais</option>
						<option value="PA">Pará</option>
						<option value="PB">Paraíba</option>
						<option value="PR">Paraná</option>
						<option value="PE">Pernambuco</option>
						<option value="PI">Piauí</option>
						<option value="RJ">Rio de Janeiro</option>
						<option value="RN">Rio Grande do Norte</option>
						<option value="RS">Rio Grande do Sul</option>
						<option value="RO">Rondônia</option>
						<option value="RR">Roraima</option>
						<option value="SC">Santa Catarina</option>
						<option value="SP">São Paulo</option>
						<option value="SE">Sergipe</option>
						<option value="TO">Tocantins</option>
						<option value="EX">Estrangeiro</option>
					</select>
               
               </div>

                <div class="mb-3">
                    <label class="form-label">Telefone:</label>
                    <input type="text" class="form-control" name="fonemodal">
                </div>

                <div class="mb-3">
                    <label class="form-label">Email:</label>
                    <input type="email" class="form-control" name="emailmodal">
                </div>
                        	<div class="modal-footer">
						<button type="button" class="btn btn-secondary"
							data-bs-dismiss="modal">Fechar</button>
						<button type="submit" class="btn btn-success">Atualizar</button>
					</div>
				
							</form>
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

</html>
