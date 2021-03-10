package br.com.fotonica.apiql;

import static org.junit.Assert.assertEquals;

import org.junit.jupiter.api.Test;

import com.google.gson.internal.LinkedTreeMap;

public class QueryWithNumberAsString {
	
	ObjectToJPQL objToJPQL = new ObjectToJPQL();
	APIQueryParams params = new APIQueryParams();
	String entityName = "Material";
	
	@Test
	public void createQueryWithInteger() {
		LinkedTreeMap<String, Object> filter = new LinkedTreeMap<String, Object>();
		filter.put("peso", 100);
		params.setEntityName(entityName);
		params.setFilter(filter);
		String jpql = objToJPQL.build(params);
		assertEquals("SELECT a FROM Material a WHERE a.peso = :apeso", jpql);
	}
	
	@Test
	public void createQueryWithDouble() {
		LinkedTreeMap<String, Object> filter = new LinkedTreeMap<String, Object>();
		filter.put("peso", 2.78);
		params.setEntityName(entityName);
		params.setFilter(filter);
		String jpql = objToJPQL.build(params);
		assertEquals("SELECT a FROM Material a WHERE a.peso = :apeso", jpql);
	}
	
	@Test
	public void createQueryWithNegative() {
		LinkedTreeMap<String, Object> filter = new LinkedTreeMap<String, Object>();
		filter.put("peso", -56.89);
		params.setEntityName(entityName);
		params.setFilter(filter);
		String jpql = objToJPQL.build(params);
		assertEquals("SELECT a FROM Material a WHERE a.peso = :apeso", jpql);
		assertEquals(-56.89, (Double) objToJPQL.getMapValues().get("apeso"), 0.01);
	}
}
