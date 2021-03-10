package br.com.fotonica.apiql;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;

import com.google.gson.internal.LinkedTreeMap;

import br.com.fotonica.apiql.exception.APIQueryException;

public class ObjectToJPQLTest {
	
	ObjectToJPQL objToJPQL = new ObjectToJPQL();
	APIQueryParams params = new APIQueryParams();
	String entityName = "Material";
	
	@Test
	public void psqlSimples() {
		params.setEntityName(entityName);
		String jpql = objToJPQL.build(params);
		assertEquals("SELECT a FROM Material a", jpql);
	}
	
	@Test
	public void psqlComObjecto() {
		LinkedTreeMap<String, Object> filter = new LinkedTreeMap<String, Object>();
		filter.put("nome", "teste");
		params.setEntityName(entityName);
		params.setFilter(filter);
		String jpql = objToJPQL.build(params);
		assertEquals("SELECT a FROM Material a WHERE LOWER(CAST(a.nome as string)) LIKE LOWER(:anome)", jpql);
	}
	
	@Test
	public void psqlComOperadorNotLike() {
		LinkedTreeMap<String, Object> filter = new LinkedTreeMap<String, Object>();
		
		LinkedTreeMap<String, Object> jpoint = new LinkedTreeMap<String, Object>();
		jpoint.put("value", "teste");
		jpoint.put("op", "ncontains");
		
		filter.put("nome", jpoint);
		params.setEntityName(entityName);
		params.setFilter(filter);
		String jpql = objToJPQL.build(params);
		
		String jpqlExpected = "SELECT a FROM Material a WHERE LOWER(CAST(a.nome as string)) NOT LIKE LOWER(:anome)";
		assertEquals(jpqlExpected, jpql);
	}
	
	@Test
	public void psqlComOperadorNotEqual() {
		LinkedTreeMap<String, Object> filter = new LinkedTreeMap<String, Object>();
		
		LinkedTreeMap<String, Object> jpoint = new LinkedTreeMap<String, Object>();
		jpoint.put("value", "teste");
		jpoint.put("op", "neq");
		
		filter.put("nome", jpoint);
		params.setEntityName(entityName);
		params.setFilter(filter);
		String jpql = objToJPQL.build(params);
		
		String jpqlExpected = "SELECT a FROM Material a WHERE a.nome != :anome";
		assertEquals(jpqlExpected, jpql);
	}
	
	@Test
	public void psqlComOperadorMaiorQAndMenorQ() {
		LinkedTreeMap<String, Object> filter = new LinkedTreeMap<String, Object>();
		
		LinkedTreeMap<String, Object> jpointMaiorQ = new LinkedTreeMap<String, Object>();
		jpointMaiorQ.put("value", "10");
		jpointMaiorQ.put("op", "gt");
		
		LinkedTreeMap<String, Object> jpointMenorQ = new LinkedTreeMap<String, Object>();
		jpointMenorQ.put("value", "100");
		jpointMenorQ.put("op", "lt");
		
		filter.put("idade", jpointMaiorQ);
		filter.put("peso", jpointMenorQ);
		params.setEntityName("Pessoa");
		params.setFilter(filter);
		String jpql = objToJPQL.build(params);
		
		String jpqlExpected = "SELECT a FROM Pessoa a WHERE a.idade > :aidade and a.peso < :apeso";
		assertEquals(jpqlExpected, jpql);
	}
	
	@Test
	public void psqlComArray() {
		LinkedTreeMap<String, Object> filter = new LinkedTreeMap<String, Object>();
		
		List<LinkedTreeMap<String, Object>> vinculos = new ArrayList<LinkedTreeMap<String, Object>>();
		
		LinkedTreeMap<String, Object> setor = new LinkedTreeMap<String, Object>();
		setor.put("sigla", "AIN");
	
		LinkedTreeMap<String, Object> v1 = new LinkedTreeMap<String, Object>();
		v1.put("setor", setor);
		v1.put("nome", "programador");
		
		vinculos.add(v1);
		
		filter.put("nome", "teste");
		filter.put("vinculos", vinculos);
		
		params.setEntityName("Colaborador");
		params.setFilter(filter);
		String jpql = objToJPQL.build(params);

		String jpqlExpected = "SELECT a FROM Colaborador a JOIN a.vinculos vinculos WHERE LOWER(CAST(a.nome as string)) LIKE LOWER(:anome) and LOWER(CAST(vinculos.setor.sigla as string)) LIKE LOWER(:vinculossetorsigla) and LOWER(CAST(vinculos.nome as string)) LIKE LOWER(:vinculosnome)";
		
		assertEquals(jpqlExpected, jpql);
	}
	
