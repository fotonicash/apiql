package br.com.fotonica.apiql;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Date;

import org.junit.Test;

public class ConvertStrintToDate {

	@Test
	public void convertStringToDate() {
		String data = "2020-08-15";
		Date date = APIQuery.toDate(data);
		assertNotNull(date);
		assertTrue(date.toString().equals("Sat Aug 15 00:00:00 BRT 2020"));
	}
	
}
