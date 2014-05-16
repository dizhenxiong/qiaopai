package com.lenovo.vctl.apps.controller;

import antlr.StringUtils;
import com.lenovo.vctl.apps.constants.DalConstants;
import com.lenovo.vctl.apps.model.Material;
import com.lenovo.vctl.apps.service.MasterialService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 提供游戏房间的创建,删除,玩家列表等功能
 *
 * @author xdyl
 */
@Controller
public class PlayerController {

    private static final Log log = LogFactory.getLog(PlayerController.class);

    @Resource(name = "materialservice")
    private MasterialService<Material> masterialService;

    public void setMasterialService(MasterialService<Material> masterialService) {
        this.masterialService = masterialService;
    }


    @RequestMapping(value = "/memory")
    public String index(HttpServletRequest request, HttpServletResponse response, ModelMap model, String name,
                        String password) throws Exception {

        return "index";

    }

    /**
     * 登入
     */
    @RequestMapping(value = "/memory/login")
    public String login(HttpServletRequest request, HttpServletResponse response, ModelMap model, String name,
                        String password) throws Exception {

        if (DalConstants.NAME_CUSTOMER.equals(name) && DalConstants.PASSWD_CUSTOMER.equals(password)) {
            return "redirect:/memory/list?id=" + DalConstants.ID_CUSTOMER;
        } else if (DalConstants.NAME_SPEC.equals(name) && DalConstants.PASSWD_SPEC.equals(password)) {
            return "redirect:/memory/list?id=" + DalConstants.ID_SPCE;
        } else {
            return "index";
        }

    }

    /**
     * 退出网站
     */
    @RequestMapping(value = "/memory/offline")
    public String offline(HttpServletRequest request, HttpServletResponse response, ModelMap model) throws Exception {

        return "redirect:/";
    }

    /**
     * 供应商提交资料，创建预案
     */

    @RequestMapping(value = "/memory/create")
    public String create(HttpServletRequest request, HttpServletResponse response, String userId,ModelMap model) throws Exception {
        model.addAttribute("bdis","disable");
        model.addAttribute("iurl","/memory/uploadFile");
        model.addAttribute("userid",userId);
        model.addAttribute("num",1);
        return "file/create";

    }

    /**
     * 查看某个预案的详情
     */
    @RequestMapping(value = "/memory/detail")
    public String detail(HttpServletRequest request, HttpServletResponse response, String userId,Long id, ModelMap model) throws Exception {
        Material material = masterialService.getEntity(id);
        model.put("material", material);
        model.addAttribute("bdis","readonly");
        model.addAttribute("adis","readonly");
        model.addAttribute("userid",userId);
        model.addAttribute("num",2);

        return "file/create";
    }


    /**
     * 准备审核某个预案
     */
    @RequestMapping(value = "/memory/checkdetail")
    public String checkdetail(HttpServletRequest request, HttpServletResponse response, String userId,Long id, ModelMap model) throws Exception {

        Material material = masterialService.getEntity(id);
        model.put("material", material);
        model.addAttribute("adis","disable");
        model.addAttribute("iurl","/memory/check");
        model.addAttribute("userid",userId);
        model.addAttribute("num",3);
        return "file/create";
    }

    @RequestMapping(value = "/memory/check")
    public String check(HttpServletRequest request, HttpServletResponse response, Long mid, String comment,String status,ModelMap model) throws Exception {
        log.info("/memory/checkdetaila : id "+mid);
        Material material = masterialService.getEntity(mid);
        material.setcCommnet(comment);
        if(null != status && status.length()>1){
            material.setStatus(Integer.parseInt(status));
        }
        masterialService.updateEntity(material);
        return "redirect:/memory/list?id=" + DalConstants.ID_SPCE;
    }

    /**
     * 获取所有预案的列表
     */
    @RequestMapping(value = "/memory/list")
    public String list(HttpServletRequest request, HttpServletResponse response, String id, ModelMap model) throws Exception {

        if (DalConstants.ID_CUSTOMER.equalsIgnoreCase(id)) {
            List<Material> materialList = masterialService.getAllMaterialList();
            model.addAttribute("id", id);
            model.addAttribute("materials", materialList);
        } else if (DalConstants.ID_SPCE.equalsIgnoreCase(id)) {
            List<Material> materialList = masterialService.getNewMaterialList();
            model.addAttribute("id", id);
            model.addAttribute("materials", materialList);
        }
        return "file/list";

    }


}
