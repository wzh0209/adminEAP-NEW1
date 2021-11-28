package com.radish.master.controller.workmanage;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.beanutils.PropertyUtils;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.hibernate.type.StringType;
import org.hibernate.type.Type;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.alibaba.fastjson.JSONArray;
import com.cnpc.framework.activiti.pojo.Constants;
import com.cnpc.framework.activiti.service.RuntimePageService;
import com.cnpc.framework.base.controller.UploaderController;
import com.cnpc.framework.base.entity.User;
import com.cnpc.framework.base.pojo.FileResult;
import com.cnpc.framework.base.pojo.Result;
import com.cnpc.framework.base.service.BaseService;
import com.cnpc.framework.utils.DateUtil;
import com.cnpc.framework.utils.FileUtil;
import com.cnpc.framework.utils.PropertiesUtil;
import com.cnpc.framework.utils.SecurityUtil;
import com.cnpc.framework.utils.StrUtil;
import com.radish.master.entity.BgwjFile;
import com.radish.master.entity.UserLeave;
import com.radish.master.entity.files.OfficeDoc;
import com.radish.master.entity.review.ReviewBid;
import com.radish.master.entity.safty.CheckFileAQ;
import com.radish.master.service.CommonService;
import com.radish.master.system.FileHelper;
import com.radish.master.system.SpringUtil;

@Controller
@RequestMapping("/leaveuser")
public class LeaveUserController {
	String prefix = "workmanage/leaveuser";
	
	@Resource
	 private RuntimePageService runtimePageService;
	
	@Autowired
	private BaseService baseService;
	
	@Resource
    private CommonService commonService;
	 @RequestMapping(value="/projectdetailfile", method = RequestMethod.GET)
	    public String projectdetailfile(String id, HttpServletRequest request) throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException{
			List<BgwjFile> wjs = baseService.findMapBySql("select id  from tbl_officefile where form_ID='"+id+"'", new Object[]{}, new Type[]{StringType.INSTANCE}, BgwjFile.class);
	        String wjid = "";
			for(int i =0;i<wjs.size();i++){
	        	if(i==wjs.size()-1){
	        		wjid += wjs.get(i).getId();
	        	}else {
	        		wjid += wjs.get(i).getId()+",";
	        	}
	        }
			request.setAttribute("lx", request.getParameter("lx"));
	        request.setAttribute("fields", wjid);
	        request.setAttribute("id", id);
	        return prefix+"/query_file";
	    }
	
	@RequestMapping("/addindex")
	public String addindex(HttpServletRequest request){
		String useid = SecurityUtil.getUserId();
		List<UserLeave> lzs = baseService.findBySql("select * from tbl_userLeave where userId='"+useid+"' and leaveStatus<>'50'", UserLeave.class);
		if(lzs.size()>0){
			request.setAttribute("users", JSONArray.toJSONString(lzs.get(0)));
		}
		request.setAttribute("id", useid);
        request.setAttribute("eduOptions", JSONArray.toJSONString(commonService.getEducationCombobox()));
        request.setAttribute("ethOptions", JSONArray.toJSONString(commonService.getEthnicCombobox()));
		return prefix +"/addindex";
	}
	@RequestMapping("/bmshindex")
	public String bmshindex(HttpServletRequest request){
		User u = SecurityUtil.getUser();
		String deptid = u.getDeptId();
		request.setAttribute("deptid", deptid);
		request.setAttribute("auidter", u.getName());
		return prefix+"/bmshindex";
	}
	@RequestMapping("/bmshaudit")
	public String bmshaudit(HttpServletRequest request){
		String id = request.getParameter("id");
		request.setAttribute("id", id);
		return prefix+"/bmsh";
	}
	@RequestMapping("/load")
	@ResponseBody
	public Result load(HttpServletRequest request){
		String id = request.getParameter("id");
		UserLeave lzn = baseService.get(UserLeave.class, id);
		
		List<BgwjFile> wjs = baseService.findMapBySql("select id  from tbl_officefile where form_ID='"+id+"'", new Object[]{}, new Type[]{StringType.INSTANCE}, BgwjFile.class);
        String wjid = "";
		for(int i =0;i<wjs.size();i++){
        	if(i==wjs.size()-1){
        		wjid += wjs.get(i).getId();
        	}else {
        		wjid += wjs.get(i).getId()+",";
        	}
        }
		Result re = new Result();
		re.setData(lzn);
		re.setCode(wjid);
		return re;
	}
	
