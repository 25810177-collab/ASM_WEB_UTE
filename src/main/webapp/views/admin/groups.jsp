<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<c:set scope="request" var="pageTitle" value="Quản lý nhóm sinh viên" />
<c:set scope="request" var="pageHeading" value="Quản lý nhóm sinh viên và đề tài phân công" />
<c:set scope="request" var="pageSubheading" value="Theo dõi cơ cấu thành viên (tối đa 3 SV), nhóm trưởng và đề tài đang thực hiện" />
<c:set scope="request" var="activeMenu" value="groups" />

<jsp:include page="../common/header.jsp" />
<jsp:include page="../common/sidebar.jsp" />

<div class="app-main">
    <jsp:include page="../common/navbar.jsp" />

    <main class="app-content workspace-page admin-page space-y-6">
        <!-- Toolbar -->
        <div class="admin-page-header flex flex-col sm:flex-row sm:items-center justify-between gap-4 bg-white p-5 rounded-3xl border border-slate-200 shadow-sm">
            <div class="flex items-center gap-3">
                <div class="w-10 h-10 rounded-2xl bg-sky-50 text-sky-700 flex items-center justify-center border border-sky-100 shadow-xs">
                    <i data-lucide="users" class="w-5 h-5"></i>
                </div>
                <div>
                    <h2 class="text-base font-bold text-slate-900 leading-tight">Danh sách nhóm sinh viên</h2>
                    <p class="text-xs text-slate-500 mt-0.5">Đang hiển thị <strong id="groupCount" aria-live="polite" class="text-sky-700">${fn:length(groups)}</strong> / ${fn:length(groups)} nhóm đã thành lập</p>
                </div>
            </div>
        </div>

        <!-- Bộ lọc nhóm sinh viên -->
        <div class="admin-filter-bar bg-white p-4 rounded-2xl border border-slate-200 shadow-sm grid grid-cols-1 md:grid-cols-3 gap-3">
            <div class="relative">
                <i data-lucide="search" class="w-4 h-4 text-slate-400 absolute left-3.5 top-1/2 -translate-y-1/2"></i>
                <input type="search" id="groupSearchInput" oninput="filterGroupsTable()" aria-label="Tìm nhóm sinh viên" placeholder="Tìm theo tên nhóm, nhóm trưởng, MSSV..."
                       class="w-full pl-10 pr-3.5 py-2.5 bg-slate-50 border border-slate-200 rounded-xl text-xs font-medium focus:bg-white focus:ring-2 focus:ring-sky-500 focus:outline-none transition-all">
            </div>
            <select id="groupPeriodFilter" onchange="filterGroupsTable()" aria-label="Lọc theo đợt đăng ký"
                    class="w-full px-3.5 py-2.5 bg-slate-50 border border-slate-200 rounded-xl text-xs font-semibold focus:bg-white focus:ring-2 focus:ring-sky-500 focus:outline-none transition-all">
                <option value="">-- Tất cả đợt đăng ký --</option>
                <c:forEach var="p" items="${periods}">
                    <option value="${p.id}"><c:out value="${p.name}" /></option>
                </c:forEach>
            </select>
            <select id="groupDepartmentFilter" onchange="filterGroupsTable()" aria-label="Lọc theo khoa"
                    class="w-full px-3.5 py-2.5 bg-slate-50 border border-slate-200 rounded-xl text-xs font-semibold focus:bg-white focus:ring-2 focus:ring-sky-500 focus:outline-none transition-all">
                <option value="">-- Tất cả khoa --</option>
                <c:forEach var="d" items="${departments}">
                    <option value="${d.id}"><c:out value="${d.name}" /></option>
                </c:forEach>
            </select>
        </div>

        <!-- Groups Table Shell -->
        <div class="table-shell">
            <div class="overflow-x-auto">
                <table class="w-full text-left border-collapse min-w-[960px]" id="groupsTable">
                    <thead>
                        <tr>
                            <th class="whitespace-nowrap">Tên Nhóm, Khoa và Đợt</th>
                            <th class="whitespace-nowrap">Nhóm Trưởng</th>
                            <th class="min-w-[200px]">Thành viên khác</th>
                            <th class="min-w-[260px]">Đề tài Đăng ký</th>
                            <th class="whitespace-nowrap">GVHD</th>
                        </tr>
                    </thead>
                    <tbody class="divide-y divide-slate-100 text-xs">
                        <c:forEach var="g" items="${groups}">
                            <tr class="hover:bg-slate-50/80 transition-colors group-row"
                                data-period-id="${g.registrationPeriod.id}"
                                data-department-id="${g.leader.department.id}">
                                <td class="align-middle">
                                    <div class="font-extrabold text-slate-900 text-xs"><c:out value="${g.name}" /></div>
                                    <div class="text-[11px] text-slate-500 mt-1 font-medium">
                                        <c:out value="${g.leader.department != null ? g.leader.department.name : 'Chưa xác định khoa'}" />
                                    </div>
                                    <div class="text-[11px] text-slate-500 mt-1 flex items-center gap-1 font-medium">
                                        <span><c:out value="${g.registrationPeriod != null ? g.registrationPeriod.name : 'Chung'}" /></span>
                                    </div>
                                </td>
                                <td class="align-middle whitespace-nowrap">
                                    <div class="space-y-0.5">
                                        <div class="font-bold text-slate-800 flex items-center gap-1.5">
                                            <span><c:out value="${g.leader.user.fullName}" /></span>
                                        </div>
                                        <div class="text-[11px] text-slate-500">
                                            MSSV: ${g.leader.studentCode}
                                        </div>
                                        <c:if test="${not empty g.leader.className}">
                                            <div class="text-[10px] text-slate-400">
                                                Lớp: ${g.leader.className}
                                            </div>
                                        </c:if>
                                    </div>
                                </td>
                                <td class="align-middle">
                                    <div class="space-y-1">
                                        <c:set var="hasOtherMembers" value="false" />
                                        <c:forEach var="m" items="${g.members}">
                                            <c:if test="${!m.leader}">
                                                <c:set var="hasOtherMembers" value="true" />
                                                <div class="flex items-center gap-1.5 text-[11px] whitespace-nowrap">
                                                    <span class="w-1.5 h-1.5 rounded-full bg-sky-500 shrink-0"></span>
                                                    <span class="font-semibold text-slate-800"><c:out value="${m.student.user.fullName}" /></span>
                                                    <span class="text-slate-400 font-mono">(${m.student.studentCode})</span>
                                                </div>
                                            </c:if>
                                        </c:forEach>
                                        <c:if test="${!hasOtherMembers}">
                                            <span class="text-[11px] text-slate-400 italic">Chưa có thành viên khác</span>
                                        </c:if>
                                    </div>
                                </td>
                                <td class="align-middle">
                                    <c:choose>
                                        <c:when test="${not empty g.topic}">
                                            <span class="code-tag">${g.topic.code}</span>
                                            <div class="font-bold text-slate-800 line-clamp-2 mt-1"><c:out value="${g.topic.title}" /></div>
                                        </c:when>
                                        <c:otherwise>
                                            <span class="status-badge status-neutral">Chưa chọn đề tài</span>
                                        </c:otherwise>
                                    </c:choose>
                                </td>
                                <td class="align-middle whitespace-nowrap">
                                    <c:choose>
                                        <c:when test="${not empty g.topic and g.topic.lecturer != null}">
                                            <div class="font-semibold text-slate-800"><c:out value="${g.topic.lecturer.user.fullName}" /></div>
                                        </c:when>
                                        <c:otherwise>
                                            <span class="text-[11px] text-slate-400 italic">Chưa có GVHD</span>
                                        </c:otherwise>
                                    </c:choose>
                                </td>
                            </tr>
                        </c:forEach>
                        <tr id="groupsNoMatches" class="table-empty-row" style="display: none;">
                            <td colspan="5">
                                <div class="empty-state py-8">
                                    <div class="empty-state-desc text-xs mb-0">Không có nhóm sinh viên phù hợp với bộ lọc đã chọn.</div>
                                </div>
                            </td>
                        </tr>
                        <c:if test="${empty groups}">
                            <tr class="table-empty-row">
                                <td colspan="5">
                                    <div class="empty-state py-8">
                                        <div class="empty-state-icon-wrap w-12 h-12 mb-2">
                                            <i data-lucide="users-round" class="w-6 h-6 opacity-40"></i>
                                        </div>
                                        <div class="empty-state-desc text-xs mb-0">Chưa có nhóm sinh viên nào được thành lập</div>
                                    </div>
                                </td>
                            </tr>
                        </c:if>
                    </tbody>
                </table>
            </div>
        </div>
    </main>

<jsp:include page="../common/footer.jsp" />

<script>
    function filterGroupsTable() {
        const input = document.getElementById('groupSearchInput').value.trim().toLowerCase();
        const periodId = document.getElementById('groupPeriodFilter').value;
        const departmentId = document.getElementById('groupDepartmentFilter').value;
        const rows = document.querySelectorAll('.group-row');
        let count = 0;
        rows.forEach(row => {
            const matches = row.textContent.toLowerCase().includes(input)
                && (!periodId || row.dataset.periodId === periodId)
                && (!departmentId || row.dataset.departmentId === departmentId);
            row.dataset.searchHidden = matches ? 'false' : 'true';
            row.style.display = matches ? '' : 'none';
            if (matches) count++;
        });
        document.getElementById('groupCount').textContent = count;
        document.getElementById('groupsNoMatches').style.display = rows.length > 0 && count === 0 ? '' : 'none';
        const table = document.getElementById('groupsTable');
        if (table && table._renderPagination) table._renderPagination();
    }
    document.addEventListener('DOMContentLoaded', filterGroupsTable);
</script>
