/**
 * Admin Dashboard Main JavaScript
 */

// State
let currentPage = 'dashboard';
let usersPage = 1;
let vocabPage = 1;
let userTrendChart = null;
let levelDistChart = null;

// ==================== INITIALIZATION ====================

document.addEventListener('DOMContentLoaded', async () => {
    console.log('🌸 Sakura Admin Initializing...');

    // 1. Core Handlers
    initNavigation();
    initTabs();
    initSearch();
    initEditForm();
    initQuizForm();
    initVocabModalHandlers();
    initQuizModalHandlers();

    // 2. Theme (this might trigger a loadDashboard, so we handle it)
    initTheme();

    // 3. UI Extras
    updateTime();
    setInterval(updateTime, 1000);
    if (window.lucide) {
        try {
            lucide.createIcons();
        } catch (e) {
            console.error('Lucide error:', e);
        }
    }

    // 4. Initial Data Load (only if not already loading)
    if (!document.querySelector('.stat-card h3').textContent || document.querySelector('.stat-card h3').textContent === '0') {
        loadDashboard();
    }
});

function updateTime() {
    document.getElementById('current-time').textContent =
        new Date().toLocaleString('vi-VN');
}

// ==================== NAVIGATION ====================

function initNavigation() {
    document.querySelectorAll('.nav-item').forEach(item => {
        item.addEventListener('click', (e) => {
            e.preventDefault();
            const page = item.dataset.page;
            switchPage(page);
        });
    });
}

function switchPage(page) {
    currentPage = page;

    // Update nav
    document.querySelectorAll('.nav-item').forEach(i => i.classList.remove('active'));
    document.querySelector(`[data-page="${page}"]`).classList.add('active');

    // Update pages
    document.querySelectorAll('.page').forEach(p => p.classList.remove('active'));
    document.getElementById(`${page}-page`).classList.add('active');

    // Update title
    const titles = { dashboard: 'Dashboard', users: 'User Management', content: 'Content Management' };
    document.getElementById('page-title').textContent = titles[page];

    // Load data
    if (page === 'dashboard') loadDashboard();
    if (page === 'users') loadUsers();
    if (page === 'content') loadVocabularies();
}

// ==================== DASHBOARD ====================

async function loadDashboard() {
    const result = await api.get('/stats');
    if (result.success) {
        const data = result.data;
        document.getElementById('stat-total-users').textContent = data.users.total;
        document.getElementById('stat-active-users').textContent = data.users.active;
        document.getElementById('stat-vocabularies').textContent = data.content.vocabularies;
        document.getElementById('stat-quizzes').textContent = data.content.quizSets;

        // Recent users
        const tbody = document.getElementById('recent-users-table');
        if (data.recentUsers.length > 0) {
            tbody.innerHTML = data.recentUsers.map(user => `
                <tr>
                    <td>${user.username}</td>
                    <td>${user.email}</td>
                    <td>${new Date(user.createdAt).toLocaleDateString('vi-VN')}</td>
                </tr>
            `).join('');
        } else {
            tbody.innerHTML = '<tr><td colspan="3">No users yet</td></tr>';
        }

        // Initialize/Update Charts
        initCharts(data);
    }
}

