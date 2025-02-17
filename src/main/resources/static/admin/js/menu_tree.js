$(document).ready(function() {
    let tree_menus = $(".tree input[type='checkbox']");
    tree_menus.on("change", function() {
        let checked = $(this).is(":checked");
        let parent = $(this).data("parent");
        $(this).closest("li").next("ul").find("input[type='checkbox']").prop("checked", checked);

        if(parent) {
            let parent_input = $(`input[value='${parent}']`);
            let children_input = parent_input.closest("li").next("ul").find("input[type='checkbox']:checked");
            if(children_input.length) {
                parent_input.prop("checked", true);
            } else {
                parent_input.prop("checked", false);
            }
        }
    });
});