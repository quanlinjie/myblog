$("#profile-img-btn_lg").click(() => {
    $("#profile-img-input").click(); // 파일 선택창이 뜬다.
});
$("#profile-img-input").change((event) => {
    profileImgUpdate(event);
}); // 파일을 선택하면!!
async function profileImgUpdate(event) {
    // image/png 이런식의 파일임.
    let f = event.target.files[0];
    if (!f.type.match("image.*")) {
        alert("이미지를 선택해주세요!");
        return;
    }
    let userId = $("#userId").val();
    let fileForm = $("#fileForm")[0];
    let formData = new FormData(fileForm);
    let response = await fetch(`/s/api/user/${userId}/profile-img`, {
        method: "put",
        body: formData
    });
    if (response.status == 200) {
        imgPreview(event, f);
    } else {
        alert("프로파일 변경에 실패하였습니다");
    }
}
function imgPreview(event, f) {
    let reader = new FileReader();

    reader.onload = (event) => {
        //console.log(event.target.result);
        $("#profile-img-btn_lg").attr("src", event.target.result);
        $("#profile-img-btn").attr("src", event.target.result);
    }
    reader.readAsDataURL(f); // 비동기 실행(I/O)
}


$("#btn-update").click(() => {
update();
});


async function update() {
let id = $("#userId").val();
let updateDto = {
    email: $("#email").val(),
}

let response = await fetch(`/s/api/user/${userId}`, {
    method: "put",
    body: JSON.stringify(updateDto),
    headers: {
        "Content-Type": "application/json; charset = utf-8"
    }
});

let responseParse = await response.json();

if (responseParse.code == 1) {
    alert("수정 성공");
    location.href = `/s/user/${userId}`;
} else {
    alert("수정 실패");
}
}