package br.com.fotonica.apiql;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import br.com.fotonica.apiql.exception.APIQueryException;
import br.com.fotonica.apiql.type.AttributeType;
import br.com.fotonica.apiql.type.ConnectorType;
import br.com.fotonica.apiql.type.OperationType;

public class ObjectToJPQL {

	private String template;
	private Boolean hasMoreThanOneCondition;
	private Boolean hasMoreThanOneJoin;
	private Map<String, Object> mapValues;

	/**
	 * PSQL based on filter
	 * 
	 * @param instance
	 * @param orderByFieldName
	 * @param clazz
	 * @param methods
	 * @return
	 */
	public String build(APIQueryParams params) {
		mapValues = new HashMap<String, Object>();
		hasMoreThanOneCondition = false;
		hasMoreThanOneJoin = false;
		template = "SELECT a FROM #entityName a #join #where #orderby";
		distinct(params.getDistinct());
		setEntityName(params.getEntityName());
		if (params.getFilter() != null) {
			where("a", params.getFilter());
		}
		orderby("a", params.getSort());
		configClousures();
		return template.replaceAll("  ", " ").trim();

	}

	@SuppressWarnings("unchecked")
	public void where(String prefix, Map<String, Object> map) {

		if (map.get("value") != null && map.get("op") != null) { // Node

			OperationType operator = null;
			ConnectorType c = null;
			AttributeType attrType = null;
			if (map.get("c") != null)
				c = map.get("c") instanceof ConnectorType ? (ConnectorType) map.get("c")
						: ConnectorType.valueOf((String) map.get("c"));
			if (map.get("t") != null)
				attrType = AttributeType.valueOf((String) map.get("t"));
			
			try {
				operator = OperationType.valueOf((String) map.get("op"));
			} catch (IllegalArgumentException ex) { // enum invalido
				System.err.println(
						"java.lang.IllegalArgumentException: No enum constant br.com.fotonica.apiql.OperationType."
								+ map.get("op"));
			}

			if (operator == OperationType.memberOf
					&& !(isPrimitiveType(map.get("value")) || map.get("value") instanceof String)) {
				throw new APIQueryException("Expect primitive type or String!");
			}

			if (map.get("value") instanceof List) {
				if (operator == OperationType.between) { // build between operator
					List<Object> values = (List<Object>) map.get("value");

					mapValues.put("inicio", APIQuery.parse((String) values.get(0), attrType));
					mapValues.put("fim", APIQuery.parse((String) values.get(1), attrType));
					String[] valuesName = new String[] { "inicio", "fim" };
					String condition = APIQueryTemplate.build(OperationType.between, prefix, Arrays.asList(valuesName));
					addCondition(condition, null);
					return;
				} else if (operator != OperationType.in) {
					throw new APIQueryException("Expect in operator to List type!");
				}
			}

			buildCondition(prefix, map.get("value"), operator, c, attrType);
			return;
		}

		Set<Entry<String, Object>> set = map.entrySet();
		for (Entry<String, Object> element : set) { // Nav through JSON as MAP
			Object value = element.getValue();
			if (value instanceof Map) { // Object
				where(String.format("%s.%s", prefix, element.getKey()), (Map<String, Object>) value);
			} else if (value instanceof List) { // Array
				List<Map<String, Object>> array = (List<Map<String, Object>>) value;
				addJoin(String.format("%s.%s %s", prefix, element.getKey(), element.getKey(), element.getKey()));
				for (Map<String, Object> obj : array) {
					where(element.getKey(), obj);
				}
			} else { // Node
				buildCondition(String.format("%s.%s", prefix, element.getKey()), value, null, null, null);
//				System.err.println(String.format("%s.%s: %s [%s]", prefix, element.getKey(), value, value.getClass().getSimpleName()));
			}
		}
	}

