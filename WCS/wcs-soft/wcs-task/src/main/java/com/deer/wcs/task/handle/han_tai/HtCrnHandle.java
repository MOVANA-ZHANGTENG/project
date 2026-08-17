package com.deer.wcs.task.handle.han_tai;


import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.deer.wcs.base.model.CellInfo;
import com.deer.wcs.base.model.DeviceInfo;
import com.deer.wcs.base.model.LineInfo;
import com.deer.wcs.base.service.CellInfoService;
import com.deer.wcs.base.service.DeviceInfoService;
import com.deer.wcs.base.service.LineInfoService;
import com.deer.wcs.common.core.domain.Result;
import com.deer.wcs.system.service.AutoService;
import com.deer.wcs.task.handle.hik.AgvResult;
import com.deer.wcs.task.model.JobInfo;
import com.deer.wcs.task.model.TaskInfo;
import com.deer.wcs.task.service.JobInfoService;
import com.deer.wcs.task.service.TaskInfoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketException;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component("HtCrnHandle")
public class HtCrnHandle {

    @Value("${isTest}")
    private Boolean isTest;
    
    public static Map<String,Socket> socketMap = new HashMap<>();

    public static Socket getSocket(String ip,Integer port){
        Socket socket = socketMap.get(ip);
        if(socket==null || socket.isClosed()){
            try {
                socket = new Socket(ip, 3000);
                socketMap.put(ip,socket);
                return socket;
            }catch (Exception ex){
                ex.printStackTrace();
                return null;
            }

        }else {
            return socketMap.get(ip);
        }
    }

 //   @Scheduled(cron = "0/1 * * * * ?")
    public void autoRun() {
        if (isTest) {
            return;
        }
        String deviceCodes[] = {"crn1","crn2"};
        for (String deviceCode : deviceCodes) {
            DeviceInfo deviceInfo = deviceInfoService.findBy("code",deviceCode);
            char[] cmd = deviceStateCmd();
            System.out.println(cmd);
            char[] result = send(deviceInfo.getIp(),deviceInfo.getPort(),cmd);
            if(result!=null && result.length>0 && deviceInfo.getIsOnline()==0){
                deviceInfo.setIsOnline(1);
                deviceInfoService.update(deviceInfo);
            }
            if((result==null || result.length==0) &&  deviceInfo.getIsOnline()==1){
                deviceInfo.setIsOnline(0);
                deviceInfoService.update(deviceInfo);
            }
        }
    }


    private static final Logger log = LoggerFactory.getLogger(HtCrnHandle.class);
    static char char2 = 0x02;
    static char char3 = 0x03;
    static char char0 = 0x30;



    public static char[] send(String ip,Integer port,char[] sendChars){


        ///////////////////////////////////

        Socket socket =new Socket();
        DataOutputStream out = null;
        DataInputStream in = null;
        try {


            socket.connect(new InetSocketAddress(ip,port),3000);
            socket.setSoTimeout(2000);
              out = new DataOutputStream(socket.getOutputStream());
             in = new DataInputStream(socket.getInputStream());

            // 发送 char 数组的长度
            byte[] msg =new byte[sendChars.length];
            // 逐个发送 char
            for (int i = 0; i < sendChars.length; i++) {
                msg[i] = (byte) sendChars[i];
            }
            out.write(msg);
            out.flush();
            System.out.println("客户端已发送 char 数组: " + new String(sendChars));

            byte[] result = new byte[30];
            // 接收服务器返回的 char 数组
            in.read(result);

            char[] resultChar =new char[result.length];
            // 逐个发送 char
            for (int i = 0; i < result.length; i++) {
                resultChar[i] = (char) result[i];
            }


            System.out.println("客户端接收到服务器的 char 数组: " + new String(resultChar));
            in.close();
            out.close();
            socket.close();
            return resultChar;
        } catch (Exception e) {
            e.printStackTrace();
            try {
                in.close();
                out.close();
                socket.close();
            }catch (Exception ex){

            }
        }

        return null;
    }


    //192.168.200.50  192.168.200.60




    public static void main(String[] args) {
        char[] aaa = {9,9};
        String bb = new String(aaa);
        System.out.println(bb);
    }


