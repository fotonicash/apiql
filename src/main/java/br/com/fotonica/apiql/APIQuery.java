package br.com.fotonica.apiql;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;

import br.com.fotonica.apiql.type.AttributeType;

public class APIQuery {

	static Object parse(Object value, AttributeType attrType) {
		if (attrType != null) {
			if (attrType == AttributeType.timestamp) {
				return toTimestamp(value);
			} else if(attrType == AttributeType.date) {
				return toDate(value);
			}
		}
		return toInteger(value);
	}

	static Long toTimestamp(Object value) {
		Long v = Long.valueOf(Math.round(Double.parseDouble((String)value)));
		return v;
	}

	static Date toDate(Object value) {
		return Date.from(LocalDate.parse((String) value, DateTimeFormatter.ofPattern("yyyy-MM-dd")).atStartOfDay()
				.atZone(ZoneId.systemDefault()).toInstant());
	}

	static Integer toInteger(Object value) {
		return Integer.valueOf((int) Math.round((double) value));
	}
}
