from flask import Flask, request, jsonify
import face_recognition
import numpy as np
import cv2
import os

app = Flask(__name__)
UPLOAD_FOLDER = 'static/uploads'
ALLOWED_EXTENSIONS = {'png', 'jpg', 'jpeg', 'gif'}
app.config['UPLOAD_FOLDER'] = UPLOAD_FOLDER
# Load the known face image and encode it
known_image = face_recognition.load_image_file(r"E:\chrome download\facemodel\gface\image\001_fe3347c0.jpg")
known_encoding = face_recognition.face_encodings(known_image)[0]

def allowed_file(filename):
    return '.' in filename and filename.rsplit('.', 1)[1].lower() in ALLOWED_EXTENSIONS

@app.route('/recognize', methods=['POST'])
def recognize_faces():
    # Check if files are in the request
    if 'images' not in request.files:
        return jsonify({'error': 'No images uploaded'})
    
    # Get the uploaded files
    files = request.files.getlist('images')
    
    results = []
    
    for file in files:
        if file and allowed_file(file.filename):
            filename = os.path.join(app.config['UPLOAD_FOLDER'], file.filename)
            
            image_data = file.read()
            file.save(filename)
            # Convert image data into numpy array
            nparr = np.frombuffer(image_data, np.uint8)
            image = cv2.imdecode(nparr, cv2.IMREAD_COLOR)
            # Find faces in the image
            face_locations = face_recognition.face_locations(image)
            face_encodings = face_recognition.face_encodings(image, face_locations)
            if len(face_encodings) > 0:
                
                matches = face_recognition.compare_faces([known_encoding], face_encodings[0])
                match = "Match" if matches[0] else "No match"
                results.append({'filename': file.filename, 'result': match})
            else:
                results.append({'filename': file.filename, 'result': 'No face detected'})
            
  
    return jsonify({'results': results})


if __name__ == '__main__':
    app.run(debug=True)
