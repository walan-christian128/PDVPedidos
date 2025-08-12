<%@ page import="Model.Clientepedido" %>
<%@ page import="DAO.ClientesPedidosDAO" %>
<%@ page import="Model.Pedidos" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="java.util.Base64" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<%
    
    Clientepedido clienteSessao = (Clientepedido) session.getAttribute("clienteLogado");
    String nomeCliente = (clienteSessao != null && clienteSessao.getNome() != null) ? clienteSessao.getNome() : "Cliente";
    Clientepedido clienteModal = (Clientepedido) request.getAttribute("clienteParaModal");

    if (clienteSessao == null) {
        response.sendRedirect("LoginPedido.jsp");
        return;
    }
    String empresaCliente = (String) session.getAttribute("empresa");
    List<Pedidos> PedidosCliente = new ArrayList<>();
    try {
    	ClientesPedidosDAO clienteDAO = new ClientesPedidosDAO(empresaCliente);
    	PedidosCliente = clienteDAO.pedidosCliente(clienteSessao.getId());

    } catch (Exception e) {

    }

%>


<!doctype html>
<html lang="pt-br">
<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Menu do Cliente</title>
    <link rel="icon" href="img/2992655_click_computer_currency_dollar_money_icon.png">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet" integrity="sha384-T3c6CoIi6uLrA9TneNEoa7RxnatzjcDSCmG1MXxSR1GAsXEV/Dwwykc2MPK8M2HN" crossorigin="anonymous">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.1/font/bootstrap-icons.css">
    <link rel="stylesheet" href="https://cdn.datatables.net/1.13.7/css/jquery.dataTables.css" />

    <style>
        body {
            background-image: url('img/background-menu.webp');
            background-size: cover;
            background-position: center center;
            background-repeat: no-repeat;
            margin: 0;
            padding: 0;
            min-height: 100vh;
            width: 100vw;
        }

        .offcanvas-body .nav-link {
            font-size: 1.5rem;
            padding: 0.75rem 1rem;
            display: flex;
            align-items: center;
            gap: 10px;
            color: inherit;
            text-decoration: none;
        }

        .offcanvas-body .nav-link .icon {
            font-size: 1.8rem;
        }
    </style>
</head>
<body>

    <i class="bi bi-list d-flex ms-3 mt-3" data-bs-toggle="offcanvas" href="#offcanvasMenu" role="button" aria-controls="offcanvasMenu" style="font-size: 3rem; cursor: pointer; color: white;"></i>

    <div class="offcanvas offcanvas-start bg-light" tabindex="-1" id="offcanvasMenu" aria-labelledby="offcanvasMenuLabel">
        <div class="offcanvas-header">
            <h5 class="offcanvas-title" id="offcanvasMenuLabel">Menu do Cliente</h5>
            <button type="button" class="btn-close" data-bs-dismiss="offcanvas" aria-label="Close"></button>
        </div>
        <div class="offcanvas-body">
            <div class="mb-3">
                <p class="h5 text-primary">Bem-vindo, <%= nomeCliente %></p>
            </div>
            <ul class="navbar-nav me-auto mb-2 mb-lg-0">
             
				<li class="nav-item"><a class="nav-link active"
					href="selecionacp?id=<%=clienteSessao.getId()%>"> <span
						class="icon"><i class="bi bi-person-circle"></i></span> <span
						class="txt-link">Meus Dados</span>
				</a></li>
				<li class="nav-item">
                    <a class="nav-link active" href="#" data-bs-toggle="modal" data-bs-target="#modalAlterarSenha">
                        <span class="icon"><i class="bi bi-card-checklist"></i></span> <span class="txt-link">Meus pedidos</span>
                    </a>
                </li>
                <li class="nav-item">
                    <a class="nav-link active" href="detroyLogaut.jsp">
                        <span class="icon"><i class="bi bi-box-arrow-right"></i></span> <span class="txt-link">Sair</span>
                    </a>
                </li>
            </ul>
        </div>
    </div>

    

