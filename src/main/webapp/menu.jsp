<%@ page import="java.util.Date"%>
<%@ page import="java.text.SimpleDateFormat"%>
<%@ page import="Model.Vendas"%>
<%@ page import="java.util.List"%>
<%@ page import="java.util.ArrayList"%>
<%@ page import="Model.ItensVenda"%>
<%

Vendas vendasDia = new Vendas();
String totalVenda = request.getAttribute("totalVenda2") != null ? request.getAttribute("totalVenda2").toString() : "";
String data = request.getAttribute("data") != null ? request.getAttribute("data").toString() : "";
%>
<%
String empresa4 = (String) session.getAttribute("empresa");
if (empresa4 == null || empresa4.isEmpty()) {
    RequestDispatcher rd = request.getRequestDispatcher("LoginExpirou.html");
    rd.forward(request, response);
    return; // Certifique-se de que o código pare de executar após o redirecionamento
}

%>

<!doctype html>
<html lang="pt-br">
<head>
<meta charset="utf-8">
<meta name="viewport" content="width=device-width, initial-scale=1">

<title>Menu</title>
<link rel="icon"
	href="img/2992655_click_computer_currency_dollar_money_icon.png">
<link
	href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css"
	rel="stylesheet"
	integrity="sha384-T3c6CoIi6uLrA9TneNEoa7RxnatzjcDSCmG1MXxSR1GAsXEV/Dwwykc2MPK8M2HN"
	crossorigin="anonymous">
<link rel="stylesheet"
	href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.1/font/bootstrap-icons.css">
<link rel="stylesheet"
	href="https://cdn.datatables.net/1.13.7/css/jquery.dataTables.css" />
