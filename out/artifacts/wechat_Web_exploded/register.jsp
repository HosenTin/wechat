<%@ page contentType="text/html;charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="host" value="localhost:8080/wechat"/>
<%--设置主机名--%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="utf-8">
    <title>wechat</title>
    <link rel="shortcut icon" type=image/x-icon href=https://res.wx.qq.com/a/wx_fed/assets/res/NTI4MWU5.ico>
    <link rel="stylesheet" href="http://maxcdn.bootstrapcdn.com/bootstrap/3.2.0/css/bootstrap.min.css">
    <script src="${pageContext.request.contextPath}/static/js/jquery-3.4.1.js"></script>
    <script>
        // 发送邮箱验证码
        function sendMailCode() {
            var userEmail = $("#email").val();
            // 普通类型的提交(application/x-www-form-urlencoded; charset=UTF-8)
            // 不需要再转为json字符串提交
            var data = {
                userEmail: userEmail
            };
            $.ajax({
                url: "http://${host}/wechat/user?method=sendMailCode.do",
                type: 'POST',
                data: data,
                dataType: 'json',
                success: function (data) {
                    if (data.message != null && data.message !== '') {
                        alert("系统提示：" + data.message);
                    }
                },
                error: function (xhr, error, exception) {
                    alert("请求发送失败，请刷新浏览器重试或检查网络");
                }
            });
        }

    function register() {
            var email = document.getElementById("email").value;
            var password = document.getElementById("password").value;
            if(email==null||email==''){
                alert("请填写邮箱");
                return;
            }
            if(password==null||password===''){
                alert("请填写密码");
                return;
            }
            if(!document.getElementById("agree").checked){
                alert("请先同意用户使用协议");
                return;
            }
            document.getElementById("submit").click();
        }


    </script>
</head>
<body style="overflow: hidden;background: url(//res.wx.qq.com/a/wx_fed/webwx/res/static/img/2zrdI1g.jpg) no-repeat 50%;
background-size: cover;">
<script>
    <c:if test="${message!=null}">
    alert("系统提示：${message}");
    </c:if>
</script>
<div class="background" style="background-color: transparent">
    <%-- 页面头部--%>
    <div class="login-head" style="height: 100px">
        <div class="jumbotron" style="padding-bottom: 20px;padding-top:20px;margin:0px;background-color: transparent">
            <div class="logo">
                    <h2>微信网页版</h2>
            </div>
        </div>
    </div>
    <div class="input-box" style="position: fixed;left: 50%;top: 300px;transform: translate(-50%,-50%);">
        <div class="color-input-field">
            <h2 class="input-box-title">注册</h2>
            <form   action="http://${host}/wechat/user?method=register.do" method="post">
                <input id="index" type="submit" style="display: none">

            <div class="input-group">
                <input id="email" type="text" required="required" class="form-control" name="email"
                       value="${data.email}" placeholder="请输入邮箱号">
                <div class="input-group-addon" onclick="sendMailCode()">发送验证码</div>
            </div>
            <br/>
            <input id="password" type="password" required="required" class="form-control" name="password"
                  value="${data.password}" placeholder="请输入密码(6-20位英文字母，数字)">
            <br/>
            <input id="mailCode" type="text" required="required" class="form-control"
                       name="mailCode"
                       placeholder="请输入邮箱收到的验证码">
            <div class="remember-me">
                <input id="agree" type="checkbox" name="agreement"  value="true"
                       style="margin-bottom: 13px">我已阅读并同意<a href="">《微信服务协议》</a>
            </div>
                <input type="submit" id="submit" style="display: none">
            <input onclick="register()" type="button" class="submit-button" value="注册">
            <br>
            <div class="switch-button">
                已有账号？<a href="${pageContext.request.contextPath}/login.jsp">请登录</a>
                <a href="http://${host}/wechat/user?method=login.do&email=visitor" >| 游客模式</a>
            </div>
            </form>
        </div>
    </div>
</div>

</body>
<style type="text/css">

    .background {
        height: -webkit-fill-available;
        min-height: 750px;
        text-align: center;
        font-size: 14px;
        background-color: #f1f1f1;
        z-index: -1;
    }

    .logo {
        position: absolute;
        top: 56px;
        margin-left: 50px;
    }

    .form-control {
        padding: 10px;
        min-height: 55px;
        max-height: 70px;
        font-size: 22px;
    }

    .input-box-title {
        text-align: center;
        margin: 0 auto 50px;
        padding: 10px;
        font-weight: 400;
        color: #969696
    }

    .color-input-field {
        padding: 50px;
        font-size: 22px;
        height: 625px;
        width: 500px
    }

    .input-box {
        width: fit-content;
        margin: 104px auto;
        background-color: #fff;
        border-radius: 4px;
        box-shadow: 0 0 8px rgba(0, 0, 0, .1);
        vertical-align: middle;
    }

    .submit-button {
        margin-top: 20px;
        background-color: #1AAD19;
        color: #FFFFFF;
        padding: 9px 18px;
        border-radius: 5px;
        outline: none;
        border: none;
        width: 100%;
    }

    .remember-me {
        float: left;
        font-weight: 400;
        color: #969696;
        margin-top: 20px;
    }

    .switch-button {
        text-align: left;
    }

</style>
</html>


