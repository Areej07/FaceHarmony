from flask import Flask, request, jsonify
import face_recognition
import numpy as np
import cv2

app = Flask(__name__)

# Load the known face image and encode it
known_image = face_recognition.load_image_file("E:\chrome download\facemodel\gface\image\001_fe3347c0.jpg")
known_encoding = face_recognition.face_encodings(known_image)[0]

@app.route('/recognize', methods=['POST'])
def recognize_faces():
    # Receive image data from the client
    image_data = request.files['image'].read()
    
    # Convert image data into numpy array
    nparr = np.frombuffer(image_data, np.uint8)
    image = cv2.imdecode(nparr, cv2.IMREAD_COLOR)
    
    # Find faces in the image
    face_locations = face_recognition.face_locations(image)
    face_encodings = face_recognition.face_encodings(image, face_locations)

    # Compare each face found in the image with the known face
    matches = face_recognition.compare_faces([known_encoding], face_encodings[0])
    match = "Match" if matches[0] else "No match"
    
    return jsonify({'result': match})

if __name__ == '__main__':
    app.run(debug=True)