	@RequestMapping("/audit/{id}")
	public String auid(@PathVariable("id") String id,HttpServletRequest request){
		request.setAttribute("users", JSONArray.toJSONString(baseService.get(UserLeave.class, id)));
		request.setAttribute("eduOptions", JSONArray.toJSONString(commonService.getEducationCombobox()));
        request.setAttribute("ethOptions", JSONArray.toJSONString(commonService.getEthnicCombobox()));
		List<BgwjFile> wjs = baseService.findMapBySql("select id  from tbl_officefile where form_ID='"+id+"'", new Object[]{}, new Type[]{StringType.INSTANCE}, BgwjFile.class);
        String wjid = "";
		for(int i =0;i<wjs.size();i++){
        	if(i==wjs.size()-1){
        		wjid += wjs.get(i).getId();
        	}else {
        		wjid += wjs.get(i).getId()+",";
        	}
        }
		 request.setAttribute("wjid", wjid);
		return prefix +"/auidindex";
	}
	@RequestMapping("/addSq")
	public String addSq(HttpServletRequest request){
		return prefix +"/addSq";
	}
	@RequestMapping("/startBmsh")
	@ResponseBody
	public Result startBmsh(HttpServletRequest request,UserLeave lz) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		String uid = SecurityUtil.getUserId();
		String fileId = request.getParameter("fileId");
		User u = baseService.get(User.class, uid);
		u.setAuditStatus("20");
		UserLeave lzn = new UserLeave();
		PropertyUtils.copyProperties(lzn, u);
		lzn.setUserId(u.getId());
		
		lzn.setLeaveReason(lz.getLeaveReason());
		lzn.setLeaveTime(lz.getLeaveTime());
		lzn.setLeaveStatus("5");
		baseService.save(lzn);
		baseService.update(u);
		
		if(fileId!=null&&fileId.length()>0){
			if(fileId.indexOf(",")<0){//单张
				BgwjFile wj = baseService.get(BgwjFile.class, fileId);
				wj.setFormId(lzn.getId());
				baseService.update(wj);
			}else{//多文件
				String[] ids = fileId.split(",");
				for(int i =0;i<ids.length;i++){
					BgwjFile wj = baseService.get(BgwjFile.class, ids[i]);
					wj.setFormId(lzn.getId());
					baseService.update(wj);
				}
			}
		}
		Result r = new Result();
		r.setSuccess(true);
		return r;
	}
	
	@RequestMapping("/start")
	@ResponseBody
	public Result start(HttpServletRequest request,UserLeave lz,String lx) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		String id = lz.getId();
		
		UserLeave lzn = baseService.get(UserLeave.class, id);
		if("20".equals(lx)){//驳回
			lzn.setLeaveStatus("50");
			lzn.setBmyj(lz.getBmyj());
			baseService.update(lzn);
			Result r = new Result();
			r.setSuccess(true);
			return r; 
		}else{
			lzn.setLeaveStatus("6");
			lzn.setBmyj(lz.getBmyj());
			baseService.update(lzn);
		}
        String name ="【部门："+lzn.getDeptName()+"--"+lzn.getName()+"的离职审批】";

        // businessKey
        String businessKey = lzn.getId();

        // 配置流程变量
        Map<String, Object> variables = new HashMap<>();
        variables.put(Constants.VAR_APPLYUSER_NAME, lzn.getName());
        variables.put(Constants.VAR_BUSINESS_KEY, businessKey);
        variables.put("taskName", name);

        // 启动流程
        return runtimePageService.startProcessInstanceByKey("leaveUser", name, variables, lzn.getUserId(), businessKey);
    }
}