	/**
	 * Build condition
	 * 
	 * @param parentName
	 * @param field
	 * @param value
	 */
	public void buildCondition(String key, Object value, OperationType op, ConnectorType connector,
			AttributeType attrType) {
		String _key = key.replaceAll("\\.", "");

		if (isPrimitiveType(value)) {
			mapValues.put(_key, value);
			String condition = APIQueryTemplate.build(op != null ? op : OperationType.eq, key, ":" + _key);
			addCondition(condition, connector);
		} else if (value instanceof String) {
			
			if(attrType != null && attrType == AttributeType.date) {
				mapValues.put(_key, APIQuery.toDate((String) value));
			}
			else if (op == OperationType.memberOf) {
				mapValues.put(_key, value);
			} else {
				mapValues.put(_key, APIQueryTemplate.getStringValue(op, (String) value));
			}
			
			String condition = APIQueryTemplate.build(op, key, ":" + _key);
			addCondition(condition, connector);
		} else if (value instanceof List) {
			mapValues.put(_key, value);
			String condition = APIQueryTemplate.build(op, key, ":" + _key);
			addCondition(condition, connector);
		} else {
			throw new APIQueryException("Type value not allowed");
		}
	}

	/**
	 * Add condition to psql template
	 * 
	 * @param condition
	 */
	public void addCondition(String condition, ConnectorType connector) {
		if (hasMoreThanOneCondition) {
			template = template.replace("#where", String.format("%s #where",
					String.format(" %s ", connector != null ? connector : "and") + condition));
		} else {
			template = template.replace("#where", String.format("WHERE %s #where", condition));
			hasMoreThanOneCondition = true;
		}
	}

	/**
	 * Add join to psql template
	 * 
	 * @param join
	 */
	public void addJoin(String join) {
		if (hasMoreThanOneJoin) {
			template = template.replace("#join",String.format("JOIN %s #join ", join));
		} else {
			template = template.replace("#join", String.format("JOIN %s #join ", join));
			hasMoreThanOneJoin = true;
		}
	}

	/**
	 * Add order by clouse
	 * 
	 * @param parentName
	 * @param fieldNames
	 */
	public void orderby(String parentName, List<String> fieldNames) {
		String orderBy = "";
		if (fieldNames != null && !fieldNames.isEmpty()) {
			// Concatenar atributos que serão usados na ordenação
			for (String fieldName : fieldNames) {
				orderBy += parentName + "." + fieldName;

				// Caso ainda existam outros atributos
				if (!fieldName.equals(fieldNames.get(fieldNames.size() - 1))) {
					orderBy += ", ";
				}
			}
			template = template.replace("#orderby", "ORDER BY " + orderBy);
		} else {
			template = template.replace("#orderby", orderBy);
		}
	}

	public void distinct(Boolean distinct) {
		if (distinct != null && distinct) {
			template = template.replace("a", "distinct a");
		}
	}

	private boolean isPrimitiveType(Object value) {
		return value instanceof Integer || value instanceof Double || value instanceof Boolean;
	}

	private void setEntityName(String entityName) {
		template = template.replace("#entityName", entityName);
	}

	public void configClousures() {
		template = template.replace(" #join", "");
		template = template.replace(" #where", "");
	}

	public Query execute(EntityManager entityManager) {
		Query query = entityManager.createQuery(template);
		if (mapValues != null) {
			for (Map.Entry<String, Object> item : mapValues.entrySet()) {
				query.setParameter(item.getKey(), item.getValue());
			}
		}
		return query;
	}

	public Query executeWithNativeQuery(EntityManager entityManager) {
		Query query = entityManager.createNativeQuery(template);
		if (mapValues != null) {
			for (Map.Entry<String, Object> item : mapValues.entrySet()) {
				query.setParameter(item.getKey(), item.getValue());
			}
		}
		return query;
	}

	public Query count(EntityManager entityManager) {
		template = template.replaceFirst("a", "COUNT(a)");
		Query query = entityManager.createQuery(template);
		if (mapValues != null) {
			for (Map.Entry<String, Object> item : mapValues.entrySet()) {
				query.setParameter(item.getKey(), item.getValue());
			}
		}
		return query;
	}
	
	public Map<String, Object> getMapValues(){
		return mapValues;
	}
}
