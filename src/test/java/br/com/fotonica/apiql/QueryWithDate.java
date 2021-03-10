package br.com.fotonica.apiql;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Date;

import org.junit.Test;

import com.google.gson.internal.LinkedTreeMap;

public class QueryWithDate {

	ObjectToJPQL objToJPQL = new ObjectToJPQL();
	APIQueryParams params = new APIQueryParams();
	String entityName = "Material";

	@Test
	public void buscarMaterialPorDataValidade() {

		LinkedTreeMap<String, Object> filter = new LinkedTreeMap<String, Object>();

		LinkedTreeMap<String, Object> dataValidade = new LinkedTreeMap<String, Object>();
		dataValidade.put("value", "2020-08-15");
		dataValidade.put("op", "eq");
		dataValidade.put("t", "date");

		filter.put("dataValidade", dataValidade);
		params.setEntityName(entityName);
		params.setFilter(filter);
		
		objToJPQL.build(params);
		
		assertNotNull(objToJPQL.getMapValues());
		assertTrue(objToJPQL.getMapValues().get("adataValidade") instanceof Date);
	}
	
	@Test
	public void buscarMaterialPorDataValidadeSemDefinitTipoDate() {

		LinkedTreeMap<String, Object> filter = new LinkedTreeMap<String, Object>();

		LinkedTreeMap<String, Object> dataValidade = new LinkedTreeMap<String, Object>();
		dataValidade.put("value", "2020-08-15");
		dataValidade.put("op", "eq");

		filter.put("dataValidade", dataValidade);
		params.setEntityName(entityName);
		params.setFilter(filter);
		
		objToJPQL.build(params);
		
		assertNotNull(objToJPQL.getMapValues());
		assertFalse(objToJPQL.getMapValues().get("adataValidade") instanceof Date);
	}

}