    public static void getDeviceStatus(String ip,Integer port){
        char[] cmd = deviceStateCmd();
        System.out.println(cmd);
        char[] result = send(ip,port,cmd);
        deviceStateResult(result);
    }


    /**
     * 设备状态指令
     * @return
     */
    private static char[] deviceStateCmd() {
        char[] deviceStateCmd = {char2,'H','A',char3};
        return completeCmd(deviceStateCmd,30);
    }

    /**
     * Char2 LA 00 111 222 3 4 5555 6 77777777 8 Char3
     *   1    2 2   3   3  1 1  4   1     8    1   1
     *        *   1    2 2   3   3  1 1  4   1     8    1   1
     * 解析设备状态结果
     * @return
     */
    private static void deviceStateResult(char[] deviceStateResultData) {
        int index=0;
        char[] cha2 = Arrays.copyOfRange(deviceStateResultData, index, index+=1);
        char[] LA = Arrays.copyOfRange(deviceStateResultData, index, index+=2);
        char[] state = Arrays.copyOfRange(deviceStateResultData, index+=3, index+=4);
        char[] col =Arrays.copyOfRange(deviceStateResultData, index, index+=3);
        char[] row = Arrays.copyOfRange(deviceStateResultData, index, index+=3);
        char[] zht = Arrays.copyOfRange(deviceStateResultData, index, index+=1);
        char[] isZt = Arrays.copyOfRange(deviceStateResultData, index, index+=1);
        char[] errCode = Arrays.copyOfRange(deviceStateResultData, index, index+=4);
        char[] type = Arrays.copyOfRange(deviceStateResultData, index, index+=1);
        char[] taskNo = Arrays.copyOfRange(deviceStateResultData, index, index+=8);
        char[] hc = Arrays.copyOfRange(deviceStateResultData, index, index+=1);
    }


    /**
     * 急停指令
     * @return
     */
    private static char[] stopCmd() {
        char[] deviceStateCmd = {char2,'H','E',char3};
        return completeCmd(deviceStateCmd,30);
    }

    /**
     * 取消急停指令
     * @return
     */
    private static char[] stopCancelCmd() {
        char[] deviceStateCmd = {char2,'H','C',char3};
        return completeCmd(deviceStateCmd,30);
    }
    /**
     * 回原点指令
     * @return
     */
    private static char[] return0Cmd() {
        char[] deviceStateCmd = {char2,'H','P',char3};
        return completeCmd(deviceStateCmd,30);
    }

    /**
     * 故障恢复
     * @return
     */
    private static char[] gzhfCmd() {
        char[] deviceStateCmd = {char2,'H','R',char3};
        return completeCmd(deviceStateCmd,30);
    }

    /**
     * 补足位数
     * @param cmd
     * @param length
     * @return
     */
    private static char[] completeCmd(char[] cmd,int length) {
        char[] completeCmd = new char[length];
       int completeLength = length - cmd.length;
       for (int i = 0; i < cmd.length; i++) {
           completeCmd[i] = cmd[i];
       }
       for (int i = 0; i < completeLength; i++) {
           completeCmd[cmd.length+i] = char0;
       }
       return completeCmd;
    }

    private static char[] stringToChar(String str,int length) {
        char[] chars = new char[length];
        if(str.length()!=length){
            throw   new RuntimeException("指令长度错误"+str);
        }
        for (int i = 0; i < str.length(); i++) {
            chars[i] = str.charAt(i);
        }
        return chars;
    }

