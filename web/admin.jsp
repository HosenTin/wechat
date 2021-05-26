<%@ page contentType="text/html;charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="host" value="localhost:8080/wechat"/>
<%--设置主机名--%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="utf-8">

    <title>wechat</title>
    <link rel="shortcut icon" type=image/x-icon href=https://res.wx.qq.com/a/wx_fed/assets/res/NTI4MWU5.ico>
    <link rel="stylesheet" href="http://maxcdn.bootstrapcdn.com/bootstrap/3.2.0/css/bootstrap.min.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/static/css/index.css">
    <script src="${pageContext.request.contextPath}/static/js/jquery-3.4.1.js"></script>
    <script src="${pageContext.request.contextPath}/static/js/index.js"></script>
</head>
<body>
<div class="page-body" style="background-color: #eee;">
    <img id="background" src="${pageContext.request.contextPath}/upload/photo/${sessionScope.login.chatBackground}"
         style="position: absolute;height: 100%;width: 100%">
    <!--BEGIN——菜单列表-->
    <div class="menu">
        <div class="menu-head" style="margin-bottom: 50px">
            <div class="menu-head-photo">
                <img src="${pageContext.request.contextPath}/upload/photo/${sessionScope.login.photo}"
                     class="menu-head-img"
                     onclick="showWindowOnRight('user-info-box')">
            </div>
            <div class="menu-head-info">
                <h3 class="menu-head-nickname">${sessionScope.login.name}</h3>
            </div>
        </div>
        <div class="menu-option">
            <div class="menu-option-item">
                <div id="report-menu-option-item" onclick="showWindowOnLeft('report-list')" class="menu-option-button">举报信息</div>
            </div>
            <div class="menu-option-item">
                <div id="feedback-menu-option-item" onclick="showWindowOnLeft('feedback-list')" class="menu-option-button">反馈信息</div>
            </div>
            <div class="menu-option-item">
                <div id="setting" onclick="showWindowOnLeft('setting-list')" class="menu-option-button">设置</div>
            </div>
            <div class="menu-option-item">
                <div id="moment-menu-option-item" onclick="showWindowOnLeft('moment-list')" class="menu-option-button">朋友圈</div>
            </div>
        </div>
        <!--BEGIN——功能列表-->
        <div id="menu-body" class="menu-body" data-window="report-list" onload="document.getElementById('report-menu-option-item').click()">
            <div id="report-list" style="display: block">
                <button class="user-list-block-href" onmouseover="this.style.backgroundColor='#3A3F45'"
                        onmouseout="this.style.backgroundColor='#2e3238';"
                        onclick="loadReports(document.getElementById('report-box').dataset.page)">
                    <div class="user-list-block">
                        <div class="user-box">
                            <div class="user-info">
                                <h3 class="my-name">举报信息</h3>
                                <p class="my-message">管理举报信息</p>
                            </div>
                        </div>
                    </div>
                </button>
            </div>
            <div id="feedback-list" style="display: none">
                <button class="user-list-block-href" onmouseover="this.style.backgroundColor='#3A3F45'"
                        onmouseout="this.style.backgroundColor='#2e3238';"
                        onclick="loadFeedbacks(document.getElementById('feedback-box').dataset.page)">
                    <div class="user-list-block">
                        <div class="user-box">
                            <div class="user-info">
                                <h3 class="my-name">反馈信息</h3>
                                <p class="my-message">管理反馈信息</p>
                            </div>
                        </div>
                    </div>
                </button>
            </div>
            <div id="setting-list" style="display: none">
                <button class="user-list-block-href" onmouseover="this.style.backgroundColor='#3A3F45'"
                        onmouseout="this.style.backgroundColor='#2e3238';"
                        onclick="logout()">
                    <div class="user-list-block">
                        <div class="user-box">
                            <div class="user-info">
                                <h3 class="my-name">退出登录</h3>
                                <p class="my-message">注销当前账号在此浏览器上的登录</p>
                            </div>
                        </div>
                    </div>
                </button>

            </div>
            <div id="moment-list" style="display: none">
                <button class="user-list-block-href" onmouseover="this.style.backgroundColor='#3A3F45'"
                        onmouseout="this.style.backgroundColor='#2e3238';"
                        onclick="loadNews(document.getElementById('news-box').dataset.page)">
                    <div class="user-list-block">
                        <div class="user-box">
                            <div class="user-info">
                                <h3 class="my-name">管理朋友圈</h3>
                                <p class="my-message">管理所有人的朋友圈</p>
                            </div>
                        </div>
                    </div>
                </button>
            </div>
        </div>
        <!--END——功能列表-->
    </div>
    <!--END——菜单列表-->

    <!--BEGIN——右边窗口-->
    <div id="right-page" data-window="report-box">
        <!--BEGIN——举报窗口-->
        <div id="report-box" data-page="1" class="info-box" style="display: none">
            <div class="info-box-head">   
                <div class="info-box-title">       
                    <div class="info-box-title-box"><a class="info-box-title-text">举报信息管理</a></div>
                    <button onclick="loadReports(++document.getElementById('report-box').dataset.page)"
                            class="button" contenteditable="false">下页
                    </button>
                    <button onclick="loadReports(--document.getElementById('report-box').dataset.page<1?
                    ++document.getElementById('report-box').dataset.page:document.getElementById('report-box').dataset.page)"
                            class="button" contenteditable="false">上页
                    </button>
                </div>
            </div>
            <div id="${sessionScope.login.id}-report-info" class="info-detail-box">
                <div class="info-detail" style="margin-top:25px">
                    <table class="table table-striped table-bordered table-hover table-condensed">
                        <tr><th>举报人</th><th>被举报人</th><th>举报内容</th><th>是否生效</th><th>解封日期</th><th>操作</th></tr>
                        <tbody id="report-box-content">

                        </tbody>
                    </table>
                </div>
            </div>
        </div>
        <!--END——举报窗口-->
        <!--BEGIN——反馈窗口-->
        <div id="feedback-box" data-page="1" class="info-box" style="display: none">
            <div class="info-box-head">   
                <div class="info-box-title">       
                    <div class="info-box-title-box"><a class="info-box-title-text">反馈信息管理</a></div>
                    <button onclick="loadFeedbacks(++document.getElementById('feedback-box').dataset.page)"
                            class="button" contenteditable="false">下页
                    </button>
                    <button onclick="loadFeedbacks(--document.getElementById('feedback-box').dataset.page<1?
                    ++document.getElementById('feedback-box').dataset.page:document.getElementById('feedback-box').dataset.page)"
                            class="button" contenteditable="false">上页
                    </button>
                </div>
            </div>
            <div id="${sessionScope.login.id}-feedback-info" class="info-detail-box">
                <div class="info-detail" style="margin-top:25px">
                    <table class="table table-striped table-bordered table-hover table-condensed" id="backuptb">
                        <tr><th>反馈人</th><th>反馈内容</th><th>是否生效</th><th>操作</th></tr>
                        <tbody id="feedback-box-content">

                        </tbody>
                    </table>
                </div>
            </div>
        </div>
        <!--END——反馈窗口-->
        <!--BEGIN——查看朋友圈窗口-->
        <div id="news-box" data-page="1" class="info-box" style="display: none">
            <div class="info-box-head">   
                <div class="info-box-title">       
                    <div class="info-box-title-box"><a class="info-box-title-text">朋友圈</a></div>
                    <button onclick="loadNews(++document.getElementById('news-box').dataset.page)"
                            class="button" contenteditable="false">下页
                    </button>
                    <button onclick="loadNews(--document.getElementById('news-box').dataset.page<1?
                    ++document.getElementById('news-box').dataset.page:document.getElementById('news-box').dataset.page)"
                            class="button" contenteditable="false">上页
                    </button>
                </div>
            </div>
            <div id="${sessionScope.login.id}info" class="info-detail-box">
                <div id="news-box-content" class="info-detail">
                </div>
            </div>
        </div>
        <!--END——查看朋友圈窗口-->
        <!--BEGIN——查看朋友圈详情窗口-->
        <div id="news-detail-box" class="info-box" style="display: none">
            <div class="info-box-head">   
                <div class="info-box-title">       
                    <div class="info-box-title-box"><a class="info-box-title-text">朋友圈</a></div>
                    <button onclick="loadNews(document.getElementById('news-box').dataset.page)"
                            class="button" contenteditable="false">返回
                    </button>
                </div>
            </div>
            <div id="${sessionScope.login.id}info" class="info-detail-box">
                <div id="news-detail-box-content" class="info-detail">
                </div>
            </div>
        </div>
        <!--END——查看朋友圈详情窗口-->
    </div>
    <!--END——右边窗口-->
