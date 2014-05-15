package com.lenovo.vctl.apps.commons.utils.test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import junit.framework.Assert;

import org.apache.commons.lang3.StringUtils;
import org.junit.Ignore;
import org.junit.Test;

import com.google.gson.reflect.TypeToken;
import com.lenovo.vctl.apps.commons.constants.SignConstant;
import com.lenovo.vctl.apps.commons.utils.CalendarUtil;
import com.lenovo.vctl.apps.commons.utils.FlagUtil;
import com.lenovo.vctl.apps.commons.utils.GsonUtil;
import com.lenovo.vctl.apps.commons.utils.MyListUtil;
import com.lenovo.vctl.apps.commons.utils.StringUtil;
import com.lenovo.vctl.apps.commons.utils.TimeUtil;

@Ignore
public class MyListTest {
	
	@Test
	public void testGetList() {
		List<String[]> objects = new ArrayList<String[]>();
		
		objects.add(new String[]{"123", "234"});
		objects.add(new String[]{"123", "234", "345"});
		objects.add(new String[]{"123", "234"});
		objects.add(new String[]{"123", "234", "345", "456"});
		
		List<Object> strs = MyListUtil.getList(5, objects);
		System.out.println(strs);
	}
	
	@Test
	public void testConvert2Map() {
		List<String[]> objects = new ArrayList<String[]>();
		
		objects.add(new String[]{"123", "234"});
		objects.add(new String[]{"1231", "2341", "3451"});
		objects.add(new String[]{"1232", "2342"});
		objects.add(new String[]{"1233", "2343", "3452", "4561"});
		
		Map<String, String> strs = MyListUtil.convert2Map(0, 2, objects);
		System.out.println(strs);
	}
	
	@Test
	public void testSubList() {
		List<String> friendMobiles = new ArrayList<String>();
		friendMobiles.add("123");
		friendMobiles.add("123");
		friendMobiles.add("123");
		friendMobiles.add("123");
		friendMobiles.add("123");
		friendMobiles.add("123");
		
		List<String> sub = MyListUtil.subList(friendMobiles, 0, 19);
		Assert.assertEquals(6, sub.size());
		
		sub = MyListUtil.subList(friendMobiles, 0, 6);
		Assert.assertEquals(6, sub.size());
	}
	
	@Test
	public void testFixTime() {
		Long current = System.currentTimeMillis();
		Long time = TimeUtil.getLatestHourFixedTimePoint(System.currentTimeMillis(), 120);
		System.out.println("current : " + CalendarUtil.formateDate(current, CalendarUtil.defaultTimeFormate));
		System.out.println("fixed : " + CalendarUtil.formateDate(time, CalendarUtil.defaultTimeFormate));
		System.out.println(" === ");
	}
	
	@Test
	public void testJsonString() {
		
		String noticeType = "noticeType";
		String receiverMobile = "receiverMobile";
		String receiverId = "receiverId";
		String senderMobile = "senderMobile";
		String senderId = "senderId";
		String senderName = "senderName";
		String createAt = "createAt";
		
		String extraInfo = "{\"info\":[{\"name\":\"\",\"pic\":\"http://dev.ifaceshow.com/a/2013/0311/1600/b7c3f5b9_145722bf4edc3b_src.jpg\",\"mobile\":\"18911779942\"},{\"name\":\"曹操\",\"pic\":\"http://dev.ifaceshow.com/a/2012/1228/1807/ac1000c9_154422ac2367f_src.jpg\",\"mobile\":\"15201394171\"}],\"size\":2}";
//		String extraInfo = "{\"info\":[{\"info\":\"info\"}]}";
		String extraInfoValue = "\"" + StringUtils.defaultString(extraInfo)+ "\"";
		boolean isJson = GsonUtil.maybeJson(extraInfo);
		if (isJson) {
			extraInfoValue = extraInfo;
		}
		System.out.println("{\"noticeType\":" + noticeType + ",\"receiverMobile\":\""
		+ StringUtils.defaultString(receiverMobile) + "\",\"receiverId\":" + receiverId
		+ ",\"senderMobile\":\"" + StringUtils.defaultString(senderMobile) + "\",\"senderId\":" + senderId 
		+ ",\"senderName\":\"" + StringUtils.defaultString(senderName) + "\",\"extraInfo\":" + extraInfoValue
		+ ",\"createAt\":\"" + StringUtils.defaultString(createAt) + "\"}");
	}
	
