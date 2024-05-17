<script>
import Layout from "../../layouts/main";
import PageHeader from "@/components/page-header";
import appConfig from "@/app.config";
import { appendIot, deleteIot, editIot, getAllIot } from '../../../api';

/**
 * 设备管理组件
 */
export default {
  page: {
    title: "设备管理",
    meta: [{ name: "description", content: appConfig.description }]
  },
  components: { Layout, PageHeader },
  data() {
    return {
      title: "设备管理",
      items: [
        {
          text: "温湿度监测系统",
          href: "/"
        },
        {
          text: "设备管理",
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
        { key: "设备型号", sortable: true },
        { key: "设备状态", sortable: true },
        { key: "发送数据量", sortable: true },
        { key: "温度报警上限", sortable: true },
        { key: "温度报警下限", sortable: true },
        { key: "湿度报警上限", sortable: true },
        { key: "湿度报警下限", sortable: true },
        { key: "设备描述", sortable: true },
        { key: "modify", label: "修改", class: "text-center", sortable: false },
        { key: "delete", label: "删除", class: "text-center", sortable: false },
      ],
      tableData: [],
      dialogVisible: false,

      rules: {
        id: [
          { required: false }
        ],
        model: [
          { required: false }
        ],
        status: [
          { required: false }
        ],
        datanum: [
          { required: false }
        ],
        maxtemp: [
          { required: true, message: '请输入温度上限', }
        ],
        mintemp: [
          { required: true, message: '请输入温度下限', }
        ],
        maxhumi: [
          { required: true, message: '请输入湿度上限', }
        ],
        minhumi: [
          { required: true, message: '请输入湿度下限', }
        ],
        desp: [
          { required: false, message: '请输入描述', }
        ]
      },
      form: {
        id: 1,
        model: 'ESP8266',
        status: '已断开',
        datanum: 0,
        maxtemp: 0,
        mintemp: 0,
        maxhumi: 0,
        minhumi: 0,
        desp: ' ',
      },
      sendData: {},
      submitType: 0
    };
  },
  computed: {
    /**
     * Total no. of records
     */
    rows() {
      return this.tableData.length;
    },
    IdEditable () {
      return this.submitType==1;
    }
  },
  mounted() {
    // Set the initial number of items
    this.totalRows = this.items.length;

    getAllIot().then(({ data }) => {
      let iots = data.data
      this.tableData = []
      iots.forEach(iot => {
        this.tableData.push({
          设备ID: iot.hardid,
          设备型号: iot.model,
          设备状态: (iot.state == 1) ? "已连接" : "已断开",
          发送数据量: iot.datanum,
          温度报警上限: iot.tempmax,
          温度报警下限: iot.tempmin,
          湿度报警上限: iot.humimax,
          湿度报警下限: iot.humimin,
          设备描述: iot.description,
          modify: '<button class="btn btn-primary btn-sm" @click="modifyItem(item)">修改</button>',
          delete: '<button class="btn btn-primary btn-sm" @click="deleteItem(item)">删除</button>',
          id: iot.hardid
        })
      });
    })
  },
  methods: {
    /**
     * Search the table data with search input
     */
    getTable() {
      getAllIot().then(({ data }) => {
        let iots = data.data
        this.tableData = []
        iots.forEach(iot => {
          this.tableData.push({
            设备ID: iot.hardid,
            设备型号: iot.model,
            设备状态: (iot.state == 1) ? "已连接" : "已断开",
            发送数据量: iot.datanum,
            温度报警上限: iot.tempmax,
            温度报警下限: iot.tempmin,
            湿度报警上限: iot.humimax,
            湿度报警下限: iot.humimin,
            设备描述: iot.description,
            modify: '<button class="btn btn-primary btn-sm" @click="modifyItem(item)">修改</button>',
            delete: '<button class="btn btn-primary btn-sm" @click="deleteItem(item)">删除</button>',
            id: iot.hardid
          })
        });
      })
    },
    onFiltered(filteredItems) {
      // Trigger pagination to update the number of buttons/pages due to filtering
      this.totalRows = filteredItems.length;
      this.currentPage = 1;
    },
    modifyItem(item) {
      this.submitType = 1
      this.dialogVisible = true
      this.rules.id[0].required = false;
      this.rules.model[0].required = false;
      var newForm = {
        model: item.设备型号,
        status: item.设备状态,
        datanum: item.发送数据量,
        maxtemp: item.温度报警上限,
        mintemp: item.温度报警下限,
        maxhumi: item.湿度报警上限,
        minhumi: item.湿度报警下限,
        desp: item.设备描述,
        id: parseInt(item.设备ID)
      }
      this.form = JSON.parse(JSON.stringify(newForm))
    },
    deleteItem(item) {
      this.$confirm('此操作将永久删除该设备, 是否继续?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        deleteIot(item.id).then(() => {
          this.$message({
            type: 'success',
            message: '删除成功!'
          });
          this.getTable()
        })  
      }).catch(() => {
        this.$message({
          type: 'info',
          message: '已取消删除'
        });
      });
    },
    handleClose() {
      this.$refs.form.resetFields()
      this.dialogVisible = false
    },
    cancel() {
      this.handleClose()
    },
    submit() {
      this.$refs.form.validate((valid) => {
        if (valid) {
          if (this.submitType == 1) {
            //edit
            this.sendData = {}
            this.sendData = {
              hardid: this.form.id,
              tempmax: this.form.maxtemp,
              tempmin: this.form.mintemp,
              humimax: this.form.maxhumi,
              humimin: this.form.minhumi,
              model: this.form.model,
              state: (this.form.status == '已连接') ? true : false,
              description: this.form.desp,
              datanum: this.form.datanum
            }
            //window.console.log(this.sendData)
            editIot(this.sendData).then(() => {
              this.getTable()
            })
          } else {
            //add
            this.sendData = {}
            this.sendData = {
              hardid: this.form.id,
              tempmax: this.form.maxtemp,
              tempmin: this.form.mintemp,
              humimax: this.form.maxhumi,
              humimin: this.form.minhumi,
              model: this.form.model,
              state: (this.form.status == '已连接') ? true : false,
              description: this.form.desp,
              datanum: this.form.datanum
            }
            appendIot(this.sendData).then(({data})=>{
              if(data.code=='500') {
                this.$message({
                  type: 'error',
                  message: '重复的设备ID！'
                });
              }
              this.getTable()
            })
          }
          this.$refs.form.resetFields()
          this.dialogVisible = false;
        }
      })
    },
    addIot() {
      this.submitType = 0
      this.dialogVisible = true;
      this.rules.id[0].required = true;
      this.rules.model[0].required = true;
    } 
  }
};
</script>

