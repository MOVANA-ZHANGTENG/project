package com.deer.wcs.base.web;

import com.deer.wcs.base.model.PositionHandle;
import com.deer.wcs.base.model.PositionStepCriteria;
import com.deer.wcs.base.model.PositionStepDto;
import com.deer.wcs.base.model.PositionStepVo;
import com.deer.wcs.base.service.PositionHandleService;
import com.deer.wcs.base.service.PositionStepService;
import com.deer.wcs.common.annotation.Log;
import com.deer.wcs.common.core.controller.BaseController;
import com.deer.wcs.common.core.domain.Result;
import com.deer.wcs.common.core.page.TableDataInfo;
import com.deer.wcs.common.enums.BusinessType;
import com.deer.wcs.common.utils.DateUtil;
import com.deer.wcs.common.utils.poi.ExcelUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

/**
 * 流程Controller
 *
 * @author deer
 * @date 2024-07-01
 */
@Api("流程")
@RestController
@RequestMapping("/wcs-base/PositionStep")
public class PositionStepController extends BaseController {
    @Autowired
    private PositionStepService positionStepService;
    @Autowired
    private PositionHandleService positionHandleService;

    /**
     * 查询流程列表
     */
    @ApiOperation("查询流程列表")
    @PreAuthorize("@ss.hasPermi('wcs-base:PositionStep:list')")
    @GetMapping("/list")
    public TableDataInfo list(PositionStepCriteria Criteria) {
        startPage();
        List<PositionStepDto> list = positionStepService.findList(Criteria);
        return getDataTable(list);
    }

    /**
     * 导出流程列表
     */
    @ApiOperation("导出流程列表")
    @PreAuthorize("@ss.hasPermi('wcs-base:PositionStep:export')")
    @Log(title = "流程", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, PositionStepCriteria criteria) {
        List<PositionStepDto> list = positionStepService.findList(criteria);
        ExcelUtil<PositionStepDto> util = new ExcelUtil<PositionStepDto>(PositionStepDto.class);
        util.exportExcel(response, list, "流程数据");
    }

    /**
     * 获取流程详细信息
     */
    @ApiOperation("获取流程详细信息")
    @PreAuthorize("@ss.hasPermi('wcs-base:PositionStep:query')")
    @GetMapping(value = "/{id}")
    public Result getInfo(@PathVariable("id") Long id) {
        return success(positionStepService.findById(id));
    }

    /**
     * 新增流程
     */
    @ApiOperation("新增流程")
    @PreAuthorize("@ss.hasPermi('wcs-base:PositionStep:add')")
    @Log(title = "流程", businessType = BusinessType.INSERT)
    @PostMapping
    public Result add(@RequestBody PositionStepVo positionStep) {

        if(positionStep.getCode()==null||positionStep.getCode().equals("")){
            return Result.error("未传入流程编码");
        }
        if(positionStep.getName()==null||positionStep.getName().equals("")){
            return Result.error("未传入流程名称");
        }
        if(positionStep.getWareCode()==null||positionStep.getWareCode().equals("")){
            return Result.error("未传入流程所属的仓库编码");
        }
        if(positionStep.getWareName()==null||positionStep.getWareName().equals("")){
            return Result.error("未传入流程所属的仓库名称");
        }
        int index = 0;
        if (positionStep.getCmdPreList() != null && positionStep.getCmdPreList().size() > 0) {
            for (PositionHandle handle : positionStep.getCmdPreList()) {
                handle.setStepCode(positionStep.getCode());
                handle.setType(1);
                //前端传过来的是handle的id
                handle.setHandleId(handle.getId());
                handle.setId(null);
                handle.setOrderNo(index + 1);
                handle.setCreateTime(DateUtil.getNowDateTimeString());
                handle.setCreateUserId(getUserId());
                handle.setCreateUserName(getUsername());
                handle.setIsDelete(0);
                handle.setVersion(0);
                positionHandleService.save(handle);
                index++;
            }
        }

        index = 0;
        if (positionStep.getCmdList() != null && positionStep.getCmdList().size() > 0) {
            for (PositionHandle handle : positionStep.getCmdList()) {
                handle.setStepCode(positionStep.getCode());
                handle.setType(2);
                //前端传过来的是handle的id
                handle.setHandleId(handle.getId());
                handle.setId(null);
                handle.setOrderNo(index + 1);
                handle.setCreateTime(DateUtil.getNowDateTimeString());
                handle.setCreateUserId(getUserId());
                handle.setCreateUserName(getUsername());
                handle.setIsDelete(0);
                handle.setVersion(0);
                positionHandleService.save(handle);
                index++;
            }
        }

        index = 0;
        if (positionStep.getSuccessPreList() != null && positionStep.getSuccessPreList().size() > 0) {
            for (PositionHandle handle : positionStep.getSuccessPreList()) {
                handle.setStepCode(positionStep.getCode());
                handle.setType(3);
                //前端传过来的是handle的id
                handle.setHandleId(handle.getId());
                handle.setId(null);
                handle.setOrderNo(index + 1);
                handle.setCreateTime(DateUtil.getNowDateTimeString());
                handle.setCreateUserId(getUserId());
                handle.setCreateUserName(getUsername());
                handle.setIsDelete(0);
                handle.setVersion(0);
                positionHandleService.save(handle);
                index++;
            }
        }

        index = 0;
        if (positionStep.getSuccessList() != null && positionStep.getSuccessList().size() > 0) {
            for (PositionHandle handle : positionStep.getSuccessList()) {
                handle.setStepCode(positionStep.getCode());
                handle.setType(4);
                //前端传过来的是handle的id
                handle.setHandleId(handle.getId());
                handle.setId(null);
                handle.setOrderNo(index + 1);
                handle.setCreateTime(DateUtil.getNowDateTimeString());
                handle.setCreateUserId(getUserId());
                handle.setCreateUserName(getUsername());
                handle.setIsDelete(0);
                handle.setVersion(0);
                positionHandleService.save(handle);
                index++;
            }
        }
        positionStepService.save(positionStep);
        return toAjax(true);
    }

