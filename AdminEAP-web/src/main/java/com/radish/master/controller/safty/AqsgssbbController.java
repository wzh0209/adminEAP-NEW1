package com.radish.master.controller.safty;

import com.alibaba.fastjson.JSONArray;
import com.cnpc.framework.activiti.service.RuntimePageService;
import com.cnpc.framework.base.controller.UploaderController;
import com.cnpc.framework.base.entity.User;
import com.cnpc.framework.base.pojo.FileResult;
import com.cnpc.framework.base.pojo.Result;
import com.cnpc.framework.base.service.BaseService;
import com.cnpc.framework.constant.RedisConstant;
import com.cnpc.framework.utils.DateUtil;
import com.cnpc.framework.utils.FileUtil;
import com.cnpc.framework.utils.PropertiesUtil;
import com.cnpc.framework.utils.SecurityUtil;
import com.cnpc.framework.utils.StrUtil;
import com.radish.master.entity.Project;
import com.radish.master.entity.safty.Aqsgssbb;
import com.radish.master.entity.safty.Aqsgssbbry;
import com.radish.master.entity.safty.CheckFileAQ;
import com.radish.master.entity.safty.CheckItemAQ;
import com.radish.master.pojo.RowEdit;
import com.radish.master.service.BudgetService;
import com.radish.master.service.SaftyItemService;
import com.radish.master.service.project.TeamSalaryService;
import com.radish.master.system.FileHelper;

import org.apache.commons.beanutils.PropertyUtils;
import org.hibernate.annotations.common.util.StringHelper;
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

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 安全事故伤亡报表
 */
@Controller
@RequestMapping("/sgswbb")
public class AqsgssbbController {

    @Resource
    private SaftyItemService saftyItemService;
    @Resource
    private BudgetService budgetService;
    @Autowired
	private BaseService baseService;
    @Autowired
    private TeamSalaryService teamSalaryService;
    @Resource
	private RuntimePageService runtimePageService;
    
    private static Logger logger= LoggerFactory.getLogger(UploaderController.class);
	private static final String uploaderPath=PropertiesUtil.getValue("safetyManageFilePath")+"\\sgssbb";
	 public static Map fileIconMap=new HashMap();
	 static {
	        fileIconMap.put("doc" ,"<i class='fa fa-file-word-o text-primary'></i>");
	        fileIconMap.put("docx","<i class='fa fa-file-word-o text-primary'></i>");
	        fileIconMap.put("xls" ,"<i class='fa fa-file-excel-o text-success'></i>");
	        fileIconMap.put("xlsx","<i class='fa fa-file-excel-o text-success'></i>");
	        fileIconMap.put("ppt" ,"<i class='fa fa-file-powerpoint-o text-danger'></i>");
	        fileIconMap.put("pptx","<i class='fa fa-file-powerpoint-o text-danger'></i>");
	        fileIconMap.put("jpg" ,"<i class='fa fa-file-photo-o text-warning'></i>");
	        fileIconMap.put("pdf" ,"<i class='fa fa-file-pdf-o text-danger'></i>");
	        fileIconMap.put("zip" ,"<i class='fa fa-file-archive-o text-muted'></i>");
	        fileIconMap.put("rar" ,"<i class='fa fa-file-archive-o text-muted'></i>");
	        fileIconMap.put("default" ,"<i class='fa fa-file-o'></i>");
	    }
    
    
    @RequestMapping(method = RequestMethod.GET, value = "/list")
    public String listIndex(HttpServletRequest request) {
    	request.setAttribute("projectOptions", JSONArray.toJSONString(budgetService.getProjectCombobox()));
		return "safetyManage/sgswbb/addIndex";
    }
    
    @RequestMapping(method = RequestMethod.GET, value = "/addWj")
    public String addWj(HttpServletRequest request) {
    	List<Project> p = baseService.findMapBySql("select p.project_name projectName ,p.id id  from tbl_project p ", new Object[]{}, new Type[]{StringType.INSTANCE}, Project.class);
		request.setAttribute("projectOptions", JSONArray.toJSONString(p));
		String id = request.getParameter("id");
		request.setAttribute("id",id);
        return "safetyManage/sgswbb/edit";
    }
    
