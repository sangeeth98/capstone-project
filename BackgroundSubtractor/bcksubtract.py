import numpy as np
import cv2
from imutils.video import VideoStream

# cap = cv2.VideoCapture('vtest.avi')
cap = VideoStream(src=0).start()

fgbg = cv2.createBackgroundSubtractorMOG2()

while(1):
    frame = cap.read()

    fgmask = fgbg.apply(frame)

    cv2.imshow('frame',fgmask)
    k = cv2.waitKey(30) & 0xff
    if k == 27:
        break

# cap.release()
cap.stop()
cv2.destroyAllWindows()