	@Test
	public void testGson() {
		QueueItemWrapperModel wrap = new QueueItemWrapperModel();
		Map<String, Object> object = new HashMap<String, Object>();
		object.put("key1", "val1");
		object.put("key2", "val2");
		object.put("key3", "val3");
		object.put("key4", "val4");
		
		wrap.setId(123L);
		wrap.setKey("eee");
		wrap.setQueueName("queue");
		wrap.setCreateAt(123L);
		wrap.setObject(object);
		System.out.println("wefw");
		String jsonString = GsonUtil.toJson(wrap);
		System.out.println(jsonString);
		
		QueueItemWrapperModel wrapfrom = GsonUtil.fromJson(jsonString, QueueItemWrapperModel.class);
		System.out.println(wrapfrom.getObject());
	}
	
	@Test
	public void testGetArray() {
		String str = "{\"title\":\"我乐橱柜郭的秀\",\"content\":\"怎么吃都好吃\",\"picture\":\"http://imgs.dapenti.org:88/dapenti/CNnPsfpq/DInus.jpg\",\"url\":\"http://www.ifaceshow.com/action.html\",\"receiverDevices\":[\"WIN7\"]}";
		Map<String, Object> message = GsonUtil.fromJson(str, new TypeToken<Map<String, Object>>() { }.getType());
		Object receiverDevices = message.get("receiverDevices");
		if (null != receiverDevices) {
			@SuppressWarnings("unchecked")
			ArrayList<String> devices = (ArrayList<String>) receiverDevices;
			System.out.println(devices.get(0));
		}
	}
	
	@Test
	public void testXmlEscape() {
		System.out.println(StringUtil.escapeXml("&*(<>\\wef~!@#$%^&*()_+"));
	}
	
	@Test
	public void testFlag() {
		boolean result = FlagUtil.hasSetFlag(8L, 8L);
		Assert.assertTrue(result);
		result = FlagUtil.hasSetFlag(9L, 8L);
		Assert.assertTrue(result);
		result = FlagUtil.hasSetFlag(10L, 8L);
		Assert.assertTrue(result);
		result = FlagUtil.hasSetFlag(11L, 8L);
		Assert.assertTrue(result);
		result = FlagUtil.hasSetFlag(12L, 8L);
		Assert.assertTrue(result);
		result = FlagUtil.hasSetFlag(13L, 8L);
		Assert.assertTrue(result);
		result = FlagUtil.hasSetFlag(14L, 8L);
		Assert.assertTrue(result);
		result = FlagUtil.hasSetFlag(15L, 8L);
		Assert.assertTrue(result);
		
		result = FlagUtil.hasSetFlag(16L, 8L);
		Assert.assertFalse(result);
		result = FlagUtil.hasSetFlag(17L, 8L);
		Assert.assertFalse(result);
		result = FlagUtil.hasSetFlag(18L, 8L);
		Assert.assertFalse(result);
		result = FlagUtil.hasSetFlag(19L, 8L);
		Assert.assertFalse(result);
	}
}

class QueueItemWrapperModel {
	private Long id;
	private String queueName;
	private String key;
	private Long createAt;
	
	private Object object;
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getQueueName() {
		return queueName;
	}
	public void setQueueName(String queueName) {
		this.queueName = queueName;
	}
	public String getKey() {
		return key;
	}
	public void setKey(String key) {
		this.key = key;
	}
	public Long getCreateAt() {
		return createAt;
	}
	public void setCreateAt(Long createAt) {
		this.createAt = createAt;
	}
	public Object getObject() {
		return object;
	}
	public void setObject(Object object) {
		this.object = object;
	}
}
