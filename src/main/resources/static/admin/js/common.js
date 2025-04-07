$(function () {
  const currentPath = window.location.pathname;
  const queryString = window.location.search;
  const referrerUrl = document.referrer;
  const urlParams = new URLSearchParams(queryString);

  $(".calendar input.datepicker, .calendar input.datepicker2").attr("readonly", true);

  $(".board .btnSet a.btn.gr, .board .btnSet a.btn.btn-cancel, .board .btnSet a.btn-bottom.btn-gray-bottom").on("click", function(event) {
    if(referrerUrl) {
      const href = $(this).attr("href");
      const beforeUrl = new URL(referrerUrl);
      if(Object.is(href, beforeUrl.pathname)) {
        event.preventDefault();
        const queryString = beforeUrl.search;
        document.location.href = `${beforeUrl.pathname}${queryString}`;
      }
    }
  });

  $(".sg-file").change( function () {
    const parent_ele = $(this).closest("div.flex-box");
    const fileName = $(this).val().split('\\').pop();
    parent_ele.find("input.upload-name").val(fileName);
  });

  $("#main-nav .gnb > ul a").each(function() {
    const href = $(this).attr("href");
    let pattern = new RegExp(`^${href}(/.*)?$`);
    if(pattern.test(currentPath)) {
      let parent = $(this).data("parent");
      $(this).addClass("on");
      $(`#main-nav .gnb > ul a[data-idx='${parent}']`).addClass("on");
    }
  });

  $("#main-nav .gnb > ul > li > a").click( function () {
    const $this = $(this);
    if ($this.next().length > 0) {
      if ($this.hasClass("on")) {
        $this.removeClass("on");
        $this.next().stop().slideUp();
      } else {
        // 다른 메뉴의 'on' 클래스 및 이미지 초기화
        $("#main-nav .gnb > ul > li > a.on").not(this).removeClass("on");


        $("#main-nav .gnb > ul > li > ul").not($this.next()).stop().slideUp();

        // 클릭한 메뉴에 'on' 클래스 및 이미지 추가
        $this.addClass("on");
        $this.next().stop().slideDown();
      }
    }
  });

  $("#main-nav .gnb > ul > li > ul > li > a.on")
      .parent()
      .parent()
      .addClass("in");

  if ($("#sub").height() > 1200) {
    $("#wrapper").addClass("on");
  }

  $(".topSwiper_wrap .swiper-slide").click(function() {
    const idx = $(this).data("idx");
    const suffix = `${currentPath}${queryString}`;
    const newUrl = `/notice/view?idx=${idx}`;
    if(!Object.is(suffix, newUrl)) location.href = newUrl;
  });

  $(".select_drop").each(function() {
    $(this).niceSelect();
    const paramValue = urlParams.get(this.name);
    if(queryString && paramValue) {
      if($(this).find(`option[value="${paramValue}"]`).length) $(this).val(paramValue);
      $(this).niceSelect("update");
    }
  });

  $("textarea.editor").each(function() {
    $(this).setQLEditor();
  });
});

let currentBtn = null;
function openPopup(id, type, active){
  currentBtn = active;

  if(type == "normal"){
    $(id).attr('data-open-type', 'normal');
    $(id).show();
    $('.popup-bg').show();
  }
  if(type == "fade"){
    $(id).attr('data-open-type', 'fade');
    $(id).fadeIn(200);
    $('.popup-bg').fadeIn(200);
  }

  $("html").css('overflow', 'hidden');
}

function closePopup(is_clear = true){
  $('.popup').each(function(){
    var openedType = $(this).attr('data-open-type');
    if(openedType == "normal"){
      $(this).hide();
      $('.popup-bg').hide();
    }
    if(openedType == "fade"){
      $(this).fadeOut(200);
      $('.popup-bg').fadeOut(200);
    }

    $("html").css('overflow', 'auto');
    if(is_clear) {
      const popup_ele = $(".popup.bg");
      popup_ele.find("input[type='text'], input[type='file'], input[readonly]").not(".notempty").val("");
      popup_ele.find("input[type='radio']").prop('checked', false);
      popup_ele.find("input[type='checkbox']").prop('checked', false);

      if (imgPopupSwiper) {
        imgPopupSwiper.destroy();
        imgPopupSwiper = null;
      }
    }
  })
}

let imgPopupSwiper; // Swiper 인스턴스를 저장할 변수

