// ═══════════════════════════════════════════════════════════
// STUDENT PAGES — shared logic
// ═══════════════════════════════════════════════════════════

// ─────────────────────────────────────────────────────────────
// Guard: Ensure student is logged in
// ─────────────────────────────────────────────────────────────
function guardStudentPage() {
    if (!requireRole('STUDENT')) return false;

    // Set user name in navbar
    const name = localStorage.getItem('name') || 'Student';
    const nameEl = document.getElementById('userName');
    const welcomeEl = document.getElementById('welcomeName');
    if (nameEl) nameEl.textContent = name;
    if (welcomeEl) welcomeEl.textContent = name.split(' ')[0];

    return true;
}

// ─────────────────────────────────────────────────────────────
// Main dashboard initializer
// ─────────────────────────────────────────────────────────────
async function initStudentDashboard() {
    if (!guardStudentPage()) return;

    await Promise.all([
        loadProfileSummary(),
        loadApplicationStats(),
        loadRecommendedJobs()
    ]);
}

// ─────────────────────────────────────────────────────────────
// Load profile summary
// ─────────────────────────────────────────────────────────────
async function loadProfileSummary() {
    try {
        const res = await api.get('/students/profile');
        const p = res.data;

        setText('profileDepartment', p.department || 'Not set');
        setText('profileCgpa', p.cgpa ? p.cgpa.toFixed(2) : 'Not set');
        setText('profileGradYear', p.graduationYear || 'Not set');
        setText('profileSkills', p.skills || 'Not set');

    } catch (error) {
        // Profile may not exist yet — that's OK
        if (error.message.includes('not found')) {
            setText('profileDepartment', 'Not set — please complete your profile');
        } else {
            showMessage(error.message, 'error');
        }
    }
}

// ─────────────────────────────────────────────────────────────
// Load application stats + recent applications
// ─────────────────────────────────────────────────────────────
async function loadApplicationStats() {
    try {
        const res = await api.get('/students/applications');
        const apps = res.data || [];

        // Count by status
        const counts = {
            total: apps.length,
            shortlisted: apps.filter(a => a.status === 'SHORTLISTED').length,
            interview: apps.filter(a => a.status === 'INTERVIEW_SCHEDULED').length
        };

        setText('applicationCount', counts.total);
        setText('shortlistedCount', counts.shortlisted);
        setText('interviewCount', counts.interview);

        // Render top 3 recent
        renderRecentApplications(apps.slice(0, 3));

    } catch (error) {
        showMessage('Could not load applications: ' + error.message, 'error');
        renderEmpty('recentApplications', 'No applications yet', 'Start applying to jobs!');
    }
}

// ─────────────────────────────────────────────────────────────
// Load eligible jobs for the student
// ─────────────────────────────────────────────────────────────
async function loadRecommendedJobs() {
    try {
        const res = await api.get('/students/jobs/eligible');
        const jobs = res.data || [];

        setText('eligibleCount', jobs.length);
        renderRecommendedJobs(jobs.slice(0, 3));

    } catch (error) {
        // Profile missing → not eligible check works
        setText('eligibleCount', '0');
        renderEmpty('recommendedJobs', 'Complete your profile',
            'Set your CGPA to see eligible jobs');
    }
}

// ─────────────────────────────────────────────────────────────
// Render recent applications list
// ─────────────────────────────────────────────────────────────
function renderRecentApplications(apps) {
    const el = document.getElementById('recentApplications');
    if (!el) return;

    if (!apps.length) {
        renderEmpty('recentApplications', 'No applications yet',
            'Browse jobs to start applying');
        return;
    }

    el.innerHTML = `
        <div class="table-container">
            <table>
                <thead>
                    <tr>
                        <th>Company</th>
                        <th>Role</th>
                        <th>Status</th>
                        <th>Applied On</th>
                    </tr>
                </thead>
                <tbody>
                    ${apps.map(a => `
                        <tr>
                            <td><strong>${escapeHtml(a.companyName)}</strong></td>
                            <td>${escapeHtml(a.jobRole)}</td>
                            <td><span class="badge ${statusColor(a.status)}">${a.status.replace('_', ' ')}</span></td>
                            <td>${formatDate(a.appliedDate)}</td>
                        </tr>
                    `).join('')}
                </tbody>
            </table>
        </div>
    `;
}

// ─────────────────────────────────────────────────────────────
// Render recommended jobs
// ─────────────────────────────────────────────────────────────
function renderRecommendedJobs(jobs) {
    const el = document.getElementById('recommendedJobs');
    if (!el) return;

    if (!jobs.length) {
        renderEmpty('recommendedJobs', 'No eligible jobs right now',
            'Check back soon for new opportunities');
        return;
    }

    el.innerHTML = `
        <div class="job-grid">
            ${jobs.map(j => `
                <div class="job-card">
                    <div class="job-card-header">
                        <div class="job-company">${escapeHtml(j.companyName)}</div>
                        <div class="job-package">${formatPackage(j.packageAmount)}</div>
                    </div>
                    <div class="job-role">${escapeHtml(j.jobRole)}</div>
                    <div class="job-meta">
                        <span>📍 ${escapeHtml(j.location)}</span>
                        <span>🎓 CGPA ≥ ${j.requiredCgpa}</span>
                    </div>
                    <div class="job-deadline">Deadline: <strong>${formatDate(j.deadline)}</strong></div>
                    <a href="/student/jobs.html" class="btn btn-primary btn-sm">View & Apply</a>
                </div>
            `).join('')}
        </div>
    `;
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