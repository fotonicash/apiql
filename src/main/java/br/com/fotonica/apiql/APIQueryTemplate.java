package br.com.fotonica.apiql;

import java.util.List;

import br.com.fotonica.apiql.type.OperationType;

public class APIQueryTemplate {
	
	public static String not(String template, String simbolo) {
		if(simbolo.equals("=")) return template.replace(" = ", " != ");
		else if(simbolo.equals("LIKE")) return template.replace(" LIKE " , " NOT LIKE ");
		return template;
	}

	public static String eq(String attribute, String value) {
		return String.format("%s = %s", attribute, value);
	}

	public static String contains(String attribute, String value) {
		return String.format("LOWER(CAST(%s as string)) LIKE LOWER(%s)", attribute, value);
	}

	public static String gt(String attribute, String value) {
		return String.format("%s > %s", attribute, value);
	}
	
	public static String lt(String attribute, String value) {
		return String.format("%s < %s", attribute, value);
	}
	
	public static String in(String attribute, String value) {
		return String.format("%s in %s", attribute, value);
	}

	private static String memberOf(String attribute, String value) {
		return String.format("%s MEMBER OF %s", value, attribute);
	}
	
	public static String between(String attribute, List<String> values) {
		return String.format("%s between :%s and :%s", attribute, values.get(0), values.get(1));
	}
	
	/**
	 * String value based on operation
	 * @param op
	 * @param value
	 * @return
	 */
	public static String getStringValue(OperationType op, String value) {
		if(op != null) {
			if(op == OperationType.eq || op == OperationType.neq) return value;
		}
		return "%" + value + "%";
	}

	/**
	 * Create condition based on template, operation and value
	 * @param op
	 * @param attribute
	 * @param value
	 * @return
	 */
	public static String build(OperationType op, String attribute, Object value) {
		if (op == OperationType.eq) return eq(attribute, (String) value);
		else if(op == OperationType.neq) return not(eq(attribute, (String) value), "=");
		else if(op == OperationType.ncontains) return not(contains(attribute, (String) value), "LIKE");
		else if(op == OperationType.gt) return gt(attribute, (String) value);
		else if(op == OperationType.lt) return lt(attribute, (String) value);
		else if(op == OperationType.in) return in(attribute, (String) value);
		else if(op == OperationType.between) return between(attribute, (List<String>)value);
		else if(op == OperationType.memberOf) return memberOf(attribute, (String) value);
		else {
			return contains(attribute, (String) value);
		}
	}

}
