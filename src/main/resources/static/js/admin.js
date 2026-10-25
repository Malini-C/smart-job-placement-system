// ═══════════════════════════════════════════════════════════
// ADMIN PAGES — shared logic
// ═══════════════════════════════════════════════════════════

// ─────────────────────────────────────────────────────────────
// Guard: Ensure admin is logged in
// ─────────────────────────────────────────────────────────────
function guardAdminPage() {
    if (!requireRole('ADMIN')) return false;

    const name = localStorage.getItem('name') || 'Admin';
    const nameEl = document.getElementById('userName');
    const welcomeEl = document.getElementById('welcomeName');
    if (nameEl) nameEl.textContent = name;
    if (welcomeEl) welcomeEl.textContent = name.split(' ')[0];

    return true;
}

// ─────────────────────────────────────────────────────────────
// Admin Dashboard initializer
// ─────────────────────────────────────────────────────────────
async function initAdminDashboard() {
    if (!guardAdminPage()) return;

    await Promise.all([
        loadStats(),
        loadRecentApplications(),
        loadRecentJobs()
    ]);
}

// ─────────────────────────────────────────────────────────────
// Load all dashboard stats
// ─────────────────────────────────────────────────────────────
async function loadStats() {
    try {
        // Companies count
        try {
            const compRes = await api.get('/companies');
            setText('statCompanies', (compRes.data || []).length);
        } catch (e) { setText('statCompanies', '0'); }

        // Jobs count (active)
        try {
            const jobsRes = await api.get('/jobs/active');
            setText('statJobs', (jobsRes.data || []).length);
        } catch (e) { setText('statJobs', '0'); }

        // Applications count
        try {
            const appRes = await api.get('/admin/applications');
            const apps = appRes.data || [];
            setText('statApplications', apps.length);
            setText('statShortlisted', apps.filter(a => a.status === 'SHORTLISTED').length);
        } catch (e) {
            setText('statApplications', '0');
            setText('statShortlisted', '0');
        }

    } catch (error) {
        console.error('Stats error:', error);
    }
}

// ─────────────────────────────────────────────────────────────
// Load recent applications
// ─────────────────────────────────────────────────────────────
async function loadRecentApplications() {
    const el = document.getElementById('recentApplications');
    if (!el) return;

    try {
        const res = await api.get('/admin/applications');
        const apps = (res.data || []).slice(0, 5);

        if (!apps.length) {
            renderEmpty('recentApplications', 'No applications yet',
                'Applications will appear as students apply for jobs');
            return;
        }

        el.innerHTML = `
            <div class="table-container">
                <table>
                    <thead>
                        <tr>
                            <th>Student</th>
                            <th>Company</th>
                            <th>Role</th>
                            <th>Status</th>
                            <th>Applied</th>
                        </tr>
                    </thead>
                    <tbody>
                        ${apps.map(a => `
                            <tr>
                                <td><strong>${escapeHtml(a.studentName)}</strong></td>
                                <td>${escapeHtml(a.companyName)}</td>
                                <td>${escapeHtml(a.jobRole)}</td>
                                <td><span class="badge ${statusColor(a.status)}">${a.status.replace('_', ' ')}</span></td>
                                <td>${formatDate(a.appliedDate)}</td>
                            </tr>
                        `).join('')}
                    </tbody>
                </table>
            </div>
        `;

    } catch (error) {
        renderEmpty('recentApplications', 'Could not load applications', error.message);
    }
}

// ─────────────────────────────────────────────────────────────
// Load recent jobs
// ─────────────────────────────────────────────────────────────
async function loadRecentJobs() {
    const el = document.getElementById('recentJobs');
    if (!el) return;

    try {
        const res = await api.get('/jobs/active');
        const jobs = (res.data || []).slice(0, 5);

        if (!jobs.length) {
            renderEmpty('recentJobs', 'No active job drives',
                'Create a job drive from the Jobs page');
            return;
        }

        el.innerHTML = `
            <div class="table-container">
                <table>
                    <thead>
                        <tr>
                            <th>Company</th>
                            <th>Role</th>
                            <th>Package</th>
                            <th>Deadline</th>
                            <th>Applicants</th>
                        </tr>
                    </thead>
                    <tbody>
                        ${jobs.map(j => `
                            <tr>
                                <td><strong>${escapeHtml(j.companyName)}</strong></td>
                                <td>${escapeHtml(j.jobRole)}</td>
                                <td>${formatPackage(j.packageAmount)}</td>
                                <td>${formatDate(j.deadline)}</td>
                                <td>${j.applicationCount || 0}</td>
                            </tr>
                        `).join('')}
                    </tbody>
                </table>
            </div>
        `;

    } catch (error) {
        renderEmpty('recentJobs', 'Could not load jobs', error.message);
    }
}

// ─────────────────────────────────────────────────────────────
// Helpers
// ─────────────────────────────────────────────────────────────
function setText(id, value) {
    const el = document.getElementById(id);
    if (el) el.textContent = value;
}

function renderEmpty(containerId, title, subtitle) {
    const el = document.getElementById(containerId);
    if (!el) return;
    el.innerHTML = `
        <div class="empty-state">
            <h3>${escapeHtml(title)}</h3>
            <p>${escapeHtml(subtitle)}</p>
        </div>
    `;
}

// ─────────────────────────────────────────────────────────────
// Shared modal open/close
// ─────────────────────────────────────────────────────────────
function openModal(id) {
    const el = document.getElementById(id);
    if (el) el.classList.add('active');
}
function closeModal(id) {
    const el = document.getElementById(id);
    if (el) el.classList.remove('active');
}