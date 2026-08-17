package com.deer.wcs.base.web;

import com.deer.wcs.base.model.*;
import com.deer.wcs.base.service.ItemInfoService;
import com.deer.wcs.common.annotation.Log;
import com.deer.wcs.common.core.controller.BaseController;
import com.deer.wcs.common.core.domain.Result;
import com.deer.wcs.common.core.page.TableDataInfo;
import com.deer.wcs.common.enums.BusinessType;
import com.deer.wcs.common.utils.poi.ExcelUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import tk.mybatis.mapper.entity.Condition;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 物料信息Controller
 *
 * @author deer
 * @date 2024-09-09
 */
@Api("物料信息")
@RestController
@RequestMapping("/wcs-base/ItemInfo")
public class ItemInfoController extends BaseController {
    @Autowired
    private ItemInfoService itemInfoService;

    /**
     * 查询物料信息列表
     */
    @ApiOperation("查询物料信息列表")
    //@PreAuthorize("@ss.hasPermi('wcs-base:info:list')")
    @GetMapping("/list")
    public TableDataInfo list(ItemInfoCriteria Criteria) {
        startPage();
        List<ItemInfoDto> list = itemInfoService.findList(Criteria);
        return getDataTable(list);
    }

    /**
     * 导出物料信息列表
     */
    @ApiOperation("导出物料信息列表")
    //@PreAuthorize("@ss.hasPermi('wcs-base:info:export')")
    @Log(title = "物料信息", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, ItemInfoCriteria criteria) {
        List<ItemInfoDto> list = itemInfoService.findList(criteria);
        ExcelUtil<ItemInfoDto> util = new ExcelUtil<ItemInfoDto>(ItemInfoDto.class);
        util.exportExcel(response, list, "物料信息数据");
    }

    /**
     * 获取物料信息详细信息
     */
    @ApiOperation("获取物料信息详细信息")
    //@PreAuthorize("@ss.hasPermi('wcs-base:info:query')")
    @GetMapping(value = "/{id}")
    public Result getInfo(@PathVariable("id") Long id) {
        return success(itemInfoService.findById(id));
    }

    /**
     * 新增物料信息
     */
    @ApiOperation("新增物料信息")
    //@PreAuthorize("@ss.hasPermi('wcs-base:info:add')")
    @Log(title = "物料信息", businessType = BusinessType.INSERT)
    @PostMapping
    public Result add(@RequestBody ItemInfo itemInfo) {
        if (itemInfo.getItemCode() == null || itemInfo.getItemCode().equals("")) {
            return error("物料信息编码不能为空");
        }
        if (itemInfo.getItemName() == null || itemInfo.getItemName().equals("")) {
            return error("物料信息名称不能为空");
        }
        Condition repeatCode = new Condition(ItemInfo.class);
        repeatCode.createCriteria().andEqualTo("itemCode", itemInfo.getItemCode());
        List<ItemInfo> repeatCodes = itemInfoService.findByCondition(repeatCode);
        if (repeatCodes.size() > 0) {
            return error("物料信息编码重复");
        }

        Condition repeatName = new Condition(ItemInfo.class);
        repeatName.createCriteria().andEqualTo("itemName", itemInfo.getItemName());
        List<ItemInfo> repeatNames = itemInfoService.findByCondition(repeatName);
        if (repeatNames.size() > 0) {
            return error("物料信息名称重复");
        }

        itemInfoService.save(itemInfo);
        return toAjax(true);
    }

