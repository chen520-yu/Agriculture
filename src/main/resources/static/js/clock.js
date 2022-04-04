function clickArrow(clockId,minPlus,hourMinute) {
    var $timeNum = $("#" + clockId + " " + "." + hourMinute);
    var timeNum = parseFloat($timeNum.text());
    if(minPlus==="+"){
        timeNum+=1;
    }else{
        timeNum-=1;
    }
    if(hourMinute==="hourNum"){
        if(timeNum<1){
            timeNum="24";
        }
        if(timeNum<10){
            timeNum="0"+timeNum.toString()
        }
        if(timeNum>24){
            timeNum="01"
        }
    }

    if(hourMinute==="minuteNum"){
        if(timeNum<0){
            timeNum="59";
        }
        if(timeNum<10){
            timeNum="0"+timeNum.toString()
        }
        if(timeNum>59){
            timeNum="00"
        }
    }
    $timeNum.text(timeNum);

}
$("#clock1 .hour-zone .time-up-button").click(function () {
    clickArrow("clock1","+","hourNum")
});
$("#clock1 .hour-zone .time-down-button").click(function() {
    clickArrow("clock1","-","hourNum")
});
$("#clock1 .min-zone .time-up-button").click(function() {
    clickArrow("clock1","+","minuteNum")
});
$("#clock1 .min-zone .time-down-button").click(function() {
    clickArrow("clock1","-","minuteNum")
});


$("#clock2 .hour-zone .time-up-button").click(function () {
    clickArrow("clock2","+","hourNum")
});
$("#clock2 .hour-zone .time-down-button").click(function() {
    clickArrow("clock2","-","hourNum")
});
$("#clock2 .min-zone .time-up-button").click(function() {
    clickArrow("clock2","+","minuteNum")
});
$("#clock2 .min-zone .time-down-button").click(function() {
    clickArrow("clock2","-","minuteNum")
});
