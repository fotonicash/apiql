package br.com.fotonica.apiql;

import java.util.List;
import java.util.Map;

public class APIQueryParams implements Cloneable{
	
	private Map<String, Object> filter;
	private Integer page;
	private Integer size;
	private List<String> sort;
	private Boolean distinct;
	private String entityName;
	
	@Override
	public APIQueryParams clone() throws CloneNotSupportedException {
		APIQueryParams params = new APIQueryParams();
		params.setFilter(filter);
		params.setPage(page);
		params.setSize(size);
		params.setSort(sort);
		params.setDistinct(distinct);
		params.setEntityName(entityName);
		return params;
	}
	
	public Map<String, Object> getFilter() {
		return filter;
	}

	public void setFilter(Map<String, Object> filter) {
		this.filter = filter;
	}

	public Integer getPage() {
		return page;
	}
	
	public void setPage(Integer page) {
		this.page = page;
	}
	
	public Integer getSize() {
		return size;
	}
	
	public void setSize(Integer size) {
		this.size = size;
	}
	
	public List<String> getSort() {
		return sort;
	}
	
	public void setSort(List<String> sort) {
		this.sort = sort;
	}
	
	public Boolean getDistinct() {
		return distinct;
	}
	
	public void setDistinct(Boolean distinct) {
		this.distinct = distinct;
	}

	public String getEntityName() {
		return entityName;
	}

	public void setEntityName(String entityName) {
		this.entityName = entityName;
	}
	
	

}
