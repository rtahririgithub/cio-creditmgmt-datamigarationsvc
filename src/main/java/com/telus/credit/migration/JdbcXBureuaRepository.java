package com.telus.credit.migration;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.telus.credit.migration.thrd.MyAsyncService;
@Repository
public class JdbcXBureuaRepository implements XBureuaRepository {
	private static final Logger LOGGER = LoggerFactory.getLogger(JdbcXBureuaRepository.class);
	  private static final String XBUREAU_TABLE_NAME = "crprofl.xbureau_concatenated";// "crprofl.xbureau ";
	  int counter=1;
	@Autowired
	  private JdbcTemplate jdbcTemplate;
	  
	@Override
	public int save(XBureauDbModel val) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	  public int updateProcessedind(Long id) {
		    return jdbcTemplate.update("UPDATE " + XBUREAU_TABLE_NAME +" SET processedind=true where customerid=?" , id);
		  }
	
	@Override
	public void updateProcessedindInBulk(ArrayList custIdList) {
		if(custIdList ==null || custIdList.isEmpty()) {
			return;
		}
		StringBuilder str = new StringBuilder("");
		 for (int j = 0; j < custIdList.size(); j++) {
			 str.append(custIdList.get(j)).append(",");		 	
		 }
		 str.deleteCharAt(str.length()-1);
		 jdbcTemplate.update("UPDATE " + XBUREAU_TABLE_NAME +" SET processedind=true where customerid in (" + str +")");		 
	}	
	
	
	@Override
	public XBureauDbModel findById(Long id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int deleteById(Long id) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public List<XBureauDbModel> findAll() {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public int deleteAll() {
		// TODO Auto-generated method stub
		return 0;
	}
	

	@Override
	public List<XBureauDbModel> findByProcessedind(boolean processedind , int limit) {
		
		    List<XBureauDbModel> list = jdbcTemplate.query(
				   	"select * from " + XBUREAU_TABLE_NAME +" WHERE processedind =? FOR UPDATE SKIP LOCKED FETCH FIRST ? ROWS ONLY"
			        ,BeanPropertyRowMapper.newInstance(XBureauDbModel.class)
			      , processedind, limit
			       )
				   ;
		   
		    LOGGER.info( "findByProcessedind: counter=" + counter + " SKIPLOCKED query size:"+list.size());
		    counter++;
		    return list;
	}
	
/*
	public List<XBureauDbModel> findByProcessedind1(boolean processedind) {
		    List<XBureauDbModel> list = jdbcTemplate.query(
				   	"SELECT * from " + XBUREAU_TABLE_NAME +" WHERE processedind =?"
			        ,BeanPropertyRowMapper.newInstance(XBureauDbModel.class)
			        , processedind)
				   ;		   
		   
		    return list;

	}	
*/
	
	

}
