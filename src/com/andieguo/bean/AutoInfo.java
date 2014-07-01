package com.andieguo.bean;

public class AutoInfo {
	private Integer id;
	private String licensePlate;
	private People owerNo;
	
	
	@Override
	public String toString() {
		return "AutoInfo [id=" + id + ", licensePlate=" + licensePlate + ", owerNo=" + owerNo.toString() + "]";
	}
	public AutoInfo(Integer id, String licensePlate) {
		super();
		this.id = id;
		this.licensePlate = licensePlate;
	}
	public AutoInfo(Integer id) {
		// TODO Auto-generated constructor stub
		this.id = id ;
	}
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getLicensePlate() {
		return licensePlate;
	}
	public void setLicensePlate(String licensePlate) {
		this.licensePlate = licensePlate;
	}
	public People getOwerNo() {
		return owerNo;
	}
	public void setOwerNo(People owerNo) {
		this.owerNo = owerNo;
	}
}
