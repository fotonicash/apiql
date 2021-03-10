package br.com.fotonica.apiql;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.google.gson.internal.LinkedTreeMap;

import br.com.fotonica.apiql.exception.APIQueryException;

public class Conectors {
	
	ObjectToJPQL objToJPQL = new ObjectToJPQL();
	APIQueryParams params = new APIQueryParams();
	String entityName = "Material";

	@Test
	public void createNestedQueryWithORConnector() {
		LinkedTreeMap<String, Object> filter = new LinkedTreeMap<String, Object>();
		
		LinkedTreeMap<String, Object> jpointMaiorQ = new LinkedTreeMap<String, Object>();
		jpointMaiorQ.put("value", "10");
		jpointMaiorQ.put("op", "gt");
		
		LinkedTreeMap<String, Object> jpointMenorQ = new LinkedTreeMap<String, Object>();
		jpointMenorQ.put("value", "100");
		jpointMenorQ.put("op", "lt");
		jpointMenorQ.put("c", "or"); 
		
		filter.put("idade", jpointMaiorQ);
		filter.put("peso", jpointMenorQ);
		params.setEntityName("Pessoa");
		params.setFilter(filter);
		String jpql = objToJPQL.build(params);
		
		String jpqlExpected = "SELECT a FROM Pessoa a WHERE a.idade > :aidade or a.peso < :apeso";
		assertEquals(jpqlExpected, jpql);
	}
	
	@Test
	public void psqlComArrayWithORConnector() {
		LinkedTreeMap<String, Object> filter = new LinkedTreeMap<String, Object>();
		
		List<LinkedTreeMap<String, Object>> vinculos = new ArrayList<LinkedTreeMap<String, Object>>();
		
		LinkedTreeMap<String, Object> setor = new LinkedTreeMap<String, Object>();
		
		LinkedTreeMap<String, Object> siglaConstrain = new LinkedTreeMap<String, Object>();
		siglaConstrain.put("value", "AIN");
		siglaConstrain.put("op", "contains");
		siglaConstrain.put("c", "or");
		
		setor.put("sigla", siglaConstrain);
	
		LinkedTreeMap<String, Object> v1 = new LinkedTreeMap<String, Object>();
		LinkedTreeMap<String, Object> v1NomeConstrain = new LinkedTreeMap<String, Object>();
		v1NomeConstrain.put("value", "programador");
		v1NomeConstrain.put("op", "contains");
		v1NomeConstrain.put("c", "or");
		
		v1.put("setor", setor);
		v1.put("nome", v1NomeConstrain);
		
		vinculos.add(v1);
		
		filter.put("nome", "teste");
		filter.put("vinculos", vinculos);
		
		params.setEntityName("Colaborador");
		params.setFilter(filter);
		String jpql = objToJPQL.build(params);

		String jpqlExpected = "SELECT a FROM Colaborador a JOIN a.vinculos vinculos WHERE LOWER(CAST(a.nome as string)) LIKE LOWER(:anome) or LOWER(CAST(vinculos.setor.sigla as string)) LIKE LOWER(:vinculossetorsigla) or LOWER(CAST(vinculos.nome as string)) LIKE LOWER(:vinculosnome)";

		assertEquals(jpqlExpected, jpql);
	}
	
	@Test
	public void psqlComObjectoAninhadoWithORConnector() {
		LinkedTreeMap<String, Object> filter = new LinkedTreeMap<String, Object>();
		LinkedTreeMap<String, Object> subfilter = new LinkedTreeMap<String, Object>();
		
		LinkedTreeMap<String, Object> produtoSetorConstrain = new LinkedTreeMap<String, Object>();
		produtoSetorConstrain.put("value", "APA");
		produtoSetorConstrain.put("op", "contains");
		produtoSetorConstrain.put("c", "or");
		
		subfilter.put("nome", "Produtos acabados");
		subfilter.put("setor", produtoSetorConstrain);
		
		filter.put("tipoMaterial", subfilter);
		params.setEntityName(entityName);
		params.setFilter(filter);
		String jpql = objToJPQL.build(params);
		
		String psqlExpected = "SELECT a FROM Material a WHERE LOWER(CAST(a.tipoMaterial.nome as string)) LIKE LOWER(:atipoMaterialnome) or LOWER(CAST(a.tipoMaterial.setor as string)) LIKE LOWER(:atipoMaterialsetor)";
		assertEquals(psqlExpected, jpql);
	}
	
