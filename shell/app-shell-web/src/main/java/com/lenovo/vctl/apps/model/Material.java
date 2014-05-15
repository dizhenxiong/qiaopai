package com.lenovo.vctl.apps.model;

import java.io.Serializable;

/**
 * Created with IntelliJ IDEA.
 * User: kangyang1
 * Date: 14-5-15
 * Time: 下午3:05
 * To change this template use File | Settings | File Templates.
 */
public class Material implements Serializable {

    private static final long serialVersionUID = -7203343128718524236L;

    private String title ; //标题

    private String type;

    private String email;
    private String emailcc;  //抄送所用的邮箱地址

    private String country;   //国家

    private String commtyp ;  //联系方式

    private String area    ;  //shell的大区

    private String length  ;  //持续时长

    private String firstTime ; //开始时间

    private String deadline;  //截止时间





}