    /**
     * 发送任务命令
     * @param taskNo
     * @param fromAb
     * @param fromCol
     * @param fromRow
     * @param toAb
     * @param toCol
     * @param toRow
     * @return
     */
    private static char[] sendTaskCmd(String taskNo,String fromAb,String fromCol,String fromRow,String toAb,String toCol,String toRow) {
        char[] cmd = new char[30];
        int index=0;
        cmd[index] = char2;

        //指令类型
        cmd[index+=1] = 'H';
        cmd[index+=1] = 'B';

        //起始位置
        char[] fromAbChars = stringToChar(fromAb,2);
        for (int i = 0; i < fromAbChars.length; i++) {
            char value = fromAbChars[i];
            cmd[index+=1] =value;
        }

        char[] fromColChars = stringToChar(fromCol,3);
        for (int i = 0; i < fromColChars.length; i++) {
            char value = fromColChars[i];
            cmd[index+=1] =value;
        }

        char[] fromRowChars = stringToChar(fromRow,3);
        for (int i = 0; i < fromRowChars.length; i++) {
            char value = fromRowChars[i];
            cmd[index+=1] =value;
        }



        //目标位置

        char[] toAbChars = stringToChar(toAb,2);
        for (int i = 0; i < toAbChars.length; i++) {
            char value = toAbChars[i];
            cmd[index+=1] =value;
        }

        char[] toColChars = stringToChar(toCol,3);
        for (int i = 0; i < toColChars.length; i++) {
            char value = toColChars[i];
            cmd[index+=1] =value;
        }

        char[] toRowChars = stringToChar(toRow,3);
        for (int i = 0; i < toRowChars.length; i++) {
            char value = toRowChars[i];
            cmd[index+=1] =value;
        }



        cmd[index+=1] = '1';
        cmd[index+=1] = '1';

        //任务号
        for (int i = 0; i < taskNo.length(); i++) {
            cmd[index+=1] = stringToChar(taskNo,8)[i];
        }


        cmd[index+=1] = char3;

      //  Char2 H B AA DDD EEE FF III JJJ KK LLLLLLLL Char 3

        return completeCmd(cmd,30);
    }

    /**
     * Char2 LB A MMMMMMMM Char 3
     * 1 2 1 8 1
     * 解析设备状态结果
     * @return
     */
    private static void sendTaskResult(char[] sendTaskResultData) {
        int index=0;
        char[] char2 = Arrays.copyOfRange(sendTaskResultData, index, index+=1);
        char[] LB = Arrays.copyOfRange(sendTaskResultData, index, index+=2);
        char[] A = Arrays.copyOfRange(sendTaskResultData, index , index+=1);
        char[] taskNo =Arrays.copyOfRange(sendTaskResultData, index, index+=8);
        char[] char3 = Arrays.copyOfRange(sendTaskResultData, index, index+=1);
    }

    private static void sendTask(String ip,Integer port){




    }


//
//    public static void main(String[] args) {
//        char[] aaa = sendTaskCmd("12345678","A1","101","001","B1","102","002");
//
//        System.out.println(aaa);
//
//        sendTaskResult("2LBA123456783".toCharArray());
//    }

