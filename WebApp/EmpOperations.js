import { db } from "./firebase-config.js";
import { ref, set, remove, get, update } from "https://www.gstatic.com/firebasejs/10.8.0/firebase-database.js";
import { showToast } from "./utils.js";

// Hook event listeners
document.getElementById("addBtn").addEventListener("click", addEmpId);
document.getElementById("resetBtn").addEventListener("click", resetStatus);

async function resetStatus() {
    const empId = document.getElementById("empfield").value.trim();
    if (!empId) {
        showToast("Please enter an Employee ID.", "error");
        return;
    }

    try {
        const empIdRef = ref(db, `Empids/${empId}`);
        await set(empIdRef, 0);

        const staffRef = ref(db, `Staffs/${empId}`);
        await remove(staffRef);

        showToast(`Employee ID '${empId}' status reset successfully.`, "success");
    } catch (error) {
        console.error("Reset status error: ", error);
        showToast("Error resetting employee status.", "error");
    }
}

async function addEmpId() {
    const empId = document.getElementById("empfield").value.trim();
    if (!empId) {
        showToast("Please enter an Employee ID.", "error");
        return;
    }

    try {
        const empIdsRef = ref(db, "Empids");
        const snapshot = await get(empIdsRef);
        
        let currIds = {};
        if (snapshot.exists()) {
            currIds = snapshot.val();
        }

        // Optimize checking existence: O(1) key check instead of looping
        if (currIds && Object.prototype.hasOwnProperty.call(currIds, empId)) {
            showToast("Employee ID already exists.", "error");
        } else {
            const updates = { [empId]: 0 };
            await update(empIdsRef, updates);
            showToast(`Employee ID '${empId}' added successfully.`, "success");
        }
    } catch (error) {
        console.error("Add employee ID error: ", error);
        showToast("Database connection error.", "error");
    }
}