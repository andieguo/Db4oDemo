package com.andieguo.db4odemo;

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

public class DB4oTest extends TestCase {

	private ObjectContainer db;

	@Override
	protected void setUp() throws Exception {
		// TODO Auto-generated method stub
		super.setUp();
		try {
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
		common.objectClass(clazz).cascadeOnActivate(true);// 级联激活
		return configuration;
	}

	public void listResult(List<?> result) {
		for (Object o : result) {
			System.out.println(o.toString());
		}
	}

	// 根据人可查询到他所拥有的所有的车
	public List<AutoInfo> queryAllCar() {
		List<AutoInfo> autoInfos = db.query(new Predicate<AutoInfo>() {
			@Override
			public boolean match(AutoInfo autoInfo) {
				return autoInfo.getOwerNo().getName().equals("andy");
			}
		});
		listResult(autoInfos);
		return autoInfos;
	}

	// 根据车牌查询该车对象+//根据车牌查询该车对象的主人
	public People queryByLicensePlate(String licensePlate) {
		List<AutoInfo> autoInfos = db.query(new Predicate<AutoInfo>() {
			@Override
			public boolean match(AutoInfo autoInfo) {
				return autoInfo.getLicensePlate().equals("鄂A000000");
			}
		});
		if (autoInfos.size() < 1)
			return null;// 可能查询不到
		return autoInfos.get(0).getOwerNo();
	}

	// 查询所有的人+//查询所有的车
	public void queryAllPeople() {
		ObjectSet<People> result = db.query(People.class);// db.queryByExample(People.class);
		listResult(result);
	}

	public void insert() {
		People people = new People(2, "posly", "九江");
		AutoInfo autoInfo = new AutoInfo(8, "鄂A000000");
		// 设置People和AutoInfo的关系
		autoInfo.setOwerNo(people);
		people.addAuotInfo(autoInfo);
		db.store(people);
	}

	// 寻找车的信息--车的id--并执行更新
	public void findAutoInfoById() {
		List<AutoInfo> autoInfos = db.query(new Predicate<AutoInfo>() {
			@Override
			public boolean match(AutoInfo autoInfo) {
				return autoInfo.getId() == 1;
			}
		});
		if (autoInfos.size() > 0) {
			AutoInfo autoInfo = autoInfos.get(0);// 先得到要修改的实体
			autoInfo.setLicensePlate("刘德华");// 更新
			db.store(autoInfo);
		}
	}

	// 更新车的信息--车的id
	public void updateAutoInfo() {
		List<AutoInfo> autoInfos = db.query(new Predicate<AutoInfo>() {
			@Override
			public boolean match(AutoInfo autoInfo) {
				return autoInfo.getId() == 1;
			}
		});
		if (autoInfos.size() > 0) {
			AutoInfo autoInfo = autoInfos.get(0);// 先得到要修改的实体
			autoInfo.setLicensePlate("刘德华");// 更新
			db.store(autoInfo);
		}
	}

	// 添加车牌--人的id,AutoInfo
	public void insertAutoInfo() {
		List<People> result = db.query(new Predicate<People>() {

			@Override
			public boolean match(People people) {
				return people.getId() == 1;
			}
		});
		if (result.size() > 0) {
			People p = result.get(0);
			AutoInfo autoInfo = new AutoInfo(7, "鄂E000000");
			autoInfo.setOwerNo(p);//
			p.addAuotInfo(autoInfo);// 添加
			db.store(p);
		}
	}

	// 删除实体
	public void deleteAutoInfo(AutoInfo autoInfo) {
		db.delete(autoInfo);
	}

	public void activatePeople() {
		ObjectSet result = db.queryByExample(People.class);
		People people = (People) result.next();
		System.out.println("id:" + db.ext().getID(people) + ",people:" + people.toString());
		db.activate(people, 1);
		System.out.println("id:" + db.ext().getID(people) + ",people:" + people.toString());
	}

	public void getInternalID() {
		ObjectSet result = db.queryByExample(People.class);
		People people = (People) result.next();
		System.out.println("id:" + db.ext().getID(people) + ",people:" + people.toString());
		people = db.ext().getByID(42);
		System.out.println("people:" + people.toString());
	}

	@Override
	protected void tearDown() throws Exception {
		// TODO Auto-generated method stub
		super.tearDown();
		db.close();// 用完数据库后要关闭
	}
}
