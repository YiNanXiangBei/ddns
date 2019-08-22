$(function () {
    //初始化方法
    init();

    $('#select-list').change(function () {
        var val = $(this).children('option:selected').val();
        if (val === '1') {
            hide();
        } else if (val === '2') {
            show();
        }
    });

    $('#submit').on('click', function () {
        var module = getModuleVal();
        var startVal = getStartVal();
        var domainVal = getDomainVal();
        var endVal = getEndVal();
        var modelVal = getModelVal();
        var oneData = {};
        if (module === '2') {
            //多ip
            oneData = {
                "address":startVal + ' - ' + endVal,
                "domain":domainVal,
                "model":parseInt(modelVal)
            }
        } else if (module === '1') {
            //单ip
            oneData = {
                "address":startVal,
                "domain":domainVal,
                "model":4
            }
        }
        var editId = getQueryString("editId");
        var data = {};
        if (editId === null) {
            //新增页面保存
            oneData["enable"] =  1;
            data['config'] = oneData;
            send(data, "/ddns-config/save", 'post');
        } else {
            //编辑页面保存
            var message = JSON.parse(localStorage.getItem("message"));
            oneData["id"] = message[0].id;
            oneData["enable"] = message[0].enable;
            var dataArray = [];
            dataArray.push(oneData);
            data['config'] = dataArray;
            send(data, '/ddns-config/update', 'post');
        }
        returnSettingPage();
    });

    $('#cancel').on('click', function () {
        returnSettingPage();
    })
});

function returnSettingPage() {
    localStorage.setItem('message', []);
    window.location.href = 'ddns_setting.html';
}

function init() {
    var editId = getQueryString("editId");
    if (editId === null) {
        hide();
        //初始化新建页面
    }  else if (editId !== null) {
        $('h5').html('编辑页面');
        //初始化编辑页面
        var message = JSON.parse(localStorage.getItem("message"));
        if (message.length === 1) {
            var model = message[0].model;
            var address = message[0].address;
            var domain = message[0].domain;
            if (model < 4) {
                //多ip数据映射
                var addresses = address.split('-');
                if (addresses.length === 2) {
                    startValue(addresses[0]);
                    endValue(addresses[1]);
                } else if (addresses.length === 1) {
                    startValue(addresses[0]);
                }
                domainValue(domain);
                moduleValue(2);
                ipValue(model);
            } else {
                //单ip数据映射
                hide();
                domainValue(domain);
                moduleValue(1);
                startValue(address);
            }
        } else {
            alert("message error !");
        }

    }
}

var send = function (data, url, type) {
    $.ajax({
        url: url,
        type: type,
        data: JSON.stringify(data),
        dataType: 'json',
        success: function (message) {
            $.tips('保存成功！', 1000);
        },
        error: function (error) {
            $.alert("保存失败，请联系管理员！");
        }
    })
};


/**
 * @return {string}
 */
function getQueryString(name) {
    var reg = new RegExp("(^|&)" + name + "=([^&]*)(&|$)", "i");
    var r = window.location.search.substr(1).match(reg);
    if (r != null)
        return unescape(r[2]);
    return null;
}

function show() {
    $('#multi-ip1').css('display','block');
    $('#multi-ip2').css('display','block');
    $('#multi-ip3').css('display','block');
}


function hide() {
    $('#multi-ip1').css('display','none');
    $('#multi-ip2').css('display','none');
    $('#multi-ip3').css('display','none');
}

function startValue(startAddress) {
    $('#start-value').val(startAddress);
}

function endValue(endAddress) {
    $('#end-value').val(endAddress);
}

function domainValue(domain) {
    $('#domain').val(domain);
}

function moduleValue(module) {
    $('#select-list').val(module);
}

function getStartVal() {
    return $('#start-value').val();
}

function getEndVal() {
    return $('#end-value').val();
}

function getDomainVal() {
    return $('#domain').val();
}

function getModuleVal() {
    return $('#select-list').val();
}

function getModelVal() {
    return $("input[name='optionsRadios']:checked").val();
}

function ipValue(ipVal) {
    $("input[name='optionsRadios'][value="+ ipVal +"]").attr("checked",true);
}