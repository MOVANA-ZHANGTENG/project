package com.deer.wcs.bc.handle;

import com.deer.wcs.base.service.DeviceValueService;
import com.deer.wcs.task.model.JobInfo;
import com.deer.wcs.task.service.JobInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component("BcSsxHandle")
public class BcSsxHandle {

    @Autowired
    private DeviceValueService deviceValueService;

    @Autowired
    private JobInfoService jobInfoService;

    // 站台1设备编码前缀
    private static final String STATION_1_PREFIX = "SSX_STATION_01_";
    // 站台2设备编码前缀
    private static final String STATION_2_PREFIX = "SSX_STATION_02_";

    /**
     * 检查站台是否有托盘
     * @param stationNo 站台号 (1或2)
     * @return true表示有托盘，false表示无托盘
     */
    public Boolean checkPalletExist(int stationNo) {
        try {
            String deviceCode = getStationDeviceCode(stationNo);
            Object result = deviceValueService.readValueByCode(deviceCode, "HAS_PALLET");
            if (result != null) {
                Short value = Short.valueOf(result.toString());
                return value == 1;
            }
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 获取站台的托盘号
     * @param stationNo 站台号 (1或2)
     * @return 托盘号
     */
    public String getPalletCode(int stationNo) {
        try {
            String deviceCode = getStationDeviceCode(stationNo);
            Object result = deviceValueService.readValueByCode(deviceCode, "PALLET_CODE");
            return result != null ? result.toString() : null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 获取站台的BCR数据
     * @param stationNo 站台号 (1或2)
     * @return BCR数据
     */
    public String getBcrData(int stationNo) {
        try {
            String deviceCode = getStationDeviceCode(stationNo);
            Object result = deviceValueService.readValueByCode(deviceCode, "BCR_DATA");
            return result != null ? result.toString() : null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 检查站台是否有任务申请
     * @param stationNo 站台号 (1或2)
     * @return true表示有申请，false表示无申请
     */
    public Boolean checkTaskRequest(int stationNo) {
        try {
            String deviceCode = getStationDeviceCode(stationNo);
            Object result = deviceValueService.readValueByCode(deviceCode, "TASK_REQUEST");
            if (result != null) {
                Short value = Short.valueOf(result.toString());
                return value == 1;
            }
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 设置站台是否允许入库
     * @param stationNo 站台号 (1或2)
     * @param allow true表示允许，false表示不允许
     * @return 操作是否成功
     */
    public Boolean setAllowIn(int stationNo, boolean allow) {
        try {
            String deviceCode = getStationDeviceCode(stationNo);
            int value = allow ? 1 : 0;
            return deviceValueService.writeValueByCode(deviceCode, "ALLOW_IN", value);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 设置站台是否允许出库
     * @param stationNo 站台号 (1或2)
     * @param allow true表示允许，false表示不允许
     * @return 操作是否成功
     */
    public Boolean setAllowOut(int stationNo, boolean allow) {
        try {
            String deviceCode = getStationDeviceCode(stationNo);
            int value = allow ? 1 : 0;
            return deviceValueService.writeValueByCode(deviceCode, "ALLOW_OUT", value);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 处理站台的入库请求
     * @param jobInfo 作业信息
     * @param stationNo 站台号 (1或2)
     * @return 处理是否成功
     */
    public Boolean handleInRequest(JobInfo jobInfo, int stationNo) {
        try {
            // 检查是否有任务申请
            if (!checkTaskRequest(stationNo)) {
                jobInfoService.updateMemo(jobInfo, "站台" + stationNo + "无入库任务申请");
                return false;
            }

            // 检查是否有托盘
            if (!checkPalletExist(stationNo)) {
                jobInfoService.updateMemo(jobInfo, "站台" + stationNo + "无托盘");
                return false;
            }

            // 获取托盘号和BCR数据
            String palletCode = getPalletCode(stationNo);
            String bcrData = getBcrData(stationNo);

            if (palletCode == null) {
                jobInfoService.updateMemo(jobInfo, "无法获取站台" + stationNo + "的托盘号");
                return false;
            }

            // 更新作业信息
            jobInfo.setPalletCode(palletCode);
            jobInfoService.updateMemo(jobInfo, "获取到站台" + stationNo + "的入库请求，托盘号：" + palletCode + "，BCR数据：" + bcrData);

            return true;
        } catch (Exception e) {
            jobInfoService.updateMemo(jobInfo, "处理站台" + stationNo + "入库请求失败：" + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 处理站台的出库完成
     * @param jobInfo 作业信息
     * @param stationNo 站台号 (1或2)
     * @return 处理是否成功
     */
    public Boolean handleOutComplete(JobInfo jobInfo, int stationNo) {
        try {
            // 设置允许出库
            boolean result = setAllowOut(stationNo, true);
            if (result) {
                jobInfoService.updateMemo(jobInfo, "已通知站台" + stationNo + "允许出库");
            } else {
                jobInfoService.updateMemo(jobInfo, "通知站台" + stationNo + "允许出库失败");
            }
            return result;
        } catch (Exception e) {
            jobInfoService.updateMemo(jobInfo, "处理站台" + stationNo + "出库完成失败：" + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 根据站台号获取设备编码
     * @param stationNo 站台号 (1或2)
     * @return 设备编码
     */
    private String getStationDeviceCode(int stationNo) {
        if (stationNo == 1) {
            return STATION_1_PREFIX;
        } else if (stationNo == 2) {
            return STATION_2_PREFIX;
        } else {
            throw new IllegalArgumentException("无效的站台号：" + stationNo);
        }
    }
}
