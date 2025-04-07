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
    $.ajax(requestData);
}