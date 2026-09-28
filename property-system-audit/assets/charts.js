// === 物业管理系统审查报告 · 图表脚本 ===
(function () {
  var style = getComputedStyle(document.documentElement);
  var accent = style.getPropertyValue('--accent').trim();
  var accent2 = style.getPropertyValue('--accent2').trim();
  var ink = style.getPropertyValue('--ink').trim();
  var muted = style.getPropertyValue('--muted').trim();
  var rule = style.getPropertyValue('--rule').trim();
  var bg2 = style.getPropertyValue('--bg2').trim();
  var danger = style.getPropertyValue('--danger').trim();
  var warn = style.getPropertyValue('--warn').trim();
  var ok = style.getPropertyValue('--ok').trim();

  // --- 图1：三端问题分布（堆叠柱状图）---
  var chartEl = document.getElementById('chart-issues');
  if (chartEl && typeof echarts !== 'undefined') {
    var chart = echarts.init(chartEl, null, { renderer: 'svg' });
    chart.setOption({
      animation: false,
      tooltip: {
        trigger: 'axis',
        axisPointer: { type: 'shadow' },
        appendToBody: true,
        textStyle: { color: ink, fontSize: 12 }
      },
      legend: {
        data: ['高危 🔴', '中危 🟠', '低危 🟢'],
        top: 0,
        textStyle: { color: muted, fontSize: 12 },
        itemWidth: 14,
        itemHeight: 10
      },
      grid: { left: '3%', right: '4%', bottom: '4%', top: '18%', containLabel: true },
      xAxis: {
        type: 'category',
        data: ['后端 Spring Boot', 'admin-web 前端', 'miniapp 小程序'],
        axisLine: { lineStyle: { color: rule } },
        axisTick: { show: false },
        axisLabel: { color: ink, fontSize: 12, fontWeight: 600 }
      },
      yAxis: {
        type: 'value',
        name: '问题数',
        nameTextStyle: { color: muted, fontSize: 11 },
        axisLine: { show: false },
        axisTick: { show: false },
        splitLine: { lineStyle: { color: rule, type: 'dashed' } },
        axisLabel: { color: muted, fontSize: 11 }
      },
      series: [
        {
          name: '高危 🔴',
          type: 'bar',
          stack: 'total',
          data: [7, 3, 2],
          itemStyle: { color: danger, borderRadius: [0, 0, 0, 0] },
          barWidth: '42%',
          label: { show: true, color: '#fff', fontWeight: 700, fontSize: 12 }
        },
        {
          name: '中危 🟠',
          type: 'bar',
          stack: 'total',
          data: [10, 8, 10],
          itemStyle: { color: warn },
          label: { show: true, color: '#fff', fontWeight: 700, fontSize: 12 }
        },
        {
          name: '低危 🟢',
          type: 'bar',
          stack: 'total',
          data: [7, 9, 9],
          itemStyle: { color: ok, borderRadius: [4, 4, 0, 0] },
          label: { show: true, color: '#fff', fontWeight: 700, fontSize: 12 }
        }
      ]
    });
    window.addEventListener('resize', function () { chart.resize(); });
  }

  // --- Mermaid 初始化 ---
  if (typeof mermaid !== 'undefined') {
    mermaid.initialize({
      startOnLoad: true,
      theme: 'neutral',
      securityLevel: 'loose',
      flowchart: { curve: 'basis', padding: 16 }
    });
  }
})();
