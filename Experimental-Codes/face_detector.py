import cv2, dlib, pickle
import pickle

cap = cv2.VideoCapture(0)
detector = dlib.get_frontal_face_detector()
while(True):
    _, frame = cap.read()
    rgb = cv2.cvtColor(frame, cv2.COLOR_BGR2RGB)
    dets = detector(rgb, 1)
    for i, d in enumerate(dets):
        cv2.rectangle(frame, (d.left(), d.top()), (d.right(), d.bottom()), (0, 255, 0), 2)
    cv2.imshow('Frame', frame)

    if cv2.waitKey(1) & 0xFF == 27: break 

cv2.destroyAllWindows()
