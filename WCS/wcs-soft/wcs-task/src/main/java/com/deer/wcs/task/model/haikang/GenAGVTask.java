package com.deer.wcs.task.model.haikang;

import lombok.Data;

/**
 * @description:
 * 接口名: genAgvSchedulingTask
 * 功能说明: 上层系统平台发送调度请求, RCS 通过请求参数, 生成调度 AGV 任务单。生成所有车型的搬运任务。
 * 接口协议: REST
 * 提供方: RCS-2000
 * 必填： reqCode /taskType
 * @author:zfj
 * @date:2024/7/16 22:04
 */
@Data
public class GenAGVTask {
    //必填 请求编号，每个请求都要一个唯一编号， 同一个请求重复提交， 使用同一编号
    private String reqCode;
    // 请求时间截
    private String reqTime;
    //客户端编号，如 PDA，HCWMS 等
    private String clientCode;
    //令牌号, 由调度系统颁发
    private String tokenCode;
    //任务类型，与在 RCS-2000 端配置的主任务类型编号一致。
    //内置任务类型:
    //厂内货架搬运: F01
    //厂内货架空满交换: F02
    //辊筒搬运接驳:F03
    //厂内货架出库 AGV 待命:F04
    //旋转货架: F05
    //厂内电梯任务: F06
    //以下为叉车专用任务类型
    //高位货架到工作台: F11
    //工作台到高位货架: F12
    //巷道到工作台: F13
    //工作台到巷道: F14
    //高位货架到工作台(接驳) : F15
    //工作台到高位货架 (接驳) : F16
    //巷道到工作台(接驳) : F17
    //工作台到巷道(接驳) : F18
    //叉车电梯主任务: F20
    private String taskTyp;
    //容器类型（叉车/CTU 专用）
    private String ctnrTyp;
    //容器编号（叉车/CTU 专用）
    private String ctnrCode;
    //任务模式
    //0-普通 move
    //1-出库 move
    //2-入库 move
    //3-移库 move
    //说明：
    //1>任务模板中也可以配置模式，如果接口传了则优先使用接口传入的值
    //2>出库 move 模式不能被打断，普通move和入库move可以被打断。
    //出库 move 执行后必须执行入库move 或者移库 move，若后续未带move 标识，则系统不会执行，会将任务挂起。
    //3>支持潜伏、叉车、CTU 场景
    private String taskMode;
    //工作位，一般为机台或工作台位置，与 RCS-2000 端配置的位置名称一致, 工作位名称为字母\数字\或组合, 不超过 32
    private String wbCode;
    //位置路径：AGV 关键路径位置集合，与任务类型中模板配置的位置路径一一对应。待现场地图部署、配置完成后可获取。
    // positionCode:位置编号, 单个编号不超过 64 位
    //type:位置类型说明:
    //00 表示：位置编号
    //01 表示：物料批次号
    //02 表示：策略编号（含多个区域）
    //如：第一个区域放不下, 可以放第二个区域
    //03 表示：货架编号，通过货架编号找到货架所在位置
    //04 表示：区域编号，在区域中查找可用位置
    //05 表示：仓位编号（叉车/CTU 专用）
    //06 表示：巷道编号
    //07 表示：容器编号
    //08 表示：巷道策略
    //09 表示：巷道区域
    //10 表示：巷道仓位
    //11 表示：输送线（机台）编号
    //12 表示：CTU 工作台（梳齿工作站）编号
    //13 表示：搬运巷道指定货架出库
    private PositionPath[] positionCodePath;
    //货架编号，不指定货架可以为空
    private String podCode;
    //“180”,”0”,”90”,”-90” 分
    //别 对 应 地 图 的 ” 左 ”,” 右 ”,”
    //上”,”下” ，不指定方向可以为
    //空；如果终点不是工作台或工作台
    //未配置操作方向则货架拣货方向
    //podDir 作为终点方向；如果工作台
    //配有操作方向，则系统会根据货架
    //拣货方向 podDir 和工作台操作方
    //向计算货架终点方向
    private String podDir;
    //货架类型, 传空时表示随机找个货
    //架
    //找空货架传参方式如下：
    //-1: 代表不关心货架类型, 找到空
    //货架即可. -2: 代表从工作位获取关联货架类
    //型, 如果未配置, 只找空货架. 货架类型编号: 只找该货架类型的
    //空货架
    private String podTyp;
    //物料批次或货架上的物料唯一编码, 生成任务单时,货架与物料直接绑定
    //时使用. （通过同时传 podCode 和
    //materialLot 来 绑 定 或 通 过
    //wbCode 找 到 位 置 上 的 货 架 和
    //materialLot 来绑定）
    //如果是区域/策略内查找货架，可以
    //匹配对应物料批次的货架，支持以
    //下值：
    //0：区域/策略内查找空货架
    //1：区域/策略内查找满货架
    //其他值：查找指定物料批次对应的
    //货架
    //巷道任务通过该字段传入特征值
    private String materialLot;
    //优先级，从（1~127）级，最大优
    //先级最高。为空时，采用任务模板
    //的优先级
    private String priority;
    //任务单号,选填, 不填系统自动生
    //成，UUID 小于等于 64
    private String taskCode;
    //AGV 编号，填写表示指定某一编号
    //的 AGV 执行该任务
    private String agvCode;
    //组编号
    //CTU 场景下用于按组出库，同组任
    //务优先拼车。 如业务需要任务组间
    //或组内按顺序出库，则需调用料箱
    //顺序出库（CTU）接口。
    //潜伏式场景下，通过组号来管理顺
    //序出库的顺序，组号小的优先出库
    private String groupId;
    //设备类型
    private String agvTyp;
    //区域/策略中挑选货架以及根据物
    //料批次挑选货架时的先进先出规
    //则，支持以下 4 个值：
    //1：按照货架到达储位的时间顺序，
    //先进先出
    //2：按照货架到达储位的时间顺序，
    //先进后出
    //9：按照货架绑定物料批次的时间顺
    //序，先进先出
    //10：按照货架绑定物料批次的时间
    //顺序，先进后出
    private String positionSelStrategy;
    //自定义字段.JSON 格式
    private String data;
}

