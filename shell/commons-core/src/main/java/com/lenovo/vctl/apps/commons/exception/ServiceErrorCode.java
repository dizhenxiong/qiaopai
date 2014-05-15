package com.lenovo.vctl.apps.commons.exception;

/**
 * 错误代码类
 * 
 * @author huwei
 * */
public class ServiceErrorCode {

	// Friends
	/** 已经添加了这位朋友 */
	public static final int FRIEND_RELATION_ADDED = 1;
	/** 已经超过好友的最大限制 */
	public static final int FRIEND_MAX_LIMIT = 2;
	/** 不能添加自己为好友 */
	public static final int FRIEND_CAN_NOT_ADD_SELF = 3;
	/** 我或朋友不能为空 */
	public static final int FRIEND_ADD_NULL = 4;
	/** 对方好友超过的最大限制 */
	public static final int FRIEND_TARGET_MAX_LIMIT = 5;
	/** 用户不存在或已被系统封禁 */
	public static final int FRIEND_USER_NOT_EXIST = 6;
	/** 对方从未申请成为你的好友，接受好友申请失败。 */
	public static final int FRIENDINVITE_NO_INVITE = 10;
	/** 已经邀请过对方。 */
	public static final int FRIENDINVITE_INVITED = 11;

	/** 用户创建的组大小超过最大限制 Group的错误代码范围 50 - 75 */
	public static final int GROUP_MAX_LIMIT = 50;
	public static final int GROUP_FAILUE_REMOVE_MEMBER = 51;
	public static final int GROUP_NAME_LENGTH_LIMIT = 52;
	/** 查询参数为空。 */
	public static final int PARAM_NULL_USERID = 53;
	/** 不合法的数据操作 Confirm的错误代码范围 76 - 100 */
	public static final int EXCEPTION_DATA_ILLEGAL = 76;

	// invite
	/** 所指定的邮箱类型有误 */
	public static final int INVITE_TYPE_ERROR = 151;
	/** 用户名或密码错误 */
	public static final int INVITE_Authentication_ERROR = 152;
	/** 无法连接到Ｇｍａｉｌ */
	public static final int INVITE_GMAIL_UNCONNECT = 153;
	/** 无法获得Ｇｍａｉｌ Contact服务 */
	public static final int INVITE_GMAIL_UNSERVICE = 154;
	/** 暂不支持的邀请类型 */
	public static final int INVITE_TYPE_UNSUPPORT = 155;
	/** 暂不支持的邀请类型 */
	public static final int INVITE_CONNECT_ERROR = 156;

	// inbox
	/** 没有发件人 */
	public static final int INBOX_NO_SENDER = 601;
	/** 没有接收人 */
	public static final int INBOX_NO_RECEIVERS = 602;
	/** 标题不能为空 */
	public static final int INBOX_TITLE_NULL = 603;
	/** 接收人格式不对 */
	public static final int INBOX_RECEIVER_FORMAT_ERROR = 604;
	/** 接收人中不允许存在发件人 */
	public static final int INBOX_RECEIVER_CONTAINS_SENDER = 605;
	/** 接收人不存在 **/
	public static final int INBOX_RECEIVER_NOT_EXIST = 606;
	// 公用2000-2500
	/** 格式错误 */
	public static final int EXCEPTION_DATA_FORMAT = 700;

	// status
	/** 标题不能为空 */
	public static final int STATUS_TITLE_NULL = 1001;
	/** 不能为负数或0 */
	public static final int MONEY_NOT_NEGATIVE_OR_ZERO = 1002;
	/** 余额不足 */
	public static final int MONEY_NOT_SAVE_ACCOUNT = 1003;

	// task
	/** 任务不存在 */
	public static final int TASK_NOT_FOUND = 1201;

	public static final int CRON_EXPRESSION_ERROR = 1202;

	// pet
	/** 物品不存在 */
	public static final int GOODS_NOT_FOUND = 1301;
	/** 包裹中的没有这种物品 */
	public static final int PET_GOODS_NOT_FOUND = 1302;
	/** 此物品不能喂养这种类型的动物 */
	public static final int CANNOT_FEED_PET_TYPE = 1303;
	/** 宠物不存在 */
	public static final int PET_NOT_FOUND = 1304;
	/** 宠物体力已满 */
	public static final int PET_POWER_FULL = 1305;
	/** 宠物清洁已满 */
	public static final int PET_SANITA_FULL = 1306;
	/** 宠物健康已满 */
	public static final int PET_HEALTH_FULL = 1307;
	/** 宠物答题站没有这道题目 */
	public static final int PET_QUESTION_NOT_FOUND = 1308;
	/** 宠物答题站没有这道题目 */
	public static final int PET_USER_QUESTION_NOT_FOUND = 1309;
	/** 宠物答题站没有这道题目 */
	public static final int PET_USER_QUESTION_IS_ANSWERED = 1310;

	// privacy
	/** 她已经加你为黑名单 */
	public static final int PRIVACY_ADDFRIEND_BLACKLIST = 1501;
	/** 他只允许好友的好友加他为好友 */
	public static final int PRIVACY_ADDFRIEND_FRIENDOFFRIENDS = 1502;
	/** 他禁止任何人加她为好友 */
	public static final int PRIVACY_ADDFRIEND_FORBID = 1503;
	/** 她已经加你为黑名单，不能发送站内信 */
	public static final int PRIVACY_INBOX_BLACKLIST = 1504;
	/** 他只允许好友的好友及好友发送站内信 */
	public static final int PRIVACY_INBOX__FRIENDOFFRIENDS = 1505;
	/** 他只允许好友给他发站内信 */
	public static final int PRIVACY_INBOX__FRIEND = 1506;
	/** 他禁止任何人给她发送站内信 */
	public static final int PRIVACY_INBOX__FORBID = 1507;

}
