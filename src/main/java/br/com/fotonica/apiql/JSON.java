package br.com.fotonica.apiql;

import java.io.IOException;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.internal.LinkedTreeMap;

public class JSON {

	private static final ObjectMapper mapper = new ObjectMapper();
	
	/**
	 * Converte uma string JSON para um objeto de uma determinada classe.
	 * @param jsonText JSON a se converter
	 * @return Objeto da classe com dados do JSON
	 */
	public synchronized static Map<String, Object> toMap(String jsonText) {
		Map<String, Object> map = null;
		try {
			map = mapper.readValue(jsonText, LinkedTreeMap.class);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return map;
	}
}
