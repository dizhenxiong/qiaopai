package com.lenovo.vctl.apps.commons.constants;

public class BIConstant {
	//邀请
	public static final String ACTION_USER_INVITE = "action_userinv";
	public static final String ACTION_FRIEND_ADD = "action_useradd";
	public static final String ACTION_USER_REGISTER = "action_useraccept";
	public static final String ACTION_FRIEND_RECOMMEND = "action_userrecommend";
	public static final String ACTION_SPREAD = "action_pushmsg";
	public static final String ACTION_LPS = "action_lps";
	
	public static final String TYPE_FRIEND_ADD_WAVER = "1";
	public static final String TYPE_FRIEND_ADD_NON_WAVER = "2";
	
	public static final String TYPE_FRIEND_RECOMMEND_FROM = "1"; //推荐方
	public static final String TYPE_FRIEND_RECOMMEND_TO = "2"; //接收方
	
	public static final String TYPE_PRAISE = "1"; //点赞
	public static final String TYPE_SEND_FLOWER = "2"; //送花
	
	//登陆
	public static final String ACTION_USER_LOGIN = "action_logon";
	public static final String ACTION_USER_AUTHENTICATION = "action_auth";
	public static final String ACTION_USER_NEW_REGISTER = "action_regist";
	
	//新增bi统计
	//5.28
	public static final String ACTION_VIDEO = "action_video"; //视频日志信息
	public static final String ACTION_VOICE = "action_video"; //语音日志信息
	public static final String ACTION_TEXT_PUBLISH = "action_text_publish"; //文本内容发布日志信息
	//4.28
	public static final String ACTION_SURPRISE = "action_surprise"; //Superise日志信息
	public static final String ACTION_VIDEO_HI = "action_video_hi"; //视频招呼日志信息
	public static final String ACTION_VIDEO_MSG = "action_video_msg"; //视频留言日志信息
	public static final String ACTION_VOICE_MSG = "action_voice_msg"; //语音留言日志信息
	public static final String ACTION_FRIEND_ADD_ADDRESS = "action_friend_add"; //通信录匹配加好友
	public static final String ACTION_FRIEND_SUCC_ADDRESS = "action_friend_succ"; //通信录匹配加好友成功
	public static final String ACTION_USER_UPDATE = "action_user_update"; //更新个人资料
	public static final String ACTION_USER_LIKE = "action_user_like"; //点赞送花
	public static final String ACTION_USER_PUBLISH = "action_user_publish"; //内容发布
}
