<%@ page contentType="text/html;charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="host" value="localhost:8080/wechat"/>
<%--设置主机名--%>
<html>
<head>
    <meta charset="utf-8">
    <title>wechat</title>
    <link rel="shortcut icon" type=image/x-icon href=https://res.wx.qq.com/a/wx_fed/assets/res/NTI4MWU5.ico>
    <link rel="stylesheet" href="http://maxcdn.bootstrapcdn.com/bootstrap/3.2.0/css/bootstrap.min.css">
    <script src="${pageContext.request.contextPath}/static/js/jquery-3.4.1.js"></script>
    <!--BEGIN——发送请求脚本-->
    <!--END——发送请求脚本-->
</head>
<body style="background: url(//res.wx.qq.com/a/wx_fed/webwx/res/static/img/2zrdI1g.jpg) no-repeat 50%;
background-size: cover;overflow: hidden;">
<div class="background" style="background-color: transparent">
    <%-- 页面头部--%>
    <div class="login-head" style="height: 100px">
        <div class="jumbotron" style="padding-bottom: 20px;padding-top:20px;margin:0px;background-color: transparent">
            <div class="logo">
            <h2>微信网页版</h2>
            </div>
        </div>
    </div>
    <script>
        <c:if test="${message!=null}">
        alert("系统提示：${message}");
        </c:if>
    </script>
    <div class="input-box" style="position: fixed;left: 50%;top: 300px;transform: translate(-50%,-50%);">
        <div class="color-input-field">
            <form  action="http://${host}/wechat/user?method=login.do" method="post">
                <input id="index" type="submit" style="display: none">
            <h2 class="input-box-title">登录</h2>
            <input type="text" required="required" class="form-control" id="email"
                   value="${param.email}" name="email" placeholder="请输入登录邮箱" >
            <br/>
            <input id="password" type="password" required="required" class="form-control" name="password"
                   placeholder="请输入密码">

            <div class="form-group">
                <input type="radio" name="roleName" value="admin" checked="checked">管理员
                <input type="radio" value="normal" name="roleName">普通用户
            </div>
            <div class="form-group">
                <input type="text" required="required" class="form-control" id="code"
                       name="code" placeholder="请输入验证码" >
                <img id="codeImg" onclick="refresh()" src="http://${host}/code"/>
            </div>

            <div class="remember-me">
                <input id="option" name="auto_login" type="checkbox" value="true">一周内免登录
            </div>
            <input type="submit" class="submit-button" value="登录">
            <br>
            <div class="switch-button">
                <a href="${pageContext.request.contextPath}/register.jsp">立即注册</a>&nbsp;&nbsp;&nbsp;
                <a href="http://${host}/wechat/user?method=login.do&email=visitor" onclick="visitor()">|&nbsp;&nbsp;&nbsp;游客模式</a>&nbsp;&nbsp;&nbsp;
                <a href="${pageContext.request.contextPath}/forget.jsp">|&nbsp;&nbsp;&nbsp;忘记密码?</a>
            </div>
            </form>
        </div>
    </div>
</div>

</body>
<script>
    // 刷新验证码
    function refresh() {
        document.getElementById("codeImg").src = "http://${host}/code?"+Math.random();
    }
</script>
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
    .form-group {
        text-align: left;
        margin-top: 20px;
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
    #code {
        width:250px;
        display: inline-block;
    }
    #codeImg {
        float: right;
    }
</style>
</html>
