package com.deer.wcs.task.web;

import java.util.List;
import javax.servlet.http.HttpServletResponse;

import com.deer.wcs.task.service.TaskInfoHistoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.deer.wcs.common.annotation.Log;
import com.deer.wcs.common.core.controller.BaseController;
import com.deer.wcs.common.core.domain.Result;
import com.deer.wcs.common.enums.BusinessType;
import com.deer.wcs.task.model.SaoMaSuccess;
import com.deer.wcs.task.model.SaoMaSuccessDto;
import com.deer.wcs.task.model.SaoMaSuccessCriteria;
import com.deer.wcs.task.service.SaoMaSuccessService;
import com.deer.wcs.common.utils.poi.ExcelUtil;
import com.deer.wcs.common.core.page.TableDataInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

/**
 * 扫描失败率Controller
 * 
 * @author deer
 * @date 2025-10-15
 */
@Api("扫描失败率")
@RestController
@RequestMapping("/wcs-task/SaoMaSuccess")
public class SaoMaSuccessController extends BaseController
{
    @Autowired
    private SaoMaSuccessService saoMaSuccessService;

    /**
     * 查询扫描失败率列表
     */
    @ApiOperation("查询扫描失败率列表")
    //@PreAuthorize("@ss.hasPermi('wcs-task:SaoMaSuccess:list')")
    @GetMapping("/list")
    public TableDataInfo list(SaoMaSuccessCriteria Criteria)
    {
        startPage();
        List<SaoMaSuccessDto> list = saoMaSuccessService.findList(Criteria);
        return getDataTable(list);
    }

    /**
     * 导出扫描失败率列表
     */
    @ApiOperation("导出扫描失败率列表")
    //@PreAuthorize("@ss.hasPermi('wcs-task:SaoMaSuccess:export')")
    @Log(title = "扫描失败率", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, SaoMaSuccessCriteria criteria)
    {
        List<SaoMaSuccessDto> list = saoMaSuccessService.findList(criteria);
        ExcelUtil<SaoMaSuccessDto> util = new ExcelUtil<SaoMaSuccessDto>(SaoMaSuccessDto.class);
        util.exportExcel(response, list, "扫描失败率数据");
    }

    /**
     * 获取扫描失败率详细信息
     */
    @ApiOperation("获取扫描失败率详细信息")
    //@PreAuthorize("@ss.hasPermi('wcs-task:SaoMaSuccess:query')")
    @GetMapping(value = "/{id}")
    public Result getInfo(@PathVariable("id") Long id)
    {
        return success(saoMaSuccessService.findById(id));
    }

    /**
     * 新增扫描失败率
     */
    @ApiOperation("新增扫描失败率")
    //@PreAuthorize("@ss.hasPermi('wcs-task:SaoMaSuccess:add')")
    @Log(title = "扫描失败率", businessType = BusinessType.INSERT)
    @PostMapping
    public Result add(@RequestBody SaoMaSuccess saoMaSuccess)
    {

        saoMaSuccessService.save(saoMaSuccess);
        return toAjax(true);
    }

    /**
     * 修改扫描失败率
     */
    @ApiOperation("修改扫描失败率")
    //@PreAuthorize("@ss.hasPermi('wcs-task:SaoMaSuccess:edit')")
    @Log(title = "扫描失败率", businessType = BusinessType.UPDATE)
    @PutMapping
    public Result edit(@RequestBody SaoMaSuccess saoMaSuccess)
    {



        return toAjax(saoMaSuccessService.update(saoMaSuccess));
    }

    @Autowired
    private TaskInfoHistoryService taskInfoHistoryService;

    /**
     * 根据时间查询总数
     */
    @GetMapping("allNumber")
    public Result allNumber(SaoMaSuccessDto saoMaSuccess)
    {
        String startDate = saoMaSuccess.getStartDate();
        String endDate = saoMaSuccess.getEndDate();
        ResultNumber resultNumber = new ResultNumber();
        if (startDate ==null || "".equals(startDate) || endDate == null || "".equals(endDate)){
            resultNumber.setSaoMaAll(0);
            resultNumber.setTaskAll(0);
        }else {
            //根据这个时间来查询
            //总任务数量
            Integer byTimeAllTask = taskInfoHistoryService.findByTimeAllTask2(startDate, endDate);
            //总扫码次数
            List<SaoMaSuccess> saoMaSuccessNumber = saoMaSuccessService.findByTypeAndTime2(null, null, startDate, endDate);
            Integer saoMaNumber = 0;
            for (SaoMaSuccess success:saoMaSuccessNumber){
                Integer classNumber = success.getClassNumber();
                saoMaNumber = saoMaNumber+classNumber;
            }
            resultNumber.setTaskAll(byTimeAllTask);
            resultNumber.setSaoMaAll(saoMaNumber);
        }

        return success(resultNumber);
    }

    class ResultNumber{
        private Integer taskAll;
        private Integer saoMaAll;

        public Integer getTaskAll() {
            return taskAll;
        }

        public void setTaskAll(Integer taskAll) {
            this.taskAll = taskAll;
        }

        public Integer getSaoMaAll() {
            return saoMaAll;
        }

        public void setSaoMaAll(Integer saoMaAll) {
            this.saoMaAll = saoMaAll;
        }
    }

    /**
     * 删除扫描失败率
     */
    @ApiOperation("删除扫描失败率")
    //@PreAuthorize("@ss.hasPermi('wcs-task:SaoMaSuccess:remove')")
    @Log(title = "扫描失败率", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public Result remove(@PathVariable Long[] ids)
    {
        return toAjax(saoMaSuccessService.deleteSaoMaSuccessByIds(ids));
    }
}
