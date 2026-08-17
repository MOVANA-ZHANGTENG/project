package com.deer.wcs.base.service;

import com.deer.wcs.base.model.CellInfo;
import com.deer.wcs.base.model.CellInfoCriteria;
import com.deer.wcs.base.model.CellInfoDto;
import com.deer.wcs.common.core.service.Service;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 库位Service接口
 * 
 * @author deer
 * @date 2024-04-28
 */
public interface CellInfoService   extends Service<CellInfo, Long>
{

    CellInfo findByCode (@Param("wareCode") String wareCode,@Param("cellCode")String cellCode);

    List<CellInfo> findByLineCode(String lineCode, String wareCode);

    public CellInfo findFirstInCell(@Param("wareCode") String wareCode);

    CellInfo findFirstInCell(String wareCode,String lineCode);

    /**
     * 查询库位
     *
     * @param id 库位主键
     * @return 库位
     */
    public CellInfo selectCellInfoById(Integer id);

    /**
     * 查询库位列表
     * 
     * @param criteria
     * @return 库位集合
     */
    public List<CellInfoDto> findList(CellInfoCriteria criteria);

    /**
     * 新增库位
     *
     * @param cellInfo 库位
     * @return 结果
     */
    public int insertCellInfo(CellInfo cellInfo);

    /**
     * 修改库位
     *
     * @param cellInfo 库位
     * @return 结果
     */
    public int updateCellInfo(CellInfo cellInfo);

    /**
     * 批量删除库位
     * 
     * @param ids 需要删除的库位主键集合
     * @return 结果
     */
    public int deleteCellInfoByIds(Long[] ids);

    /**
     * 删除库位信息
     * 
     * @param id 库位主键
     * @return 结果
     */
    public int deleteCellInfoById(Integer id);

    /**
     * 添加库位日志记录
     * 检查cellInfo的recordContent字段是否等于当前要插入的日志
     * 如果等于，则不插入cell_record
     * 如果不等于，则更新cellInfo的recordContent字段，并插入一条cell_record
     * 
     * @param cellCode 库位编码
     * @param wareCode 仓库编码
     * @param content 日志内容
     */
    public void addRecord(String cellCode, String wareCode, String content);

    /**
     * 根据仓库和巷道编码查询库位列表（关联托盘信息）
     * 
     * @param wareCode 仓库编码
     * @param lineCode 巷道编码
     * @return 包含库位列表和巷道信息的Map
     */
    public java.util.Map<String, Object> findByLineCodeWithPallet(String wareCode, String lineCode);

    List<CellInfoDto> find3dInventory(String wareCode);

    /**
     * 批量更新库位适用托盘类型
     */
    int batchUpdatePalletType(String wareCode, List<String> cellCodes, String palletType,
                              Long updateUserId, String updateUserName);


    /**
     *  四向车库位相关方法
     *      1. 根据库位类型查询库位
     *      2. 分配入库货位
     *      3. 分配小车停放位置
     */

    /* 根据库位类型查询库位 */
    List<CellInfo> findByType(String wareCode,Integer type);
    /* 为四向车分配入库库位 */
    CellInfo allotCellForSxc(String wareCode,Integer floorZ,boolean isAsc);
    /* 为四向车分配停放位置 */
    CellInfo allotLocForSxc(String wareCode,Integer floorZ);


    /**
     *  通用方法
     */


    /*  查询前置库位 */
    CellInfo findSxcCellByPriority(String wareCode,int priority, Integer x,Integer y, Integer z);

}
