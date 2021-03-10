package br.com.fotonica.apiql.type;

public enum OperationType{
	eq("="), neq("<>"), contains("LIKE"), ncontains("LIKE"), gt(">"), lt("<"), in("in"), between("between"), memberOf("memberOf");
	
	private final String name;
	
	OperationType(String name) {
		this.name = name;
	}
	
	@Override
	public String toString() {
		return name;
	}
}