package com.telus.credit;


import java.sql.SQLException;
import java.util.List;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import com.telus.credit.migration.XBureauDbModel;
import com.telus.credit.migration.XBureuaRepository;


public class SpringMainClass {

	public static void main(String[] args) throws SQLException {
		AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();
		context.scan("com.telus.credit.db");
		context.refresh();

		XBureuaRepository repository = context.getBean(XBureuaRepository.class);

		// store
		repository.save(new XBureauDbModel());


		// retrieve
		 List<XBureauDbModel>  emp = repository.findAll();
		System.out.println(emp);



		// close the spring context
		context.close();
	}

}