package com.pmo.dashboard.controller;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.pmo.dashboard.entity.CSDept;
import com.pmo.dashboard.entity.Demand;
import com.pmo.dashboard.entity.HSBCDept;
import com.pmo.dashboard.entity.PageCondition;
import com.pmo.dashboard.util.Constants;
import com.pmo.dashboard.util.Utils;
import com.pom.dashboard.service.CSDeptService;
import com.pom.dashboard.service.DemandService;
import com.pom.dashboard.service.HSBCDeptService;

import jxl.Workbook;
import jxl.format.Alignment;
import jxl.format.Border;
import jxl.format.BorderLineStyle;
import jxl.write.Label;
import jxl.write.WritableCellFormat;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;

/**
 * 招聘需求的controller
 * 
 * @author tianzhao
 * @version1.0 2017-8-25 14:54:57
 */
@Controller
@RequestMapping(value="/demand")
public class DemandController {
	
	@Resource
	DemandService demandService;
	
	@Resource
	HSBCDeptService hsbcDeptService;
	
	@Resource
	CSDeptService csDeptService;

	private static Logger logger = LoggerFactory.getLogger(DemandController.class);
	
	@RequestMapping("/demandInfo")
	public String demandInfo(){
		return "/demand/demandQuery";
	}
	
	/**
	 * 加载Department信息
	 * @return
	 */
	@RequestMapping("/loadDepartment")
	@ResponseBody
	public List<HSBCDept> loadDepartment(){
		List<HSBCDept> list = hsbcDeptService.queryHSBCDeptName();
		return list;
	}
	
	/**
	 * 加载SubDepartment信息
	 * @param hsbcDeptName
	 * @return
	 */
	@RequestMapping("/loadSubDepartment")
	@ResponseBody
	public List<HSBCDept> loadSubDepartment(String hsbcDeptName){
		List<HSBCDept> list = hsbcDeptService.queryHSBCSubDeptNameByDeptName(hsbcDeptName);
		return list;
	}
	
	/**
	 * 加载交付部信息
	 * @param csBuName
	 * @return
	 */
	@RequestMapping("/loadScSubDeptName")
	@ResponseBody
	public List<CSDept> loadScSubDeptName(String csBuName){
		List<CSDept> list = csDeptService.queryCSSubDeptNameByCsBuName(csBuName);
		return list;
	}
	
	/**
	 * 按条件查询招聘需求和分页功能
	 * @param demand
	 * @param pageCondition
	 * @return
	 */
	@RequestMapping("/queryDemandList")
	@ResponseBody
	public Object demandQuery(String csBuName,Demand demand,PageCondition pageCondition,HttpServletRequest request){
		if("".equals(pageCondition.getCurrPage()) || pageCondition.getCurrPage() == null){
			pageCondition.setCurrPage(1);
		}
		//String csBuName = request.getParameter("csBuName");
		List<Demand> list = demandService.queryDemandList(demand,pageCondition,csBuName,request);
		//把查询到的结果存到session中
		request.getSession().setAttribute("demandList", list);
		Map<String,Object> result = new HashMap<String,Object>();
		result.put("list", list);
		result.put("pageCondition", pageCondition);
		return result;
	}
	