function initCharts(data) {
    if (!window.Chart) {
        console.warn('Chart.js not loaded yet. Skipping chart initialization.');
        return;
    }

    try {
        const isDark = document.documentElement.getAttribute('data-theme') === 'dark';
        const textColor = isDark ? '#e0e0e0' : '#212529';
        const gridColor = isDark ? 'rgba(255, 255, 255, 0.05)' : 'rgba(0, 0, 0, 0.05)';

        // 1. User Trend Chart
        const trendEl = document.getElementById('userTrendChart');
        if (trendEl) {
            const ctxTrend = trendEl.getContext('2d');
            if (userTrendChart) userTrendChart.destroy();

            const labels = Array.from({ length: 7 }, (_, i) => {
                const d = new Date();
                d.setDate(d.getDate() - (6 - i));
                return d.toLocaleDateString('vi-VN', { weekday: 'short' });
            });

            const trendData = [
                Math.floor((data.users?.total || 10) * 0.7),
                Math.floor((data.users?.total || 10) * 0.75),
                Math.floor((data.users?.total || 10) * 0.82),
                Math.floor((data.users?.total || 10) * 0.9),
                Math.floor((data.users?.total || 10) * 0.95),
                Math.floor((data.users?.total || 10) * 0.98),
                data.users?.total || 0
            ];

            userTrendChart = new Chart(ctxTrend, {
                type: 'line',
                data: {
                    labels: labels,
                    datasets: [{
                        label: 'Total Users',
                        data: trendData,
                        borderColor: '#e91e63',
                        backgroundColor: 'rgba(233, 30, 99, 0.1)',
                        fill: true,
                        tension: 0.4,
                        borderWidth: 3,
                        pointRadius: 4,
                        pointBackgroundColor: '#e91e63'
                    }]
                },
                options: {
                    responsive: true,
                    maintainAspectRatio: false,
                    plugins: { legend: { display: false } },
                    scales: {
                        y: { grid: { color: gridColor }, ticks: { color: textColor } },
                        x: { grid: { display: false }, ticks: { color: textColor } }
                    }
                }
            });
        }

        // 2. Level Distribution Chart
        const distEl = document.getElementById('levelDistChart');
        if (distEl) {
            const ctxDist = distEl.getContext('2d');
            if (levelDistChart) levelDistChart.destroy();

            levelDistChart = new Chart(ctxDist, {
                type: 'doughnut',
                data: {
                    labels: ['N5', 'N4', 'N3', 'N2', 'N1'],
                    datasets: [{
                        data: [40, 25, 20, 10, 5],
                        backgroundColor: ['#28a745', '#007bff', '#9c27b0', '#ff9800', '#f44336'],
                        borderWidth: 0
                    }]
                },
                options: {
                    responsive: true,
                    maintainAspectRatio: false,
                    plugins: {
                        legend: { position: 'bottom', labels: { color: textColor, padding: 20 } }
                    },
                    cutout: '70%'
                }
            });
        }
    } catch (err) {
        console.error('Error initializing charts:', err);
    }
}

// ==================== USERS ====================

