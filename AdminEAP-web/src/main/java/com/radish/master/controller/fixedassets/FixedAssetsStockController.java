/**
 * 版权所有 (c) 2019，中金支付有限公司  
 */
package com.radish.master.controller.fixedassets;

import java.util.Date;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONArray;
import com.cnpc.framework.base.pojo.Result;
import com.cnpc.framework.utils.SecurityUtil;
import com.radish.master.entity.fixedassets.FixedAssetsPur;
import com.radish.master.entity.fixedassets.FixedAssetsStk;
import com.radish.master.entity.fixedassets.FixedAssetsStkTx;
import com.radish.master.service.CommonService;

/**
* 类说明
* 
* <pre>
* Modify Information:
* Author        Date          Description
* ============ =========== ============================
* dongyan      2019年2月13日    Create this file
* </pre>
* 
*/
@Controller
@RequestMapping("/fixedassets/stock")
public class FixedAssetsStockController {
    
    @Resource
    private CommonService commonService;

    @RequestMapping(value="/list",method = RequestMethod.GET)
    public String assetsList(HttpServletRequest request){
        request.setAttribute("deptOptions", JSONArray.toJSONString(commonService.getDepartmentCombobox()));
        return "fixedassets/stock/list";
    }
    
    @RequestMapping(value="/detail/{id}",method = RequestMethod.GET)
    public String detail(@PathVariable("id") String id, HttpServletRequest request){
        request.setAttribute("fixedAssetsID", id);
        
        return "fixedassets/stock/detail";
    }
    @RequestMapping(value="/cshZc",method = RequestMethod.GET)
    public String cshZc( HttpServletRequest request){
        
        return "fixedassets/stock/new_stk";
    }
    
    @RequestMapping(method = RequestMethod.POST, value="/save")
    @ResponseBody
    public Result save(FixedAssetsStk stk, HttpServletRequest request){
    	stk.setUpdateDateTime(new Date());
    	stk.setCreateDateTime(new Date());
    	stk.setBelongedStock("总库");
    	stk.setQuantityAvl(stk.getQuantity());
        try {
            commonService.save(stk);
            
            FixedAssetsStkTx stkTx = new FixedAssetsStkTx();
            stkTx.setFixedAssetsID(stk.getId());
            stkTx.setOperation("10");//入库
            stkTx.setAmount(stk.getQuantity());
            stkTx.setBalance(stk.getQuantityAvl());
            stkTx.setPrice(stk.getPrice());
            stkTx.setRemark("资产初始化入库");
            stkTx.setOperator(SecurityUtil.getUser().getLoginName());
            stkTx.setOperateTime(new Date());
            stkTx.setSourceTxID("0");
            commonService.save(stkTx);
        } catch (Exception e) {
            return new Result(false,"保存失败，请联系系统管理员！");
        }
        return new Result(true, stk);
    }
}