    /**
     * 修改物料信息
     */
    @ApiOperation("修改物料信息")
    //@PreAuthorize("@ss.hasPermi('wcs-base:info:edit')")
    @Log(title = "物料信息", businessType = BusinessType.UPDATE)
    @PutMapping
    public Result edit(@RequestBody ItemInfo itemInfo) {
        if (itemInfo.getItemCode() == null || itemInfo.getItemCode().equals("")) {
            return error("物料信息编码不能为空");
        }
        if (itemInfo.getItemName() == null || itemInfo.getItemName().equals("")) {
            return error("物料信息名称不能为空");
        }
        Condition repeatCode = new Condition(ItemInfo.class);
        repeatCode.createCriteria().andEqualTo("itemCode", itemInfo.getItemCode());
        List<ItemInfo> repeatCodes = itemInfoService.findByCondition(repeatCode);
        if (repeatCodes.size() > 0) {
            if (repeatCodes.size() == 1 && repeatCodes.get(0).getId().longValue() != itemInfo.getId().longValue()) {
                return error("物料信息编码重复");
            } else if (repeatCodes.size() > 1) {
                return error("物料信息编码重复");
            }
        }

        Condition repeatName = new Condition(ItemInfo.class);
        repeatName.createCriteria().andEqualTo("itemName", itemInfo.getItemName());
        List<ItemInfo> repeatNames = itemInfoService.findByCondition(repeatName);
        if (repeatNames.size() > 0) {
            if (repeatNames.size() == 1 && repeatNames.get(0).getId().longValue() != itemInfo.getId().longValue()) {
                return error("物料信息名称重复");
            } else if (repeatNames.size() > 1) {
                return error("物料信息名称重复");
            }
        }


        return toAjax(itemInfoService.update(itemInfo));
    }

    /**
     * 删除物料信息
     */
    @ApiOperation("删除物料信息")
    //@PreAuthorize("@ss.hasPermi('wcs-base:info:remove')")
    @Log(title = "物料信息", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public Result remove(@PathVariable Long[] ids) {
        return toAjax(itemInfoService.deleteItemInfoByIds(ids));
    }
    @ApiOperation("托盘信息导入")
    @Log(title = "托盘信息导入", businessType = BusinessType.IMPORT)
    @PostMapping("/importData")
    public Result importData(MultipartFile file, boolean updateSupport) throws Exception
    {
        ExcelUtil<ItemInfo> util = new ExcelUtil<>(ItemInfo.class);
        List<ItemInfo> itemInfos = util.importExcel(file.getInputStream());
        Integer taskCount = itemInfos.size();
        Integer insertCount = 0;
        Integer updateCount = 0;
        Integer failCount = 0;
        if(updateSupport){
            //更新存在的旧数据
            for(ItemInfo itemInfo :itemInfos){
                //非空校验，有字段为空则跳过
                if((itemInfo.getItemCode()==null||"".equals(itemInfo.getItemCode()))
                        ||(itemInfo.getItemName()==null||"".equals(itemInfo.getItemName()))){
                    continue;
                }
                Condition condition = new Condition(ItemInfo.class);
                condition.createCriteria().andEqualTo("itemCode",itemInfo.getItemCode());
                List<ItemInfo> oldInfos = itemInfoService.findByCondition(condition);
                if(oldInfos.size()==0){
                    Result result = add(itemInfo);
                    if((Integer)result.get("code")==200){
                        insertCount++;
                    }
                }else if(oldInfos.size()==1){
                    ItemInfo saveInfo = oldInfos.get(0);
                    saveInfo.setItemName(itemInfo.getItemName());
                    saveInfo.setSpec(itemInfo.getSpec());
                    saveInfo.setModel(itemInfo.getModel());
                    Result result = edit(saveInfo);
                    if((Integer)result.get("code")==200){
                        updateCount++;
                    }
                }
            }
        }else{
            //不更新数据，如果重复直接跳过
            //更新存在的旧数据
            //更新存在的旧数据
            for(ItemInfo itemInfo :itemInfos){
                //非空校验，有字段为空则跳过
                if((itemInfo.getItemCode()==null||"".equals(itemInfo.getItemCode()))
                        ||(itemInfo.getItemName()==null||"".equals(itemInfo.getItemName()))){
                    continue;
                }
                Condition condition = new Condition(ItemInfo.class);
                condition.createCriteria().andEqualTo("itemCode",itemInfo.getItemCode());
                List<ItemInfo> oldInfos = itemInfoService.findByCondition(condition);
                if(oldInfos.size()==0){
                    Result result = add(itemInfo);
                    if((Integer)result.get("code")==200){
                        insertCount++;
                    }
                }
            }
        }
        failCount = taskCount-insertCount-updateCount;
        String msg = "任务总数："+taskCount+"，成功插入数据："+insertCount+"条，成功更新数据："+updateCount+"条，数据处理失败："+failCount+"条。";
        return success(msg);
    }

    @PostMapping("/importTemplate")
    public void importTemplate(HttpServletResponse response)
    {
        ExcelUtil<ItemInfo> util = new ExcelUtil<>(ItemInfo.class);
        util.importTemplateExcel(response,"托盘信息");
    }
}