    @RequestMapping("/look")
	public String auditHf(HttpServletRequest request){
    	String id = request.getParameter("id");
    	List<Project> p = baseService.findMapBySql("select p.project_name projectName ,p.id id  from tbl_project p ", new Object[]{}, new Type[]{StringType.INSTANCE}, Project.class);
		request.setAttribute("projectOptions", JSONArray.toJSONString(p));
		request.setAttribute("id",id);
        return "safetyManage/sgswbb/look";
    }
    
    @RequestMapping("/load")
	@ResponseBody
	public Result load(String id){
		List<CheckFileAQ> wjs = baseService.findMapBySql("select id  from tbl_checkfileAQ where form_ID='"+id+"'", new Object[]{}, new Type[]{StringType.INSTANCE}, CheckFileAQ.class);
        String wjid = "";
		for(int i =0;i<wjs.size();i++){
        	if(i==wjs.size()-1){
        		wjid += wjs.get(i).getId();
        	}else {
        		wjid += wjs.get(i).getId()+",";
        	}
        }
		Aqsgssbb  jd= baseService.get(Aqsgssbb.class, id);
		 Result r = new Result();
		 r.setData(jd);
		 r.setCode(wjid);
		return r;
	}
    
    @RequestMapping(method = RequestMethod.GET, value = "/scWj")
    public String scWj(HttpServletRequest request) {
    	
		
		String id = request.getParameter("id");
		request.setAttribute("id",id);
		String islook = request.getParameter("islook");
		request.setAttribute("islook",islook);
		return "safetyManage/sgswbb/query_file";
    }
    
    @RequestMapping("saveWj")
    @ResponseBody
    public Result saveWj(HttpServletRequest request,Aqsgssbb cr){
    	Result r = new Result();
    	String fid = request.getParameter("fid");
    	User u = SecurityUtil.getUser();
    	String fileids = request.getParameter("fileId");
    	
    	try{
    		if(cr.getId()==null){
    			cr.setTbr(u.getName());
    			cr.setStatus("10");
        		baseService.save(cr);
        		r.setCode(cr.getId());
        	}else{
        		Aqsgssbb c = baseService.get(Aqsgssbb.class, cr.getId());
        		cr.setStatus(c.getStatus());
        		cr.setTbr(u.getName());
        		PropertyUtils.copyProperties(c, cr);
        		
        		baseService.update(c);
        		r.setCode(cr.getId());
        	}
        	r.setSuccess(true);
        	if(fileids!=null&&fileids.length()>0){
        		if(fileids.indexOf(",")<0){//单个
        			CheckFileAQ wj = baseService.get(CheckFileAQ.class, fileids);
        			wj.setFormId(r.getCode());
        			baseService.update(wj);
        		}else{//多个
        			String[] ids = fileids.split(",");
        			for(int i = 0;i<ids.length;i++){
        				CheckFileAQ wj = baseService.get(CheckFileAQ.class, ids[i]);
        				wj.setFormId(r.getCode());
        				baseService.update(wj);
        			}
        		}
        	}
    	}catch(Exception e){
    		return new Result(false, e.getMessage());
    	}
    	return r;
    }
    @RequestMapping(value = "/saveRy", method = RequestMethod.POST)
    @ResponseBody
    public Result saveRy(String pid) {
    	Aqsgssbbry ry = new Aqsgssbbry();
    	ry.setPid(pid);
    	ry.setCol1("请输入人员姓名");
    	baseService.save(ry);
        return new Result(true);
    }
    @RequestMapping(value = "/rowedit", method = RequestMethod.POST)
    @ResponseBody
    public String singleEstimate(String action, HttpServletRequest request) throws Exception {
        String id = "";
        String field = "";
        String value = "";
        
        RowEdit re = new RowEdit();
        List<Object> list = new ArrayList<Object>();
        
        Enumeration<String> key = request.getParameterNames();
        while (key.hasMoreElements()) {
            String paramName = (String) key.nextElement();
            if ("action".equals(paramName)) {
                continue;
            }
            String[] paramValues = request.getParameterValues(paramName);
            if (paramValues.length == 1) {
                String paramValue = paramValues[0];
                if (paramValue.length() != 0) {
                    int idIndexStart = paramName.indexOf("[");
                    int idIndexEnd = paramName.indexOf("]");
                    int fieldIndexStart = paramName.lastIndexOf("[");
                    int fieldIndexEnd = paramName.lastIndexOf("]");
                    id = paramName.substring(idIndexStart + 1, idIndexEnd);
                    field = paramName.substring(fieldIndexStart + 1, fieldIndexEnd);
                    value = paramValue;
                }
            }
        }
        Aqsgssbbry detail = baseService.get(Aqsgssbbry.class, id);
        Method set = detail.getClass().getMethod("set" + teamSalaryService.captureName(field), String.class);
        set.invoke(detail, value);
        baseService.update(detail);
        list.add(detail);
        re.setData(list);
        return JSONArray.toJSONString(re);
    }
    
