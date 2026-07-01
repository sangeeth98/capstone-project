/**
 * Displays a non-intrusive custom toast notification in the UI.
 * @param {string} message The message content
 * @param {'success' | 'error' | 'info'} type The type of notification
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
    
    let icon = "🔔";
    if (type === 'success') icon = "✨";
    else if (type === 'error') icon = "⚠️";
    else if (type === 'info') icon = "ℹ️";
    
    toast.innerHTML = `
        <span class="toast-icon">${icon}</span>
        <span class="toast-message">${message}</span>
    `;
    
    container.appendChild(toast);
    
    // Auto remove after 4.5 seconds
    setTimeout(() => {
        toast.classList.add('fade-out');
        setTimeout(() => {
            toast.remove();
        }, 300);
    }, 4500);
}
