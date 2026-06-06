document.addEventListener('DOMContentLoaded', () => {
    initThemeToggle();
    initSidebarState();
    initDataTables();
});

function initThemeToggle() {
    const toggle = document.getElementById('themeToggle');
    if (!toggle) return;

    const applyTheme = theme => {
        document.body.dataset.theme = theme;
        const icon = theme === 'dark' ? 'bi-sun' : 'bi-moon-stars';
        toggle.innerHTML = `<i class="bi ${icon}"></i>`;
        toggle.setAttribute('aria-label', theme === 'dark' ? 'Switch to light mode' : 'Switch to dark mode');
    };

    applyTheme(localStorage.getItem('peopleflow_theme') || 'light');
    toggle.addEventListener('click', () => {
        const next = document.body.dataset.theme === 'dark' ? 'light' : 'dark';
        localStorage.setItem('peopleflow_theme', next);
        applyTheme(next);
    });
}

function initSidebarState() {
    const currentPath = window.location.pathname;
    document.querySelectorAll('.sidebar .nav-link, .offcanvas-sidebar .nav-link').forEach(link => {
        const href = link.getAttribute('href');
        if (href && currentPath.startsWith(href) && href !== '/') {
            link.classList.add('active');
        }
    });
}

function initDataTables() {
    document.querySelectorAll('[data-table]').forEach(table => {
        const wrapper = table.closest('.data-table-wrapper');
        if (!wrapper) return;

        const searchInput = wrapper.querySelector('[data-table-search]');
        const pageSizeSelect = wrapper.querySelector('[data-table-size]');
        const pagination = wrapper.querySelector('[data-table-pagination]');
        const pageInfo = wrapper.querySelector('[data-table-info]');
        const prevBtn = wrapper.querySelector('[data-table-prev]');
        const nextBtn = wrapper.querySelector('[data-table-next]');
        const tbody = table.querySelector('tbody');
        const rows = Array.from(tbody.querySelectorAll('tr')).filter(row => !row.hasAttribute('data-empty-row'));
        const emptyRow = tbody.querySelector('[data-empty-row]');

        let filteredRows = [...rows];
        let currentPage = 1;
        let pageSize = Number(pageSizeSelect?.value || 10);

        const render = () => {
            const totalPages = Math.max(1, Math.ceil(filteredRows.length / pageSize));
            currentPage = Math.min(currentPage, totalPages);
            const start = (currentPage - 1) * pageSize;
            const end = start + pageSize;

            rows.forEach(row => row.style.display = 'none');
            filteredRows.slice(start, end).forEach(row => row.style.display = '');

            if (emptyRow) {
                emptyRow.style.display = filteredRows.length === 0 ? '' : 'none';
            }

            if (pageInfo) {
                const showingStart = filteredRows.length === 0 ? 0 : start + 1;
                const showingEnd = Math.min(end, filteredRows.length);
                pageInfo.textContent = `Showing ${showingStart}-${showingEnd} of ${filteredRows.length}`;
            }

            if (pagination) {
                pagination.classList.toggle('d-none', filteredRows.length <= pageSize);
            }

            if (prevBtn) prevBtn.disabled = currentPage <= 1;
            if (nextBtn) nextBtn.disabled = currentPage >= totalPages;
        };

        const applyFilter = () => {
            const query = (searchInput?.value || '').trim().toLowerCase();
            filteredRows = rows.filter(row => row.textContent.toLowerCase().includes(query));
            currentPage = 1;
            render();
        };

        searchInput?.addEventListener('input', applyFilter);
        pageSizeSelect?.addEventListener('change', () => {
            pageSize = Number(pageSizeSelect.value);
            currentPage = 1;
            render();
        });
        prevBtn?.addEventListener('click', () => {
            currentPage -= 1;
            render();
        });
        nextBtn?.addEventListener('click', () => {
            currentPage += 1;
            render();
        });

        render();
    });
}
