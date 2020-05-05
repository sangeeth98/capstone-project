import dlib
import cv2
import matplotlib.pyplot as plt

from skimage.feature import hog
from skimage import data, exposure
detector = dlib.get_frontal_face_detector()  # face detector
cap = cv2.VideoCapture(0)
_, frame = cap.read()
cap.release()

gray = cv2.cvtColor(frame, cv2.COLOR_BGR2GRAY)  # grayscale image
rgb = cv2.cvtColor(frame, cv2.COLOR_BGR2RGB)    # rgb image

image = rgb

fd, hog_image = hog(image, orientations=8, pixels_per_cell=(16, 16),
                    cells_per_block=(1, 1), visualize=True, multichannel=True)

fig, (ax1, ax2) = plt.subplots(1, 2, figsize=(
    8, 4), sharex=True, sharey=True)

ax1.axis('off')
ax1.imshow(image, cmap=plt.cm.gray)
ax1.set_title('Input image')

# Rescale histogram for better display
hog_image_rescaled = exposure.rescale_intensity(hog_image, in_range=(0, 10))

ax2.axis('off')
ax2.imshow(hog_image_rescaled, cmap=plt.cm.gray)
ax2.set_title('Histogram of Oriented Gradients')
plt.show()
"""
laplacian = cv2.Laplacian(rgb,cv2.CV_64F)
sobelx = cv2.Sobel(rgb,cv2.CV_64F,1,0,ksize=5)
sobely = cv2.Sobel(rgb,cv2.CV_64F,0,1,ksize=5)

plt.subplot(2,2,1),plt.imshow(rgb,cmap = 'gray')
plt.title('Original'), plt.xticks([]), plt.yticks([])
plt.subplot(2,2,2),plt.imshow(laplacian,cmap = 'gray')
plt.title('Laplacian'), plt.xticks([]), plt.yticks([])
plt.subplot(2,2,3),plt.imshow(sobelx,cmap = 'gray')
plt.title('Sobel X'), plt.xticks([]), plt.yticks([])
plt.subplot(2,2,4),plt.imshow(sobely,cmap = 'gray')
plt.title('Sobel Y'), plt.xticks([]), plt.yticks([])

plt.show()
"""
