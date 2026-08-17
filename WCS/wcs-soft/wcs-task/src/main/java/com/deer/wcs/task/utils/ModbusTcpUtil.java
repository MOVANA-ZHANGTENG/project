package com.deer.wcs.task.utils;

import com.serotonin.modbus4j.BatchRead;
import com.serotonin.modbus4j.BatchResults;
import com.serotonin.modbus4j.ModbusMaster;
import com.serotonin.modbus4j.code.DataType;
import com.serotonin.modbus4j.exception.ErrorResponseException;
import com.serotonin.modbus4j.exception.ModbusTransportException;
import com.serotonin.modbus4j.locator.BaseLocator;
import com.serotonin.modbus4j.msg.*;

import java.nio.charset.Charset;

public class ModbusTcpUtil {

    /**
     * 读取[01 Coil Status 0x]类型 开关数据
     *
     * @param slaveId slaveId
     * @param offset  位置
     * @return 读取值
     * @throws ModbusTransportException 异常
     * @throws ErrorResponseException   异常
     */
    public static Boolean readCoilStatus(ModbusMaster master, int slaveId, int offset, String dev_code){
        // 01 Coil Status
        BaseLocator<Boolean> loc = BaseLocator.coilStatus(slaveId, offset);
        try {
            return master.getValue(loc);
        }catch (Exception e){
            if (e.getMessage().equals("java.net.SocketTimeoutException: connect timed out")) System.err.println(dev_code+"："+e.getMessage());
            else e.printStackTrace();
            return null;
        }
    }


    /**
     * 读取[02 Input Status 1x]类型 开关数据
     *
     * @param slaveId
     * @param offset
     * @return
     * @throws ModbusTransportException
     * @throws ErrorResponseException
     */
    public static Boolean readInputStatus(ModbusMaster master,int slaveId, int offset,String dev_code) {
        // 02 Input Status
        BaseLocator<Boolean> loc = BaseLocator.inputStatus(slaveId, offset);
        try{
            return master.getValue(loc);
        }catch (Exception e){
            throw new RuntimeException(e);
        }
    }


    /**
     * 读取[03 Holding Register类型 2x]模拟量数据
     *
     * @param slaveId  slave Id
     * @param offset   位置
     * @param dataType 数据类型,来自com.serotonin.modbus4j.code.DataType
     * @return
     * @throws ModbusTransportException 异常
     * @throws ErrorResponseException   异常
     */
    public static Number readHoldingRegister(ModbusMaster master,int slaveId, int offset, int dataType,String dev_code) {
        // 03 Holding Register类型数据读取
        BaseLocator<Number> loc = BaseLocator.holdingRegister(slaveId, offset, dataType);
        try {
            return master.getValue(loc);
        }catch (Exception e){
           throw new RuntimeException(e);
        }
    }


    /**
     * 读取[04 Input Registers 3x]类型 模拟量数据
     *
     * @param slaveId  slaveId
     * @param offset   位置
     * @param dataType 数据类型,来自com.serotonin.modbus4j.code.DataType
     * @return 返回结果
     * @throws ModbusTransportException 异常
     * @throws ErrorResponseException   异常
     */
    public static Number readInputRegisters(ModbusMaster master,int slaveId, int offset, int dataType,String dev_code) {
        // 04 Input Registers类型数据读取
        BaseLocator<Number> loc = BaseLocator.inputRegister(slaveId, offset, dataType);
        try{
            return master.getValue(loc);
        }catch (Exception e){
            if (e.getMessage().equals("java.net.SocketTimeoutException: connect timed out")) System.err.println(dev_code+"："+e.getMessage());
            else e.printStackTrace();
            return null;
        }
    }

