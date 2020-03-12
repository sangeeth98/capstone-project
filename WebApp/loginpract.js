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
var ref = database.ref("Admins");
var test;

function validate() {
	ref.on("value", goData, errData);
}

function goData(data) {
	var login = document.getElementById("login").value;
	var pass = document.getElementById("Password").value;
	var staffs = data.val();
	var keys = Object.keys(staffs);
	console.log(keys);
	var check = true;
	for (var i = 0; i < keys.length; i++) {
		var k = keys[i];
		var Empid = staffs[k].Empid;
		if (Empid == login) {
			check = false;
			var Password = staffs[k].Password;
			if (Password == pass) {
				var today = new Date();
				var date =
					today.getFullYear() +
					"-" +
					(today.getMonth() + 1) +
					"-" +
					today.getDate();
				var time =
					today.getHours() +
					":" +
					today.getMinutes() +
					":" +
					today.getSeconds();
				console.log(date + " " + time);
				var ref2 = database.ref("Login Activity/" + date);
				var data = {
					[time]: login
				};
				ref2.update(data);
				window.open("connectiontofirebase.html", "_self");
			} else {
				alert("Wrong Password");
			}
		}
		console.log(Password + "\n" + Empid);
	}
	if (check) {
		alert("Wrong Empid");
	}
}

function errData() {
	alert("Error");
}
