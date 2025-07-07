<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
	<%@ page import="Model.Pedidos" %>
	<%@ page import="java.util.List"%>
    <%@ page import="java.util.ArrayList"%>
    <%@ page import="DAO.PedidosDAO" %>

<%
String empresa = (String) session.getAttribute("empresa");
if (empresa == null || empresa.isEmpty()) {
    RequestDispatcher rd = request.getRequestDispatcher("LoginExpirou.html");
    rd.forward(request, response);
    return;
}

// Sua lógica original de carregamento de pedidos permanece aqui
List<Pedidos> pedidos = new ArrayList<>(); // Inicializa para garantir que não seja nulo
try {
    PedidosDAO daop = new PedidosDAO(empresa);
    pedidos = daop.listaTodosPedidosDoDia(); // Carrega os pedidos para a renderização inicial
} catch (Exception e) {
    e.printStackTrace(); // Loga qualquer erro na conexão ou consulta
    // Opcional: Adicionar uma mensagem de erro ao usuário
}
%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<link rel="icon"
	href="img/2992664_cart_dollar_mobile_shopping_smartphone_icon.png">
<link href="bootstrap/css/bootstrap.min.css" rel="stylesheet">
<script src="bootstrap/js/bootstrap.bundle.min.js"></script>
<title>Pedidos</title>
<link rel="stylesheet"
	href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.min.css">