function openListImgPopup(id, type) {
  // 팝업 열기
  if(type == "normal"){
    $(id).attr('data-open-type', 'normal');
    $(id).show();
    $('.popup-bg').show();
  }
  if(type == "fade"){
    $(id).attr('data-open-type', 'fade');
    $(id).fadeIn(200);
    $('.popup-bg').fadeIn(200);
  }
  $(id).addClass('active');

  // 클릭된 이미지의 src 가져오기
  let clickedImageSrc = $(event.target).attr('src');

  // 팝업 안에 이미지 추가
  $(id).find('.img-area').html('' +
      '<img src="' + clickedImageSrc + '" alt="popup image" />');
}

function viewOpenImgPopup(id, type, clickedImg) {
  // 팝업 열기
  if (type == "normal") {
    $(id).attr("data-open-type", "normal");
    $(id).show();
    $(".popup-bg").show();
  }
  if (type == "fade") {
    $(id).attr("data-open-type", "fade");
    $(id).fadeIn(200);
    $(".popup-bg").fadeIn(200);
  }
  $(id).addClass("active");

  // Swiper가 이미 존재하면 제거 (중복 초기화 방지)
  if (imgPopupSwiper) {
    imgPopupSwiper.destroy(); // 기존 Swiper 삭제
    imgPopupSwiper = null;
  }

  // 이미지 리스트 가져오기
  let clickedIndex = 0; // 클릭한 이미지의 인덱스
  let imgList = $(".imgList img").map(function (index) {
    let imgSrc = $(this).attr("src");
    if ($(this).is(clickedImg)) {
      clickedIndex = index; // 클릭한 이미지의 인덱스 저장
    }
    return '<div class="swiper-slide"><img src="' + imgSrc + '" alt="popup image" /></div>';
  }).get();

  // 생성된 HTML을 swiper-wrapper에 삽입
  /*$(id).find(".swiper-wrapper.img-area").html(imgList.join(""));*/

  // Swiper 새로 초기화 (클릭한 이미지부터 시작)
  imgPopupSwiper = new Swiper(".imgPopupSwiper", {
    slidesPerView: 1,
    loop: true,
    initialSlide: clickedIndex, // 클릭한 이미지부터 시작
    navigation: {
      nextEl: ".imgPopupSwiper-button-next",
      prevEl: ".imgPopupSwiper-button-prev",
    },
  });
}