async function loadUsers(page = 1, search = '') {
    usersPage = page;
    const result = await api.get(`/users?page=${page}&limit=10&search=${search}`);

    if (result.success) {
        const { users, pagination } = result.data;
        const tbody = document.getElementById('users-table');
        tbody.innerHTML = '';

        if (users.length > 0) {
            users.forEach(user => {
                const tr = document.createElement('tr');

                // Username
                const tdUsername = document.createElement('td');
                tdUsername.textContent = user.username;
                tr.appendChild(tdUsername);

                // Email
                const tdEmail = document.createElement('td');
                tdEmail.textContent = user.email;
                tr.appendChild(tdEmail);

                // Level
                const tdLevel = document.createElement('td');
                const levelBadge = document.createElement('span');
                levelBadge.className = `badge ${getLevelBadgeClass(user.profile?.currentLevel || 'N5')}`;
                levelBadge.textContent = user.profile?.currentLevel || 'N5';
                tdLevel.appendChild(levelBadge);
                tr.appendChild(tdLevel);

                // Role
                const tdRole = document.createElement('td');
                const roleBadge = document.createElement('span');
                roleBadge.className = `badge ${user.role === 'admin' ? 'badge-warning' : 'badge-success'}`;
                roleBadge.textContent = user.role;
                tdRole.appendChild(roleBadge);
                tr.appendChild(tdRole);

                // Status
                const tdStatus = document.createElement('td');
                const statusBadge = document.createElement('span');
                statusBadge.className = `badge ${user.isActive ? 'badge-success' : 'badge-danger'}`;
                statusBadge.textContent = user.isActive ? 'Active' : 'Locked';
                tdStatus.appendChild(statusBadge);
                tr.appendChild(tdStatus);

                // Actions
                const tdActions = document.createElement('td');
                tdActions.className = 'action-btns';

                // Edit button
                const editBtn = document.createElement('button');
                editBtn.className = 'btn btn-sm btn-primary';
                editBtn.textContent = 'Edit';
                editBtn.addEventListener('click', () => editUser(user._id));
                tdActions.appendChild(editBtn);

                // Lock/Unlock button
                const lockBtn = document.createElement('button');
                lockBtn.className = `btn btn-sm ${user.isActive ? 'btn-danger' : 'btn-secondary'}`;
                lockBtn.textContent = user.isActive ? 'Lock' : 'Unlock';
                lockBtn.addEventListener('click', () => toggleLock(user._id));
                tdActions.appendChild(lockBtn);

                // Delete button
                const deleteBtn = document.createElement('button');
                deleteBtn.className = 'btn btn-sm btn-danger';
                deleteBtn.textContent = 'Delete';
                deleteBtn.addEventListener('click', () => deleteUser(user._id));
                tdActions.appendChild(deleteBtn);

                tr.appendChild(tdActions);
                tbody.appendChild(tr);
            });
        } else {
            const tr = document.createElement('tr');
            const td = document.createElement('td');
            td.colSpan = 6;
            td.textContent = 'No users found';
            tr.appendChild(td);
            tbody.appendChild(tr);
        }

        // Pagination
        renderPagination(pagination, 'users-pagination', (p) => loadUsers(p, search));

        if (window.lucide) lucide.createIcons();
    }
}

// Global pagination callbacks
window.paginationCallbacks = {};

function renderPagination(pagination, elementId, callback) {
    const el = document.getElementById(elementId);
    if (!el || !pagination || pagination.pages <= 1) {
        if (el) el.innerHTML = '';
        return;
    }

    // Store callback
    window.paginationCallbacks[elementId] = callback;

    // Clear existing content
    el.innerHTML = '';

    const currentPage = pagination.page;
    const totalPages = pagination.pages;
    const maxVisible = 5; // Chỉ hiển thị tối đa 5 số trang

    // Previous button
    if (currentPage > 1) {
        const prevBtn = document.createElement('button');
        prevBtn.textContent = '«';
        prevBtn.addEventListener('click', () => callback(currentPage - 1));
        el.appendChild(prevBtn);
    }

    // Calculate page range to show
    let startPage = Math.max(1, currentPage - Math.floor(maxVisible / 2));
    let endPage = Math.min(totalPages, startPage + maxVisible - 1);

    // Adjust if near the end
    if (endPage - startPage < maxVisible - 1) {
        startPage = Math.max(1, endPage - maxVisible + 1);
    }

    // First page + ellipsis
    if (startPage > 1) {
        const firstBtn = document.createElement('button');
        firstBtn.textContent = '1';
        firstBtn.addEventListener('click', () => callback(1));
        el.appendChild(firstBtn);

        if (startPage > 2) {
            const dots = document.createElement('span');
            dots.textContent = '...';
            dots.style.padding = '8px 4px';
            dots.style.color = '#999';
            el.appendChild(dots);
        }
    }

    // Page numbers
    for (let i = startPage; i <= endPage; i++) {
        const pageBtn = document.createElement('button');
        pageBtn.textContent = i;
        if (i === currentPage) {
            pageBtn.className = 'active';
        }
        pageBtn.addEventListener('click', () => callback(i));
        el.appendChild(pageBtn);
    }

    // Last page + ellipsis
    if (endPage < totalPages) {
        if (endPage < totalPages - 1) {
            const dots = document.createElement('span');
            dots.textContent = '...';
            dots.style.padding = '8px 4px';
            dots.style.color = '#999';
            el.appendChild(dots);
        }

        const lastBtn = document.createElement('button');
        lastBtn.textContent = totalPages;
        lastBtn.addEventListener('click', () => callback(totalPages));
        el.appendChild(lastBtn);
    }

    // Next button
    if (currentPage < totalPages) {
        const nextBtn = document.createElement('button');
        nextBtn.textContent = '»';
        nextBtn.addEventListener('click', () => callback(currentPage + 1));
        el.appendChild(nextBtn);
    }
}