</head>
<body
	style="background-image: url('img/Gemini_Generated_Image_97a36f97a36f97a3.jpg'); background-size: cover; background-position: center; margin: 0; padding: 0; min-height: 100vh; width: 100vw; display: flex; flex-direction: column;">

	<%@ include file="menu.jsp"%>

	<div class="container-fluid flex-grow-1 py-4">
		<h3 class="mb-4 text-dark">Pedidos</h3>

		<div class="row g-3 mb-4">
			<div class="col-12 col-sm-6 col-md-4 col-lg-2">
				<div
					class="card text-white bg-primary bg-gradient shadow-sm rounded-3">
					<div
						class="card-body d-flex justify-content-between align-items-center">
						<div>
							<h2 class="card-title mb-0" id="totalPedidosEfetuados">0</h2> <%-- Iniciar com 0 e o JS populada --%>
							<p class="card-text mb-0">PEDIDOS EFETUADOS</p>
						</div>
						<i class="bi bi-cart fs-1 opacity-75"></i>
					</div>
				</div>
			</div>

			<div class="col-12 col-sm-6 col-md-4 col-lg-2">
				<div
					class="card text-white bg-warning bg-gradient shadow-sm rounded-3">
					<div
						class="card-body d-flex justify-content-between align-items-center">
						<div>
							<h2 class="card-title mb-0" id="totalPedidosPendentes">0</h2>
							<p class="card-text mb-0">PEDIDOS PENDENTES</p>
						</div>
						<i class="bi bi-cart fs-1 opacity-75"></i>
					</div>
				</div>
			</div>

			<div class="col-12 col-sm-6 col-md-4 col-lg-2">
				<div
					class="card text-white bg-info bg-gradient shadow-sm rounded-3">
					<div
						class="card-body d-flex justify-content-between align-items-center">
						<div>
							<h2 class="card-title mb-0" id="totalPedidosEmPreparacao">0</h2>
							<p class="card-text mb-0">PEDIDOS EM PREPARAÇÃO</p>
						</div>
						<i class="bi bi-cart fs-1 opacity-75"></i>
					</div>
				</div>
			</div>

			<div class="col-12 col-sm-6 col-md-4 col-lg-2">
				<div
					class="card text-white bg-success bg-gradient shadow-sm rounded-3">
					<div
						class="card-body d-flex justify-content-between align-items-center">
						<div>
							<h2 class="card-title mb-0" id="totalPedidosEmEntrega">0</h2>
							<p class="card-text mb-0">PEDIDOS EM ROTA DE ENTREGA</p>
						</div>
						<i class="bi bi-cart fs-1 opacity-75"></i>
					</div>
				</div>
			</div>

			<div class="col-12 col-sm-6 col-md-4 col-lg-2">
				<div
					class="card text-white bg-danger bg-gradient shadow-sm rounded-3">
					<div
						class="card-body d-flex justify-content-between align-items-center">
						<div>
							<h2 class="card-title mb-0" id="totalPedidosReprovados">0</h2>
							<p class="card-text mb-0">PEDIDOS REPROVADOS</p>
						</div>
						<i class="bi bi-cart fs-1 opacity-75"></i>
					</div>
				</div>
			</div>

			<div class="col-12 col-sm-6 col-md-4 col-lg-2">
				<div
					class="card text-white bg-secondary bg-gradient shadow-sm rounded-3">
					<div
						class="card-body d-flex justify-content-between align-items-center">
						<div>
							<h2 class="card-title mb-0" id="totalClientesPedidos">0</h2>
							<p class="card-text mb-0">CLIENTES COM PEDIDOS</p>
						</div>
						<i class="bi bi-person fs-1 opacity-75"></i>
					</div>
				</div>
			</div>
		</div>

	 <div class="row">
            <div class="col-md-12">
                <div class="table-container">
                <h1 class="card-title mb-0">Pedidos do dia</h1>
                    <table id="VendaDiaria" class="table table-dark table-striped table-hover">
                        <thead>
                            <tr>
                                <th>Codigo do pedido</th>
                                <th>Nome</th>
                                <th>Data</th>
                                <th>Status</th>
                                <th>Total Pedido</th>
                                <th>Observação</th>
                                <th>Forma de pagamento</th>
                            </tr>
                        </thead>
                        <tbody id="pedidosTableBody">
									<%
									// Sua lógica de preenchimento da tabela no JSP permanece
									if (pedidos != null && !pedidos.isEmpty()) {
										for (int i = 0; i <pedidos.size(); i++) {
									%>
									<tr>
										<td><%= pedidos.get(i).getIdPedido() %></td>
										<td><%= pedidos.get(i).getClientepedido().getNome() %></td>
										<td><%= pedidos.get(i).getDataPeedido() %></td>
										<td><%= pedidos.get(i).getStatus() %></td>
										<td>R$ <%=String.format("%.2f",pedidos.get(i).getTotalPedido())   %></td>
										<td><%= pedidos.get(i).getObservacoes() != null && !pedidos.get(i).getObservacoes().isEmpty() ? pedidos.get(i).getObservacoes() : "-" %></td>
										<td><%= pedidos.get(i).getFormapagamento() != null && !pedidos.get(i).getFormapagamento().isEmpty() ? pedidos.get(i).getFormapagamento() : "-" %></td>
									</tr>
									<%
										}
									} else {
									%>
									<tr>
										<td colspan="6" class="text-center">Nenhum pedido encontrado para hoje.</td>
									</tr>
									<%
									}
									%>
								</tbody>
                    </table>
                </div>
            </div>
        </div>
	</div>

    <script>
        // Função para atualizar os cartões com base na lista de pedidos
        function atualizarCartoes(pedidos) {
            let totalPedidosEfetuados = 0;
            let totalPedidosPendentes = 0;
            let totalPedidosEmPreparacao = 0;
            let totalPedidosEmEntrega = 0;
            let totalPedidosReprovados = 0;
            const clientesComPedidos = new Set();

            if (pedidos && pedidos.length > 0) {
                pedidos.forEach(pedido => {
                    totalPedidosEfetuados++;
                    if (pedido.status === 'PENDENTE') { // Adapte os status conforme seu sistema
                        totalPedidosPendentes++;
                    } else if (pedido.status === 'EM PREPARAÇÃO') {
                        totalPedidosEmPreparacao++;
                    } else if (pedido.status === 'EM ROTA DE ENTREGA') {
                        totalPedidosEmEntrega++;
                    } else if (pedido.status === 'REPROVADO') {
                        totalPedidosReprovados++;
                    }
                    if (pedido.clientepedido && pedido.clientepedido.nome) {
                        clientesComPedidos.add(pedido.clientepedido.nome);
                    }
                });
            }

            document.getElementById('totalPedidosEfetuados').textContent = totalPedidosEfetuados;
            document.getElementById('totalPedidosPendentes').textContent = totalPedidosPendentes;
            document.getElementById('totalPedidosEmPreparacao').textContent = totalPedidosEmPreparacao;
            document.getElementById('totalPedidosEmEntrega').textContent = totalPedidosEmEntrega;
            document.getElementById('totalPedidosReprovados').textContent = totalPedidosReprovados;
            document.getElementById('totalClientesPedidos').textContent = clientesComPedidos.size;
        }

        // Função para carregar e atualizar a tabela e os cartões via AJAX
        function carregarEAtualizarPedidosViaAjax() {
            fetch('getPedidosJson.jsp') // O endpoint que retorna o JSON dos pedidos
                .then(response => {
                    if (!response.ok) {
                        if (response.status === 401) {
                            window.location.href = 'LoginExpirou.html'; // Redireciona em caso de sessão expirada
                        }
                        throw new Error('Erro ao carregar os pedidos: ' + response.statusText);
                    }
                    return response.json();
                })
                .then(pedidos => {
                    const tbody = document.getElementById('pedidosTableBody');
                    tbody.innerHTML = ''; // Limpa a tabela antes de adicionar os novos dados

                    if (pedidos && pedidos.length > 0) {
                        pedidos.forEach(pedido => {
                            const row = tbody.insertRow();
                            row.insertCell().textContent = pedido.idPedido;
                            row.insertCell().textContent = pedido.clientepedido ? pedido.clientepedido.nome : '-';
                            row.insertCell().textContent = pedido.dataPeedido;
                            row.insertCell().textContent = pedido.status;
                            row.insertCell().textContent = pedido.observacoes || '-';
                            row.insertCell().textContent = pedido.formapagamento || '-';
                        });
                    } else {
                        const row = tbody.insertRow();
                        const cell = row.insertCell();
                        cell.colSpan = 6;
                        cell.classList.add('text-center');
                        cell.textContent = 'Nenhum pedido encontrado para hoje.';
                    }

                    // Atualiza os cartões com os dados recém-carregados
                    atualizarCartoes(pedidos);
                })
                .catch(error => {
                    console.error('Erro ao buscar pedidos via AJAX:', error);
                    // Opcional: exibir uma mensagem de erro na interface
                });
        }

        // 1. No carregamento inicial da página, use os dados já disponíveis no JSP para popular os cartões.
        // A tabela já foi populada pelo JSP.
        document.addEventListener('DOMContentLoaded', () => {
            // Recupere a lista de pedidos que foi renderizada pelo JSP
            const initialPedidosData = [];
            const tbody = document.getElementById('pedidosTableBody');
            for (let i = 0; i < tbody.rows.length; i++) {
                const row = tbody.rows[i];
                if (row.cells.length === 6) { // Verifica se é uma linha de dados e não a de "Nenhum pedido"
                    initialPedidosData.push({
                        idPedido: row.cells[0].textContent,
                        clientepedido: { nome: row.cells[1].textContent },
                        dataPeedido: row.cells[2].textContent,
                        status: row.cells[3].textContent,
                        observacoes: row.cells[4].textContent === '-' ? '' : row.cells[4].textContent,
                        formapagamento: row.cells[5].textContent === '-' ? '' : row.cells[5].textContent
                    });
                }
            }
            atualizarCartoes(initialPedidosData);

            // 2. Em seguida, configure o intervalo para futuras atualizações via AJAX.
            setInterval(carregarEAtualizarPedidosViaAjax, 3); // Atualiza a cada 30 segundos
        });
    </script>
</body>
</html>