package com.deer.wcs.base.web;

import java.util.ArrayList;
import java.util.List;
import javax.servlet.http.HttpServletResponse;

import com.deer.wcs.base.model.*;
import com.deer.wcs.base.service.ItemInfoService;
import com.deer.wcs.base.service.PositionInfoService;
import com.deer.wcs.base.service.ProLineService;
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
import com.deer.wcs.base.service.ProPositionContentService;
import com.deer.wcs.common.utils.poi.ExcelUtil;
import com.deer.wcs.common.core.page.TableDataInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

/**
 * 站台扩展Controller
 * 
 * @author deer
 * @date 2024-11-21
 */
@Api("站台扩展")
@RestController
@RequestMapping("/wcs-base/ProPositionContent")
public class ProPositionContentController extends BaseController
{
    @Autowired
    private ProPositionContentService proPositionContentService;

    @Autowired
    private PositionInfoService positionInfoService;

    /**
     * 查询站台扩展列表
     */
    @ApiOperation("查询站台扩展列表")
    //@PreAuthorize("@ss.hasPermi('wcs-base:ProPositionContent:list')")
    @GetMapping("/list")
    public TableDataInfo list(ProPositionContentCriteria Criteria)
    {
        startPage();
        List<ProPositionContentDto> list = proPositionContentService.findList(Criteria);
        return getDataTable(list);
    }

    @GetMapping("/findByCodeGroup")
    public TableDataInfo findByCodeGroup(Integer codeGroup)
    {
        ProPositionContentCriteria criteria = new ProPositionContentCriteria();
        criteria.setType(1);
        criteria.setCodeGroup(codeGroup);
        startPage();
        List<ProPositionContentDto> list = proPositionContentService.findList(criteria);
        return getDataTable(list);
    }


    @GetMapping("/updateItemByLine")
    public Result updateItemByLine(String lineCode,String itemTypeCode,String itemCode)
    {
        if(lineCode==null || itemTypeCode==null || itemCode==null){
            return error("参数不能为空");
        }
        ProPositionContentCriteria criteria = new ProPositionContentCriteria();
        criteria.setProLineCode(lineCode);
        criteria.setItemTypeCode(itemTypeCode);
        List<ProPositionContentDto> list = proPositionContentService.findList(criteria);
        for (ProPositionContent content:list){
            content.setItemCode(itemCode);
            proPositionContentService.update(content);

        }
        return success();
    }


    @GetMapping("/report")
    public Result report()
    {
        List<ContentReport> list = proPositionContentService.report( );
        return success(list);
    }

    @Autowired
    private ProLineService proLineService;

    public static List<String> WrongPlacement = new ArrayList<>();

    @GetMapping("/report2")
    public Result report2()
    {
        List<ProLine> lines = proLineService.findAll();
        List<ContentReportData> list = new ArrayList<>();
        for (ProLine line:lines){
            ContentReportData data = new ContentReportData();
            data.setLineCode(line.getCode());
            data.setLineName(line.getName());
            data.setWareCode(line.getWareCode());
            data.setDetails(new ArrayList<>());
            List<ContentReport> contentReports = proPositionContentService.report( );
            for (ContentReport report:contentReports){
                if(report.getLineCode()==null){
                    continue;
                }
                if(report.getLineCode().equals(line.getCode())){
                    data.setWareName(report.getWareName());
                    data.getDetails().add(report);
                }
            }
            list.add(data);
        }
        return success(list);
    }


    @GetMapping("/memo")
    public Result memo()
    {
//        if (WrongPlacement == null || WrongPlacement.size()==0){
//            WrongPlacement.add("aaaa");
//            WrongPlacement.add("bbbb");
//            WrongPlacement.add("cccc");
//        }
        if (WrongPlacement != null && WrongPlacement.size()>0){
            return success(WrongPlacement);
        }
        return success();
    }

    /**
     * 导出站台扩展列表
     */
    @ApiOperation("导出站台扩展列表")
    //@PreAuthorize("@ss.hasPermi('wcs-base:ProPositionContent:export')")
    @Log(title = "站台扩展", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, ProPositionContentCriteria criteria)
    {
        List<ProPositionContentDto> list = proPositionContentService.findList(criteria);
        ExcelUtil<ProPositionContentDto> util = new ExcelUtil<ProPositionContentDto>(ProPositionContentDto.class);
        util.exportExcel(response, list, "站台扩展数据");
    }

    /**
     * 获取站台扩展详细信息
     */
    @ApiOperation("获取站台扩展详细信息")
    //@PreAuthorize("@ss.hasPermi('wcs-base:ProPositionContent:query')")
    @GetMapping(value = "/{id}")
    public Result getInfo(@PathVariable("id") Long id)
    {
        return success(proPositionContentService.findById(id));
    }

    @Autowired
    private ItemInfoService itemInfoService;

    /**
     * 新增站台扩展
     */
    @ApiOperation("新增站台扩展")
    //@PreAuthorize("@ss.hasPermi('wcs-base:ProPositionContent:add')")
    @Log(title = "站台扩展", businessType = BusinessType.INSERT)
    @PostMapping
    public Result add(@RequestBody ProPositionContent ProPositionContent)
    {


        proPositionContentService.save(ProPositionContent);
        return toAjax(true);
    }

    /**
     * 修改站台扩展
     */
    @ApiOperation("修改站台扩展")
    //@PreAuthorize("@ss.hasPermi('wcs-base:ProPositionContent:edit')")
    @Log(title = "站台扩展", businessType = BusinessType.UPDATE)
    @PutMapping
    public Result edit(@RequestBody ProPositionContent ProPositionContent)
    {



        return toAjax(proPositionContentService.update(ProPositionContent));
    }

    /**
     * 删除站台扩展
     */
    @ApiOperation("删除站台扩展")
    //@PreAuthorize("@ss.hasPermi('wcs-base:ProPositionContent:remove')")
    @Log(title = "站台扩展", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public Result remove(@PathVariable Long[] ids)
    {
        return toAjax(proPositionContentService.deleteProPositionContentByIds(ids));
    }
}
