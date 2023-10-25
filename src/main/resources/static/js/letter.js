$(function(){
	$("#sendBtn").click(send_letter);
	$(".close").click(delete_msg);
});

function send_letter() {
	$("#sendModal").modal("hide");
	// 获取参数
	var toName = $("#recipient-name").val();
	var content = $("#message-text").val();

	$.post(
		CONTEXT_PATH + "/letter/send",
		{"toName": toName, "content": content},
		function(data){
			data = JSON.parse(data);
			if (data.code == 0) {//成功
				$("#hintBody").text("发送成功！");
			} else {
				$("#hintBody").text(data.msg);
			}
		}
	);

	$("#hintModal").modal("show");
	setTimeout(function(){
		$("#hintModal").modal("hide");
		location.reload(); // 不管成功还是失败都希望将页面刷新一下
	}, 2000);
}

function delete_msg() {
	// TODO 删除数据
	$(this).parents(".media").remove();
}