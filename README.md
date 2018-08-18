# Seam-Carver
## A program for image content-aware resizing using the seam carving algorithm. 

Main Features:
- Resize images with regard to the content in the image. This will minimize the noticability of stretching or squeezing.
- Remove content from images. This can be done by biasing certain pixels so that they are removed while shrinking, and then expanding the image back to its original size.

Comes with a basic JavaFX GUI for using the algorithm.

Original image:
![alt text](https://user-images.githubusercontent.com/5619132/44302534-cd9ce200-a2f8-11e8-9689-9bcd55635749.png)

Shrinked:
![alt text](https://user-images.githubusercontent.com/5619132/44302537-cfff3c00-a2f8-11e8-88e7-b1176498b116.png)

Expanded:
![alt text](https://user-images.githubusercontent.com/5619132/44302538-d55c8680-a2f8-11e8-9a3b-54ece46caeb4.png)

Area of image highlighted for removal:
![alt text](https://user-images.githubusercontent.com/5619132/44302530-b827b800-a2f8-11e8-9f9e-965769e126de.png)

Highlighted area removed:
![alt text](https://user-images.githubusercontent.com/5619132/44302529-b52cc780-a2f8-11e8-835b-bc6fbf98edc5.png)


Credit: http://www.faculty.idc.ac.il/arik/SCWeb/imret/imret.pdf
