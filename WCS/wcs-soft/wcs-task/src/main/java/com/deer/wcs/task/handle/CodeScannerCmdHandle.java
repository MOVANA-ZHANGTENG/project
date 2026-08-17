package com.deer.wcs.task.handle;


import com.deer.wcs.system.service.BillRecordService;
import com.deer.wcs.task.model.CodeScanner.CodeScannerInfo;
import com.deer.wcs.task.service.CodeScannerInfoService;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

//执行函数
@Component("CodeScannerCmdHandle")
public class CodeScannerCmdHandle {

    @Resource
    private CodeScannerInfoService smqInfoService;

    @Resource
    private BillRecordService billRecordService;

    public Boolean move(CodeScannerInfo codeScannerInfo){
        System.out.println("扫描器的执行器，移动");
        billRecordService.createTaskRecord(Long.valueOf(codeScannerInfo.getId()),"扫描器的执行器，移动");
        return true;
    }
}
