<script>
import Layout from "../../layouts/main";
import PageHeader from "@/components/page-header";
import appConfig from "@/app.config";


import TemperatureChart from "./TemperatureChart.vue";
import HumidityChart from "./HumidityChart.vue";

import { getAllData, getData, getIotById} from '../../../api'

/**
 * Chartist Chart component
 */
export default {
  page: {
    title: "数据分析",
    meta: [{ name: "description", content: appConfig.description }]
  },
  components: { Layout, PageHeader, TemperatureChart, HumidityChart },
  data() {
    return {
      title: "数据分析",
      items: [
        {
          text: "温湿度监测系统",
          href: "/"
        },
        {
          text: "数据分析",
          active: true
        }
      ],
      currentTemperature1: 0,
      maxTemperature1: 0,
      minTemperature1: 100,
      avgTemperature1: 0,

      currentHumidity1: 0,
      maxHumidity1: 0,
      minHumidity1: 100,
      avgHumidity1: 0,

      datanum1: 0,
      timestamp1: 0,



      currentTemperature2: 0,
      maxTemperature2: 0,
      minTemperature2: 100,
      avgTemperature2: 0,

      currentHumidity2: 0,
      maxHumidity2: 0,
      minHumidity2: 100,
      avgHumidity2: 0,

      datanum2: 0,
      timestamp2: 0,


      maxThresholdTemp1: 0,
      minThresholdTemp1: 0,

      maxThresholdHumi1: 0,
      minThresholdHumi1: 0,

      maxThresholdTemp2: 0,
      minThresholdTemp2: 0,

      maxThresholdHumi2: 0,
      minThresholdHumi2: 0,

    };
  },
  methods: {
    updateTemperature1() {
      var total = this.avgTemperature1 * (this.datanum1 - 1) + this.currentTemperature1
      this.avgTemperature1 = total / this.datanum1
      if (this.currentTemperature1 > this.maxTemperature1) this.maxTemperature1 = this.currentTemperature1
      if (this.currentTemperature1 < this.minTemperature1) this.minTemperature1 = this.currentTemperature1
    },
    updateHumidity1() {
      var total = this.avgHumidity1 * (this.datanum1 - 1) + this.currentHumidity1
      this.avgHumidity1 = total / this.datanum1
      if (this.currentHumidity1 > this.maxHumidity1) this.maxHumidity1 = this.currentHumidity1
      if (this.currentHumidity1 < this.minHumidity1) this.minHumidity1 = this.currentHumidity1
    },
    updateTemperature2() {
      var total = this.avgTemperature2 * (this.datanum2 - 1) + this.currentTemperature2
      this.avgTemperature2 = total / this.datanum2
      if (this.currentTemperature2 > this.maxTemperature2) this.maxTemperature2 = this.currentTemperature2
      if (this.currentTemperature2 < this.minTemperature2) this.minTemperature2 = this.currentTemperature2
    },
    updateHumidity2() {
      var total = this.avgHumidity2 * (this.datanum2 - 1) + this.currentHumidity2
      this.avgHumidity2 = total / this.datanum2
      if (this.currentHumidity2 > this.maxHumidity2) this.maxHumidity2 = this.currentHumidity2
      if (this.currentHumidity2 < this.minHumidity2) this.minHumidity2 = this.currentHumidity2
    },
  },
  mounted() {

    getIotById(1).then(({ data }) => {
      let res = data.data
      this.maxThresholdTemp1 = res.tempmax
      this.minThresholdTemp1 = res.tempmin
      this.maxThresholdHumi1 = res.humimax
      this.minThresholdHumi1 = res.humimin
    })

    getIotById(2).then(({ data }) => {
      let res = data.data
      this.maxThresholdTemp2 = res.tempmax
      this.minThresholdTemp2 = res.tempmin
      this.maxThresholdHumi2 = res.humimax
      this.minThresholdHumi2 = res.humimin
    })

    //处理非图标
    getAllData().then(({ data }) => {
      let records = data.data
      records.forEach(element => {
        if (parseInt(element.froms) == 1) {
          this.currentHumidity1 = parseFloat(element.humidity)
          this.currentTemperature1 = parseFloat(element.temperature)
          this.datanum1 = this.datanum1 + 1
          this.timestamp1 = parseInt(element.timestamp)
          this.updateTemperature1();
          this.updateHumidity1();
        } else if (parseInt(element.froms) == 2) {
          this.currentHumidity2 = parseFloat(element.humidity)
          this.currentTemperature2 = parseFloat(element.temperature)
          this.datanum2 = this.datanum2 + 1
          this.timestamp2 = parseInt(element.timestamp)
          this.updateTemperature2();
          this.updateHumidity2();
        }
      });
    })

    

    setInterval(() => {
      getData().then(({ data }) => {
        let element = data.data
        if (parseInt(element.froms) == 1) {
          if (this.timestamp1 != element.timestamp) {
            this.currentHumidity1 = parseFloat(element.humidity)
            this.currentTemperature1 = parseFloat(element.temperature)
            this.datanum1 = this.datanum1 + 1
            this.timestamp1 = parseInt(element.timestamp)
            this.updateTemperature1();
            this.updateHumidity1();
          }
        } else {
          if (this.timestamp2 != element.timestamp) {
            this.currentHumidity2 = parseFloat(element.humidity)
            this.currentTemperature2 = parseFloat(element.temperature)
            this.datanum2 = this.datanum2 + 1
            this.timestamp2 = parseInt(element.timestamp)
            this.updateTemperature2();
            this.updateHumidity2();
          }
        }
      })
      getIotById(1).then(({ data }) => {
        let res = data.data
        this.maxThresholdTemp1 = res.tempmax
        this.minThresholdTemp1 = res.tempmin
        this.maxThresholdHumi1 = res.humimax
        this.minThresholdHumi1 = res.humimin
      })

      getIotById(2).then(({ data }) => {
        let res = data.data
        this.maxThresholdTemp2 = res.tempmax
        this.minThresholdTemp2 = res.tempmin
        this.maxThresholdHumi2 = res.humimax
        this.minThresholdHumi2 = res.humimin
      })
    }, 3000); // 每 3 秒更新一次
  },
};
</script>

