package com.madx.bwm.entity;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.io.Serializable;

/**
 * demo for a table entivy
 */
@DatabaseTable(tableName = "orm_entity_demos")
public class OrmEntityDemo implements Serializable {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;
	@DatabaseField(generatedId = true)
	private int id;
	@DatabaseField
	private String securityKey;
	/** 0短1长(,对应Brand中的type字段),-1为没指定，2为长短通用 */
	@DatabaseField
	private int type;
	/**注册时的unix天数*/
	@DatabaseField
	private long registerDate;

    /**实体关系UserEntity为测试用，可以到UserEntity注释看反引用关系写法*/
//	@ForeignCollectionField(eager = true,orderColumnName="date",orderAscending=false)
//	private ForeignCollection<UserEntity> scores;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}


	public String getSecurityKey() {
		return securityKey;
	}

	public void setSecurityKey(String securityKey) {
		this.securityKey = securityKey;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

//    public ForeignCollection<UserEntity> getScores() {
//        return scores;
//    }
//
//    public void setScores(ForeignCollection<UserEntity> scores) {
//        this.scores = scores;
//    }

    public long getRegisterDate() {
		return registerDate;
	}

	public void setRegisterDate(long registerDate) {
		this.registerDate = registerDate;
	}

    public OrmEntityDemo() {
        // ORMLite needs a no-arg constructor
    }


}
