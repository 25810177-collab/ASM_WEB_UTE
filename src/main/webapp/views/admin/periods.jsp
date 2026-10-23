<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<c:set scope="request" var="pageTitle" value="Quản lý đợt đăng ký" />
<c:set scope="request" var="pageHeading" value="Quản lý đợt đăng ký đề tài" />
<c:set scope="request" var="pageSubheading" value="Thiết lập các mốc thời gian, điều kiện ràng buộc GVPB và Hội đồng phản biện" />
<c:set scope="request" var="activeMenu" value="periods" />

<jsp:include page="../common/header.jsp" />
<jsp:include page="../common/sidebar.jsp" />

<div class="app-main">
    <jsp:include page="../common/navbar.jsp" />

    <main class="app-content workspace-page admin-page space-y-6">
        <!-- Top Action Bar -->
        <div class="admin-page-header flex flex-col sm:flex-row sm:items-center justify-between gap-4 bg-white p-5 rounded-3xl border border-slate-200 shadow-sm">
            <div class="flex items-center gap-3">
                <div class="w-10 h-10 rounded-2xl bg-sky-50 text-sky-700 flex items-center justify-center border border-sky-100 shadow-xs">
                    <i data-lucide="calendar-range" class="w-5 h-5"></i>
                </div>
                <div>
                    <h2 class="text-base font-bold text-slate-900 leading-tight">Danh sách các đợt đăng ký</h2>
                    <p class="text-xs text-slate-500 mt-0.5">Hiện có <strong class="text-sky-700">${fn:length(periods)}</strong> đợt trong hệ thống</p>
                </div>
            </div>
            <c:choose>
                <c:when test="${not empty editingPeriodId}">
                    <a href="${pageContext.request.contextPath}/admin/periods/new" class="btn-ui btn-ui-primary text-xs py-2.5 px-4 shadow-sm">
                        <i data-lucide="plus-circle" class="w-4 h-4"></i> Tạo đợt đăng ký mới
                    </a>
                </c:when>
                <c:otherwise>
            <button class="btn-ui btn-ui-primary text-xs py-2.5 px-4 shadow-sm"
                    data-bs-toggle="modal" data-bs-target="#createPeriodModal">
                <i data-lucide="plus-circle" class="w-4 h-4"></i> Tạo đợt đăng ký mới
            </button>
                </c:otherwise>
            </c:choose>
        </div>

        <!-- Grid Cards of Registration Periods -->
        <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6" data-pagination-list>
            <c:forEach var="p" items="${periods}">
                <div data-pagination-item class="bg-white rounded-3xl border border-slate-200 p-6 shadow-sm hover:shadow-md transition-all flex flex-col justify-between group">
                    <div>
                        <!-- Header Status Badge -->
                        <div class="flex justify-between items-start mb-4">
                            <span class="code-tag font-bold">
                                ${enumLabel.label(p.type)}
                            </span>
                            <span class="status-badge ${p.status == 'OPEN' ? 'status-approved' : p.status == 'DRAFT' ? 'status-pending' : 'status-neutral'}">
                                ${p.status == 'OPEN' ? 'ĐANG MỞ' : p.status == 'DRAFT' ? 'BẢN NHÁP' : 'ĐÃ ĐÓNG'}
                            </span>
                        </div>

                        <!-- Period Name -->
                        <h3 class="text-sm font-extrabold text-slate-900 group-hover:text-sky-600 transition-colors line-clamp-2 mb-4 leading-snug">
                            <c:out value="${p.name}" />
                        </h3>

                        <!-- Progress Bar Component -->
                        <div class="bg-slate-50 p-3.5 rounded-2xl border border-slate-200/80 mb-4 space-y-2 period-progress"
                             data-status="${p.status}"
                             data-start="${p.studentStartDate}"
                             data-end="${p.studentEndDate}">
                            <div class="flex justify-between text-[11px] font-bold text-slate-600">
                                <span>Tiến độ đợt</span>
                                <span class="text-sky-700 progress-label">${editablePeriods[p.id] ? 'Chưa bắt đầu' : p.status == 'OPEN' ? 'Đang diễn ra' : p.status == 'CLOSED' ? 'Đã kết thúc' : 'Bản nháp'}</span>
                            </div>
                            <div class="w-full bg-slate-200 rounded-full h-2 overflow-hidden">
                                <div class="progress-bar h-2 rounded-full transition-all duration-500 ${p.status == 'OPEN' ? 'bg-sky-600' : p.status == 'CLOSED' ? 'bg-slate-400' : 'bg-amber-500'}"
                                     style="width: 15%"></div>
                            </div>
                        </div>

                        <!-- Timeline Details -->
                        <div class="space-y-2.5 text-xs text-slate-600">
                            <div class="flex items-center justify-between py-1 border-b border-slate-100">
                                <span class="flex items-center gap-1.5 text-slate-500">
                                    <i data-lucide="user-check" class="w-3.5 h-3.5 text-sky-600"></i> GV Đề xuất:
                                </span>
                                <span class="font-bold text-slate-800">${dateDisplay.format(p.lecturerStartDate)} &rarr; ${dateDisplay.format(p.lecturerEndDate)}</span>
                            </div>
                            <div class="flex items-center justify-between py-1 border-b border-slate-100">
                                <span class="flex items-center gap-1.5 text-slate-500">
                                    <i data-lucide="users" class="w-3.5 h-3.5 text-emerald-600"></i> SV Đăng ký:
                                </span>
                                <span class="font-bold text-slate-800">${dateDisplay.format(p.studentStartDate)} &rarr; ${dateDisplay.format(p.studentEndDate)}</span>
                            </div>
                            <div class="flex items-center justify-between py-1 border-b border-slate-100">
                                <span class="flex items-center gap-1.5 text-slate-500">
                                    <i data-lucide="scale" class="w-3.5 h-3.5 text-purple-600"></i> Báo cáo Hội đồng:
                                </span>
                                <span class="font-bold ${not empty p.councilReportDate ? 'text-purple-700 bg-purple-50 px-1.5 py-0.5 rounded' : 'text-slate-400'}">
                                    ${not empty p.councilReportDate ? dateDisplay.format(p.councilReportDate) : 'Không bắt buộc'}
                                </span>
                            </div>
                            <div class="flex items-center justify-between py-1">
                                <span class="flex items-center gap-1.5 text-slate-500">
                                    <i data-lucide="clock" class="w-3.5 h-3.5 text-amber-500"></i> Hạn GVPB nộp điểm:
                                </span>
                                <span class="font-bold ${not empty p.reviewerDeadline ? 'text-amber-700 bg-amber-50 px-1.5 py-0.5 rounded' : 'text-slate-400'}">
                                    ${not empty p.reviewerDeadline ? dateDisplay.format(p.reviewerDeadline) : 'Không bắt buộc'}
                                </span>
                            </div>
                        </div>
                    </div>

                    <!-- Footer Action -->
                    <div class="pt-4 mt-4 border-t border-slate-100 flex items-center justify-between text-xs text-slate-400">
                        <span>Khởi tạo: <strong class="text-slate-600"><c:out value="${p.createdBy != null ? p.createdBy.fullName : 'Quản trị viên'}" /></strong></span>
                        <div class="flex items-center gap-1">
                            <a href="${pageContext.request.contextPath}/admin/periods/${p.id}"
                                class="btn-icon p-1.5 text-slate-400 hover:text-sky-600 hover:bg-sky-50 rounded-lg transition-colors"
                                title="${editablePeriods[p.id] ? 'Xem / Chỉnh sửa đợt đăng ký' : 'Xem đợt đăng ký'}"
                                aria-label="${editablePeriods[p.id] ? 'Xem / Chỉnh sửa đợt đăng ký' : 'Xem đợt đăng ký'}">
                                <i data-lucide="${editablePeriods[p.id] ? 'square-pen' : 'eye'}" class="w-4 h-4"></i>
                            </a>
                        <form method="post" action="${pageContext.request.contextPath}/admin/periods/${p.id}/delete" onsubmit="return confirmDeletePeriod(event, '${p.name}');">
                                <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}">
                            <button type="submit" class="btn-icon p-1.5 text-slate-400 hover:text-rose-600 hover:bg-rose-50 rounded-lg transition-colors" title="Xóa đợt">
                                <i data-lucide="trash-2" class="w-4 h-4"></i>
                            </button>
                        </form>
                        </div>
                    </div>
                </div>
            </c:forEach>
        </div>
    </main>

    <!-- Modal Tạo Đợt Đăng Ký Mới -->
    <div class="modal fade" id="createPeriodModal" tabindex="-1" aria-labelledby="createPeriodTitle" aria-hidden="true" data-reopen="${periodFormHasErrors or periodFormOpen}">
        <div class="modal-dialog modal-lg modal-dialog-centered modal-dialog-scrollable">
            <div class="modal-content rounded-3xl border-0 shadow-2xl overflow-hidden">
                <form:form modelAttribute="period" id="createPeriodForm" method="post" cssClass="managed-modal-form" novalidate="novalidate" action="${pageContext.request.contextPath}/admin/periods/save">
                    <form:hidden path="id" />
                    <div class="bg-gradient-to-r from-sky-700 to-blue-900 px-6 py-4 text-white flex justify-between items-center">
                        <h5 id="createPeriodTitle" class="text-base font-bold flex items-center gap-2">
                            <i data-lucide="calendar-plus" class="w-5 h-5 text-sky-300"></i>
                            ${periodReadOnly ? 'Thông tin đợt đăng ký' : not empty editingPeriodId ? 'Chỉnh sửa đợt đăng ký' : 'Tạo đợt đăng ký đề tài mới'}
                        </h5>
                        <button type="button" class="btn-icon text-white/80 hover:text-white p-1" data-bs-dismiss="modal" aria-label="Đóng">
                            <i data-lucide="x" class="w-5 h-5"></i>
                        </button>
                    </div>

                    <div class="modal-body p-6 space-y-4">
                        <c:if test="${periodFormHasErrors}">
                            <p role="alert" class="form-field-error">Chưa thể lưu đợt đăng ký. Vui lòng sửa các thông tin được đánh dấu bên dưới.</p>
                        </c:if>
                        <form:errors path="" element="div" cssClass="form-field-error" />
                        <form:errors path="id" element="p" cssClass="form-field-error" />
                        <c:if test="${periodReadOnly}">
                            <p class="rounded-xl border border-amber-200 bg-amber-50 p-3 text-xs text-amber-800" role="status">
                                Đợt đăng ký đã bắt đầu hoặc đã đóng. Bạn chỉ có thể xem thông tin, không thể chỉnh sửa.
                            </p>
                        </c:if>
                        <!-- Rule notification banner -->
                        <div class="bg-sky-50 border border-sky-200 text-sky-950 rounded-2xl p-4 text-xs flex items-start gap-2.5">
                            <i data-lucide="info" class="w-4 h-4 text-sky-600 mt-0.5 shrink-0"></i>
                            <div>
                                <strong class="font-bold">Quy định ràng buộc thời hạn:</strong>
                                <div class="mt-0.5 text-slate-600">Đợt loại <strong>TLCN</strong> và <strong>KLTN</strong> bắt buộc nhập Hạn nộp điểm GVPB. Đợt <strong>KLTN</strong> bắt buộc nhập Ngày báo cáo hội đồng.</div>
                            </div>
                        </div>

                        <!-- Name -->
                        <fieldset class="space-y-4 min-w-0" ${periodReadOnly ? 'disabled' : ''}>
                        <div>
                            <label for="periodNameInput" class="block text-xs font-bold text-slate-700 mb-1.5">Tên đợt đăng ký <span class="text-rose-500">*</span></label>
                            <form:input path="name" id="periodNameInput" cssClass="period-field-control" cssErrorClass="period-field-control is-invalid" aria-describedby="periodNameError" maxlength="255" placeholder="Ví dụ: Đợt KLTN Học kỳ 2 năm học 2026-2027" required="required" />
                            <form:errors path="name" id="periodNameError" element="p" cssClass="form-field-error" />
                        </div>

                        <!-- Type và Status -->
                        <div class="grid grid-cols-1 sm:grid-cols-2 gap-4">
                            <div>
                                <label for="periodTypeSelect" class="block text-xs font-bold text-slate-700 mb-1.5">Loại đợt đăng ký <span class="text-rose-500">*</span></label>
                                <form:select path="type" id="periodTypeSelect" cssClass="period-field-control" cssErrorClass="period-field-control is-invalid" aria-describedby="periodTypeError" onchange="handlePeriodTypeChange(this.value)" required="required">
                                    <form:option value="">-- Chọn loại đợt --</form:option>
                                    <form:option value="KLTN">KHÓA LUẬN TỐT NGHIỆP (KLTN)</form:option>
                                    <form:option value="TLCN">TIỂU LUẬN CHUYÊN NGÀNH (TLCN)</form:option>
                                    <form:option value="NCKH">NGHIÊN CỨU KHOA HỌC (NCKH)</form:option>
                                </form:select>
                                <form:errors path="type" id="periodTypeError" element="p" cssClass="form-field-error" />
                            </div>
                            <div>
                                <label for="periodStatusSelect" class="block text-xs font-bold text-slate-700 mb-1.5">${not empty editingPeriodId ? 'Trạng thái' : 'Trạng thái khởi tạo'}</label>
                                <form:select path="status" id="periodStatusSelect" cssClass="period-field-control" cssErrorClass="period-field-control is-invalid" aria-describedby="periodStatusError" required="required">
                                    <form:option value="">-- Chọn trạng thái --</form:option>
                                    <form:option value="OPEN">MỞ</form:option>
                                    <form:option value="DRAFT">BẢN NHÁP</form:option>
                                    <form:option value="CLOSED">ĐÓNG</form:option>
                                </form:select>
                                <form:errors path="status" id="periodStatusError" element="p" cssClass="form-field-error" />
                            </div>
                        </div>

                        <!-- Timeline Lecturers và Students -->
                        <div class="bg-slate-50 p-4 rounded-2xl border border-slate-200 space-y-3">
                            <h6 class="text-xs font-bold text-slate-800 flex items-center gap-1.5">
                                <i data-lucide="clock" class="w-4 h-4 text-sky-600"></i> Khung thời gian GV Đề xuất và SV Đăng ký
                            </h6>
                            <div class="grid grid-cols-1 sm:grid-cols-2 gap-3">
                                <div>
                                    <label for="lecturerStartDateInput" class="block text-[11px] font-semibold text-slate-600 mb-1">GV bắt đầu đề xuất</label>
                                    <form:input type="date" path="lecturerStartDate" id="lecturerStartDateInput" cssClass="period-field-control" cssErrorClass="period-field-control is-invalid" aria-describedby="lecturerStartDateError" required="required" />
                                    <form:errors path="lecturerStartDate" id="lecturerStartDateError" element="p" cssClass="form-field-error" />
                                </div>
                                <div>
                                    <label for="lecturerEndDateInput" class="block text-[11px] font-semibold text-slate-600 mb-1">GV kết thúc đề xuất</label>
                                    <form:input type="date" path="lecturerEndDate" id="lecturerEndDateInput" cssClass="period-field-control" cssErrorClass="period-field-control is-invalid" aria-describedby="lecturerEndDateError" required="required" />
                                    <form:errors path="lecturerEndDate" id="lecturerEndDateError" element="p" cssClass="form-field-error" />
                                </div>
                                <div>
                                    <label for="studentStartDateInput" class="block text-[11px] font-semibold text-slate-600 mb-1">SV bắt đầu đăng ký</label>
                                    <form:input type="date" path="studentStartDate" id="studentStartDateInput" cssClass="period-field-control" cssErrorClass="period-field-control is-invalid" aria-describedby="studentStartDateError" required="required" />
                                    <form:errors path="studentStartDate" id="studentStartDateError" element="p" cssClass="form-field-error" />
                                </div>
                                <div>
                                    <label for="studentEndDateInput" class="block text-[11px] font-semibold text-slate-600 mb-1">SV kết thúc đăng ký</label>
                                    <form:input type="date" path="studentEndDate" id="studentEndDateInput" cssClass="period-field-control" cssErrorClass="period-field-control is-invalid" aria-describedby="studentEndDateError" required="required" />
                                    <form:errors path="studentEndDate" id="studentEndDateError" element="p" cssClass="form-field-error" />
                                </div>
                            </div>
                        </div>

                        <!-- Dynamic Fade-in Fields for GVPB và Council (TLCN / KLTN) -->
                        <div id="advancedDeadlinePanel" class="bg-sky-50/50 p-4 rounded-2xl border border-sky-100 space-y-3 fade-panel show">
                            <h6 class="text-xs font-bold text-sky-950 flex items-center gap-1.5">
                                <i data-lucide="scale" class="w-4 h-4 text-sky-600"></i> Ngày báo cáo Hội đồng và Hạn GVPB nộp điểm
                            </h6>
                            <div class="grid grid-cols-1 sm:grid-cols-2 gap-3">
                                <div id="councilDateContainer" class="transition-all duration-300">
                                    <label for="councilDateInput" class="block text-[11px] font-semibold text-slate-700 mb-1">
                                        Ngày báo cáo hội đồng <span class="text-rose-500" id="councilReqMark">*</span>
                                    </label>
                                    <form:input type="date" path="councilReportDate" id="councilDateInput" cssClass="period-field-control" cssErrorClass="period-field-control is-invalid" aria-describedby="councilReportDateError" />
                                    <form:errors path="councilReportDate" id="councilReportDateError" element="p" cssClass="form-field-error" />
                                    <span class="text-[10px] text-slate-500 mt-1 block">Bắt buộc đối với KLTN</span>
                                </div>
                                <div id="reviewerDeadlineContainer" class="transition-all duration-300">
                                    <label for="reviewerDeadlineInput" class="block text-[11px] font-semibold text-slate-700 mb-1">
                                        Hạn chót GVPB nộp điểm <span class="text-rose-500" id="reviewerReqMark">*</span>
                                    </label>
                                    <form:input type="date" path="reviewerDeadline" id="reviewerDeadlineInput" cssClass="period-field-control" cssErrorClass="period-field-control is-invalid" aria-describedby="reviewerDeadlineError" />
                                    <form:errors path="reviewerDeadline" id="reviewerDeadlineError" element="p" cssClass="form-field-error" />
                                    <span class="text-[10px] text-slate-500 mt-1 block">Bắt buộc đối với TLCN và KLTN</span>
                                </div>
                            </div>
                        </div>
                        </fieldset>
                    </div>

                    <div class="bg-slate-50 px-6 py-4 border-t border-slate-100 flex justify-end gap-3">
                        <button type="button" class="btn-ui btn-ui-outline text-xs" data-bs-dismiss="modal">${periodReadOnly ? 'Đóng' : 'Hủy'}</button>
                        <c:if test="${not periodReadOnly}">
                            <button type="submit" class="btn-ui btn-ui-primary text-xs">
                                ${not empty editingPeriodId ? 'Lưu thay đổi' : 'Lưu và áp dụng đợt'}
                            </button>
                        </c:if>
                    </div>
                </form:form>
            </div>
        </div>
    </div>

