import { initializeApp } from "https://www.gstatic.com/firebasejs/10.8.0/firebase-app.js";
import { getDatabase } from "https://www.gstatic.com/firebasejs/10.8.0/firebase-database.js";

// Your web app's Firebase configuration
const firebaseConfig = {
    apiKey: "AIzaSyBkNkpSNjI4w_v6Ue0M34KSl8Spp_2KuUw",
    authDomain: "capstone-prototype-7b1f9.firebaseapp.com",
    databaseURL: "https://capstone-prototype-7b1f9.firebaseio.com",
    projectId: "capstone-prototype-7b1f9",
    storageBucket: "capstone-prototype-7b1f9.appspot.com",
    messagingSenderId: "84439194622",
    appId: "1:84439194622:web:78f3659951bd9e6e123eaf",
    measurementId: "G-70RLHN723Z"
};

// Initialize Firebase
const app = initializeApp(firebaseConfig);
const db = getDatabase(app);

export { app, db };
