<template>
  <div class="icons-container">
    <el-card v-loading="loading"
             class="box-card">
      <div slot="header"
           class="clearfix">
        <span>单据履历 </span>
      </div>
      <el-timeline>
        <el-timeline-item v-for="billRecord in billRecords"
                          :timestamp="'【'+billRecord.createUserName+'】'+billRecord.createTime"
                          placement="top">
          <p>{{billRecord.content}}</p>
        </el-timeline-item>
      </el-timeline>
    </el-card>
  </div>
</template>

<script>
import { listBill_record, getBill_record, delBill_record, addBill_record, updateBill_record } from "@/api/wcs-inventory/bill_record";
export default {
  name: 'Icons',
  props: {
    billNo: {
      type: String,
      default: "-1"
    },
  },
  data () {
    return {
      loading: false,
      billRecords: [],
    }
  },
  watch: {
    billNo () {

      this.getBillRecords();
    }

  },
  methods: {
    getBillRecords () {

      if (this.billNo != "-1" && this.billNo != "" && this.billNo != null) {
        this.loading = true;
        listBill_record({ billNo: this.billNo }).then(response => {
          this.billRecords = response.rows;
          this.loading = false;
        });
      }
    }

  }
}
</script>

<style lang="scss" scoped>
.icons-container {
}
</style>