    /**
     * 批量读取使用方法
     *
     * @throws ModbusTransportException
     * @throws ErrorResponseException
     */
    public static void batchRead(ModbusMaster master) throws ModbusTransportException, ErrorResponseException {
        BatchRead<Integer> batch = new BatchRead<Integer>();
        batch.addLocator(0, BaseLocator.holdingRegister(1, 1, DataType.TWO_BYTE_INT_SIGNED));
        batch.addLocator(1, BaseLocator.inputStatus(1, 0));
        batch.setContiguousRequests(true);
        BatchResults<Integer> results = master.send(batch);
        System.out.println("batchRead:" + results.getValue(0));
        System.out.println("batchRead:" + results.getValue(1));
    }

    /**
     * 写单个（线圈）开关量数据
     * 功能码为：05，开关量输出点Q置位或复位，写入数据到真机的DO类型的寄存器上面，可以读写的布尔类型(0x)
     * @param slaveId     slave的ID
     * @param writeOffset 位置-预访问的地址-地址范围：0-255
     * @param writeValue  值-置位则为1，复位则为0
     * @return 是否写入成功
     */
    public static boolean writeCoil(ModbusMaster master,int slaveId, int writeOffset, boolean writeValue){
        boolean flag = false;
        try {
            // 创建请求
            WriteCoilRequest request = new WriteCoilRequest(slaveId, writeOffset, writeValue);
            // 发送请求并获取响应对象
            WriteCoilResponse response = (WriteCoilResponse) master.send(request);
            flag =  !response.isException();
        }catch (ModbusTransportException e){
            e.printStackTrace();
        }
        return flag;
    }

    /**
     * 写多个开关量数据（线圈）
     * 功能码为：0F，写多个开关量数据（线圈）
     * @param slaveId     slaveId
     * @param startOffset 开始位置
     * @param bdata       写入的数据
     * @return 是否写入成功
     */
    public static boolean writeCoils(ModbusMaster master,int slaveId, int startOffset, boolean[] bdata) {
        boolean flag = false;
        try {
            // 创建请求
            WriteCoilsRequest request = new WriteCoilsRequest(slaveId, startOffset, bdata);
            // 发送请求并获取响应对象
            WriteCoilsResponse response = (WriteCoilsResponse) master.send(request);
            flag = !response.isException();
        }catch (ModbusTransportException e){
            e.printStackTrace();
        }
        return flag;
    }

    /***
     *  保持寄存器写单个
     *  功能码为：06，将数据写入至V存储器， 数据到真机，数据类型是Int,可以读写的数字类型(4x)
     * @param slaveId slaveId
     * @param writeOffset 开始位置
     * @param writeValue 写入的数据
     */
    public static boolean writeRegister(ModbusMaster master,int slaveId, int writeOffset, short writeValue){
        boolean flag = false;
        try {
            // 创建请求对象
            WriteRegisterRequest request = new WriteRegisterRequest(slaveId, writeOffset, writeValue);
            // 发送请求并获取响应对象
            WriteRegisterResponse response = (WriteRegisterResponse) master.send(request);
            flag = !response.isException();
        }catch (ModbusTransportException e){
            e.printStackTrace();
        }
        return flag;
    }

    /**
     * 保持寄存器写入多个模拟量数据
     * 功能码为：16,将数据写入至多个V存储器，写入数据到真机，数据类型是short[],可以读写的数字类型(4x)
     * @param slaveId     modbus的slaveID
     * @param startOffset 起始位置偏移量值
     * @param sdata       写入的数据
     * @return 返回是否写入成功
     */
    public static boolean writeRegisters(ModbusMaster master,int slaveId, int startOffset, short[] sdata) {
        boolean flag = false;
        try {
            // 创建请求对象
            WriteRegistersRequest request = new WriteRegistersRequest(slaveId, startOffset, sdata);
            // 发送请求并获取响应对象
            WriteRegistersResponse response = (WriteRegistersResponse) master.send(request);
            flag = !response.isException();
        }catch (ModbusTransportException e){
            e.printStackTrace();
        }
        return flag;
    }

