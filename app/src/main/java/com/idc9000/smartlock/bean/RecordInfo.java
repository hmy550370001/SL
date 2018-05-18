package com.idc9000.smartlock.bean;

import java.io.Serializable;

/**
 * Created by 赵先生 on 2017/10/26.
 */

public class RecordInfo implements Serializable {

    private String name="";
    private String content="";
    private String type="";

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "RecordInfo{" +
                "name='" + name + '\'' +
                ", content='" + content + '\'' +
                ", type='" + type + '\'' +
                '}';
    }
}
