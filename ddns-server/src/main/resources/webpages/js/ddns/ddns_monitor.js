$(function () {
    var chart;
    //图标全局属性
    Highcharts.setOptions({
        global: {
            useUTC: false
        }
    });

    var options = {
        chart: {
            type: 'spline',
            marginRight: 10,
            events: {
            }
        },
        title: {
            text: '实时请求数据展示'
        },
        xAxis: {
            type: 'datetime',
            tickInterval: 1000,
            min:Date.UTC(new Date().getFullYear(), new Date().getMonth(), new Date().getDate()-1, 16, 0, 0),
            max:Date.UTC(new Date().getFullYear(), new Date().getMonth(), new Date().getDate(), 16, 0, 0),
            dateTimeLabelFormats: {
                day: '%H:%M'
            }
        },
        yAxis: {
            min: 0,
            max: 1,
            title: {
                text: "实时请求数据量"
            }
        },
        legend: {
            enabled: false
        },
        series: [{
            name: '1分钟内平均请求数',
            data: []
        }]
    };

    //从后台获取数据
    requestData(options);

    //websocket初始化
    var ws_scheme = "ws://";
    // Support TLS-specific URLs, when appropriate.
    if (window.location.protocol === "https:") {
        ws_scheme = "wss://";
    }

    var inbox = new ReconnectingWebSocket(ws_scheme + location.host + "/ws");

    // 用于指定收到服务器数据后的回调函数
    inbox.onmessage = function(message) {
        // console.log(message);
        var data = JSON.parse(message.data);
        // console.log(data);
        switch (data['name']) {
            case 'yinan_counter':
                solveAllRequest(data['value']['count']);
                break;
            case 'unauthority_request':
                solveUnAuthorityRequest(data['value']['count']);
                break;
            case 'test_like':
                solveTestLike(chart, data['value']['m1_rate']);
                break;
            default:
                break;
        }


    };
    // ws.onclose: 用于指定服务器端连接关闭后的回调函数
    inbox.onclose = function(){
        console.log('inbox closed');

    };

    //初始化实时请求数据图表
    function requestData(options) {
        $.ajax({
            url: '/getMeter',
            data: {
                "meterName": "test_like",
                "rateName": "m1_rate"
            },
            async: false,
            success: function(result) {
                var data =  JSON.parse(result);
                var message = '[' + data.message + ']';
                options.series[0].data = eval(message);
                chart = Highcharts.chart('container', options);
            },
            cache: true
        });
    }


});

//服务有效请求数
function solveAllRequest(val) {
    $('#all-request').text(val);
}
//总无效请求数
function solveUnAuthorityRequest(val) {
    $('#unauthority_request').text(val);
}

//实时请求数据展示
function solveTestLike(chart, val) {
    chart.series[0].addPoint([new Date().getTime(), Math.round(val*100)/100], true, false);
}

