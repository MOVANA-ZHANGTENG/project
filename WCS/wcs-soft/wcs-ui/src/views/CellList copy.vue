<template>
  <div class="container">
    <el-row>
      <el-col :span="4">
        <el-card style="
            width: 95%;
            height: 95vh;
            background-color: #ffffff;
            margin-left: 5%;
            margin-top: 3vh;
            padding-top: 2vh;
          ">

          <!-- <el-form :inline="true" ref="ware" :model="wareInfo" class="demo-form-inline">
            <el-form-item label="仓库名称"> -->
          <el-select v-model="wareCode" placeholder="请选择仓库">
            <el-option v-for="item in wareInfos" :key="item.code" :label="item.name" :value="item.code">
            </el-option>
          </el-select>
          <!-- </el-form-item>
          </el-form> -->

          <!-- 设备状态 -->
          <div>
            <div class="deviceState">左提升机</div>
            <div class="deviceState">左提升机</div>
            <div class="deviceState">四向车#1</div>
            <div class="deviceState">四向车#2</div>
            <div class="deviceState">四向车#3</div>
            <div class="deviceState">四向车#4</div>
            <div class="deviceState">四向车#5</div>
            <div class="deviceState">四向车#6</div>
          </div>

          <!-- 层数选择 -->
          <div style="width: 100%; margin-top: 50px">
            <el-radio style="
                display: block;
                width: 90%;
                margin-left: 5%;
                margin-top: 5px;
              " v-model="z" :label="1" border>一层</el-radio>
            <el-radio style="
                display: block;
                width: 90%;
                margin-left: 5%;
                margin-top: 5px;
              " v-model="z" :label="2" border>二层</el-radio>
            <el-radio style="
                display: block;
                width: 90%;
                margin-left: 5%;
                margin-top: 5px;
              " v-model="z" :label="3" border>三层</el-radio>
            <el-radio style="
                display: block;
                width: 90%;
                margin-left: 5%;
                margin-top: 5px;
              " v-model="z" :label="4" border>四层</el-radio>
            <el-radio style="
                display: block;
                width: 90%;
                margin-left: 5%;
                margin-top: 5px;
              " v-model="z" :label="5" border>五层</el-radio>
          </div>
        </el-card>
      </el-col>
      <el-col :span="15">
        <!-- 货位状态示例 -->
        <el-card style="
            width: 95%;
            height: 7vh;
            background-color: #ffffff;
            margin-left: 1%;
            margin-top: 3vh;
            padding: 0.3vh 0px 10px 10px;
          ">
          <div style="float: left; margin-left: 25px; margin-top: 5px">
            无货
          </div>
          <div class="kongCell" style="
              float: left;
              border-style: solid;
              width: 3%;
              height: 2.3vh;
              margin-top: 2px;
              margin-left: 2px;
              margin-top: 7px;
            "></div>

          <div style="float: left; margin-left: 25px; margin-top: 5px">
            有货
          </div>
          <div class="noKongCell" style="
              float: left;
              border-style: solid;
              width: 3%;
              height: 2.3vh;
              margin-top: 2px;
              margin-left: 2px;
              margin-top: 7px;
            "></div>

          <div style="float: left; margin-left: 25px; margin-top: 5px">
            入库中
          </div>
          <div class="inCell" style="
              float: left;
              border-style: solid;
              width: 3%;
              height: 2.3vh;
              margin-top: 2px;
              margin-left: 2px;
              margin-top: 7px;
            "></div>

          <div style="float: left; margin-left: 25px; margin-top: 5px">
            出库中
          </div>
          <div class="outCell" style="
              float: left;
              border-style: solid;
              width: 3%;
              height: 2.3vh;
              margin-top: 2px;
              margin-left: 2px;
              margin-top: 7px;
            "></div>
          <div style="float: left; margin-left: 25px; margin-top: 5px">
            禁用
          </div>
          <div style="
              float: left;

              width: 3%;
              height: 2.3vh;
              margin-top: 2px;
              margin-left: 2px;
              margin-top: 7px;
            ">
            <i style="color: red" class="el-icon-close"></i>
          </div>
        </el-card>
        <!-- 货位展示 -->
        <el-card v-loading="loading" style="
            width: 95%;
            height: 40vh;
            background-color: #ffffff;
            margin-left: 1%;
            margin-top: 0vh;
            padding: 10px 0px 10px 10px;
          ">
          <div style="margin-left: 5%">
            <!-- x -->
            <el-row>
              <el-col style="float: left; width: 3%">
                <div v-for="y in totalY" style="height: 3vh">{{ 11 - y }}</div>
              </el-col>
              <el-col v-for="x in totalX" style="float: left; width: 3%">
                <!-- y -->
                <div v-for="y in totalY" style="
                    height: 3vh;
                    border-style: solid;
                    border-color: #f2f6fc;
                  ">
                  <div class="noCell" :id="totalX + 1 - x + '-' + (11 - y)"
                    @click="findByXYZ(totalX + 1 - x, 11 - y, z)" style="
                      border-width: 3px;
                      width: 100%;
                      height: 100%;
                      font-size: 2vh;
                      color: red;
                      line-height: 2.5vh;
                      text-align: center;
                    ">
                    <i style="margin-top: -6vh" class="el-icon-success" v-if="
                      cellInfo.z == z &&
                      cellInfo.x == totalX + 1 - x &&
                      cellInfo.y == 11 - y
                    "></i>
                    <i v-else class="el-icon-close"></i>
                  </div>
                </div>
                <div style="font-size: 8px; padding-left: 5px">
                  {{ totalX + 1 - x }}
                </div>
              </el-col>
            </el-row>
          </div>
        </el-card>
        <!-- 库存 -->
        <el-card v-loading="invenLoading" style="
            width: 95%;

            background-color: #ffffff;
            margin-left: 1%;
            margin-top: 1vh;
            padding: 10px 0px 0px 10px;
          ">
          <!-- 原料库存 -->
          <el-table v-if="inventoryList != null && inventoryList.length > 0" v-loading="loading" :data="inventoryList">
            <!-- <el-table-column type="selection" width="55" align="center" />
            <el-table-column
              label="ID"
              width="55"
              align="center"
              prop="inventoryId"
            /> -->
            <el-table-column label="物料编码" align="center" prop="itemCode" />
            <!-- <el-table-column label="LOT" align="center" prop="lotNo" /> -->
            <el-table-column label="物料名称" align="center" prop="itemName" />
            <el-table-column label="入库日期" align="center" prop="inDate" width="100">
              <template slot-scope="scope">
                <span>{{ parseTime(scope.row.inDate, "{y}-{m}-{d}") }}</span>
              </template>
            </el-table-column>
            <el-table-column label="批次" align="center" prop="batchNo" />
            <el-table-column label="托盘号" align="center" prop="palletCode" />
            <el-table-column label="位置" align="center" prop="cellCode" />
            <el-table-column label="质量状态" align="center" prop="qcState">
              <template slot-scope="scope">
                <span style="color: #e6a23c" v-if="scope.row.qcState == 1">待确认
                </span>
                <span style="color: #67c23a" v-if="scope.row.qcState == 0">合格
                </span>
                <!-- <span style="color: #f56c6c" v-if="scope.row.qcState == -1"
            >不合格
          </span> -->
              </template>
            </el-table-column>
            <el-table-column label="库存类型" align="center" prop="inType">
              <template slot-scope="scope">
                <span v-for="dict in goodsStatus" v-if="scope.row.goodsStatus == dict.value">{{ dict.label }}</span>
              </template>
            </el-table-column>
            <el-table-column label="数量（袋）" align="center" prop="quantity" />
            <el-table-column label="分配数" align="center" prop="allotQuantity" />
            <el-table-column label="重量（kg）" align="center" prop="weight" />
            <!-- <el-table-column label="单位" align="center" prop="unitCode" /> -->
            <el-table-column label=" 货主名称" align="center" prop="deptName">
            </el-table-column>
          </el-table>
          <!-- 金型库存 -->
          <el-table v-if="MetalModeList != null && MetalModeList.length > 0" :data="MetalModeList">
            <el-table-column label="金型条形码" align="center" prop="code" />
            <el-table-column label="部品名" align="center" prop="departName" />
            <el-table-column label="重量(kg)" align="center" prop="weight" />
            <el-table-column label="长(mm)*宽(mm)*高(mm)" align="center" prop="length">
              <template slot-scope="scope">
                <span>{{ scope.row.length }}*{{ scope.row.width }}*{{
                  scope.row.high
                }}</span>
              </template>
            </el-table-column>
            <el-table-column label="组合编号" align="center" prop="groupCode" />
            <el-table-column label="托盘号" align="center" prop="palletCode" />
            <el-table-column label="货位号" align="center" prop="cellCode" />
            <el-table-column label="机种" align="center" prop="models" />
            <el-table-column label="资产号" align="center" prop="assetNumber" />
            <el-table-column label="金型类型" align="center" prop="metalType" />
            <!-- <el-table-column label="组合标记" align="center" prop="mark" /> -->
            <el-table-column label="是否高频" align="center">
              <template slot-scope="scope">
                <div v-if="scope.row.highFrequencyMark == 1">是</div>
                <div v-if="scope.row.highFrequencyMark == 0">否</div>
              </template>
            </el-table-column>
            <el-table-column label="是否有额外宽度" align="center">
              <template slot-scope="scope">
                <div v-if="scope.row.extraWidth == 1">是</div>
                <div v-if="scope.row.extraWidth == 0">否</div>
              </template>
            </el-table-column>

            <el-table-column label="部品号" align="center" prop="departCode">
              <template slot-scope="scope">
                <div v-for="code in scope.row.list">{{ code }}</div>
              </template>
            </el-table-column>
            <el-table-column label=" 货主名称" align="center" prop="deptName">
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>
      <el-col :span="5">
        <el-card style="margin-top: 3vh">
          <el-form v-loading="cellLoading" v-if="cellInfo.cellCode != null" ref="form" :model="cellInfo"
            label-width="80px">
            <el-form-item label="货位编码">
              <span>{{ cellInfo.cellCode }}</span>
            </el-form-item>
            <el-form-item label="托盘编码">
              <span>{{ cellInfo.palletCode }}</span>
            </el-form-item>

            <el-form-item label="层">
              <span>{{ cellInfo.z }}</span>
              <!-- <el-input :disabled="true" v-model="cellInfo.z"></el-input> -->
            </el-form-item>
            <el-form-item label="通道编码">
              <span>{{ cellInfo.lineCode }}</span>
              <!-- <el-input :disabled="true" v-model="cellInfo.lineCode"></el-input> -->
            </el-form-item>
            <el-form-item label="库存状态">
              <el-select v-model="cellInfo.invenState" placeholder="请选择库存状态">
                <el-option v-for="item in invenStates" :key="item.value" :label="item.label" :value="item.value">
                </el-option>
              </el-select>
            </el-form-item>
            <el-form-item label="任务状态">
              <el-input :disabled="true" v-model="cellInfo.taskState"></el-input>
            </el-form-item>
            <el-form-item label="是否禁用">
              <el-select v-model="cellInfo.disableState" placeholder="请选择禁用状态">
                <el-option v-for="item in disableStates" :key="item.value" :label="item.label" :value="item.value">
                </el-option>
              </el-select>
            </el-form-item>
            <el-form-item label="货区编码：">
              <el-select v-model="cellInfo.areaCode" placeholder="请选择库区">
                <el-option v-for="area in areaInfos" :label="area.name" :value="area.code"></el-option>
              </el-select>
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="updateAreaCode(cellInfo)">修改提交</el-button>
            </el-form-item>
          </el-form></el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script>