<div class="modal fade" id="modalDadosCadastrais" tabindex="-1" aria-labelledby="modalDadosCadastraisLabel" aria-hidden="true">
    <div class="modal-dialog modal-dialog-centered modal-dialog-scrollable">
        <div class="modal-content">
            <div class="modal-header">
                <h1 class="modal-title fs-5" id="modalDadosCadastraisLabel">Alterar Dados Cadastrais</h1>
                <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
            </div>
            <div class="modal-body">
                <form action="atualizaDadosCliente" method="post" class="needs-validation" novalidate>
                    <%-- Input para o ID do cliente, usado para identificar qual cliente atualizar --%>
                    <input type="hidden" name="idCliente" id="idClienteInput" value="<%= clienteModal != null ? clienteModal.getId() : "" %>">

                    <div class="mb-3">
                        <label for="nomeCliente" class="form-label">Nome Completo:</label>
                        <input type="text" class="form-control" id="nomeCliente" name="nomeCliente" required
                               value="<%= clienteModal != null ? clienteModal.getNome() : "" %>">
                        <div class="invalid-feedback">Por favor, insira seu nome.</div>
                    </div>
                    <div class="mb-3">
                        <label for="telefoneCliente" class="form-label">Telefone:</label>
                        <input type="text" class="form-control" id="telefoneCliente" name="telefoneCliente" placeholder="(99) 9999-9999"
                               value="<%= clienteModal != null ? clienteModal.getCelular() : "" %>">
                    </div>
                    <div class="mb-3">
                        <label for="emailCliente" class="form-label">E-mail:</label>
                        <input type="email" class="form-control" id="emailCliente" name="emailCliente" required
                               value="<%= clienteModal != null ? clienteModal.getEmail() : "" %>">
                        <div class="invalid-feedback">Por favor, insira um e-mail válido.</div>
                    </div>

                    <h5 class="text-secondary mt-4 mb-3">Endereço</h5>
                    <div class="row">
                        <div class="col-md-9 mb-3">
                            <label for="ruaCliente" class="form-label">Rua:</label>
                            <input type="text" class="form-control" id="ruaCliente" name="ruaCliente"
                                   value="<%= clienteModal != null ? clienteModal.getEndereco() : "" %>">
                        </div>
                        <div class="col-md-3 mb-3">
                            <label for="numeroCliente" class="form-label">Número:</label>
                            <input type="text" class="form-control" id="numeroCliente" name="numeroCliente"
                                   value="<%= clienteModal != null ? clienteModal.getNumero() : "" %>">
                        </div>
                    </div>
                    <div class="row">
                        <div class="col-md-6 mb-3">
                            <label for="bairroCliente" class="form-label">Bairro:</label>
                            <input type="text" class="form-control" id="bairroCliente" name="bairroCliente"
                                   value="<%= clienteModal != null ? clienteModal.getBairro() : "" %>">
                        </div>
                        <div class="col-md-6 mb-3">
                            <label for="cidadeCliente" class="form-label">Cidade:</label>
                            <input type="text" class="form-control" id="cidadeCliente" name="cidadeCliente"
                                   value="<%= clienteModal != null ? clienteModal.getCidade() : "" %>">
                        </div>
                    </div>
                    <div class="mb-3">
                        <label for="estadoCliente" class="form-label">Estado:</label>
                        <input type="text" class="form-control" id="estadoCliente" name="estadoCliente" placeholder="Ex: MG"
                               value="<%= clienteModal != null ? clienteModal.getUf() : "" %>">
                    </div>

                    <div class="d-flex justify-content-end gap-2 mt-3">
                        <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Fechar</button>
                        <button type="submit" class="btn btn-primary">Salvar Alterações</button>
                    </div>
                </form>
            </div>
        </div>
    </div>
