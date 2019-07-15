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

    $('#cancel').on('click', function () {
        localStorage.setItem('message', []);
        window.location.href = 'ddns_setting.html';
    })
});
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

function ipValue(ipVal) {
    $("input[name='optionsRadios'][value="+ ipVal +"]").attr("checked",true);
}