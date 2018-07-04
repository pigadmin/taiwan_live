package com.live.bean;

import java.io.Serializable;

public class Ad implements Serializable {
    private int id;

    private String name;

    private int type;

    private String path;

    private String createtime;

    private String backFile;

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return this.id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getType() {
        return this.type;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getPath() {
        return this.path;
    }

    public void setCreatetime(String createtime) {
        this.createtime = createtime;
    }

    public String getCreatetime() {
        return this.createtime;
    }

    public void setBackFile(String backFile) {
        this.backFile = backFile;
    }

    public String getBackFile() {
        return this.backFile;
    }
}