    public static String client(String url, HttpMethod method, String json) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> requestEntity = new HttpEntity<>(json, headers);
        RestTemplate client = new RestTemplate();
        ResponseEntity<String> response = client.exchange(url, method, requestEntity, String.class);
        if (response.getStatusCode() == HttpStatus.OK){
            return response.getBody();
        }
        log.error(url,response.getStatusCode());
        return null;
    }

    @Autowired
    private TaskInfoService taskInfoService;

    @Autowired
    private JobInfoService jobInfoService;

    @Value("${wms.baseUrl}")
    private String wmsBaseUrl;

    /**
     * 向WMS申请库位
     * @param jobInfo
     * @return
     */
    public Boolean applyCellCode(JobInfo jobInfo){
        jobInfo=jobInfoService.findById(jobInfo.getId());
        TaskInfo taskInfo = taskInfoService.findById(jobInfo.getTaskId());
        Double weight = taskInfo.getPalletWeight();
        String palletCode = taskInfo.getPalletCode();
        String point = taskInfo.getFromCellCode();
        String url=wmsBaseUrl+"/fromWcs/targetApply";
        String json="";
        ApplyCell applyCell = new ApplyCell();
        applyCell.state=0;
        applyCell.weight=weight;
        applyCell.type=1;
        applyCell.palletCode=palletCode;
        applyCell.pointCode=point;
        applyCell.taskNo=taskInfo.getId().toString();
        json=JSON.toJSONString(applyCell);
        WmsResult result;
        try {
            String resultStr = client(url,HttpMethod.POST,json);
            result = JSON.parseObject(resultStr, WmsResult.class);

        }catch (Exception ex){
            result = new WmsResult();
            result.setCode(500);
            result.setMsg(ex.getMessage());
        }
        if (result.getCode() == 200) {
            JSONObject data =JSONObject.parse( result.data);
            String cellCode =(String) data.get("cellCode");
            String wmsTaskNo =(String) data.get("wmsTaskNo");
            taskInfo.setToCellCode(cellCode);
            taskInfo.setWmsTaskNo(wmsTaskNo);
            taskInfoService.update(taskInfo);
        }else {
            jobInfoService.updateMemo(jobInfo,"申请库位时，WMS返回："+result.getMsg());
            return false;
        }
        return true;
    }

    @Autowired
    private LineInfoService lineInfoService;
    @Autowired
    private CellInfoService cellInfoService;

    @Autowired
    private DeviceInfoService deviceInfoService;

    private DeviceInfo getDeviceInfo(String cellCode){
        CellInfo cellInfo = cellInfoService.findBy("code",cellCode);
        LineInfo lineInfo = lineInfoService.findBy("code",cellInfo.getLineCode());
        DeviceInfo deviceInfo =deviceInfoService.findBy("code",lineInfo.getDeviceCode());
        return deviceInfo;
    }

    private String getString(Integer value ,Integer length){
        String str = String.valueOf(value);
        while (str.length() < length) {
            str = "0" + str;
        }
        return str;
    }
    private String getString(Long value ,Integer length){
        String str = String.valueOf(value);
        while (str.length() < length) {
            str = "0" + str;
        }
        return str;
    }

    private String getX(Integer value){
        if(value==1){
            return "01";
        }
        if(value==2){
            return "02";
        }
        if(value==3){
            return "01";
        }
        if(value==4){
            return "02";
        }
        throw new RuntimeException("未知排");
    }

    @Autowired
    AutoService autoService;


    public Boolean sandTask(JobInfo jobInfo){
        if(isTest){
            return true;
        }
        Boolean canSend = canSandTask(jobInfo);
        if(!canSend){
            return false;
        }
        String fromCellCode = jobInfo.getFromCellCode();
        String toCellCode = jobInfo.getToCellCode();
        CellInfo fromCellInfo = cellInfoService.findBy("code",fromCellCode);
        CellInfo toCellInfo = cellInfoService.findBy("code",toCellCode);
        DeviceInfo deviceInfo = getDeviceInfo(fromCellCode);

        String fromX = getX(fromCellInfo.getX());
        String fromY = getString(fromCellInfo.getY(),3);
        String fromZ = getString(fromCellInfo.getZ(),3);

        String toX = getX(toCellInfo.getX());
        String toY = getString(toCellInfo.getY(),3);
        String toZ = getString(toCellInfo.getZ(),3);


        jobInfo.setTaskNo(autoService.getHtCrnTaskNo());
        jobInfoService.update(jobInfo);
        char[] cmd = sendTaskCmd(jobInfo.getTaskNo(),fromX,fromY,fromZ,toX,toY,toZ);
        char[] sendTaskResultData = send(deviceInfo.getIp(),deviceInfo.getPort(),cmd);

        if(sendTaskResultData==null){
            jobInfoService.updateMemo(jobInfo,"堆垛机通信异常");
            return false;
        }
        if(sendTaskResultData.length==0){
            jobInfoService.updateMemo(jobInfo,"堆垛机通信异常");
            return false;
        }

            int index=0;
            char[] char2 = Arrays.copyOfRange(sendTaskResultData, index, index+=1);
            char[] LB = Arrays.copyOfRange(sendTaskResultData, index, index+=2);
            char[] A = Arrays.copyOfRange(sendTaskResultData, index , index+=1);
            char[] taskNo =Arrays.copyOfRange(sendTaskResultData, index, index+=8);
            char[] char3 = Arrays.copyOfRange(sendTaskResultData, index, index+=1);

        if(Arrays.equals(A, new char[]{'1'})){
            return true;

        }
        if(Arrays.equals(A, new char[]{'2'})){
            jobInfoService.updateMemo(jobInfo,"堆垛机接受任务不正确");
            return false;

        }
        if(Arrays.equals(A, new char[]{'3'})){
            jobInfoService.updateMemo(jobInfo,"堆垛机上一任务还未完成");
            return false;
        }



        return false;

    }

    @Autowired
    private HtSsxDevice htSsxDevice;;
    public Boolean canSandTask(JobInfo jobInfo){
        if(isTest){
            return true;
        }
        String toCellCode = jobInfo.getToCellCode();
        if(toCellCode.equals("25") || toCellCode.equals("34")){
            int device = Integer.parseInt(jobInfo.getToCellCode());
            Integer address = htSsxDevice.deviceMap.get(device);
            Short plcHasPallet = htSsxDevice.hasPallet(address);
            if(plcHasPallet>0){
                jobInfoService.updateMemo(jobInfo,"输送线接驳位有货，不允许堆垛机放货");
                return false;
            }
        }

        String fromCellCode = jobInfo.getFromCellCode();
        if(fromCellCode.equals("30") || fromCellCode.equals("40")){

            int device = Integer.parseInt(jobInfo.getFromCellCode());
            Integer address = htSsxDevice.deviceMap.get(device);
            Short plcHasPallet = htSsxDevice.hasPallet(address);

            if(plcHasPallet==0){
                jobInfoService.updateMemo(jobInfo,"输送线接驳位无货，不允许堆垛机取货");
                return false;
            }

            if(toCellCode.equals("25") || toCellCode.equals("34")){

            }else {
                Integer taskNo = htSsxDevice.taskNo(address);
                JobInfo lastJob = jobInfoService.findByIndex(jobInfo.getTaskId(),jobInfo.getJobIndex()-1);
                if(!taskNo.equals( Integer.parseInt(lastJob.getTaskNo()))){
                    jobInfoService.updateMemo(jobInfo,"输送线接驳位任务号对不上，不允许堆垛机取货");
                    return false;
                }
            }

        }


        DeviceInfo deviceInfo = getDeviceInfo(fromCellCode);

        char[] cmd = deviceStateCmd();
        char[] deviceStateResultData = send(deviceInfo.getIp(),deviceInfo.getPort(),cmd);

        if(deviceStateResultData==null){
            return false;
        }

        if(deviceStateResultData.length==0){
            return false;
        }

        int index=0;
        char[] cha2 = Arrays.copyOfRange(deviceStateResultData, index, index+=1);
        char[] LA = Arrays.copyOfRange(deviceStateResultData, index, index+=2);
        char[] state = Arrays.copyOfRange(deviceStateResultData, index, index+=2);
        char[] col =Arrays.copyOfRange(deviceStateResultData, index, index+=3);
        char[] row = Arrays.copyOfRange(deviceStateResultData, index, index+=3);
        char[] zht = Arrays.copyOfRange(deviceStateResultData, index, index+=1);
        char[] isZt = Arrays.copyOfRange(deviceStateResultData, index, index+=1);
        char[] errCode = Arrays.copyOfRange(deviceStateResultData, index, index+=4);
        char[] type = Arrays.copyOfRange(deviceStateResultData, index, index+=1);
        char[] taskNo = Arrays.copyOfRange(deviceStateResultData, index, index+=8);
        char[] hc = Arrays.copyOfRange(deviceStateResultData, index, index+=1);
        char[] cha3 = Arrays.copyOfRange(deviceStateResultData, index, index+=1);



        if(Arrays.equals(state, new char[]{'1', '2'})){
            jobInfoService.updateMemo(jobInfo,"堆垛机为手动模式");
            return false;
        }

        if(Arrays.equals(state, new char[]{'0', '8'})){
            jobInfoService.updateMemo(jobInfo,"堆垛机报警");
            return false;
        }

        if(Arrays.equals(state, new char[]{'0', '2'})){
            return true;
        }


        return true;

    }


    /**
     * 优先入库
     *
     * @param jobInfo 作业信息对象，包含作业的相关信息，如任务编号、起始库位等
     * @return 如果满足优先入库条件，返回 true；否则返回 false
     */
    public Boolean yxrk(JobInfo jobInfo){
        // 如果处于测试模式，直接返回 true
        if(isTest){
            return true;
        }

        // 获取作业的起始库位编码
        String fromCellCode = jobInfo.getFromCellCode();
        // 根据起始库位编码获取库位信息
        CellInfo cellInfo = cellInfoService.findBy("code",fromCellCode);
        // 根据库位信息中的线路编码获取线路信息
        LineInfo lineInfo = lineInfoService.findBy("code",cellInfo.getLineCode());

        // 如果起点巷道被其他任务占用
        if(!taskState0(lineInfo,jobInfo)){
            // 更新作业备注信息，表明起点巷道被其他任务占用
            jobInfoService.updateMemo(jobInfo,fromCellCode+"：起点巷道被其他任务占用 "+lineInfo.getTaskState());
            // 返回 false
            return false;
        }

        // 根据起始库位编码获取设备信息
        DeviceInfo deviceInfo = getDeviceInfo(fromCellCode);
        // 生成设备状态指令
        char[] cmd = deviceStateCmd();
        // 发送设备状态指令并获取返回结果
        char[] deviceStateResultData = send(deviceInfo.getIp(),deviceInfo.getPort(),cmd);
        // 如果设备状态返回结果为空，返回 false
        if(deviceStateResultData==null){
            jobInfoService.updateMemo(jobInfo,"堆垛机通信异常");
            return false;
        }
        // 如果设备状态返回结果长度为 0，返回 false
        if(deviceStateResultData.length==0){
            jobInfoService.updateMemo(jobInfo,"堆垛机通信异常");
            return false;
        }
        // 初始化索引
        int index=0;
        // 从设备状态返回结果中截取 col 字段
        char[] col =Arrays.copyOfRange(deviceStateResultData, 5, 8);
        // 如果 列 字段等于 {'0'}  判断入库优先
        if(Arrays.equals(col, new char[]{'0', '0', '0'})){
            // 获取设备地址，初始为 30 对应的地址
            Integer address = htSsxDevice.deviceMap.get(30);
            // 如果线路的设备编码为 "crn1"
            if(lineInfo.getDeviceCode().equals("crn1")){
                // 更新设备地址为 30 对应的地址
                address = htSsxDevice.deviceMap.get(30);
            }
            // 如果线路的设备编码为 "crn2"
            if(lineInfo.getDeviceCode().equals("crn2")){
                // 更新设备地址为 40 对应的地址
                address = htSsxDevice.deviceMap.get(40);
            }
            // 获取 PLC 上该地址是否有托盘的信息
            Short plcHasPallet = htSsxDevice.hasPallet(address);
            // 如果 PLC 上该地址有托盘
            if(plcHasPallet>0){
                // 更新作业备注信息，表明输送线入库接驳位有货，优先入库
                jobInfoService.updateMemo(jobInfo,"输送线入库接驳位有货，优先入库");
                // 返回 false
                return false;
            }

            Short palletCount = htSsxDevice.palletCount();
            if(palletCount>10){
                jobInfoService.updateMemo(jobInfo,"输送线上箱子数量大于10个，暂时不允许出库");

                return false;
            }

        }


        // 更新线路的任务状态为当前作业的 ID
        lineInfo.setTaskState(jobInfo.getId());
        // 更新线路信息
        lineInfoService.update(lineInfo);
        // 返回 true
        return true;
    }

    private  Boolean taskState0(LineInfo lineInfo,JobInfo jobInfo){
        Long taskState=lineInfo.getTaskState();
        if(taskState.equals(jobInfo.getId())){
            return true;
        }
        if(taskState.equals(jobInfo.getTaskId())){
            return true;
        }
        if(taskState<0.1){
            return true;
        }
        return false;
    }
    public Boolean canFinish(JobInfo jobInfo){
//        if(isTest){
//            return true;
//        }

        String fromCellCode = jobInfo.getFromCellCode();
        DeviceInfo deviceInfo = getDeviceInfo(fromCellCode);

        char[] cmd = deviceStateCmd();
        char[] deviceStateResultData = send(deviceInfo.getIp(),deviceInfo.getPort(),cmd);

        if(deviceStateResultData==null){

            jobInfoService.updateMemo(jobInfo,"堆垛机通信异常");
            return false;
        }

        if(deviceStateResultData.length==0){
            return false;
        }

        int index=0;
        char[] cha2 = Arrays.copyOfRange(deviceStateResultData, index, index+=1);
        char[] LA = Arrays.copyOfRange(deviceStateResultData, index, index+=2);
        char[] state = Arrays.copyOfRange(deviceStateResultData, index, index+=2);
        char[] col =Arrays.copyOfRange(deviceStateResultData, index, index+=3);
        char[] row = Arrays.copyOfRange(deviceStateResultData, index, index+=3);
        char[] zht = Arrays.copyOfRange(deviceStateResultData, index, index+=1);
        char[] isZt = Arrays.copyOfRange(deviceStateResultData, index, index+=1);
        char[] errCode = Arrays.copyOfRange(deviceStateResultData, index, index+=4);
        char[] type = Arrays.copyOfRange(deviceStateResultData, index, index+=1);
        char[] taskNo = Arrays.copyOfRange(deviceStateResultData, index, index+=8);
        char[] hc = Arrays.copyOfRange(deviceStateResultData, index, index+=1);


        if(Arrays.equals(type, new char[]{2})){

            jobInfoService.updateMemo(jobInfo,"堆垛机PLC返回开始取货-2");
            return false;
        }

        if(Arrays.equals(type, new char[]{3})){

            jobInfoService.updateMemo(jobInfo,"堆垛机PLC返回取货完成-3");
            return false;
        }

        if(Arrays.equals(type, new char[]{4})){

            jobInfoService.updateMemo(jobInfo,"堆垛机PLC返回开始放货-4");
            return false;
        }

        if(Arrays.equals(type, new char[]{5})){

            jobInfoService.updateMemo(jobInfo,"堆垛机PLC返回放货完成-5");
            return false;
        }
        if(Arrays.equals(type, new char[]{7})){

            jobInfoService.updateMemo(jobInfo,"堆垛机PLC返回急停-7");
            return false;
        }
        if(Arrays.equals(type, new char[]{8})){

            jobInfoService.updateMemo(jobInfo,"堆垛机PLC返回报警暂停（报警后切换手自动后的状态）-8");
            return false;
        }
       if(Arrays.equals(type, new char[]{9})){

            jobInfoService.updateMemo(jobInfo,"堆垛机PLC回原点-9");
            return false;
        }

        if(!Arrays.equals(type, new char[]{6})){

            jobInfoService.updateMemo(jobInfo,"堆垛机PLC返回type!=6");
            return false;
        }

        if(!new String(taskNo).equals(jobInfo.getTaskNo())){
            jobInfoService.updateMemo(jobInfo,"堆垛机PLC返回任务号！="+jobInfo.getTaskNo());
            return false;
        }

//        if(Arrays.equals(state, new char[]{'1', '2'})){
//            jobInfoService.updateMemo(jobInfo,"堆垛机为手动模式");
//            return false;
//        }
        if(Arrays.equals(state, new char[]{'0', '8'})){
            jobInfoService.updateMemo(jobInfo,"堆垛机报警");
            return false;
        }

        if(Arrays.equals(type, new char[]{6}) && new String(taskNo).equals(jobInfo.getTaskNo())){

            jobInfoService.updateMemo(jobInfo,"堆垛机PLC返回type=6 而且任务号="+jobInfo.getTaskNo());
            return true;
        }

        return false;


    }
    public Boolean finish(JobInfo jobInfo){
        if(isTest){
            return true;
        }
        return true;

    }

    private class ApplyCell{
        /** 托盘号 */
        private String palletCode;

        /** 申请时位置 */
        private String pointCode;

        private Double weight;

        /** 时间 */
        private String createTime;
        private String taskNo;

        /** 状态 0-初始化 1-处理中 2-已处理 */
        private Integer state;

        /** 类型 1-入库申请 2-出库口目的地申请 */
        private Integer type;

        public String getTaskNo() {
            return taskNo;
        }

        public void setTaskNo(String taskNo) {
            this.taskNo = taskNo;
        }

        public String getPalletCode() {
            return palletCode;
        }

        public void setPalletCode(String palletCode) {
            this.palletCode = palletCode;
        }

        public String getPointCode() {
            return pointCode;
        }

        public void setPointCode(String pointCode) {
            this.pointCode = pointCode;
        }

        public Double getWeight() {
            return weight;
        }

        public void setWeight(Double weight) {
            this.weight = weight;
        }

        public String getCreateTime() {
            return createTime;
        }

        public void setCreateTime(String createTime) {
            this.createTime = createTime;
        }

        public Integer getState() {
            return state;
        }

        public void setState(Integer state) {
            this.state = state;
        }

        public Integer getType() {
            return type;
        }

        public void setType(Integer type) {
            this.type = type;
        }
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
