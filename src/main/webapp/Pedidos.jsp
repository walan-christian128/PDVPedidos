<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ page import="Model.Pedidos" %>
<%@ page import="Model.ItensPedidos" %>
<%@ page import="Model.Produtos" %>
<%@ page import="java.util.List"%>
<%@ page import="java.util.ArrayList"%>
<%@ page import="DAO.PedidosDAO" %>

<%
String empresa = (String) session.getAttribute("empresa");
if (empresa == null || empresa.isEmpty()) {
    // Se a sessão expirou, redireciona sem fazer forward para evitar erros de renderização
    response.sendRedirect("LoginExpirou.html");
    return;
}

List<Pedidos> pedidos = new ArrayList<>(); // Inicializa para garantir que não seja nulo
try {
    PedidosDAO daop = new PedidosDAO(empresa);
    pedidos = daop.listaTodosPedidosDoDia(); // Carrega os pedidos para a renderização inicial
} catch (Exception e) {
    e.printStackTrace(); // Loga qualquer erro na conexão ou consulta
    // Opcional: Adicionar uma mensagem de erro ao usuário na tela
}
%>

<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<meta http-equiv="refresh" content="30">
<link rel="icon"
	href="img/2992664_cart_dollar_mobile_shopping_smartphone_icon.png">
<link href="bootstrap/css/bootstrap.min.css" rel="stylesheet">
<script src="bootstrap/js/bootstrap.bundle.min.js"></script>
<title>Pedidos</title>
<link rel="stylesheet"
	href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.min.css">
<style>
    /* Estilo para a tabela dentro de um container rolável */
    .table-container {
        max-height: 500px; /* Altura máxima para a tabela */
        overflow-y: auto; /* Adiciona scroll vertical quando necessário */
        border-radius: 0.5rem; /* Bordas arredondadas */
        box-shadow: 0 0.125rem 0.25rem rgba(0, 0, 0, 0.075); /* Sombra */
    }
    .table-container table {
        margin-bottom: 0; /* Remove margem inferior da tabela dentro do container */
    }
    /* Para garantir que o cabeçalho fique fixo ao rolar */
    .table-container thead th {
        position: sticky;
        top: 0;
        background-color: #343a40; /* Cor de fundo do cabeçalho da tabela escura */
        color: white;
        z-index: 10;
    }
    #modalItensTableBody td {
  color: black !important;
  height: auto !important;
  visibility: visible !important;
  display: table-cell !important;
}

