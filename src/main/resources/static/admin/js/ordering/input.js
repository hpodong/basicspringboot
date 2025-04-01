let category_idx = null;

const addProductInfo = (row) => {
    const length = $(".inputForm .product-box").length + 1;
    const html = productInfoCard(length, row);
    $(".inputForm").append(html);

    const $this = $(`.product-box[data-num='${length}']`);

    $this.find("input[name='company_name']").addValidation({
        cases: {
            "강사(업체명)을 입력해주세요": (value) => !value.length
        },
        maxlength: 100,
        focusElement: $this,
        defaultvalue: row?.name
    });

    $this.find("input[name='height']").addValidation({
        regex: SGValidateType.NUMBER,
        maxlength: 3,
        cases: {
            "키를 입력해주세요": (value) => !value.length
        },
        focusElement: $this,
        defaultvalue: row?.height
    });

    $this.find("input[name='weight']").addValidation({
        regex: SGValidateType.NUMBER,
        maxlength: 3,
        cases: {
            "몸무게를 입력해주세요": (value) => !value.length
        },
        focusElement: $this,
        defaultvalue: row?.weight
    });

    $this.find("input[name='shoe_size']").addValidation({
        regex: SGValidateType.NUMBER,
        maxlength: 3,
        cases: {
            "운동화 사이즈를 입력해주세요": (value) => !value.length
        },
        focusElement: $this,
        defaultvalue: row?.shoe_size
    });

    $this.find("input[name='not_use']").each(function() {
        const $not_use = $(this);
        const $parent = $not_use.closest("td");
        const $product_idx = $parent.find("input[type='hidden']");

        $not_use
            .change(function() {
                if($not_use.is(":checked")) {
                    $parent.find(".productNameAddArea").empty();
                    $product_idx.val("").change();
                    $parent.find("button[data-category_idx]").hide();
                } else {
                    $parent.find("button[data-category_idx]").show();
                }
            })
            .addValidation({
                cases: {
                    "상품을 선택해주세요.": (values) => {
                        return !values.length && !$product_idx.val().length;
                    }
                },
                focusElement: $this,
            });
    });

    if(row) {
        setProduct($this, "blade_idx", row?.blade_idx, row?.blade);
        setProduct($this, "foot_pocket_idx", row?.foot_pocket_idx, row?.foot_pocket);
        setProduct($this, "water_rail_idx", row?.water_rail_idx, row?.water_rail);
        setProduct($this, "sticker_idx", row?.sticker_idx, row?.sticker);
    }
}

const categoryNameFromRowAndProductCategoryIdx = (row, pc_idx) => {
    if(!row) return null;
    switch (pc_idx) {
        case 1: return row?.blade;
        case 2: return row?.foot_pocket;
        case 3: return row?.water_rail;
        case 4: return row?.sticker;
    }
}

const productInfoCard = (index, row) => {

    let category_heads = ``;
    let category_bodies = ``;
    product_categories.forEach(pc => {
        const initial = categoryNameFromRowAndProductCategoryIdx(row, pc.idx);
        category_heads += `
			<th>
				${pc.name}
				<span class="required">*</span>
			</th>
			`;
        category_bodies += `
			<td class="product-field">
				<div class="flex-box flex-column align-items-baseline gap-5">
					<div class="flex-box gap-5">
						<button data-category_idx="${pc.idx}" type="button" class="btn bl max150" onClick="openPopup('#searchProductPopup', 'normal', this)">조회
						</button>
						<div class="flex-box align-items-center productNameAddArea">
							<!--<button type="button" class="delete-btn"><img src="/admin/images/ico_close.svg" width="12"></button>-->
							<p class="t-rw rw-1 select-name"></p>
						</div>
					</div>
					<div class="check_box">
						<input type="checkbox" id="not_use_${index}_${pc.idx}" name="not_use" value="true"/>
						<label for="not_use_${index}_${pc.idx}">미사용</label>
					</div>
				</div>
				<input type="hidden" name="${pc.input_name}" data-category-hidden-idx="${pc.idx}" ${row ? `data-field_name="${pc.name}" data-initial="${initial}"` : ""}>
			</td>
			`;
    });

    console.log(row);

    return `<div class="product-box" data-num="${index}">
			<div class="flex-box gap-10 align-items-center">
				<div class="tbl_box tbl_box5">
					<table class="row_line">
						<caption>상품정보</caption>
						<colgroup>
							<col width="180px"/>
							<col width="*"/>
							<col width="16%"/>
							<col width="16%"/>
							<col width="16%"/>
							<col width="16%"/>
						</colgroup>
						<thead>
						<tr>
							<th>강사(업체명)<span class="required">*</span></th>
							<th>키/몸무게/운동화<span class="required">*</span></th>
							${category_heads}
						</tr>
						</thead>
						<tbody>
						<tr>
							<td>
								<input type="text" name="company_name" ${row ? `data-initial="${row.name ?? ""}" data-field_name="강사(업체명)"` : ""}/>
							</td>
							<td>
								<div class="flex-box gap-5 align-items-center">
									<div class="flex-box flex-column gap-5">
										<input type="text" name="height" placeholder="키" ${row ? `data-initial="${row.height ?? ""}" data-field_name="키"` : ""}/>
									</div>
									/
									<div class="flex-box flex-column gap-5">
										<input type="text" name="weight" placeholder="몸무게" ${row ? `data-initial="${row.weight ?? ""}" data-field_name="몸무게"` : ""}/>
									</div>
									/
									<div class="flex-box flex-column gap-5">
										<input type="text" name="shoe_size" placeholder="운동화" ${row ? `data-initial="${row.shoe_size ?? ""}" data-field_name="운동화"` : ""}/>
									</div>
								</div>
							</td>
							${category_bodies}
						</tr>
						</tbody>
					</table>
				</div>
				<button type="button" class="table-delete-btn"><img src="/admin/images/ico_close_wht.svg" width="12"/></button>
			</div>
		</div>`
}