function initSearch() {
    const searchInput = document.getElementById('user-search');
    let timeout;
    searchInput.addEventListener('input', (e) => {
        clearTimeout(timeout);
        timeout = setTimeout(() => loadUsers(1, e.target.value), 300);
    });
}

async function editUser(id) {
    const result = await api.get(`/users/${id}`);
    if (result.success) {
        const user = result.data;
        document.getElementById('edit-user-id').value = id;
        document.getElementById('edit-username').value = user.username;
        document.getElementById('edit-email').value = user.email;
        document.getElementById('edit-displayname').value = user.profile?.displayName || '';
        document.getElementById('edit-role').value = user.role;
        document.getElementById('edit-modal').classList.add('active');
    }
}

function closeModal() {
    document.getElementById('edit-modal').classList.remove('active');
}

function initEditForm() {
    // Close modal buttons
    document.getElementById('close-modal-btn').addEventListener('click', closeModal);
    document.getElementById('cancel-modal-btn').addEventListener('click', closeModal);

    // Click outside modal to close
    document.getElementById('edit-modal').addEventListener('click', (e) => {
        if (e.target.id === 'edit-modal') {
            closeModal();
        }
    });

    // Form submit
    document.getElementById('edit-user-form').addEventListener('submit', async (e) => {
        e.preventDefault();
        const id = document.getElementById('edit-user-id').value;
        const data = {
            username: document.getElementById('edit-username').value,
            email: document.getElementById('edit-email').value,
            role: document.getElementById('edit-role').value,
            profile: {
                displayName: document.getElementById('edit-displayname').value
            }
        };

        const result = await api.put(`/users/${id}`, data);
        if (result.success) {
            closeModal();
            loadUsers(usersPage);
            alert('User updated successfully!');
        } else {
            alert('Error: ' + result.message);
        }
    });
}

async function toggleLock(id) {
    if (!confirm('Are you sure?')) return;
    const result = await api.post(`/users/${id}/toggle-lock`);
    if (result.success) {
        loadUsers(usersPage);
    } else {
        alert('Error: ' + result.message);
    }
}

async function deleteUser(id) {
    if (!confirm('Are you sure you want to DELETE this user? This cannot be undone!')) return;
    const result = await api.delete(`/users/${id}`);
    if (result.success) {
        loadUsers(usersPage);
        alert('User deleted!');
    } else {
        alert('Error: ' + result.message);
    }
}

// ==================== CONTENT (TABS) ====================

function initTabs() {
    document.querySelectorAll('.tab-btn').forEach(btn => {
        btn.addEventListener('click', () => {
            const tab = btn.dataset.tab;
            document.querySelectorAll('.tab-btn').forEach(b => b.classList.remove('active'));
            document.querySelectorAll('.tab-content').forEach(c => c.classList.remove('active'));
            btn.classList.add('active');
            document.getElementById(`${tab}-tab`).classList.add('active');

            if (tab === 'vocabularies') loadVocabularies();
            if (tab === 'quizzes') loadQuizzes();
        });
    });

    // Level filter
    document.getElementById('vocab-level-filter').addEventListener('change', () => {
        loadVocabularies();
    });

    // Topic filter
    document.getElementById('vocab-topic-filter').addEventListener('change', () => {
        loadVocabularies();
    });

    // Load topics dynamically
    loadTopics();
}

