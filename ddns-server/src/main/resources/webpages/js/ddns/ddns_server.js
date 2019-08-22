$(function () {
    var $server = $('#server-list');
    $server.bootstrapTable({
        url: '/server-host',
        method: 'get',
        sidePagination: "true",
        striped: true, // 是否显示行间隔色
        pagination: true, // 是否分页
        sortable: true, // 是否启用排序
        columns: [
            {
                field: 'id',
                title: '编号',
                formatter:function(value,row,index){
                    //return index+1; //序号正序排序从1开始
                    var pageSize=$server.bootstrapTable('getOptions').pageSize;//通过表的#id 可以得到每页多少条
                    var pageNumber=$server.bootstrapTable('getOptions').pageNumber;//通过表的#id 可以得到当前第几页
                    return pageSize * (pageNumber - 1) + index + 1;    //返回每条的序号： 每页条数 * （当前页 - 1 ）+ 序号
                }
            },
            {
                field: 'host',
                title: '服务器地址'
            }
        ]
    });

    $('#submit-host').on('click', function () {
        var data = {
            'host' : $('#address').val()
        };
        send(data, "/update-server-host", "post", $server);

    });


});

function send(data, url, type, server) {
    $.ajax({
        url: url,
        type: type,
        data: JSON.stringify(data),
        dataType: 'json',
        success: function (message) {
            if ('host is duplicate!' === message['message']) {
                $.alert("数据已经存在！");
            } else {
                $.tips('更新成功！', 1000);
                server.bootstrapTable('refresh');
            }

        },
        error: function (error) {
            $.alert("保存失败，请联系管理员！");
        }
    })
}