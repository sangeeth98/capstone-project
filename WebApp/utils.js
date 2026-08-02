/**
 * SentinelAI — Shared Utilities
 *
 * Toast notifications, CSV export, confirmation dialogs,
 * and date/time formatting helpers.
 */

// ═══════════════════════════════════════════════
// TOAST NOTIFICATIONS
// ═══════════════════════════════════════════════

/**
 * Displays a non-intrusive custom toast notification.
 * @param {string} message - The message content
 * @param {'success' | 'error' | 'info'} type - The notification type
 */
export function showToast(message, type = 'info') {
    let container = document.querySelector('.toast-container');
    if (!container) {
        container = document.createElement('div');
        container.className = 'toast-container';
        document.body.appendChild(container);
    }

    const toast = document.createElement('div');
    toast.className = `toast toast-${type}`;

    const icons = { success: '✅', error: '⚠️', info: 'ℹ️' };

    toast.innerHTML = `
        <span style="font-size: 1.1rem; flex-shrink: 0;">${icons[type] || '🔔'}</span>
        <span class="toast-message">${message}</span>
    `;

    container.appendChild(toast);

    // Auto-remove after 4 seconds
    setTimeout(() => {
        toast.classList.add('fade-out');
        setTimeout(() => toast.remove(), 300);
    }, 4000);
}


// ═══════════════════════════════════════════════
// CONFIRMATION DIALOG
// ═══════════════════════════════════════════════

/**
 * Shows a modal confirmation dialog.
 * @param {string} title - Dialog title
 * @param {string} bodyHtml - Dialog body (HTML allowed)
 * @returns {Promise<boolean>} - Resolves true if confirmed, false if cancelled
 */
export function showConfirmDialog(title, bodyHtml) {
    return new Promise((resolve) => {
        const overlay = document.createElement('div');
        overlay.className = 'dialog-overlay';

        overlay.innerHTML = `
            <div class="dialog">
                <div class="dialog-title">${title}</div>
                <div class="dialog-body">${bodyHtml}</div>
                <div class="dialog-actions">
                    <button class="btn btn-ghost btn-sm" id="dialogCancel">Cancel</button>
                    <button class="btn btn-primary btn-sm" id="dialogConfirm" style="width: auto; min-width: 100px;">Confirm</button>
                </div>
            </div>
        `;

        document.body.appendChild(overlay);

        overlay.querySelector('#dialogConfirm').addEventListener('click', () => {
            overlay.remove();
            resolve(true);
        });

        overlay.querySelector('#dialogCancel').addEventListener('click', () => {
            overlay.remove();
            resolve(false);
        });

        // Close on backdrop click
        overlay.addEventListener('click', (e) => {
            if (e.target === overlay) {
                overlay.remove();
                resolve(false);
            }
        });
    });
}


// ═══════════════════════════════════════════════
// CSV EXPORT
// ═══════════════════════════════════════════════

/**
 * Exports all visible datatable tables to a CSV file.
 * @param {string} filenamePrefix - Prefix for the download filename
 */
export function exportTableToCSV(filenamePrefix = 'sentinelai-export') {
    const tables = document.querySelectorAll('table.datatable');
    if (tables.length === 0) {
        showToast("No data tables to export.", "info");
        return;
    }

    let csv = '';

    tables.forEach((table, idx) => {
        // Include caption as section header
        const caption = table.querySelector('caption');
        if (caption) {
            csv += `"${caption.textContent.trim()}"\n`;
        }

        // Headers
        const headers = [];
        table.querySelectorAll('thead th').forEach(th => {
            headers.push(`"${th.textContent.trim()}"`);
        });
        csv += headers.join(',') + '\n';

        // Rows
        table.querySelectorAll('tbody tr').forEach(row => {
            const cells = [];
            row.querySelectorAll('td').forEach(td => {
                cells.push(`"${td.textContent.trim().replace(/"/g, '""')}"`);
            });
            csv += cells.join(',') + '\n';
        });

        if (idx < tables.length - 1) {
            csv += '\n'; // Separator between tables
        }
    });

    // Trigger download
    const blob = new Blob([csv], { type: 'text/csv;charset=utf-8;' });
    const link = document.createElement('a');
    link.href = URL.createObjectURL(blob);
    link.download = `${filenamePrefix}_${new Date().toISOString().slice(0, 10)}.csv`;
    link.click();
    URL.revokeObjectURL(link.href);

    showToast(`Exported ${tables.length} table${tables.length !== 1 ? 's' : ''} to CSV.`, "success");
}


// ═══════════════════════════════════════════════
// DATE / TIME HELPERS
// ═══════════════════════════════════════════════

/**
 * Formats a date string for display.
 * @param {string} dateStr - ISO date string (YYYY-MM-DD)
 * @returns {string} Formatted date
 */
export function formatDate(dateStr) {
    try {
        const date = new Date(dateStr + 'T00:00:00');
        return date.toLocaleDateString('en-US', {
            weekday: 'short',
            year: 'numeric',
            month: 'short',
            day: 'numeric'
        });
    } catch {
        return dateStr;
    }
}

/**
 * Returns the current date in YYYY-MM-DD format.
 * @returns {string}
 */
export function todayISO() {
    return new Date().toISOString().slice(0, 10);
}
