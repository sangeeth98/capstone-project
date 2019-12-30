from firebase import firebase

firebase=firebase.FirebaseApplication('https://mypro-b78bd.firebaseio.com/', None)
result=firebase.get('/Attendence',None)
print(result)