</head>
<body
	style="background-image: url('img/f417de95-2534-4362-8c7f-85a6245f3feb.jpeg'); background-size: auto auto; background-position: center; margin: 0; padding: 0; height: 100vh; width: 100vw;">

	<i class="bi bi-border-width d-flex ms-3" data-bs-toggle="offcanvas"
		href="#offcanvasExample" role="button"
		aria-controls="offcanvasExample" style="font-size: 3rem;"></i>

	<div class="offcanvas offcanvas-start" tabindex="-1"
		id="offcanvasExample" aria-labelledby="offcanvasExampleLabel">
		<div class="offcanvas-header">
			<h5 class="offcanvas-title" id="offcanvasExampleLabel">Menu</h5>
			<button type="button" class="btn-close" data-bs-dismiss="offcanvas"
				aria-label="Close"></button>
		</div>
		<div class="offcanvas-body">
			<div><%=empresa4%></div>
			<div class="dropdown mt-3">
				<ul class="navbar-nav me-auto mb-2 mb-lg-0">
					<li class="nav-item"><a class="nav-link active md-2"
						href="Home.jsp" style="font-size: 2rem;"> <span class="icon"><i
								class="bi bi-house-up-fill"></i></span> <span class="txt-link">Pagina
								Inicial</span>
					</a></li>
					
						<li class="nav-item"><a class="nav-link active md-2"
						href="Home.jsp" style="font-size: 2rem;"> <span class="icon"><i
								class="bi bi-pen-fill"></i></span> <span class="txt-link">Pedidos</span>
					</a></li>
					   	<li class="nav-item"><a class="nav-link active md-2"
						href="LinkCadastroPedido.jsp" style="font-size: 2rem;"> <span class="icon"><i
								class="bi bi-link"></i></span> <span class="txt-link">Link de cadastro de clientes</span>
					</a></li>
					
					<li class="nav-item"><a class="nav-link active md-3" href="Produtos.jsp"
						style="font-size: 2rem;"> <span class="icon"><i
								class="bi bi-backpack4"></i></span> <span class="txt-link">Estoque</span>
					</a></li>
				
					<li class="nav-item"><a class="nav-link active md-3" href="#"
						id="historico-link" style="font-size: 2rem;"> <span
							class="icon"><i class="bi bi-clock-history"></i></span> <span
							class="txt-link">Historico</span>
					</a>
						<button id="hidden-button" class="btn btn-primary mt-2"
							data-bs-toggle="modal" data-bs-target="#periodo"
							style="display: none;">Data</button>
							
						<button id="hidden-button_2" class="btn btn-danger mt-2"
							data-bs-toggle="modal" data-bs-target="#dia"
							style="display: none;">Dia</button>
							
							<button id= "hidden-button_3" style="display: none;" class="btn btn-dark mt-2" data-bs-toggle="modal" data-bs-target="#maisVendido">Mais vendidos</button>
							
							</li>
							
					<li class="nav-item"><a class="nav-link active md-3"
						href="Clientes.jsp" style="font-size: 2rem;"> <span
							class="icon"><i class="bi bi-person"></i></span> <span
							class="txt-link">Clientes</span>
					</a></li>
					<li class="nav-item"><a class="nav-link active md-3"
						href="Fornecedores.jsp" style="font-size: 2rem;"> <span
							class="icon"><i class="bi bi-building-up"></i></span> <span
							class="txt-link">Fornecedores</span>
					</a></li>
					<li class="nav-item"><a class="nav-link active md-3" href="#"
						style="font-size: 2rem;"> <span class="icon"><i
								class="bi bi-file-person"></i></span> <span class="txt-link">Funcionarios</span>
					</a></li>
			           <li class="nav-item"><a class="nav-link active md-3" href="#"
						id="resumo-link" style="font-size: 2rem;"> <span
							class="icon"><i class="bi bi-journal"></i></span> <span
							class="txt-link">Resumo</span>
					</a>
						<button id="hidden-button_4" class="btn btn-primary mt-2"
							data-bs-toggle="modal" data-bs-target="#lucroPorVenda"
							style="display: none;">Lucro por venda</button>
							
						<button id="hidden-button_5" class="btn btn-danger mt-2"
							data-bs-toggle="modal" data-bs-target="#lucroPeriodo"
							style="display: none;">Lucro por periodo</button>
	
							</li>
					<li class="nav-item"><a class="nav-link active md-3"
						href="#" style="font-size: 2rem;"> <span
							class="icon"><i class="bi bi-file-person-fill"></i></span> <span
							class="txt-link">Usuários</span>
					</a></li>
					<li class="nav-item"><a class="nav-link active md-3"
						href="realizarVendas.jsp" style="font-size: 2rem;"> <span
							class="icon"><i class="bi bi-receipt"></i></span> <span
							class="txt-link">Vendas</span>
					</a></li>
					<li class="nav-item"><a class="nav-link active md-3"
						href="detroyLogaut.jsp" style="font-size: 2rem;"> <span
							class="icon"><i class="bi bi-box-arrow-right"></i></span> <span
							class="txt-link">Sair</span>
					</a></li>
				</ul>
			</div>
		</div>
	</div>
	

	<div class="modal fade" id="lucroPeriodo" tabindex="-1"
		aria-labelledby="exampleModalLabel" aria-hidden="true">
		<div
			class="modal-dialog modal-dialog-centered modal-dialog-scrollable">
			<div class="modal-content">
				<div class="modal-header">
					<h1 class="modal-title fs-5" id="exampleModalLabel">Lucro por periodo</h1>
					<button type="button" class="btn-close" data-bs-dismiss="modal"
						aria-label="Close"></button>
				</div>
				<div class="modal-body">
					<form id="lucroPeridoForm" action="lucroPeriodo" method="post">

						<div class="mb-3">
							<label for="dataInicial" class="form-label">Data Incial:</label>
							<input type="text" id="dataIniciallucro" class="form-control"
								name="dataIniciallucro" required
								placeholder="Digite a data: DD/MM/AAAA">
						</div>

						<div class="mb-3">
							<label for="dataFinallucro" class="form-label">Data Final:</label> <input
								type="text" id="dataFinallucro" class="form-control" name="dataFinallucro"
								placeholder="Digite a data: DD/MM/AAAA">
						</div>
						<div>
							<table class="table table-dark table-striped" id="VendaDiaria">
								<thead>
									<tr>
										
										<th>Total de lucro no periodo:</th>


									</tr>
								</thead>
								<tbody>
									<!-- Itera sobre os objetos ItensVenda -->
									<%
									Double lucroperiodo = (Double) request.getAttribute("totalLucro");
									if (lucroperiodo != null) {
										String lucroStr = lucroperiodo.toString();
										 if (!lucroStr.isEmpty()) {
									%>
									<tr>
										
										<td><%=lucroStr%></td>

									</tr>

									<%
								    }
									} else {
									%>
									<tr>
										<td colspan="2">Nenhum dado encontrado.</td>
									</tr>
									<%
									}
									%>

								</tbody>
							</table>

						</div>
						<button type="button" class="btn btn-secondary"
							data-bs-dismiss="modal">Fechar</button>
						<button type="submit" class="btn btn-primary">Buscar</button>
					</form>
				</div>
				<div class="modal-footer"></div>
			</div>
		</div>
	</div>
	
	<div class="modal fade" id="lucroPorVenda" tabindex="-1"
		aria-labelledby="exampleModalLabel" aria-hidden="true">
		<div
			class="modal-dialog modal-dialog-centered modal-dialog-scrollable">
			<div class="modal-content">
				<div class="modal-header">
					<h1 class="modal-title fs-5" id="exampleModalLabel">Lucro por venda</h1>
					<button type="button" class="btn-close" data-bs-dismiss="modal"
						aria-label="Close"></button>
				</div>
				<div class="modal-body">
					<form id="lucroForm" action="lucroVenda" method="post">
						
						<div class="mb-3">
							<label for="dataFinal" class="form-label">Codigo :</label> <input
								type="text" id="CodigoVenda" class="form-control" name="CodigoVenda"
								required >
						</div>
						<div>
							<table class="table table-dark table-striped" id="lucroVenda">
								<thead>
									<tr>
										<th>Código:</th>
										<th>Lucro:</th>


									</tr>
								</thead>
								<tbody>
									<!-- Itera sobre os objetos ItensVenda -->
									<%
									Double lucro = (Double) request.getAttribute("lucro");
									if (lucro != null) {
										String lucroStr = lucro.toString();
										 if (!lucroStr.isEmpty()) {
									%>
									<tr>
										<td><%=request.getAttribute("vendaCodigo")%></td>
										<td><%=lucroStr%></td>

									</tr>

									<%
								    }
									} else {
									%>
									<tr>
										<td colspan="2">Nenhum dado encontrado.</td>
									</tr>
									<%
									}
									%>

								</tbody>
							</table>

						</div>
						<button type="button" class="btn btn-secondary"
							data-bs-dismiss="modal">Fechar</button>
						<button type="submit" class="btn btn-primary">Buscar</button>
					</form>
				</div>
				<div class="modal-footer"></div>
			</div>
		</div>
	</div>

	<div class="modal fade" id="dia" tabindex="-1"
		aria-labelledby="exampleModalLabel" aria-hidden="true">
		<div
			class="modal-dialog modal-dialog-centered modal-dialog-scrollable">
			<div class="modal-content">
				<div class="modal-header">
					<h1 class="modal-title fs-5" id="exampleModalLabel">Vendas por
						Dia</h1>
					<button type="button" class="btn-close" data-bs-dismiss="modal"
						aria-label="Close"></button>
				</div>
				<div class="modal-body">
					<form  id = "dia"action="dia" method="post">
						<div class="mb-3">
							<label for="data" class="form-label">Data:</label> <input
								type="text" id="data" class="form-control" name="data" required
								placeholder="Digite a data: DD/MM/AAAA" value="<%=data%>">
						</div>
						<div class="mb-3">
							<label for="totalVenda" class="form-label">Total:</label> <input
								type="text" id="totalVenda" class="form-control"
								name="totalVenda" value="<%=totalVenda%>">
						</div>

						<button type="button" class="btn btn-danger"
							data-bs-dismiss="modal">Fechar</button>
						<button type="submit" class="btn btn-primary">Buscar</button>

					</form>
				</div>
				<div class="modal-footer"></div>
			</div>
		</div>
	</div>
	
		<div class="modal fade" id="maisVendido" tabindex="-1"
		aria-labelledby="exampleModalLabel" aria-hidden="true">
		<div
			class="modal-dialog modal-dialog-centered modal-dialog-scrollable">
			<div class="modal-content">
				<div class="modal-header">
					<h1 class="modal-title fs-5" id="exampleModalLabel">Produtos mais vendidos por periodo</h1>
					<button type="button" class="btn-close" data-bs-dismiss="modal"
						aria-label="Close"></button>
				</div>
				<div class="modal-body">
					<form id="vendasForm" action=maisVendidos method="post">
						<div class="mb-3">
							<label for="dataInicial" class="form-label">Data Incial:</label>
							<input type="text" id="dataVendainicio" class="form-control"
								name="dataVendainicio" required
								placeholder="Digite a data: DD/MM/AAAA">
						</div>
						<div class="mb-3">
							<label for="dataFinal" class="form-label">Data Final:</label> <input
								type="text" id="dataVendafim" class="form-control" name="dataVendafim"
								required placeholder="Digite a data: DD/MM/AAAA">
						</div>
						<div>
							<table class="table table-dark table-striped" id="VendaDiaria">
								<thead>
									<tr>
										<th>QUANTIDADE:</th>
										<th>DESCRIÇÃO :</th>


									</tr>
								</thead>
								<tbody>
									<!-- Itera sobre os objetos ItensVenda -->
									<%
									List<ItensVenda> maisVendidos = (List<ItensVenda>) request.getAttribute("maisVendidos");
									if (maisVendidos != null && !maisVendidos.isEmpty()) {
										for (ItensVenda produtosVendidos : maisVendidos) {
									%>
									<tr>
										<td><%=produtosVendidos.getQtd()%></td>
										<td><%=produtosVendidos.getProduto().getDescricao()%></td>

									</tr>

									<%
									}
									} else {
									%>
									<tr>
										<td colspan="2">Nenhum dado encontrado.</td>
									</tr>
									<%
									}
									%>

								</tbody>
							</table>

						</div>
						<button type="button" class="btn btn-secondary"
							data-bs-dismiss="modal">Fechar</button>
						<button type="submit" class="btn btn-primary">Buscar</button>
					</form>
				</div>
				<div class="modal-footer"></div>
			</div>
		</div>
	</div>
	<div class="modal fade" id="periodo" tabindex="-1"
		aria-labelledby="exampleModalLabel" aria-hidden="true">
		<div
			class="modal-dialog modal-dialog-centered modal-dialog-scrollable">
			<div class="modal-content">
				<div class="modal-header">
					<h1 class="modal-title fs-5" id="exampleModalLabel">Vendas por
						periodo</h1>
					<button type="button" class="btn-close" data-bs-dismiss="modal"
						aria-label="Close"></button>
				</div>
				<div class="modal-body">
					<form id="vendasForm" action="PeriodoVenda" method="post">
						<div class="mb-3">
							<label for="dataInicial" class="form-label">Data Incial:</label>
							<input type="text" id="dataInicial" class="form-control"
								name="dataInicial" required
								placeholder="Digite a data: DD/MM/AAAA">
						</div>
						<div class="mb-3">
							<label for="dataFinal" class="form-label">Data Final:</label> <input
								type="text" id="dataFinal" class="form-control" name="dataFinal"
								required placeholder="Digite a data: DD/MM/AAAA">
						</div>
						<div>
							<table class="table table-dark table-striped" id="VendaDiaria">
								<thead>
									<tr>
										<th>Data:</th>
										<th>Total:</th>


									</tr>
								</thead>
								<tbody>
									<!-- Itera sobre os objetos ItensVenda -->
									<%
									List<Vendas> periodoVenda = (List<Vendas>) request.getAttribute("periodo");
									if (periodoVenda != null && !periodoVenda.isEmpty()) {
										for (Vendas vendas : periodoVenda) {
									%>
									<tr>
										<td><%=vendas.getData_venda()%></td>
										<td><%=vendas.getTotal_venda()%></td>

									</tr>

									<%
									}
									} else {
									%>
									<tr>
										<td colspan="2">Nenhum dado encontrado.</td>
									</tr>
									<%
									}
									%>

								</tbody>
							</table>

						</div>
						<button type="button" class="btn btn-secondary"
							data-bs-dismiss="modal">Fechar</button>
						<button type="submit" class="btn btn-primary">Buscar</button>
					</form>
				</div>
				<div class="modal-footer"></div>
			</div>
		</div>
	</div>

	<script src="https://code.jquery.com/jquery-3.6.4.min.js"></script>
	<script
		src="https://cdn.jsdelivr.net/npm/@popperjs/core@2.11.6/dist/umd/popper.min.js"></script>
	<script
		src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.min.js"></script>
	<script src="https://cdn.datatables.net/1.13.7/js/jquery.dataTables.js"></script>
	<script
		src="https://cdnjs.cloudflare.com/ajax/libs/jquery.mask/1.14.11/jquery.mask.min.js"></script>
	<script
		src="https://cdnjs.cloudflare.com/ajax/libs/jquery.inputmask/5.0.6/jquery.inputmask.min.js"></script>

	<script type="text/javascript">
	
		
		$(document).ready(function() {
			$('#dataInicial').mask('00/00/0000');
			$('#dataFinal').mask('00/00/0000');
			$('#dataIniciallucro').mask('00/00/0000');
			$('#dataFinallucro').mask('00/00/0000');
			$('#dataVendainicio').mask('00/00/0000');
			$('#dataVendafim').mask('00/00/0000');
			$('#data').mask('00/00/0000');
			

			$('#historico-link').on('click', function(event) {
				event.preventDefault();
				$('#hidden-button').toggle();
				$('#hidden-button_2').toggle();
				$('#hidden-button_3').toggle();
			});
	<%if (periodoVenda != null && !periodoVenda.isEmpty()) {%>
		$('#periodo').modal('show');
	<%}%>
		
	<%if (!totalVenda.isEmpty()) {%>
		$('#dia').modal('show');
	<%}%>
		
	<%if (maisVendidos != null && !maisVendidos.isEmpty()) {%>
		$('#maisVendido').modal('show'); 
	<%}%>
		
	<%if (lucro != null) {%>
		$('#lucroPorVenda').modal('show');
	<%}%>
	<%if (lucroperiodo != null) {%>
		$('#lucroPeriodo').modal('show');
	<%}%>
		});
	</script>
	<script>
	$(document).ready(function() {
	
		$('#resumo-link').on('click', function(event) {
			event.preventDefault();
			
			$('#hidden-button_4').toggle();
			$('#hidden-button_5').toggle();
		});

	});
	
	</script>
</body>
</html>
