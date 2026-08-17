package com.deer.wcs.base.service.impl;

import com.deer.wcs.base.dao.CellInfoMapper;
import com.deer.wcs.base.model.*;
import com.deer.wcs.base.service.CellInfoService;
import com.deer.wcs.base.service.LineInfoService;
import com.deer.wcs.base.service.PalletInfoService;
import com.deer.wcs.common.core.redis.RedisCache;
import com.deer.wcs.common.core.service.AbstractService;
import com.deer.wcs.common.enums.CellPriorityEnum;
import com.deer.wcs.common.enums.CellTypeEnum;
import com.deer.wcs.common.exception.ServiceException;
import com.deer.wcs.common.utils.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Condition;

import java.util.*;

/**
 * 库位Service业务层处理
 *
 * @author deer
 * @date 2024-04-28
 */
@Service
@Component("cellInfoService")
public class CellInfoServiceImpl extends AbstractService<CellInfo, Long> implements CellInfoService {
    @Autowired
    private CellInfoMapper cellInfoMapper;

    @Autowired
    private RedisCache redisCache;

    @Autowired
    private LineInfoService lineInfoService;

    @Override
    public void save(CellInfo model) {
        model.setInvenState(0L);
        model.setTaskState(0L);
        model.setDisableState(0L);
        model.setVersion(0);
        model.setIsDelete(0);
        super.save(model);

        redisCache.deleteObject("CellList:" + model.getWareCode() + "-" + model.getLineCode());
    }

    @Override
    public int update(CellInfo model) {
        Condition condition = new Condition(CellInfo.class);
        condition.createCriteria()
                .andEqualTo("id", model.getId())
                .andEqualTo("version", model.getVersion());

        // 将版本号加1
        if (model.getVersion() == null) {
            model.setVersion(1);
        } else {
            model.setVersion(model.getVersion() + 1);
        }

        int count = super.updateByConditionSelective(model, condition);
        if (count == 0) {
            throw new ServiceException("乐观锁冲突，请重试", 500);
        }
        redisCache.deleteObject("CellList:" + model.getWareCode() + "-" + model.getLineCode());
        return super.update(model);
    }

    /**
     * 查询库位
     *
     * @return 库位
     */

    private List<CellInfo> findByLineCode2(String lineCode, String wareCode) {
        Condition condition = new Condition(CellInfo.class);
        condition.createCriteria().andEqualTo("lineCode", lineCode)
                .andEqualTo("wareCode", wareCode);
        List<CellInfo> list = super.findByCondition(condition);
        redisCache.setCacheObject("CellList:" + wareCode + "-" + lineCode, list);
        return list;
    }


    @Override
    public CellInfo findByCode(String wareCode, String cellCode) {
        Condition condition = new Condition(CellInfo.class);
        condition.createCriteria().andEqualTo("code", cellCode)
                .andEqualTo("wareCode", wareCode)
                .andEqualTo("isDelete", 0);


        List<CellInfo> list = super.findByCondition(condition);
        if (list.isEmpty()) {
            return null;
        }
        if (list.size() > 1) {
            throw new RuntimeException("库位" + cellCode + "在" + wareCode + "中存在多个");
        }
        return list.get(0);
    }

    public List<CellInfo> findByLineCode(String lineCode, String wareCode) {
        if (redisCache.hasKey("CellList:" + wareCode + "-" + lineCode)) {
            return redisCache.getCacheObject("CellList:" + wareCode + "-" + lineCode);
        } else {
            return findByLineCode2(lineCode, wareCode);
        }
    }

    @Autowired
    private PalletInfoService palletInfoService;

    @Autowired
    private com.deer.wcs.base.service.CellRecordService cellRecordService;


    /**
     * 这个方法 不是查询第一个可用的空库位
     * 而是查询这个仓库里面所有有货的库位  哪个是最先入进去的
     *
     * @param wareCode
     * @return
     */
    @Override
    public CellInfo findFirstInCell(String wareCode) {
        return cellInfoMapper.findFirstInCell(wareCode);
    }