</style>
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
							<h2 class="card-title mb-0" id="totalPedidosEfetuados">0</h2>
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
                    <table id="pedidosDiario" class="table table-dark table-striped table-hover">
                        <thead>
                            <tr>
                                <th>Codigo do pedido</th>
                                <th>Nome</th>
                                <th>Data</th>
                                <th>Status</th>
                                <th>Total Pedido</th>
                                <th>Observação</th>
                                <th>Forma de pagamento</th>
                                <th>Ações</th>
                            </tr>
                        </thead>
                        <tbody id="pedidosTableBody">
				
    <%
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
        <td>
            <button type="button"
                    class="btn btn-sm btn-info visualizar-pedido"
                    data-id-pedido="<%=pedidos.get(i).getIdPedido()%>"
                    data-bs-toggle="modal"
                    data-bs-target="#gereciarPedido">
                <i class="bi bi-eye"></i> Detalhes
            </button>
        </td>
    </tr>
    <%
        }
    } else {
    %>
    <tr>
        <td colspan="8" class="text-center">Nenhum pedido encontrado para hoje.</td>
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

   <div class="modal fade" id="gereciarPedido" data-bs-backdrop="static"
     data-bs-keyboard="false" tabindex="-1"
     aria-labelledby="staticBackdropLabel" aria-hidden="true">
    <div class="modal-dialog modal-lg modal-dialog-centered modal-dialog-scrollable"> <div class="modal-content">
            <div class="modal-header">
                <h1 class="modal-title fs-5" id="staticBackdropLabel">Detalhes do Pedido <span id="modalPedidoId"></span></h1>
                <button type="button" class="btn-close" data-bs-dismiss="modal"
                        aria-label="Close"></button>
            </div>
            <div class="modal-body">
                <div class="bg-light p-4 rounded shadow mb-4"> <h5 class="text-center mb-3">Informações do Pedido e Cliente</h5>
                    <div class="row">
                        <div class="col-md-6">
                            <p><strong>ID Pedido:</strong> <span id="modalIdPedido"></span></p>
                            <p><strong>Data Pedido:</strong> <span id="modalDataPedido"></span></p>
                            <p><strong>Status:</strong> <span id="modalStatusPedido"></span></p>
                            <p><strong>Total Pedido:</strong> <span id="modalTotalPedido"></span></p>
                            <p><strong>Forma Pagamento:</strong> <span id="modalFormaPagamento"></span></p>
                            <p><strong>Observações:</strong> <span id="modalObservacoes"></span></p>
                        </div>
                        <div class="col-md-6">
                            <p><strong>Cliente:</strong> <span id="modalNomeCliente"></span></p>
                            <p><strong>Endereço:</strong> <span id="modalEnderecoCliente"></span></p>
                            <p><strong>Número:</strong> <span id="modalNumeroCliente"></span></p>
                            <p><strong>CEP:</strong> <span id="modalCepCliente"></span></p>
                            <p><strong>Estado:</strong> <span id="modalEstadoCliente"></span></p>
                            <p><strong>Telefone:</strong> <span id="modalTelefoneCliente"></span></p>
                            <p><strong>Email:</strong> <span id="modalEmailCliente"></span></p>
                        </div>
                    </div>
                </div>

                <div class="bg-white p-4 rounded shadow">
                    <h5 class="text-center mb-3">Itens do Pedido</h5>
                    <div class="table-responsive">
                        <table class="table table-striped table-hover">
                            <thead>
                                <tr>
                                    <th>Item</th>
                                    <th>Quantidade</th>
                                    <th>Preço Unitário</th>
                                    <th>Subtotal</th>
                                </tr>
                            </thead>
                            <tbody id="modalItensTableBody">
                                <tr>
                                    <td colspan="4" class="text-center text-muted">Carregando itens...</td>
                                </tr>
                            </tbody>
                            <tfoot>
                                <tr>
                                    <th colspan="3" class="text-end">Total Itens:</th>
                                    <th id="modalTotalItens">R$ 0,00</th>
                                </tr>
                            </tfoot>
                        </table>
                    </div>
                </div>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Fechar</button>
                </div>
        </div>
    </div>
</div>
       


 <script>
        document.addEventListener("DOMContentLoaded", function () {
            // Inicializa o modal uma vez para ser reutilizado
            const gereciarPedidoModal = new bootstrap.Modal(document.getElementById('gereciarPedido'));
            const modalItensTableBody = document.getElementById("modalItensTableBody");

            /**
             * Função que lida com o clique no botão "Detalhes" do pedido.
             * Faz a requisição AJAX e preenche o modal.
             */
          
            function adicionarListenersAosBotoesDetalhes() {
                // Remove listeners anteriores para evitar duplicação (importante se a tabela for recarregada via AJAX)
                document.querySelectorAll('.visualizar-pedido').forEach(button => {
                    button.removeEventListener('click', handleDetalhesClick);
                });
                // Adiciona o listener a todos os botões com a classe 'visualizar-pedido'
                document.querySelectorAll('.visualizar-pedido').forEach(button => {
                    button.addEventListener('click', handleDetalhesClick);
                });
            }

            /**
             * Função para atualizar os cartões de resumo com base na lista de pedidos.
             */
            function atualizarCartoes(pedidos) {
                let totalPedidosEfetuados = 0;
                let totalPedidosPendentes = 0;
                let totalPedidosEmPreparacao = 0;
                let totalPedidosEmEntrega = 0;
                let totalPedidosReprovados = 0;
                const clientesComPedidos = new Set();

                if (pedidos && pedidos.length > 0) {
                    pedidos.forEach(pedido => {
                        totalPedidosEfetuados++; // Todos os pedidos são considerados "efetuados"
                        if (pedido.status === 'PENDENTE') {
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

            /**
             * Função para carregar e atualizar a tabela e os cartões via AJAX.
             */
            function carregarEAtualizarPedidosViaAjax() {
                console.log("Atualizando pedidos via AJAX...");
                fetch('getPedidosJson.jsp') // O endpoint que retorna o JSON dos pedidos
                    .then(response => {
                        if (!response.ok) {
                            if (response.status === 401) {
                                window.location.href = 'LoginExpirou.html'; // Redireciona em caso de sessão expirada
                                return Promise.reject('Sessão expirada. Redirecionando...');
                            }
                            throw new Error('Erro ao carregar os pedidos: ' + response.statusText);
                        }
                        return response.json();
                    })
                    .then(pedidos => {
                        console.log("Pedidos recebidos via AJAX:", pedidos);
                        const tbody = document.getElementById('pedidosTableBody');
                        tbody.innerHTML = ''; // Limpa a tabela antes de adicionar os novos dados

                        if (pedidos && pedidos.length > 0) {
                            pedidos.forEach(pedido => {
                                const row = tbody.insertRow();
                                row.insertCell().textContent = pedido.idPedido;
                                row.insertCell().textContent = pedido.clientepedido ? pedido.clientepedido.nome : '-';
                                row.insertCell().textContent = pedido.dataPeedido;
                                row.insertCell().textContent = pedido.status;
                                row.insertCell().textContent = 'R$ ' + (pedido.totalPedido ? pedido.totalPedido.toFixed(2).replace('.', ',') : '0,00'); // Formata o valor
                                row.insertCell().textContent = pedido.observacoes && pedido.observacoes !== "" ? pedido.observacoes : '-';
                                row.insertCell().textContent = pedido.formapagamento && pedido.formapagamento !== "" ? pedido.formapagamento : '-';
                                
                                // Adiciona o botão de detalhes novamente, pois o tbody foi limpo
                                const acoesCell = row.insertCell();
                                const detalhesButton = document.createElement('button');
                                detalhesButton.type = 'button';
                                detalhesButton.className = 'btn btn-sm btn-info visualizar-pedido';
                                detalhesButton.setAttribute('data-id-pedido', pedido.idPedido);
                                detalhesButton.setAttribute('data-bs-toggle', 'modal');
                                detalhesButton.setAttribute('data-bs-target', '#gereciarPedido');
                                detalhesButton.innerHTML = '<i class="bi bi-eye"></i> Detalhes';
                                acoesCell.appendChild(detalhesButton);
                            });
                        } else {
                            const row = tbody.insertRow();
                            const cell = row.insertCell();
                            cell.colSpan = 8; // Ajuste o colspan para o número correto de colunas
                            cell.classList.add('text-center');
                            cell.textContent = 'Nenhum pedido encontrado para hoje.';
                        }

                        // Atualiza os cartões com os dados recém-carregados
                        atualizarCartoes(pedidos);
                        // Adiciona listeners aos novos botões gerados dinamicamente
                        adicionarListenersAosBotoesDetalhes(); 
                    })
                    .catch(error => {
                        console.error('Erro ao buscar pedidos via AJAX:', error);
                        // Opcional: exibir uma mensagem de erro na interface
                    });
            }

            // --- Lógica de Inicialização ---
            
            // No carregamento inicial, já temos os dados no JSP.
            // Extraímos os dados da tabela já renderizada para popular os cartões.
            const initialPedidosData = [];
            const tbodyInitial = document.getElementById('pedidosTableBody');
            // Verifica se há pedidos renderizados na tabela
            if (tbodyInitial && tbodyInitial.rows.length > 0 && tbodyInitial.rows[0].cells[0].colSpan !== 8) {
                for (let i = 0; i < tbodyInitial.rows.length; i++) {
                    const row = tbodyInitial.rows[i];
                    initialPedidosData.push({
                        idPedido: row.cells[0].textContent,
                        clientepedido: { nome: row.cells[1].textContent },
                        dataPeedido: row.cells[2].textContent,
                        status: row.cells[3].textContent,
                        totalPedido: parseFloat(row.cells[4].textContent.replace('R$ ', '').replace(',', '.')), // Converte para número
                        observacoes: row.cells[5].textContent === '-' ? '' : row.cells[5].textContent,
                        formapagamento: row.cells[6].textContent === '-' ? '' : row.cells[6].textContent
                    });
                }
            }
            atualizarCartoes(initialPedidosData);

            // Adiciona listeners aos botões de detalhes já existentes no carregamento inicial
            adicionarListenersAosBotoesDetalhes();

            // Em seguida, configure o intervalo para futuras atualizações via AJAX (se desejado).
            // setInterval(carregarEAtualizarPedidosViaAjax, 30000); // Atualiza a cada 30 segundos
        });
    </script>
    
    
<script>
// Função principal para lidar com o clique nos botões de detalhes do pedido
function handleDetalhesClick() {
    const idDoPedidoParaRequisicao = this.getAttribute('data-id-pedido');
    const modalItensTableBody = document.getElementById("modalItensTableBody");
    const modalElement = document.getElementById('gereciarPedido');

    // --- Ajuste para Gerenciamento do Modal ---
    // Verifica se já existe uma instância do modal.
    let gereciarPedidoModal = bootstrap.Modal.getInstance(modalElement);

    // Se não existir, cria uma nova instância.
    if (!gereciarPedidoModal) {
        gereciarPedidoModal = new bootstrap.Modal(modalElement);
    }
    // ------------------------------------------

    // Exibe mensagem de carregando
    modalItensTableBody.innerHTML = `<tr><td colspan="4" class="text-center text-muted">Carregando itens...</td></tr>`;
    document.getElementById("modalTotalItens").innerText = "R$ 0,00";

    // Mostra o modal (utiliza a instância existente ou a recém-criada)
    gereciarPedidoModal.show();

    const urlDeRequisicao = "selecionarPedido?id=" + encodeURIComponent(idDoPedidoParaRequisicao);

    fetch(urlDeRequisicao)
        .then(response => {
            if (!response.ok) {
                if (response.status === 401) {
                    window.location.href = 'LoginExpirou.html';
                    return Promise.reject('Sessão expirada.');
                }
                return response.text().then(text => { 
                    throw new Error(text || `Erro HTTP ${response.status}: ${response.statusText}`); 
                });
            }
            return response.json();
        })
        .then(itensPedido => {
            console.log("Detalhes do pedido recebidos com sucesso:", itensPedido);

            if (itensPedido && itensPedido.length > 0) {
                const primeiroItem = itensPedido[0];
                const pedidoInfo = primeiroItem.pedido || {};
                const clienteInfo = pedidoInfo.clientepedido || {};

                // Preenchimento de informações gerais do pedido e cliente
                document.getElementById("modalIdPedido").innerText = pedidoInfo.idPedido || '-';
                document.getElementById("modalDataPedido").innerText = pedidoInfo.dataPeedido || '-';
                document.getElementById("modalStatusPedido").innerText = pedidoInfo.status || '-';

                const totalPedidoValue = pedidoInfo.totalPedido || 0;
                const totalPedidoFormatado = typeof totalPedidoValue === 'number'
                    ? totalPedidoValue.toFixed(2).replace('.', ',')
                    : '0,00';
                document.getElementById("modalTotalPedido").innerText = 'R$ ' + totalPedidoFormatado;

                document.getElementById("modalFormaPagamento").innerText = pedidoInfo.formapagamento || "-";
                document.getElementById("modalObservacoes").innerText = pedidoInfo.observacoes || "-";

                document.getElementById("modalNomeCliente").innerText = clienteInfo.nome || '-';
                document.getElementById("modalEnderecoCliente").innerText = clienteInfo.endereco || '-';
                document.getElementById("modalNumeroCliente").innerText = clienteInfo.numero || "-";
                document.getElementById("modalEstadoCliente").innerText = clienteInfo.uf || "-";
                document.getElementById("modalTelefoneCliente").innerText = clienteInfo.telefone || "-";
                document.getElementById("modalEmailCliente").innerText = clienteInfo.email || "-";

                // Limpa a tabela antes de inserir
                modalItensTableBody.innerHTML = "";
                let totalItens = 0;

                // Loop para preencher a tabela de itens
                itensPedido.forEach(item => {
                    const descricao = item.produto?.descricao || '-';
                    
                    const quantidade = parseFloat(item.quantidade) || 0;
                    const precoUnitario = parseFloat(item.precoUnitario) || 0;
                    
                    const subtotal = quantidade * precoUnitario;
                    totalItens += subtotal;

                    const precoFormatado = precoUnitario.toFixed(2).replace('.', ',');
                    const subtotalFormatado = subtotal.toFixed(2).replace('.', ',');

                    // Criação dos elementos da linha da tabela
                    const tr = document.createElement('tr');

                    const tdDescricao = document.createElement('td');
                    tdDescricao.textContent = descricao;

                    const tdQtd = document.createElement('td');
                    tdQtd.textContent = quantidade;

                    const tdPreco = document.createElement('td');
                    tdPreco.textContent = "R$ " + precoFormatado; 

                    const tdSubtotal = document.createElement('td');
                    tdSubtotal.textContent = "R$ " + subtotalFormatado; 

                    tr.appendChild(tdDescricao);
                    tr.appendChild(tdQtd);
                    tr.appendChild(tdPreco);
                    tr.appendChild(tdSubtotal);

                    modalItensTableBody.appendChild(tr);
                });

                // Atualiza o total de itens no rodapé da tabela
                document.getElementById("modalTotalItens").innerText = "R$ " + totalItens.toLocaleString('pt-BR', {minimumFractionDigits: 2, maximumFractionDigits: 2});


            } else {
                // Caso não haja itens
                modalItensTableBody.innerHTML = `<tr><td colspan="4" class="text-center text-muted">Nenhum item encontrado para este pedido.</td></tr>`;
                document.getElementById("modalTotalItens").innerText = "R$ 0,00";
            }
        })
        .catch(error => {
            // Tratamento de erros
            console.error('Erro ao carregar detalhes do pedido:', error);
            modalItensTableBody.innerHTML = `<tr><td colspan="4" class="text-center text-danger">Erro ao carregar detalhes: ${error.message || 'Erro desconhecido'}</td></tr>`;
            document.getElementById("modalTotalItens").innerText = "R$ 0,00";
            alert("Ocorreu um erro ao carregar os detalhes do pedido. Verifique o console.");
        });
}

</script>
</body>
</html>