/*!
 * Remark (http://getbootstrapadmin.com/remark)
 * Copyright 2015 amazingsurge
 * Licensed under the Themeforest Standard Licenses
 */

(function(document, window, $) {
  'use strict';
  (function() {
    var $tableEvent = $('#exampleTableEvents');

      $tableEvent.bootstrapTable({
        url: "/ddns-config",
        search: true,
        pagination: true,
        showRefresh: true,
        showToggle: true,
        showColumns: true,
        iconSize: 'outline',
        toolbar: '#exampleTableEventsToolbar',
        icons: {
          refresh: 'glyphicon-repeat',
          toggle: 'glyphicon-list-alt',
          columns: 'glyphicon-list'
        },
        columns: [
            {
                field: 'state',
                checkbox: true
            },
            {
                field: 'id',
                title: '编号'
            },
            {
                field: 'address',
                title: 'IP地址段'
            },
            {
                field: 'domain',
                title: '域名'
            },
            {
                field: 'model',
                title: 'IP分发模式',
                formatter: function (value, row, index) {
                    var result = "默认";
                    switch (value) {
                        case 1:
                            result = "轮询，依次应答";
                            break;
                        case 2:
                            result = "IP Hash，与请求来源IP绑定";
                            break;
                        case 3:
                            result = "随机";
                            break;
                        default:
                            result = "默认";
                    }
                    return result;
                }
            },
            {
                field: 'enable',
                title: '启用',
                formatter: operateFormatter
            }
        ]
    });

    function operateFormatter(value, row, index) {
        if (value === 1) {
            return "启用";
        } else if (value === 0) {
            return "未启用";
        }
    }



    var $result = $('#examplebtTableEventsResult');

      $tableEvent.on('all.bs.table', function(e, name, args) {
          // console.log(args);
      })
      .on('click-row.bs.table', function(e, row, $element) {

        $result.text('Event: click-row.bs.table');
      })
      .on('dbl-click-row.bs.table', function(e, row, $element) {
        $result.text('Event: dbl-click-row.bs.table');
      })
      .on('sort.bs.table', function(e, name, order) {
        $result.text('Event: sort.bs.table');
      })
      .on('check.bs.table', function(e, row) {

        $result.text('Event: check.bs.table');
      })
      .on('uncheck.bs.table', function(e, row) {
        $result.text('Event: uncheck.bs.table');
      })
      .on('check-all.bs.table', function(e) {
        $result.text('Event: check-all.bs.table');
      })
      .on('uncheck-all.bs.table', function(e) {
        $result.text('Event: uncheck-all.bs.table');
      })
      .on('load-success.bs.table', function(e, data) {
        $result.text('Event: load-success.bs.table');
      })
      .on('load-error.bs.table', function(e, status) {
        $result.text('Event: load-error.bs.table');
      })
      .on('column-switch.bs.table', function(e, field, checked) {
        $result.text('Event: column-switch.bs.table');
      })
      .on('page-change.bs.table', function(e, size, number) {
        $result.text('Event: page-change.bs.table');
      })
      .on('search.bs.table', function(e, text) {
        $result.text('Event: search.bs.table');
      });
  })();
})(document, window, jQuery);
