/**
 * 版权所有 (c) 2017，周庆博和他的朋友们有限公司
 */
package com.radish.master.entity.safty;

import com.cnpc.framework.annotation.Header;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import org.hibernate.annotations.GenericGenerator;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;

import java.io.Serializable;
import java.util.Date;

/**
 * 类说明 安全事故死亡报表
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
@Table(name = "tbl_aqsgswbb")
@JsonIgnoreProperties(value = { "hibernateLazyInitializer", "handler", "fieldHandler" })
public class Aqsgssbb implements Serializable {


	private static final long serialVersionUID = 7064265855870717810L;
	/**
     * 主键ID自动生成策略
     */
    @Id
    @GenericGenerator(name = "id", strategy = "uuid")
    @GeneratedValue(generator = "id")
    @Column(name = "id", length = 64)
    protected String id;

    
    @Header(name = "项目id")
    @Column(name = "projectid")
    private String projectid;

    @Header(name = "项目经理")
    @Column(name = "xmjl")
    private String xmjl;
    @DateTimeFormat(pattern="yyyy-MM-dd")
    @Header(name = "事故日期")
    @Column(name = "sgfsrq")
    private Date sgfsrq;
    
    @Header(name = "事故性质")
    @Column(name = "sgxz")
    private String sgxz;

    @Header(name = "经济损失")
    @Column(name = "jjss")
    private String jjss;
    
    @Header(name = "事故经过")
    @Column(name = "sgjg")
    private String sgjg;
    
    @Header(name = "责任人")
    @Column(name = "zrr")
    private String zrr;
    
    @Header(name = "调研人员")
    @Column(name = "dyr")
    private String dyr;
    
    @Header(name = "填表人")
    @Column(name = "tbr")
    private String tbr;
    @DateTimeFormat(pattern="yyyy-MM-dd")
    @Header(name = "月报日期")
    @Column(name = "ybrq")
    private Date ybrq;
    
    @Header(name = "审核状态  10=新建,40=完成,20=驳回,30=项目经理审核")
    @Column(name = "status")
    private String status;
    
    @Header(name = "驳回原因")
    @Column(name = "bhyy")
    private String bhyy;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getProjectid() {
		return projectid;
	}

	public void setProjectid(String projectid) {
		this.projectid = projectid;
	}

	public String getXmjl() {
		return xmjl;
	}

	public void setXmjl(String xmjl) {
		this.xmjl = xmjl;
	}

	public Date getSgfsrq() {
		return sgfsrq;
	}

	public void setSgfsrq(Date sgfsrq) {
		this.sgfsrq = sgfsrq;
	}

	public String getSgxz() {
		return sgxz;
	}

	public void setSgxz(String sgxz) {
		this.sgxz = sgxz;
	}

	public String getJjss() {
		return jjss;
	}

	public void setJjss(String jjss) {
		this.jjss = jjss;
	}

	public String getSgjg() {
		return sgjg;
	}

	public void setSgjg(String sgjg) {
		this.sgjg = sgjg;
	}

	public String getZrr() {
		return zrr;
	}

	public void setZrr(String zrr) {
		this.zrr = zrr;
	}

	public String getDyr() {
		return dyr;
	}

	public void setDyr(String dyr) {
		this.dyr = dyr;
	}

	public String getTbr() {
		return tbr;
	}

	public void setTbr(String tbr) {
		this.tbr = tbr;
	}

	public Date getYbrq() {
		return ybrq;
	}

	public void setYbrq(Date ybrq) {
		this.ybrq = ybrq;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getBhyy() {
		return bhyy;
	}

	public void setBhyy(String bhyy) {
		this.bhyy = bhyy;
	}


}