async function loadTopics() {
    const result = await api.get('/vocabularies?limit=1000');
    if (result.success) {
        const topics = [...new Set(result.data.vocabularies.map(v => v.topic))].sort();
        const select = document.getElementById('vocab-topic-filter');
        select.innerHTML = '<option value="">All Topics</option>' +
            topics.map(t => `<option value="${t}">${formatTopic(t)}</option>`).join('');
    }
}

function formatTopic(topic) {
    return topic.replace(/_/g, ' ').toLowerCase().replace(/\b\w/g, c => c.toUpperCase());
}

async function loadVocabularies(page = 1) {
    vocabPage = page;
    const level = document.getElementById('vocab-level-filter').value;
    const topic = document.getElementById('vocab-topic-filter').value;

    let url = `/vocabularies?page=${page}&limit=20`;
    if (level) url += `&level=${level}`;
    if (topic) url += `&topic=${topic}`;

    const result = await api.get(url);

    if (result.success) {
        const vocabularies = result.data?.vocabularies || [];
        const pagination = result.data?.pagination;
        const tbody = document.getElementById('vocabularies-table');
        tbody.innerHTML = '';

        if (vocabularies.length > 0) {
            vocabularies.forEach(v => {
                const tr = document.createElement('tr');

                // Topic
                const tdTopic = document.createElement('td');
                const topicBadge = document.createElement('span');
                topicBadge.className = 'badge badge-secondary';
                topicBadge.textContent = formatTopic(v.topic || '-');
                tdTopic.appendChild(topicBadge);
                tr.appendChild(tdTopic);

                // Japanese
                const tdJapanese = document.createElement('td');
                const japaneseStrong = document.createElement('strong');
                japaneseStrong.textContent = v.word?.japanese || '-';
                tdJapanese.appendChild(japaneseStrong);
                tr.appendChild(tdJapanese);

                // Reading
                const tdReading = document.createElement('td');
                tdReading.textContent = v.word?.reading || '-';
                tr.appendChild(tdReading);

                // Meaning
                const tdMeaning = document.createElement('td');
                tdMeaning.textContent = v.word?.meaning || '-';
                tr.appendChild(tdMeaning);

                // Level
                const tdLevel = document.createElement('td');
                const levelBadge = document.createElement('span');
                levelBadge.className = `badge ${getLevelBadgeClass(v.level)}`;
                levelBadge.textContent = v.level || '-';
                tdLevel.appendChild(levelBadge);
                tr.appendChild(tdLevel);

                // Actions
                const tdActions = document.createElement('td');
                tdActions.className = 'action-btns';

                const editBtn = document.createElement('button');
                editBtn.className = 'btn btn-sm btn-primary';
                editBtn.textContent = 'Edit';
                editBtn.addEventListener('click', () => openEditVocabModal(v));
                tdActions.appendChild(editBtn);

                const deleteBtn = document.createElement('button');
                deleteBtn.className = 'btn btn-sm btn-danger';
                deleteBtn.textContent = 'Delete';
                deleteBtn.addEventListener('click', () => deleteVocab(v._id));
                tdActions.appendChild(deleteBtn);

                tr.appendChild(tdActions);
                tbody.appendChild(tr);
            });
        } else {
            const tr = document.createElement('tr');
            const td = document.createElement('td');
            td.colSpan = 6;
            td.textContent = 'No vocabularies found';
            tr.appendChild(td);
            tbody.appendChild(tr);
        }

        if (pagination) {
            renderPagination(pagination, 'vocab-pagination', loadVocabularies);
        }
    }
}

// Get badge class for JLPT level
function getLevelBadgeClass(level) {
    const levelMap = {
        'N5': 'badge-n5',
        'N4': 'badge-n4',
        'N3': 'badge-n3',
        'N2': 'badge-n2',
        'N1': 'badge-n1'
    };
    return levelMap[level] || 'badge-info';
}

