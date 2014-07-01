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

	// ���ݿ�Ĳ�������. ���ݿ����ò�û�д洢���ļ�,ÿ�δ����ݿ�ִ�д˷�����������
	private EmbeddedConfiguration dbConfig(Class clazz, String unique) {
		EmbeddedConfiguration configuration = Db4oEmbedded.newConfiguration();
		CommonConfiguration common = configuration.common();
		if (unique != null) {
			common.objectClass(clazz).objectField(unique).indexed(true);// �������
			common.objectClass(clazz).generateUUIDs(true);// ���UUID
		}
		common.objectClass(clazz).cascadeOnUpdate(true);// ��������
		common.objectClass(clazz).cascadeOnDelete(true);// ����ɾ��
		common.objectClass(clazz).cascadeOnActivate(true);// ��������
		return configuration;
	}

	public void listResult(List<?> result) {
		for (Object o : result) {
			if (o != null) {
				System.out.println(o.toString());
			}

		}
	}

	// Query-By-Example�����QBE����ѯ
	public void queryQBEALL() {
		ObjectSet<People> result = db.queryByExample(People.class);
		listResult(result);
	}

	// Query-By-Example�����QBE����ѯ �� ������ID�����
	public void queryQBEById() {
		ObjectSet<People> result = db.queryByExample(new People(1));
		People people = result.next();
		System.out.println(people.toString());
	}

	// �������������
	public void queryQBEByName() {
		ObjectSet<People> result = db.queryByExample(new People("andy"));
		People people = result.next();
		System.out.println(people.toString());
	}

	// �������������
	public void queryQBE() {
		ObjectSet<People> result = db.queryByExample(new People(null, "andy", null));
		People people = result.next();
		System.out.println(people.toString());
	}

	// �����˲�ѯ������ӵ�е����еĳ�
	public void queryQBECars() {
		ObjectSet<People> result = db.queryByExample(new People(1));
		People people = result.next();
		List<AutoInfo> autoInfos = people.getAuotInfoList();
		listResult(autoInfos);
	}

	// ���ݳ��Ʋ�ѯ�ó����������
	public void queryQBEPeopleByLicensePlate() {
		ObjectSet<AutoInfo> result = db.queryByExample(new AutoInfo(null, "���»�"));
		AutoInfo autoInfo = result.next();
		People people = autoInfo.getOwerNo();
		System.out.println(people.toString());

	}

	// ����ID��ѯ������Ϣ
	public void queryQBECarById() {
		ObjectSet<AutoInfo> result = db.queryByExample(new AutoInfo(4));
		AutoInfo autoInfo = result.next();
		System.out.println(autoInfo.toString());
	}

	public void insert() {
		People people = new People(3, "jack", "���");
		AutoInfo autoInfo = new AutoInfo(8, "��A000000");
		autoInfo.setOwerNo(people);// ����AutoInfo��People�Ĺ�ϵ
		people.addAuotInfo(autoInfo);// ����People��AutoInfo�Ĺ�ϵ
		db.store(people);// ˫���
	}

	// ��ӳ��ƣ������˵�id����ض���AutoInfo
	public void insertAutoInfo() {// ǰ�������ǣ�cascadeOnUpdate����Ϊtrue
		ObjectSet<People> result = db.queryByExample(new People(3));
		People p = result.next();
		AutoInfo autoInfo = new AutoInfo(11, "��F000000");
		autoInfo.setOwerNo(p);// ˫���
		p.addAuotInfo(autoInfo);// ˫���
		db.store(p);
	}

	// ���³�����Ϣ�����ݳ���id��ѯʵ�壬�����ֶκ�ִ�и���
	public void updateByCarId() {
		ObjectSet<AutoInfo> result = db.queryByExample(new AutoInfo(null, "���»�"));
		AutoInfo autoInfo = result.next();
		autoInfo.setLicensePlate("��A00ANDY");
		db.store(autoInfo);
	}

	// 1��������Ƚ�����복�Ĺ�ϵ��Ҳû������cascadeOnDeleteΪtrue,�˵�list<AutoInfo>�н�����null��Ԫ�ء�
	// 2������ɾ�������������cascadeOnDeleteΪtrue��ɾ��autoInfo��ͬʱ��ɾ����p�󶨵�autoinfo��Ϣ.
	public void cascadeDeleteByCarId() {
		ObjectSet<AutoInfo> result = db.queryByExample(new AutoInfo(3));
		AutoInfo autoInfo = result.next();
		db.delete(autoInfo);// ɾ��autoInfoʵ��
	}

	// ɾ��������Ϣ���Ƚ�����복�İ󶨹�ϵ����ɾ��������Ϣ
	public void deleteByCarId() {// ���ʱ��˫��󶨵ģ�ɾ��ʱҪ���˫���
		ObjectSet<AutoInfo> result = db.queryByExample(new AutoInfo(2));
		AutoInfo autoInfo = result.next();
		People people = autoInfo.getOwerNo();// ���ݳ��ҵ�����
		people.removeAutoInfo(autoInfo);// ���������ó��󶨹�ϵ��ʹ�øó���Ϊһ�������ĸ���
		db.store(people);// �������ϵ���Peopleʵ��������ݿ�
		db.delete(autoInfo);// ɾ��autoInfoʵ��
	}

	public void addList() {
		List<Integer> list = new ArrayList<Integer>();
		list.add(null);
		list.add(null);
		System.out.println(list.size());
	}

	// ɾ���ˣ�1����ɾ�������˵İ󶨹�ϵ��2����ɾ����;
	public void cascadeDeleteByPeopleId() {
		ObjectSet<People> result = db.queryByExample(new People(3));
		People p = result.next();
		db.delete(p);
		// 1��������Ӵ��󶨹�ϵ�Ļ���Ҳû������cascadeOnDeleteΪtrue,��ʵ������˽�����Ϊnull.�ó���û������.
		// 2������ɾ�������������cascadeOnDeleteΪtrue��ɾ��p��ͬʱ��ɾ����p�󶨵�autoinfo��Ϣ.
	}

	// ����󶨹�ϵ
	public void removeCarFromPeople() {
		ObjectSet<People> result = db.queryByExample(new People(1));
		People p = result.next();
		p.getAuotInfoList().remove(0);// ����󶨹�ϵ
		db.store(p);
	}

	// ɾ��ʵ��
	public void deleteAutoInfo(AutoInfo autoInfo) {
		db.delete(autoInfo);
	}

	@Override
	protected void tearDown() throws Exception {
		// TODO Auto-generated method stub
		super.tearDown();
		db.close();// �������ݿ��Ҫ�ر�
	}

}
