package com.bondwithme.BondCorp.entity;

/**
 *
 * 关系类型枚举
 * Created by Jackie on 8/19/15.
 *
 * @author Jackie
 * @version 1.0
 */
public enum RelationshipEnum {
    supervisor("supervisor"), colleague("colleague"), subordinate("subordinate");
    private final String type;

    RelationshipEnum(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    @Override
    public String toString() {
        return type;
    }
}
