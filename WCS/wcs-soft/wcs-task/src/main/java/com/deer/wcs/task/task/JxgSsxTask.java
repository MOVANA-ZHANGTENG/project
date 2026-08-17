package com.deer.wcs.task.task;

import com.alibaba.fastjson2.JSONObject;
import com.deer.wcs.base.model.DeviceInfo;
import com.deer.wcs.base.model.DeviceValue;
import com.deer.wcs.base.model.PositionInfo;
import com.deer.wcs.base.model.ValueData;
import com.deer.wcs.base.service.DeviceInfoService;
import com.deer.wcs.base.service.PalletInfoService;
import com.deer.wcs.base.service.PositionInfoService;
import com.deer.wcs.base.utils.ModbusPLCUtils;
import com.deer.wcs.task.handle.JxgSxxPosition;
import com.deer.wcs.task.handle.JxgSxxPositionValue;
import com.deer.wcs.task.model.ThreeData;
import com.deer.wcs.task.websocket.WebSocketUsers;
import com.github.xingshuangs.iot.protocol.modbus.service.ModbusTcp;
import org.apache.poi.ss.formula.functions.T;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import tk.mybatis.mapper.entity.Condition;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Component("JxgSsxTask")
public class JxgSsxTask {

    @Autowired
    private PositionInfoService positionInfoService;

    @Autowired
    private PalletInfoService palletInfoService;

    static ModbusTcp modbusTcp = null;


    private static Map<String ,JxgSxxPosition> map=new HashMap<>();

    public void run(){
        Condition condition = new Condition(PositionInfo.class);
        condition.createCriteria().andEqualTo("wareCode","JXG_DISPALY")
                .andEqualTo("isDelete",0);
        List<PositionInfo> list = positionInfoService.findByCondition(condition);
        if(modbusTcp==null){
              modbusTcp = new ModbusTcp(1, "192.168.1.88", 502);
        }
        heart();
        for (int i = 0; i < list.size(); i++) {
            PositionInfo positionInfo = list.get(i);
            if(positionInfo.getSubCode()!=null){
                if(!map.containsKey(positionInfo.getCode())){
                    JxgSxxPosition jxgSxxPosition = new JxgSxxPosition(positionInfo.getSubCode());
                    map.put(positionInfo.getCode(),jxgSxxPosition);
                    handle(jxgSxxPosition,positionInfo.getCode());
                }
            }
        }
    }

    @Autowired
    private DeviceInfoService deviceInfoService;

    private static Boolean isOnline = false;
    private static Boolean lastIsOnline = false;
    private void heart(){
        try {
            Short test= (short) modbusTcp.readInt32(12111);
            isOnline=true;
        } catch (Exception e) {
            isOnline=false;
        }
        if(isOnline!=lastIsOnline){
            DeviceInfo deviceInfo = deviceInfoService.findByCode("JXG_SSX");
            DeviceInfo deviceInfo2 = deviceInfoService.findByCode("JXG_SSX_SC");
            deviceInfo.setIsOnline(isOnline?1:0);
            deviceInfo2.setIsOnline(isOnline?1:0);
            deviceInfoService.update(deviceInfo);
            deviceInfoService.update(deviceInfo2);
            lastIsOnline =isOnline;
        }

    }


