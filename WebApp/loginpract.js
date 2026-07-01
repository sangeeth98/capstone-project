import { db } from "./firebase-config.js";
import { ref, get, update } from "https://www.gstatic.com/firebasejs/10.8.0/firebase-database.js";
import { showToast } from "./utils.js";

document.getElementById("loginForm").addEventListener("submit", validate);

async function validate() {
    const submitBtn = document.getElementById("submitBtn");
    const loginInput = document.getElementById("login").value.trim();
    const passInput = document.getElementById("Password").value;

    if (!loginInput || !passInput) {
        showToast("Please enter both ID and Password.", "error");
        return;
    }

    // Disable button & show spinner state
    submitBtn.disabled = true;
    const originalContent = submitBtn.innerHTML;
    submitBtn.innerHTML = `<span class="spinner"></span> <span>Verifying...</span>`;

    try {
        const adminRef = ref(db, "Admins");
        const snapshot = await get(adminRef);

        if (!snapshot.exists()) {
            showToast("No admin configurations found in database.", "error");
            submitBtn.disabled = false;
            submitBtn.innerHTML = originalContent;
            return;
        }

        const staffs = snapshot.val();
        const keys = Object.keys(staffs);
        let found = false;

        for (let i = 0; i < keys.length; i++) {
            const key = keys[i];
            const empId = staffs[key].Empid;

            if (empId == loginInput) {
                found = true;
                const correctPassword = staffs[key].Password;

                if (correctPassword == passInput) {
                    showToast("Access Granted. Redirecting...", "success");

                    // Create structured date & time strings
                    const today = new Date();
                    const month = String(today.getMonth() + 1).padStart(2, '0');
                    const day = String(today.getDate()).padStart(2, '0');
                    const dateStr = `${today.getFullYear()}-${month}-${day}`;

                    const hours = String(today.getHours()).padStart(2, '0');
                    const minutes = String(today.getMinutes()).padStart(2, '0');
                    const seconds = String(today.getSeconds()).padStart(2, '0');
                    const timeStr = `${hours}:${minutes}:${seconds}`;

                    // Update Login Activity in Firebase
                    const logRef = ref(db, `Login Activity/${dateStr}`);
                    await update(logRef, {
                        [timeStr]: loginInput
                    });

                    // Redirect to Control Panel
                    setTimeout(() => {
                        window.open("connectiontofirebase.html", "_self");
                    }, 1000);
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
        console.error("Login verification failed: ", error);
        showToast("Database connection error. Try again.", "error");
    } finally {
        submitBtn.disabled = false;
        submitBtn.innerHTML = originalContent;
    }
}