    @Override
    public CellInfo findFirstInCell(String wareCode, String lineCode) {
        List<CellInfo> list = findByLineCode2(lineCode, wareCode);
        for (CellInfo cellInfo : list) {
            try {
                if (cellInfo.getInvenState() > 0.9) {
                    continue;
                }
                if (cellInfo.getDisableState() > 0.9) {
                    continue;
                }
                if (cellInfo.getTaskState() > 0.9) {
                    continue;
                }
                Condition condition = new Condition(PalletInfo.class);
                condition.createCriteria().andEqualTo("cellCode", cellInfo.getCode())
                        .andEqualTo("wareCode", wareCode);
                List<PalletInfo> palletInfos = palletInfoService.findByCondition(condition);
                if (!palletInfos.isEmpty()) {
                    continue;
                }
                return cellInfo;
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        return null;
    }

    /**
     * 查询库位
     *
     * @param id 库位主键
     * @return 库位
     */
    @Override
    public CellInfo selectCellInfoById(Integer id) {
        return cellInfoMapper.selectCellInfoById(id);
    }

    /**
     * 查询库位列表
     *
     * @param criteria
     * @return 库位
     */
    @Override
    public List<CellInfoDto> findList(CellInfoCriteria criteria) {
        return cellInfoMapper.findList(criteria);
    }

    /**
     * 新增库位
     *
     * @param cellInfo 库位
     * @return 结果
     */
    @Override
    public int insertCellInfo(CellInfo cellInfo) {
        cellInfo.setCreateTime(DateUtil.getNowDateTimeString());
        return cellInfoMapper.insertCellInfo(cellInfo);
    }

    /**
     * 修改库位
     *
     * @param cellInfo 库位
     * @return 结果
     */
    @Override
    public int updateCellInfo(CellInfo cellInfo) {
        cellInfo.setUpdateTime(DateUtil.getNowDateTimeString());
        return cellInfoMapper.updateCellInfo(cellInfo);
    }

    /**
     * 批量删除库位
     *
     * @param ids 需要删除的库位主键
     * @return 结果
     */
    @Override
    public int deleteCellInfoByIds(Long[] ids) {
        return cellInfoMapper.deleteCellInfoByIds(ids);
    }

    /**
     * 删除库位信息
     *
     * @param id 库位主键
     * @return 结果
     */
    @Override
    public int deleteCellInfoById(Integer id) {
        return cellInfoMapper.deleteCellInfoById(id);
    }

    /**
     * 添加库位日志记录
     * 检查cellInfo的recordContent字段是否等于当前要插入的日志
     * 如果等于，则不插入cell_record
     * 如果不等于，则更新cellInfo的recordContent字段，并插入一条cell_record
     *
     * @param cellCode 库位编码
     * @param wareCode 仓库编码
     * @param content  日志内容
     */
    @Override
    public void addRecord(String cellCode, String wareCode, String content) {
        if (content == null || content.trim().isEmpty()) {
            return;
        }

        try {
            // 1. 查询库位信息
            CellInfo cellInfo = findByCode(wareCode, cellCode);
            if (cellInfo == null) {
                return; // 库位不存在，直接返回
            }

            // 2. 检查recordContent字段是否等于当前要插入的日志
            String currentContent = cellInfo.getRecordContent();
            if (content.equals(currentContent)) {
                // 内容相同，不插入cell_record
                return;
            }

            // 3. 内容不同，更新cellInfo的recordContent字段
            cellInfo.setRecordContent(content);
            super.update(cellInfo);

            // 4. 插入一条cell_record
            CellRecord cellRecord = new CellRecord();
            cellRecord.setCellId(cellInfo.getId());
            cellRecord.setCellCode(cellCode);
            cellRecord.setWareCode(wareCode);
            cellRecord.setContent(content);
            cellRecord.setCreateTime(DateUtil.getNowDateTimeString());

            cellRecordService.save(cellRecord);

        } catch (Exception e) {
            // 记录日志异常不应该影响主流程，只记录错误
            e.printStackTrace();
        }
    }

    /**
     * 根据仓库和巷道编码查询库位列表（关联托盘信息）
     *
     * @param wareCode 仓库编码
     * @param lineCode 巷道编码
     * @return 包含库位列表和巷道信息的Map
     */
    @Override
    public Map<String, Object> findByLineCodeWithPallet(String wareCode, String lineCode) {
        Map<String, Object> result = new HashMap<>();

        // 查询库位列表（包含托盘状态）- 返回 List<CellInfoDto>
        List<CellInfoDto> cellList = cellInfoMapper.findByLineCodeWithPallet(wareCode, lineCode);

        // 查询巷道信息（通过仓库编码和巷道编码精确查询）
        Condition condition = new Condition(LineInfo.class);
        condition.createCriteria()
                .andEqualTo("wareCode", wareCode)
                .andEqualTo("code", lineCode);
        List<LineInfo> lineInfos = lineInfoService.findByCondition(condition);
        LineInfo lineInfo = lineInfos.isEmpty() ? null : lineInfos.get(0);

        result.put("list", cellList);
        result.put("lineInfo", lineInfo);

        return result;
    }


    @Override
    public List<CellInfoDto> find3dInventory(String wareCode) {
        return cellInfoMapper.find3dInventory(wareCode);
    }

    @Override
    public int batchUpdatePalletType(String wareCode, List<String> cellCodes, String palletType,
                                     Long updateUserId, String updateUserName) {
        if (wareCode == null || wareCode.trim().isEmpty() || cellCodes == null || cellCodes.isEmpty()
                || palletType == null || palletType.trim().isEmpty()) {
            return 0;
        }
        String now = DateUtil.getNowDateTimeString();
        int count = 0;
        for (String cellCode : cellCodes) {
            if (cellCode == null || cellCode.trim().isEmpty()) {
                continue;
            }
            CellInfo cellInfo = findByCode(wareCode, cellCode.trim());
            if (cellInfo == null) {
                continue;
            }
            cellInfo.setPalletType(palletType);
            cellInfo.setUpdateTime(now);
            cellInfo.setUpdateUserId(updateUserId);
            cellInfo.setUpdateUserName(updateUserName);
            if (update(cellInfo) > 0) {
                count++;
            }
        }
        return count;
    }


    @Override
    public List<CellInfo> findByType(String wareCode, Integer type) {
        return cellInfoMapper.findByType(wareCode,type);
    }

    @Override
    public CellInfo findSxcCellByPriority(String wareCode,int priority, Integer x, Integer y, Integer z) {
        return cellInfoMapper.findSxcCellByPriority(wareCode,priority,x,y,z);
    }

    /**
     *  四向车入库分配库位
     * @param wareCode 仓库编码
     * @param floorZ 层数
     * @param isAsc x正序/x倒序
     * @return
     */
    @Override
    public CellInfo allotCellForSxc(String wareCode,Integer floorZ,boolean isAsc) {
        // 查询所有可入库的库位（普通库位，无货，无任务，未禁用，未删除，无异常）
        List<CellInfo> canAllotCells = null;
        if(isAsc){
            canAllotCells = cellInfoMapper.findCanAllotCellsAsc(wareCode, CellTypeEnum.NORMAL.getCode(),floorZ);
        }else{
            canAllotCells = cellInfoMapper.findCanAllotCellsDesc(wareCode, CellTypeEnum.NORMAL.getCode(),floorZ);
        }

        // 每一层必须保留两个位置，用于移货
        if(floorZ!=null){
            if(canAllotCells.size()<2){
                return null;
            }
        }

        // 备选库位
        List<CellInfo> remainCells = new ArrayList<>();

        for(CellInfo cellInfo:canAllotCells){
            if (cellInfo.getPriority() == CellPriorityEnum.FIRST_PRIORITY.getCode()) {
                // 1伸位检测2伸位是否有任务
                CellInfo afterCell = findSxcCellByPriority(
                        cellInfo.getWareCode(),
                        CellPriorityEnum.SECOND_PRIORITY.getCode(),
                        cellInfo.getX(),
                        cellInfo.getY(),
                        cellInfo.getZ());

                if (afterCell == null) {
                    return cellInfo;
                }

                if (afterCell.getInvenState() == 0 && afterCell.getTaskState() == 0) {
                    return afterCell;
                }

                if (afterCell.getTaskState() > 0) {
                    continue;
                }

                if (afterCell.getInvenState() == 1) {
                    remainCells.add(cellInfo);
                }
            } else {
                // 2伸位检测1伸位是否有任务,有货,禁用
                CellInfo preCell = findSxcCellByPriority(
                        cellInfo.getWareCode(),
                        CellPriorityEnum.FIRST_PRIORITY.getCode(),
                        cellInfo.getX(),
                        cellInfo.getY(),
                        cellInfo.getZ());

                if (preCell == null) {
                    return cellInfo;
                }

                if (preCell.getTaskState() > 0) {
                    continue;
                }

                // 前置货位有货，要入库需要移动
                if (preCell.getInvenState() == 1) {
                    continue;
                } else {
                    return cellInfo;
                }
            }
            return null;
        }

        if (remainCells.size() > 2) {
            CellInfo cell = remainCells.get(0);
            return cell;
        }
        return null;
    }


    /*
        为四向车分配停放位置
     */
    @Override
    public CellInfo allotLocForSxc(String wareCode,Integer floorZ) {
        // 查询所有可停放位置（普通库位，无货，无任务，未禁用，未删除，无异常）
        List<CellInfo> canAllotCells = cellInfoMapper.findCanStopCells(wareCode, CellTypeEnum.NORMAL.getCode(), floorZ);

        // 备选库位
        List<CellInfo> remainCells = new ArrayList<>();

        for (CellInfo cellInfo : canAllotCells) {
            if (cellInfo.getPriority() == CellPriorityEnum.FIRST_PRIORITY.getCode()) {
                // 1伸位检测2伸位是否有任务
                CellInfo afterCell = findSxcCellByPriority(
                        cellInfo.getWareCode(),
                        CellPriorityEnum.SECOND_PRIORITY.getCode(),
                        cellInfo.getX(),
                        cellInfo.getY(),
                        cellInfo.getZ());

                if (afterCell == null) {
                    return cellInfo;
                }

                if (afterCell.getInvenState() == 0 && afterCell.getTaskState() == 0) {
                    return afterCell;
                }

                if (afterCell.getTaskState() > 0) {
                    continue;
                }

                if (afterCell.getInvenState() == 1) {
                    remainCells.add(cellInfo);
                }
            } else {
                // 2伸位检测1伸位是否有任务,有货,禁用
                CellInfo preCell = findSxcCellByPriority(
                        cellInfo.getWareCode(),
                        CellPriorityEnum.FIRST_PRIORITY.getCode(),
                        cellInfo.getX(),
                        cellInfo.getY(),
                        cellInfo.getZ());

                if (preCell == null) {
                    return cellInfo;
                }

                if (preCell.getTaskState() > 0) {
                    continue;
                }

                // 前置货位有货，要入库需要移动
                if (preCell.getInvenState() == 1) {
                    continue;
                } else {
                    return cellInfo;
                }
            }

            return null;
        }

        if (remainCells.size() > 2) {
            CellInfo cell = remainCells.get(0);
            return cell;
        }
        return null;
    }


}
