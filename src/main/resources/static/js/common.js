var hwOceanConnectDemo = function(){
	/*-- Main business */
	// 初始化
	var interval = 5000;
	var switchCount = 0;
	var deviceId = getDeviceId();
	var verifyCode = '';
	var deviceParas = getDeviceParas();
	var INTERVAL_ID;

    getHistoryData();

	var humidity=localStorage.getItem("humidity")||'0';
	var tempture=localStorage.getItem("tempture")||'0';
    var luxVal = localStorage.getItem('luxVal') || '0';
    var motorStatus = localStorage.getItem('motorStatus')|| 'ON';

    var ydata = [];
    if (localStorage.getItem("ydata") != null){
        ydata = JSON.parse(localStorage.getItem('ydata'));
    }
    var lineData = [];
    if (localStorage.getItem("lineData") != null){
        lineData =  JSON.parse(localStorage.getItem('lineData'));
    }
    var maxLen = 50;
    var myChart;

   // 指定图表的配置项和数据
    var option = {
        title: {
            text: ''
        },
        tooltip: {
            trigger:"axis",
            formatter:"{c} lux<br />{b}"
        },
        xAxis: {
            type: 'category',
            data: lineData
        },
        yAxis: {
            type: 'value'
        },
        series: [{
            data: ydata,
            type: 'line'
        }]
    };

    getPreDeviceParas(function(){
        var deviceParas = getDeviceParas();
		localStorage.setItem('timerStatus',false);
		localStorage.setItem('dataMode',"subscribe");
        !deviceParas.name && openSetParamsDialog(); // 未配置参数时自动弹出参数配置窗口
        // deviceId && getDeviceLuxThreshold(); // 获取阈值
        deviceId && showStatus();// 已注册设备时直接显示状态
	});

	function getDeviceId() {
        $.ajax({
            url: "/get-device-id",
            type: "get",
            success: function (data) {
                if (data){
                    localStorage.setItem('deviceId',data);
                    console.log("get deviceId from server");
                }
                else{
                localStorage.removeItem('deviceId');
                }
            },
            error: function () {
              console.error("连接服务器失败！");
            }
        });
		return localStorage.getItem('deviceId')||'';
	}

	function storeDeviceId(id) {
		localStorage.setItem('deviceId',id);
		deviceId = id;
	}

	// 定时获取数据
	function initTickTimer(){
		var dataMode1 = localStorage.getItem('dataMode');
		if(localStorage.getItem("timerStatus")){
			var dataMode = localStorage.getItem('dataMode');
			if(dataMode==="DIS"){
				getDis();
				localStorage.setItem('timerStatus',true);
				INTERVAL_ID === undefined && ( INTERVAL_ID = setInterval(function() {;
						getDis();
					},interval)
				);
			}
			if(dataMode==="subscribe"){
				getSub();
				localStorage.setItem('timerStatus',true);
				INTERVAL_ID === undefined && ( INTERVAL_ID = setInterval(function() {;
						getSub();
					},interval)
				);
			}

		}

	}
	
	// 注册设备
	function regDevice( btn ){
	    var deviceParas = getDeviceParas();
		if( !deviceParas.name ){
			return openDialog('info','提示', "请设置应用参数！");
		}
		var verifyCode = $('#verifyCode').val();
        if( !verifyCode){
            return openDialog('info','提示', "请输入设备标识码！");
        }
		localStorage.setItem('verifyCode',verifyCode);
		$(btn).attr('disabled','');
		$.ajax({
			url: "/register-device",
			type: "post",
			contentType: "application/json; charset=utf-8",
            dataType: "json",
			data: JSON.stringify( {"verifyCode": verifyCode} ),
			success: function (data) {
		        storeDeviceId(data.device_id);
				$(btn).removeAttr('disabled');
				console.log(data);
				openDialogRegister('success',"注册设备成功！","设备Id："+data.device_id,"密钥："+data.secret);
                showStatus();
			},
			error: function () {
			  openDialog('error','提示', "注册设备失败！");
			  console.log($(btn));
			  $(btn).removeAttr('disabled');
			}
        });
	}

	// 显示状态查看模块并开始获取数据
	function showStatus(){
        // 显示状态查看模块
        $('.toggle-block').removeClass('hide');
        // 绘制光强曲线图表。
        myChart = echarts.init(document.getElementById('main'));
        myChart.setOption(option);
        // 启动定时器定时获取设备上报数据
        initTickTimer();
	}
	
	// // 下发设备命令,阈值模式
	// function setDeviceCommand(){
	// 	var setPower = $('.switch-toggle').hasClass( 'open' );
	//
	// 	$.ajax({
	// 		url: "/set-command-threshold",
	// 		type: "post",
	// 		contentType: "application/json; charset=utf-8",
    //         dataType: "text",
	// 		data: JSON.stringify( {"power": setPower ? "on" : 'off'} ),
	// 		success: function (data) {
	// 			//openDialog('success','提示', "请求成功！");
	// 		},
	// 		error: function () {
	// 		  console.error("连接服务器失败！");
	// 		}
    //     });
	// }
	
	// // 设置阈值
	// function setDeviceLuxThreshold(btn) {
	// 	$(btn).attr('disabled','');
	// 	var deviceLuxThreshold = parseInt( $('#deviceLuxThreshold').val() );
	//
	// 	$.ajax({
	// 		url: "/threshold",
	// 		type: "post",
	// 		contentType: "application/json; charset=utf-8",
	// 		dataType: "text",
	// 		data: JSON.stringify( {"threshold": deviceLuxThreshold } ),
	// 		success: function (data) {
	// 			openDialog('success','提示', "设置成功！");
	// 			$(btn).removeAttr('disabled');
	// 		},
	// 		error: function () {
	// 		  openDialog('error','提示', "设置失败！");
	// 		  $(btn).removeAttr('disabled');
	// 		}
    //     });
	// }
	//
	// // 获取阈值
	// function getDeviceLuxThreshold() {
	//
	// 	$.ajax({
	// 		url: "/threshold",
	// 		type: "get",
	// 		success: function (data) {
	// 			$('#deviceLuxThreshold').val( data && data.threshold )
	// 		},
	// 		error: function () {
	// 		  console.error("连接服务器失败！");
	// 		}
    //     });
	// }
	//
	// Dialog
	function openDialog(states,title, content){
		$('#modalDialog').addClass('show');
		$('#modalDialog').css('display','block');
		var showIcon = states ? '.'+states : '.info';
		$('.states-icon').removeClass('active');
		$('.states-icon'+showIcon).addClass('active');

		$('#dialogContentDevId').addClass("hide");
		$('#dialogContentSecet').addClass("hide");
		$('.btn-cancel').removeClass("hide");
		$('#dialogTitle').html(title || '提示');
		$('#dialogContent').text(content);
	}
	$('#modalDialog .close-modal').click(function() {
		$('#modalDialog').removeClass('show');
		$('#modalDialog').css('display','none');
	});

	// Dialog
	function openDialogRegister(states,title,deviceId,secret){
		$('#modalDialog').addClass('show');
		$('#modalDialog').css('display','block');
		var showIcon = states ? '.'+states : '.info';
		$('.states-icon').removeClass('active');
		$('.states-icon'+showIcon).addClass('active');

		$('#dialogContent').html(title || '提示');

		$('#dialogContentDevId').removeClass("hide");
		$('#dialogContentSecet').removeClass("hide");
		$('.btn-cancel').addClass("hide");

		$('#dialogContentDevId').text(deviceId);
		$('#dialogContentSecet').text(secret);
	}
	$('#modalDialog .close-modal').click(function() {
		$('#modalDialog').removeClass('show');
		$('#modalDialog').css('display','none');
	});
	
	
	// 从本地缓存读取配置
	function getDeviceParas(){
		var deviceParas = localStorage.getItem('deviceParas');
		return deviceParas && JSON.parse( deviceParas ) || {
			"app_id":"",
			"domain":"",
			"name":"",
			"password":"",
			"product_id":"",
			"ak": "",
			"sk": "",
			"projectId": "",
			"streamName": "",
			"callbackIp":"",
			"dataMode":"",
			"topicUrn":""
		};
	}

	// 二级函数：将input数据保存成JSON，并且保存到localstorage,并且发送到后台保存
	function onSetParamsDialogConfirm() {
		var app_id = $('input[name="app_id"]').val();
		var domain = $('input[name="domain"]').val();
		var name = $('input[name="name"]').val();
		var password = $('input[name="password"]').val();
		var product_id = $('input[name="product_id"]').val();
		var ak = $('input[name="ak"]').val();
		var sk = $('input[name="sk"]').val();
		var projectId = $('input[name="projectId"]').val();
		var streamName = $('input[name="streamName"]').val();
		var callbackIp = $('input[name="callbackIp"]').val();
		var dataMode = localStorage.getItem('dataMode');
		var topicUrn = $('input[name="topicUrn"]').val();

		var jsonStringify = JSON.stringify({
			"app_id": app_id,
			"domain": domain,
			"name": name,
			"password": password,
			"product_id": product_id,
			"ak": ak,
			"sk": sk,
			"projectId": projectId,
			"streamName": streamName,
			"callbackIp":callbackIp,
			"dataMode":dataMode,
			"topicUrn":topicUrn
		});
		
		localStorage.setItem('deviceParas',jsonStringify);
		
		postDeviceParas();
	}

	// 主函数：打开设置参数窗口，并且从后台获取数据填入设置框框中
	function openSetParamsDialog(){
		$('#setParamsDialog').addClass('show');
		$('#setParamsDialog').css('display','block');
        var deviceParas = getDeviceParas();
		$('input[name="app_id"]').val( deviceParas.app_id );
		$('input[name="domain"]').val( deviceParas.domain );
		$('input[name="name"]').val( deviceParas.name );
		$('input[name="password"]').val( deviceParas.password );
		$('input[name="product_id"]').val( deviceParas.product_id );
		$('input[name="ak"]').val( deviceParas.ak );
		$('input[name="sk"]').val( deviceParas.sk );
		$('input[name="projectId"]').val( deviceParas.projectId );
		$('input[name="streamName"]').val( deviceParas.streamName );
		$('input[name="topicUrn"]').val( deviceParas.topicUrn );
	}
	$('#setParamsDialog .confirm-btn').click(function() {
		onSetParamsDialogConfirm();
	});
	$('#setParamsDialog .close-modal').click(function() {
		$('#setParamsDialog').removeClass('show');
		$('#setParamsDialog').css('display','none');
	});


    // 从DIS获取数据
	function getDis() {
		$.ajax({
			url: "/get-dis",
			type: "get",
			contentType: "application/json; charset=utf-8",
			dataType: "json",
			success: function (data) {
				if(data){
                    refreshData(data);
				}
			},
			error: function () {
			}
		});
	}

    // 通过订阅推送获取数据
	function getSub() {
		$.ajax({
			url: "/get-device-data",
			type: "get",
			contentType: "application/json; charset=utf-8",
			dataType: "json",
			success: function (data) {
				if(data){
                    refreshData(data);
				}
			},
			error: function () {
			}
		});
	}

	// 刷新数据
	function refreshData(data){
        console.log(data);
        if(data.luminance&&data.humidity&&data.tempture){

			tempture=data.tempture;
			humidity=data.humidity;
            luxVal = data.luminance;
            motorStatus = data.motorState;

            let dataSa = Number(luxVal);
			let data_tempture=Number(tempture);
			let data_humidity=Number(humidity);


            ydata.push(dataSa);
			ydata.push(data_tempture);
			ydata.push(data_humidity)

            var lineTime = data.eventTime;
            let time = '';
            let year = lineTime.substr(0, 4);
            let month = lineTime.substr(4, 2);
            let day = lineTime.substr(6, 2);
            let hour = lineTime.substr(9, 2);
            let min = lineTime.substr(11, 2);
            let second = lineTime.substr(13, 2);
            time = year + '-' + month + '-' + day + ' '+ hour + ':' + min + ':' + second;
            lineData.push(time);
            if(ydata.length === maxLen){
                ydata.shift();
                lineData.shift();
            }
        }
        // 刷新环境光强
        $("#luxVal").text(luxVal);
		$("#tempture").text(tempture);
		$("#humidity").text(humidity);

        // 刷新光强曲线
        myChart.setOption(option);
        // 刷新开关状态
        var imgStatus = motorStatus ==="ON" ? '.open' : '.close';
        $('.prod-stat-img').removeClass('active');
        $('.prod-stat-img' + imgStatus ).addClass('active');
        $('#motorStatus').text(motorStatus==="ON"?"灯状态：开":"灯状态：关");

		localStorage.setItem("tempture",tempture);
		localStorage.setItem("humidity",humidity);
        localStorage.setItem('luxVal',luxVal);
        localStorage.setItem('motorStatus',motorStatus);
        localStorage.setItem('ydata',JSON.stringify(ydata));
        localStorage.setItem('lineData',JSON.stringify(lineData));
        // 历史数据保存到后台
        var historyData = JSON.stringify({
									"humidity":humidity,
                          			"luminance": luxVal,
									"tempture":tempture,
                          			"lightState": motorStatus,
                          			"ydata": JSON.stringify(ydata),
                          			"lineData": JSON.stringify(lineData)
                          		});
        postHistoryData(historyData);
	}

	// 三级函数，设置参数，把所有输入的参数输入到后台，保存到后台的tempDatabase
	function postDeviceParas() {
		$.ajax({
			url: "/set-paras",
			type: "post",
			contentType: "application/json; charset=utf-8",
            dataType: "text",
			data: JSON.stringify( getDeviceParas() ),
			success: function () {
				openDialog('success','提示', "设置参数成功！");
			},
			error: function () {
			  openDialog('error','提示', "设置参数失败！");
			}
        });
	}
    // 函数，从后台重新获取数据
    function getPreDeviceParas( callback) {
        $.ajax({
            url: "/get-paras",
            type: "get",
            contentType: "application/json; charset=utf-8",
            dataType: "json",
            success: function (data) {
            	if(data && data.name){
            		var jsonStringify = JSON.stringify(data);
                    localStorage.setItem('deviceParas',jsonStringify);
                    console.log("getPreDeviceParas from sever");
				}
                callback && callback();
            },
            error: function () {
                console.log("getPreDeviceParas failed");
            }
        });
    }
    // 保存历史数据到后台
	function postHistoryData(hisData) {
		$.ajax({
			url: "/set-history-data",
			type: "post",
			contentType: "application/json; charset=utf-8",
            dataType: "text",
			data: hisData,
			success: function () {
				console.log("Data Saved");
			},
			error: function () {
			}
        });
	}
    // 从后台获取历史数据
    function getHistoryData() {
        $.ajax({
            url: "/get-history-data",
            type: "get",
            contentType: "application/json; charset=utf-8",
            dataType: "json",
            success: function (data) {
            	if(data && data.luminance&&data.tempture&&data.humidity){
            	    console.log("getHistoryData from sever");

					localStorage.setItem("tempture",data.tempture);
					localStorage.setItem("humidity",data.humidity);
                    localStorage.setItem('luxVal',data.luminance);
                    localStorage.setItem('motorStatus',data.motorState);
                    localStorage.setItem('ydata',data.ydata);
                    localStorage.setItem('lineData',data.lineData);

				}
				else{
				    console.log("Clear HistoryData");
				    localStorage.removeItem('luxVal');
				    localStorage.removeItem('motorStatus');
				    localStorage.removeItem('ydata');
				    localStorage.removeItem('lineData');
				}
            },
            error: function () {
            }
        });
    }

	/* paramsDialog --*/
	function changeComboBox() {
		$("#ctrlMode").change(function() {
			$(".combo-item").removeClass("show");
			if(this.value==="terminal"){
				$("#terminal").addClass("show")
			}
			if(this.value==="timeRange"){
				$("#timeRange").addClass("show")
			}
			if(this.value==="autoLux"){
				$("#autoLux").addClass("show")
			}
		});

		$("#dataSource").change(function() {
			$(".info-tags").removeClass("show");
			if(this.value==="DIS"){
				$("#tunnelInfo").addClass("show")
			}
			if(this.value==="subscribe"){
				$("#sbscrbInfo").addClass("show")
			}
			localStorage.setItem('dataMode',this.value);
		});
	}

    // 设置开关灯
	function setOpenClose() {

		var status_motor=$('#statusCombo_motor option:selected') .val();
		// var status_led=$('#statusCombo_led option:selected' ).val();

		$.ajax({
			url: "/set-open-close",
			type: "post",
			contentType: "application/json; charset=utf-8",
			dataType: "text",
			data: status_motor,
			success: function (data) {
				openDialog('success','提示', "设置成功！");
			},
			error: function () {
				console.error("连接服务器失败！");
			}
		});
	}

    // 设置时间范围
	function setTimeRange() {
		$.ajax({
			url: "/set-time-range",
			type: "post",
			contentType: "application/json; charset=utf-8",
			dataType: "text",
			data: JSON.stringify( getTimeRange() ),
			success: function (data) {
				openDialog('success','提示', "设置参数成功！");
			},
			error: function () {
				openDialog('error','提示', "设置参数失败！");
			}
		});
	}

	// 获取时间范围
	function getTimeRange() {
		var startHour=$("#clock1 .hourNum").text();
		var startMinute=$("#clock1 .minuteNum").text();

		var endHour=$("#clock2 .hourNum").text();
		var endMinute=$("#clock2 .minuteNum").text();


		var startTime = startHour+":"+startMinute;
		var endTime = endHour+":"+endMinute;
		return  {"startTime": startTime,"endTime":endTime}

	}

	return {
        regDevice: regDevice,
		openSetParamsDialog: openSetParamsDialog,
		// setDeviceLuxThreshold: setDeviceLuxThreshold,
		changeComboBox: changeComboBox,
		setOpenClose:setOpenClose,
		setTimeRange:setTimeRange
	};
}($);