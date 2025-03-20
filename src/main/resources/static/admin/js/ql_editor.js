(function($) {
    /**
     * @function $.fn.setQLEditor
     * @description QL 에디터를 세팅합니다.
     */
    $.fn.setQLEditor = function() {
        const $this = this;
        const tag = $this[0];
        $this.hide();
        let id = tag.id;
        let height = $this.data("height");
        let styles = "";
        if(height) styles = `style='height: ${height}'`;
        $this.after(`<div id="${id}_editor" ${styles}></div>`);
        let quill = new Quill(`#${id}_editor`, {
            theme: "snow",
            modules: {
                toolbar: [
                    [{header: [1,2,false] }],
                    ['bold', 'italic', 'underline'],
                    ['image', 'code-block'],
                    [{ list: 'ordered' }, { list: 'bullet' }]
                ]
            },
        });
        quill.root.innerHTML = tag.value;
        quill.on('text-change', function() {
            document.getElementById(id).value = quill.root.innerHTML;
        });

        quill.getModule('toolbar').addHandler('image', function () {
            const fileInput = document.createElement('input');
            fileInput.setAttribute('type', 'file');
            fileInput.setAttribute('accept', 'image/*');

            fileInput.click();

            fileInput.addEventListener("change", function () {  // change 이벤트로 input 값이 바뀌면 실행
                const formData = new FormData();
                const file = fileInput.files[0];
                formData.append('file', file);

                $.ajax({
                    type: 'POST',
                    enctype: 'multipart/form-data',
                    url: '/editor/upload',
                    data: formData,
                    processData: false,
                    contentType: false,
                    success: function (data) {
                        const range = quill.getSelection(); // 사용자가 선택한 에디터 범위
                        quill.insertEmbed(range.index, 'image', data);
                    },
                    error: function (err) {
                        console.error(err);
                    }
                });

            });
        });
    }

})(jQuery);