package com.madx.bwm.entity;

import com.j256.ormlite.table.DatabaseTable;

import java.io.Serializable;

/**
 * demo for a table entivy
 */
@DatabaseTable(tableName = "binded_devices")
public class BindedDevice implements Serializable {

//	/**
//	 *
//	 */
//	private static final long serialVersionUID = 1L;
//	@DatabaseField(generatedId = true)
//	private int id;
//	/** 设备名称 */
//	@DatabaseField(unique = true)
//	private String deviceName;
//	@DatabaseField
//	private String address;
//	@DatabaseField
//	private String sn;
//	@DatabaseField
//	private String securityKey;
//	/** 服务器生成的id */
//	@DatabaseField
//	private String deviceId;
//	/** 0短1长(,对应Brand中的type字段),-1为没指定，2为长短通用 */
//	@DatabaseField
//	private int type;
//	/**注册时的unix天数*/
//	@DatabaseField
//	private long registerDate;
//
//	@ForeignCollectionField(eager = true,orderColumnName="date",orderAscending=false)
//	private ForeignCollection<Score> scores;
//
//	public int getId() {
//		return id;
//	}
//
//	public void setId(int id) {
//		this.id = id;
//	}
//
//	public String getSn() {
//		return sn;
//	}
//
//	public void setSn(String sn) {
//		this.sn = sn;
//	}
//
//	public String getSecurityKey() {
//		return securityKey;
//	}
//
//	public void setSecurityKey(String securityKey) {
//		this.securityKey = securityKey;
//	}
//
//	public int getType() {
//		return type;
//	}
//
//	public void setType(int type) {
//		this.type = type;
//	}
//
//	public String getAddress() {
//		return address;
//	}
//
//	public void setAddress(String address) {
//		this.address = address;
//	}
//
//	public String getDeviceName() {
//		return deviceName;
//	}
//
//	public void setDeviceName(String deviceName) {
//		this.deviceName = deviceName;
//	}
//
//	public String getDeviceId() {
//		return deviceId;
//	}
//
//	public void setDeviceId(String deviceId) {
//		this.deviceId = deviceId;
//	}
//
//	public ForeignCollection<Score> getScores() {
//		return scores;
//	}
//
//	public void setScores(ForeignCollection<Score> scores) {
//		this.scores = scores;
//	}
//
//	public long getRegisterDate() {
//		return registerDate;
//	}
//
//	public void setRegisterDate(long registerDate) {
//		this.registerDate = registerDate;
//	}
//
//    public BindedDevice() {
//        // ORMLite needs a no-arg constructor
//    }
	

}
