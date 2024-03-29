package com.radish.master.controller.volumePay;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.hibernate.annotations.common.util.StringHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONArray;
import com.cnpc.framework.activiti.pojo.Constants;
import com.cnpc.framework.activiti.service.RuntimePageService;
import com.cnpc.framework.base.entity.Org;
import com.cnpc.framework.base.entity.User;
import com.cnpc.framework.base.pojo.Result;
import com.cnpc.framework.base.service.BaseService;
import com.cnpc.framework.utils.SecurityUtil;
import com.radish.master.entity.ProAccount;
import com.radish.master.entity.ProAccountDet;
import com.radish.master.entity.Project;
import com.radish.master.entity.review.MaxNumber;
import com.radish.master.entity.volumePay.Loans;
import com.radish.master.entity.volumePay.LoansHk;
import com.radish.master.entity.volumePay.ProReceipt;
import com.radish.master.service.BudgetService;
import com.radish.master.system.Arith;

@Controller
@RequestMapping("/loans")
public class LoansController {
	String prefix = "/VolumePay/Loans/";
	@Autowired
	private BaseService baseService;
	@Resource
    private BudgetService budgetService;
	@Resource
	private RuntimePageService runtimePageService;
	@RequestMapping("/addindex")
	public String addindex(HttpServletRequest request){
		request.setAttribute("jkr", SecurityUtil.getUserId());
		return prefix+"addindex";
	}
	@RequestMapping("/bmshindex")
	public String bmshindex(HttpServletRequest request){
		User u = SecurityUtil.getUser();
		String deptid = u.getDeptId();
		request.setAttribute("deptid", deptid);
		return prefix+"bmshindex";
	}
	@RequestMapping("/bmlook")
	public String bmlook(HttpServletRequest request){
		request.setAttribute("projectOptions", JSONArray.toJSONString(budgetService.getProjectCombobox()));
		String  id = request.getParameter("id");
		request.setAttribute("id", id);
		return prefix+"bmlook";
	}
	@RequestMapping("/auidt/{id}")//审核查看页
	public String auidtMx(@PathVariable("id") String id,HttpServletRequest request){
		request.setAttribute("projectOptions", JSONArray.toJSONString(budgetService.getProjectCombobox()));
		request.setAttribute("id", id);
		return prefix+"auidLook";
		
	}
	@RequestMapping("/auidtCw/{id}")//审核查看页
	public String auidtCw(@PathVariable("id") String id,HttpServletRequest request){
		request.setAttribute("projectOptions", JSONArray.toJSONString(budgetService.getProjectCombobox()));
		request.setAttribute("id", id);
		return prefix+"auidLookCw";
		
	}
	//财务付款页
	@RequestMapping("/cwfkindex")
	public String cwfkindex(HttpServletRequest request){
		return prefix+"cwfkindex";
	}
	@RequestMapping("/cwlook")
	public String cwlook(HttpServletRequest request){
		request.setAttribute("projectOptions", JSONArray.toJSONString(budgetService.getProjectCombobox()));
		String  id = request.getParameter("id");
		String lx = request.getParameter("lx");
		String iscw = request.getParameter("iscw");
		request.setAttribute("id", id);
		request.setAttribute("lx", lx);
		request.setAttribute("iscw", iscw);
		return prefix+"cwlook";
	}
	//财务还款
	@RequestMapping("/addHk")
	public String addHk(HttpServletRequest request){
		String  jkid = request.getParameter("jkid");
		request.setAttribute("jkid", jkid);
		
		Loans jk = baseService.get(Loans.class,jkid);
		List<LoansHk> jks = baseService.find(" from LoansHk where loansid='"+jkid+"'");
		BigDecimal zje = BigDecimal.ZERO;
		for(LoansHk j :jks){
			zje = zje.add(new BigDecimal(j.getMoney()));
		}
		BigDecimal jkmoney = new BigDecimal(jk.getMoney());
		BigDecimal hkmoney = jkmoney.subtract(zje);
		
		request.setAttribute("yhmoney", zje+"");
		request.setAttribute("jkmoney", jkmoney+"");
		request.setAttribute("hkmoney", hkmoney+"");
		
		return prefix+"addHk";
	}
	public  String maxNum(){ 
		List<String> result = baseService.find("select max(mat.id) from com.radish.master.entity.review.MaxNumber mat");
		if(result.size()>0&&!"".equals(result.get(0))&&StringHelper.isNotEmpty(result.get(0))){
			String num = result.get(0);
			
			MaxNumber m = new MaxNumber();
			Integer newId = Integer.valueOf(num)+1;
			m.setId(newId+"");
			baseService.save(m);
			return num;
		}else{
			MaxNumber m = new MaxNumber();
			m.setId("1001");
			baseService.save(m);
			return "1001";
		}
	}
	@RequestMapping("/add")
	public String add(HttpServletRequest request){
		String  id = request.getParameter("id");
		request.setAttribute("projectOptions", JSONArray.toJSONString(budgetService.getProjectCombobox()));
		if(id==null){
			String str =maxNum();
			
			Calendar date = Calendar.getInstance();
			String year = String.valueOf(date.get(Calendar.YEAR));
			String strs = year+str;
			
			User u = SecurityUtil.getUser();
			request.setAttribute("bh",strs);
			request.setAttribute("ryid", u.getId());
			request.setAttribute("xm", u.getName());
			if(u.getDeptId()!=null){
				Org bm = baseService.get(Org.class, u.getDeptId());
				if(bm==null){
					request.setAttribute("msg","未找到所属部门，请联系办公管理人员！");	
				}
			}else{
				request.setAttribute("msg","请联系办公管理人员，完善个人部门信息！");
			}
		}else{
			request.setAttribute("id", id);
		}
		
		return prefix+"add";
	}
	@RequestMapping("/look")
	public String look(HttpServletRequest request){
		return prefix+"look";
	}
	@RequestMapping("/zffs")
	public String zffs(HttpServletRequest request){
		String id = request.getParameter("id");
		request.setAttribute("id", id);
		request.setAttribute("zdr", SecurityUtil.getUserId());
		return prefix +"zffs";
	}
	
