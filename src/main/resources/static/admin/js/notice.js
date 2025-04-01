const getImages = (idx, onsuccess) => {
    ajaxRequest({
        url: "/notice/images",
        data: {
            idx
        },
        onsuccess: onsuccess,
        onerror: console.error
    })
}