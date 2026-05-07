package com.example.gas.api.service.iml;

import cn.hutool.core.map.MapUtil;
import com.example.gas.api.common.PageUtils;
import com.example.gas.api.db.dao.GasUserDao;
import com.example.gas.api.db.pojo.GasUserEntity;
import com.example.gas.api.service.GasUserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class GasUserServiceImpl implements GasUserService {

    @Resource
    private GasUserDao gasUserDao;

    @Override
    public PageUtils searchByPage(Map param) {
        List<HashMap> list = gasUserDao.searchByPage(param);
        long count = gasUserDao.searchCount(param);
        int page = MapUtil.getInt(param, "page");
        int length = MapUtil.getInt(param, "length");
        return new PageUtils(list, count, page, length);
    }

    @Override
    public GasUserEntity searchById(Long id) {
        return gasUserDao.searchById(id);
    }

    @Override
    public HashMap searchByIdHashMap(Long id) {
        return gasUserDao.searchByIdHashMap(id);
    }

    @Override
    public int insert(GasUserEntity entity) {
        return gasUserDao.insert(entity);
    }

    @Override
    public int update(GasUserEntity entity) {
        return gasUserDao.update(entity);
    }

    @Override
    public int deleteById(Long id) {
        return gasUserDao.deleteById(id);
    }

    @Override
    public int deleteByIds(List<Long> ids) {
        return gasUserDao.deleteByIds(ids);
    }

    @Override
    public boolean checkUserNoExists(String userNo, Long id) {
        int count = gasUserDao.checkUserNoExists(userNo, id);
        return count > 0;
    }

    @Override
    public List<HashMap> searchAll() {
        return gasUserDao.searchAll();
    }
}
