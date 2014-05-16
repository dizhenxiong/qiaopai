package com.lenovo.vctl.apps.service.impl;

import com.lenovo.vctl.apps.constants.DalConstants;
import com.lenovo.vctl.apps.model.Material;
import com.lenovo.vctl.apps.service.MasterialService;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: kangyang1
 * Date: 14-5-15
 * Time: 下午5:13
 * To change this template use File | Settings | File Templates.
 */
@Component("materialservice")
public class MaterialServiceImpl extends DalBaseServiceImpl<Material> implements MasterialService<Material> {
    @Override
    public Class<Material> getEntityClass() {
        return Material.class;
    }

    @Override
    public List<Material> getUserMaterialList(Long userId) throws Exception {
        List<Long> idLs = getIdList(DalConstants.USER_MATERIAL_ID_LIST, new Object[]{userId}, 0, 100000, true);
        if (CollectionUtils.isEmpty(idLs))
            return new ArrayList<Material>();

        return getObjectList(userId, idLs);
    }

    @Override
    public List<Material> getAllMaterialList() throws Exception {
        List<Long> idLs = getIdList(DalConstants.USER_ALLMATERIAL_ID_LIST, new Object[]{1}, 0, 100000, true);
        if (CollectionUtils.isEmpty(idLs))
            return new ArrayList<Material>();

        return getObjectList(idLs);
    }

    @Override
    public List<Material> getNewMaterialList() throws Exception {
        List<Long> idLs = getIdList(DalConstants.USER_MATERIAL_STATUS_ID_LIST, new Object[]{DalConstants.STATSS_NEW}, 0, 100000, true);
        if (CollectionUtils.isEmpty(idLs))
            return new ArrayList<Material>();

        return getObjectList(idLs);

    }

    public List<Material> fullMaterialList(List<Material> materials){
        List<Material> materialList = new ArrayList<Material>();
        for(Material material: materials){
            if(material.getStatus() == DalConstants.STATSS_NEW){
                material.setStatusName("审核中");
            }
            else if(material.getStatus() == DalConstants.STATUS_FAIL){
                material.setStatusName("审核拒绝");
            }
            else if(material.getStatus() == DalConstants.STATUS_SUCCESS){
                material.setStatusName("审核通过");
            }
            materialList.add(material);
        }
        return  materialList;
    }
}
