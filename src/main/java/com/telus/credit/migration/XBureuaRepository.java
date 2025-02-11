package com.telus.credit.migration;
import java.util.ArrayList;
import java.util.List;
public interface XBureuaRepository {
  int save(XBureauDbModel val);
  int updateProcessedind(Long id);
  XBureauDbModel findById(Long id);
  int deleteById(Long id);
  List<XBureauDbModel> findAll();
  //List<XBureauDbModel> findByProcessedind(boolean processedind);
  List<XBureauDbModel> findByProcessedind(boolean processedind, int limit);

  int deleteAll();
void updateProcessedindInBulk(ArrayList custIdList);


}

