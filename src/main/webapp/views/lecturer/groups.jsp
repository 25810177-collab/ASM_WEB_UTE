<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<c:set scope="request" var="pageTitle" value="Duyệt đăng ký SV" />
<c:set scope="request" var="pageHeading" value="Duyệt đăng ký nhóm sinh viên" />
<c:set scope="request" var="pageSubheading" value="Nhóm SV đăng ký đề tài của bạn — GVHD chấp nhận hoặc từ chối" />
<c:set scope="request" var="activeMenu" value="groups" />
<c:set scope="request" var="periodFilterTarget" value="#lecturerGroupsTable" />
<c:set scope="request" var="periodSearchPlaceholder" value="Tìm nhóm, mã đề tài, thành viên, MSSV..." />

<jsp:include page="../common/header.jsp" />
<jsp:include page="../common/sidebar.jsp" />

<div class="app-main">
    <jsp:include page="../common/navbar.jsp" />

    <main class="app-content workspace-page space-y-6">
        <jsp:include page="../common/period-filter.jsp" />
        <div class="table-shell">
            <div class="p-5 border-b border-slate-100 flex items-center justify-between bg-white">
                <div class="flex items-center gap-2.5">
                    <div class="w-8 h-8 rounded-xl bg-sky-50 text-sky-700 flex items-center justify-center border border-sky-100">
                        <i data-lucide="users" class="w-4 h-4"></i>
                    </div>
                    <h3 class="text-sm font-bold text-slate-900">
                        Danh sách đăng ký đề tài hướng dẫn (${fn:length(registrations)})
                    </h3>
                </div>
            </div>

            <c:choose>
                <c:when test="${empty registrations}">
                    <div class="empty-state py-12">
                        <div class="empty-state-icon-wrap w-12 h-12 mb-2">
                            <i data-lucide="inbox" class="w-6 h-6 opacity-40"></i>
                        </div>
                        <div class="empty-state-desc text-xs mb-0">Chưa có nhóm nào đăng ký đề tài của Thầy/Cô</div>
                    </div>
                </c:when>
                <c:otherwise>
                    <div class="overflow-x-auto">
                        <table class="w-full text-left border-collapse datatable-pagination" id="lecturerGroupsTable" data-page-size="10">
                            <thead>
                                <tr>
                                    <th class="w-[18%]">Tên nhóm và Đợt</th>
                                    <th class="w-[24%]">Đề tài Đăng ký</th>
                                    <th class="w-[22%]">Thành viên nhóm</th>
                                    <th class="w-[18%]">Ghi chú và Lý do</th>
                                    <th class="w-[8%] text-center whitespace-nowrap">Trạng thái</th>
                                    <th class="w-[10%] text-center whitespace-nowrap">Thao tác</th>
                                </tr>
                            </thead>
                            <tbody class="divide-y divide-slate-100 text-xs">
                                <c:forEach var="reg" items="${registrations}">
                                    <tr class="hover:bg-slate-50/80 transition-colors" data-period-item
                                        data-period-id="${reg.group.registrationPeriod != null ? reg.group.registrationPeriod.id : reg.topic.registrationPeriod.id}">
                                        <td class="align-top">
                                            <div class="font-extrabold text-slate-900 leading-snug"><c:out value="${reg.group.name}" /></div>
                                            <div class="text-[10px] text-slate-500 mt-1 flex items-center gap-1 font-medium">
                                                <span><c:out value="${reg.group.registrationPeriod != null ? reg.group.registrationPeriod.name : (reg.topic.registrationPeriod != null ? reg.topic.registrationPeriod.name : 'Chung')}" /></span>
                                            </div>
                                        </td>

                                        <td class="align-top">
                                            <span class="code-tag">${reg.topic.code}</span>
                                            <div class="font-bold text-slate-900 leading-snug mt-1">
                                                <c:out value="${reg.topic.title}" />
                                            </div>
                                        </td>

                                        <td class="align-top">
                                            <div class="space-y-1.5">
                                                <c:forEach var="m" items="${reg.group.members}">
                                                    <div class="flex items-start gap-1.5">
                                                        <c:choose>
                                                            <c:when test="${m.leader}">
                                                                <i data-lucide="crown" class="w-3.5 h-3.5 text-amber-500 shrink-0 mt-0.5"></i>
                                                            </c:when>
                                                            <c:otherwise>
                                                                <i data-lucide="user" class="w-3.5 h-3.5 text-slate-400 shrink-0 mt-0.5"></i>
                                                            </c:otherwise>
                                                        </c:choose>
                                                        <div class="min-w-0 leading-tight">
                                                            <div class="text-[11px] font-bold ${m.leader ? 'text-amber-900' : 'text-slate-800'}">
                                                                <c:out value="${m.student.user.fullName}" /> 
                                                                <c:if test="${m.leader}">
                                                                    <span class="text-amber-600 font-medium text-[10px]">(Nhóm trưởng)</span>
                                                                </c:if>
                                                            </div>
                                                            <div class="text-[10px] text-slate-500 mt-0.5 font-mono">
                                                                MSSV: ${m.student.studentCode}
                                                            </div>
                                                        </div>
                                                    </div>
                                                </c:forEach>
                                            </div>
                                        </td>

                                        <td class="align-top">
                                            <div class="text-slate-600 italic leading-snug">
                                                <c:out value="${reg.note != null ? reg.note : 'Không có'}" />
                                            </div>
                                            <c:if test="${not empty reg.rejectionReason}">
                                                <div class="mt-1.5 p-2 rounded-xl text-rose-700 text-[10px] leading-relaxed">
                                                    <strong>Lý do từ chối:</strong> <c:out value="${reg.rejectionReason}" />
                                                </div>
                                            </c:if>
                                        </td>

                                        <td class="align-middle text-center whitespace-nowrap">
                                            <c:choose>
                                                <c:when test="${reg.status == 'APPROVED'}">
                                                    <span class="status-badge status-approved">CHẤP NHẬN</span>
                                                </c:when>
                                                <c:when test="${reg.status == 'PENDING'}">
                                                    <span class="status-badge status-pending">CHỜ DUYỆT</span>
                                                </c:when>
                                                <c:when test="${reg.status == 'REJECTED'}">
                                                    <span class="status-badge status-danger">TỪ CHỐI</span>
                                                </c:when>
                                                <c:otherwise>
                                                    <span class="status-badge status-neutral">${enumLabel.label(reg.status)}</span>
                                                </c:otherwise>
                                            </c:choose>
                                        </td>

                                        <td class="align-middle text-center whitespace-nowrap">
                                            <button type="button" class="btn-ui btn-ui-outline w-full mb-2"
                                                data-registration-view="${reg.id}" data-bs-toggle="modal" data-bs-target="#registrationDetailModal"
                                                aria-label="Xem thông tin nhóm và đăng ký">
                                                <i data-lucide="eye"></i> Xem
                                            </button>
                                            <c:if test="${reg.status == 'PENDING'}">
                                                    <div class="flex flex-col gap-1.5 items-center">
                                                        <form method="post" action="${pageContext.request.contextPath}/lecturer/groups/${reg.id}/status" class="w-full">
                                <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}">
                                                            <input type="hidden" name="status" value="APPROVED">
                                                            <button type="submit" class="btn-ui bg-emerald-600 hover:bg-emerald-700 text-white w-full py-1 text-[11px] rounded-lg shadow-xs" title="Chấp nhận">
                                                                <i data-lucide="check" class="w-3.5 h-3.5"></i> Duyệt
                                                            </button>
                                                        </form>
                                                        <button type="button" onclick="rejectGroupSweetAlert('${reg.id}', '${fn:escapeXml(reg.group.name)}')"
                                                                class="btn-ui bg-rose-50 hover:bg-rose-600 text-rose-700 hover:text-white border border-rose-200 w-full py-1 text-[11px] rounded-lg transition-colors" title="Từ chối">
                                                            <i data-lucide="x" class="w-3.5 h-3.5"></i> Từ chối
                                                        </button>
                                                    </div>
                                            </c:if>
                                        </td>
                                    </tr>
                                </c:forEach>
                            </tbody>
                        </table>
                    </div>
                </c:otherwise>
            </c:choose>
        </div>
    </main>

    <c:forEach var="reg" items="${registrations}">
        <template id="registrationDetail-${reg.id}">
            <div class="space-y-5 text-sm">
                <dl class="grid grid-cols-1 sm:grid-cols-2 gap-4">
                    <div><dt class="text-xs font-bold text-slate-500">Tên nhóm</dt><dd class="mt-1 font-semibold break-words"><c:out value="${reg.group.name}" /></dd></div>
                    <div><dt class="text-xs font-bold text-slate-500">Đăng ký #${reg.id}</dt><dd class="mt-1"><c:out value="${enumLabel.label(reg.status)}" /></dd></div>
                    <div class="sm:col-span-2"><dt class="text-xs font-bold text-slate-500">Đợt đăng ký</dt><dd class="mt-1"><c:out value="${reg.group.registrationPeriod != null ? reg.group.registrationPeriod.name : (reg.topic.registrationPeriod != null ? reg.topic.registrationPeriod.name : 'Chung')}" /></dd></div>
                    <div><dt class="text-xs font-bold text-slate-500">Thời gian gửi</dt><dd class="mt-1">${dateDisplay.format(reg.createdAt)}</dd></div>
                    <div><dt class="text-xs font-bold text-slate-500">Người xử lý</dt><dd class="mt-1"><c:out value="${reg.approvedBy != null ? reg.approvedBy.fullName : 'Chưa xử lý'}" /></dd></div>
                </dl>

                <section class="rounded-xl border border-slate-200 bg-slate-50 p-4 space-y-2">
                    <h6 class="text-sm font-bold text-slate-900">Đề tài đăng ký</h6>
                    <div class="text-xs font-mono font-semibold text-sky-700"><c:out value="${reg.topic.code}" /></div>
                    <p class="font-semibold break-words"><c:out value="${reg.topic.title}" /></p>
                    <p class="text-xs text-slate-600">Khoa: <c:out value="${reg.topic.department.name}" /></p>
                    <p class="text-xs text-slate-600">GVHD chính: <c:out value="${reg.topic.lecturer != null ? reg.topic.lecturer.user.fullName : 'Chưa phân công'}" /></p>
                    <c:if test="${not empty reg.topic.coLecturer}">
                        <p class="text-xs text-slate-600">Đồng hướng dẫn: <c:out value="${reg.topic.coLecturer.user.fullName}" /></p>
                    </c:if>
                    <c:if test="${not empty reg.topic.description}">
                        <p class="text-xs text-slate-600 whitespace-pre-wrap break-words"><c:out value="${reg.topic.description}" /></p>
                    </c:if>
                </section>

                <section>
                    <h6 class="text-sm font-bold text-slate-900 mb-3">Thành viên hiện tại (${fn:length(reg.group.members)} / 3)</h6>
                    <div class="grid grid-cols-1 sm:grid-cols-2 gap-3">
                        <c:forEach var="member" items="${reg.group.members}">
                            <div class="rounded-xl border border-slate-200 p-3 space-y-1 text-xs">
                                <div class="font-semibold text-slate-900 break-words">
                                    <c:out value="${member.student.user.fullName}" />
                                    <c:if test="${member.leader}"><span class="font-semibold text-amber-700 whitespace-nowrap">(Nhóm trưởng)</span></c:if>
                                </div>
                                <div>MSSV: <c:out value="${member.student.studentCode}" /></div>
                                <div>Lớp: <c:out value="${member.student.className}" /></div>
                                <div>Khoa: <c:out value="${member.student.department.name}" /></div>
                                <div class="break-all">Email: <c:out value="${member.student.user.email}" /></div>
                            </div>
                        </c:forEach>
                    </div>
                </section>

                <section>
                    <h6 class="text-xs font-bold text-slate-500 mb-2">Ghi chú đăng ký</h6>
                    <p class="text-sm whitespace-pre-wrap break-words"><c:out value="${not empty reg.note ? reg.note : 'Không có ghi chú'}" /></p>
                </section>
                <c:if test="${reg.status == 'REJECTED'}">
                    <section class="rounded-xl border border-rose-200 bg-rose-50 p-3 text-rose-800">
                        <h6 class="text-xs font-bold mb-2">Lý do từ chối</h6>
                        <p class="text-sm whitespace-pre-wrap break-words"><c:out value="${not empty reg.rejectionReason ? reg.rejectionReason : 'Chưa cung cấp lý do'}" /></p>
                    </section>
                </c:if>
            </div>
        </template>
    </c:forEach>

    <div class="modal fade" id="registrationDetailModal" tabindex="-1" aria-labelledby="registrationDetailTitle" aria-hidden="true">
        <div class="modal-dialog modal-lg modal-dialog-centered modal-dialog-scrollable">
            <div class="modal-content border-0 shadow-2xl">
                <div class="topic-modal-header modal-header-hero flex items-center justify-between">
                    <h5 id="registrationDetailTitle" class="font-bold">Thông tin nhóm và đăng ký đề tài</h5>
                    <button type="button" class="btn-icon text-white" data-bs-dismiss="modal" aria-label="Đóng"><i data-lucide="x"></i></button>
                </div>
                <div id="registrationDetailContent" class="modal-body topic-modal-body"></div>
                <div class="topic-modal-footer bg-slate-50 flex justify-end border-t border-slate-100">
                    <button type="button" class="btn-ui btn-ui-outline" data-bs-dismiss="modal">Đóng</button>
                </div>
            </div>
        </div>
    </div>

    <jsp:include page="../common/footer.jsp" />

