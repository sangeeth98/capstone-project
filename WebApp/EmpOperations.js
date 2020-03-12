// Your web app's Firebase configuration
var firebaseConfig = {
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
firebase.initializeApp(firebaseConfig);
var database = firebase.database();

function ResetStatus() {
    var empid = document.getElementById("empfield").value;
    var ref = database.ref("Empids/" + empid);
    ref.set(0);
    var refdel = database.ref("Staffs/" + empid);
    refdel.remove();
}

function AddEmpid() {
    var ref = database.ref("Empids");
    ref.once("value", addtodb, errordb);
}

function addtodb(data) {
    var empid = document.getElementById("empfield").value;
    var ref = database.ref("Empids");
    var currids = data.val();
    var keys = Object.keys(currids);
    var count = 0;
    for (var i = 0; i < keys.length; i++) {
        if (empid != keys[i]) {
            count += 1;
            console.log(count);
        }
    }
    if (count == keys.length) {
        var data = {
            [empid]: 0
        };
        ref.update(data);
    } else {
        alert("Employee ID already Exists");
    }
}

function errordb() {
    alert("Error connecting to database");
}