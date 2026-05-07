package com.example.gas.api.service.iml;

import com.example.gas.api.db.dao.AreaNodeDao;
import com.example.gas.api.db.pojo.AreaNodeEntity;
import com.example.gas.api.service.AreaNodeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
@Slf4j
public class AreaNodeServiceImpl implements AreaNodeService {

    @Resource
    private AreaNodeDao areaNodeDao;

    @Override
    public List<AreaNodeEntity> searchByParentCode(String parentCode) {
        return areaNodeDao.searchByParentCode(parentCode);
    }

    @Override
    public List<AreaNodeEntity> searchByLevel(Integer level) {
        return areaNodeDao.searchByLevel(level);
    }

    @Override
    public AreaNodeEntity searchByCode(String code) {
        return areaNodeDao.searchByCode(code);
    }

    @Override
    public List<AreaNodeEntity> searchAll() {
        return areaNodeDao.searchAll();
    }
}