import request from "@/utils/request";
export default {
  name: "container",
  components: {},
  data() {
    return {
      MetalModeList: [],
      goodsStatus: [],
      inventoryList: [],
      cellFind: {
        x: null,
        y: null,
        z: null,
      },
      totalX: 29,
      totalY: 14,
      id: "",
      loading: false,
      cellLoading: false,

      invenLoading: false,
      cellList: [],
      z: 0,
      cellInfo: {},
      areaInfos: {},
      disableStates: [
        { value: 1, label: "禁用" },
        { value: 0, label: "不禁用" },
      ],
      invenStates: [
        { value: 1, label: "有货" },
        { value: 0, label: "无货" },
      ],
    };
  },
  watch: {
    z: {
      handler(newVal, oldVal) {
        this.getCellInfos(newVal);
      },
    },
  },
  created() {
    this.z = 1;
    this.getCellInfos(1);
  },

  methods: {
    findInventorys(cellCode) {
      this.invenLoading = true;
      listInventory({ cellCode: cellCode }).then((response) => {
        this.invenLoading = false;
        if (response.code == 200) {
          this.inventoryList = response.rows;
        } else {
          this.$modal.msgError(response.msg);
        }
      });
    },
    findModels(cellCode) {
      this.invenLoading = true;
      listMetalMode({ cellCode: cellCode }).then((response) => {
        this.invenLoading = false;
        if (response.code == 200) {
          this.MetalModeList = response.rows;
        } else {
          this.$modal.msgError(response.msg);
        }
      });
    },
    getCellInfos(z) {
      var that = this;
      that.loading = true;
      // that.cellInfo = {};
      for (var x = 1; x <= this.totalX; x++) {
        for (var y = 1; y <= this.totalY; y++) {
          var div = document.getElementById(x + "-" + y);
          if (div != null) {
            div.className = "noCell";
          }
        }
      }

      request({
        url: "/wcs-base/CellInfo/list",
        method: "get",
        params: { z: z },
      }).then((response) => {
        that.loading = false;
        console.log(that.loading);
        if (response.code == 200) {
          that.loading = false;
          that.cellList = response.rows;
          that.cellList.forEach((cell) => {
            if (cell.cellCode == "SZ01290101") {
              debugger;
            }
            if (cell.disableState == 1) {
              if (cell.invenState == 0) {
                var div = document.getElementById(cell.x + "-" + cell.y);
                div.className = "kongCell disableCell";
              }
              if (cell.invenState == 1) {
                var div = document.getElementById(cell.x + "-" + cell.y);
                div.className = "noKongCell disableCell";
              }
            } else {
              if (cell.invenState == 0) {
                if (cell.taskState == 0) {
                  var div = document.getElementById(cell.x + "-" + cell.y);
                  div.className = "kongCell";
                }
                if (cell.taskState > 0) {
                  document.getElementById(cell.x + "-" + cell.y).className =
                    "inCell";
                }
              }
              if (cell.invenState == 1) {
                if (cell.taskState == 0) {
                  var div = document.getElementById(cell.x + "-" + cell.y);
                  div.className = "noKongCell";
                }
                if (cell.taskState > 0) {
                  debugger;
                  document.getElementById(cell.x + "-" + cell.y).className =
                    "outCell";
                }
              }
            }
          });
        }
        that.loading = false;
      });
    },

    findByXYZ(x, y, z) {
      this.cellLoading = true;
      request({
        url: "/wcs-base/CellInfo/findByXYZ",
        method: "get",
        params: { x: x, y: y, z: z },
      }).then((response) => {
        this.cellLoading = false;
        if (response.code == 200) {
          this.cellFind = {
            x: x,
            y: y,
            z: z,
          };
          if (response.data != null) {
            this.cellInfo = response.data;
            if (this.cellInfo != null) {
              this.findInventorys(this.cellInfo.cellCode);
              this.findModels(this.cellInfo.cellCode);
            }
          }
        }
      });
    },

    updateAreaCode(cellInfo) {
      // if (cellInfo.palletCode != null && cellInfo.invenState == 0) {
      //   this.$modal.msgError("该货位有托盘，无法将库存状态设置未无货");
      //   return;
      // }
      request({
        url: "/wcs-base/lineInfo/updateAreaCode",
        method: "get",
        params: {
          lineCode: cellInfo.lineCode,
          areaCode: cellInfo.areaCode,
          cellCode: cellInfo.cellCode,
          disableState: cellInfo.disableState,
          invenState: cellInfo.invenState,
        },
      }).then((response) => {
        debugger;

        if (response.code == 200) {
          this.$modal.msgSuccess("修改成功");
          this.getCellInfos(this.z);
          this.findByXYZ(this.cellFind, x, this.cellFind.y, this.cellFind.z);
        } else {
          this.$modal.msgError(response.msg);
        }
      });
    },
  },
};

