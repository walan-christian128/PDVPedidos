<%
String empresa = (String) session.getAttribute("empresa"); // Salva o valor antes de invalidar

session.invalidate(); // Encerra a sess�o atual

HttpSession novaSessao = request.getSession(true); // Cria uma nova sess�o
novaSessao.setAttribute("empresa", empresa); // Restaura o atributo da empresa

response.sendRedirect("LoginPedido.jsp"); // Redireciona
%>