	@RequestMapping("/load")
	@ResponseBody
	public Result load(HttpServletRequest request){
		String id = request.getParameter("id");
		Result r = new Result();
		Loans jk = baseService.get(Loans.class, id);
		r.setData(jk);
		return r;
	}
	@RequestMapping("/save")
	@ResponseBody
	public Result save(HttpServletRequest request,Loans jk){
		String id = request.getParameter("id");
		Result r = new Result();
		if(id==null){//新增
			String userid = jk.getLoanerid();
			User u = baseService.get(User.class, userid);
			Org bm = baseService.get(Org.class, u.getDeptId());
			jk.setDept(bm.getName());
			jk.setPid(u.getDeptId());
			jk.setStatus("10");
			jk.setIshk("0");
			jk.setIsjz("0");
			if("10".equals(jk.getType())){
				jk.setProid(null);
			}
			baseService.save(jk);
		}else{//修改
			Loans j = baseService.get(Loans.class, id);
			j.setMoney(jk.getMoney());
			j.setEnddate(jk.getEnddate());
			j.setContent(jk.getContent());
			j.setProid(jk.getProid());
			j.setType(jk.getType());
			if("10".equals(jk.getType())){
				j.setProid(null);
			}else if("20".equals(jk.getType())){
				j.setProid(jk.getProid());
			}
			baseService.update(j);
		}
		return r;
	}
	
