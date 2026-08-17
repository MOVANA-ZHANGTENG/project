package com.deer.wcs.task.handle.han_tai;


import com.alibaba.fastjson2.JSON;
import com.deer.wcs.common.utils.DateUtil;
import com.deer.wcs.system.service.AutoService;
import com.deer.wcs.task.handle.wms.WmsJobHandle;
import com.deer.wcs.task.model.JobInfo;
import com.deer.wcs.task.model.TaskInfo;
import com.deer.wcs.task.service.JobInfoService;
import com.deer.wcs.task.service.TaskInfoService;
import com.deer.wcs.task.task.LgNaTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component("HtSsxHandle")
public class HtSsxHandle {

    @Value("${isTest}")
    private Boolean isTest;



    private static final Logger log = LoggerFactory.getLogger(HtSsxHandle.class);

    @Autowired
    HtSsxDevice htSsxDevice;

    @Autowired
    private TaskInfoService taskInfoService;


    /**
     * 遍历所有的设备，申请，错误
     * 12、6 是入库申请  需要在这里处理
     *
     * 34、25是出库申请  在其他地方处理
     *
     *
     */
    //@Scheduled(cron = "0/1 * * * * ?")
    public void autoRun() {
        if(isTest){
            return;
        }
        //如果站台有申请，则执行
        Integer deviceCodes[] = {6,12};

        for (Integer deviceCode : deviceCodes) {
            Integer address = htSsxDevice.deviceMap.get(deviceCode);
            //判断是否有申请
            Short apply = htSsxDevice.apply(address);
            if(apply==1){
                //判断是否有货
                Short hasPallet = htSsxDevice.hasPallet(address);
                String palletCode = htSsxDevice.palletCode(address);
                Float weight = htSsxDevice.weight(address);
                Short height = htSsxDevice.height(address);

                if(hasPallet==0){
                    log.info("申请的设备没有托盘");
                    return;
                }
                if(palletCode==null|| palletCode.trim().isEmpty()){
                    log.info("申请的设备没有托盘号");
                    return;
                }
                if(palletCode!=null && palletCode.trim().length()<4){
                    log.info("申请的设备托盘号不全");
                    return;
                }
                palletCode = palletCode.trim();
                TaskInfo taskInfo = taskInfoService.getTaskInfoByPalletCode(palletCode);
                if(taskInfo!=null && taskInfo.getType().equals("22")){
                    log.info("托盘号的入库任务已经存在");
                    return;
                }

                //保存入库任务 在入库任务里给WMS发送库位申请
                taskInfo = new TaskInfo();
                taskInfo.setType("22");
                taskInfo.setFromCellCode(deviceCode.toString());
                taskInfo.setPalletCode(palletCode);
                taskInfo.setCreateTime(DateUtil.getNowDateTimeString());
                taskInfo.setState(0);
                taskInfo.setWareCode("hq");
                taskInfo.setWareName("瀚氢");
                taskInfo.setPalletWeight(Double.valueOf(weight));
                taskInfo.setPalletHeight(Double.valueOf(height));
                taskInfoService.save(taskInfo);
            }
        }

        //如果站台无托盘，则清除亮灯信号
        Integer deviceCodes2[] = {5,11};
        for (Integer deviceCode : deviceCodes2) {
            Integer address = htSsxDevice.deviceMap.get(deviceCode);
            //判断是否有托盘
            Short hasPallet = htSsxDevice.hasPallet(address);
            if(hasPallet==0){
                htSsxDevice.deng0All(deviceCode);
               // jobInfoService.updatePalletTask(deviceCode.toString(),"");
                updatePalletTask(deviceCode.toString(),"");
            }else {
                Integer taskNo =  htSsxDevice.taskNo(address);
                if(taskNo!=null){
                //    jobInfoService.updatePalletTask(deviceCode.toString(),taskNo.toString());
                    updatePalletTask(deviceCode.toString(),taskNo.toString());
                }
            }
        }
    }

    @Value("${wms.baseUrl}")
    private String wmsBaseUrl;