    /**
     * 根据类型写数据（如:写入Float类型的模拟量、Double类型模拟量、整数类型Short、Integer、Long）
     *
     * @param value    写入值
     * @param dataType com.serotonin.modbus4j.code.DataType
     */
    public static void writeHoldingRegister(ModbusMaster master,int slaveId, int offset, Number value, int dataType) {
        try {
            // 类型
            BaseLocator<Number> locator = BaseLocator.holdingRegister(slaveId, offset, dataType);
            master.setValue(locator, value);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

//    @Autowired
//    @Qualifier(value = "ModbusTcpMaster")
//    ModbusTcpMaster masterTcp;

    public static int bytesToInt(byte[] a){
        int ans=0;
        for(int i=0;i<4;i++){
            ans<<=8;//左移 8 位
            ans|=a[3-i];//保存 byte 值到 ans 的最低 8 位上
        }
        return ans;
    }

    /// <summary>
/// 字节数组转16进制字符串：空格分隔
/// </summary>
/// <param name="byteDatas"></param>
/// <returns></returns>
    public static String ToHexStrFromByte( byte[] byteDatas)
    {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < byteDatas.length; i++)
        {
            builder.append(String.format("{0:X2} ", byteDatas[i]));
        }
        return builder.toString().trim();
    }

    public static void StringToInt(String value){

        for(int i = 0;i<value.length();i++){
            byte[] aaa =value.substring(i,1).getBytes(Charset.forName("GBK"));
           // ;
            //int intValue = bytesToInt(aaa);
            System.out.println(ToHexStrFromByte(aaa));

        }
    }












    //汉字后面不能加空格
    public static void main(String[] args) throws InterruptedException {
        //开启ModbusTcpMaster连接
        ModbusMaster master = ModbusTcpMaster.getSlave("192.168.1.88", 502);
        try {
            // 01测试
//            Boolean v01 = ModbusTcpUtil.readCoilStatus(master,1, 8000,"wcs_to_device");
//            ModbusTcpUtil.writeCoil(master,1, 9000,true);
//            Boolean v02 = ModbusTcpUtil.readCoilStatus(master,1, 9000,"device_to_wcs");
//
//            Boolean v03 = ModbusTcpUtil.readCoilStatus(master,1, 102,"test_code");
//            Boolean v04 = ModbusTcpUtil.readCoilStatus(master,1, 103,"test_code");
//
//            System.out.println("v01:" + v01);
//            System.out.println("v02:" + v02);
//            System.out.println("v03:" + v03);
//            System.out.println("v04:" + v04);
//
//            //02测试
//            Boolean v021 = ModbusTcpUtil.readInputStatus(master,1, 0,"test_code");
//            Boolean v022 = ModbusTcpUtil.readInputStatus(master,1, 1,"test_code");
//            Boolean v023 = ModbusTcpUtil.readInputStatus(master,1, 2,"test_code");
//            System.out.println("v021:" + v021);
//            System.out.println("v022:" + v022);
//            System.out.println("v023:" + v023);
//
            // 03测试
            Number v031 = ModbusTcpUtil.readHoldingRegister(master,1, 8000, DataType.TWO_BYTE_INT_SIGNED,"test_code");// 注意,float
            ModbusTcpUtil.writeHoldingRegister(master,1,9500,111,DataType.TWO_BYTE_INT_SIGNED);
            Number v033 = ModbusTcpUtil.readHoldingRegister(master,1, 9000, DataType.TWO_BYTE_INT_SIGNED,"test_code");// 同上
            System.out.println("v031:" + v031);
            System.out.println("v033:" + v033);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     *  String转short[]
     * @param value?
     * @return
     */
    public static short[] SetString(String value) {
        byte[] bytesTemp = value.getBytes(Charset.forName("GBK"));
        return toShortArray(bytesTemp);
    }

    /**
     * byte[]转short[]
     * @param src
     * @return
     */
    public static short[] toShortArray(byte[] src) {
        int count = src.length >> 1;
        short[] dest = new short[count];
        for (int i = 0; i < count; i++) {
            dest[i] = (short) (src[i * 2] << 8 | src[2 * i + 1] & 0xff);
        }
        return dest;
    }


}
