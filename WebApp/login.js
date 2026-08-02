import { db } from "./firebase-config.js";
import { ref, get, update } from "https://www.gstatic.com/firebasejs/10.8.0/firebase-database.js";
import { showToast } from "./utils.js";

const form = document.getElementById("loginForm");
const submitBtn = document.getElementById("submitBtn");
const loginInput = document.getElementById("login");

// Restore remembered employee ID
const savedId = localStorage.getItem("sentinelai_emp_id");
if (savedId) {
    loginInput.value = savedId;
    const rememberBox = document.getElementById("remember");
    if (rememberBox) rememberBox.checked = true;
}

form.addEventListener("submit", validate);

async function validate() {
    const empId = loginInput.value.trim();
    const password = document.getElementById("Password").value;
    const remember = document.getElementById("remember")?.checked;

    if (!empId || !password) {
        showToast("Please enter both ID and Password.", "error");
        return;
    }

    // Disable button & show spinner
    submitBtn.disabled = true;
    const originalContent = submitBtn.innerHTML;
    submitBtn.innerHTML = `<span class="spinner"></span> <span>Verifying...</span>`;

    try {
        const adminRef = ref(db, "Admins");
        const snapshot = await get(adminRef);

        if (!snapshot.exists()) {
            showToast("No admin configurations found in database.", "error");
            return;
        }

        const staffs = snapshot.val();
        let found = false;

        for (const [key, staff] of Object.entries(staffs)) {
            if (staff.Empid == empId) {
                found = true;

                if (staff.Password == password) {
                    showToast("Access Granted. Redirecting...", "success");

                    // Save or clear remembered ID
                    if (remember) {
                        localStorage.setItem("sentinelai_emp_id", empId);
                    } else {
                        localStorage.removeItem("sentinelai_emp_id");
                    }

                    // Log login activity
                    const now = new Date();
                    const dateStr = now.toISOString().slice(0, 10);
                    const timeStr = now.toTimeString().slice(0, 8);

                    await update(ref(db, `Login Activity/${dateStr}`), {
                        [timeStr]: empId,
                    });

                    // Redirect to dashboard
                    setTimeout(() => {
                        window.location.href = "dashboard.html";
                    }, 800);
                    return;
                } else {
                    showToast("Wrong Password. Access Denied.", "error");
                    break;
                }
            }
        }

        if (!found) {
            showToast("Employee ID not found.", "error");
        }
    } catch (error) {
        console.error("Login verification failed:", error);
        showToast("Database connection error. Try again.", "error");
    } finally {
        submitBtn.disabled = false;
        submitBtn.innerHTML = originalContent;
    }
}
