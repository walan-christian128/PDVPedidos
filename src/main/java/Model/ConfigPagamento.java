package Model;

public class ConfigPagamento {
	 private int id;
	    private int empresaId;
	    private String gateway;
	    private String chavePix;
	    private String clientId;
	    private String clientSecret;
	    private String accessToken;

	    // Getters e Setters
	    public int getId() {
	        return id;
	    }
	    public void setId(int id) {
	        this.id = id;
	    }

	    public int getEmpresaId() {
	        return empresaId;
	    }
	    public void setEmpresaId(int empresaId) {
	        this.empresaId = empresaId;
	    }

	    public String getGateway() {
	        return gateway;
	    }
	    public void setGateway(String gateway) {
	        this.gateway = gateway;
	    }

	    public String getChavePix() {
	        return chavePix;
	    }
	    public void setChavePix(String chavePix) {
	        this.chavePix = chavePix;
	    }

	    public String getClientId() {
	        return clientId;
	    }
	    public void setClientId(String clientId) {
	        this.clientId = clientId;
	    }

	    public String getClientSecret() {
	        return clientSecret;
	    }
	    public void setClientSecret(String clientSecret) {
	        this.clientSecret = clientSecret;
	    }

	    public String getAccessToken() {
	        return accessToken;
	    }
	    public void setAccessToken(String accessToken) {
	        this.accessToken = accessToken;
	    }

}