// Helper function to escape HTML
function escapeHtml(text) {
    if (text === null || text === undefined) return '-';
    const div = document.createElement('div');
    div.textContent = String(text);
    return div.innerHTML;
}

// ==================== VOCABULARY CRUD ====================

function initVocabModalHandlers() {
    // Add vocabulary button
    document.getElementById('add-vocab-btn').addEventListener('click', openAddVocabModal);

    // Close/cancel buttons
    document.getElementById('close-vocab-modal').addEventListener('click', closeVocabModal);
    document.getElementById('cancel-vocab-modal').addEventListener('click', closeVocabModal);

    // Click outside modal to close
    document.getElementById('vocab-modal').addEventListener('click', (e) => {
        if (e.target.id === 'vocab-modal') closeVocabModal();
    });

    // Form submit
    document.getElementById('vocab-form').addEventListener('submit', handleVocabSubmit);
}

function openAddVocabModal() {
    document.getElementById('vocab-modal-title').textContent = 'Add Vocabulary';
    document.getElementById('vocab-id').value = '';
    document.getElementById('vocab-japanese').value = '';
    document.getElementById('vocab-reading').value = '';
    document.getElementById('vocab-meaning').value = '';
    document.getElementById('vocab-topic').value = '';
    document.getElementById('vocab-level').value = 'N5';
    document.getElementById('vocab-modal').classList.add('active');
}

function openEditVocabModal(vocab) {
    document.getElementById('vocab-modal-title').textContent = 'Edit Vocabulary';
    document.getElementById('vocab-id').value = vocab._id;
    document.getElementById('vocab-japanese').value = vocab.word?.japanese || '';
    document.getElementById('vocab-reading').value = vocab.word?.reading || '';
    document.getElementById('vocab-meaning').value = vocab.word?.meaning || '';
    document.getElementById('vocab-topic').value = vocab.topic || '';
    document.getElementById('vocab-level').value = vocab.level || 'N5';
    document.getElementById('vocab-modal').classList.add('active');
}

function closeVocabModal() {
    document.getElementById('vocab-modal').classList.remove('active');
}

async function handleVocabSubmit(e) {
    e.preventDefault();

    const id = document.getElementById('vocab-id').value;
    const data = {
        word: {
            japanese: document.getElementById('vocab-japanese').value,
            reading: document.getElementById('vocab-reading').value,
            meaning: document.getElementById('vocab-meaning').value
        },
        topic: document.getElementById('vocab-topic').value,
        level: document.getElementById('vocab-level').value
    };

    let result;
    if (id) {
        result = await api.put(`/vocabularies/${id}`, data);
    } else {
        result = await api.post('/vocabularies', data);
    }

    if (result.success) {
        closeVocabModal();
        loadVocabularies(vocabPage);
        alert(id ? 'Vocabulary updated!' : 'Vocabulary added!');
    } else {
        alert('Error: ' + result.message);
    }
}

async function deleteVocab(id) {
    if (!confirm('Are you sure you want to delete this vocabulary?')) return;

    const result = await api.delete(`/vocabularies/${id}`);
    if (result.success) {
        loadVocabularies(vocabPage);
        alert('Vocabulary deleted!');
    } else {
        alert('Error: ' + result.message);
    }
}

// ==================== QUIZ SETS ====================

