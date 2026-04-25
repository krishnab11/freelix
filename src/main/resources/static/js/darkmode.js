/**
 * Freelix Dark Mode Toggle
 * Persists preference in localStorage
 */

function toggleDarkMode() {
    const body = document.body;
    const isDark = body.classList.toggle('dark-mode');
    localStorage.setItem('freelix-dark-mode', isDark ? '1' : '0');
    updateToggleIcon(isDark);
}

function updateToggleIcon(isDark) {
    const btn = document.getElementById('darkModeToggle');
    if (!btn) return;
    btn.innerHTML = isDark
        ? '<i class="bi bi-sun-fill"></i> Light Mode'
        : '<i class="bi bi-moon-stars"></i> Dark Mode';
}

// Apply saved preference on page load
(function () {
    const saved = localStorage.getItem('freelix-dark-mode');
    if (saved === '1') {
        document.body.classList.add('dark-mode');
        document.addEventListener('DOMContentLoaded', function () {
            updateToggleIcon(true);
        });
    }
})();
