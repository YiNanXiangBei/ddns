$(function () {

    $('#ddns-server').bootstrapValidator({
        live: 'disabled',
        message: '输入的值无效',
        feedbackIcons: {
            valid: 'glyphicon glyphicon-ok',
            invalid: 'glyphicon glyphicon-remove',
            validating: 'glyphicon glyphicon-refresh'
        },
        fields: {
            address: {
                validators: {
                    notEmpty: {
                        message: '输入的值不能为空'
                    }
                }
            }
        }
    });
    $('#new-ddns-setting').bootstrapValidator({
        live: 'disabled',
        message: '输入的值无效',
        feedbackIcons: {
            valid: 'glyphicon glyphicon-ok',
            invalid: 'glyphicon glyphicon-remove',
            validating: 'glyphicon glyphicon-refresh'
        },
        fields: {
            domain: {
                validators: {
                    notEmpty: {
                        message: '不能为空'
                    }
                }
            },
            startValue: {
                validators: {
                    notEmpty: {
                        message: '不能为空'
                    },
                    ip: {
                        message: 'ip格式错误'
                    }
                }
            },
            endValue: {
                validators: {
                    notEmpty: {
                        message: '不能为空'
                    },
                    ip: {
                        message: 'ip格式错误'
                    }
                }
            }
        }
    });

});