package com.andieguo.db4odemo;

import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import com.andieguo.bean.AutoInfo;
import com.andieguo.bean.People;
import com.db4o.Db4oEmbedded;
import com.db4o.ObjectContainer;
import com.db4o.ObjectSet;
import com.db4o.config.CommonConfiguration;
import com.db4o.config.EmbeddedConfiguration;
import com.db4o.query.Predicate;

public class DB4oQBETest extends TestCase {

	private ObjectContainer db;

	@Override
	protected void setUp() throws Exception {
		// TODO Auto-generated method stub
		super.setUp();
		try {
			//
			// EmbeddedConfiguration config = Db4oEmbedded.newConfiguration();
			// config.common().objectClass(People.class).cascadeOnUpdate(true);
			db = Db4oEmbedded.openFile(dbConfig(People.class, "id"), "auto.yap");

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// 数据库的参数配置. 数据库配置并没有存储到文件,每次打开数据库执行此方法进行配置
	private EmbeddedConfiguration dbConfig(Class clazz, String unique) {
		EmbeddedConfiguration configuration = Db4oEmbedded.newConfiguration();
		CommonConfiguration common = configuration.common();
		if (unique != null) {
			common.objectClass(clazz).objectField(unique).indexed(true);// 添加索引
			common.objectClass(clazz).generateUUIDs(true);// 添加UUID
		}
		common.objectClass(clazz).cascadeOnUpdate(true);// 级联更新
		common.objectClass(clazz).cascadeOnDelete(true);// 级联删除
		common.objectClass(clazz).cascadeOnActivate(true);// 级联激活
		return configuration;
	}

	public void listResult(List<?> result) {
		for (Object o : result) {
			if (o != null) {
				System.out.println(o.toString());
			}

		}
	}

	// Query-By-Example（简称QBE）查询
	public void queryQBEALL() {
		ObjectSet<People> result = db.queryByExample(People.class);
		listResult(result);
	}

	// Query-By-Example（简称QBE）查询 ： 根据人ID查对象
	public void queryQBEById() {
		ObjectSet<People> result = db.queryByExample(new People(1));
		People people = result.next();
		System.out.println(people.toString());
	}

	// 根据人名查对象
	public void queryQBEByName() {
		ObjectSet<People> result = db.queryByExample(new People("andy"));
		People people = result.next();
		System.out.println(people.toString());
	}

	// 根据人名查对象
	public void queryQBE() {
		ObjectSet<People> result = db.queryByExample(new People(null, "andy", null));
		People people = result.next();
		System.out.println(people.toString());
	}

	// 根据人查询到他所拥有的所有的车
	public void queryQBECars() {
		ObjectSet<People> result = db.queryByExample(new People(1));
		People people = result.next();
		List<AutoInfo> autoInfos = people.getAuotInfoList();
		listResult(autoInfos);
	}

	// 根据车牌查询该车对象的主人
	public void queryQBEPeopleByLicensePlate() {
		ObjectSet<AutoInfo> result = db.queryByExample(new AutoInfo(null, "刘德华"));
		AutoInfo autoInfo = result.next();
		People people = autoInfo.getOwerNo();
		System.out.println(people.toString());

	}

	// 根据ID查询车的信息
	public void queryQBECarById() {
		ObjectSet<AutoInfo> result = db.queryByExample(new AutoInfo(4));
		AutoInfo autoInfo = result.next();
		System.out.println(autoInfo.toString());
	}

	public void insert() {
		People people = new People(3, "jack", "香港");
		AutoInfo autoInfo = new AutoInfo(8, "鄂A000000");
		autoInfo.setOwerNo(people);// 设置AutoInfo和People的关系
		people.addAuotInfo(autoInfo);// 设置People和AutoInfo的关系
		db.store(people);// 双向绑定
	}

	// 添加车牌：根据人的id添加特定的AutoInfo
	public void insertAutoInfo() {// 前提条件是：cascadeOnUpdate设置为true
		ObjectSet<People> result = db.queryByExample(new People(3));
		People p = result.next();
		AutoInfo autoInfo = new AutoInfo(11, "鄂F000000");
		autoInfo.setOwerNo(p);// 双向绑定
		p.addAuotInfo(autoInfo);// 双向绑定
		db.store(p);
	}

	// 更新车的信息：根据车的id查询实体，更改字段后执行更新
	public void updateByCarId() {
		ObjectSet<AutoInfo> result = db.queryByExample(new AutoInfo(null, "刘德华"));
		AutoInfo autoInfo = result.next();
		autoInfo.setLicensePlate("鄂A00ANDY");
		db.store(autoInfo);
	}

	// 1、如果不先解除人与车的关系，也没有设置cascadeOnDelete为true,人的list<AutoInfo>中将出现null的元素。
	// 2、级联删除：如果设置了cascadeOnDelete为true，删除autoInfo的同时，删除与p绑定的autoinfo信息.
	public void cascadeDeleteByCarId() {
		ObjectSet<AutoInfo> result = db.queryByExample(new AutoInfo(3));
		AutoInfo autoInfo = result.next();
		db.delete(autoInfo);// 删除autoInfo实体
	}

	// 删除车的信息：先解除人与车的绑定关系，再删除车的信息
	public void deleteByCarId() {// 添加时是双向绑定的，删除时要解除双向绑定
		ObjectSet<AutoInfo> result = db.queryByExample(new AutoInfo(2));
		AutoInfo autoInfo = result.next();
		People people = autoInfo.getOwerNo();// 根据车找到主人
		people.removeAutoInfo(autoInfo);// 解除主人与该车绑定关系，使得该车成为一个独立的个体
		db.store(people);// 将解除关系后的People实体存入数据库
		db.delete(autoInfo);// 删除autoInfo实体
	}

	public void addList() {
		List<Integer> list = new ArrayList<Integer>();
		list.add(null);
		list.add(null);
		System.out.println(list.size());
	}

	// 删除人：1、先删除车与人的绑定关系；2、再删除车;
	public void cascadeDeleteByPeopleId() {
		ObjectSet<People> result = db.queryByExample(new People(3));
		People p = result.next();
		db.delete(p);
		// 1、如果不接触绑定关系的话，也没有设置cascadeOnDelete为true,则车实体的主人将会置为null.该车将没有主人.
		// 2、级联删除：如果设置了cascadeOnDelete为true，删除p的同时，删除与p绑定的autoinfo信息.
	}

	// 解除绑定关系
	public void removeCarFromPeople() {
		ObjectSet<People> result = db.queryByExample(new People(1));
		People p = result.next();
		p.getAuotInfoList().remove(0);// 解除绑定关系
		db.store(p);
	}

	// 删除实体
	public void deleteAutoInfo(AutoInfo autoInfo) {
		db.delete(autoInfo);
	}

	@Override
	protected void tearDown() throws Exception {
		// TODO Auto-generated method stub
		super.tearDown();
		db.close();// 用完数据库后要关闭
	}

}
