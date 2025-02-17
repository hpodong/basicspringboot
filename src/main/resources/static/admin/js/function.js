/**
 *
 * @description 팝업을 여는 함수
 * @param {Object} options 옵션
 * @param {string} options.url = 팝업의 url
 * @param {int} options.width = 팝업의 가로 길이
 * @param {int} options.height = 팝업의 세로 길이
 * @param {function} options.onListener = 팝업의 세로 길이
 */
const openWindowPopup = (options) => {
    if(!options) {
        SGAlert({
            title: "옵션을 지정해주세요.",
        });
        return;
    } else if(!options.url) {
        SGAlert({
            title: "URL을 지정해주세요."
        });
        return;
    } else if(!options.width) {
        SGAlert({
            title: "가로 길이를 지정해주세요."
        });
        return;
    } else if(!options.height) {
        SGAlert({
            title: "세로 길이를 지정해주세요."
        });
        return;
    }
    let width = options.width;
    let height = options.height;
    const left = (screen.width / 2) - (width / 2);
    const top = (screen.height / 2) - (height / 2);
    window.open(options.url, 'popupWindow', `width=${width}, height=${height}, top=${top}, left=${left}, resizable=yes, scrollbars=yes`);
    window.addEventListener = options.onListener;
}

/**
 * @description 이전 페이지에 메세지를 보내느 함수
 * @param {Object} options 옵션
 * @param {string} options.msg 이전 팝업에 보내는 메세지
 * @param {boolean} options.is_close 팝업 종료 여부
 */
const closeWindowPopup = (options) => {
    if(options?.msg) window.opener.postMessage(options.msg, window.location.origin);
    if(options?.is_close !== false) window.close();
}

const timestampToTime = (value) => {
    const date = new Date(value);

    return date.getFullYear() + '-' +
        String(date.getMonth() + 1).padStart(2, '0') + '-' +
        String(date.getDate()).padStart(2, '0') + ' ' +
        String(date.getHours()).padStart(2, '0') + ':' +
        String(date.getMinutes()).padStart(2, '0') + ':' +
        String(date.getSeconds()).padStart(2, '0');

}

const goSearch = (is_page_nation) => {
    let searchForm = $("#searchForm");
    let page_input = $("#page");
    if(!Object.is(is_page_nation, true)) page_input.val("1");
    searchForm.submit();
}

const logout = () => {
    SGConfirm({
        title: "로그아웃 하시겠습니까?",
        type: SGAlertType.QUESTION,
        onsuccess: (res) => {
            if(res) {
                $.ajax({
                    url: "/admin/logout",
                    type: "POST",
                    async: false,
                    success: function(res) {
                        SGAlert({
                            title: "로그아웃 되었습니다.",
                            type: SGAlertType.SUCCESS,
                            onsuccess: (res) => {
                                location.replace("/admin");
                            }
                        });
                    },
                    error: function(err) {
                        SGAlert({
                            title: err,
                            type: SGAlertType.ERROR
                        });
                    }
                })
            }
        }
    })
}

const formatNumberToString = (number, length = 4) => {
    return String(number).padStart(length, '0');
}

const getCurrentParams = () => {
    const currentUrl = window.location.href;
    const url = new URL(currentUrl);
    return url.searchParams;
}

const getParamValue = (key) => {
    const params = getCurrentParams();
    return params.get(key);
}

const setData = (data) => {
    for (const key in data) {
        if (data.hasOwnProperty(key)) {
            let input_element = $(`[name='${key}']`);
            let value = data[key];
            if(Object.is(true, value)) {
                value = 1;
            } else if(Object.is(false, value)) {
                value = 0;
            }
            if(input_element.length) {
                let inputType = input_element.prop("tagName")?.toLocaleLowerCase();
                switch (inputType) {
                    case "select": {
                        input_element.find(`option[value='${value}']`).prop("selected", true).change();
                        if(input_element.hasClass("select_drop")) input_element.val(value).niceSelect("update");
                        break;
                    }
                    default: {
                        let type = input_element.attr("type");
                        switch (type) {
                            case "radio":
                                input_element.filter(`[value='${value}']`).prop("checked", true).change();
                                break;
                            default:
                                if(typeof value === "number") {
                                    input_element.val(value.toLocaleString());
                                } else {
                                    input_element.val(value);
                                }
                        }
                    }
                }
            }
        }
    }
}

const initialCheckbox = (inputName, values) => {
    $(`input[name='${inputName}'][type='checkbox']`).each(function() {
        let value = parseInt(this.value);
        if(values.includes(value)) $(this).prop("checked", true).change();
    });
}

/**
 * @function SGAlert
 * @description 알럿 메세지
 * @param options - 옵션 객체
 * @param {string} options.title - 알럿의 제목
 * @param {string} options.text - 알럿의 내용
 * @param {SGAlertType} options.type - 알럿의 종류
 * @param {function} options.onsuccess - 알럿의 결과
 * @returns {*}
 * @constructor
 */
