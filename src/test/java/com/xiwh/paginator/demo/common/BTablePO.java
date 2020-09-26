package com.xiwh.paginator.demo.common;

import java.io.Serializable;

public class BTablePO implements Serializable {
    private int id;
    private String name;
    private String value;
    private String value2;

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getValue() {
        return value;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getValue2() {
        return value2;
    }

    public void setValue2(String value2) {
        this.value2 = value2;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("BTableDO{");
        sb.append("id=").append(id);
        sb.append(", name='").append(name).append('\'');
        sb.append(", value='").append(value).append('\'');
        sb.append(", value2='").append(value2).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
