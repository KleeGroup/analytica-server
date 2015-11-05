package io.analytica.store;

public interface AnalyticaStoreQuery {

	public String SEPARATOR="/";
	
	public String getApplicationName();
	
	public String getLocation();
	
	public String getCategory();
	
	public AnalyticaStoreDateRange getDateRange();
}
