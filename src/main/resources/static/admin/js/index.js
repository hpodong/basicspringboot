$(function() {

	$(".pagenate .pagination a").click(function() {
		let value = $(this).data("value");
		$("#page").val(value);
		goSearch(true);
	});

	$("#searchForm input[type='submit']").click(function() {
		$("#page").val(1);
	});

	$("#size_select").change(function() {
		$("#searchForm input[name='s']").val(this.value);
		goSearch();
	});

	$("input[name='ma']").change(goSearch);

	$("#all_chk").change(function () {
		let isChecked = $(this).is(":checked");
		$("input[name='idx']").prop("checked", isChecked);
	});
	$("td[data-idx], button[data-idx]").css("cursor", "pointer").on("click", function() {
		let idx = $(this).data("idx");
		location.href = `${location.pathname}/view?idx=${idx}`;
	});
	$("td[data-update-idx], button[data-update-idx], td p[data-idx]").css("cursor", "pointer").on("click", function() {
		let idx = $(this).data("update-idx");
		location.href = `${location.pathname}/update?idx=${idx}`;
	});
	$("td[data-popup-idx], button[data-popup-idx], td p[data-popup-idx]").css("cursor", "pointer").on("click", function() {
		let idx = $(this).data("popup-idx");
		const width = 960;
		const height = 1011.5;
		openWindowPopup({
			url: `${location.pathname}/popup/${idx}`,
			width,
			height,
		})
	});
	$("input[name='idx']").on("change", function() {
		if (!$("input[name='idx']:not(:checked)").length) {
			// 모든 체크박스가 체크되어 있으면 #all_chk를 true로 설정
			$("#all_chk").prop('checked', true);
		} else {
			// 체크박스 중 하나라도 체크 해제되어 있으면 false로 설정
			$("#all_chk").prop('checked', false);
		}
	});
	let month_btn_list = $(".month_btn");
	month_btn_list.each(function() {
		let now = new Date();
		let sd_time = new Date();

		switch (this.value) {
			case "금일":
				break;
			case "1개월":
				sd_time.setMonth(now.getMonth()-1);
				break;
			case "3개월":
				sd_time.setMonth(now.getMonth()-3);
				break;
			case "6개월":
				sd_time.setMonth(now.getMonth()-6);
				break;
			case "1년":
				sd_time.setFullYear(now.getFullYear()-1);
				break;
		}

		let sd_value = $("input[name='sd']").val();
		$(this).data("value", formatDate(sd_time));
		if(Object.is(sd_value, $(this).data("value"))) $(this).addClass("on");

		$(this).on("click", function() {
			if(sd_time) {
				$("input[name='sd']").val($(this).data("value"));
				$("input[name='ed']").val(formatDate(now));
				goSearch();
			}
		});
	});

	function formatDate(date) {
		const year = date.getFullYear();
		const month = String(date.getMonth() + 1).padStart(2, '0'); // 월은 0부터 시작하므로 1을 더합니다
		const day = String(date.getDate()).padStart(2, '0'); // 일자를 2자리로 만듭니다

		return `${year}-${month}-${day}`;
	}

	var date_ = $(".datepicker").datepicker({
		format: "yyyy-mm-dd",
		language: "kr",
		autoclose: true,
		todayHighlight: true,
	});


















});

function openPopup(id, type){
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

function closePopup(){
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
		const popup_ele = $(".popup.bg");
		popup_ele.find("input[type='text'], input[type='file'], input[readonly]").val("");
		popup_ele.find("input[type='radio']").prop('checked', false);
		popup_ele.find("input[type='checkbox']").prop('checked', false);
	})
}