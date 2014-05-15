package com.lenovo.vctl.apps.commons.service;

import java.util.List;

import com.lenovo.vctl.apps.commons.exception.ServiceDaoException;
import com.lenovo.vctl.apps.commons.exception.ServiceException;

public interface DalBaseService<E> {

    public Long saveEntity(E entity) throws ServiceDaoException, ServiceException;

    public boolean updateEntity(E entity) throws ServiceDaoException, ServiceException;

    public boolean removeEntity(Long entityId) throws ServiceDaoException, ServiceException;

    public E getEntity(Long id) throws ServiceDaoException, ServiceException;

    public List<E> getObjectList(List<Long> ids) throws ServiceDaoException, ServiceException;

    public Integer count(String regItem, Object[] params) throws ServiceDaoException, ServiceException;


}