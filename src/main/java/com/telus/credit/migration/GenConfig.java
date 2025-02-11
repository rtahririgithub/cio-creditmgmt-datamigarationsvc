package com.telus.credit.migration;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;


@Configuration
public class GenConfig {

  @Value("${spring.datasource.url}")
  private String datasourceUrl;

  
public String getDatasourceUrl() {
	return datasourceUrl;
}

public void setDatasourceUrl(String datasourceUrl) {
	this.datasourceUrl = datasourceUrl;
}






}