    @RequestMapping(value = "/deleteRy", method = RequestMethod.POST)
    @ResponseBody
    public Result deleteRy(String id) {
    	Aqsgssbbry cr = baseService.get(Aqsgssbbry.class, id);
    	baseService.delete(cr);
        return new Result(true);
    }
    
    
    @RequestMapping(value = "/save", method = RequestMethod.POST)
    @ResponseBody
    public Result save(CheckItemAQ org) {

        org.setUpdateDateTime(new Date());
        saftyItemService.saveOrUpdate(org);
        if (!StrUtil.isEmpty(org.getParentId())) {
        	CheckItemAQ parent = saftyItemService.get(CheckItemAQ.class, org.getParentId());
        	saftyItemService.deleteCacheByKey(RedisConstant.CHECK_PRE + parent.getCode());
        }
        saftyItemService.deleteCacheByKey(RedisConstant.SAFTY_PRE + "tree");
        return new Result(true);
    }
    
    @RequestMapping("/deleteWj")
    @ResponseBody
    public Result deleteWj(String id,HttpServletRequest request) {
    	Result r = new Result();
    	
    	Aqsgssbb cr = baseService.get(Aqsgssbb.class, id);
    	
    	List<CheckFileAQ> wjs = baseService.findMapBySql("select id ,savedName from tbl_checkfileAQ where form_ID='"+id+"'", new Object[]{}, new Type[]{StringType.INSTANCE}, CheckFileAQ.class);
    	for(CheckFileAQ fj : wjs){
            String dirPath=request.getRealPath("/");
            FileUtil.delFile(uploaderPath+File.separator+fj.getSavedName());
            baseService.delete(fj);
    	}
    	baseService.delete(cr);
    	r.setSuccess(true);
    	
    	return r;
    }
    
    
    @RequestMapping("/fileIndex")
	public String fileIndex(HttpServletRequest request){
		List<Project> p = baseService.findMapBySql("select p.project_name projectName ,p.id id  from tbl_project p ", new Object[]{}, new Type[]{StringType.INSTANCE}, Project.class);
		request.setAttribute("projectOptions", JSONArray.toJSONString(p));
		
		return "safetyManage/aqfiles/fileIndex";
	}
	
	@RequestMapping(value="/projectdetailfile", method = RequestMethod.GET)
    public String projectdetailfile(String id, HttpServletRequest request) throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException{
		List<CheckFileAQ> wjs = baseService.findMapBySql("select id  from tbl_checkfileAQ where form_ID='"+id+"'", new Object[]{}, new Type[]{StringType.INSTANCE}, CheckFileAQ.class);
        String wjid = "";
		for(int i =0;i<wjs.size();i++){
        	if(i==wjs.size()-1){
        		wjid += wjs.get(i).getId();
        	}else {
        		wjid += wjs.get(i).getId()+",";
        	}
        }
		String islook = request.getParameter("islook");
		request.setAttribute("islook",islook);
        request.setAttribute("fields", wjid);
        request.setAttribute("id", id);
        return "safetyManage/sgswbb/query_file";
    }
    
