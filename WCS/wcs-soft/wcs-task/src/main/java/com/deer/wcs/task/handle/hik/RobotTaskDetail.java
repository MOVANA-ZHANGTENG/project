package com.deer.wcs.task.handle.hik;

/**
 * 机器人任务实体类（精细化版本）
 */
public class RobotTaskDetail {
    private String robotTaskCode;
    private String singleRobotCode;
    private int currentSeq;
    private ExtraDetail extra;

    // getter & setter
    public String getRobotTaskCode() {
        return robotTaskCode;
    }

    public void setRobotTaskCode(String robotTaskCode) {
        this.robotTaskCode = robotTaskCode;
    }

    public String getSingleRobotCode() {
        return singleRobotCode;
    }

    public void setSingleRobotCode(String singleRobotCode) {
        this.singleRobotCode = singleRobotCode;
    }

    public int getCurrentSeq() {
        return currentSeq;
    }

    public void setCurrentSeq(int currentSeq) {
        this.currentSeq = currentSeq;
    }

    public ExtraDetail getExtra() {
        return extra;
    }

    public void setExtra(ExtraDetail extra) {
        this.extra = extra;
    }

    /**
     * 扩展信息
     */
    public static class ExtraDetail {
        private String async;
        private Values values;

        // getter & setter
        public String getAsync() {
            return async;
        }

        public void setAsync(String async) {
            this.async = async;
        }

        public Values getValues() {
            return values;
        }

        public void setValues(Values values) {
            this.values = values;
        }
    }

    /**
     * Values详情
     */
    public static class Values {
        private String mapCode;
        private String slotCategory;
        private String slotCode;
        private String slotName;
        private Double x;
        private Double y;
        private String method;
        private String carrierCategory;
        private String carrierType;
        private String carrierCode;
        private Integer pileCount;
        private String taskTime;
        private String amrCategory;
        private String amrType;
        private String amrCode;
        private String carrierName;
        private Integer layerNo;

        // 所有字段的getter & setter
        public String getMapCode() {
            return mapCode;
        }

        public void setMapCode(String mapCode) {
            this.mapCode = mapCode;
        }

        public String getSlotCategory() {
            return slotCategory;
        }

        public void setSlotCategory(String slotCategory) {
            this.slotCategory = slotCategory;
        }

        public String getSlotCode() {
            return slotCode;
        }

        public void setSlotCode(String slotCode) {
            this.slotCode = slotCode;
        }

        public String getSlotName() {
            return slotName;
        }

        public void setSlotName(String slotName) {
            this.slotName = slotName;
        }

        public Double getX() {
            return x;
        }

        public void setX(Double x) {
            this.x = x;
        }

        public Double getY() {
            return y;
        }

        public void setY(Double y) {
            this.y = y;
        }

        public String getMethod() {
            return method;
        }

        public void setMethod(String method) {
            this.method = method;
        }

        public String getCarrierCategory() {
            return carrierCategory;
        }

        public void setCarrierCategory(String carrierCategory) {
            this.carrierCategory = carrierCategory;
        }

        public String getCarrierType() {
            return carrierType;
        }

        public void setCarrierType(String carrierType) {
            this.carrierType = carrierType;
        }

        public String getCarrierCode() {
            return carrierCode;
        }

        public void setCarrierCode(String carrierCode) {
            this.carrierCode = carrierCode;
        }

        public Integer getPileCount() {
            return pileCount;
        }

        public void setPileCount(Integer pileCount) {
            this.pileCount = pileCount;
        }

        public String getTaskTime() {
            return taskTime;
        }

        public void setTaskTime(String taskTime) {
            this.taskTime = taskTime;
        }

        public String getAmrCategory() {
            return amrCategory;
        }

        public void setAmrCategory(String amrCategory) {
            this.amrCategory = amrCategory;
        }

        public String getAmrType() {
            return amrType;
        }

        public void setAmrType(String amrType) {
            this.amrType = amrType;
        }

        public String getAmrCode() {
            return amrCode;
        }

        public void setAmrCode(String amrCode) {
            this.amrCode = amrCode;
        }

        public String getCarrierName() {
            return carrierName;
        }

        public void setCarrierName(String carrierName) {
            this.carrierName = carrierName;
        }

        public Integer getLayerNo() {
            return layerNo;
        }

        public void setLayerNo(Integer layerNo) {
            this.layerNo = layerNo;
        }
    }
}