	@RequestMapping("/delete")
	@ResponseBody
	public Result delete(HttpServletRequest request){
		String id = request.getParameter("id");
		Loans j = baseService.get(Loans.class, id);
		Result r = new Result();
		baseService.delete(j);
		return r;
	}
	//提交部门审核
	@RequestMapping("/bmsh")
	@ResponseBody
	public Result bmsh(HttpServletRequest request){
		String id = request.getParameter("id");
		Loans j = baseService.get(Loans.class, id);
		j.setStatus("20");
		baseService.update(j);
		Result r = new Result();
		return r;
		
	}
	//部门审核
	@RequestMapping("/bmsubmit")
	@ResponseBody
	public Result bmsubmit(HttpServletRequest request,Loans jk){
		String id = request.getParameter("id");
		String lx = request.getParameter("lx");
		Loans j = baseService.get(Loans.class, id);
		
		User user = SecurityUtil.getUser();
		Org bm = baseService.get(Org.class, j.getPid());
		if("10".equals(lx)){//通过
			j.setStatus("90");
			String name ="部门："+bm.getName()+",姓名："+j.getLoanname()+",借款【审核】";
			if("30".equals(j.getType())){
				 name ="(外单位/非单位人员)姓名："+j.getLoanname()+",借款【审核】";
			}
			

	        // businessKey
	        String businessKey = j.getId();

	        // 配置流程变量
	        Map<String, Object> variables = new HashMap<>();
	        variables.put(Constants.VAR_APPLYUSER_NAME, user.getName());
	        variables.put(Constants.VAR_BUSINESS_KEY, businessKey);
	        variables.put("taskName", name);

	        // 启动流程
	        runtimePageService.startProcessInstanceByKey("Loans", name, variables, user.getId(), businessKey);
			
			
		}else if("20".equals(lx)){//驳回
			j.setStatus("30");
		}
		j.setBmyj(jk.getBmyj());
		j.setBmshr(user.getName());
		baseService.update(j);
		Result r = new Result();
		return r;
	}
	//确认付款
		@RequestMapping("/dofk")
		@ResponseBody
		public Result dofk(HttpServletRequest request){
			String id = request.getParameter("id");
			Loans j = baseService.get(Loans.class, id);
			j.setStatus("70");
			baseService.update(j);
			Result r = new Result();
			return r;
			
		}
	//记账
		@RequestMapping("/dojz")
		@ResponseBody
		public Result dojz(HttpServletRequest request){
			Arith arith = new Arith();
			String id = request.getParameter("id");
			Loans j = baseService.get(Loans.class, id);
			
			String xmid = "";
			String content = j.getDept()+"-"+j.getLoanname()+"借款";
			if("30".equals(j.getType())){
				content ="(外单位/非单位人员)姓名："+j.getLoanname()+",借款";
			}
			if("10".equals(j.getType())||"30".equals(j.getType())){//公司账本
				xmid = "1";
			}else if("20".equals(j.getType())){//项目账本
				xmid = j.getProid();
			}
			
			List<ProAccount> xmz = baseService.find(" from ProAccount where projectId='"+xmid+"'");
			
			User u = SecurityUtil.getUser();
			//账目明细
			ProAccountDet mx = new ProAccountDet();
			mx.setCreateDate(new Date());
			mx.setAbstracts(content+"-"+j.getContent());
			mx.setZmtype("2");
			mx.setOutMoney(j.getMoney());
			mx.setAccounter(u.getName());
			mx.setAccounterId(u.getId());
			//mx.setAuditName(u.getName());
			//mx.setAuditId(u.getId());
			mx.setStatus("10");
			if(xmz.size()<=0){//无账本，先生成账本
				ProAccount zb = new ProAccount();
				zb.setProjectId(xmid);
				if("1".equals(xmid)){
					zb.setAccountName("公司账本");
				}else{
					Project p = baseService.get(Project.class, j.getProid());
					zb.setAccountName(p.getProjectName());
				}
				
				baseService.save(zb);
				zb.setAllMoney(arith.sub(0.0,Double.valueOf(j.getMoney()))+"");
				mx.setProjectAccountId(zb.getId());
				baseService.save(mx);
				baseService.update(zb);
			}else{
				ProAccount zb = xmz.get(0);
				zb.setAllMoney(arith.sub(Double.valueOf(zb.getAllMoney()),Double.valueOf(j.getMoney()) )+"");
				mx.setProjectAccountId(zb.getId());
				baseService.save(mx);
				baseService.update(zb);
			}
			j.setIsjz("10");
			baseService.update(j);
			Result r = new Result();
			return r;
		}
		//添加还款
		@RequestMapping("/saveHk")
		@ResponseBody
		public Result  saveHk(LoansHk hk){
			User user = SecurityUtil.getUser();
			hk.setIsjz("0");
			hk.setSqrid(user.getId());
			hk.setSqr(user.getName());
			hk.setSqtime(new Date());
			baseService.save(hk);
			Result r = new Result();
			return r;
		}
		//删除还款
		@RequestMapping("/deleteHk")
		@ResponseBody
		public Result  deleteHk(String id){
			LoansHk hk = baseService.get(LoansHk.class, id);
			baseService.delete(hk);
			Result r = new Result();
			return r;
		}
		//确定并存入收据记账
		@RequestMapping("/hkjz")
		@ResponseBody
		public Result  hkjz(String id){
			Result r = new Result();
			LoansHk hk = baseService.get(LoansHk.class, id);
			if(hk.getLoansid()==null){
				return new Result(false, "未获取到借款id");
			}
			Loans jk = baseService.get(Loans.class, hk.getLoansid());
			
			//添加收据
			ProReceipt sj = new ProReceipt();
			sj.setBz(jk.getLoanname()+"借款还款");
			sj.setContent(jk.getLoanname()+"借款还款");
			if("30".equals(jk.getType())){
				sj.setBz(jk.getLoanname()+"借款还款(外单位/非公司人员)");
				sj.setContent(jk.getLoanname()+"借款还款(外单位/非公司人员)");
			}
			sj.setCreateDate(hk.getHktime());
			sj.setJkr(jk.getLoanname());
			sj.setMoney(jk.getMoney());
			sj.setProId(jk.getProid());
			sj.setType("20");//转账
			sj.setSjtype("20");//普通收据
			sj.setMoney(hk.getMoney());
			String str =maxNum();
			Calendar date = Calendar.getInstance();
			String year = String.valueOf(date.get(Calendar.YEAR));
			String strs = "SJ"+year+str;
			sj.setNumber(strs);
			baseService.save(sj);
			//修改还款状态
			hk.setIsjz("1");
			hk.setReceiptid(sj.getId());
			baseService.update(hk);
			//修改借款状态
			List<LoansHk> jks = baseService.find(" from LoansHk where loansid='"+hk.getLoansid()+"' and isjz='1'");
			BigDecimal zje = BigDecimal.ZERO;
			for(LoansHk j :jks){
				zje = zje.add(new BigDecimal(j.getMoney()));
			}
			BigDecimal jkmoney = new BigDecimal(jk.getMoney());
			if(jkmoney.compareTo(zje)<=0){//还清
				jk.setStatus("110");
			}else{//未还清
				jk.setStatus("80");	
			}
			jk.setHkmoney(zje+"");
			baseService.update(jk);
			
			if(sj.getProId()==null){
				r.setData("公司收据，编号："+sj.getNumber());
			}else{
				r.setData("项目收据，编号："+sj.getNumber());
			}
			return r;
		}
}