<template>
  <Layout>
    <PageHeader :title="title" :items="items" />


    <el-dialog title="提示" :visible.sync="dialogVisible" width="40%" :before-close="handleClose">
      <!-- 表单信息 -->
      <el-form :inline="true" :rules="rules" ref="form" :model="form" label-width="80px">

        <el-form-item label="设备ID" prop="id">
          <el-input v-model="form.id" :disabled="this.IdEditable"></el-input>
        </el-form-item>

        <el-form-item label="设备型号" prop="model">
          <el-input v-model="form.model" :disabled="this.IdEditable"></el-input>
        </el-form-item>

        <el-form-item label="设备状态" prop="status">
          <el-input v-model="form.status" :disabled="true"></el-input>
        </el-form-item>

        <el-form-item label="数据条数" prop="datanum">
          <el-input v-model="form.datanum" :disabled="true"></el-input>
        </el-form-item>

        <el-form-item label="温度下限" prop="mintemp">
          <el-input v-model="form.mintemp" :disabled="false"></el-input>
        </el-form-item>

        <el-form-item label="温度上限" prop="maxtemp">
          <el-input v-model="form.maxtemp" :disabled="false"></el-input>
        </el-form-item>

        <el-form-item label="湿度下限" prop="minhumi">
          <el-input v-model="form.minhumi" :disabled="false"></el-input>
        </el-form-item>

        <el-form-item label="湿度上限" prop="maxhumi">
          <el-input v-model="form.maxhumi" :disabled="false"></el-input>
        </el-form-item>

        <el-form-item label="设备描述" prop="desp">
          <el-input v-model="form.desp" :disabled="false"></el-input>
        </el-form-item>

      </el-form>


      <span slot="footer" class="dialog-footer">
        <el-button @click="cancel">取 消</el-button>
        <el-button type="primary" @click="submit">确 定</el-button>
      </span>
    </el-dialog>


    <div class="row">
      <div class="col-12">
        <div class="card">
          <div class="card-body">
            <h4 class="card-title">设备信息</h4>
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
              <div class="col-sm-12 col-md-6" style="margin-top: 10px;">
                <b-button variant="outline-primary" @click="addIot"
                  style="margin-left: 10px;">添加设备</b-button>
              </div>
              
              <!-- End search -->
            </div>

            <!-- Table -->
            <div class="table-responsive mb-0 datatables">
              <b-table :items="tableData" :fields="fields" responsive="sm" :per-page="perPage" :current-page="currentPage"
                :sort-by.sync="sortBy" :sort-desc.sync="sortDesc" :filter="filter" :filter-included-fields="filterOn"
                @filtered="onFiltered">
                <template v-slot:cell(modify)="data">
                  <div v-html="data.value" @click="modifyItem(data.item)" :data-id="data.item.id"></div>
                </template>
                <template v-slot:cell(delete)="data">
                  <div v-html="data.value" @click="deleteItem(data.item)" :data-id="data.item.id"></div>
                </template>
              </b-table>
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