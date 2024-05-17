<script>
import Layout from "../../layouts/main";
import PageHeader from "@/components/page-header";
import appConfig from "@/app.config";
import { getAllData, getAllIot, getData } from '../../../api';
import * as XLSX from 'xlsx';



/**
 * 历史信息组件
 */
export default {
  page: {
    title: "历史数据",
    meta: [{ name: "description", content: appConfig.description }]
  },
  components: { Layout, PageHeader },
  data() {
    return {
      title: "历史数据",
      items: [
        {
          text: "温湿度监测系统",
          href: "/"
        },
        {
          text: "历史数据",
          active: true
        }
      ],
      totalRows: 1,
      currentPage: 1,
      perPage: 10,
      pageOptions: [10, 25, 50, 100],
      filter: null,
      filterOn: [],
      sortBy: "age",
      sortDesc: false,
      fields: [
        { key: "设备ID", sortable: true },
        { key: "时间", sortable: true },
        { key: "温度", sortable: true },
        { key: "湿度", sortable: true }
      ],
      tableData: [
      ]
    };
  },
  computed: {
    /**
     * Total no. of records
     */
    rows() {
      return this.tableData.length;
    }
  },
  mounted() {
    // Set the initial number of items
    this.totalRows = this.items.length;


    getAllData().then(({ data }) => {
      let records = data.data
      this.tableData = []
      records.forEach(element => {
        this.tableData.push({
          设备ID: element.froms,
          时间: this.timestampToTime(parseInt(element.timestamp)),
          温度: parseFloat(element.temperature).toFixed(2),
          湿度: parseFloat(element.humidity).toFixed(2),
          timestamp: parseInt(element.timestamp)
        })
      });
    })

    setInterval(() => {
      getData().then(({ data }) => {
        let obj = data.data
        let nowRecord = {
          设备ID: obj.froms,
          时间: this.timestampToTime(parseInt(obj.timestamp)),
          温度: parseFloat(obj.temperature).toFixed(2),
          湿度: parseFloat(obj.humidity).toFixed(2),
          timestamp: parseInt(obj.timestamp)
        }
        let newTime = nowRecord.timestamp
        let latestTime = this.tableData[this.tableData.length - 1].timestamp

        if (newTime > latestTime) {
          this.tableData.push(nowRecord)
        }
      });
    }, 3000);


  },
  methods: {
    /**
     * Search the table data with search input
     */
    onFiltered(filteredItems) {
      // Trigger pagination to update the number of buttons/pages due to filtering
      this.totalRows = filteredItems.length;
      this.currentPage = 1;
    },
    timestampToTime(timestamp) {
      var date = new Date(timestamp * 1000); // 注意要将时间戳转为毫秒
      var year = date.getFullYear();
      var month = date.getMonth() + 1; // 月份是从0开始的，所以要加1
      var day = date.getDate();
      var hour = date.getHours();
      var minute = date.getMinutes();
      var second = date.getSeconds();
      var time = year + '-' + month + '-' + day + ' ' + hour + ':' + minute + ':' + second;
      return time;
    },
    outputXLSX() {
      const wb = XLSX.utils.book_new();
      const processedData = []
      this.tableData.forEach(element => {
        processedData.push({
          设备ID: element.设备ID,
          时间: element.时间,
          温度: element.温度,
          湿度: element.湿度,
        })
      });
      window.console.log(processedData)
      const ws = XLSX.utils.json_to_sheet(processedData);
      XLSX.utils.book_append_sheet(wb, ws, "Sheet1");
      XLSX.writeFile(wb, "AllRecords.xlsx");
    },
    outputAbnormalData() {
      getAllIot().then(({ data }) => {
        var dict_maxtemp = {}
        var dict_mintemp = {}
        var dict_maxhumi = {}
        var dict_minhumi = {}
        var iots = data.data
        iots.forEach(element => {
          var id = (element.hardid)
          dict_maxtemp[id] = element.tempmax
          dict_mintemp[id] = element.tempmin
          dict_maxhumi[id] = element.humimax
          dict_minhumi[id] = element.humimin
        });

        const wb = XLSX.utils.book_new();
        const processedData = []
        this.tableData.forEach(element => {
          var curId = parseInt(element.设备ID)
          var curTemp = parseFloat(element.温度)
          var curHumi = parseFloat(element.湿度)
          var items = {
            设备ID: element.设备ID,
            时间: element.时间,
            温度: element.温度,
            湿度: element.湿度,
            状态: '',
            正常温度范围: '',
            正常湿度范围: ''
          }
          items.正常温度范围 = dict_mintemp[curId] + '~' + dict_maxtemp[curId]
          items.正常湿度范围 = dict_minhumi[curId] + '~' + dict_maxhumi[curId]
          if (!(dict_mintemp[curId] <= curTemp && curTemp <= dict_maxtemp[curId])) {
            if (!(dict_minhumi[curId] <= curHumi && curHumi <= dict_maxhumi[curId])) {
              items.状态 = '温湿度异常'
            } else {
              items.状态 = '温度异常'
            }
          } else {
            if (!(dict_minhumi[curId] <= curHumi && curHumi <= dict_maxhumi[curId])) {
              items.状态 = '湿度异常'
            } else {
              items.状态 = ''
            }
          }
          if (items.状态 != '') {
            processedData.push(items)
          }
        });
        
        const ws = XLSX.utils.json_to_sheet(processedData);
        XLSX.utils.book_append_sheet(wb, ws, "Sheet1");
        XLSX.writeFile(wb, "AbnormalRecords.xlsx");
      })
    }
  }
};
</script>

