<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
</main>
</div>
<footer class="bg-white border-t border-slate-200 px-6 py-4 text-xs text-slate-500 flex flex-col sm:flex-row sm:items-center sm:justify-between gap-2">
    <span>Hệ thống quản lý đồ án &amp; bài tập lớn · Khoa CNTT · HCMUTE</span>
    <span>ASM_WEB_UTE · Spring Boot MVC + JSP</span>
</footer>
<script>
    document.addEventListener('DOMContentLoaded', function () {
        if (window.lucide) lucide.createIcons();

        function bindToggle(btnId, wrapId, menuId) {
            const btn = document.getElementById(btnId);
            const wrap = document.getElementById(wrapId);
            const menu = document.getElementById(menuId);
            if (!btn || !menu) return;
            btn.addEventListener('click', function (e) {
                e.stopPropagation();
                menu.classList.toggle('hidden');
            });
            document.addEventListener('click', function (e) {
                if (wrap && !wrap.contains(e.target)) menu.classList.add('hidden');
            });
        }
        bindToggle('notifyBtn', 'notifyWrap', 'notifyDropdown');
        bindToggle('profileBtn', 'profileWrap', 'profileDropdown');

        const sidebar = document.getElementById('uteSidebar');
        const overlay = document.getElementById('uteSidebarOverlay');
        const toggle = document.getElementById('uteSidebarToggle');
        if (toggle && sidebar && overlay) {
            toggle.addEventListener('click', function () {
                sidebar.classList.toggle('-translate-x-full');
                overlay.classList.toggle('hidden');
            });
            overlay.addEventListener('click', function () {
                sidebar.classList.add('-translate-x-full');
                overlay.classList.add('hidden');
            });
        }
    });
</script>
</body>
</html>