    private void handle(JxgSxxPosition jxgSxxPosition,String wmsCode){
        Thread t = new Thread(() -> {

            while (true){
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {

                }
                if(!isOnline){
                    continue;
                }
                JxgSxxPositionValue value = new JxgSxxPositionValue();
                value.code= (short) modbusTcp.readInt32(Integer.parseInt(jxgSxxPosition.code));
                value.hasPallet= (short) modbusTcp.readInt32(Integer.parseInt(jxgSxxPosition.hasPallet));
                value.palletQuantity= (short) modbusTcp.readInt32(Integer.parseInt(jxgSxxPosition.palletQuantity));
                value.gdUp= (short) modbusTcp.readInt32(Integer.parseInt(jxgSxxPosition.gdUp));
                value.gdDown= (short) modbusTcp.readInt32(Integer.parseInt(jxgSxxPosition.gdDown));
                value.yzUp= (short) modbusTcp.readInt32(Integer.parseInt(jxgSxxPosition.yzUp));
                value.yzDown= (short) modbusTcp.readInt32(Integer.parseInt(jxgSxxPosition.yzDown));
                value.toNode= (short) modbusTcp.readInt32(Integer.parseInt(jxgSxxPosition.toNode));  //10307
                value.arrive= (short) modbusTcp.readInt32(Integer.parseInt(jxgSxxPosition.arrive));
                value.leave= (short) modbusTcp.readInt32(Integer.parseInt(jxgSxxPosition.leave));
                value.action= (short) modbusTcp.readInt32(Integer.parseInt(jxgSxxPosition.action));
                value.palletCode= (String) modbusTcp.readString(Integer.parseInt(jxgSxxPosition.palletCode),50);

                if(!lastMap.containsKey(wmsCode)){
                    lastMap.put(wmsCode,new JxgSxxPositionValue());
                } else {
                    JxgSxxPositionValue last = lastMap.get(wmsCode);
                    //托盘有了
                    if(dataUpdate(value.hasPallet,last.hasPallet, (short) 1)){
                        System.out.println(wmsCode+"有托盘，托盘数："+value.palletQuantity+" 编码："+value.palletCode);
                        send("hasPallet",wmsCode,null,value.palletCode);
                    }
                    //托盘没了
                    if(dataUpdate(value.hasPallet,last.hasPallet, (short) 0)){
                        System.out.println(wmsCode+"无托盘"+" 编码："+value.palletCode);
                        send("noHasPallet",wmsCode,null,value.palletCode);
                    }
                    //目的地
//                    if(dataUpdate(value.toNode,last.toNode, (short) 0)){
//                        System.out.println(wmsCode+"目的地"+value.toNode);
//                    }
                    //格挡上了
                    if(dataUpdate(value.gdUp,last.gdUp, (short) 1)){
                        //System.out.println(wmsCode+"格挡上了");
                        //send("gdUp",wmsCode,null,value.palletCode);
                    }
                    //格挡下了
                    if(dataUpdate(value.gdDown,last.gdDown, (short) 1)){
                        //System.out.println(wmsCode+"格挡下了");
                        //send("gdDown",wmsCode,null,value.palletCode);
                    }
                    //移栽上
                    if(dataUpdate(value.yzUp,last.yzUp, (short) 1)){
                        //System.out.println(wmsCode+"移栽上");
                        //send("yzUp",wmsCode,null,value.palletCode);
                    }
                    //移栽下
                    if(dataUpdate(value.yzDown,last.yzDown, (short) 1)){
                        //System.out.println(wmsCode+"移栽下");
                        //send("yzDown",wmsCode,null,value.palletCode);
                    }
                    //托盘到达  动画
                    if(dataUpdate(value.arrive,last.arrive, (short) 1)){
                       // System.out.println(wmsCode+"："+value.palletCode+"托盘到达  动画");
                    }
                    //托盘离开  动画
                    if(dataUpdate(value.leave,last.leave, (short) 1)){
                        System.out.println("地址："+jxgSxxPosition.toNode);
                        value.toNode= (short) modbusTcp.readInt32(Integer.parseInt(jxgSxxPosition.toNode));
                        System.out.println("值："+value.toNode);
                        System.out.println(wmsCode+"："+value.palletCode+"托盘离开  动画");
                        System.out.println(wmsCode+"目的地"+value.toNode);
                        send("leave",wmsCode,nodeCode(value.toNode),value.palletCode);

                    }
                    //托盘号
                    if(dataUpdate(value.palletCode,last.palletCode, (short) 1)){
                        System.out.println(wmsCode+"托盘号");

                    }
                    //叠盘动作/拆盘动作/拍照/机械臂
                    if(dataUpdate(value.action,last.action, (short) 1)){
                        System.out.println(wmsCode+"叠盘动作/拆盘动作/拍照/机械臂："+value.action);
                    }


                    lastMap.replace(wmsCode,value);
                }
            }
        });
        t.start();
    }

    private Boolean dataUpdate(Object newValue,Object lastValue,Object nowValue){
        if(nowValue!=null){
            if(!newValue.equals(lastValue) && newValue.equals(nowValue)){
                return true;
            }
        }else {
            if(!newValue.equals(lastValue) ){
                return true;
            }
        }

        return false;
    }

    private void send(String type,String node,String toNode,String palletCode ){
        ThreeData data = new ThreeData();
        data.setType("ssx");
        data.setSsxType(type);
        data.setFromNode(node);
        data.setToNode(toNode);
        if(palletCode==null || palletCode.trim().equals("")){
            palletCode="123";
        }
        data.setPalletCode(palletCode);
        sendMsgToWebSocket(data);
    }

    private String nodeCode(Short code){
        if(code<10){
            return "0"+code;
        }else {
            return code.toString();
        }
    }



    private void sendMsgToWebSocket(ThreeData data){
        WebSocketUsers.sendMessageToUsersByText(JSONObject.toJSONString(data));
    }



    public static Map<String ,JxgSxxPositionValue> lastMap=new HashMap<>();
    private void last(){

    }






}
