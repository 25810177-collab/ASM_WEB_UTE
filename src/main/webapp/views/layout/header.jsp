<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>${pageTitle != null ? pageTitle : 'ASM_WEB_UTE'} | HCMUTE</title>
    <link rel="icon" type="image/png" href="${pageContext.request.contextPath}/assets/img/logo/logo_hcmute.png">
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=Plus+Jakarta+Sans:wght@400;500;600;700;800&display=swap" rel="stylesheet">
    <script src="https://cdn.tailwindcss.com"></script>
    <script>
        tailwind.config = {
            theme: {
                extend: {
                    colors: {
                        ute: {
                            DEFAULT: '#003366',
                            dark: '#00264d',
                            light: '#0088cc',
                            50: '#e6f4fb',
                            100: '#cce9f7'
                        },
                        warn: '#f59e0b',
                        ok: '#10b981',
                        danger: '#ef4444'
                    },
                    fontFamily: {
                        sans: ['"Plus Jakarta Sans"', 'ui-sans-serif', 'system-ui']
                    }
                }
            }
        }
    </script>
    <script src="https://unpkg.com/lucide@latest"></script>
    <style>
        body { background: #f8fafc; }
        .ute-shadow { box-shadow: 0 10px 30px rgba(0, 51, 102, 0.08); }
        .dropzone-active { border-color: #0088cc !important; background: #e6f4fb !important; }
        .sidebar-link.active { background: #003366; color: #fff; }
        .sidebar-link.active svg { color: #fff; }
    </style>
</head>
<body class="font-sans text-slate-800 antialiased min-h-screen">
<c:set var="ctx" value="${pageContext.request.contextPath}" />
<c:set var="roleName" value="${sessionScope.user.role}" />
<header class="sticky top-0 z-40 bg-ute text-white shadow-lg">
    <div class="h-16 px-4 lg:px-6 flex items-center gap-4">
        <button type="button" id="uteSidebarToggle" class="lg:hidden p-2 rounded-lg hover:bg-white/10" aria-label="Mở menu">
            <i data-lucide="menu" class="w-5 h-5"></i>
        </button>
        <a href="${ctx}/" class="flex items-center gap-3 shrink-0">
            <img src="${ctx}/assets/img/logo/logo_nav_hcmute.png" alt="HCMUTE"
                 class="h-10 w-10 object-contain bg-white rounded-lg p-0.5"
                 onerror="this.src='${ctx}/assets/img/logo/logo_hcmute.png'">
            <div class="leading-tight">
                <div class="font-extrabold tracking-wide text-sm sm:text-base">ASM_WEB_UTE</div>
                <div class="text-[10px] text-sky-200 hidden sm:block">Đại học Sư phạm Kỹ thuật TP.HCM</div>
            </div>
        </a>

        <form action="${ctx}${roleName == 'LECTURER' ? '/lecturer/topics' : (roleName == 'STUDENT' ? '/student/topics' : '/')}"
              method="get" class="hidden md:flex flex-1 max-w-xl mx-auto">
            <label class="relative w-full">
                <span class="absolute left-3 top-1/2 -translate-y-1/2 text-slate-400">
                    <i data-lucide="search" class="w-4 h-4"></i>
                </span>
                <input type="text" name="keyword" value="${keyword}"
                       placeholder="Tìm đồ án, đề tài, môn học..."
                       class="w-full h-10 pl-10 pr-4 rounded-xl bg-white text-slate-800 text-sm placeholder:text-slate-400 focus:outline-none focus:ring-2 focus:ring-ute-light">
            </label>
        </form>

        <div class="ml-auto flex items-center gap-2">
            <div class="relative" id="notifyWrap">
                <button type="button" id="notifyBtn"
                        class="relative p-2.5 rounded-xl hover:bg-white/10 transition"
                        aria-label="Thông báo">
                    <i data-lucide="bell" class="w-5 h-5"></i>
                    <c:if test="${unreadNotifyCount != null && unreadNotifyCount > 0}">
                        <span class="absolute -top-0.5 -right-0.5 min-w-[18px] h-[18px] px-1 rounded-full bg-danger text-[10px] font-bold flex items-center justify-center">
                            <c:out value="${unreadNotifyCount}"/>
                        </span>
                    </c:if>
                </button>
                <div id="notifyDropdown" class="hidden absolute right-0 mt-2 w-80 bg-white text-slate-800 rounded-2xl shadow-xl border border-slate-100 overflow-hidden z-50">
                    <div class="px-4 py-3 border-b border-slate-100 font-bold text-sm text-ute">Thông báo mới</div>
                    <div class="max-h-72 overflow-y-auto">
                        <c:choose>
                            <c:when test="${empty topNotifications}">
                                <p class="px-4 py-8 text-center text-xs text-slate-400">Chưa có thông báo chưa đọc.</p>
                            </c:when>
                            <c:otherwise>
                                <c:forEach var="n" items="${topNotifications}">
                                    <a href="${ctx}${roleName == 'LECTURER' ? '/lecturer/notifications' : '/student/notifications'}?notificationId=${n.id}"
                                       class="block px-4 py-3 hover:bg-slate-50 border-b border-slate-50">
                                        <div class="text-xs font-bold text-slate-800">${n.title}</div>
                                        <div class="text-[11px] text-slate-500 mt-0.5 line-clamp-2">${n.content}</div>
                                    </a>
                                </c:forEach>
                            </c:otherwise>
                        </c:choose>
                    </div>
                </div>
            </div>

            <div class="relative" id="profileWrap">
                <button type="button" id="profileBtn" class="flex items-center gap-2 pl-1 pr-2 py-1 rounded-xl hover:bg-white/10">
                    <span class="w-9 h-9 rounded-full bg-ute-light text-white font-bold flex items-center justify-center">
                        ${fn:substring(sessionScope.user.fullName, 0, 1)}
                    </span>
                    <span class="hidden lg:block text-left leading-tight">
                        <span class="block text-xs font-bold">${sessionScope.user.fullName}</span>
                        <span class="block text-[10px] text-sky-200">${sessionScope.user.code}</span>
                    </span>
                    <i data-lucide="chevron-down" class="w-4 h-4 hidden lg:block"></i>
                </button>
                <div id="profileDropdown" class="hidden absolute right-0 mt-2 w-64 bg-white text-slate-800 rounded-2xl shadow-xl border border-slate-100 overflow-hidden z-50">
                    <div class="px-4 py-3 bg-slate-50">
                        <div class="font-bold text-sm">${sessionScope.user.fullName}</div>
                        <div class="text-[11px] text-slate-500">${sessionScope.user.email}</div>
                        <div class="text-[11px] font-semibold text-ute mt-1">Mã số: ${sessionScope.user.code}</div>
                    </div>
                    <a href="${ctx}${roleName == 'STUDENT' ? '/student/dashboard' : '/lecturer/dashboard'}"
                       class="flex items-center gap-2 px-4 py-2.5 text-sm hover:bg-slate-50">
                        <i data-lucide="user" class="w-4 h-4 text-ute"></i> Thông tin cá nhân
                    </a>
                    <a href="${ctx}/login" class="flex items-center gap-2 px-4 py-2.5 text-sm hover:bg-slate-50">
                        <i data-lucide="key-round" class="w-4 h-4 text-ute"></i> Đổi mật khẩu
                    </a>
                    <a href="${ctx}/logout"
                       class="flex items-center gap-2 px-4 py-2.5 text-sm font-bold text-danger hover:bg-red-50 border-t border-slate-100">
                        <i data-lucide="log-out" class="w-4 h-4"></i> Đăng xuất
                    </a>
                </div>
            </div>
        </div>
    </div>
</header>
<div class="flex min-h-[calc(100vh-4rem)]">
