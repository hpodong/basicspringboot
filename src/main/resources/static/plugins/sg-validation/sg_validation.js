/**
 * @constant SGValidateType
 * @description 파일 유효성 종류
 * @return SGValidateType
 * */
const SGValidateType = {
    CELL: "cell",
    EMAIL: "email",
    ID: "id",
    PASSWORD: "password",
    NUMBER: "number",
    PRICE: "price",
    DATE: "date",
    TIMESTAMP: "timestamp",
    FILE: "file",
    URL: "url",
    LINK: "link",
    PATH: "path",
    EDITOR: "editor",
    QL_EDITOR: "ql_editor",
    SELECT: "select",
    UPPER: "upper",
    UPPER_OR_NUMBER: "upper_or_number",
    BUSINESS_NUMBER: "business_number",
    KR: "korean",
};



/**
 * @constant SGValidateType
 * @description 파일 종류
 * @return SGValidateType
 * */
const FileType = {
    PDF: "application/pdf",
    PNG: "image/png",
    JPG: "image/jpg",
    JPEG: "image/jpeg",
    IMAGES: "image/*",
};

(function($) {

    const IS_SHOW_ALERT = false;
    const ON_CHANGE_CHECK = false;
    const FILE_OPTION_AUTO_EMPTY = true;
    const EDITOR_PATH = "/smarteditor/SmartEditor2Skin.html";

    const price_regex = /\D/g;
    const number_regex = /[^0-9]/g;
    const id_regex = /^[a-zA-Z0-9]+$/;
    /*const cell_regex = /^\d{3}-\d{3,4}-\d{4}$/;*/
    const cell_regex = /^(02-\d{3,4}-\d{4}|\d{3}-\d{3,4}-\d{4})$/;
    const email_regex = /^[0-9a-zA-Z]([-_.]?[0-9a-zA-Z])*@[0-9a-zA-Z]([-_.]?[0-9a-zA-Z])*.[a-zA-Z]{2,3}$/i;


    /* 대문자 미포함 */
    const password_regex = new RegExp(/^(?=.*[a-z])(?=.*\d)(?=.*[@$!%*?&])[a-z\d@$!%*?&]{8,20}$/);
    /* 대문자 포함 */
    /*const password_regex = new RegExp(/^(?=.*[a-z])(?=.*\d)(?=.*[@$!%*?&])[A-Za-z\d@$!%*?&]{8,20}$/);*/
    const business_number_regex = new RegExp(/^\d{3}-\d{2}-\d{5}$/);
    const timestamp_regex = new RegExp(/^\d{4}-\d{2}-\d{2}T\d{2}:\d{2}:\d{2}(?:\.\d+)?(?:Z|[\+\-]\d{2}:\d{2})$/);
    const kr_regex = /^[가-힣]+$/;
    const url_regex = /^(https?|http):\/\/(-\.)?([^\s\/?\.#-]+\.?)+(\/[^\s]*)?$/i;

    const regexFromSGType = function(type) {
        if(!type) return null;
        switch (type) {
            case SGValidateType.CELL: return cell_regex;
            case SGValidateType.PRICE: return price_regex;
            case SGValidateType.ID: return id_regex;
            case SGValidateType.EMAIL: return email_regex;
            case SGValidateType.PASSWORD: return password_regex;
            case SGValidateType.BUSINESS_NUMBER: return business_number_regex;
            case SGValidateType.TIMESTAMP: return timestamp_regex;
            case SGValidateType.KR: return kr_regex;
            case SGValidateType.URL:
            case SGValidateType.LINK: return url_regex;
            default: return null;
        }
    }

    /**
     * @function $.fn.isValidate
     * @description 폼 유효성 검사 통과 여부
     * @returns {boolean}
     */
    $.fn.isValidate = function () {
        const $this = $(this);
        return !$this.find("[data-has_validation=true]:visible").length;
    }

    /**
     * @function $.fn.addValidation
     * @description 폼 입력 요소를 검증하고, 유효하지 않은 경우 메시지를 표시합니다.
     * @param {Object} options - 발리데이션 옵션
     * @param {Object} options.fileoptions - 파일 옵션
     * @param {FileType[]} options.fileoptions.types - 허용할 파일 종류
     * @param {number} options.fileoptions.minsize - 파일 최소 사이즈(MB)
     * @param {number} options.fileoptions.maxsize - 파일 최대 사이즈(MB)
     * @param {boolean} options.fileoptions.autoempty - 유효성 검사 시 파일 자동으로 비움 여부
     * @param {Object.<string, function(string || number || string[] || FileList[], RegExp)>} options.cases - 유효성 검사 조건 { "메시지": (value, regex) => boolean }
     * @param {any} options.defaultvalue - 초기 밸류값
     * @param {SGValidateType} options.regex - 정규식 패턴
     * @param {number} options.minlength - 최소 길이
     * @param {number} options.maxlength - 최대 길이
     * @param {boolean} options.showalert - 알럿 표시 여부
     * @param {boolean} options.onchangecheck - onchange 체크 여부
     * @param {jQuery[input]} options.focusElement - 유효성 검사에 걸릴 때 포커싱할 태그
     * @param {function} options.onvalidate - 유효성 검사에 통과되었을 때 함수
     * @param {function} options.oninvalidate - 유효성 검사에 걸렸을 때 함수
     * @returns {jQuery}
     */
    $.fn.addValidation = function (options) {
        const $this = this;
        const $form = $this.closest("form");
        const {
            minlength,
            maxlength,
            defaultvalue,
            regex,
            fileoptions,
            focusElement,
        } = options;
        const input_type = $this.attr("type");
        const tag_name = $this.prop("tagName")?.toLocaleLowerCase();

        if(minlength) $this.attr("minlength", minlength);
        if(maxlength) $this.attr("maxlength", maxlength);
        if(fileoptions) {
            const {
                types
            } = fileoptions;
            if(types && types.length) {
                $this.attr("accept", types.join(", "))
            }
        }

        if(Object.is(SGValidateType.EDITOR, regex) && Object.is(tag_name, "textarea")) $this.setEditor();

        $this.changehandler(options);

        const {
            cases,
        } = options;
        if(cases) {
            $form.submit(function(event) {
                $this.removeValidationText(focusElement);
                $this.submitHandler(event, options);
            });
        }

        if(defaultvalue) {
            switch (tag_name) {
                case "select":
                    $this.val(defaultvalue).change();
                    $this.niceSelect("update");
                    break;
                case "textarea":
                    $this.val(defaultvalue).keyup();
                    break;
                default:
                    switch (input_type) {
                        case "radio":
                        case "checkbox":
                            if(Array.isArray(defaultvalue)) {
                                $this.each(function() {
                                    const $checked =  $(this);
                                    if(defaultvalue.includes($checked.val().trim())) $checked.prop("checked", true).change();
                                });
                            } else {
                                $this.filter(`[value='${defaultvalue}']`).prop("checked", true).change();
                            }
                            break;
                        default:
                            $this.val(defaultvalue).keyup();
                    }
            }
        }
        return $this;
    };

    $.fn.changehandler = function(options) {
        const $this = this;
        const input_type = $this.attr("type");
        const tag_name = $this.prop("tagName")?.toLocaleLowerCase();

        const {
            fileoptions,
            focusElement,
            maxlength,
            regex,
        } = options;

        switch (tag_name) {
            case "select":
                $this.change(() => $this.changevalue(options, focusElement));
                break;
            case "textarea":
                if(Object.is(SGValidateType.QL_EDITOR, regex)) {
                    $this.parent().on("input", () => $this.changevalue(options, focusElement));
                } else {
                    $this.change(() => $this.changevalue(options, focusElement));
                }
                break;
            default:
                switch (input_type) {
                    case "radio":
                    case "checkbox":
                        $this.change(function() {
                            if(maxlength) {
                                const $check_element = $(this);
                                const length = $this.filter(":checked").length;
                                if(length > maxlength) $check_element.prop("checked", false);
                            }
                            $this.changevalue(options, focusElement);
                        });
                        break;
                    case "file":
                        $this.change(function() {
                            const $file_tag = $(this);
                            const files = $file_tag.prop("files");
                            const length = files.length;
                            $this.changevalue(options, focusElement);
                            if(fileoptions && length) {
                                const {
                                    minsize,
                                    maxsize,
                                    autoempty = FILE_OPTION_AUTO_EMPTY
                                } = fileoptions;
                                if(autoempty) {
                                    if(maxlength && length > maxlength) $file_tag.val("");
                                    $.each(files, (index, file) => {
                                        if(file) {
                                            const under_size = minsize && file.size < minsize*1024*1024;
                                            const over_size = maxsize && file.size > maxsize*1024*1024;
                                            if(under_size || over_size) {
                                                $file_tag.val("");
                                                return false;
                                            }
                                        }
                                    });
                                }
                            }
                        });
                        break;
                    case "hidden":
                        $this.change(() => $this.changevalue(options, focusElement));
                        break;
                    default:
                        $this.keyup(() => $this.changevalue(options, focusElement));
                }
        }
    }

    $.fn.changevalue = function(options) {
        const $this = $(this);

        const {
            regex,
            onchangecheck = ON_CHANGE_CHECK,
            focusElement
        } = options;

        $this.removeValidationText(focusElement);

        let value = $this.val().trim();
        switch (regex) {
            case SGValidateType.CELL:
                if(value.startsWith("02")) {
                    $this.attr("maxlength", 12);
                    $this.val(value
                        .replace(/[^0-9]/g, '')
                        .replace(/^(\d{0,2})(\d{0,4})(\d{0,4})$/g, "$1-$2-$3").replace(/(\-{1,2})$/g, ""));
                } else {
                    $this.attr("maxlength", 13);
                    $this.val(value
                        .replace(/[^0-9]/g, '')
                        .replace(/^(\d{0,3})(\d{0,4})(\d{0,4})$/g, "$1-$2-$3").replace(/(\-{1,2})$/g, ""));
                }
                break;
            case SGValidateType.NUMBER:
                if(value.length) $this.val(value.replace(number_regex, ""));
                else $this.val("");
                break;
            case SGValidateType.PRICE:
                let price_value = value.replace(price_regex, "");
                if(price_value) $this.val(parseInt(price_value, 10).toLocaleString());
                else $this.val("");
                break;
            case SGValidateType.ID:
                $this.val(value.replace(/[^a-zA-Z0-9]/g, ""));
                break;
            case SGValidateType.KR:
                $this.val(value.replace(/[^가-힣]/g, ""));
                break;
            case SGValidateType.BUSINESS_NUMBER:
                value = value.replace(/[^0-9]/g, "");

                if (value.length > 3 && value.length <= 5) {
                    $this.val(`${value.slice(0, 3)}-${value.slice(3)}`);
                } else if (value.length > 5) {
                    $this.val(`${value.slice(0, 3)}-${value.slice(3, 5)}-${value.slice(5, 10)}`);
                } else {
                    $this.val(value);
                }
                break;
        }
        if(onchangecheck) $this.submitHandler(null, options);
        return $this;
    }

    $.fn.getValue = function(options) {
        const $this = $(this);
        const input_type = $this.attr("type");
        const tag_name = $this.prop("tagName")?.toLocaleLowerCase();

        const {regex} = options;

        switch (tag_name) {
            case "select":
                return $this.val()?.trim();
            case "textarea":
                if(Object.is(SGValidateType.EDITOR, regex)) {
                    const id = $this.prop("id");
                    return oEditors.getById[id].getIR().replace("<p><br></p>", "");
                } else if(Object.is(SGValidateType.QL_EDITOR, regex)) {
                    return $this.parent().find(".ql-editor p").html().replace("<br>", "").trim();
                }
                return $this.val()?.trim();
            default:
                switch (input_type) {
                    case "file":
                        return $this.prop("files");
                    case "radio":
                        return $this.filter(":checked").val();
                    case "checkbox":
                        return $this.filter(":checked").map(function() {return this.value;}).get();
                    default:
                        return $this.val()?.trim();
                }
        }
    }

    $.fn.validationHandler = function(event, options) {
        const $this = $(this);
        const form = $this.closest("form");
        const {
            cases,
            regex,
            showalert = IS_SHOW_ALERT,
            focusElement,
            onvalidate,
            oninvalidate,
        } = options;

        const invalid_messages = [];
        for (const [message, condition] of Object.entries(cases)) {
            if (condition($this.getValue(options), regexFromSGType(regex))) {
                invalid_messages.push(message);
                break;
            }
        }
        let is_validated = !invalid_messages.length;
        if(!is_validated) {
            if(event) event.preventDefault();
            const message = invalid_messages[0];
            if(oninvalidate) oninvalidate(message);
            if(showalert) {
                alert(message);
                if(event) event.stopImmediatePropagation();
            } else {
                if(!$this.hasValidationText(focusElement)) {
                    $this.addValidationText(message, focusElement);
                }
            }
            if(event) form.find("[data-has_validation=true]").first().focus();
        } else {
            if(onvalidate) onvalidate();
            $this.removeValidationText(focusElement);
        }
    }

    $.fn.submitHandler = function(event, options) {
        const $this = this;
        const {
            regex,
        } = options;
        const input_type = $this.attr("type");
        const tag_name = $this.prop("tagName")?.toLocaleLowerCase();
        const include_types = [SGValidateType.FILE, SGValidateType.EDITOR, SGValidateType.QL_EDITOR];
        const has_validation = include_types.some(type => Object.is(type, regex))
            || $this.is(":visible")
            || Object.is(tag_name, "select")
            || Object.is(input_type, "hidden");
        try {
            if(has_validation) {
                $this.validationHandler(event, options);
            }
        } catch (e) {
            event.stopImmediatePropagation();
            event.preventDefault();
            console.error(e);
            alert(e);
        }
    }

    /**
     * @function $.fn.removeValidationText
     * @description 유효성 검사 문구를 지웁니다.
     * @param {jQuery[element]} $focusElement - 유효성 검사에 걸릴 때 포커싱할 태그
     */
    $.fn.removeValidationText = function($focusElement) {
        const $this = $(this);
        if($focusElement) {
            $focusElement.find("p.invalidation-txt").remove();
        } else {
            $this.next("p.invalidation-txt").remove();
        }
        $this.attr("data-has_validation", false);
    }

    /**
     * @function $.fn.addValidationText
     * @description 유효성 검사 문구를 추가합니다.
     * @param {string} message - 추가할 메세지
     * @param {jQuery[element]} $focusElement - 유효성 검사에 걸릴 때 포커싱할 태그
     */
    $.fn.addValidationText = function(message, $focusElement) {
        const $this = $(this);
        if($focusElement) {
            $focusElement.append(`<p class='invalidation-txt'>${message}</p>`);
        } else {
            $this.after(`<p class='invalidation-txt'>${message}</p>`);
        }
        $this.attr("data-has_validation", true);
    }

    /**
     * @function $.fn.hasValidationText
     * @description 유효성 검사 텍스트가 있는지 여부
     * @param {jQuery[element]} $focusElement - 유효성 검사에 걸릴 때 포커싱할 태그
     */
    $.fn.hasValidationText = function($focusElement) {
        if($focusElement) {
            return $focusElement.find("p.invalidation-txt").length > 0;
        } else {
            return $(this).data("has_validation");
        }
    }

    let oEditors = [];

    $.fn.setEditor = function() {
        const $this = $(this);
        const id = $this.prop("id");
        if(!id) {
            alert("에디터 아이디를 지정해주세요.");
        } else {
            nhn.husky.EZCreator.createInIFrame({
                oAppRef: oEditors,
                elPlaceHolder: id,
                sSkinURI: EDITOR_PATH,
                htParams : {
                    bUseToolbar : true,				// 툴바 사용 여부 (true:사용/ false:사용하지 않음)
                    bUseVerticalResizer : true,		// 입력창 크기 조절바 사용 여부 (true:사용/ false:사용하지 않음)
                    bUseModeChanger : true,			// 모드 탭(Editor | HTML | TEXT) 사용 여부 (true:사용/ false:사용하지 않음)
                    fOnBeforeUnload : function(){
                        //alert("아싸!");
                    }
                }, //boolean
                fOnAppLoad : function(){
                    //예제 코드
                    //oEditors.getById["contents"].exec("PASTE_HTML", ["로딩이 완료된 후에 본문에 삽입되는 text입니다."]);
                },
                fCreator: "createSEditor2"
            });
        }
    }


})(jQuery);

/**
 * @function fileMaxSizeCheck
 * @description 파일 최대 사이즈 체크
 * @param {number} size - 최대 파일 크기(MB 기준)
 * @param {FileList[]} files - 파일 목록
 * @return boolean
 */
const fileMaxSizeCheck = function(size, files) {
    let value = true;
    $.each(files, (index, file) => {
        if(file && file.size > size*1024*1024) {
            value = false;
            if(!value) return false;
        }
    });
    return value;
}

/**
 * @function fileMinSizeCheck
 * @description 파일 최소 사이즈 체크
 * @param {number} size - 최소 파일 크기(MB 기준)
 * @param {FileList[]} files - 파일 목록
 * @return boolean
 */
const fileMinSizeCheck = function(size, files) {
    let value = true;
    $.each(files, (index, file) => {
        if(file && file.size < size*1024*1024) {
            value = false;
            if(!value) return false;
        }
    });
    return value;
}