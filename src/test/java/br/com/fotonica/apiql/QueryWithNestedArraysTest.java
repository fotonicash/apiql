package br.com.fotonica.apiql;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.google.gson.internal.LinkedTreeMap;

public class QueryWithNestedArraysTest {
	
	ObjectToJPQL objToJPQL = new ObjectToJPQL();
	APIQueryParams params = new APIQueryParams();
	String entityName = "EspecificacaoMaterial";

	@Test
	public void testeWithOneJoin() {
		LinkedTreeMap<String, Object> filter = new LinkedTreeMap<String, Object>();
		LinkedTreeMap<String, Object> especificacao = new LinkedTreeMap<String, Object>();
		LinkedTreeMap<String, Object> parametro = new LinkedTreeMap<String, Object>();
		
		parametro.put("titulo", "abc");
		especificacao.put("parametro", parametro);
		
		List<Object> especificacoes = new ArrayList<Object>();
		especificacoes.add(especificacao);
		
		filter.put("especificacoes", especificacoes);
		
		params.setEntityName(entityName);
		params.setFilter(filter);
		String jpql = objToJPQL.build(params);
		assertEquals("SELECT a FROM EspecificacaoMaterial a JOIN a.especificacoes especificacoes WHERE LOWER(CAST(especificacoes.parametro.titulo as string)) LIKE LOWER(:especificacoesparametrotitulo)", jpql);
	}
	
	@Test
	public void testeWithTwoJoins() {
		LinkedTreeMap<String, Object> filter = new LinkedTreeMap<String, Object>();
		LinkedTreeMap<String, Object> quadro = new LinkedTreeMap<String, Object>();
		LinkedTreeMap<String, Object> especificacao = new LinkedTreeMap<String, Object>();
		LinkedTreeMap<String, Object> parametro = new LinkedTreeMap<String, Object>();
		
		parametro.put("titulo", "abc");
		especificacao.put("parametro", parametro);
		
		List<Object> especificacoes = new ArrayList<Object>();
		especificacoes.add(especificacao);
		quadro.put("especificacoes", especificacoes);
		
		List<Object> quadros = new ArrayList<Object>();
		quadros.add(quadro);
		
		filter.put("quadros", quadros);
		
		params.setEntityName(entityName);
		params.setFilter(filter);
		String jpql = objToJPQL.build(params);
		assertEquals("SELECT a FROM EspecificacaoMaterial a JOIN a.quadros quadros JOIN quadros.especificacoes especificacoes  WHERE LOWER(CAST(especificacoes.parametro.titulo as string)) LIKE LOWER(:especificacoesparametrotitulo)", jpql);
	}
	
}
