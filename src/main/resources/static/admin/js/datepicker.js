$(function () {
  $(".datepicker").datepicker({
    dateFormat: "yy-mm-dd",
    duration: "fast",
    onSelect: function() {
      $(this).keyup().change();
    }
    // 시간까지 사용할 경우 주석 해제하고 사용하시면 됩니다.
    // onSelect: function(datetext) {
    //     var d = new Date(); // for now

    //     var h = d.getHours();
    //     h = (h < 10) ? ("0" + h) : h ;

    //     var m = d.getMinutes();
    //     m = (m < 10) ? ("0" + m) : m ;

    //     var s = d.getSeconds();
    //     s = (s < 10) ? ("0" + s) : s ;

    //     datetext = datetext + " " + h + ":" + m + ":" + s;

    //     $('.datepicker2').val(datetext);
    // }
  });

  $(".datepicker2").datepicker({
    dateFormat: "yy-mm-dd",
    duration: "fast",
    // 시간까지 사용할 경우 주석 해제하고 사용하시면 됩니다.
    // onSelect: function(datetext) {
    //     var d = new Date(); // for now

    //     var h = d.getHours();
    //     h = (h < 10) ? ("0" + h) : h ;

    //     var m = d.getMinutes();
    //     m = (m < 10) ? ("0" + m) : m ;

    //     var s = d.getSeconds();
    //     s = (s < 10) ? ("0" + s) : s ;

    //     datetext = datetext + " " + h + ":" + m + ":" + s;

    //     $('.datepicker2').val(datetext);
    // }
  });
  $(".datepicker3").datepicker({
    dateFormat: "yy-mm-dd",
    duration: "fast",
    // 시간까지 사용할 경우 주석 해제하고 사용하시면 됩니다.
    // onSelect: function(datetext) {
    //     var d = new Date(); // for now

    //     var h = d.getHours();
    //     h = (h < 10) ? ("0" + h) : h ;

    //     var m = d.getMinutes();
    //     m = (m < 10) ? ("0" + m) : m ;

    //     var s = d.getSeconds();
    //     s = (s < 10) ? ("0" + s) : s ;

    //     datetext = datetext + " " + h + ":" + m + ":" + s;

    //     $('.datepicker2').val(datetext);
    // }
  });

  $.datepicker.setDefaults({
    closeText: "닫기",
    currentText: "오늘",
    prevText: '이전 달',
    nextText: '다음 달',
    monthNames: ['1월', '2월', '3월', '4월', '5월', '6월', '7월', '8월', '9월', '10월', '11월', '12월'],
    monthNamesShort: ['1월', '2월', '3월', '4월', '5월', '6월', '7월', '8월', '9월', '10월', '11월', '12월'],
    dayNames: ['일', '월', '화', '수', '목', '금', '토'],
    dayNamesShort: ['일', '월', '화', '수', '목', '금', '토'],
    dayNamesMin: ['일', '월', '화', '수', '목', '금', '토'],
    weekHeader: "주",
    changeMonth: true, //이게 셀렉트로 선택하게 해주는 데이트피커 기본 옵션
    changeYear: true, //이게 셀렉트로 선택하게 해주는 데이트피커 기본 옵션
    yearRange: "-0:+10", // 10년 범위 설정 (옵션)
    beforeShow: function(input, inst) {
      setTimeout(function(){
        $(inst.dpDiv).find('.ui-datepicker-year option').each(function(){
          var yearText = $(this).text();
          $(this).text(yearText + '년');
        });
      }, 1);
    },
    onChangeMonthYear: function(year, month, inst) {
      setTimeout(function(){
        $(inst.dpDiv).find('.ui-datepicker-year option').each(function(){
          var yearText = $(this).text();
          if(!yearText.includes('년')) { // 이미 년 텍스트가 붙어있는지 확인
            $(this).text(yearText + '년');
          }
        });
      }, 1);
    }
  });
})

