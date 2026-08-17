package com.deer.wcs.task.handle.wms;


import com.alibaba.fastjson2.JSON;
import com.deer.wcs.base.model.ProPositionContent;
import com.deer.wcs.task.model.JobInfo;
import com.deer.wcs.task.model.TaskInfo;
import com.deer.wcs.task.service.JobInfoService;
import com.deer.wcs.task.service.TaskInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Component("WmsJobHandle")
public class WmsJobHandle {

    @Value("${wms.baseUrl}")
    private String wmsBaseUrl;

    @Autowired
    private TaskInfoService taskInfoService;

    @Autowired
    private JobInfoService jobInfoService;

    public Boolean taskFinish(JobInfo jobInfo){
        TaskInfo taskInfo = taskInfoService.findById(jobInfo.getTaskId());
        String wmsTaskNo = taskInfo.getWmsTaskNo();

        String url = wmsBaseUrl+"/fromWcs/task";
        WmsResult result;
        try {
            Map<String,Object> map = new HashMap<>();
            map.put("taskNo",wmsTaskNo);
            map.put("status",2);
            map.put("memo","任务完成");
            map.put("cellCode",jobInfo.getToCellCode());
            String json=JSON.toJSONString(map );
            String resultStr = client(url,HttpMethod.POST,json);
            result = JSON.parseObject(resultStr, WmsResult.class);

        }catch (Exception ex){
            result = new WmsResult();
            result.setCode(500);
            result.setMsg(ex.getMessage());
        }
        if (result.getCode() == 200) {
            return true;
        }else {
            jobInfoService.updateMemo(jobInfo,"发送WMS任务状态时，WMS返回："+result.getMsg());
            return false;
        }
    }

    /**
     * WMS中托盘位置变更（不经过WMS任务）
     * @param jobInfo
     * @return
     */
    public Boolean inCellRecord(JobInfo jobInfo){
        TaskInfo taskInfo = taskInfoService.findById(jobInfo.getTaskId());
        String wmsTaskNo = taskInfo.getWmsTaskNo();

        String url = wmsBaseUrl+"/fromWcs/inCellRecord";
        WmsResult result;
        try {
            Map<String,Object> map = new HashMap<>();
            map.put("wareCode",taskInfo.getWareCode());
            map.put("palletCode",taskInfo.getPalletCode());
            map.put("cellCode",jobInfo.getToCellCode());
            String json=JSON.toJSONString(map );
            String resultStr = client(url,HttpMethod.POST,json);
            result = JSON.parseObject(resultStr, WmsResult.class);

        }catch (Exception ex){
            result = new WmsResult();
            result.setCode(500);
            result.setMsg(ex.getMessage());
        }
        if (result.getCode() == 200) {
            return true;
        }else {
            jobInfoService.updateMemo(jobInfo,"发送WMS任务状态时，WMS返回："+result.getMsg());
            return false;
        }
    }

    public Boolean taskState(JobInfo jobInfo){
        TaskInfo taskInfo = taskInfoService.findById(jobInfo.getTaskId());
        String wmsTaskNo = taskInfo.getWmsTaskNo();

        String url = wmsBaseUrl+"/fromWcs/task";
        WmsResult result;
        try {
            Map<String,Object> map = new HashMap<>();
            map.put("taskNo",wmsTaskNo);
            map.put("status",1);
            map.put("memo","位置变更");
            map.put("cellCode",jobInfo.getToCellCode());
            String json=JSON.toJSONString(map );
            String resultStr = client(url,HttpMethod.POST,json);
            result = JSON.parseObject(resultStr, WmsResult.class);

        }catch (Exception ex){
            result = new WmsResult();
            result.setCode(500);
            result.setMsg(ex.getMessage());
        }
        if (result.getCode() == 200) {
            return true;
        }else {
            jobInfoService.updateMemo(jobInfo,"发送WMS任务状态时，WMS返回："+result.getMsg());
            return false;
        }
    }

    private class TaskStatusToWms{
        private String taskNo;

        //0-任务开始执行 1-到达某个位置  2-任务结束  -1任务异常 -2任务删除
        private Integer status;
        private String cellCode;
    }

    public static String client(String url, HttpMethod method, String json) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> requestEntity = new HttpEntity<>(json, headers);
        RestTemplate client = new RestTemplate();
        ResponseEntity<String> response = client.exchange(url, method, requestEntity, String.class);
        if (response.getStatusCode() == HttpStatus.OK){
            return response.getBody();
        }
        return null;
    }


    private class WmsResult{
        Integer code;
        String msg;
        String data;

        public Integer getCode() {
            return code;
        }

        public void setCode(Integer code) {
            this.code = code;
        }

        public String getMsg() {
            return msg;
        }

        public void setMsg(String msg) {
            this.msg = msg;
        }

        public String getData() {
            return data;
        }

        public void setData(String data) {
            this.data = data;
        }
    }
}