	@Test
	public void tryUseConnectorWithList() {
		LinkedTreeMap<String, Object> filter = new LinkedTreeMap<String, Object>();
		
		List<LinkedTreeMap<String, Object>> vinculos = new ArrayList<LinkedTreeMap<String, Object>>();
		
		LinkedTreeMap<String, Object> setor = new LinkedTreeMap<String, Object>();
		
		LinkedTreeMap<String, Object> siglaConstrain = new LinkedTreeMap<String, Object>();
		siglaConstrain.put("value", "AIN");
		siglaConstrain.put("op", "contains");
		siglaConstrain.put("c", "or");
		
		setor.put("sigla", siglaConstrain);
	
		LinkedTreeMap<String, Object> v1 = new LinkedTreeMap<String, Object>();
		LinkedTreeMap<String, Object> v1NomeConstrain = new LinkedTreeMap<String, Object>();
		v1NomeConstrain.put("value", "programador");
		v1NomeConstrain.put("op", "contains");
		v1NomeConstrain.put("c", "or");
		
		v1.put("setor", setor);
		v1.put("nome", v1NomeConstrain);
		
		vinculos.add(v1);
		
		LinkedTreeMap<String, Object> vinculosContrain = new LinkedTreeMap<String, Object>();
		vinculosContrain.put("value", vinculos);
		vinculosContrain.put("op", "neq");
		vinculosContrain.put("c", "or");
		
		filter.put("nome", "teste");
		filter.put("vinculos", vinculosContrain);
		
		params.setEntityName("Colaborador");
		params.setFilter(filter);
		
		try {
			String jpql = objToJPQL.build(params);
		} catch (Exception e) {
			assert(e instanceof APIQueryException);
		}
	}
	
	
	@Test()
	public void connectorINWithList() {
		LinkedTreeMap<String, Object> filter = new LinkedTreeMap<String, Object>();
		
		List<LinkedTreeMap<String, Object>> vinculos = new ArrayList<LinkedTreeMap<String, Object>>();
		
		LinkedTreeMap<String, Object> setor = new LinkedTreeMap<String, Object>();
		
		LinkedTreeMap<String, Object> siglaConstrain = new LinkedTreeMap<String, Object>();
		siglaConstrain.put("value", "AIN");
		siglaConstrain.put("op", "contains");
		siglaConstrain.put("c", "or");
		
		setor.put("sigla", siglaConstrain);
	
		LinkedTreeMap<String, Object> v1 = new LinkedTreeMap<String, Object>();
		LinkedTreeMap<String, Object> v1NomeConstrain = new LinkedTreeMap<String, Object>();
		v1NomeConstrain.put("value", "programador");
		v1NomeConstrain.put("op", "contains");
		v1NomeConstrain.put("c", "or");
		
		v1.put("setor", setor);
		v1.put("nome", v1NomeConstrain);
		
		vinculos.add(v1);
		
		LinkedTreeMap<String, Object> vinculosContrain = new LinkedTreeMap<String, Object>();
		vinculosContrain.put("value", vinculos);
		vinculosContrain.put("op", "in");
		vinculosContrain.put("c", "or");
		
		filter.put("nome", "teste");
		filter.put("vinculos", vinculosContrain);
		
		params.setEntityName("Colaborador");
		params.setFilter(filter);
		
		String jpql = objToJPQL.build(params);
		System.out.println(jpql);
	}
	
}