function init() {
  if (window.goSamples) goSamples(); // init for these samples -- you don't need to call this
  var $ = go.GraphObject.make; // for conciseness in defining templates
  myDiagram = $(
    go.Diagram,
    "myDiagramDiv", // create a Diagram for the DIV HTML element
    {
      "undoManager.isEnabled": true, // enable undo & redo
    }
  );
  // define a simple Node template
  myDiagram.nodeTemplate = $(
    go.Node,
    "Auto", // the Shape will go around the TextBlock
    $(
      go.Shape,
      "RoundedRectangle",
      { strokeWidth: 0, fill: "white" },
      // Shape.fill is bound to Node.data.color
      new go.Binding("fill", "color")
    ),
    $(
      go.TextBlock,
      { margin: 8, font: "bold 14px sans-serif", stroke: "#333" }, // Specify a margin to add some room around the text
      // TextBlock.text is bound to Node.data.key
      new go.Binding("text", "key")
    )
  );
  // but use the default Link template, by not setting Diagram.linkTemplate
  // create the model data that will be represented by Nodes and Links
  myDiagram.model = new go.GraphLinksModel(
    [
      { key: "Alpha", color: "lightblue" },
      { key: "Beta", color: "orange" },
      { key: "Gamma", color: "lightgreen" },
      { key: "Delta", color: "pink" },
    ],
    [
      { from: "Alpha", to: "Beta" },
      { from: "Alpha", to: "Gamma" },
      { from: "Beta", to: "Beta" },
      { from: "Gamma", to: "Delta" },
      { from: "Delta", to: "Alpha" },
    ]
  );
}
if (window.init) {
  init();
}
</script>