	/**
     * 多文件上传，用于uploadAsync=false(同步多文件上传使用)
     * @param files
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = "/uploadMultipleFile", method = RequestMethod.POST)
    @ResponseBody
    public FileResult uploadMultipleFile(@RequestParam(value = "file", required = false) MultipartFile[] files,
                                         HttpServletRequest request, HttpServletResponse response) throws IOException {
    	String psid = request.getParameter("psid");
        FileResult msg = new FileResult();

        ArrayList<Integer> arr = new ArrayList<>();
        //缓存当前的文件
        List<CheckFileAQ> fileList=new ArrayList<>();
        String dirPath = request.getRealPath("/");
        for (int i = 0; i < files.length; i++) {
            MultipartFile file = files[i];

            if (!file.isEmpty()) {
                InputStream in = null;
                OutputStream out = null;
                try {
                    File dir = new File(uploaderPath);
                    if (!dir.exists())
                        dir.mkdirs();
                    //这样也可以上传同名文件了
                    String filePrefixFormat="yyyyMMddHHmmssS";
                    String savedName=DateUtil.format(new Date(),filePrefixFormat)+"_"+file.getOriginalFilename();
                    String filePath=dir.getAbsolutePath() + File.separator + savedName;
                    File serverFile = new File(filePath);
                    //将文件写入到服务器
                    //FileUtil.copyInputStreamToFile(file.getInputStream(),serverFile);
                    file.transferTo(serverFile);
                    CheckFileAQ sysFile=new CheckFileAQ();
                    sysFile.setFileName(file.getOriginalFilename());
                    sysFile.setSavedName(savedName);
                    sysFile.setCreateDateTime(new Date());
                    sysFile.setUpdateDateTime(new Date());
                    sysFile.setCreateUserId(SecurityUtil.getUserId());
                    sysFile.setDeleted(0);
                    sysFile.setFileSize(file.getSize());
                    sysFile.setFilePath(uploaderPath+File.separator+savedName);
                    if(psid!=null)sysFile.setFormId(psid);
                    baseService.save(sysFile);
                    fileList.add(sysFile);
                    /*preview.add("<div class=\"file-preview-other\">\n" +
                            "<span class=\"file-other-icon\"><i class=\"fa fa-file-o text-default\"></i></span>\n" +
                            "</div>");*/