const setProduct = ($this, input_name, idx, name) => {
    const $parent = $this.find(`input[name='${input_name}']`).closest(".product-field");
    if(idx) {
        $this.find(`input[name='${input_name}']`)
            .val(idx)
            .closest(".product-field").find(".productNameAddArea .select-name").text(name);
    } else {
        if(idx === undefined && name === undefined) return;
        $parent.find("[name='not_use']").prop("checked", true).change();
    }
}

const resultBodyHtml = (row) => {
    return `<tr>
        <td>${row.code}</td>
        <td class="product_name">${row.name}</td>
        <td>
            <button type="button" class="btn bl select-btn" data-idx="${row.idx}">선택하기</button>
        </td>
    </tr>`
}

const getProducts = (category_idx, onsuccess) => {
    ajaxRequest({
        url: "/product/search",
        data: {
            c: category_idx,
            q: $("#search_query").val(),
        },
        onsuccess
    })
}

function searchEvent(){
    getProducts(category_idx, (res) => {
        console.log(res);

        const count = res.count;
        const list = res.list;

        const $result_body = $("#resultBody");
        const $result_body_count = $("#resultBodyCount");
        $result_body.empty();

        $result_body_count.text(count.toLocaleString());

        if(count) {
            list.forEach(row => {
                $result_body.append(resultBodyHtml(row));
            });
        } else {
            $result_body.append(`
				<tr>
					<td colspan="3">검색 결과가 없습니다.</td>
				</tr>
				`);
        }
    });
}

$(document).ready(function(){

    $("#searchForm").submit(function(event) {
        event.preventDefault();
        searchEvent();
    });

    // 상품정보 테이블 삭제 버튼 클릭 이벤트
    $(document).on("click", ".table-delete-btn", function(){
        $(this).closest(".product-box").remove();
    });

    // 상품정보 입력폼 추가 이벤트
    $(".form-add-btn").on("click", addProductInfo);

    $("#searchBtn").on('click', function() {
        searchEvent();
    });

    //엔터키 누를시에도 검색
    $('#searchKeyword').on('keydown', function(e) {
        if (e.keyCode === 13) {
            e.preventDefault();
            searchEvent();
        }
    });

    //선택하기 버튼 클릭시 제품명 삽입 이벤트
    $(document).on('click', '.select-btn', function(){
        const productName = $(this).parent().siblings('.product_name').text();
        const idx = $(this).data("idx");

        $(currentBtn).siblings('.productNameAddArea').html(
            `
					<button type="button" class="delete-btn"><img src="/admin/images/ico_close.svg" width="12"></button>
					<p class="t-rw rw-1 select-name">${productName}</p>
				`
        );
        const $parent = $(currentBtn).closest("td");
        $parent.find("input[name='not_use']").change();
        $parent.find(`input[type='hidden'][data-category-hidden-idx='${category_idx}']`).val(idx).change();

        $('#searchProductPopup').hide();
        $(".popup-bg").hide();
        $("html").css("overflow", "auto");
    });

    //초기화 버튼 클릭 이벤트
    $("#resetBtn").on("click", function(){
        $("#search_query").val("");
        searchEvent();
    });

    $(document).on("click", ".product-box .product-field button[data-category_idx]", function() {
        category_idx = $(this).data("category_idx");
        searchEvent();
    });

    $(document).on("click", ".product-box .product-field button.delete-btn", function() {
        const $this = $(this);
        const $parent = $this.parent();
        $this.hide();
        $parent.find(".select-name").text("");
        $this.closest(".product-field").find("input[data-category-hidden-idx]").val("");
    });
});