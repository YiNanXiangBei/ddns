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
            text: '平均每分钟服务请求量'
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
            max: 10,
            title: {
                text: "平均每分钟服务请求量"
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
            case 'total_request':
                solveAllRequest(data['value']['count']);
                break;
            case 'total_response':
                solveTotalResponse(data['value']['count']);
                break;
            case 'total_request_average':
                solveAveRequest(chart, data['value']['m1_rate']);
                break;
            case 'query_times':
                solveDDNSQueryTimes(data['value']['count']);
                break;
            case 'invalid_request':
                solveDDNSInvalidRequest(data['value']['count']);
                break;
            case 'personal_ddns_request':
                personalDDNSRequest(data['value']['count']);
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
                "meterName": "total_request_average",
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
    $('#total-request').text(val);
}
//总无效请求数
function solveTotalResponse(val) {
    $('#total-response').text(val);
}

//实时请求数据展示
function solveAveRequest(chart, val) {
    chart.series[0].addPoint([new Date().getTime(), Math.round(val*100)/100], true, false);
}

function solveDDNSQueryTimes(val) {
    $('#query-times').text(val);
}

function solveDDNSInvalidRequest(val) {
    $('#invalid-request').text(val);
}

function personalDDNSRequest(val) {
    $('#personal-ddns-request').text(val);
}