                    logger.info("Server File Location=" + serverFile.getAbsolutePath());
                } catch (Exception e) {
                    logger.error(   file.getOriginalFilename()+"上传发生异常，异常原因："+e.getMessage());
                    arr.add(i);
                } finally {
                    if (out != null) {
                        out.close();
                    }
                    if (in != null) {
                        in.close();
                    }
                }
            } else {
                arr.add(i);
            }
        }

        if(arr.size() > 0) {
            msg.setError("文件上传失败！");
            msg.setErrorkeys(arr);
        }
        FileResult preview=getPreivewSettings(fileList,request);
        msg.setInitialPreview(preview.getInitialPreview());
        msg.setInitialPreviewConfig(preview.getInitialPreviewConfig());
        msg.setFileIds(preview.getFileIds());
        return msg;
    }
    /**
     * 回填已有文件的缩略图
     * @param fileList 文件列表
     * @param request
     * @return initialPreiview initialPreviewConfig fileIds
     */
    public FileResult getPreivewSettings(List<CheckFileAQ> fileList,HttpServletRequest request){
        FileResult fileResult=new FileResult();
        List<String> previews=new ArrayList<>();
        List<FileResult.PreviewConfig> previewConfigs=new ArrayList<>();
        //缓存当前的文件
        String[] fileArr=new String[fileList.size()];
        int index=0;
        for (CheckFileAQ sysFile : fileList) {
            //上传后预览 TODO 该预览样式暂时不支持theme:explorer的样式，后续可以再次扩展
            //如果其他文件可预览txt、xml、html、pdf等 可在此配置
        	 //如果其他文件可预览txt、xml、html、pdf等 可在此配置
        	String dirPath = request.getContextPath() + "/sgswbb/showImage?imageID=" + sysFile.getId();
            if(FileUtil.isImage(uploaderPath+File.separator+sysFile.getSavedName())) {
                previews.add("<img src='" + dirPath+ "' class='file-preview-image kv-preview-data' " +
                        "style='width:auto;height:160px' alt='" + sysFile.getFileName() + " title='" + sysFile.getFileName() + "''>");
            }else{
                previews.add("<div class='kv-preview-data file-preview-other-frame'><div class='file-preview-other'>" +
                        "<span class='file-other-icon'>"+getFileIcon(sysFile.getFileName())+"</span></div></div>");
            }
            //上传后预览配置
            FileResult.PreviewConfig previewConfig=new FileResult.PreviewConfig();
            previewConfig.setWidth("120px");
            previewConfig.setCaption(sysFile.getFileName());
            previewConfig.setKey(sysFile.getId());
            // previewConfig.setUrl(request.getContextPath()+"/file/delete");
            previewConfig.setExtra(new FileResult.PreviewConfig.Extra(sysFile.getId()));
            previewConfig.setSize(sysFile.getFileSize());
            previewConfigs.add(previewConfig);
            fileArr[index++]=sysFile.getId();
        }
        fileResult.setInitialPreview(previews);
        fileResult.setInitialPreviewConfig(previewConfigs);
        fileResult.setFileIds(StrUtil.join(fileArr));
        return fileResult;
    }
    /**
     * 根据文件名获取icon
     * @param fileName 文件
     * @return
     */
    public String getFileIcon(String fileName){
        String ext= StrUtil.getExtName(fileName);
        return fileIconMap.get(ext)==null?fileIconMap.get("default").toString():fileIconMap.get(ext).toString();
    }
    
    @RequestMapping(value="/deletefile",method = RequestMethod.POST)
    @ResponseBody
    public Result delete(String id, HttpServletRequest request){
    	CheckFileAQ sysFile=baseService.get(CheckFileAQ.class,id);
        String dirPath=request.getRealPath("/");
        FileUtil.delFile(uploaderPath+File.separator+sysFile.getSavedName());
        baseService.delete(sysFile);
        return new Result();
    }
    @RequestMapping(value="/download/{id}",method = RequestMethod.GET)
    public void downloadFile(@PathVariable("id") String id,HttpServletRequest request,HttpServletResponse response) throws IOException {
    	CheckFileAQ sysfile = baseService.get(CheckFileAQ.class, id);

        InputStream is = null;
        OutputStream os = null;
        File file = null;
        try {
            // PrintWriter out = response.getWriter();
            if (sysfile != null)
                file = new File(sysfile.getFilePath());
            if (file != null && file.exists() && file.isFile()) {
                long filelength = file.length();
                is = new FileInputStream(file);
                // 设置输出的格式
                os = response.getOutputStream();
                response.setContentType("application/x-msdownload");
                response.setContentLength((int) filelength);
                response.addHeader("Content-Disposition", "attachment; filename=\"" + new String(sysfile.getFileName().getBytes("GBK"),// 只有GBK才可以
                        "iso8859-1") + "\"");

                // 循环取出流中的数据
                byte[] b = new byte[4096];
                int len;
                while ((len = is.read(b)) > 0) {
                    os.write(b, 0, len);
                }
            } else {
                response.getWriter().println("<script>");
                response.getWriter().println(" modals.info('文件不存在!');");
                response.getWriter().println("</script>");
            }

        } catch (IOException e) {
            // e.printStackTrace();
        } finally {
            if (is != null) {
                is.close();
            }
            if (os != null) {
                os.close();
            }
        }
    }
    /**
     * 根据附件IDS 获取文件
     * @param fileIds
     * @param request
     * @return
     */
    @RequestMapping(value="/getFiles",method = RequestMethod.POST)
    @ResponseBody
    public FileResult getFiles(String fileIds, String type, HttpServletRequest request){
        List<CheckFileAQ> fileList=new ArrayList<>();
        if(!StrUtil.isEmpty(fileIds)) {
            String[] fileIdArr = fileIds.split(",");
            DetachedCriteria criteria = DetachedCriteria.forClass(CheckFileAQ.class);
            criteria.add(Restrictions.in("id", fileIdArr));
            criteria.addOrder(Order.asc("createDateTime"));
            fileList = baseService.findByCriteria(criteria);
        }
        if(StrUtil.isEmpty(type)){
            return getPreivewSettings(fileList,request);
        }else{
            return getPreivewSettingsPreview(fileList,request);
        }
        
    }
    public FileResult getPreivewSettingsPreview(List<CheckFileAQ> fileList,HttpServletRequest request){
        FileResult fileResult=new FileResult();
        List<String> previews=new ArrayList<>();
        List<FileResult.PreviewConfig> previewConfigs=new ArrayList<>();
        //缓存当前的文件
        String[] fileArr=new String[fileList.size()];
        int index=0;
        for (CheckFileAQ sysFile : fileList) {
            //上传后预览 TODO 该预览样式暂时不支持theme:explorer的样式，后续可以再次扩展
        	 //如果其他文件可预览txt、xml、html、pdf等 可在此配置
        	String dirPath = request.getContextPath() + "/sgswbb/showImage?imageID=" + sysFile.getId();
        	if(FileUtil.isImage(uploaderPath+File.separator+sysFile.getSavedName())) {
                previews.add("<img src='" + dirPath+ "' class='file-preview-image kv-preview-data' " +
                        "style='width:auto;height:160px' alt='" + sysFile.getFileName() + " title='" + sysFile.getFileName() + "''>");
            }else{
                previews.add("<div class='kv-preview-data file-preview-other-frame'><div class='file-preview-other'>" +
                        "<span class='file-other-icon'>"+getFileIcon(sysFile.getFileName())+"</span></div></div>");
            }
            //上传后预览配置
            FileResult.PreviewConfig previewConfig=new FileResult.PreviewConfig();
            previewConfig.setWidth("60px");
            previewConfig.setCaption(sysFile.getFileName());
            previewConfig.setKey(sysFile.getId());
            // previewConfig.setUrl(request.getContextPath()+"/file/delete");
            previewConfig.setExtra(new FileResult.PreviewConfig.Extra(sysFile.getId()));
            previewConfig.setSize(sysFile.getFileSize());
            previewConfigs.add(previewConfig);
            fileArr[index++]=sysFile.getId();
        }
        fileResult.setInitialPreview(previews);
        fileResult.setInitialPreviewConfig(previewConfigs);
        fileResult.setFileIds(StrUtil.join(fileArr));
        return fileResult;
    }
    @RequestMapping(value="/showImage",method = RequestMethod.GET)
    public String showImage(String imageID, HttpServletResponse response) throws IOException{
        
    	CheckFileAQ item = baseService.get(CheckFileAQ.class, imageID);

        byte[] fileBytes = getImage(item.getFilePath(), item.getFileName());
        
                
        if(fileBytes != null){
            BufferedOutputStream bos = null;
            try {
                bos = new BufferedOutputStream(response.getOutputStream());
                bos.write(fileBytes);
            } catch (IOException e) {
                throw e;
            } finally {
                if (bos != null)
                    bos.close();
            }
        }
        
        return null;
    }
    public byte[] getImage(String path, String name) {
        try {

            // 调接口写读文件
            return FileHelper.showImageFile(name, path);
        } catch (Exception e) {
            logger.error("", e);
        }

        return new byte[0];
    }
}
