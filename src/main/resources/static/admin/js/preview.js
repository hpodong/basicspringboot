(function($) {
    const defaultMaxSize = 500;

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
                    <img src="/admin/images/ico_close_wht.svg" alt="x아이콘" width="10">
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