$(document).ready(function () {
  $('.calendar').on('click', function(){
    $(this).find("input").datepicker("show");
  });

  $("#img_filename").on('change', function () {
    let fileName = $(this).val().split('\\').pop();
    $(".img_filename").val(fileName);
  });

  //탭박스 클릭 이벤트
  $(".tab-box a").on("click", function(){
    $(".tab-box a").removeClass("on");
    $(this).addClass("on");
  });

  $(".tab-box2 a").on("click", function(){
    $(".tab-box2 a").removeClass("on");
    $(this).addClass("on");
  });

  $(".tab-box3 a").on("click", function(){
    $(".tab-box3 a").removeClass("on");
    $(this).addClass("on");
  });

  //알람 팝업 노출, 미노출 이벤트
  $("#alarm_btn").on("click", function(){
    $(".alarm-popup").fadeIn(300);
    get_push_list(true);
  });

  $(".alarm-popup .popup-close").on("click", function(){
    $(".alarm-popup").fadeOut(300);
  });

  let alarm_order_page = 1;
  let can_request_alarm_order = true;

  const get_push_list = (is_refresh) => {
    const list_tag = $("#notification_alarm_list");
    if(is_refresh) {
      alarm_order_page = 1;
      can_request_alarm_order = true;
      list_tag.empty();
    }
    if(can_request_alarm_order) {
      ajaxRequest({
        url: "/recent/push-logs",
        data: {
          p: alarm_order_page,
        },
        onsuccess: (res) => {
          console.log(res);
          if(res.count){
            const addAlarm = (id, count) => {
              const element = document.getElementById(id);
              if (element) {
                if (count === 0) {
                  element.classList.remove('alarm-dot');
                } else {
                  element.classList.add('alarm-dot');
                }
              }
            };
            addAlarm('order_alarm', res.count.order);
            addAlarm('return_alarm', res.count.return);
            addAlarm('consult_alarm', res.count.consult);
          }

          res.list.forEach(row => {
            list_tag.append(`
            <li ${row.is_read ? "" : "class='on'"} data-idx="${row.idx}" data-url="${row.url}">
                <p class="t-rw rw-1 lh1 fs16 fw600 ls-032">${row.title}</p>
                <span class="fs14 fw500 ls-042 lh1 fc666 flex-box gap-20"><em class="fc111 fw500">이름</em>${row.name}</span>
                <div class="flex-box align-items-center justify-content-between mt10">
                    <span class="fs14 fw500 ls-042 lh1 fc666 flex-box gap-20"><em class="fc111 fw500">문의유형</em>${row.category}</span>
                    <span class="fs12 fw400 ls-024 fc999">${timestampToDate(row.created_at, 'yyyy.MM.dd')}</span>
                </div>
            </li>
            `);
          });
          alarm_order_page++;
          can_request_alarm_order = res.list.length === 10;
        },
      });
    }
  }

  $(document).on("click", "#notification_alarm_list li", function() {
    const $this = $(this);
    const idx = $this.data("idx");
    const url = $this.data("url");
    ajaxRequest({
      url: "/recent/push-logs/read",
      data: {
        idx
      },
      onsuccess: (res) => {
        if(res) $this.closest("li").removeClass("on");
        location.href = url;
      }
    })
  });

  $("#notification_alarm_list").sgScroll({
    onbottom: () => {
      get_push_list();
    },
  })


  //헤더 알람 팝업 더보기 클릭 이벤트
  $(".alarm-popup .more-btn").on("click", function(){
    get_push_list();
  });
  
  //데이트타임피커
  $(".datetimepicker").datetimepicker({
    format: "Y-m-d H:i:s",
    step: 10,
    defaultSelect: false,
    defaultDate: false,
    scrollMonth: false,
    scrollTime: false,
    scrollInput: false,
    minTime: '10:00',
    maxTime: '18:00',
    minDate: 0,

    yearStart: new Date().getFullYear(),
    yearEnd: new Date().getFullYear() + 5,
    todayButton: false,
    onSelectDate: function(dp, $input) {
      $input.val("");

    },


  });

  // 연도와 월 순서 변경
  /* $(".datetimepicker").on("click", function() {
    setTimeout(function() {
      var $datepicker = $(".xdsoft_monthpicker");
      var $year = $datepicker.children(".xdsoft_year");
      var $month = $datepicker.children(".xdsoft_month");

      // 연도와 월 순서 변경
      $year.insertBefore($month);
    }, 0);
  }); */
  $.datetimepicker.setLocale('kr');


  $(".xdsoft_year span").each(function() {
    $(this).after('<span class="year-text">년</span>');
  });
  /* $(".xdsoft_datepicker .xdsoft_year .xdsoft_select .xdsoft_option").each(function() {
    $(this).text($(this).text() + "년");
  }); */

  /*//멀티업로드
  $(".multiple-file-attach input[type='file']").change(function(event) {
    const $this = $(this);
    const files = event.target.files;
    const length = files.length;
    const max_count = $this.data("max-count");
    const $file_area_tag = $this.closest(".multiple-file-attach").find(".file-upload-area");
    const $select_box = $this.closest(".file-select-box");
    if(!$file_area_tag.length) {
      SGAlert({
        title: "파일 영역을 생성해주세요. (ex. file-upload-area)"
      });
    } else if($file_area_tag.find("div.file-box").length + length > max_count){
      SGAlert({
        title: `파일은 최대 ${max_count}개만 업로드 가능합니다.`
      });
      $this.val("");
    } else {
      for (let i = 0; i < files.length; i++) {
        const fileBox = `
          <div class="file-box flex-box align-items-center justify-content-between gap-5 wid-fit mt5" data-index="${i}">
            <div class="flex-box align-items-center gap-5">
              <p class="file-name t-rw rw-1">${files[i].name}</p>
            </div>
            <a href="javascript:;" class="delete-btn"><img src="/admin/images/ico_close_wht.svg" alt="" width="10"></a>
          </div>
        `;


        $file_area_tag.append(fileBox);
        $select_box.find(".file-caution-txt").css("display", "none");
      }
    }
  });*/

  // 파일 삭제 기능
  /*$(document).on("click", ".file-upload-area .delete-btn", function() {
    const $parent = $(this).closest(".file-box");

    const file_idx = $(this).data("file-idx");
    const index = $parent.data("index");
    const delete_form = $(this).closest("form");

    if(!Object.is(index, undefined)) {
      let file_input = $(".multiple-file-attach input[type='file']")[0];
      let dataTransfer = new DataTransfer();
      let files = Array.from(file_input.files);
      files.splice(index, 1);
      files.forEach(file => dataTransfer.items.add(file));
      file_input.files = dataTransfer.files;
    }

    if(file_idx != null) delete_form.append(`<input type='hidden' name='delete_file_idx' value='${file_idx}'>`);
    $parent.remove();

    const $file_text = $(".file-caution-txt");

    if (!$('.multiple-file-attach .file-box').length) {
      $file_text.css("display", "inline-block");
    } else {
      $file_text.hide();
    }
  });*/

  //파일 이름 색깔
  $(".file-name").each(function() {
    if ($(this).text() === "선택된 파일이 없습니다.") {
      $(this).css("color", "#bbbbbb");
    } else {
      $(this).css("color", "#222222");
    }
  });

if($(".file-img-box .image-view").is(':visible')){
  $('.page-footer').css('bottom', 'auto');
} else {
  $('.page-footer').css('bottom', '0');
}

  if($(".footer_").is(':visible')){
    $('.page-footer').css('bottom', 'auto');
  } else {
    $('.page-footer').css('bottom', '0');
  }


//   document 끝
});