const SGAlert = (options) => {
    return Swal.fire({
        title: options?.title ?? "알림",
        text: options?.text,
        icon: options?.type ?? SGAlertType.WARNING,
        confirmButtonText: "확인",
        confirmButtonColor: "#004CB5"
    }).then((result) => {
        if(options?.onsuccess) options?.onsuccess(result.isConfirmed);
    });
}

/**
 * @function SGConfirm
 * @description 컨펌 메세지
 * @param options - 옵션 객체
 * @param {string} options.title - 알럿의 제목
 * @param {string} options.text - 알럿의 내용
 * @param {SGAlertType} options.type - 알럿의 종류 (기본 SWType.QUESTION)
 * @param {function} options.onsuccess - 알럿의 결과
 * @returns {*}
 * @constructor
 */
const SGConfirm = (options) => {
    return Swal.fire({
        title: options?.title ?? "알림",
        text: options?.text,
        icon: options?.type ?? SGAlertType.QUESTION,
        showCancelButton: true,
        confirmButtonText: "확인",
        confirmButtonColor: "#004CB5",
        cancelButtonText: "취소",
    }).then((result) => {
        if(options?.onsuccess) options?.onsuccess(result.isConfirmed);
    });
}

/**
 * @function SGProcess
 * @description 로딩 메세지
 * @param options - 옵션 객체
 * @param {string} options.title - 알럿의 제목
 * @param {string} options.text - 알럿의 내용
 * @param {SGAlertType} options.type - 알럿의 종류 (기본 SWType.QUESTION)
 * @returns {*}
 * @constructor
 */
const SGProcess = (options) => {
    return Swal.fire({
        title: options?.title ?? "알림",
        text: options?.text,
        icon: options?.type,
        didOpen: () => Swal.showLoading(),
        allowOutsideClick: false
    })
}

const SGAlertType = {
    WARNING: 'warning',
    SUCCESS: 'success',
    QUESTION: 'question',
    ERROR: 'error',
};

const APStatus = {
    ACTIVATED: 'activated',
    PAUSED: 'paused'
};

const apStatusToString = (value) => {
    switch (value) {
        case "ACTIVATED": return "activated";
        case "PAUSED": return "paused";
        default: return "";
    }
}

const ajaxFromJson = (options) => {
    $.ajax({
        url: options.url,
        data: options.data,
        type: options.type ?? "POST",
        async: options.async ?? false,
        dataType: "JSON",
        success: !options?.onsuccess ? null : (res) => options.onsuccess(res),
        error: (err) => {
            console.error(err);
            SGAlert({
                title: "알 수 없는 오류가 발생했습니다.",
                type: SGAlertType.ERROR,
                onsuccess: (res) => {
                    if (res) location.reload()
                }
            });
        }
    })
}

const moveToLoginPage = (msg) => {
    msg ??= "세션이 만료되었습니다.\n로그인 하시겠습니까?";
    SGConfirm({
        title: msg,
        type: SGAlertType.ERROR,
        onsuccess: (res) => {
            if(res) location.replace("/admin/login");
        }
    });
}

/**
 * @function ajaxRequest
 * @description AJAX 리퀘스트 기본 요청
 * @param {Object} options - 옵션 객체
 * @param {string} [options.url] - 요청 URL
 * @param {Object} [options.data] - 요청 데이터 파라미터
 * @param {string} [options.type] - 요청 메소드
 * @param {boolean} [options.async] - 동기 여부
 * @param {function} [options.onsuccess] - 완료 시 동작
 * @param {function} [options.onerror] - 오류 시 동작
 * @param {boolean} [options.processData] - 기본적으로 query string으로 변환되는 것을 방지
 * @param {boolean} [options.contentType] - 파일이나 Blob 객체 전송 시 사용
 * @param {boolean} [options.isFormData] - 폼데이터 여부
 * @param {boolean} [options.isJson] - JSON데이터 여부
 */
const ajaxRequest = (options) => {
    let requestData = {
        url: options?.url,
        data: options?.data,
        type: options?.type ?? "POST",
        async: options?.async ?? false,
        success: !options?.onsuccess ? null : (res) => options.onsuccess(res),
        error: options.onerror,
        statusCode: {
            401: () => moveToLoginPage(),
            403: () => moveToLoginPage("세션이 만료되었습니다. 로그인 하시겠습니까?")
        }
    };
    if(options.processData !== undefined) requestData.processData = options.processData;
    if(options.contentType !== undefined) requestData.contentType = options.contentType;
    if(options.isFormData) {
        requestData.processData = false;
        requestData.contentType = false;
    }
    if(options.isJson) {
        requestData.contentType = "application/json"
        requestData.data = JSON.stringify(requestData.data);
        requestData.dataType = "JSON";
    }
    $.ajax(requestData);
}

const refreshYearSelect = (select) => {
    select.find("option").not("option[value='']").empty();
    let curYear = new Date().getFullYear();
    for(let year = curYear; year <= curYear+10; year++) {
        select.append(`<option value="${year}">${year}년</option>`);
    }
    select.niceSelect("update");
}

const refreshMonthSelect = (select) => {
    select.find("option").not("option[value='']").empty();
    for(let month = 1; month <= 12; month++) {
        select.append(`<option value="${month}">${month}월</option>`);
    }
    select.niceSelect("update");
}