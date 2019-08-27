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
            },
            {
                title: '操作',
                formatter: function (value, row, index) {
                    return '<button type="button" class="btn btn-primary btn-sm" onclick="del(\'' + row.host + '\')">删除</button>';
                }
            }
        ]
    });

    $('#submit-host').on('click', function () {
        var $validate = $('#ddns-server');
        $validate.data('bootstrapValidator').validate();
        if(!$validate.data('bootstrapValidator').isValid()){
            return ;
        }
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

function del(host) {
    $.confirm('是否删除上游服务地址[' + host + ']?',function(e){
        if (e) {
            var data = {
                'host': host
            };
            $.ajax({
                url: '/del/server-host',
                type: 'post',
                data: JSON.stringify(data),
                dataType: 'json',
                success: function (message) {
                    if (20000 === message['code']) {
                        $.tips('删除成功！', 1000);
                        $('#server-list').bootstrapTable('refresh');
                    } else if (50000 === message['code']) {
                        $.alert("删除失败，请联系管理员！");
                    }
                },
                error: function (error) {
                    $.alert("保存失败，请联系管理员！");
                }
            })
        }
        //点击确定或取消后的回调函数，点击确定e = true，点击取消e = false
        //return false 可以阻止对话框关闭
        //this 指向弹窗对象
    }).ok('确定').cancel('取消');

}