package com.lenovo.vctl.apps.controller;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * 提供游戏房间的创建,删除,玩家列表等功能
 * 
 * @author xdyl
 * 
 */
@Controller
public class PlayerController {
	private static final Log log = LogFactory.getLog(PlayerController.class);

	/**
	 * 玩家登入
	 * 
	 * @param request
	 * @param response
	 * @param model
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/memory")
	public String index(HttpServletRequest request, HttpServletResponse response, ModelMap model, String name,
			String password) throws Exception {

		return "index";

	}

	/**
	 * 玩家登入
	 * 
	 * @param request
	 * @param response
	 * @param model
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/memory/login")
	public String login(HttpServletRequest request, HttpServletResponse response, ModelMap model, String name,
			String password) throws Exception {

		if ("customer".equals(name)) {
			return "redirect:/memory/list";
		} else {
			return "redirect:/memory/detail";
		}

	}

	/**
	 * 玩家退出网站
	 * 
	 * @param request
	 * @param response
	 * @param model
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/memory/offline")
	public String offline(HttpServletRequest request, HttpServletResponse response, ModelMap model) throws Exception {

		return "redirect:/";
	}

	

	/**
	 * 玩家退出网站
	 * 
	 * @param request
	 * @param response
	 * @param model
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/memory/list")
	public String list(HttpServletRequest request, HttpServletResponse response, ModelMap model) throws Exception {

		return "file/list";
	}
	
	/**
	 * 玩家退出网站
	 * 
	 * @param request
	 * @param response
	 * @param model
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/memory/detail")
	public String detail(HttpServletRequest request, HttpServletResponse response, ModelMap model) throws Exception {

		return "file/detail";
	}
}
