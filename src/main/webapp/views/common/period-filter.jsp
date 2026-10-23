<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<div class="period-filter-bar" data-period-filter data-filter-target="${periodFilterTarget}">
    <div class="period-filter-field">
        <label for="lecturerSearchInput">Tìm kiếm</label>
        <div class="period-filter-search">
            <i data-lucide="search" aria-hidden="true"></i>
            <input id="lecturerSearchInput" type="search" data-period-search
                   placeholder="<c:out value='${periodSearchPlaceholder}' />">
        </div>
    </div>
    <div class="period-filter-field">
        <label for="lecturerPeriodFilter">Đợt đăng ký</label>
        <select id="lecturerPeriodFilter" name="periodId" data-period-select>
            <option value="">-- Tất cả đợt đăng ký --</option>
            <c:forEach var="p" items="${filterPeriods}">
                <option value="${p.id}"><c:out value="${p.name}" /></option>
            </c:forEach>
        </select>
    </div>
    <p class="period-filter-count" role="status" aria-live="polite">
        Hiển thị <strong data-period-count>0</strong> / <span data-period-total>0</span> kết quả
    </p>
</div>
