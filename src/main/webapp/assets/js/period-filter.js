(function () {
    'use strict';

    document.addEventListener('DOMContentLoaded', function () {
        document.querySelectorAll('[data-period-filter]').forEach(function (bar) {
            const target = document.querySelector(bar.dataset.filterTarget);
            const select = bar.querySelector('[data-period-select]');
            const search = bar.querySelector('[data-period-search]');
            const items = target ? Array.from(target.querySelectorAll('[data-period-item]')) : [];
            let emptyState = null;

            bar.querySelector('[data-period-total]').textContent = items.length;
            if (target && items.length > 0) {
                emptyState = document.createElement(target.tagName === 'TABLE' ? 'tr' : 'div');
                emptyState.className = target.tagName === 'TABLE' ? 'table-empty-row' : 'empty-state';
                const message = document.createElement('div');
                message.className = 'empty-state py-8';
                message.textContent = 'Không có kết quả phù hợp với đợt đăng ký hoặc từ khóa đã chọn.';
                if (target.tagName === 'TABLE') {
                    const cell = document.createElement('td');
                    cell.colSpan = target.tHead ? target.tHead.rows[0].cells.length : 1;
                    cell.appendChild(message);
                    emptyState.appendChild(cell);
                    target.tBodies[0].appendChild(emptyState);
                } else {
                    emptyState.appendChild(message);
                    target.after(emptyState);
                }
            }

            function filter() {
                const query = search ? search.value.trim().toLocaleLowerCase('vi') : '';
                let count = 0;
                items.forEach(function (item) {
                    const matches = (!select.value || item.dataset.periodId === select.value)
                        && (!query || item.textContent.toLocaleLowerCase('vi').includes(query));
                    item.dataset.searchHidden = matches ? 'false' : 'true';
                    item.style.display = matches ? '' : 'none';
                    if (matches) count++;
                });
                bar.querySelector('[data-period-count]').textContent = count;
                if (emptyState) emptyState.style.display = count === 0 ? '' : 'none';
                if (target && target._renderPagination) target._renderPagination();
            }

            select.addEventListener('change', filter);
            if (search) search.addEventListener('input', filter);
            filter();
        });
    });
})();
