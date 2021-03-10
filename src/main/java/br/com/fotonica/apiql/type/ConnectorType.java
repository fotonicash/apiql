package br.com.fotonica.apiql.type;

public enum ConnectorType {
	
	and("and"), or("or");
	
	private final String name;
	
	ConnectorType(String name) {
		this.name = name;
	}
	
	@Override
	public String toString() {
		return name;
	}

}