function setEmail(val){
  $("#email2").val(val.value);
  if(val.value){
    $("#email2").prop("readonly", true);
  } else {
    $("#email2").prop("readonly", false);
  }
}

/**
 * 우편번호 창
 **/
var win_zip = function(frm_name, frm_zipcode, frm_address1, frm_address2, frm_address3, frm_address4) {
  if (typeof daum === 'undefined') {
    alert('다음 juso.js 파일이 로드되지 않았습니다.');
    return false;
  }

  var zip_case = 0;	//0이면 레이어, 1이면 페이지에 끼워 넣기, 2이면 새창

  var complete_fn = function(data){
    // 팝업에서 검색결과 항목을 클릭했을때 실행할 코드를 작성하는 부분.

    // 각 주소의 노출 규칙에 따라 주소를 조합한다.
    // 내려오는 변수가 값이 없는 경우엔 공백('')값을 가지므로, 이를 참고하여 분기 한다.
    var fullAddr = ''; // 최종 주소 변수
    var extraAddr = ''; // 조합형 주소 변수

    // 사용자가 선택한 주소 타입에 따라 해당 주소 값을 가져온다.
    if (data.userSelectedType === 'R') { // 사용자가 도로명 주소를 선택했을 경우
      fullAddr = data.roadAddress;

    } else { // 사용자가 지번 주소를 선택했을 경우(J)
      fullAddr = data.jibunAddress;
    }

    // 사용자가 선택한 주소가 도로명 타입일때 조합한다.
    if (data.userSelectedType === 'R'){
      //법정동명이 있을 경우 추가한다.
      if (data.bname !== ''){
        extraAddr += data.bname;
      }
      // 건물명이 있을 경우 추가한다.
      if (data.buildingName !== ''){
        extraAddr += (extraAddr !== '' ? ', ' + data.buildingName : data.buildingName);
      }
      // 조합형주소의 유무에 따라 양쪽에 괄호를 추가하여 최종 주소를 만든다.
      extraAddr = (extraAddr !== '' ? ' (' + extraAddr + ')' : '');
    }

    // 우편번호와 주소 정보를 해당 필드에 넣고, 커서를 상세주소 필드로 이동한다.
    var of = document[frm_name];

    of[frm_zipcode].value = data.zonecode;

    of[frm_address1].value = fullAddr;
    if(of[frm_address3]) of[frm_address3].value = extraAddr;

    if (of[frm_address4] !== undefined){
      of[frm_address4].value = data.userSelectedType;
    }

    of[frm_address2].focus();
  };

  switch(zip_case) {
    case 1 :	//iframe을 이용하여 페이지에 끼워 넣기
      var daum_pape_id = 'daum_juso_page' + frm_zipcode,
          element_wrap = document.getElementById(daum_pape_id),
          currentScroll = Math.max(document.body.scrollTop, document.documentElement.scrollTop);
      if (element_wrap === null) {
        element_wrap = document.createElement('div');
        element_wrap.setAttribute('id', daum_pape_id);
        element_wrap.style.cssText = 'display:none;border:1px solid;left:0;width:100%;height:300px;margin:5px 0;position:relative;-webkit-overflow-scrolling:touch;';
        element_wrap.innerHTML = '<img src="//i1.daumcdn.net/localimg/localimages/07/postcode/320/close.png" id="btnFoldWrap" style="cursor:pointer;position:absolute;right:0px;top:-21px;z-index:1" class="close_daum_juso" alt="접기 버튼">';
        jQuery('form[name="' + frm_name + '"]').find('input[name="' + frm_address1 + '"]').before(element_wrap);
        jQuery('#' + daum_pape_id).off('click', '.close_daum_juso').on('click', '.close_daum_juso', function(e){
          e.preventDefault();
          jQuery(this).parent().hide();
        });
      }

      new daum.Postcode({
        oncomplete: function(data) {
          complete_fn(data);
          // iframe을 넣은 element를 안보이게 한다.
          element_wrap.style.display = 'none';
          // 우편번호 찾기 화면이 보이기 이전으로 scroll 위치를 되돌린다.
          document.body.scrollTop = currentScroll;
        },
        // 우편번호 찾기 화면 크기가 조정되었을때 실행할 코드를 작성하는 부분.
        // iframe을 넣은 element의 높이값을 조정한다.
        onresize : function(size) {
          element_wrap.style.height = size.height + 'px';
        },
        width : '100%',
        height : '100%'
      }).embed(element_wrap);

      // iframe을 넣은 element를 보이게 한다.
      element_wrap.style.display = 'block';
      break;
    case 2 :	//새창으로 띄우기
      new daum.Postcode({
        oncomplete: function(data) {
          complete_fn(data);
        }
      }).open();
      break;
    default :	//iframe을 이용하여 레이어 띄우기
      var rayer_id = 'daum_juso_rayer' + frm_zipcode,
          element_layer = document.getElementById(rayer_id);
      if (element_layer === null) {
        element_layer = document.createElement('div');
        element_layer.setAttribute('id', rayer_id);
        element_layer.style.cssText = 'display:none;border:5px solid;position:fixed;width:300px;height:460px;left:50%;margin-left:-155px;top:50%;margin-top:-235px;overflow:hidden;-webkit-overflow-scrolling:touch;z-index:10000';
        element_layer.innerHTML = '<img src="//i1.daumcdn.net/localimg/localimages/07/postcode/320/close.png" id="btnCloseLayer" style="cursor:pointer;position:absolute;right:-3px;top:-3px;z-index:1" class="close_daum_juso" alt="닫기 버튼">';
        document.body.appendChild(element_layer);
        jQuery('#' + rayer_id).off('click', '.close_daum_juso').on('click', '.close_daum_juso', function(e){
          e.preventDefault();
          jQuery(this).parent().hide();
        });
      }

      new daum.Postcode({
        oncomplete: function(data) {
          complete_fn(data);
          // iframe을 넣은 element를 안보이게 한다.
          element_layer.style.display = 'none';
        },
        width : '100%',
        height : '100%'
      }).embed(element_layer);

      // iframe을 넣은 element를 보이게 한다.
      element_layer.style.display = 'block';
  }
}

