$(function () {

    var checkedData = [];
    var rowAllValues = [];
    var $dataTable = $('#exampleTableEvents');
    var send = function (data, url) {

    };
    //主动触发刷新事件，刷新完成之后主动置空选中的元素
    var refresh = function () {
        $('#exampleTableEvents').bootstrapTable('refresh', {
            url: "js/demo/ddns-config.json"
        });
        checkedData = [];
    };
    //删除数组内的元素,arr:数组;val:元素
    var remove = function(arr, val) {
      var index = arr.indexOf(val);
      arr.splice(index, 1);
    };
    //点击前面的checkbox
    $dataTable.on('check.bs.table', function (e, row) {
        checkedData.push(row.id);
        rowAllValues.push(row);
    });
    //单击取消前面的checkbox
    $dataTable.on('uncheck.bs.table', function (e, row) {
        remove(checkedData, row.id);
        remove(rowAllValues, row);
    });
    //取消所有checkbox前面的值
    $dataTable.on('uncheck-all.bs.table', function (e, row) {
        checkedData = [];
        rowAllValues = [];
    });
    //选择所有前面的checkbox值
    $dataTable.on('check-all.bs.table', function (e, row) {
        row.forEach(function (item) {
            checkedData.push(item.id);
        });

        row.forEach(function (item) {
            rowAllValues.push(item);
        });
    });

    //点击删除图标触发
    $('#deleteTargets').on('click', function () {
        if (checkedData.length === 0) {
            alert("请选中需要删除的数据！");
        }
        $dataTable.bootstrapTable('uncheckAll');
        refresh();
    });
    //点击启用图标触发事件
    $('#choose').on('click', function () {
        if (checkedData.length === 0) {
            alert("请选中需要启用的数据！");
        }
        refresh();
    });
    //点击不启用图标触发事件
    $('#unChoose').on('click', function () {
        if (checkedData.length === 0) {
            alert("请选中不需要启用的数据！");
        }
        refresh();
    });
    //点击编辑按钮触发
    $('#edit').on('click', function () {
        if (checkedData.length === 0) {
            alert("请选中需要编辑的数据！");
        } else if(checkedData.length > 1) {
            alert("选择的数据条数过多，请确定真正需要修改的元素！");
        } else {
            localStorage.setItem("message", JSON.stringify(rowAllValues));
            window.location.href = 'new_setting.html?editId=' + checkedData.pop();
        }
    });

});