<jsp:include page="../common/footer.jsp" />

<script>
    function handlePeriodTypeChange(val) {
        const panel = document.getElementById('advancedDeadlinePanel');
        const revInput = document.getElementById('reviewerDeadlineInput');
        const revMark = document.getElementById('reviewerReqMark');
        const revContainer = document.getElementById('reviewerDeadlineContainer');
        const couInput = document.getElementById('councilDateInput');
        const couMark = document.getElementById('councilReqMark');
        const couContainer = document.getElementById('councilDateContainer');

        const needsPanel = (val === 'KLTN' || val === 'TLCN' || panel.querySelector('.form-field-error') || revInput.value || couInput.value);
        if (panel) {
            panel.classList.toggle('show', needsPanel);
        }

        if (val === 'KLTN') {
            revContainer.style.opacity = '1';
            revInput.required = true;
            revMark.style.display = 'inline';
            couContainer.style.opacity = '1';
            couContainer.style.display = '';
            couInput.required = true;
            couMark.style.display = 'inline';
        } else if (val === 'TLCN') {
            revContainer.style.opacity = '1';
            revInput.required = true;
            revMark.style.display = 'inline';
            couContainer.style.opacity = '1';
            couInput.required = false;
            couMark.style.display = 'none';
        } else {
            revContainer.style.opacity = '1';
            couContainer.style.opacity = '1';
            revInput.required = false;
            couInput.required = false;
            revMark.style.display = 'none';
            couMark.style.display = 'none';
        }
    }

    document.addEventListener('DOMContentLoaded', function () {
        const sel = document.getElementById('periodTypeSelect');
        if (sel) handlePeriodTypeChange(sel.value);
        const modal = document.getElementById('createPeriodModal');
        if (modal.dataset.reopen === 'true') {
            modal.querySelectorAll('.is-invalid').forEach(function (field) { field.setAttribute('aria-invalid', 'true'); });
            modal.addEventListener('shown.bs.modal', function () {
                const firstInvalid = modal.querySelector('.is-invalid');
                if (firstInvalid) {
                    firstInvalid.focus({ preventScroll: true });
                    firstInvalid.scrollIntoView({ block: 'center' });
                }
            }, { once: true });
            bootstrap.Modal.getOrCreateInstance(modal).show();
        }

        document.querySelectorAll('.period-progress').forEach(function (el) {
            const status = el.dataset.status;
            const start = el.dataset.start ? new Date(el.dataset.start) : null;
            const end = el.dataset.end ? new Date(el.dataset.end) : null;
            const bar = el.querySelector('.progress-bar');
            let pct = 15;
            if (status === 'CLOSED') pct = 100;
            else if (status === 'DRAFT') pct = 10;
            else if (start && end && end > start) {
                const now = new Date();
                pct = ((now - start) / (end - start)) * 100;
                pct = Math.max(5, Math.min(98, pct));
            }
            if (bar) bar.style.width = pct.toFixed(0) + '%';
        });
    });

    function confirmDeletePeriod(event, name) {
        event.preventDefault();
        const form = event.target;
        Swal.fire({
            title: 'Xóa đợt đăng ký?',
            text: 'Bạn có chắc chắn muốn xóa đợt "' + name + '" không? Các dữ liệu liên quan sẽ bị ảnh hưởng.',
            icon: 'warning',
            showCancelButton: true,
            confirmButtonColor: '#dc2626',
            cancelButtonColor: '#64748b',
            confirmButtonText: 'Đồng ý xóa',
            cancelButtonText: 'Hủy',
            customClass: { popup: 'rounded-3xl shadow-xl' }
        }).then((result) => {
            if (result.isConfirmed) form.submit();
        });
        return false;
    }
</script>
