const idCheck = (input, is_member = false, exclude) => {
    let url = "/admin/manage/id-check";
    if(is_member) url = "/admin/member/id-check";
    $.ajax({
        url,
        data: {
            id: input.val(),
            exclude
        },
        type: "POST",
        async: false,
        success: function(res) {
            if(!res) setInvalidate(input, "이미 존재하는 아이디입니다.");
        },
        error: function(err) {
            console.error(err);
        }
    });
}

const emailCheck = (input, is_member = false, exclude) => {
    let url = "/admin/manage/email-check";
    if(is_member) url = "/admin/member/email-check";
    $.ajax({
        url,
        data: {
            email: input.val(),
            exclude
        },
        type: "POST",
        async: false,
        success: function(res) {
            if(!res) setInvalidate(input, "이미 존재하는 이메일입니다.")
        },
        error: function(err) {
            console.error(err);
        }
    });
}

const cellCheck = (input, is_member = false, exclude) => {
    let url = "/admin/manage/cell-check";
    if(is_member) url = "/admin/member/cell-check";
    $.ajax({
        url,
        data: {
            cell: input.val(),
            exclude
        },
        type: "POST",
        async: false,
        success: function(res) {
            if(!res) setInvalidate(input,  "이미 존재하는 전화번호입니다.")
        },
        error: function(err) {
            console.error(err);
        }
    });
}