<template>
  <Layout>
    <PageHeader :title="title" :items="items" />

    <!-- 温度报表 -->
    <div class="row">
      <div class="col-lg-6">
        <div class="card">
          <div class="card-body">

            <h4 class="card-title mb-4">设备[id#1]温度报表</h4>

            <div class="row justify-content-center">

              <div class="col-sm-4">
                <div class="text-center">
                  <h5 class="mb-0 font-size-20">{{ this.maxTemperature1.toFixed(2) }} ℃</h5>
                  <p class="text-muted">最高温度</p>
                </div>
              </div>

              <div class="col-sm-4">
                <div class="text-center">
                  <h5 class="mb-0 font-size-20">{{ this.minTemperature1.toFixed(2) }} ℃</h5>
                  <p class="text-muted">最低温度</p>
                </div>
              </div>

              <div class="col-sm-4">
                <div class="text-center">
                  <h5 class="mb-0 font-size-20">{{ this.avgTemperature1.toFixed(2) }} ℃</h5>
                  <p class="text-muted">平均温度</p>
                </div>
              </div>

            </div>
            <div style="margin-top: 20px;">
              <TemperatureChart :temperature="this.currentTemperature1" :timestamp="this.timestamp1" ref="tmpc"
                :Maxthreshold="this.maxThresholdTemp1" :Minthreshold="this.minThresholdTemp1" :ids="1">
              </TemperatureChart>
            </div>
          </div>
        </div>
      </div>

      <!-- 湿度报表 -->
      <div class="col-lg-6">
        <div class="card">
          <div class="card-body">

            <h4 class="card-title mb-4">设备[id#1]湿度报表</h4>

            <div class="row justify-content-center">

              <div class="col-sm-4">
                <div class="text-center">
                  <h5 class="mb-0 font-size-20">{{ this.maxHumidity1.toFixed(2) }} %</h5>
                  <p class="text-muted">最高湿度</p>
                </div>
              </div>

              <div class="col-sm-4">
                <div class="text-center">
                  <h5 class="mb-0 font-size-20">{{ this.minHumidity1.toFixed(2) }} %</h5>
                  <p class="text-muted">最低湿度</p>
                </div>
              </div>

              <div class="col-sm-4">
                <div class="text-center">
                  <h5 class="mb-0 font-size-20">{{ this.avgHumidity1.toFixed(2) }} %</h5>
                  <p class="text-muted">平均湿度</p>
                </div>
              </div>

            </div>

            <!-- <TemperatureChart :temperature="this.currentTemperature"> </TemperatureChart> -->

            <div style="margin-top: 20px;">
              <HumidityChart :humidity="this.currentHumidity1" :timestamp="this.timestamp1" ref="hdc"
                :Maxthreshold="this.maxThresholdHumi1" :Minthreshold="this.minThresholdHumi1" :ids="1"> </HumidityChart>
            </div>
          </div>
        </div>
        <!-- end card -->
      </div>
      <!-- end col -->
    </div>


    <!-- 温度报表 -->
    <div class="row">
      <div class="col-lg-6">
        <div class="card">
          <div class="card-body">

            <h4 class="card-title mb-4">设备[id#2]温度报表</h4>

            <div class="row justify-content-center">

              <div class="col-sm-4">
                <div class="text-center">
                  <h5 class="mb-0 font-size-20">{{ this.maxTemperature2.toFixed(2) }} ℃</h5>
                  <p class="text-muted">最高温度</p>
                </div>
              </div>

              <div class="col-sm-4">
                <div class="text-center">
                  <h5 class="mb-0 font-size-20">{{ this.minTemperature2.toFixed(2) }} ℃</h5>
                  <p class="text-muted">最低温度</p>
                </div>
              </div>

              <div class="col-sm-4">
                <div class="text-center">
                  <h5 class="mb-0 font-size-20">{{ this.avgTemperature2.toFixed(2) }} ℃</h5>
                  <p class="text-muted">平均温度</p>
                </div>
              </div>

            </div>
            <div style="margin-top: 20px;">
              <TemperatureChart :temperature="this.currentTemperature2" :timestamp="this.timestamp2" ref="tmpc"
                :Maxthreshold="this.maxThresholdTemp2" :Minthreshold="this.minThresholdTemp2" :ids="2">
              </TemperatureChart>
            </div>
          </div>
        </div>
      </div>

      <!-- 湿度报表 -->
      <div class="col-lg-6">
        <div class="card">
          <div class="card-body">

            <h4 class="card-title mb-4">设备[id#2]湿度报表</h4>

            <div class="row justify-content-center">

              <div class="col-sm-4">
                <div class="text-center">
                  <h5 class="mb-0 font-size-20">{{ this.maxHumidity2.toFixed(2) }} %</h5>
                  <p class="text-muted">最高湿度</p>
                </div>
              </div>

              <div class="col-sm-4">
                <div class="text-center">
                  <h5 class="mb-0 font-size-20">{{ this.minHumidity2.toFixed(2) }} %</h5>
                  <p class="text-muted">最低湿度</p>
                </div>
              </div>

              <div class="col-sm-4">
                <div class="text-center">
                  <h5 class="mb-0 font-size-20">{{ this.avgHumidity2.toFixed(2) }} %</h5>
                  <p class="text-muted">平均湿度</p>
                </div>
              </div>

            </div>

            <!-- <TemperatureChart :temperature="this.currentTemperature"> </TemperatureChart> -->

            <div style="margin-top: 20px;">
              <HumidityChart :humidity="this.currentHumidity2" :timestamp="this.timestamp2" ref="hdc"
                :Maxthreshold="this.maxThresholdHumi2" :Minthreshold="this.minThresholdHumi2" :ids="2"> </HumidityChart>
            </div>
          </div>
        </div>
        <!-- end card -->
      </div>
      <!-- end col -->
    </div>

</Layout></template>