async function loadQuizzes() {
    const result = await api.get('/quiz-sets');

    if (result.success) {
        const quizzes = result.data || [];
        const tbody = document.getElementById('quizzes-table');
        tbody.innerHTML = '';

        if (quizzes.length > 0) {
            quizzes.forEach(q => {
                const tr = document.createElement('tr');

                // Topic
                const tdTopic = document.createElement('td');
                tdTopic.textContent = formatTopic(q.topic || '-');
                tr.appendChild(tdTopic);

                // Level
                const tdLevel = document.createElement('td');
                const levelBadge = document.createElement('span');
                levelBadge.className = `badge ${getLevelBadgeClass(q.level)}`;
                levelBadge.textContent = q.level || '-';
                tdLevel.appendChild(levelBadge);
                tr.appendChild(tdLevel);

                // Questions count
                const tdQuestions = document.createElement('td');
                tdQuestions.textContent = q.questions?.length || 0;
                tr.appendChild(tdQuestions);

                // Actions
                const tdActions = document.createElement('td');
                tdActions.className = 'action-btns';

                const viewBtn = document.createElement('button');
                viewBtn.className = 'btn btn-sm btn-info';
                viewBtn.innerHTML = '<i data-lucide="eye" style="width:14px"></i>';
                viewBtn.title = 'View Details';
                viewBtn.addEventListener('click', () => viewQuizSet(q._id));
                tdActions.appendChild(viewBtn);

                const editBtn = document.createElement('button');
                editBtn.className = 'btn btn-sm btn-primary';
                editBtn.innerHTML = '<i data-lucide="edit-3" style="width:14px"></i>';
                editBtn.title = 'Edit Title/Desc';
                editBtn.addEventListener('click', () => openEditQuizModal(q));
                tdActions.appendChild(editBtn);

                const deleteBtn = document.createElement('button');
                deleteBtn.className = 'btn btn-sm btn-danger';
                deleteBtn.innerHTML = '<i data-lucide="trash-2" style="width:14px"></i>';
                deleteBtn.title = 'Delete Quiz Set';
                deleteBtn.addEventListener('click', () => deleteQuizSet(q._id));
                tdActions.appendChild(deleteBtn);

                tr.appendChild(tdActions);
                tbody.appendChild(tr);
            });

            if (window.lucide) lucide.createIcons();
        } else {
            const tr = document.createElement('tr');
            const td = document.createElement('td');
            td.colSpan = 4;
            td.textContent = 'No quiz sets found';
            tr.appendChild(td);
            tbody.appendChild(tr);
        }
    }
}

function initQuizForm() {
    document.getElementById('add-quiz-btn').addEventListener('click', openAddQuizModal);
    document.getElementById('close-quiz-form-btn').addEventListener('click', closeQuizFormModal);
    document.getElementById('cancel-quiz-form-btn').addEventListener('click', closeQuizFormModal);

    document.getElementById('quiz-form').addEventListener('submit', handleQuizSubmit);

    document.getElementById('quiz-form-modal').addEventListener('click', (e) => {
        if (e.target.id === 'quiz-form-modal') closeQuizFormModal();
    });
}

function openAddQuizModal() {
    document.getElementById('quiz-form-title').textContent = 'Add New Quiz Set';
    document.getElementById('quiz-id').value = '';
    document.getElementById('quiz-topic').value = '';
    document.getElementById('quiz-level').value = 'N5';
    document.getElementById('quiz-title').value = '';
    document.getElementById('quiz-description').value = '';
    document.getElementById('quiz-form-modal').classList.add('active');
}

function openEditQuizModal(quiz) {
    document.getElementById('quiz-form-title').textContent = 'Edit Quiz Set';
    document.getElementById('quiz-id').value = quiz._id;
    document.getElementById('quiz-topic').value = quiz.topic || '';
    document.getElementById('quiz-level').value = quiz.level || 'N5';
    document.getElementById('quiz-title').value = quiz.title || '';
    document.getElementById('quiz-description').value = quiz.description || '';
    document.getElementById('quiz-form-modal').classList.add('active');
}

function closeQuizFormModal() {
    document.getElementById('quiz-form-modal').classList.remove('active');
}

async function handleQuizSubmit(e) {
    e.preventDefault();
    const id = document.getElementById('quiz-id').value;
    const data = {
        topic: document.getElementById('quiz-topic').value,
        level: document.getElementById('quiz-level').value,
        title: document.getElementById('quiz-title').value,
        description: document.getElementById('quiz-description').value
    };

    const result = id ? await api.put(`/quiz-sets/${id}`, data) : await api.post('/quiz-sets', data);

    if (result.success) {
        closeQuizFormModal();
        loadQuizzes();
        alert(id ? 'Quiz set updated!' : 'Quiz set created!');
    } else {
        alert('Error: ' + result.message);
    }
}