// 테이블 드래그 앤 드롭
let isDown = false;
let isDragging = false;
let startX;
let scrollLeft;

$('.scroll_list_wrap')
    .on('mousedown', function(e) {
      isDown = true;
      isDragging = false;
      $(this).addClass('active');
      startX = e.pageX - $(this).offset().left;
      scrollLeft = $(this).scrollLeft();
    })
    .on('mouseleave mouseup', function() {
      isDown = false;
      setTimeout(() => {
        isDragging = false; // 약간의 지연 후 드래그 종료
      }, 50);
      $(this).removeClass('active');
    })
    .on('mousemove', function(e) {
      if (!isDown) return;
      isDragging = true;
      e.preventDefault();
      const x = e.pageX - $(this).offset().left;
      const walk = (x - startX) * 1; // 드래그 속도 조절
      $(this).scrollLeft(scrollLeft - walk);
    });

// 업로드 양식 다운로드 클릭 이벤트
$('.uploadDown-btn').on('click', function(){
  SGAlert({
    title: "합·포인 경우, 최초 한 행만 작성하시고 상품정보(양식 내 G열 ~N열까지)는 순차적으로 모두 작성해주세요.",
    type: SGAlertType.SUCCESS,
    // onsuccess: (res) => {
    // }
  })
});
