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

	// ���ݿ�Ĳ�������. ���ݿ����ò�û�д洢���ļ�,ÿ�δ����ݿ�ִ�д˷�����������
	private EmbeddedConfiguration dbConfig(Class clazz, String unique) {
		EmbeddedConfiguration configuration = Db4oEmbedded.newConfiguration();
		CommonConfiguration common = configuration.common();
		if (unique != null) {
			common.objectClass(clazz).objectField(unique).indexed(true);// �������
			common.objectClass(clazz).generateUUIDs(true);// ���UUID
		}
		common.objectClass(clazz).cascadeOnUpdate(true);// ��������
		common.objectClass(clazz).cascadeOnActivate(true);// ��������
		return configuration;
	}

	public void listResult(List<?> result) {
		for (Object o : result) {
			System.out.println(o.toString());
		}
	}

	// �����˿ɲ�ѯ������ӵ�е����еĳ�
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

	// ���ݳ��Ʋ�ѯ�ó�����+//���ݳ��Ʋ�ѯ�ó����������
	public People queryByLicensePlate(String licensePlate) {
		List<AutoInfo> autoInfos = db.query(new Predicate<AutoInfo>() {
			@Override
			public boolean match(AutoInfo autoInfo) {
				return autoInfo.getLicensePlate().equals("��A000000");
			}
		});
		if (autoInfos.size() < 1)
			return null;// ���ܲ�ѯ����
		return autoInfos.get(0).getOwerNo();
	}

	// ��ѯ���е���+//��ѯ���еĳ�
	public void queryAllPeople() {
		ObjectSet<People> result = db.query(People.class);// db.queryByExample(People.class);
		listResult(result);
	}

	public void insert() {
		People people = new People(2, "posly", "�Ž�");
		AutoInfo autoInfo = new AutoInfo(8, "��A000000");
		// ����People��AutoInfo�Ĺ�ϵ
		autoInfo.setOwerNo(people);
		people.addAuotInfo(autoInfo);
		db.store(people);
	}

	// Ѱ�ҳ�����Ϣ--����id--��ִ�и���
	public void findAutoInfoById() {
		List<AutoInfo> autoInfos = db.query(new Predicate<AutoInfo>() {
			@Override
			public boolean match(AutoInfo autoInfo) {
				return autoInfo.getId() == 1;
			}
		});
		if (autoInfos.size() > 0) {
			AutoInfo autoInfo = autoInfos.get(0);// �ȵõ�Ҫ�޸ĵ�ʵ��
			autoInfo.setLicensePlate("���»�");// ����
			db.store(autoInfo);
		}
	}

	// ���³�����Ϣ--����id
	public void updateAutoInfo() {
		List<AutoInfo> autoInfos = db.query(new Predicate<AutoInfo>() {
			@Override
			public boolean match(AutoInfo autoInfo) {
				return autoInfo.getId() == 1;
			}
		});
		if (autoInfos.size() > 0) {
			AutoInfo autoInfo = autoInfos.get(0);// �ȵõ�Ҫ�޸ĵ�ʵ��
			autoInfo.setLicensePlate("���»�");// ����
			db.store(autoInfo);
		}
	}

	// ��ӳ���--�˵�id,AutoInfo
	public void insertAutoInfo() {
		List<People> result = db.query(new Predicate<People>() {

			@Override
			public boolean match(People people) {
				return people.getId() == 1;
			}
		});
		if (result.size() > 0) {
			People p = result.get(0);
			AutoInfo autoInfo = new AutoInfo(7, "��E000000");
			autoInfo.setOwerNo(p);//
			p.addAuotInfo(autoInfo);// ���
			db.store(p);
		}
	}

	// ɾ��ʵ��
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
		db.close();// �������ݿ��Ҫ�ر�
	}
}