async function deleteQuizSet(id) {
    if (!confirm('Are you sure you want to delete this quiz set?')) return;
    const result = await api.delete(`/quiz-sets/${id}`);
    if (result.success) {
        loadQuizzes();
        alert('Quiz set deleted!');
    } else {
        alert('Error: ' + result.message);
    }
}

function initQuizModalHandlers() {
    document.getElementById('close-quiz-modal').addEventListener('click', closeQuizModal);
    document.getElementById('close-quiz-btn').addEventListener('click', closeQuizModal);
    document.getElementById('quiz-modal').addEventListener('click', (e) => {
        if (e.target.id === 'quiz-modal') closeQuizModal();
    });
}

function closeQuizModal() {
    document.getElementById('quiz-modal').classList.remove('active');
}

async function viewQuizSet(id) {
    document.getElementById('quiz-modal-content').innerHTML = '<p>Loading...</p>';
    document.getElementById('quiz-modal').classList.add('active');

    const result = await api.get(`/quiz-sets/${id}`);

    if (result.success) {
        const quiz = result.data;
        document.getElementById('quiz-modal-title').textContent =
            `${formatTopic(quiz.topic)} - ${quiz.level}`;

        let html = `<p><strong>Total Questions:</strong> ${quiz.questions?.length || 0}</p>`;

        if (quiz.questions && quiz.questions.length > 0) {
            html += '<div style="max-height: 400px; overflow-y: auto;">';
            quiz.questions.forEach((q, i) => {
                html += `
                    <div style="padding: 10px; margin: 8px 0; background: var(--bg-dark); border-radius: 6px;">
                        <p><strong>Q${i + 1}:</strong> ${escapeHtml(q.question)}</p>
                        <p style="color: var(--text-secondary); font-size: 12px;">
                            Type: ${q.type} | Points: ${q.points || 10}
                        </p>
                    </div>
                `;
            });
            html += '</div>';
        }

        document.getElementById('quiz-modal-content').innerHTML = html;
    } else {
        document.getElementById('quiz-modal-content').innerHTML =
            '<p style="color: var(--danger);">Error loading quiz set</p>';
    }
}

// ==================== THEME ====================

function initTheme() {
    const toggle = document.getElementById('theme-toggle');
    const icon = document.getElementById('theme-icon');
    const text = document.getElementById('theme-text');

    // Load saved theme
    const savedTheme = localStorage.getItem('admin-theme') || 'light';
    setTheme(savedTheme);

    toggle.addEventListener('click', () => {
        const currentTheme = document.documentElement.getAttribute('data-theme');
        const newTheme = currentTheme === 'dark' ? 'light' : 'dark';
        setTheme(newTheme);
    });
}

function setTheme(theme) {
    const icon = document.getElementById('theme-icon');
    const text = document.getElementById('theme-text');

    document.documentElement.setAttribute('data-theme', theme);
    localStorage.setItem('admin-theme', theme);

    if (theme === 'dark') {
        icon.setAttribute('data-lucide', 'sun');
        text.textContent = 'Light Mode';
    } else {
        icon.setAttribute('data-lucide', 'moon');
        text.textContent = 'Dark Mode';
    }

    if (window.lucide) lucide.createIcons();

    // Refresh charts if needed to update colors
    if (currentPage === 'dashboard') {
        const statEl = document.getElementById('stat-total-users');
        const total = statEl ? parseInt(statEl.textContent) || 0 : 0;

        // Only reload if we actually have data or if this is a theme switch, 
        // but avoid the very first redundant call if possible.
        if (total > 0) {
            loadDashboard();
        }
    }
}

