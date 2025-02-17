let connect_members_chart;

$(function() {
    "use strict";
    // chart2
    var refererElement = document.getElementById('referer_chart').getContext('2d');
    var refererChart = new Chart(refererElement, {
        type: 'bar',
        data: {
            labels: pc_referer_data.map(data => data.date),
            datasets: [
                {
                    label: 'PC',
                    data: pc_referer_data.map(data => data.count),
                    backgroundColor: [
                        '#5e72e4'
                    ],
                    borderColor: [
                        '#5e72e4'
                    ],
                    borderWidth: 0
                },
                {
                    label: 'Muant',
                    data: m_referer_data.map(data => data.count),
                    backgroundColor: [
                        'rgb(94 114 228 / 32%)'
                    ],
                    borderColor: [
                        'rgb(94 114 228 / 32%)'
                    ],
                    borderWidth: 0
                }
            ]
        },
        options: {
            maintainAspectRatio: false,
            barPercentage: 0.35,
            //categoryPercentage: 0.5,
            plugins: {
                legend: {
                    position:'bottom',
                    display: true,
                }
            },
            scales: {
                x: {
                    stacked: true,
                    beginAtZero: true
                },
                y: {
                    stacked: true,
                    beginAtZero: true
                }
            }
        }
    });

    const connect_members_options = {
        series: [0, 0],
        chart: {
            foreColor: '#9ba7b2',
            height: 380,
            type: 'donut',

        },
        colors: ["#2dce89", "#fb6340"],
        labels: ["로그인", "비로그인"],
        responsive: [{
            breakpoint: 480,
            options: {
                chart: {
                    height: 320
                },
                legend: {
                    position: 'bottom'
                }
            }
        }],
        noData: {
            text: 'Loading...',
            align: 'center',
            verticalAlign: 'middle',
            style: {
                fontSize: '14px',
                color: '#000000'
            }
        }
    };

    connect_members_chart = new ApexCharts(document.querySelector("#connect_members"), connect_members_options);

    connect_members_chart.render();
});
