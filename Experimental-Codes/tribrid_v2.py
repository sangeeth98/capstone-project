# new tribrid file
import dlib
import face_recognition
import cv2, pickle
from firebase import firebase
import time, copy, datetime
import numpy as np
from imutils.video import FPS
# use Ctrl+K+C & Ctrl+K+U to comment and uncomment

# initialize firebase url
firebase = firebase.FirebaseApplication('https://capstone-prototype-7b1f9.firebaseio.com/', None)
# serialized facial encodings
data = pickle.loads(open("encodings.pickle","rb").read())
detector = dlib.get_frontal_face_detector() # face detector
sdThresh = 8                                # thresh for standard deviation 
font = cv2.FONT_HERSHEY_SIMPLEX             # cv2 font general

# distMap returns pythogorean distance between two frames
def distMap(frame1, frame2):
    diff32 = np.float32(frame1) - np.float32(frame2)
    return np.uint8((np.sqrt(diff32[:,:,0]**2 + diff32[:,:,1]**2 + diff32[:,:,2]**2)/441.6729559300637)*255)

cap = cv2.VideoCapture(0)                   # general videocapture from default camera
_, frame1 = cap.read()                      # capturing first frame
_, frame2 = cap.read()                      # capturing second frame

time1 = time.time()
activity_count = 0

fps = FPS().start()
# Main Loop
while(True):
    #TODO: Activity Monitoring
    _, frame = cap.read()                           # capture image
    rows, cols, _ = np.shape(frame)                 # get length & width of image
    # cv2.imshow('dist', frame)                     # display normal frame
    dist = distMap(frame1, frame)                   # compute pythogorean distance
    frame1 = frame2                                 # reassign x[-2] frame
    frame2 = frame                                  # reassign x[-1] frame
    mod = cv2.GaussianBlur(dist, (9,9), 0)          # Apply gaussian smoothing
    _, thresh = cv2.threshold(mod, 100, 255, 0)     # Thresholding
    _, stDev = cv2.meanStdDev(mod)                  # calculate std deviation test
    # cv2.imshow('dist', mod)                 
    # cv2.putText(frame2, "Standard Deviation - {}".format(round(stDev[0][0],0)), (70, 70), font, 1, (255, 0, 255), 1, cv2.LINE_AA)
    if stDev > sdThresh: activity_count+=1          # computing activity intensity
    if(time.time()-time1>=5):
            time1 = time.time()
            nowtime = datetime.datetime.now().strftime("%Y-%m-%d %H:%M:%S")
            firebase.patch('/Motion Detection/',{nowtime:activity_count})
            activity_count=0

    #TODO: Facial Recognition
    gray = cv2.cvtColor(frame, cv2.COLOR_BGR2GRAY)  # grayscale image
    rgb = cv2.cvtColor(frame, cv2.COLOR_BGR2RGB)    # rgb image

    dets = detector(rgb, 1)
    boxes = [(d.left(), d.top(), d.right(), d.bottom()) for i,d in enumerate(dets)]    # get tuple of box coordinates
    encodings = face_recognition.face_encodings(rgb, boxes)     # encode those faces from rgb
    
    names=[]
    # Loop over facial embeddings and check if faces match
    for encoding in encodings:
        matches = face_recognition.compare_faces(data["encodings"], encoding)
        name = "Unknown"
        if True in matches:
            matchedIdxs = [i for (i, b) in enumerate(matches) if b]
            counts = {}

            for i in matchedIdxs:
                name = data["names"][i]
                counts[name] = counts.get(name, 0) + 1
            name = max(counts, key=counts.get)

        # patching data to firebase console
        x=datetime.datetime.now().strftime("%H:%M:%S")
        y=datetime.datetime.now().strftime("%Y-%m-%d")
        firebase.patch('/Monitoring/'+name+'/'+y+'/',{x:"At camera 1"})

		
    # draw the predicted face name on the image
    frame3=copy.copy(frame)
    for ((top, right, bottom, left), name) in zip(boxes, names):
        cv2.rectangle(frame3, (left, top), (right, bottom),
            (0, 255, 0), 2)
        y = top - 15 if top - 15 > 15 else top + 15
        cv2.putText(frame3, name, (left, y), font,
            0.75, (0, 255, 0), 2)
    cv2.imshow("frame",frame3)

    #TODO: object detection
    # gun = gun_detector.detectMultiScale(gray, 1.3, 5, minSize = (100, 100))
    # if len(gun) > 0:
    #     nowtime = datetime.datetime.now().strftime("%Y-%m-%d %H:%M:%S")
    #     firebase.patch("/Object Detection/",{nowtime:"gun detected at camera 1"})

    if cv2.waitKey(1) & 0xFF == 27: break       # break if esc is pressed
cap.release()
cv2.destroyAllWindows()

# firebase dependencies
# python_jwt gcloud sseclient requests_toolbelt