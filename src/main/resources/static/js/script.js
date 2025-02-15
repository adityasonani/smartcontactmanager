console.log("This is script .js");

let url = `https://smartcontactmanager-k15b.onrender.com/search/`;
const toggleSidebar=()=>{
    if ($(".sidebar").is(":visible")) {
        $(".sidebar").css("display", "none");
        $(".content").css("margin-left", "0%");
    } else {
        $(".sidebar").css("display", "block");
        $(".content").css("margin-left", "20%");
    }
};


const search=()=>{
    console.log('searching...');
    
    let query = $("#search-input").val();

    if (query=='') {
        $(".search-result").hide();
    } else {
        url = window.location.hostname === "localhost" ? "http://localhost:8080/search/" : url;
        let nurl = url + query;

        fetch(nurl).then((res)=>{
            return res.json();
        }).then((data)=>{
            let txt = `<div class='list-group'>`;

            data.forEach((contact)=>{
                txt += `<a href='/user/contact/${contact.cId}' class='list-group-item list-group-item-action'> ${contact.name} </a>`
            });

            txt += `</div>`;

            $(".search-result").html(txt);
            $(".search-result").show();

        });

    }
}