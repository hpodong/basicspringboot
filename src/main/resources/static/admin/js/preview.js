class MultiFile {
    constructor(idx, name, unique) {
        this.idx = idx;
        this.name = name;
        this.unique = unique;
    }

    toHtml() {
        return `<div class="file-box flex-box align-items-center justify-content-between gap-5 wid-fit">
            <div class="flex-box align-items-center gap-5">
                <p class="file-name t-rw rw-1" >${this.name}</p>
            </div>
            <a href="javascript:;" ${this.unique === undefined ? "" : `data-unique="${this.unique}"`} class="delete-btn" ${!this.idx ? "" : `data-file-idx="${this.idx}"`}><img src="/admin/images/ico_close.svg" alt="" width="10"></a>
        </div>`;
    }
}

(function($) {

    const defaultMaxSize = 500;

    /**
     * @function $.fn.validate
     * @description 이 플러그인은 여러개의 파일 뷰를 보여줍니다.
     * @param {Object} options - 옵션 객체
     * @param {MultiFile[]} [options.default_files] - 초기 파일 목록
     * @param {number} [options.max_size] - 최대 사이즈(MB 기준)
     * @param {number} [options.max_length] - 최대 갯수
     * @param {FileType[]} [options.allows] - 허용할 파일 목록
     * @return jQuery
     */
    $.fn.multifile = function(options) {

        const id = this.attr("id");
        const preHtml = this.prop('outerHTML');
        const {
            default_files= this.data("default_files"),
            max_size,
            max_length,
            allows,
        } = options;
        const dataTransfer = new DataTransfer();
        const filesHtml = default_files?.map(file => file.toHtml()).join("") ?? "";
        const field_idx = `files_field_${id}`;
        const replaceHtml =
            `<div class="multiple-file-attach" id="${field_idx}">
                <div class="file-select-box">
                    <div>
                        <label for="${id}" class="flex-box align-items-center justify-content-center btn bl">파일 찾기</label>
                        <span class="file-caution-txt">선택된 파일이 없습니다.</span>
                    </div>
                    ${preHtml}
                </div>
                <div class="file-upload-area flex-box gap-5 flex-wrap">${filesHtml ?? ""}</div>
            </div>`;
        this.replaceWith(replaceHtml);

        const $newInput = $(`[type='file'][id='${id}']`);

        const $removeText = $(`#${field_idx}`).find(".file-caution-txt");
        if(default_files?.length) $removeText.hide()
        else $removeText.css("display", "inline-block")

        $newInput.change(function(event) {
            const $newHtml = $(`[id='files_field_${id}']`);
            const $fileArea = $newHtml.find(".file-upload-area");
            const $fileBox = $fileArea.find(".file-box");
            const fileLength = $fileBox.length;
            const newFiles = Array.from(event.target.files);
            const newFilesLength = newFiles.length;
            const totalLength = fileLength + newFilesLength;

            if(max_length && totalLength > max_length) {
                SGAlert({
                    title: `최대 ${max_length}개까지만 등록이 가능합니다.`,
                    type: SGAlertType.ERROR
                })
                return;
            }


            for(let index = 0; index < newFilesLength; index++) {
                const file = newFiles[index];
                const size = file.size;
                if(max_size && size > (max_size*1024*1024)) {
                    SGAlert({
                        title: `최대 ${max_size}MB까지 등록이 가능합니다.`,
                        type: SGAlertType.ERROR
                    })
                } else {
                    $fileArea.append(new MultiFile(null, file.name, dataTransfer.items.length).toHtml());
                    dataTransfer.items.add(file);
                }
            }

            $newInput[0].files = dataTransfer.files;

            if(dataTransfer.items.length) {
                $removeText.hide();
            } else {
                $removeText.css("display", "inline-block");
            }
        });

        $newInput.attr("accept", allows?.join(", "));

        // 삭제 버튼 이벤트 위임 등록 추천
        $(document).on("click", `#${field_idx} .delete-btn`, function(e) {

            const unique = $(this).data("unique");
            const file_idx = $(this).data("file-idx");
            const delete_form = $(this).closest("form");

            Array.from(dataTransfer.files).forEach((f, index) => {
                if(Object.is(unique, index)) dataTransfer.items.remove(f);
            });

            $newInput[0].files = dataTransfer.files;

            // DOM에서 파일 제거
            $(this).closest(".file-box").remove();
            if(file_idx != null) delete_form.append(`<input type='hidden' name='delete_file_idx' value='${file_idx}'>`);

            const $field = $(`#${field_idx}`);

            $field.find("a[data-unique]").each(function(index) {
                $(this).data("unique", index);
            });

            const afterTotalCount = $field.find(".file-box").length;
            if (!afterTotalCount) {
                $removeText.css("display", "inline-block");
            } else {
                $removeText.hide();
            }
        });

        return $newInput;
    }

    /**
     * @function $.fn.validate
     * @description 이 플러그인은 input 태그에 이미지뷰를 생성합니다.
     * @param {Object} options - 옵션 객체
     * @param {string} [options.default_image] - 초기 이미지
     * @param {number} [options.width] - 가로 길이
     * @param {number} [options.height] - 세로 길이
     * @param {string} [options.label] - 초기 라벨 요소
     * @param {string} [options.button_label] - 버튼 라벨 요소
     * @param {number} [options.max_size] - 이미지뷰의 최대 사이즈
     * @param {function} [options.onchange] - 이미지 변경 시 호출
     * @return jQuery
     */
    $.fn.preview = function(options) {

        let id = this.attr("id");
        let name = this.attr("name");
        let label = options.label;
        let button_label = options.button_label;

        this.replaceWith(`
        <div class="file-img-box">
            <input class="preview" type="file" name="${name}" id="${id}" accept="image/jpg, image/png, image/jpeg">
            <div class="image-view">
                <div class="photo-delete">
                    <img src="/admin/images/ico_x.svg" alt="x아이콘" width="10">
                </div>
            </div>
            <div>
                <label for="${id}" class="flex-box align-items-center justify-content-center">${button_label ?? "파일 찾기"}</label>
                <span class="file-name">${label ?? "선택된 파일이 없습니다."}</span>
            </div>
        </div> 
        `);
        let input = $(`#${id}`);
        let parent = input.closest(".file-img-box");

        parent.find("input").on("change", function() {
            const file = this.files[0];
            if(file) {
                const reader = new FileReader();
                const img = new Image();

                reader.onload = function (e) {
                    img.src = e.target.result;
                    img.onload = function () {
                        options.default_image = e.target.result;
                        options.width = img.width;
                        options.height = img.height;
                        options.label = file.name;
                        input.setPreview(options);
                        if(options.onchange) options.onchange(img);
                        $(".file-img-box .image-view").css('display', 'block');
                        $(".file-img-box .file-name").css('color', '#222222');
                        $(".page-footer").css("bottom", "auto");
                    };
                };

                reader.readAsDataURL(file);
            }
        });

        parent.find(".photo-delete").on("click", function() {
            let parent = $(this).closest(".file-img-box");
            let input = parent.find("input[type='file']");
            input.resetPreview();
            $(".file-img-box .image-view").css('display', 'none');
            $(".file-img-box .file-name").text(options.label ?? "선택된 파일이 없습니다.").css('color', '#bbbbbb');
            $(".page-footer").css("bottom", "0");
        });

        if(options.default_image) {
            let img = new Image();
            img.src = options.default_image;
            img.onload = function() {
                options.width = img.naturalWidth;
                options.height = img.naturalHeight;
                input.setPreview(options);
            }
        }
        return this;
    }

    /**
     * @function $.fn.setPreview
     * @description 이 플러그인은 input 태그에 이미지뷰를 세팅합니다.
     * * @param {Object} options - 옵션 객체
     * @param {string} [options.default_image] - 초기 이미지
     * @param {number} [options.width] - 가로 길이
     * @param {number} [options.height] - 세로 길이
     * @param {string} [options.label] - 초기 라벨 요소
     * @param {string} [options.button_label] - 버튼 라벨 요소
     * @param {number} [options.max_size] - 이미지뷰의 최대 사이즈
     * @param {function} [options.onchange] - 이미지 변경 시 호출
     */
    $.fn.setPreview = function(options) {
        if(options.default_image) {
            const max_size = options.max_size ?? defaultMaxSize;
            const parent = this.closest(".file-img-box");
            const preview = parent.find(".image-view");
            const filename_element = parent.find(".file-name");
            const originalWidth = options.width;
            const originalHeight = options.height;
            const aspectRatio = originalWidth / originalHeight;

            preview.show().css("background-image", `url('${options.default_image}')`)
            filename_element.text(options?.label);
            parent.find('.photo-delete').css('display', 'flex');

            if (originalWidth > originalHeight) {
                let min = Math.min(originalWidth, max_size);
                preview.css("width", `${min}px`);
                preview.css("height", `${(min / aspectRatio)}px`);
            } else {
                let min = Math.min(originalHeight, max_size);
                preview.css("height", `${min}px`);
                preview.css("width", `${(min * aspectRatio)}px`);
            }

            parent.show();
        }
    }

    /**
     * @function $.fn.resetPreview
     * @description 이 input 태그에 이미지뷰를 초기화합니다.
     */
    $.fn.resetPreview = function() {
        let parent = this.closest(".file-img-box");
        let image_view = parent.find('.image-view');
        image_view.css({
            'width' : '250px',
            'height' : '250px',
            'background-image' : ''
        });
        parent.find('.file-name').text("");
        this.val("");
        image_view.find(".photo-delete").hide();
    }

})(jQuery);