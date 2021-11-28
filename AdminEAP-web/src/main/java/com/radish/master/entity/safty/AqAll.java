/**
 * 版权所有 (c) 2017，周庆博和他的朋友们有限公司
 */
package com.radish.master.entity.safty;

import com.cnpc.framework.annotation.Header;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * 类说明
 * 
 * <pre>
* Modify Information:
* Author        Date          Description
* ============ =========== ============================
* wangzhihao      2017年10月28日    Create this file
 * </pre>
 * 
 */
@Entity
@Table(name = "tbl_aqall")
@JsonIgnoreProperties(value = { "hibernateLazyInitializer", "handler", "fieldHandler" })
public class AqAll implements Serializable {


	private static final long serialVersionUID = 7064265855870717810L;
	/**
     * 主键ID自动生成策略
     */
    @Id
    @GenericGenerator(name = "id", strategy = "uuid")
    @GeneratedValue(generator = "id")
    @Column(name = "ID", length = 64)
    protected String id;

    
    @Header(name = "项目id")
    @Column(name = "proid")
    private String proid;

    @Header(name = "目录类型,取filesType")
    @Column(name = "type")
    private String type;
    
    @Header(name = "通用类型,取fileselects")
    @Column(name = "fileType")
    private String fileType;
    
    /**
     * 以下通用字段按页面放置顺序定义
     */
    @Column(name = "col1")
    private String col1;
    
    @Column(name = "col2")
    private String col2;
    
    @Column(name = "col3")
    private String col3;
    
    @Column(name = "col4")
    private String col4;
    
    @Column(name = "col5")
    private String col5;
    
    @Column(name = "col6")
    private String col6;
    
    @Column(name = "col7")
    private String col7;
    
    @Column(name = "col8")
    private String col8;
    

    @Header(name = "创建人")
    @Column(name = "create_name")
    private String create_name;

    @Header(name = "创建时间")
    @Column(name = "create_time")
    private Date create_time;

    
    @Header(name = "备注")
    @Column(name = "notes")
    private String notes;


	public String getId() {
		return id;
	}


	public void setId(String id) {
		this.id = id;
	}


	public String getProid() {
		return proid;
	}


	public void setProid(String proid) {
		this.proid = proid;
	}

	public String getType() {
		return type;
	}


	public void setType(String type) {
		this.type = type;
	}


	public String getFileType() {
		return fileType;
	}


	public void setFileType(String fileType) {
		this.fileType = fileType;
	}


	public String getCol1() {
		return col1;
	}


	public void setCol1(String col1) {
		this.col1 = col1;
	}


	public String getCol2() {
		return col2;
	}


	public void setCol2(String col2) {
		this.col2 = col2;
	}


	public String getCol3() {
		return col3;
	}


	public void setCol3(String col3) {
		this.col3 = col3;
	}


	public String getCol4() {
		return col4;
	}


	public void setCol4(String col4) {
		this.col4 = col4;
	}


	public String getCol5() {
		return col5;
	}


	public void setCol5(String col5) {
		this.col5 = col5;
	}


	public String getCol6() {
		return col6;
	}


	public void setCol6(String col6) {
		this.col6 = col6;
	}


	public String getCol7() {
		return col7;
	}


	public void setCol7(String col7) {
		this.col7 = col7;
	}


	public String getCol8() {
		return col8;
	}


	public void setCol8(String col8) {
		this.col8 = col8;
	}


	public String getCreate_name() {
		return create_name;
	}


	public void setCreate_name(String create_name) {
		this.create_name = create_name;
	}


	public Date getCreate_time() {
		return create_time;
	}


	public void setCreate_time(Date create_time) {
		this.create_time = create_time;
	}


	public String getNotes() {
		return notes;
	}


	public void setNotes(String notes) {
		this.notes = notes;
	}



}
