<script>
import Layout from "../layouts/main";
import appConfig from "@/app.config";

import Stat from "@/components/widgets/widget-stat";

import { sparklineChartData} from "./data";
import { getAbnormalTempData, getAbnormalHumiData, getAvgHum, getAvgTemp, getCount, getIotCount, getLatestDataById } from "../../api";

export default {
  page: {
    title: "系统首页",
    meta: [{ name: "description", content: appConfig.description }]
  },
  components: {
    Layout,
    Stat,
  },
  data() {
    return {
      sparklineChartData: sparklineChartData,
      statData: [
        {
          title: "连接设备数目",
          image: require("@/assets/images/services-icon/01.png"),
          value: "-",
          subText: " ☆ ",
          arrowup: true,
          color: "success"
        },
        {
          title: "数据总条目数",
          image: require("@/assets/images/services-icon/02.png"),
          value: "-",
          subText: " ☆ ",
          arrowup: true,
          color: "danger"
        },
        {
          title: "平均温度",
          image: require("@/assets/images/services-icon/04.png"),
          value: "- ℃",
          subText: " ☆ ",
          arrowup: false,
          color: "warning"
        },
        {
          title: "平均湿度",
          image: require("@/assets/images/services-icon/04.png"),
          value: "- %",
          subText: " ☆ ",
          arrowup: true,
          color: "warning"
        },
        {
          title: "异常温度条数",
          image: require("@/assets/images/services-icon/03.png"),
          value: "-",
          subText: " ☆ ",
          arrowup: true,
          color: "warning"
        },
        {
          title: "异常湿度条数",
          image: require("@/assets/images/services-icon/03.png"),
          value: "-",
          subText: " ☆ ",
          arrowup: true,
          color: "warning"
        },
      ],
      sparkChartTemperature: [],
      sparkChartHumidity: [],
      currentHumidity: '-',
      currentTemperature: '-',  

      iotSelected:'iot1'
    };  
  },
  methods: {

  },
  mounted() {
    getIotCount().then(({data})=> {
      this.statData[0].value = data.data
    })
    getCount().then(({ data }) => {
      this.statData[1].value = data.data
    })
    getAvgTemp().then(({ data }) => {
      this.statData[2].value = parseFloat(data.data).toFixed(2) + '℃'
    })
    getAvgHum().then(({ data }) => {
      this.statData[3].value = parseFloat(data.data).toFixed(2) + '%'
    })
    getLatestDataById(1).then(({ data }) => {
      let res = data.data
      res.forEach(element => {
        this.sparkChartTemperature.push(parseFloat(element.temperature))
        this.sparkChartHumidity.push(parseFloat(element.humidity))
      });
      this.currentHumidity=parseFloat(res[res.length-1].humidity).toFixed(2)
      this.currentTemperature=parseFloat(res[res.length-1].temperature).toFixed(2)
    })
    getAbnormalTempData().then(({ data }) => {
      let res = data.data
      this.statData[4].value = res
    })
    getAbnormalHumiData().then(({ data }) => {
      let res = data.data
      this.statData[5].value = res
    })
    // 启动定时器或设置 WebSocket，以更新温度数据
    setInterval(() => {
      getIotCount().then(({data})=> {
        this.statData[0].value = data.data
      })
      getCount().then(({ data }) => {
        this.statData[1].value = data.data
      })
      getAvgTemp().then(({ data }) => {
        this.statData[2].value = parseFloat(data.data).toFixed(2) + '℃'
      })
      getAvgHum().then(({ data }) => {
        this.statData[3].value = parseFloat(data.data).toFixed(2) + '%'
      })
      getLatestDataById((this.iotSelected =='iot1')?1:2).then(({ data }) => {
        let res = data.data
        res.forEach(element => {
          this.sparkChartTemperature.push(parseFloat(element.temperature))
          this.sparkChartHumidity.push(parseFloat(element.humidity))
        });
        this.currentHumidity=parseFloat(res[res.length-1].humidity).toFixed(2)
        this.currentTemperature=parseFloat(res[res.length-1].temperature).toFixed(2)
      })
      
      getAbnormalTempData().then(({ data }) => {
        let res = data.data
        this.statData[4].value = res
      })
      getAbnormalHumiData().then(({ data }) => {
        let res = data.data
        this.statData[5].value = res
      })
    }, 3000); // 每 3 秒更新一次
  },
  watch:{
    iotSelected(newSel) {
      getLatestDataById(newSel=='iot1'?1:2).then(({ data }) => {
        let res = data.data
        res.forEach(element => {
          this.sparkChartTemperature.push(parseFloat(element.temperature))
          this.sparkChartHumidity.push(parseFloat(element.humidity))
        });
        this.currentHumidity=parseFloat(res[res.length-1].humidity).toFixed(2)
        this.currentTemperature=parseFloat(res[res.length-1].temperature).toFixed(2)
      })
    }
  }
};