</div>
<!--BEGIN——程序执行脚本-->
<script>
    //注销登录
    function logout() {
        if (confirm("是否确定要退出登录？注销之后你需要重新登录")) {
            var url = 'http://${host}/wechat/user';
            var request = {
                method: "logout.do",
                user_id: ${sessionScope.login.id},
            };
            alert("正在退出登录，请稍后...");
            postRequest(url, request, function (result) {
                if (result.status === 'SUCCESS') {
                    window.location.href = '${pageContext.request.contextPath}/login.jsp';
                }
            });
        } else {
            return;
        }
    }

    //显示群成员
    function showChatMember(chat_id) {
        alert("正在加载聊天信息，请稍后...");
        var url = "http://${host}/wechat/chat";
        var request = {
            method: "member.do",
            chat_id: chat_id
        };
        postRequest(url, request, function (result) {
            var members = result.data;
            if (members.length === 0) {
                return;
            }
            loadChatInfoHead(chat_id);
            for (var i = 0; i < members.length; i++) {
                addMemberHtml(members[i]);
            }
        })
    }

    //清除聊天记录
    function deleteChatMessage(chat_id) {
        var url = "http://${host}/wechat/message";
        var request = {
            method: "clear.do",
            chat_id: chat_id,
            user_id: "${sessionScope.login.id}"
        };
        postRequest(url, request, function (result) {
        })
    }

    //显示聊天窗口
    function showChatBox(chat_id) {
        showWindowOnRight(chat_id);

        var url = "http://${host}/wechat/message";
        var request = {
            method: "read.do",
            user_id: ${sessionScope.login.id},
            chat_id: chat_id
        };
        postRequest(url, request, function (result) {

        })
    }

    //加载聊天列表，并且每个聊天加载隐藏的聊天窗口
    function loadChatListAndBox() {
        var url = "http://${host}/wechat/chat";
        var request = {
            method: "list.do",
            id: "${sessionScope.login.id}"
        };
        postRequest(url, request, function (result) {
            var chats = result.data;
            console.log("查询到聊天窗口数量：" + chats.length);
            for (var i = 0; i < chats.length; i++) {
                loadChatListOnMenu(chats[i]);
                loadChatBox(chats[i]);
            }
            loadUnReadMessage(1);
        });
    }


    //加载加载好友列表
    function loadFriendList() {
        var url = "http://${host}/wechat/friend";
        var request = {
            method: "list.do",
            user_id: "${sessionScope.login.id}"
        };
        postRequest(url, request, function (result) {
            document.getElementById("friend-list").innerHTML = ' <button class="user-list-block-href" onmouseover="this.style.backgroundColor=\'#3A3F45\'"\n' +
                '                        onmouseout="this.style.backgroundColor=\'#2e3238\';"\n' +
                '                        onclick="document.getElementById(\'search\').click()">\n' +
                '                    <div class="user-list-block">\n' +
                '                        <div class="user-box">\n' +
                '                            <div class="user-info">\n' +
                '                                <h3 class="my-name">添加好友</h3>\n' +
                '                                <p class="my-message">搜索并添加系统中的用户为好友</p>\n' +
                '                            </div>\n' +
                '                        </div>\n' +
                '                    </div>\n' +
                '                </button>\n' +
                '                <button class="user-list-block-href" onmouseover="this.style.backgroundColor=\'#3A3F45\'"\n' +
                '                        onmouseout="this.style.backgroundColor=\'#2e3238\';"\n' +
                '                        onclick="createChat()">\n' +
                '                    <div class="user-list-block">\n' +
                '                        <div class="user-box">\n' +
                '                            <div class="user-info">\n' +
                '                                <h3 class="my-name">创建群聊</h3>\n' +
                '                                <p class="my-message">创建一个群聊，邀请好友加入</p>\n' +
                '                            </div>\n' +
                '                        </div>\n' +
                '                    </div>\n' +
                '                </button>\n' +
                '                <button class="user-list-block-href" onmouseover="this.style.backgroundColor=\'#3A3F45\'"\n' +
                '                        onmouseout="this.style.backgroundColor=\'#2e3238\';"\n' +
                '                        onclick="joinChat()">\n' +
                '                    <div class="user-list-block">\n' +
                '                        <div class="user-box">\n' +
                '                            <div class="user-info">\n' +
                '                                <h3 class="my-name">加入群聊</h3>\n' +
                '                                <p class="my-message">通过群号加入一个群聊</p>\n' +
                '                            </div>\n' +
                '                        </div>\n' +
                '                    </div>\n' +
                '                </button>';
            var friends = result.data;
            for (var i = 0; i < friends.length; i++) {
                loadFriendOnMenu(friends[i]);
            }
        });
    }

    //加载未读消息
    function loadUnReadMessage(page) {
        var url = "http://${host}/wechat/message";
        var request = {
            method: "unread.do",
            user_id: "${sessionScope.login.id}",
            page: page
        };
        postRequest(url, request, function (result) {
            var messages = result.data;
            console.log("查询到未读消息 ： " + messages.length)

            for (var i = messages.length - 1; i >= 0; i--) {
                showMessage(messages[i]);
            }
        });
    }


    //加载一个聊天的未读消息
    function loadUnReadMessageInAChat(chat_id, page) {
        var url = "http://${host}/wechat/message";
        var request = {
            method: "unread.do",
            user_id: "${sessionScope.login.id}",
            chat_id: chat_id,
            page: page
        };
        postRequest(url, request, function (result) {
            var messages = result.data;
            console.log("查询到未读消息 ： " + messages.length)
            for (var i = messages.length - 1; i >= 0; i--) {
                showMessage(messages[i]);
            }
        });
    }

    //加载一个聊天中的所有消息
    function loadAllMessage(page, chat_id) {
        var url = "http://${host}/wechat/message";
        var request = {
            method: "list.do",
            user_id: "${sessionScope.login.id}",
            chat_id: chat_id,
            page: page,
        };
        postRequest(url, request, function (result) {
            document.getElementById(chat_id + "accept-message").innerHTML = '';
            var messages = result.data;
            for (var i = messages.length - 1; i >= 0; i--) {
                showMessage(messages[i]);
            }
        });
    }

    //加载朋友圈动态
    function loadNews(page) {
        var url = "http://${host}/wechat/moment";
        var request = {
            method: "news.do",
            page: page
        };
        postRequest(url, request, function (result) {
            var news = result.data;
            //加载之前先将之前的清空
            document.getElementById('news-box-content').innerHTML = '';
            for (var i = 0; i <= news.length - 1; i++) {
                addNewsBlockHtml(news[i]);
            }
        });
        showWindowOnRight('news-box');
    }

    //加载举报信息
    function loadReports(page) {
        var url = "http://${host}/wechat/report";
        var request = {
            method: "list.do",
            page: page
        };
        postRequest(url, request, function (result) {
            var reports = result.data;
            //加载之前先将之前的清空
            document.getElementById('report-box-content').innerHTML = '';
            for (var i = 0; i <= reports.length - 1; i++) {
                addReportsBlockHtml(reports[i]);
            }
        });
        showWindowOnRight('report-box');
    }
    //加载反馈信息
    function loadFeedbacks(page) {
        var url = "http://${host}/wechat/feedback";
        var request = {
            method: "list.do",
            page: page
        };
        postRequest(url, request, function (result) {
            var feedbacks = result.data;
            //加载之前先将之前的清空
            document.getElementById('feedback-box-content').innerHTML = '';
            for (var i = 0; i <= feedbacks.length - 1; i++) {
                addFeedbacksBlockHtml(feedbacks[i]);
            }
        });
        showWindowOnRight('feedback-box');
    }

    //加载我的朋友圈
    function loadMyMoment(page) {
        var url = "http://${host}/wechat/moment";
        var request = {
            method: "moment.do",
            user_id: "${sessionScope.login.id}",
            page: page
        };
        alert("正在加载您的朋友圈，请稍后...");
        postRequest(url, request, function (result) {
            var moments = result.data;
            //加载之前先将之前的清空
            document.getElementById('moment-box-content').innerHTML = '';
            for (var i = 0; i < moments.length - 1; i++) {
                addMomentBlockHtml(moments[i]);
            }
        });
        showWindowOnRight('moment-box');
    }

    //查询朋友圈图片
    function loadPhoto(page) {
        var url = 'http://${host}/wechat/moment';
        var request = {
            method: "photo.do",
            user_id: ${sessionScope.login.id},
            page: page
        };
        alert("正在加载朋友圈相册，请稍后...");
        postRequest(url, request, function (result) {
            var photos = result.data;
            //加载之前先将之前的清空
            document.getElementById('photo-box-content').innerHTML = '';
            for (var i = 0; i < photos.length; i++) {
                addPhotoHtml(photos[i]);
            }
        });
        showWindowOnRight('photo-box');
    }

    //下一页
    function nextNewsPage() {
        var page = document.getElementById("news-box").dataset.page;
        loadNews(page);
        document.getElementById("news-box").dataset.page = page + 1;
    }

    //删除朋友圈
    function deleteMoment(moment_id) {
        if (confirm("是否确定要删除这条朋友圈？")) {
            var url = 'http://${host}/wechat/moment';
            var request = {
                method: "delete.do",
                moment_id: moment_id
            };
            postRequest(url, request, function (result) {
            });
        } else {
            return;
        }
    }

    //删除朋友圈评论
    function deleteRemark(remark_id, moment_id) {
        if (confirm("是否确定要删除这条评论？")) {
            var url = 'http://${host}/wechat/remark';
            var request = {
                method: "delete.do",
                remark_id: remark_id
            };
            postRequest(url, request, function (result) {
                addMomentDetailHtml(moment_id);
            });
        } else {
            return;
        }
    }

    //拉黑好友
    function pullInBlacklist(friend_id) {
        if (confirm("是否确定要拉黑这个好友？")) {
            var url = 'http://${host}/wechat/friend';
            var request = {
                method: "blacklist.do",
                user_id: ${sessionScope.login.id},
                friend_id: friend_id
            };
            postRequest(url, request, function (result) {
                loadFriendList();
            });
        }
    }

    //举报好友
    function reportFriend(friend_id) {
        var reportCont = prompt("请输入举报内容", "");
        if (!reportCont) {
            return;
        }
        var url = 'http://${host}/wechat/friend';
        var request = {
            method: "report.do",
            user_id: ${sessionScope.login.id},
            friend_id: friend_id,
            report_cont:reportCont
        };
        postRequest(url, request, function (result) {
            loadFriendList();
        });
    }

    //删除好友
    function deleteFriend(friend_id) {
        if (confirm("是否确定要删除这个好友？")) {
            var url = 'http://${host}/wechat/friend';
            var request = {
                method: "delete.do",
                user_id: ${sessionScope.login.id},
                friend_id: friend_id
            };
            alert("正在删除好友，请稍后...");
            postRequest(url, request, function (result) {
                loadFriendList();
            });
        } else {
            return;
        }
    }


    //加好友
    function addFriend(friend_id) {

        var url = "http://${host}/wechat/friend?method=add.do";
        var description = prompt("请输入好友申请", "我是${sessionScope.login.name},快加我为好友吧！");
        var alias = prompt("请输入好友备注", "");
        if (description == null) {
            return;
        }
        var request = JSON.stringify({
            user_id: "${sessionScope.login.id}",
            friend_id: friend_id,
            description: description,
            alias: alias
        });
        if (alias == null) {
            return;
        }
        if (confirm("是否确定要发送好友申请？")) {
            alert("正在发送好友申请，请稍后...");
            ajaxJsonRequest(url, request, function (result) {
            })
        } else {
            return;
        }
    }

    //同意加好友
    function agreeAddFriend(friend_id) {
        if (confirm("是否确定同意好友申请？")) {
            console.log("同意加好友：friend_id " + friend_id);
            var url = "http://${host}/wechat/friend?method=add.do";
            var alias = prompt("请输入好友备注", "");
            if (alias == null) {
                return;
            }
            var request = JSON.stringify({
                user_id: "${sessionScope.login.id}",
                friend_id: friend_id,
                alias: alias
            });
            alert("正在同意好友申请，请稍后...");
            ajaxJsonRequest(url, request, function (result) {
            })
        } else {
            return;
        }
    }


    //退出群聊
    function quitChat(chat_id) {
        if (confirm("是否确定要退出该群聊？")) {
            var url = "http://${host}/wechat/chat?method=quit.do";
            var request = JSON.stringify({
                user_id: "${sessionScope.login.id}",
                chat_id: chat_id,
            });
            alert("正在退出群聊，请稍后...");
            ajaxJsonRequest(url, request, function (result) {
            })
        } else {
            return;
        }
    }

    //更新个人信息
    function updateUserInfo() {
        if (confirm("是否确定更新个人信息？")) {
            var jsonStr = JSON.stringify({
                id: "${sessionScope.login.id}",
                name: document.getElementById("name").innerText,
                signature: document.getElementById("signature").innerText,
                wechat_id: document.getElementById("wechat_id").innerText,
                gender: document.getElementById("gender").innerText,
                location: document.getElementById("location").innerText
            });
            //更新基本信息
            alert("正在更新个人信息，请稍后...");
            var url = "http://${host}/wechat/user?method=update.do";
            ajaxJsonRequest(url, jsonStr, function (result) {
                if ("SUCCESS" === result.status) {
                } else {
                }
            });
            //更新头像
            url = "http://${host}/wechat/upload?method=uploadphoto.do&id=${sessionScope.login.id}&table=user";
            uploadPhoto(url, 'user-photo');
        } else {
            return;
        }
    }


    //更新聊天背景
    function updateBackground() {
        imgPreview(document.getElementById('background-upload'), 'background');
        var url = "http://${host}/wechat/upload?method=background.do&id=${sessionScope.login.id}";
        alert("正在更新聊天背景，请稍后...");
        uploadPhoto(url, 'background-upload');
    }

    //发布朋友圈
    function postMoment() {
        var content = document.getElementById("moment-content").value;
        var jsonStr = JSON.stringify({
            owner_id: "${sessionScope.login.id}",
            content: content
        });
        var url = "http://${host}/wechat/moment?method=add.do";
        if (!('' === content)) {
            alert("正在发布朋友圈，请稍后...");
            ajaxJsonRequest(url, jsonStr, function (result) {
                if ("SUCCESS" === result.status) {
                    //成功之后上传图片
                    url = "http://${host}/wechat/upload?method=uploadphoto.do&id=" + result.data.id + "&table=moment";
                    uploadPhoto(url, 'moment-photo');
                } else {
                }
            });
            document.getElementById("moment-content").value = '';
        } else {
            alert("发送内容不能为空");
            return;
        }
    }

    //点赞朋友圈
    function loveMoment(moment_id, moment_love) {
        var url = 'http://${host}/wechat/moment';
        var request = {
            method: "love.do",
            moment_id: moment_id,
            user_id:${sessionScope.login.id},
        };
        postRequest(url, request, function (result) {
            var islove = result.data;
            var love = parseInt(document.getElementById(moment_id + "love").dataset.love);
            console.log("当前点赞数" + love);
            if (islove) {
                document.getElementById(moment_id + "love").innerText = '点赞(' + (love + 1) + ')';
                document.getElementById(moment_id + "love").dataset.love = love + 1;
            } else {
                document.getElementById(moment_id + "love").innerText = '点赞(' + (love - 1) + ')';
                document.getElementById(moment_id + "love").dataset.love = love - 1;
            }
        });
    }


    //评论朋友圈//TODO
    function remarkMoment(moment_id) {
        var content = prompt("请输入评论", '');
        if (content == null) {
            return;
        }
        var time = new Date().getTime();
        var url = "http://${host}/wechat/remark?method=add.do";
        var request = JSON.stringify({
            user_id: "${sessionScope.login.id}",
            moment_id: moment_id,
            time: time,
            content: content
        });
        if (!('' === content || content == null)) {
            if (confirm("是否确定要发布该评论？")) {
                alert("正在发布评论，请稍后...");
                ajaxJsonRequest(url, request, function (result) {
                    var remark = parseInt(document.getElementById(moment_id + "remark").dataset.remark);
                    console.log("当前评论数" + remark);
                    document.getElementById(moment_id + "remark").dataset.remark = remark + 1;
                    document.getElementById(moment_id + "remark").innerText = '评论(' + (remark + 1) + ')';
                    addMomentDetailHtml(moment_id);
                })
            } else {
                return;
            }
        } else {
            alert("评论内容不能为空");
            return;
        }
    }

    //回复一条评论
    function replyRemark(moment_id, user_name) {
        var content = prompt("请输入回复", "");
        if (content == null) {
            return;
        }
        content = "@" + user_name + " " + content;
        var time = new Date().getTime();
        var url = "http://${host}/wechat/remark?method=add.do";
        var request = JSON.stringify({
            user_id: "${sessionScope.login.id}",
            moment_id: moment_id,
            time: time,
            content: content
        });
        if (!('' === content)) {
            if (confirm("是否确定要发布该回复？")) {
                alert("正在发布回复，请稍后...");
                ajaxJsonRequest(url, request, function (result) {
                    var remark = parseInt(document.getElementById(moment_id + "remark").dataset.remark);
                    document.getElementById(moment_id + "remark").dataset.remark = remark + 1;
                    document.getElementById(moment_id + "remark").innerText = '评论(' + (remark + 1) + ')';
                    addMomentDetailHtml(moment_id);

                })
            } else {
                return;
            }
        } else {
            alert("回复内容不能为空");
            return;
        }
    }


    //上传图片
    function uploadPhoto(url, photo_id) {
        var formData = new FormData();
        formData.append('photo', $(document.getElementById(photo_id))[0].files[0]);  //添加图片信息的参数
        $.ajax({
            url: url,
            type: 'POST',
            cache: false, //上传文件不需要缓存
            data: formData,
            processData: false, // 告诉jQuery不要去处理发送的数据
            contentType: false, // 告诉jQuery不要去设置Content-Type请求头
            success: function (data) {
                if (data.message != null && data.message !== '') {
                    alert("系统提示：" + data.message);
                }
            },
            error: function (xhr, error, exception) {
                alert(exception.toString());
            }
        })
    }

    //加载聊天列表
    function loadChatListOnMenu(chat) {
        var chat_html = '<button class="user-list-block-href"  onmouseover="this.style.backgroundColor=\'#3A3F45\';" ' +
            'onmouseout="this.style.backgroundColor=\'#2e3238\';"' +
            'onclick="showChatBox(\'' + chat.id + '\')"><div class="user-list-block">\n' +
            '                <div class="user-box" >\n' +
            '                    <div class="user-photo">\n' +
            '                        <img src="${pageContext.request.contextPath}/upload/photo/' + chat.photo + '" alt="用户头像" class="my-photo">\n' +
            '                    </div>\n' +
            '                    <div class="user-info">\n' +
            '                        <h3 class="my-name">' + chat.name + '</h3>\n' +
            '                        <p class="my-message" id="' + chat.id + 'new-message">没有新消息</p>\n' +
            '                    </div>\n' +
            '                </div>\n' +
            '            </div></button>';
        document.getElementById("chat-list").innerHTML += chat_html;
    }

    //加载通知列表
    function loadNotify(notify) {
        var html = '        <button class="user-list-block-href" onmouseover="this.style.backgroundColor=#3A3F45;"\n' +
            '                        onmouseout="this.style.backgroundColor=#2e3238;"\n' +
            '                        onclick="">\n' +
            '                    <div class="user-list-block">\n' +
            '                        <div class="user-box">\n' +
            '                            <div class="user-photo">\n' +
            '                                <img src="${pageContext.request.contextPath}/upload/photo/' + friend.photo + '  " alt="用户头像" class="my-photo">\n' +
            '                            </div>\n' +
            '                            <div class="user-info">\n' +
            '                                <h3 class="my-name"></h3>\n' +
            '                                <p class="my-message">' + friend.description + ' </p>\n' +
            '                            </div>\n' +
            '                        </div>\n' +
            '                    </div>\n' +
            '                    <button id="" onclick="postMoment()" style="float: right;\n' +
            '                    background-color:#1AAD19;border: solid 1px;width: 100px;margin-right: 50px"\n' +
            '                            contenteditable="false">发布\n' +
            '                    </button>\n' +
            '                </button>';
        document.getElementById("notify-list").innerHTML += chat_html;
    }

    //加载好友列表
    function loadFriendOnMenu(friend) {
        // 朋友已被拉黑时显示的html:仅显示"已拉黑"
        var friend_in_black_html =
            '<div class="menu-option-item">\n' +
            '                <div id="chat" class="menu-option-button">已拉黑</div>\n' +
            '            </div>          ';
        // 朋友未被拉黑时显示的html:显示拉黑操作
        var friend_not_in_black_html =
            '<div class="menu-option-item">\n' +
            '                <div id="chat" onclick="pullInBlacklist(\'' + friend.friend_id + '\')" class="menu-option-button">拉黑</div>\n' +
            '            </div>          ';
        // 朋友已被举报时显示的html:仅显示"已举报"
        var friend_reported =
            '<div class="menu-option-item">\n' +
            '                <div id="chat" class="menu-option-button">已举报</div>\n' +
            '            </div>          ';
        // 朋友未被举报时显示的html:显示举报操作
        var friend_not_reported =
            '<div class="menu-option-item">\n' +
            '                <div id="chat" onclick="reportFriend(\'' + friend.friend_id + '\')" class="menu-option-button">举报</div>\n' +
            '            </div>          ';

        var chat_html = '<button class="user-list-block-href"  onmouseover="this.style.backgroundColor=\'#3A3F45\';" ' +
            'onmouseout="this.style.backgroundColor=\'#2e3238\';"' +
            '><div class="user-list-block">\n' +
            '                <div class="user-box" onclick="updateFriend(\'' + friend.id + '\')">\n' +
            '                    <div class="user-photo">\n' +
            '                        <img src="${pageContext.request.contextPath}/upload/photo/' + friend.photo + '" alt="用户头像" class="my-photo">\n' +
            '                    </div>\n' +
            '                    <div class="user-info">\n' +
            '                        <h3 class="my-name">' + friend.alias + '</h3>\n' +
            '                        <p class="my-message">' + friend.description + '</p>\n' +
            '                    </div>\n' +
            '                </div>\n' +
            '            </div>  ' +
            '<div class="menu-option-item">\n' +
            '                <div id="chat" onclick="showChatBox(\'' + friend.chat_id + '\')" class="menu-option-button">发消息</div>\n' +
            '            </div>          ';
            if ('N' === friend.friend_in_black) {
                chat_html += friend_not_in_black_html;
            } else {
                chat_html += friend_in_black_html;
            }
            if ('N' === friend.friend_reported) {
                chat_html += friend_not_reported;
            } else {
                chat_html += friend_reported;
            }
            chat_html += '<div class="menu-option-item">\n' +
            '                <div id="chat"  onclick="deleteFriend(\'' + friend.friend_id + '\')" class="menu-option-button" style="float: right">删除好友</div>\n' +
            '            </div></button>';
        document.getElementById("friend-list").innerHTML += chat_html;
    }

    //加载好友通知
    function loadAddFriendOnMenu(message) {
        var chat_html = '<button class="user-list-block-href"  onmouseover="this.style.backgroundColor=\'#3A3F45\';" ' +
            'onmouseout="this.style.backgroundColor=\'#2e3238\';"' +
            '><div class="user-list-block">\n' +
            '                <div class="user-box" >\n' +
            '                    <div class="user-photo">\n' +
            '                        <img src="${pageContext.request.contextPath}/upload/photo/' + message.sender_photo + '" alt="用户头像" class="my-photo">\n' +
            '                    </div>\n' +
            '                    <div class="user-info">\n' +
            '                        <h3 class="my-name">' + message.sender_name + '</h3>\n' +
            '                        <p class="my-message">' + message.content + '</p>\n' +
            '                    </div>\n' +
            '                </div>\n' +
            '            </div>  ' +
            '<div class="menu-option-item">\n' +
            '                <div id="chat" onclick="agreeAddFriend(\'' + message.sender_id + '\')" class="menu-option-button">同意</div>\n' +
            '            </div>          ';
        document.getElementById("friend-list").innerHTML += chat_html;
    }

    //加载搜索结果页面，并显示
    function loadSearchResult() {
        document.getElementById("search-result-box").innerHTML =
            '            <div class="info-box-head">          \n' +
            '                <div class="info-box-title">                  \n' +
            '                    <div class="info-box-title-box"><a class="info-box-title-text">用户列表</a></div>\n' +
            '                </div>\n' +
            '            </div>\n' +
            '            <div class="info-detail-box">                  \n' +
            '                <div id="content" class="info-detail">           \n' +
            '                    <div></div>\n' +
            '                </div>\n' +
            '            </div>\n';

        showWindowOnRight("search-result-box");
    }

    //加载群聊信息
    function loadChatInfoHead(chat_id) {
        document.getElementById("search-result-box").innerHTML =
            '                <div class="chat-box-title">       \n' +
            '                        <a class="chat-box-title-text">聊天成员列表</a>       \n' +
            '                </div>\n' +
            '            <div class="info-detail-box">                  \n' +
            '                <div id="content" class="info-detail">           \n' +
            '                    <div></div>\n' +
            '                </div>\n' +
            '            </div>\n';

        showWindowOnRight("search-result-box");
    }

    //让该id对应的窗口显示出来，并把之前的隐藏起来 user-info-box
    function showWindowOnRight(window_id) {
        var current_window = document.getElementById("right-page").dataset.window;
        console.log("隐藏窗口id : " + current_window);
        document.getElementById(current_window).style.display = "none";
        document.getElementById(window_id).style.display = "";
        document.getElementById("right-page").dataset.window = window_id;
        console.log("当前窗口id : " + document.getElementById("right-page").dataset.window);
    }

    //让该id对应的窗口显示出来，并把之前的隐藏起来
    function showWindowOnLeft(window_id) {
        // dataset用于获取自定义data-*的属性
        var current_window = document.getElementById("menu-body").dataset.window;
        console.log("隐藏窗口id : " + current_window);
        document.getElementById(current_window).style.display = "none";
        document.getElementById(window_id).style.display = "";
        document.getElementById("menu-body").dataset.window = window_id;
        console.log("当前窗口id : " + document.getElementById("menu-body").dataset.window);
    }


    //加载发朋友圈窗口
    function loadPostMomentBox() {
        var html = '<div class="info-box" id="info-box">\n' +
            '            <div class="info-box-head">   \n' +
            '                <div class="info-box-title">       \n' +
            '                    <div class="info-box-title-box"><a class="info-box-title-text">写朋友圈</a></div>\n' +
            '                    <button id="post-moment" onclick="postMoment()"\n' +
            '                            style="float: right" contenteditable="false">发布\n' +
            '                    </button>\n' +
            '                </div>\n' +
            '            </div>\n' +
            '            <div id="${sessionScope.login.id}info" class="info-detail-box">\n' +
            '                <div class="info-outline">\n' +
            '                    <div class="info-head-photo">\n' +
            '                        <img id="preview" src="${pageContext.request.contextPath}/upload/photo/upload.jpg" class="info-head-img"\n' +
            '                             onclick="document.getElementById(\'input_file\').click()">\n' +
            '                        <input type="file" name="file" id="input_file"\n' +
            '                               accept="image/gif,image/jpeg,image/jpg,image/png,image/svg"\n' +
            '                               oninput="imgPreview(document.getElementById(\'input_file\'))" style="display: none">\n' +
            '                    </div>\n' +
            '                    <div class="info-head-info">\n' +
            '                        <h3 class="info-head-nickname" style="    font-size: 30px;">单击左侧上传图片</h3>\n' +
            '                    </div>\n' +
            '                </div>\n' +
            '                <div class="info-detail">\n' +
            '                    <div class="info-detail-block" style="margin-left: 20px">\n' +
            '                        <label for="moment-content"></label>\n' +
            '                        <textarea class="input-text-content" style="    width: 70%;" id="moment-content" autofocus="autofocus" cols="100"\n' +
            '                                  contenteditable="true" onchange="enterClick(\'post-moment\')" placeholder="分享自己的动态..."\n' +
            '                                  required="required" maxlength="300" oninput="enterClick(\'post-moment\')"></textarea>\n' +
            '                    </div>\n' +
            '                </div>\n' +
            '            </div>\n' +
            '        </div>\n';
        document.getElementById("info-box").innerHTML = html;
    }

    //加载图片
    function imgPreview(fileDom, preview) {
        //判断是否支持FileReader
        if (window.FileReader) {
            var reader = new FileReader();
        } else {
            alert("您的设备不支持图片预览功能，如需该功能请升级您的设备！");
        }
        //获取文件
        var file = fileDom.files[0];
        var imageType = /^image\//;
        //是否是图片
        if (!imageType.test(file.type)) {
            alert("该文件不是图片或者已经损坏，请重新选择！");
            return;
        }
        //图片大小
        var size = (file.size / 1024) / 1024;
        console.log("size" + size);
        if (size > 5) {
            alert("图片大小不能大于5m！");
            file.value = "";
            return;
        } else if (size <= 0) {
            alert("文件大小不能为0M！");
            file.value = "";
            return;
        }

        //读取完成
        reader.onload = function (e) {
            //获取图片dom
            var img = document.getElementById(preview);
            //图片路径设置为读取的图片
            img.src = e.target.result;
        };
        reader.readAsDataURL(file);

    }


    //动态加载聊天窗口，不显示loadAllMessage(page, chat_id)
    function loadChatBox(chat) {
        var html =
            '<div id="' + chat.id + '" class="chat-box" style="display: none;background: transparent;">\n' +
            '            <div class="chat-box-title">\n' +
            '                    <button onclick="quitChat(\'' + chat.id + '\')"\n' +
            '                            class="button" contenteditable="false">退出聊天\n' +
            '                    </button>\n' +
            '         <button \n' +
            '                            onclick="deleteChatMessage(\'' + chat.id + '\')"\n' +
            '                            class="button" contenteditable="false">清除聊天记录\n' +
            '                    </button>' +
            '         <button \n' +
            '                            onclick="showChatMember(\'' + chat.id + '\')"\n' +
            '                            class="button" contenteditable="false">聊天信息\n' +
            '                    </button>' +
            '         <button \n' +
            '                            onclick="loadAllMessage(\'1\',\'' + chat.id + '\')"\n' +
            '                            class="button" contenteditable="false">加载已读消息\n' +
            '                    </button>' +
            '                    <p class="chat-box-title-text">\n' +
            '                        ' + chat.name + '\n' +
            '                    </p>\n' +

            '            </div>\n' +
            '<div class="chat-box-head">\n' +
            '        </div>\n' +
            '        <div id="' + chat.id + 'accept-message" class="chat-output-box" style="background: transparent;">\n' +
            '        </div>\n' +
            '        <div class="chat-input-box" style="background-color:#eee;" >\n' +
            '            <button id="' + chat.id + 'send-button" onclick="sendMessage(\'' + chat.id + '\',\'user\')"  style="float: left" class="button">发送</button>\n' +


            '      <input type="file" name="file" id="' + chat.id + 'send-file"\n' +
            '                               oninput="send_file(document.getElementById(\'' + chat.id + 'send-file\'),\'' + chat.id + 'send-file\',\'' + chat.id + 'send-message\',\'' + chat.id + '\')"\n' +
            '                               style="display: none">' +
            '            <button onclick="document.getElementById(\'' + chat.id + 'send-file\').click()" style="float: left" class="' +
            'button">文件</button>\n' +

            '      <input type="file" name="file" id="' + chat.id + 'send-img"\n' +
            '                               oninput="send_img(document.getElementById(\'' + chat.id + 'send-img\'),\'' + chat.id + 'send-img\',\'' + chat.id + 'send-message\',\'' + chat.id + '\')"\n' +
            '                               style="display: none">' +
            '            <button  onclick="document.getElementById(\'' + chat.id + 'send-img\').click()" style="float: left" class="button">图片</button>\n' +

            '            <textarea id="' + chat.id + 'send-message" class="text-area" autofocus="autofocus" cols="100"\n' +
            '                      required="required" maxlength="300"  oninput="enterClick(\'' + chat.id + 'send-button\')"></textarea>\n' +
            '        </div></div>';
        document.getElementById("right-page").innerHTML += html;
    }

    //发送消息，chat_id:251 type:user
    function sendMessage(chat_id, type) {
        if (websocket === null) {
            alert("正在初始化websocket连接，第一次连接需要加载数据，请稍后...");
            return;
        }
        if (websocket === '') {
            alert("您已断开与服务器的连接，可能您的账号已在别处登录，请刷新浏览器重新连接服务器");
            return;
        }
        // 251send-message 你好啊
        var content = document.getElementById(chat_id + 'send-message').value;
        if (!('' === content)) {
            var user_id = "${sessionScope.login.id}";
            var time = new Date().getTime();
            // JSON.stringify:把json对象或arr转成字符串
            websocket.send(JSON.stringify({
                sender_id: user_id,
                chat_id: chat_id,
                content: content,
                type: type,
                time: time
            }));
            //发送完之后将消息清空
            document.getElementById(chat_id + 'send-message').value = '';
        } else {
            alert("发送内容不能为空");
            return;
        }
    }

    //发送文件
    function send_file(fileDom, file_id, preview, chat_id) {
        //判断是否支持FileReader
        if (window.FileReader) {
            var reader = new FileReader();
        } else {
            alert("您的设备不支持文件预览功能，如需该功能请升级您的设备！");
        }
        //获取文件
        var file = fileDom.files[0];
        //文件大小
        var size = (file.size / 1024) / 1024;
        console.log("size" + size);
        if (size > 20) {
            alert("文件大小不能大于20m！");
            file.value = "";
            return;
        } else if (size <= 0) {
            alert("文件大小不能为0M！");
            file.value = "";
            return;
        }
        if (confirm("是否确定要立即发送文件：" + file.name)) {
            //upload-file
            var formData = new FormData();
            var url = "http://${host}/wechat/upload?method=uploadfile.do";
            formData.append('file', $(document.getElementById(file_id))[0].files[0]);
            $.ajax({
                url: url,
                type: 'POST',
                cache: false,
                data: formData,
                dataType: 'json',
                processData: false,
                contentType: false,
                success: function (result) {
                    if (result.message != null && result.message !== '') {
                        alert("系统提示：" + result.message);
                    }
                    if ("SUCCESS" === result.status) {
                        // 聊天输入框的内容
                        var html = '<a href="http://${host}/upload/file/' + result.data + '" download="' + file.name + '">下载</a>';
                        document.getElementById(preview).value += '[文件：' + file.name + ']' + html;
                        sendMessage(chat_id, "file");
                    }
                },
                error: function (xhr, error, exception) {
                    alert(exception.toString());
                }
            })
        } else {
            return;
        }
    }

    //发送图片
    function send_img(fileDom, file_id, preview, chat_id) {
        //判断是否支持FileReader
        if (window.FileReader) {
            var reader = new FileReader();
        } else {
            alert("您的设备不支持文件预览功能，如需该功能请升级您的设备！");
        }
        //获取文件
        var file = fileDom.files[0];
        //文件大小
        var size = (file.size / 1024) / 1024;
        console.log("size" + size);
        var imageType = /^image\//;
        //是否是图片
        if (!imageType.test(file.type)) {
            alert("该文件不是图片或者已经损坏，请重新选择！");
            return;
        }
        if (size > 5) {
            alert("图片大小不能大于5m！");
            file.value = "";
            return;
        } else if (size <= 0) {
            alert("文件大小不能为0M！");
            file.value = "";
            return;
        }
        //读取完成
        reader.onload = function () {
        };
        reader.readAsDataURL(file);
        if (confirm("是否确定要立即发送图片：" + file.name)) {
            //upload-file
            var formData = new FormData();
            var url = "http://${host}/wechat/upload?method=uploadfile.do";
            formData.append('file', $(document.getElementById(file_id))[0].files[0]);
            $.ajax({
                url: url,
                type: 'POST',
                cache: false,
                data: formData,
                dataType: 'json',
                processData: false,
                contentType: false,
                success: function (result) {
                    if (result.message != null && result.message !== '') {
                        alert("系统提示：" + result.message);
                    }
                    if ("SUCCESS" === result.status) {
                        var html = '<img src="${pageContext.request.contextPath}/upload/file/' + result.data + '"\n' +
                            '         style="height: 100%;width: 100%;max-height:200px;max-width:200px;">\n';
                        document.getElementById(preview).value += html;
                        sendMessage(chat_id, "img");
                    }
                },
                error: function (xhr, error, exception) {
                    alert(exception.toString());
                }
            })
        } else {
            return;
        }
    }


    //移除一个成员
    function removeMember(member_id, chat_id) {
        if (confirm("是否确定要移除该成员？")) {
            var url = 'http://${host}/wechat/chat';
            var request = {
                method: "remove.do",
                member_id: member_id,
            };
            alert("正在移除该成员，请稍后...");
            postRequest(url, request, function (result) {
                if ('SUCCESS' === result.status) {
                    showChatMember(chat_id);
                }
            });
        } else {
            return;
        }
    }

    //插入一个搜索用户的结果
    function addSearchUserResultHtml(user) {
        <%--var html = '                   <div class="info-detail-block">               \n' +--%>
        <%--    // '                        <div class="user-box" style="border-top: 1px solid;margin: 20px;">\n' +--%>
        <%--    '                            <div class="user-photo" style="margin: 20px">\n' +--%>
        <%--    '                                <img src="${pageContext.request.contextPath}/upload/photo/' + user.photo + '" alt="用户头像" class="my-photo">\n' +--%>
        <%--    '                            </div>\n' +--%>
        <%--    '                            <div class="user-info">\n' +--%>
        <%--    '                                <h3 class="my-name" style="color: #333；width: fit-content;">' + user.name + '</h3>\n' +--%>
        <%--    '                            <button onclick="addFriend(\'' + user.id + '\')" style="float: right"\n' +--%>
        <%--    '                                    contenteditable="false">加好友\n' +--%>
        <%--    '                            </button>\n' +--%>
        <%--    '                            </div>\n' +--%>
        <%--    '                                <p class="my-message" style="margin-top:-25px;">' + user.signature + '</p>\n' +--%>
        <%--    // '                        </div>\n' +--%>
        <%--    '                    </div>';--%>
        var html = '                   <div class="info-detail-block" style="  width: 95%;min-width:300px;">               \n' +
            // '                        <div class="user-box" style="border-top: 1px solid;margin: 20px;">\n' +
            '                            <div class="user-photo" style="margin: 20px">\n' +
            '                                <img src="${pageContext.request.contextPath}/upload/photo/' + user.photo + '" alt="用户头像" class="my-photo">\n' +
            '                            </div>\n' +
            '                            <div class="user-info">\n' +
            '                                <h3 class="my-name" style="color: #333;width: fit-content;">' + user.name + '</h3>\n' +
            '                            <button onclick="addFriend(\'' + user.id + '\')" class="button"\n' +
            '                                    contenteditable="false">加好友\n' +
            '                            </button>\n' +
            '                            </div>\n' +
            '                                <p class="my-message" style="margin-top:-25px;width: 70%;">' + user.signature + '</p>\n' +
            // '                        </div>\n' +
            '                    </div>';
        document.getElementById("content").innerHTML += html;
    }

    //插入一个群成员的信息
    function addMemberHtml(member) {
        var html = '                   <div class="info-detail-block" style="  width: 95%;min-width:300px;">               \n' +
            // '                        <div class="user-box" style="border-top: 1px solid;margin: 20px;">\n' +
            '                            <div class="user-photo" style="margin: 20px">\n' +
            '                                <img src="${pageContext.request.contextPath}/upload/photo/' + member.photo + '" alt="用户头像" class="my-photo">\n' +
            '                            </div>\n' +
            '                            <div class="user-info">\n' +
            '                                <h3 class="my-name" style="color: #333;width: fit-content;">' + member.name + '</h3>\n' +
            '                            <button onclick="removeMember(\'' + member.id + '\',\'' + member.chat_id + '\')" class="button"\n' +
            '                                    contenteditable="false">移出该群\n' +
            '                            </button>\n' +
            '                            <button onclick="addFriend(\'' + member.user_id + '\')" class="button"\n' +
            '                                    contenteditable="false">加好友\n' +
            '                            </button>\n' +
            '                            </div>\n' +
            '                                <p class="my-message" style="margin-top:-25px;width: 60%;">' + member.signature + '</p>\n' +
            // '                        </div>\n' +
            '                    </div>';
        document.getElementById("content").innerHTML += html;
    }


    //显示朋友圈详情
    function addMomentDetailHtml(moment_id) {
        var html = '           <div id="' + moment.id + '" class="info-detail-block" style="margin-left: 20px">\n' +
            document.getElementById(moment_id).innerHTML +
            '                    </div>';
        document.getElementById("news-detail-box-content").innerHTML = html;
        loadRemark(moment_id, 1);
        showWindowOnRight('news-detail-box');
    }

    //加载评论
    function loadRemark(moment_id, page) {
        var url = "http://${host}/wechat/remark";
        var request = {
            method: "list.do",
            moment_id: moment_id,
            page: page
        };
        alert("正在加载评论，请稍侯...");
        postRequest(url, request, function (result) {
            var remarks = result.data;
            for (var i = 0; i < remarks.length; i++) {
                addRemarkBlockHtml(remarks[i]);
            }
        });
    }

    //插入一条用户的朋友圈评论
    function addRemarkBlockHtml(remark) {
        var time = new Date(remark.time).toLocaleString();
        var html = '           <div id="' + remark.id + '" class="info-detail-block" style="margin-left: 20px;margin-bottom: -26px">\n' +
            '                        <label for="moment-content">\n' +
            '                            <div class="info-detail-block" style="margin-bottom: -5px;">               \n' +
            '                                <div class="user-box" style="width: 95%;border-bottom: 1px solid #ccc;    margin-left: 70px;">\n' +
            '                                    <div class="user-photo">\n' +
            '                                        <img src="${pageContext.request.contextPath}/upload/photo/' + remark.user_photo + '" alt="用户头像" class="my-photo">\n' +
            '                                    </div>\n' +
            '                                    <div onclick="addMomentDetailHtml(\'' + remark.id + '\')" class="user-info" style="height: fit-content;margin-bottom: 11px;">\n' +
            '                                        <h3 class="my-name" style="color: #333">' + remark.user_name + ' 发布于 ' + time + '</h3>\n' +
            '                                        <div style="word-break: break-all;white-space: normal;max-width: 600px;">' + remark.content + '</div>\n' +
            '                                    </div>\n' +
            '                                    <button id="' + remark.id + 'love" data-love="' + remark.love + '" onclick="dev()" style="float: left"\n' +
            '                                            contenteditable="false" class="button">点赞\(' + remark.love + '\)\n' +
            '                                    </button>\n' +
            '                                    <button id="' + remark.id + 'remark" data-reply="' + remark.reply + '" onclick="replyRemark(\'' + remark.moment_id + '\',\'' + remark.user_name + '\')" style="float: left"\n' +
            '                                            contenteditable="false" class="button">回复' +
            '                                    </button>\n';
        var visitorArea =
            '                                </div>\n' +
            '                            </div>\n' +
            '                        </label>\n' +
            '                    </div>';
        var ownerArea =
            '                                    <button onclick="deleteRemark(\'' + remark.id + '\',\'' + remark.moment_id + '\')" style="float: right" \n' +
            '                                            contenteditable="false" class="button">删除\n' +
            '                                    </button>\n' +
            '                                </div>\n' +
            '                            </div>\n' +
            '                        </label>\n' +
            '                    </div>';
        if (remark.user_id ===${sessionScope.login.id}) {
            document.getElementById("news-detail-box-content").innerHTML += html + ownerArea;
        } else {
            document.getElementById("news-detail-box-content").innerHTML += html + visitorArea;
        }
    }

    /**
     * 同意举报
     * @param reportId
     */
    function agreeReport(reportId) {
        var lockedEndTime = prompt("请输入封号日期,格式yyyy-MM-dd HH:mm:ss,如2021-05-25 11:30:30", "");
        if (!lockedEndTime) {
            return;
        }
        var url = 'http://${host}/wechat/report';
        var request = {
            method: "agreereport.do",
            id:reportId,
            lockedEndTime:lockedEndTime
        };
        postRequest(url, request, function (result) {
            loadReports(document.getElementById('report-box').dataset.page);
        });
    }
    /**
     * 同意反馈
     * @param feedbackId : 反馈id
     * @param feedbackUserId : 反馈人的id
     */
    function agreeFeedback(feedbackId,feedbackUserId) {
        var url = 'http://${host}/wechat/feedback';
        var request = {
            method: "agreefeedback.do",
            id:feedbackId,
            feedbackUserId:feedbackUserId
        };
        postRequest(url, request, function (result) {
            loadFeedbacks(document.getElementById('feedback-box').dataset.page);
        });
    }

    //插入一条举报信息
    function addReportsBlockHtml(report) {
        var html = '<tr><td>'+report.user_id+'</td>'+
        '<td>'+report.friend_id+'</td><td>'+report.report_cont+'</td>'+
        '<td>'+report.status_text+'</td><td>'+report.locked_end_time_str+'</td>'+
        '<td><a href="javascript:void(0);" onclick="agreeReport(\''+report.id+'\')">同意举报</a></td></tr>';
        // 管理员可删除所有人的
        document.getElementById("report-box-content").innerHTML += html;
    }
    //插入一条反馈信息
    function addFeedbacksBlockHtml(feedback) {
        var html = '<tr><td>'+feedback.user_id+'</td>'+
            '<td>'+feedback.feedback_cont+'</td>'+
            '<td>'+feedback.status_text+'</td>'+
            '<td><a href="javascript:void(0);" onclick="agreeFeedback(\''+feedback.id+'\',\''+feedback.user_id+'\')">同意反馈</a></td></tr>';
        // 管理员可删除所有人的
        document.getElementById("feedback-box-content").innerHTML += html;
    }
    //插入一条朋友圈动态
    function addNewsBlockHtml(moment) {
        var time = new Date(moment.time).toLocaleString();
        var html = '           <div id="' + moment.id + '" class="info-detail-block" style="margin-left: 20px;margin-bottom:0px;">\n' +
            '                        <label for="moment-content" style="    margin-bottom: -47px;">\n' +
            '                            <div class="info-detail-block" >               \n' +
            '                                <div class="user-box" style="border-bottom: 1px solid #ccc;width:100%;">\n' +
            '                                    <div class="user-photo">\n' +
            '                                        <img src="${pageContext.request.contextPath}/upload/photo/' + moment.user_photo + '" alt=w"用户头像" class="my-photo">\n' +
            '                                    </div>\n' +
            '                                    <div onclick="addMomentDetailHtml(\'' + moment.id + '\')" class="user-info" style="height: fit-content;margin-bottom: 11px;">\n' +
            '                                        <h3 class="my-name" style="color: #333">' + moment.user_name + ' 发布于 ' + time + '</h3>\n' +
            '                                        <div style="word-break: break-all;white-space: normal;    max-width: 70%">' + moment.content + '</div>\n' +
            '                                        <img src="${pageContext.request.contextPath}/upload/photo/' + moment.photo + '" style="position:relative;height: 100%;' +
            '                                       max-width:500px;max-height:300px;width: 100%">\n' +
            '                                    </div>\n' +
            '                                    <button id="' + moment.id + 'love" data-love="' + moment.love + '" ' +
            '                                       onclick="loveMoment(\'' + moment.id + '\',' + moment.love + ')" style="float: left;height: 30px;"  class="button" \n' +
            '                                            contenteditable="false">点赞\(' + moment.love + '\)\n' +
            '                                    </button>\n' +
            '                                    <button id="' + moment.id + 'remark" data-remark="' + moment.remark + '" ' +
            '                                       onclick="remarkMoment(\'' + moment.id + '\',' + moment.remark + ')" style="float: left;height: 30px;"  class="button" \n' +
            '                                            contenteditable="false">评论\(' + moment.remark + '\)\n' +
            '                                    </button>\n' +
            '                                    <button onclick="dev()" style="float: left;height: 30px;"  class="button" \n' +
            '                                            contenteditable="false">收藏\(' + moment.collect + '\)\n' +
            '                                    </button>\n' +
            '                                    <button onclick="dev()" style="float: left;height: 30px;"  class="button" \n' +
            '                                            contenteditable="false">转发\(' + moment.share + '\)\n' +
            '                                    </button>\n' +
            '                                    <button onclick="" style="float: left;height: 30px;"  class="button" \n' +
            '                                            contenteditable="false">浏览量\(' + moment.view + '\)\n' +
            '                                    </button>\n';

        var ownerArea =
            '                                    <button onclick="deleteMoment(\'' + moment.id + '\')" style="float: left;margin-left: 210px;height: 30px;"  \n' +
            '                                            class="button" contenteditable="false">删除\n' +
            '                                    </button>\n' +
            '                                </div>\n' +
            '                            </div>\n' +
            '                        </label>\n' +
            '                    </div>';
        // 管理员可删除所有人的
        document.getElementById("news-box-content").innerHTML += html + ownerArea;
    }

    //插入一条用户的朋友圈
    function addMomentBlockHtml(moment) {
        var time = new Date(moment.time).toLocaleString();
        var html = '           <div id="' + moment.id + '" class="info-detail-block" style="margin-left: 20px;margin-bottom:0px;">\n' +
            '                        <label for="moment-content" style="    margin-bottom: -47px;">\n' +
            '                            <div class="info-detail-block" >               \n' +
            '                                <div class="user-box" style="border-bottom: 1px solid #ccc;width:100%;">\n' +
            '                                    <div class="user-photo">\n' +
            '                                        <img src="${pageContext.request.contextPath}/upload/photo/' + moment.user_photo + '" alt=w"用户头像" class="my-photo">\n' +
            '                                    </div>\n' +
            '                                    <div onclick="addMomentDetailHtml(\'' + moment.id + '\')" class="user-info" style="height: fit-content;margin-bottom: 11px;">\n' +
            '                                        <h3 class="my-name" style="color: #333">' + moment.user_name + ' 发布于 ' + time + '</h3>\n' +
            '                                        <div style="word-break: break-all;white-space: normal;    max-width: 70%">' + moment.content + '</div>\n' +
            '                                        <img src="${pageContext.request.contextPath}/upload/photo/' + moment.photo + '" style="position:relative;height: 100%;' +
            '                                       max-width:500px;max-height:300px;width: 100%">\n' +
            '                                    </div>\n' +
            '                                    <button id="' + moment.id + 'love" data-love="' + moment.love + '" ' +
            '                                       onclick="loveMoment(\'' + moment.id + '\',' + moment.love + ')" style="float: left;height: 30px;"  class="button"\n' +
            '                                            contenteditable="false">点赞\(' + moment.love + '\)\n' +
            '                                    </button>\n' +
            '                                    <button id="' + moment.id + 'remark" data-remark="' + moment.remark + '" ' +
            '                                       onclick="remarkMoment(\'' + moment.id + '\',' + moment.remark + ')" style="float: left;height: 30px;"  class="button"\n' +
            '                                            contenteditable="false">评论\(' + moment.remark + '\)\n' +
            '                                    </button>\n' +
            '                                    <button onclick="dev()" style="float: left;height: 30px;"  class="button"\n' +
            '                                            contenteditable="false">收藏\(' + moment.collect + '\)\n' +
            '                                    </button>\n' +
            '                                    <button onclick="dev()" style="float: left;height: 30px;"  class="button"\n' +
            '                                            contenteditable="false">转发\(' + moment.share + '\)\n' +
            '                                    </button>\n' +
            '                                    <button onclick="" style="float: left;height: 30px;"  class="button"\n' +
            '                                            contenteditable="false">浏览量\(' + moment.view + '\)\n' +
            '                                    </button>\n';

        var visitorArea =
            '                                </div>\n' +
            '                            </div>\n' +
            '                        </label>\n' +
            '                    </div>';
        var ownerArea =
            '                                    <button onclick="deleteMoment(\'' + moment.id + '\')" style="float: left;margin-left: 210px;height: 30px;"\n' +
            '                                            class="button" contenteditable="false">删除\n' +
            '                                    </button>\n' +
            '                                </div>\n' +
            '                            </div>\n' +
            '                        </label>\n' +
            '                    </div>';
        document.getElementById("moment-box-content").innerHTML += html + ownerArea;
    }

    //插入朋友圈照片
    function addPhotoHtml(photo) {
        var html = '           <div class="info-detail-block" style="margin-left: 20px">\n' +
            '                            <div class="info-detail-block">               \n' +
            '                                <div class="user-box" style="">\n' +
            '                                    <div class="user-info" style="    border-bottom: 1px solid #ccc;height: fit-content;margin-bottom: 11px;">\n' +
            '                                        <img src="${pageContext.request.contextPath}/upload/photo/' + photo + '" style="position:relative;height: 100%;' +
            'max-width:500px;max-height:300px;width: 100%">\n' +
            '                                    </div>\n' +
            '                                </div>\n' +
            '                            </div>\n' +
            '                    </div>';

        document.getElementById("photo-box-content").innerHTML += html;
    }

    //将消息显示在消息对应的聊天窗口上,并在聊天列表对应位置显示
    function addMessageToChat(message) {
        var right_bubble_html = '<div class="chat-output-content-right">\n' +
            '    <img src="${pageContext.request.contextPath}/upload/photo/' + message.sender_photo + '" alt="头像" class="chat-output-head-photo-right">\n' +
            '    <div class="chat-output-bubble-right">\n' +
            '        <div class="chat-output-bubble-inner">\n' +
            '            <pre class="chat-output-bubble-pre-right">' + message.content + '</pre></div></div></div>';
        var left_bubble_html = '<div class="chat-output-content-left">\n' +
            '    <img src="${pageContext.request.contextPath}/upload/photo/' + message.sender_photo + '" alt="头像" class="chat-output-head-photo-left">\n' +
            '    <h4 class="chat-output-meta-left">' + message.sender_name + '</h4>\n' +
            '    <div class="chat-output-bubble-left">\n' +
            '        <div class="chat-output-bubble-inner">\n' +
            '            <pre class="chat-output-bubble-pre-left">' + message.content + '</pre></div></div></div>';
        // 判断应该显示到左边还是右边
        if (message.sender_id ===${sessionScope.login.id}) {
            // 设置聊天窗口的内容251accept-message
            document.getElementById(message.chat_id + "accept-message").innerHTML += '<br/>' + right_bubble_html;
        } else {
            document.getElementById(message.chat_id + "accept-message").innerHTML += '<br/>' + left_bubble_html;
        }
        // 让滚动条自动下拉
        document.getElementById(message.chat_id + "accept-message").scrollTop = document.getElementById(message.chat_id + "accept-message").scrollHeight;
        //显示在左侧列表上
        if ("file" === message.type) {
            document.getElementById(message.chat_id + "new-message").innerText = "你收到一个文件";
        } else if ("img" === message.type) {
            document.getElementById(message.chat_id + "new-message").innerText = "你收到一个图片";
        } else {
            // 251new-message
            document.getElementById(message.chat_id + "new-message").innerText = message.content;
        }
    }

    //显示消息
    function showMessage(message) {
        if (message.type === "system") {
            alert(message.content);
            return;
        }
        if (message.type === "user" || message.type === "file" || message.type === "img") {
            console.log("显示用户消息");
            addMessageToChat(message);
        }
        if (message.type === "friend") {
            alert("收到好友申请");
            console.log("显示加好友通知");
            loadAddFriendOnMenu(message);
            addMessageToChat(message);
        }
    }

</script>
<!--END——程序执行脚本-->
</body>
</html>