	/**
	 * 导出查询到的需求信息
	 * @param condition
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("/exportExcel")
	public HttpServletResponse exportExcel(String condition,HttpServletRequest request,HttpServletResponse response){
		Map<String, Object> params = (Map<String, Object>)request.getSession().getAttribute("demandParams");
		List<Demand> demandList = demandService.queryAllDemand(params);
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			String date = sdf.format(new Date());
			
			String fileName =  Constants.PATH+""+Utils.getUUID()+".xls";
			// 创建可写入的Excel工作簿
			File file = new File(fileName);
			if(!file.exists()){
				file.createNewFile();
			}
			//以fileName为文件名来创建一个Workbook
			WritableWorkbook wwb = Workbook.createWorkbook(file);
			// 创建工作表
			WritableSheet ws = wwb.createSheet("Demand Tracker", 0);
			//List<Demand> list = demandService.queryAllDemand(demand,csBuName);
			//List<Demand> demandList = (List<Demand>)request.getSession().getAttribute("demandList");
			String[] array = condition.split(",");
			for (int i = 0;i < array.length;i++) {
				Label label = new Label(i, 0, array[i]);
				ws.addCell(label);
			}
			for (int i =0 ; i < demandList.size(); i++) {
				int j = 0;
				if(condition.indexOf("RR #")!= -1){
					Label lab = new Label(j++, i+1, demandList.get(i).getRr());
					ws.addCell(lab);
				}
				if(condition.indexOf("Job Code")!= -1){
					Label lab = new Label(j++, i+1, demandList.get(i).getJobCode());
					ws.addCell(lab);
				}
				if(condition.indexOf("Tech/Skill")!= -1){
					Label lab = new Label(j++, i+1, demandList.get(i).getSkill());
					ws.addCell(lab);
				}
				if(condition.indexOf("Requestor")!= -1){
					Label lab = new Label(j++, i+1, demandList.get(i).getRequestor());
					ws.addCell(lab);
				}
				if(condition.indexOf("Position")!= -1){
					Label lab = new Label(j++, i+1, demandList.get(i).getPosition());
					ws.addCell(lab);
				}
				if(condition.indexOf("Department")!= -1){
					Label lab = new Label(j++, i+1, demandList.get(i).getHsbcDept().getHsbcDeptName());
					ws.addCell(lab);
				}
				if(condition.indexOf("Sub - Department")!= -1){
					Label lab = new Label(j++, i+1, demandList.get(i).getHsbcDept().getHsbcSubDeptName());
					ws.addCell(lab);
				}
				if(condition.indexOf("Location")!= -1){
					Label lab = new Label(j++, i+1, demandList.get(i).getLocation());
					ws.addCell(lab);
				}
				if(condition.indexOf("Req published Date")!= -1){
					Label lab = new Label(j++, i+1, demandList.get(i).getReqPublishedDate());
					ws.addCell(lab);
				}
				if(condition.indexOf("Ageing")!= -1){
					Label lab = new Label(j++, i+1, demandList.get(i).getAgeing());
					ws.addCell(lab);
				}
				if(condition.indexOf("No. of Profiles Sent to HSBC")!= -1){
					Label lab = new Label(j++, i+1, demandList.get(i).getProfilesNo());
					ws.addCell(lab);
				}
				if(condition.indexOf("No of Profiles Interviewed")!= -1){
					Label lab = new Label(j++, i+1, demandList.get(i).getInterviewedNo());
					ws.addCell(lab);
				}
				if(condition.indexOf("Status")!= -1){
					Label lab = new Label(j++, i+1, demandList.get(i).getStatus());
					ws.addCell(lab);
				}
				if(condition.indexOf("Proposed Date of Joining")!= -1){
					Label lab = new Label(j++, i+1, demandList.get(i).getProposedJoiningDate());
					ws.addCell(lab);
				}
				if(condition.indexOf("SOW signed")!= -1){
					Label lab = new Label(j++, i+1, demandList.get(i).getSowSigned());
					ws.addCell(lab);
				}
				if(condition.indexOf("BGV Cleared")!= -1){
					Label lab = new Label(j++, i+1, demandList.get(i).getBgvCleared());
					ws.addCell(lab);
				}
				
				if(condition.indexOf("Reason for Abort / Delay")!= -1){
					Label lab = new Label(j++, i+1, demandList.get(i).getReason());
					ws.addCell(lab);
				}
				if(condition.indexOf("Remark")!= -1){
					Label lab = new Label(j++, i+1, demandList.get(i).getRemark());
					ws.addCell(lab);
				}
				if(condition.indexOf("交付部")!= -1){
					Label lab = new Label(j++, i+1, demandList.get(i).getCsSubDept());
					ws.addCell(lab);
				}
				if(condition.indexOf("Planned Onboard date")!= -1){
					Label lab = new Label(j++, i+1, demandList.get(i).getPlannedOnboardDate());
					ws.addCell(lab);
				}
				if(condition.indexOf("DO number")!= -1){
					Label lab = new Label(j++, i+1, demandList.get(i).getDoNumber());
					ws.addCell(lab);
				}
				if(condition.indexOf("HR Priority")!= -1){
					Label lab = new Label(j++, i+1, demandList.get(i).getHrPriority());
					ws.addCell(lab);
				}
			}
			//写入数据
			wwb.write();
			//关闭文件
			wwb.close();
			
			String filename = "GSV Engagement Dashboard_"+date+".xls";

            // 以流的形式下载文件。
            InputStream fis = new BufferedInputStream(new FileInputStream(fileName));
            byte[] buffer = new byte[fis.available()];
            fis.read(buffer);
            fis.close();
            // 清空response
            response.reset();
            // 设置response的Header
            response.addHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(filename,"UTF-8"));
            //response.setContentType("application/octet-stream");
            response.setContentType("application/vnd.ms-excel");
            response.addHeader("Content-Length", "" + file.length());
            OutputStream toClient = new BufferedOutputStream(response.getOutputStream());
            
            toClient.write(buffer);
            toClient.flush();
            toClient.close();
            
           file.delete();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * 需求详情页面信息
	 * @return
	 */
	@RequestMapping("/demandDetail")
	public String demandDetail(String demandId,Model model,HttpServletRequest request){
		List<Demand> list = (List<Demand>) request.getSession().getAttribute("demandList");
		for (Demand demand : list) {
			if(demand.getDemandId().equals(demandId)){
				model.addAttribute("demand", demand);
				return "/demand/demandDetail";
			}
		}
		return "/demand/demandDetail";
	}
	
}