<template>
  <Layout>
    <PageHeader :title="title" :items="items" />

    <div class="row">
      <div class="col-12">
        <div class="card">
          <div class="card-body">
            <h4 class="card-title">数据条目</h4>
            <p class="card-title-desc"></p>
            <div class="row mb-md-2">
              <div class="col-sm-12 col-md-6">
                <div id="tickets-table_length" class="dataTables_length">
                  <label class="d-inline-flex align-items-center">
                    Show
                    <b-form-select class="form-control form-control-sm form-select form-select-sm" v-model="perPage"
                      size="sm" :options="pageOptions"></b-form-select>entries
                  </label>
                </div>
              </div>
              <!-- Search -->
              <div class="col-sm-12 col-md-6">
                <div id="tickets-table_filter" class="dataTables_filter text-md-end">
                  <label class="d-inline-flex align-items-center">
                    Search:
                    <b-form-input v-model="filter" type="search" placeholder="Search..."
                      class="form-control form-control-sm ml-2"></b-form-input>
                  </label>
                </div>
              </div>
              <div style="margin-top: 15px;">
                数据共 {{ this.tableData.length }} 条 &nbsp; <b-button variant="outline-primary"
                  @click="outputXLSX">导出报表</b-button>
                <b-button variant="outline-primary" @click="outputAbnormalData"
                  style="margin-left: 10px;">导出异常数据</b-button>
              </div>
              <!-- End search -->
            </div>

            <!-- Table -->
            <div class="table-responsive mb-0 datatables">
              <b-table :items="tableData" :fields="fields" responsive="sm" :per-page="perPage" :current-page="currentPage"
                :sort-by.sync="sortBy" :sort-desc.sync="sortDesc" :filter="filter" :filter-included-fields="filterOn"
                @filtered="onFiltered"></b-table>
            </div>

            <div class="row">
              <div class="col">
                <div class="dataTables_paginate paging_simple_numbers float-end">
                  <ul class="pagination pagination-rounded mb-0">
                    <!-- pagination -->
                    <b-pagination v-model="currentPage" :total-rows="rows" :per-page="perPage"></b-pagination>
                  </ul>
                </div>
              </div>
            </div>

          </div>
        </div>
      </div>
    </div>
  </Layout>
</template>