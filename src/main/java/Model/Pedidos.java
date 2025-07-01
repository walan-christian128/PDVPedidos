package Model;

public class Pedidos {
 private int idPedido;
 public int getIdPedido() {
	return idPedido;
}
 public void setIdPedido(int idPedido) {
	this.idPedido = idPedido;
 }
 public Clientepedido getClientepedido() {
	return clientepedido;
 }
 public void setClientepedido(Clientepedido clientepedido) {
	this.clientepedido = clientepedido;
 }
 public String getDataPeedido() {
	return dataPeedido;
 }
 public void setDataPeedido(String dataPeedido) {
	this.dataPeedido = dataPeedido;
 }
 public String getStatus() {
	return status;
 }
 public void setStatus(String status) {
	this.status = status;
 }
 public String getObservacoes() {
	return observacoes;
 }
 public void setObservacoes(String observacoes) {
	this.observacoes = observacoes;
 }
 private Clientepedido clientepedido;
 private String dataPeedido;
 private String status;
 private String observacoes;
 private String formapagamento;
 public String getFormapagamento() {
	return formapagamento;
 }
 public void setFormapagamento(String formapagamento) {
	this.formapagamento = formapagamento;
 }
}
