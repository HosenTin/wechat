//post方法
function postRequest(url, request, callback) {
    $.post(url, request, function (data, status) {
        if (status === 'error') {
            alert("请求发送失败，请刷新浏览器重试或检查网络");
        }
        var result = eval("(" + data + ")");
        if (result.message != null && result.message !== '') {
            alert("系统提示：" + result.message);
        }
        //回调
        callback(result);
    });
}

//ajax方法
function ajaxJsonRequest(url, data, callback) {
    $.ajax({
        url: url,
        type: 'POST',
        data: data,
        dataType: 'json',
        contentType: 'application/json',
        success: function (data) {
            if (data.message != null && data.message !== '') {
                alert("系统提示：" + data.message);
            }
            callback(data);
        },
        error: function (xhr, error, exception) {
            alert("请求发送失败，请刷新浏览器重试或检查网络");
            alert(exception.toString());
            callback(data);
        }
    });
}

<!--BEGIN——监听键盘-->
function enterClick(button_id) {
    document.onkeydown = function (event) {
        var e = event || window.event || arguments.callee.caller.arguments[0];
        if (e && e.keyCode === 13) {
            event.cancelBubble = true;
            event.preventDefault();
            event.stopPropagation();
            document.getElementById(button_id).click();
        }
    };
}
<!--END——监听键盘-->