    /**
     * 修改流程
     */
    @ApiOperation("修改流程")
    @PreAuthorize("@ss.hasPermi('wcs-base:PositionStep:edit')")
    @Log(title = "流程", businessType = BusinessType.UPDATE)
    @PutMapping
    public Result edit(@RequestBody PositionStepDto positionStep) {
        if(positionStep.getId()==null){
            return Result.error("数据有误");
        }
        if(positionStep.getCode()==null||positionStep.getCode().equals("")){
            return Result.error("未传入流程编码");
        }
        if(positionStep.getName()==null||positionStep.getName().equals("")){
            return Result.error("未传入流程名称");
        }
        if(positionStep.getWareCode()==null||positionStep.getWareCode().equals("")){
            return Result.error("未传入流程所属的仓库编码");
        }
        if(positionStep.getWareName()==null||positionStep.getWareName().equals("")){
            return Result.error("未传入流程所属的仓库名称");
        }

        List<Integer> range = new ArrayList<>();
        range.add(1);
        range.add(2);
        range.add(3);
        range.add(4);

        /*
            删除所有与positionStep有绑定关系的positionHandle
         */
        positionHandleService.deleteByStepCode(positionStep.getCode());

        int index = 0;
        if (positionStep.getCmdPreList() != null && positionStep.getCmdPreList().size() > 0) {
            for (PositionHandle handle : positionStep.getCmdPreList()) {
                handle.setStepCode(positionStep.getCode());
                handle.setType(1);
                //前端传过来的是handle的id
                if(handle.getHandleId()==null){
                    handle.setHandleId(handle.getId());
                    handle.setId(null);
                }
                handle.setOrderNo(index + 1);
                handle.setCreateTime(DateUtil.getNowDateTimeString());
                handle.setCreateUserId(getUserId());
                handle.setCreateUserName(getUsername());
                handle.setIsDelete(0);
                handle.setVersion(0);
                positionHandleService.save(handle);
                index++;
            }
        }

        index = 0;
        if (positionStep.getCmdList() != null && positionStep.getCmdList().size() > 0) {
            for (PositionHandle handle : positionStep.getCmdList()) {
                handle.setStepCode(positionStep.getCode());
                handle.setType(2);
                //前端传过来的是handle的id
                if(handle.getHandleId()==null){
                    handle.setHandleId(handle.getId());
                    handle.setId(null);
                }
                handle.setOrderNo(index + 1);
                handle.setCreateTime(DateUtil.getNowDateTimeString());
                handle.setCreateUserId(getUserId());
                handle.setCreateUserName(getUsername());
                handle.setIsDelete(0);
                handle.setVersion(0);
                positionHandleService.save(handle);
                index++;
            }
        }

        index = 0;
        if (positionStep.getSuccessPreList() != null && positionStep.getSuccessPreList().size() > 0) {
            for (PositionHandle handle : positionStep.getSuccessPreList()) {
                handle.setStepCode(positionStep.getCode());
                handle.setType(3);
                //前端传过来的是handle的id
                if(handle.getHandleId()==null){
                    handle.setHandleId(handle.getId());
                    handle.setId(null);
                }
                handle.setOrderNo(index + 1);
                handle.setCreateTime(DateUtil.getNowDateTimeString());
                handle.setCreateUserId(getUserId());
                handle.setCreateUserName(getUsername());
                handle.setIsDelete(0);
                handle.setVersion(0);
                positionHandleService.save(handle);
                index++;
            }
        }

        index = 0;
        if (positionStep.getSuccessList() != null && positionStep.getSuccessList().size() > 0) {
            for (PositionHandle handle : positionStep.getSuccessList()) {
                handle.setStepCode(positionStep.getCode());
                handle.setType(4);
                //前端传过来的是handle的id
                if(handle.getHandleId()==null){
                    handle.setHandleId(handle.getId());
                    handle.setId(null);
                }
                handle.setOrderNo(index + 1);
                handle.setCreateTime(DateUtil.getNowDateTimeString());
                handle.setCreateUserId(getUserId());
                handle.setCreateUserName(getUsername());
                handle.setIsDelete(0);
                handle.setVersion(0);
                positionHandleService.save(handle);
                index++;
            }
        }
        return toAjax(positionStepService.update(positionStep));
    }

    /**
     * 删除流程
     */
    @ApiOperation("删除流程")
    @PreAuthorize("@ss.hasPermi('wcs-base:PositionStep:remove')")
    @Log(title = "流程", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public Result remove(@PathVariable Long[] ids) {
        return toAjax(positionStepService.deletePositionStepByIds(ids));
    }
}
