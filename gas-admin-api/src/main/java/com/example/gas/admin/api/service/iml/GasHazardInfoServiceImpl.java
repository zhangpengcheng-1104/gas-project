package com.example.gas.api.service.iml;

import cn.hutool.core.map.MapUtil;
import com.example.gas.api.common.PageUtils;
import com.example.gas.api.db.dao.GasHazardInfoDao;
import com.example.gas.api.db.pojo.GasHazardInfoEntity;
import com.example.gas.api.service.GasHazardInfoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class GasHazardInfoServiceImpl implements GasHazardInfoService {

    @Resource
    private GasHazardInfoDao gasHazardInfoDao;

    @Override
    public PageUtils searchByPage(Map param) {
        List<HashMap> list = null;
        long count = gasHazardInfoDao.searchCount(param);
        if (count > 0) {
            list = gasHazardInfoDao.searchByPage(param);
        } else {
            list = new ArrayList<>();
        }
        int page = MapUtil.getInt(param, "page");
        int length = MapUtil.getInt(param, "length");
        PageUtils pageUtils = new PageUtils(list, count, page, length);
        return pageUtils;
    }

    @Override
    public GasHazardInfoEntity searchById(Integer id) {
        return gasHazardInfoDao.searchById(id);
    }

    @Override
    public HashMap searchByIdHashMap(Integer id) {
        return gasHazardInfoDao.searchByIdHashMap(id);
    }

    @Override
    public int insert(GasHazardInfoEntity entity) {
        return gasHazardInfoDao.insert(entity);
    }

    @Override
    public int update(GasHazardInfoEntity entity) {
        return gasHazardInfoDao.update(entity);
    }

    @Override
    public int deleteById(Integer id) {
        return gasHazardInfoDao.deleteById(id);
    }

    @Override
    public int deleteByIds(List<Integer> ids) {
        if (ids == null || ids.isEmpty()) {
            return 0;
        }
        Map<String, Object> param = new HashMap<>();
        param.put("ids", ids);
        return gasHazardInfoDao.deleteByIds(param);
    }
}
