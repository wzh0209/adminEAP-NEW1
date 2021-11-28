package com.radish.master.listener;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.delegate.TaskListener;

import com.cnpc.framework.activiti.pojo.Constants;
import com.cnpc.framework.base.service.BaseService;
import com.cnpc.framework.utils.SecurityUtil;
import com.radish.master.entity.common.ActivitiSuggestion;
import com.radish.master.entity.project.Notice;
import com.radish.master.entity.safty.CheckRecordAQ;
import com.radish.master.system.SpringUtil;

public class AqFilesTaskCompleteListener3 implements TaskListener{


    private static final long serialVersionUID = 4294247719992051739L;
    private static final String TRUE = "true";

    private static final String FALSE = "false";
    @Override
    public void notify(DelegateTask delegateTask) {
    	
        String eventName = delegateTask.getEventName();
        if(EVENTNAME_COMPLETE.equals(eventName)){
             String businessKey = delegateTask.getVariable(Constants.VAR_BUSINESS_KEY).toString();
             String taskDefinitionKey = delegateTask.getTaskDefinitionKey();
             String suggestion = delegateTask.getVariable("suggestion").toString();
             BaseService baseService = (BaseService)SpringUtil.getObject("baseActServer");
             
             String suggestionHql = "from ActivitiSuggestion where businessKey=:businessKey AND taskNode=:taskNode";
             Map<String, Object> suggestionParams = new HashMap<>();
             suggestionParams.put("businessKey", businessKey);
             suggestionParams.put("taskNode", taskDefinitionKey);
             ActivitiSuggestion as = baseService.get(suggestionHql, suggestionParams);
             
             if(as == null){
                 as = new ActivitiSuggestion();
                 as.setCreateDateTime(new Date());
                 as.setUpdateDateTime(new Date());
                 as.setBusinessKey(businessKey);
                 as.setTaskNode(taskDefinitionKey);
                 as.setApprove("true");
             }
             as.setSuggestion(suggestion);
             as.setOperator(SecurityUtil.getUser().getName());
             as.setUpdateDateTime(new Date());
             baseService.save(as);
             
             Map<String, Object> params = new HashMap<String, Object>();
             params.put("id", businessKey);
             CheckRecordAQ zf = baseService.get("from CheckRecordAQ where id=:id", params);
             //10=新建,20=总经理审核,30=审核完成,40=驳回,60=项目经理审核,70=总工审核
             if (FALSE.equalsIgnoreCase(delegateTask.getVariable("approved").toString())) {
            	 zf.setStatus("40");
            	 zf.setReserve2(suggestion);
             }else if (TRUE.equalsIgnoreCase(delegateTask.getVariable("approved").toString())) {
            	 if ("auidt3".equals(taskDefinitionKey)) {///终审
            		 zf.setStatus("30");
            	 }else if("zg".equals(taskDefinitionKey)){//总工
            		 zf.setStatus("20");
            	 }
             }
             baseService.update(zf);
             
             
             
        }

    }
}
