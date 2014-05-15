package com.lenovo.vctl.apps.model;

import javax.persistence.Entity;
import javax.persistence.Table;
import java.io.Serializable;

/**
 * Created with IntelliJ IDEA.
 * User: kangyang1
 * Date: 14-5-15
 * Time: 下午3:05
 * To change this template use File | Settings | File Templates.
 */

@Entity
@Table(name = "user_invite_record")
public class Material implements Serializable {

    private static final long serialVersionUID = -7203343128718524236L;

    private String title; //标题

    private String type;

    private String email;
    private String emailcc;  //抄送所用的邮箱地址
    private String country;   //国家
    private String commtyp;  //联系方式
    private String area;  //shell的大区
    private String length;  //持续时长
    private String firstTime; //开始时间
    private String deadline;  //截止时间

    public Material() {
    }


    public Material(String title, String type, String email, String emailcc, String country, String commtyp, String area, String length, String firstTime, String deadline) {
        this.title = title;
        this.type = type;
        this.email = email;
        this.emailcc = emailcc;
        this.country = country;
        this.commtyp = commtyp;
        this.area = area;
        this.length = length;
        this.firstTime = firstTime;
        this.deadline = deadline;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getEmailcc() {
        return emailcc;
    }

    public void setEmailcc(String emailcc) {
        this.emailcc = emailcc;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getCommtyp() {
        return commtyp;
    }

    public void setCommtyp(String commtyp) {
        this.commtyp = commtyp;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public String getLength() {
        return length;
    }

    public void setLength(String length) {
        this.length = length;
    }

    public String getFirstTime() {
        return firstTime;
    }

    public void setFirstTime(String firstTime) {
        this.firstTime = firstTime;
    }

    public String getDeadline() {
        return deadline;
    }

    public void setDeadline(String deadline) {
        this.deadline = deadline;
    }


    @Override
    public String toString() {
        return "Material{" +
                "title='" + title + '\'' +
                ", type='" + type + '\'' +
                ", email='" + email + '\'' +
                ", emailcc='" + emailcc + '\'' +
                ", country='" + country + '\'' +
                ", commtyp='" + commtyp + '\'' +
                ", area='" + area + '\'' +
                ", length='" + length + '\'' +
                ", firstTime='" + firstTime + '\'' +
                ", deadline='" + deadline + '\'' +
                '}';
    }

}