<style lang="scss" scoped>
.container {
  background-color: #f2f6fc;
  height: 100vh;

  .deviceState {
    color: #f2f6fc;
    width: 80%;
    margin-left: 10%;
    height: 30px;
    margin-top: 20px;
    background-color: #67c23a;
    text-align: center;
    line-height: 30px;
  }

  .kongCell {
    color: #dcdfe6;
    background-color: #dcdfe6;

    .el-icon-close {
      display: none;
    }
  }

  .noKongCell {
    color: #409eff;
    background-color: #409eff;

    .el-icon-close {
      display: none;
    }
  }

  .inCell {
    color: #f56c6c;
    background-color: #f56c6c;

    .el-icon-close {
      display: none;
    }
  }

  .outCell {
    color: #e6a23c;
    background-color: #e6a23c;

    .el-icon-close {
      display: none;
    }
  }

  .noCell {
    .el-icon-close {
      display: none;
    }
  }

  .disableCell {
    .el-icon-close {
      display: contents;
    }
  }
}

.el-header,
.el-footer {
  background-color: #b3c0d1;
  color: #333;
  text-align: center;
  line-height: 60px;
}

.el-aside {
  background-color: #d3dce6;
  color: #333;
  text-align: center;
  line-height: 200px;
}

.el-main {
  background-color: #e9eef3;
  color: #333;
  text-align: center;
  line-height: 160px;
}

body>.el-container {
  margin-bottom: 40px;
}

.el-container:nth-child(5) .el-aside,
.el-container:nth-child(6) .el-aside {
  line-height: 260px;
}

.el-container:nth-child(7) .el-aside {
  line-height: 320px;
}
</style>
