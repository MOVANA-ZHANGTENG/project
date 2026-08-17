package com.deer.wcs.base.web;

import com.deer.wcs.base.model.*;
import com.deer.wcs.base.service.WarnMsgService;
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
 * 报警代码Controller
 * 
 * @author deer
 * @date 2025-09-24
 */
@Api("报警代码")
@RestController
@RequestMapping("/wcs-base/WarnMsg")
public class WarnMsgController extends BaseController
{
    @Autowired
    private WarnMsgService warnMsgService;

    /**
     * 查询报警代码列表
     */
    @ApiOperation("查询报警代码列表")
    //@PreAuthorize("@ss.hasPermi('wcs-base:WarnMsg:list')")
    @GetMapping("/list")
    public TableDataInfo list(WarnMsgCriteria Criteria)
    {
        startPage();
        List<WarnMsgDto> list = warnMsgService.findList(Criteria);
        return getDataTable(list);
    }

    /**
     *  下载模板
     */
    @ApiOperation("下载模板")
    @PostMapping("/importTemplate")
    public void importTemplate(HttpServletResponse response)
    {
        ExcelUtil<WarnMsg> util = new ExcelUtil<>(WarnMsg.class);
        util.importTemplateExcel(response,"报警代码");
    }

    /**
     *  导入
     */
    @ApiOperation("报警代码导入")
    @PostMapping("/importData")
    public Result importData(MultipartFile file, boolean updateSupport, String deviceType) throws Exception
    {
        ExcelUtil<WarnMsg> util = new ExcelUtil<>(WarnMsg.class);
        List<WarnMsg> warnMsgs = util.importExcel(file.getInputStream());
        Integer taskCount = warnMsgs.size();
        Integer insertCount = 0;
        Integer updateCount = 0;
        Integer failCount = 0;
        if(updateSupport){
            //更新存在的旧数据
            for(WarnMsg warnMsg :warnMsgs){
                //非空校验，有字段为空则跳过
                if((warnMsg.getCode()==null||"".equals(warnMsg.getCode()))
                        ||(warnMsg.getWarnMsg()==null||"".equals(warnMsg.getWarnMsg()))){
                    continue;
                }
                Condition condition = new Condition(WarnMsg.class);
                condition.createCriteria().andEqualTo("code",warnMsg.getCode())
                        .andEqualTo("type",deviceType);
                List<WarnMsg> oldInfos = warnMsgService.findByCondition(condition);
                if(oldInfos.size()==0){
                    warnMsg.setType(deviceType);
                    Result result = add(warnMsg);
                    if((Integer)result.get("code")==200){
                        insertCount++;
                    }
                }else if(oldInfos.size()==1){
                    WarnMsg saveInfo = oldInfos.get(0);
                    saveInfo.setWarnMsg(warnMsg.getWarnMsg());
                    warnMsg.setType(deviceType);

                    Result result = edit(saveInfo);
                    if((Integer)result.get("code")==200){
                        updateCount++;
                    }
                }
            }
        }else{
            //不更新数据，如果重复直接跳过
            for(WarnMsg warnMsg :warnMsgs){
                //非空校验，有字段为空则跳过
                if((warnMsg.getCode()==null||"".equals(warnMsg.getCode()))
                        ||(warnMsg.getWarnMsg()==null||"".equals(warnMsg.getWarnMsg()))){
                    continue;
                }
                Condition condition = new Condition(WarnMsg.class);
                condition.createCriteria().andEqualTo("code",warnMsg.getCode())
                        .andEqualTo("type",deviceType);
                List<WarnMsg> oldInfos = warnMsgService.findByCondition(condition);
                if(oldInfos.size()==0){
                    warnMsg.setType(deviceType);
                    Result result = add(warnMsg);
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

    /**
     * 导出报警代码列表
     */
    @ApiOperation("导出报警代码列表")
    //@PreAuthorize("@ss.hasPermi('wcs-base:WarnMsg:export')")
    @Log(title = "报警代码", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, WarnMsgCriteria criteria)
    {
        List<WarnMsgDto> list = warnMsgService.findList(criteria);
        ExcelUtil<WarnMsgDto> util = new ExcelUtil<WarnMsgDto>(WarnMsgDto.class);
        util.exportExcel(response, list, "报警代码数据");
    }

    /**
     * 获取报警代码详细信息
     */
    @ApiOperation("获取报警代码详细信息")
    //@PreAuthorize("@ss.hasPermi('wcs-base:WarnMsg:query')")
    @GetMapping(value = "/{id}")
    public Result getInfo(@PathVariable("id") Long id)
    {
        return success(warnMsgService.findById(id));
    }

    /**
     * 新增报警代码
     */
    @ApiOperation("新增报警代码")
    //@PreAuthorize("@ss.hasPermi('wcs-base:WarnMsg:add')")
    @Log(title = "报警代码", businessType = BusinessType.INSERT)
    @PostMapping
    public Result add(@RequestBody WarnMsg warnMsg)
    {
        if(warnMsg.getId()!=null){
            warnMsg.setId(null);
        }
        warnMsgService.save(warnMsg);
        return toAjax(true);
    }

    /**
     * 修改报警代码
     */
    @ApiOperation("修改报警代码")
    //@PreAuthorize("@ss.hasPermi('wcs-base:WarnMsg:edit')")
    @Log(title = "报警代码", businessType = BusinessType.UPDATE)
    @PutMapping
    public Result edit(@RequestBody WarnMsg warnMsg)
    {



        return toAjax(warnMsgService.update(warnMsg));
    }

    /**
     * 删除报警代码
     */
    @ApiOperation("删除报警代码")
    //@PreAuthorize("@ss.hasPermi('wcs-base:WarnMsg:remove')")
    @Log(title = "报警代码", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public Result remove(@PathVariable Long[] ids)
    {
        return toAjax(warnMsgService.deleteWarnMsgByIds(ids));
    }
}
