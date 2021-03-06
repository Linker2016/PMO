package com.pom.dashboard.service;

import java.io.File;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.pmo.dashboard.entity.CandidateInfo;
import com.pmo.dashboard.entity.CandidatePush;

public interface CandidateService
{
    List<CandidateInfo> queryCandidateList(CandidateInfo candidate);

	int queryCandidateCount(CandidateInfo candidate);
	
	String queryCandidateResumePath(CandidateInfo candidate);
	
    List<LinkedHashMap<String, String>> queryExportData(CandidateInfo candidate);
    
    void transferExportData( List<LinkedHashMap<String,String>> candidateDatalist,List<String> conditionList,File file);
    
    List<CandidateInfo> queryMyCandidateList(CandidateInfo candidate);

	int queryMyCandidateCount(CandidateInfo candidate);
	
	List<Map<String,String>> queryCusDeptInfo();  
	
	CandidateInfo queryCandidateForId(String candidateId);
	
	boolean updateCandidateInterviewStatus(CandidateInfo candidate);
	
	boolean insertCandidatePushData(CandidatePush candidatePush);
	
	boolean updateCandidatePushStatus(CandidatePush candidatePush);
}
