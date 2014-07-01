package com.andieguo.bean;

import java.util.ArrayList;
import java.util.List;
//���복��һ�Զ�Ĺ�ϵ
//�����˿ɲ�ѯ������ӵ�е����еĳ�
//���ݳ��ɲ�ѯ������������˭��һ����ֻ��һ������
public class People {
	private Integer id;
	private String name;
	private String address;
	private List<AutoInfo> autoInfoList;
	
	public People(Integer id) {
		super();
		this.id = id;
	}
	public People(Integer id, String name, String address) {
		super();
		this.id = id;
		this.name = name;
		this.address = address;
	}
	public People(String name) {
		this.name = name;
	}
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public List<AutoInfo> getAuotInfoList() {
		return autoInfoList;
	}
	public void addAuotInfo(AutoInfo autotInfo) {
		if(null == this.autoInfoList){
			this.autoInfoList = new ArrayList<AutoInfo>();
		}
		this.autoInfoList.add(autotInfo);
	}
	
	public void removeAutoInfo(AutoInfo autotInfo){
		if(this.autoInfoList.size() > 0){
			this.autoInfoList.remove(autotInfo);
		}
	}
	
	
	@Override
	public String toString() {
		return "People [id=" + id + ", name=" + name + ", address=" + address + "]";
	}

	

}