</div>

    <div class="modal fade" id="modalAlterarSenha" tabindex="-1" aria-labelledby="modalAlterarSenhaLabel" aria-hidden="true">
        <div class="modal-dialog modal-lg modal-dialog-centered modal-dialog-scrollable">
            <div class="modal-content">
                <div class="modal-header">
                    <h1 class="modal-title fs-5" id="modalAlterarSenhaLabel">Meus Pedidos</h1>
                    <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                </div>
                <div class="modal-body">
 <div class="col-md-12">
    <div class="table-container">
        <table class="table table-dark table-striped table-hover">
            <thead>
                <tr>
               
                    <th>Data</th>
                    <th>Status</th>
                    <th>Total</th>
                    <th>Observação</th>
                    <th>Pagamento</th>
                    <th>Ações</th>
                </tr>
            </thead>
            <tbody>
                <%if(PedidosCliente != null && ! PedidosCliente.isEmpty()){ 
                    for(int i = 0 ; i< PedidosCliente.size();i++ ){
                        String status = PedidosCliente.get(i).getStatus();
                        String badgeClass = "";
                        switch(status.toLowerCase()) {
                            case "entregue":
                                badgeClass = "bg-success";
                                break;
                            case "reprovado":
                                badgeClass = "bg-danger";
                                break;
                            case "pendente":
                                badgeClass = "bg-warning text-dark";
                                break;
                            default:
                                badgeClass = "bg-secondary";
                                break;
                        }
                %>
                <tr>
                 
                    <td><%=PedidosCliente.get(i).getDataPeedido() %></td>
                    <td><span class="badge <%= badgeClass %>"><%= status %></span></td>
                    <td>R$ <%= PedidosCliente.get(i).getTotalPedido() %></td>
                    <td class="text-truncate" style="max-width: 150px;"><%= PedidosCliente.get(i).getObservacoes() %></td>
                   <td><%=PedidosCliente.get(i).getFormapagamento() != null && !PedidosCliente.get(i).getFormapagamento().isEmpty()
		? PedidosCliente.get(i).getFormapagamento()
		: "-"%></td>
                    <td>
                        <div class="d-flex flex-column flex-md-row gap-1">
                            <button type="button" class="btn btn-sm btn-info visualize-pedido"
                                data-id-pedido="<%=PedidosCliente.get(i).getIdPedido()%>"
                                data-bs-toggle="modal" data-bs-target="#gereciarPedido">
                                <i class="bi bi-eye"></i>
                            </button>
                           
                        </div>
                    </td>
                </tr>
                <%
                    }
                } else{
                %>
                <tr>
                    <td colspan="8" class="text-center">Nenhum pedido realizado ainda.</td>
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
            </div>
        </div>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js" integrity="sha384-C6RzsynM9kWDrMNeT87bh95OGNyZPhcTNXj1NW7RuBCsyN/o0jlpcV8Qyq46cDfL" crossorigin="anonymous"></script>
    <script>
        (function () {
            'use strict';
            var forms = document.querySelectorAll('.needs-validation');
            Array.prototype.slice.call(forms).forEach(function (form) {
                form.addEventListener('submit', function (event) {
                    if (!form.checkValidity()) {
                        event.preventDefault();
                        event.stopPropagation();
                    }
                    form.classList.add('was-validated');
                }, false);
            });
           
					
				<%if (request.getAttribute("clienteParaModal") != null) {%>
					$('#modalDadosCadastrais').modal('show');
				<%}%>
        })();
    </script>
    <script>
    (function () {
        'use strict';
        var forms = document.querySelectorAll('.needs-validation');
        Array.prototype.slice.call(forms).forEach(function (form) {
            form.addEventListener('submit', function (event) {
                if (!form.checkValidity()) {
                    event.preventDefault();
                    event.stopPropagation();
                }
                form.classList.add('was-validated');
            }, false);
        });

        // Este é o scriptlet que abre o modal
        // Ele verifica se o objeto 'clienteParaModal' foi colocado na requisição pelo Servlet
        <%if (request.getAttribute("clienteParaModal") != null) {%>
            var modal = new bootstrap.Modal(document.getElementById('modalDadosCadastrais'));
            modal.show();
        <%}%>
    })();
</script>
</body>
</html>