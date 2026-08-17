package com.deer.wcs.base.model;

import com.github.xingshuangs.iot.protocol.s7.service.S7PLC;

public class S7Item {
    String address;
    int type;

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    // 超时时间（单位：毫秒，在连接时使用）
    private static final int TIMEOUT = 5000;
    public S7PLC s7PLC;//= new S7PLC(EPlcType.S1500, "127.0.0.1");

    public static final int BOOL = 1;
    public static final int INT = 2;
    public static final int INT32 = 4;
    public static final int STRING = 5;
    public static final int FLOAT32 = 6;
    public static final int BYTE = 7;

    public S7Item(String address, int type) {
        this.address = address;
        this.type = type;
    }

    public S7Item(String address, int type, S7PLC s7PLC) {
        this.address = address;
        this.type = type;
        this.s7PLC = s7PLC;
    }


    public Object read() {
        Object o=null;
        if (type == BOOL) {
            o = this.s7PLC.readBoolean(address);
        }
        if (type == INT) {
            o = this.s7PLC.readInt16(address);
        }
        if (type == INT32) {
            o = this.s7PLC.readInt32(address);
        }
        if (type == STRING) {
            o = this.s7PLC.readString(address);
        }
        if (type == FLOAT32) {
            o = this.s7PLC.readFloat32(address);
        }
        if (type == BYTE) {
            o = this.s7PLC.readByte(address);
        }
        return o;
    }

    public void write(Object object) {
        if (type == BOOL) {
            this.s7PLC.writeBoolean(address, (boolean) object);
        }

        if (type == INT) {
            this.s7PLC.writeInt16(address, (short) object);
        }
        if (type == INT32) {
            this.s7PLC.writeInt32(address, (int) object);
        }
        if (type == STRING) {
            this.s7PLC.writeString(address, (String) object);
        }
        if (type == FLOAT32) {
            this.s7PLC.writeFloat32(address, (Float) object);
        }
        if (type == BYTE) {
            this.s7PLC.writeByte(address, (byte) object);
        }
        //this.s7PLC.close();
    }
}