<script>
    document.addEventListener('DOMContentLoaded', function () {
        document.getElementById('registrationDetailModal').addEventListener('show.bs.modal', function (event) {
            const template = document.getElementById('registrationDetail-' + event.relatedTarget?.dataset.registrationView);
            if (template) document.getElementById('registrationDetailContent').replaceChildren(template.content.cloneNode(true));
        });
    });

    function rejectGroupSweetAlert(regId, groupName) {
        Swal.fire({
            title: 'Từ chối nhóm đăng ký?',
            text: 'Vui lòng nhập lý do từ chối hướng dẫn cho nhóm "' + groupName + '":',
            input: 'textarea',
            inputPlaceholder: 'Nhập lý do chi tiết (VD: Nhóm chưa đáp ứng kiến thức tiên quyết, GV đã nhận đủ số lượng đề tài...)...',
            showCancelButton: true,
            confirmButtonText: 'Xác nhận từ chối',
            cancelButtonText: 'Hủy bỏ',
            confirmButtonColor: '#dc2626',
            cancelButtonColor: '#64748b',
            customClass: { popup: 'rounded-3xl shadow-2xl' },
            inputValidator: (value) => {
                if (!value || !value.trim()) {
                    return 'Bắt buộc phải nhập lý do từ chối!';
                }
            }
        }).then((result) => {
            if (result.isConfirmed) {
                const form = document.createElement('form');
                form.method = 'POST';
                form.action = '${pageContext.request.contextPath}/lecturer/groups/' + regId + '/status';
                const statusInput = document.createElement('input');
                statusInput.type = 'hidden';
                statusInput.name = 'status';
                statusInput.value = 'REJECTED';
                form.appendChild(statusInput);
                const reasonInput = document.createElement('input');
                reasonInput.type = 'hidden';
                reasonInput.name = 'rejectionReason';
                reasonInput.value = result.value.trim();
                form.appendChild(reasonInput);
                // Form tạo bằng JavaScript cũng phải gửi kèm mã CSRF, nếu không Spring Security trả 403
                const csrfInput = document.createElement('input');
                csrfInput.type = 'hidden';
                csrfInput.name = '${_csrf.parameterName}';
                csrfInput.value = '${_csrf.token}';
                form.appendChild(csrfInput);
                document.body.appendChild(form);
                form.submit();
            }
        });
    }
</script>
