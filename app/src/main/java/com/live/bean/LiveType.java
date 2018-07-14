package com.live.bean;

import java.io.Serializable;

public class LiveType implements Serializable {
    private int id;

    private String icon;

    private String name;

    private int position;

    private String ngPath;

    public void setId(int id){
        this.id = id;
    }
    public int getId(){
        return this.id;
    }
    public void setIcon(String icon){
        this.icon = icon;
    }
    public String getIcon(){
        return this.icon;
    }
    public void setName(String name){
        this.name = name;
    }
    public String getName(){
        return this.name;
    }
    public void setPosition(int position){
        this.position = position;
    }
    public int getPosition(){
        return this.position;
    }
    public void setNgPath(String ngPath){
        this.ngPath = ngPath;
    }
    public String getNgPath(){
        return this.ngPath;
    }
}