    public Boolean updatePalletTask(String key,String value){
        String url = wmsBaseUrl+"/fromWcs/hqPalletTask";
        try {
            String json= "{\n" +
                    "    \"key\":\""+key+"\"\n" +
                    "    \"value\":\""+value+"\"\n" +
                    "}\n" ;
            String resultStr = client(url, HttpMethod.POST,json);
        }catch (Exception ex){

        }
        return true;

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



    @Autowired
    private JobInfoService jobInfoService;


    @Autowired
    private AutoService autoService;
    /**
     * 给输送线发送任务
     * @param jobInfo
     * @return
     */
    public Boolean sandTask(JobInfo jobInfo){
        if(isTest){
            return true;
        }

        String taskNo = null;
        String palletCode = jobInfo.getPalletCode();
        String[] aaa = palletCode.split("\\|");

        if(aaa.length==2){
            taskNo=aaa[1];
        }else {
            taskNo= autoService.getHtCrnTaskNo();
        }

        String fromCellCode = jobInfo.getFromCellCode();
        String toCellCode = jobInfo.getToCellCode();
        jobInfo.setTaskNo(taskNo);
        jobInfoService.update(jobInfo);

        String msg = htSsxDevice.sendTask(Integer.parseInt(fromCellCode),Integer.parseInt(taskNo),Short.parseShort(toCellCode));

        if(msg==null){
            return true;
        }else {
            jobInfoService.updateMemo(jobInfo,msg);
            return false;
        }
    }
    public Boolean canSandTask(JobInfo jobInfo){
        if(isTest){
            return true;
        }
        TaskInfo taskInfo = taskInfoService.findById(jobInfo.getTaskId());
        String palletCode = taskInfo.getPalletCode();
        int device = Integer.parseInt(jobInfo.getFromCellCode());
        Integer address = htSsxDevice.deviceMap.get(device);
        Short plcHasPallet = htSsxDevice.hasPallet(address);
        Short apply = htSsxDevice.apply(address);
        if(plcHasPallet==0){
            jobInfoService.updateMemo(jobInfo,jobInfo.getFromCellCode()+"无托盘");
            return false;
        }
        if(apply==0){
            jobInfoService.updateMemo(jobInfo,jobInfo.getFromCellCode()+"无申请");
            return false;
        }
        if(apply==1){
            jobInfoService.updateMemo(jobInfo,jobInfo.getFromCellCode()+"允许发送任务");
            return true;
        }
        return false;

    }

    public Boolean clearTaskData(JobInfo jobInfo){
        if(isTest){
            return true;
        }
        TaskInfo taskInfo = taskInfoService.findById(jobInfo.getTaskId());
        int device = Integer.parseInt(jobInfo.getFromCellCode());
        Integer address = htSsxDevice.deviceMap.get(device);
        Short apply = htSsxDevice.apply(address);
        if(apply==1){
            jobInfoService.updateMemo(jobInfo,jobInfo.getFromCellCode()+"申请信号未清除");
            return false;
        }
        htSsxDevice.clearTaskData(device);
        jobInfoService.updateMemo(jobInfo,jobInfo.getFromCellCode()+"清除任务信号");

        return true;
    }
    public Boolean canFinish(JobInfo jobInfo){
        if(isTest){
            return true;
        }

        if(jobInfo.getToCellCode().equals("5") || jobInfo.getToCellCode().equals("11")){
            String cmdTime = jobInfo.getCmdTime();
            long between = DateUtil.getTimeDifferenceInSeconds( cmdTime);
            if(between>30){
                return true;
            }
        }


        TaskInfo taskInfo = taskInfoService.findById(jobInfo.getTaskId());
        String palletCode = taskInfo.getPalletCode();
        int device = Integer.parseInt(jobInfo.getToCellCode());
        Integer address = htSsxDevice.deviceMap.get(device);
        String plcPalletCode = htSsxDevice.palletCode(address);
        Short plcHasPallet = htSsxDevice.hasPallet(address);
        Integer taskNo =  htSsxDevice.taskNo(address);
        if(plcHasPallet==0){
            jobInfoService.updateMemo(jobInfo,jobInfo.getToCellCode()+"无托盘");
            return false;
        }
        if(taskNo==null || taskNo==0){
            jobInfoService.updateMemo(jobInfo,jobInfo.getToCellCode()+"未获取到任务号");
            return false;
        }
        Integer jobTaskNo = Integer.parseInt(jobInfo.getTaskNo());
        if(! taskNo.equals(jobTaskNo)){
            jobInfoService.updateMemo(jobInfo,"终点位置被阻挡："+jobInfo.getToCellCode()+"【"+taskNo+"】不是任务号："+jobInfo.getTaskNo());
            return false;
        }
        Short apply = htSsxDevice.apply(address);
        if(apply==0){
            jobInfoService.updateMemo(jobInfo,jobInfo.getToCellCode()+"无申请信号");
            return false;
        }
        jobInfoService.updateMemo(jobInfo,jobInfo.getFromCellCode()+"-"+jobInfo.getToCellCode()+"任务完成");

        return true;
    }

    /**
     * 亮灯显示拣选
     * @param jobInfo
     * @return
     */
    public Boolean finish(JobInfo jobInfo){
        if(isTest){
            return true;
        }
        TaskInfo taskInfo = taskInfoService.findById(jobInfo.getTaskId());
        String palletCode = taskInfo.getPalletCode();
        int device = Integer.parseInt(jobInfo.getToCellCode());
        Integer address = htSsxDevice.deviceMap.get(device);
        try {
            Integer palletCodeInt = Integer.parseInt(palletCode.substring(2).trim());
            Integer deng = palletCodeInt%4;
            htSsxDevice.deng1(device,deng);
        }catch (Exception ex){
           ex.printStackTrace();
        }

        return true;
    }

    public static void main(String[] args) {
        Integer aaa =  Integer.parseInt("hq0000123".substring(2));
        System.out.println(aaa);
    }


}
