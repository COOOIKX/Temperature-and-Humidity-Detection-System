<template>
    <div ref="echarts1" style="height: 400px;"> </div>
</template>

<script>

import * as echarts from 'echarts'
import { getAllData } from '../../../api'

export default {
    props: ['humidity', 'timestamp', 'Maxthreshold', 'Minthreshold', 'ids'],
    data() {
        return {

            humidityData: [],

            echartsInstance: null,

            nowTimestamp: 0,

            thoptions: {
                tooltip: {
                    trigger: 'axis'
                },
                legend: {},

                xAxis: {
                    type: 'time',
                    splitLine: {
                        show: false
                    }
                },

                yAxis: {
                    max: 100,
                    min: 0,
                    type: 'value',
                    axisLabel: {
                        formatter: '{value} %'
                    }
                },

                series: [
                    {
                        smooth: true,
                        name: '湿度',
                        type: 'line',
                        data: [],
                        markPoint: {
                            data: [
                                { type: 'max', name: 'Max' },
                                { type: 'min', name: 'Min' }
                            ]
                        },
                        markLine: {
                            symbol: ['none', 'none'], // 去掉箭头
                            label: {
                                show: false,
                                position: 'start',
                                formatter: '{b}'
                            },
                            data: [
                                {
                                    name: '阈值-Max',
                                    lineStyle: { color: '#228008' },
                                    yAxis: 0
                                },
                                {
                                    name: '阈值-Min',
                                    lineStyle: { color: '#228008' },
                                    yAxis: 0
                                },
                                { type: 'average', name: 'Avg' }
                            ],

                        }
                    },

                ]
            }

        }
    },
    mounted() {
        this.thoptions.series[0].markLine.data[0].yAxis = this.Maxthreshold
        this.thoptions.series[0].markLine.data[1].yAxis = this.Minthreshold

        getAllData().then(({ data }) => {
            var records = data.data
            records.forEach(element => {
                if(parseInt(element.froms) == this.ids) {
                    var timestamp = parseInt(element.timestamp) * 1000
                    var humidity = parseFloat(element.humidity)
                    this.humidityData.push({ value: [timestamp, humidity.toFixed(2)] })
                    const maxDataPoints = 20;
                    if (this.humidityData.length > maxDataPoints) {
                        this.humidityData.shift();
                    }
                }
            });

            this.thoptions.series[0].data = this.humidityData
        })

        this.echartsInstance = echarts.init(this.$refs.echarts1)
        this.echartsInstance.setOption(this.thoptions)
    },
    methods: {
        updateChartData(newTimestamp, newHumidity) {
            const currentTime = newTimestamp * 1000

            this.humidityData.push({ value: [currentTime, newHumidity.toFixed(2)] })

            // 只保留最新的 N 个数据点，以防止图表过大
            const maxDataPoints = 20;
            if (this.humidityData.length > maxDataPoints) {
                this.humidityData.shift();
            }
            this.thoptions.series[0].data = this.humidityData
        },
        sortData() {
            this.thoptions.series[0].data.sort(function (obj1, obj2) {
                var t1 = obj1.value[0]
                var t2 = obj2.value[0]
                if (t1 < t2) {
                    return -1;
                } else if (t1 > t2) {
                    return 1;
                } else {
                    return 0
                }
            })
        }
    },
    watch: {
        timestamp(newTimestamp) {
            this.updateChartData(newTimestamp, this.humidity)
            this.sortData()
            this.echartsInstance.setOption(this.thoptions);
        },
        Maxthreshold(x) {
            this.thoptions.series[0].markLine.data[0].yAxis = x
            this.echartsInstance.setOption(this.thoptions);
        },
        Minthreshold(x) {
            this.thoptions.series[0].markLine.data[1].yAxis = x
            this.echartsInstance.setOption(this.thoptions);
        },
    }
}
</script>

<style></style>