	@Test
	public void psqlComObjectoAninhado() {
		LinkedTreeMap<String, Object> filter = new LinkedTreeMap<String, Object>();
		LinkedTreeMap<String, Object> subfilter = new LinkedTreeMap<String, Object>();
		subfilter.put("nome", "Produtos acabados");
		subfilter.put("setor", "APA");
		
		filter.put("tipoMaterial", subfilter);
		params.setEntityName(entityName);
		params.setFilter(filter);
		String jpql = objToJPQL.build(params);
		
		String psqlExpected = "SELECT a FROM Material a WHERE LOWER(CAST(a.tipoMaterial.nome as string)) LIKE LOWER(:atipoMaterialnome) and LOWER(CAST(a.tipoMaterial.setor as string)) LIKE LOWER(:atipoMaterialsetor)";
		assertEquals(psqlExpected, jpql);
	}
	
	@Test
	public void psqlComObjectoEArrayAninhado() {
		String json = "{" +
				"    \"tipoMaterial\": {" +
				"        \"nome\": \"Produtos Acabados\"," +
				"        \"setores\": [" +
				"            {" +
				"                \"sigla\": \"APA\"" +
				"            }," +
				"            {" +
				"                \"sigla\": \"AIN\"" +
				"            }" +
				"        ]" +
				"    }" +
				"}";
		
		
		Map<String, Object> filter = JSON.toMap(json);

		params.setEntityName(entityName);
		params.setFilter(filter);
		String jpql = objToJPQL.build(params);
		
		String psqlExpected = "SELECT a FROM Material a JOIN a.tipoMaterial.setores setores " +
				"WHERE LOWER(CAST(a.tipoMaterial.nome as string)) LIKE LOWER(:atipoMaterialnome) " +
				"and LOWER(CAST(setores.sigla as string)) LIKE LOWER(:setoressigla) " +
				"and LOWER(CAST(setores.sigla as string)) LIKE LOWER(:setoressigla)";
		assertEquals(psqlExpected, jpql);
		assertEquals("%Produtos Acabados%", objToJPQL.getMapValues().get("atipoMaterialnome"));
		// TODO como fica o set dos parâmetros :setoressigla ? imagino que deveria existe 2 (como no teste abaixo), um
		//  para o apa e outro para o ain. Além disso como a operação default é 'and' para essa operação, então nesse
		//  cenário nenhum resultado será encontrado, pois a sigla do setor não pode ser as duas ao mesmo tempo.
		//assertEquals("%AIN%", objToJPQL.getMapValues().get("setoressigla0"));
		//assertEquals("%APA%", objToJPQL.getMapValues().get("setoressigla1"));
	}

	@Test
	public void psqlComArrayAndOpIn1() {
		String json = "{" +
				"    \"id\": {" +
				"        \"value\": [1, 2, 3]," +
				"        \"op\": \"in\"" +
				"    }" +
				"}";
		Map<String, Object> filter = JSON.toMap(json);

		params.setEntityName(entityName);
		params.setFilter(filter);

		String jpql = objToJPQL.build(params);
		String psqlExpected = "SELECT a FROM Material a WHERE a.id in :aid";
		assertEquals(psqlExpected, jpql);
		assertEquals(Arrays.asList(1, 2, 3), objToJPQL.getMapValues().get("aid"));
	}

	@Test
	public void psqlComArrayAndOpIn2() {
		String json = "{" +
				"    \"volumes.id\": {" +
				"        \"value\": [1, 2, 3]," +
				"        \"op\": \"in\"" +
				"    }" +
				"}";
		Map<String, Object> filter = JSON.toMap(json);

		params.setEntityName("LoteMaterial");
		params.setFilter(filter);

		String jpql = objToJPQL.build(params);
		String psqlExpected = "SELECT a FROM LoteMaterial a WHERE a.volumes.id in :avolumesid";
		assertEquals(psqlExpected, jpql);
		assertEquals(Arrays.asList(1, 2, 3), objToJPQL.getMapValues().get("avolumesid"));
	}

	@Test
	public void psqlComOpMemberOf() {
		String json = "{" +
				"    \"tiposPessoaJuridica\": {" +
				"        \"value\": \"FABRICANTE\"," +
				"        \"op\": \"memberOf\"" +
				"    }" +
				"}";
		Map<String, Object> filter = JSON.toMap(json);

		params.setEntityName("PessoaJuridica");
		params.setFilter(filter);

		String jpql = objToJPQL.build(params);
		String psqlExpected = "SELECT a FROM PessoaJuridica a WHERE :atiposPessoaJuridica MEMBER OF a.tiposPessoaJuridica";
		assertEquals(psqlExpected, jpql);
		assertEquals("FABRICANTE", objToJPQL.getMapValues().get("atiposPessoaJuridica"));
	}

	@Test
	public void psqlComOpMemberOfTypeError() {
		String json = "{" +
				"    \"tiposPessoaJuridica\": {" +
				"        \"value\": [\"FABRICANTE\"]," +
				"        \"op\": \"memberOf\"" +
				"    }" +
				"}";
		Map<String, Object> filter = JSON.toMap(json);

		params.setEntityName("PessoaJuridica");
		params.setFilter(filter);

		try {
			objToJPQL.build(params);
			fail();
		} catch (APIQueryException exception) {
			assertEquals("Expect primitive type or String!", exception.getMessage());
		}

	}

}