// iotSelect.addEventListener("change",function() {
//   alert(this.value)
// })
</script>

<template>
  <Layout>
    <!-- start page title -->
    <div class="row align-items-center">
      <div class="col-sm-6">
        <div class="page-title-box">
          <h4 class="font-size-22">系统主页</h4>
          <ol class="breadcrumb mb-0">
            <li class="breadcrumb-item active font-size-15">欢迎来到智能温湿度监测系统</li>
          </ol>
        </div>
      </div>

      <div class="col-sm-6">
        <div class="float-end d-none d-md-block">
          <b-dropdown right variant="primary" menu-class="dropdown-menu-end">
            <template v-slot:button-content>
              <i class="mdi mdi-cog me-2"></i> 设置
            </template>
            <b-dropdown-item>界面设置</b-dropdown-item>
            <b-dropdown-item>状态设置</b-dropdown-item>
            <b-dropdown-item>温湿度阈值设置</b-dropdown-item>
          </b-dropdown>
        </div>
      </div>
    </div>

    <!-- end page title -->
    <div class="row">
      <div class="col-xl-3 col-md-6" v-for="stat of statData" :key="stat.icon">
        <Stat :title="stat.title" :image="stat.image" :subText="stat.subText" :value="stat.value" :color="stat.color" />
      </div>
    </div>
    <!-- end row -->

    <div class="row">

      <div class="col-xl-12">
        <div class="card">
          <div class="card-body">

            <div>
              <h4 class="card-title mb-4">数据概览</h4>
            </div>

            <div class="mb-3 row">
              <label class="col-sm-1 col-form-label">设备选择</label>
              <div class="col-sm-2">
                <select class="form-select" v-model="iotSelected">
                  <option selected value="iot1">设备#1</option>
                  <option value="iot2">设备#2</option>
                </select>
              </div>
            </div>

            <div class="wid-peity mb-4">
              <div class="row">

                <div class="col-md-1">
                  <div>
                    <p class="text-muted">实时温度</p>
                    <h5 class="mb-4">{{ this.currentTemperature }} ℃</h5>
                  </div>
                </div>

                <div class="col-md-10">
                  <div class="mb-4">
                    <!-- sparkline chart -->
                    <sparkline height="65" width="1000">
                      <sparklineLine :data="sparkChartTemperature" :limit="sparklineChartData.sparkChart1.length"
                        :styles="sparklineChartData.style" :refLineStyles="sparklineChartData.reflineStyle" />
                    </sparkline>
                  </div>
                </div>

              </div>
            </div>

            <div class="wid-peity mb-4">
              <div class="row">
                <div class="col-md-1">
                  <div>
                    <p class="text-muted">实时湿度</p>
                    <h5 class="mb-4">{{ this.currentHumidity }} %</h5>
                  </div>
                </div>
                <div class="col-md-6">
                  <div class="mb-4">
                    <sparkline height="65" width="1000">
                      <sparklineLine :data="sparkChartHumidity" :limit="sparklineChartData.sparkChart2.length"
                        :styles="sparklineChartData.style" :refLineStyles="sparklineChartData.reflineStyle" />
                    </sparkline>
                  </div>
                </div>
              </div>
            </div>


          </div>
        </div>
      </div>
    </div>
    <!-- end row -->


  </Layout>
</template>