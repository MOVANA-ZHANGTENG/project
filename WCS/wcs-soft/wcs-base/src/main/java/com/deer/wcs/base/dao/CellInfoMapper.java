package com.deer.wcs.base.dao;

import com.deer.wcs.base.model.CellInfo;
import com.deer.wcs.base.model.CellInfoCriteria;
import com.deer.wcs.base.model.CellInfoDto;
import com.deer.wcs.common.core.mapper.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 库位Mapper接口
 * 
 * @author deer
 * @date 2024-04-28
 */
public interface CellInfoMapper  extends Mapper<CellInfo>
{
    /**
     * 查询库位
     *
     * @param id 库位主键
     * @return 库位
     */
    public CellInfo selectCellInfoById(Integer id);
    public CellInfo findFirstInCell(@Param("wareCode") String wareCode);

    /**
     * 查询库位列表
     * 
     * @param cellInfo 库位
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
     * 删除库位
     * 
     * @param id 库位主键
     * @return 结果
     */
    public int deleteCellInfoById(Integer id);

    /**
     * 批量删除库位
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteCellInfoByIds(Long[] ids);

    /**
     * 根据仓库和巷道编码查询库位列表（关联托盘信息）
     * 
     * @param wareCode 仓库编码
     * @param lineCode 巷道编码
     * @return 库位列表（包含托盘状态）
     */
    public List<CellInfoDto> findByLineCodeWithPallet(@Param("wareCode") String wareCode, @Param("lineCode") String lineCode);

    /**
     * 查询3D库存
     *
     * @param wareCode 仓库编码
     * @return 3D库存列表（包含库位、巷道、托盘状态）
     */
    List<CellInfoDto> find3dInventory(@Param("wareCode") String wareCode);

    List<CellInfo> findByType(@Param("wareCode") String wareCode,@Param("type")  Integer type);

    List<CellInfo> findCanAllotCellsAsc(@Param("wareCode") String wareCode,@Param("type") Integer type,@Param("floorZ") Integer floorZ);

    List<CellInfo> findCanAllotCellsDesc(@Param("wareCode") String wareCode,@Param("type") Integer type,@Param("floorZ") Integer floorZ);

    List<CellInfo> findCanStopCells(@Param("wareCode") String wareCode,@Param("type") Integer type,@Param("floorZ") Integer floorZ);

    CellInfo findSxcCellByPriority(@Param("wareCode") String wareCode,@Param("priority") int priority,@Param("x") Integer x,@Param("y") Integer y,@Param("z") Integer z);
}
