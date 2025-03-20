(function($) {
    /**
     * @function $.fn.sgScroll
     * @description 스크롤바가 가장 하단 기준으로 이벤트를 실행합니다.
     * @param {Object} options 무한 스크롤 옵션
     * @param {function} options.hasindicator 로딩바 유무
     * @param {function} options.ontop 스크롤이 가장 위로 갔을 때의 이벤트
     * @param {function} options.onbottom 스크롤이 아래로 갔을 때의 이벤트
     * @param {function(number)} options.oncustom 스크롤이 값에 따른 이벤트
     * @param {Object.<number, function(number)>} options.onover 스크롤이 지정 값 이상일 경우의 이벤트
     * @param {Object.<number, function(number)>} options.onunder 스크롤이 지정 값 이하일 경우의 이벤트
     * @throws {TypeError} 옵션이 누락되었거나 잘못된 경우 오류가 발생합니다.
     * */
    $.fn.sgScroll = function(options) {
        const $this = $(this);
        let current_position = 0;

        $this.scroll(function() {
            if(!options) throw new TypeError("옵션을 지정해주세요.");

            const {
                hasindicator,
                ontop,
                onbottom,
                onover,
                onunder,
                oncustom,
            } = options;

            const scroll_top = Math.ceil($this.scrollTop());
            const height = Math.ceil($this.outerHeight());
            const scrollHeight = Math.ceil($this[0].scrollHeight);

            const is_top = scroll_top === 0 && ontop;
            const is_bottom = scroll_top + height >= scrollHeight;

            if(is_top) ontop();
            if(is_bottom) {
                if(hasindicator) {
                    $this.removeIndicator();
                    $this.addIndicator();
                }
                onbottom();
            }
            if(oncustom) oncustom(scroll_top);
            if(onover) {
                for (const [value, onevent] of Object.entries(onover)) {
                    if (value <= scroll_top && scroll_top > current_position) onevent(scroll_top);
                }
            }
            if(onunder) {
                for (const [value, onevent] of Object.entries(onunder)) {
                    if (value >= scroll_top && scroll_top < current_position) onevent(scroll_top);
                }
            }
            current_position = scroll_top;
        });
    }

    $.fn.addIndicator = function() {
        const $this = $(this);
        $this.append(`<div class="fetch__loader"></div>`);
    }

    $.fn.removeIndicator = function() {
        const $this = $(this);
        $this.find(".fetch__loader").remove();
    }
})(jQuery)