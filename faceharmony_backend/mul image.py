from flask import Flask, request, jsonify
import face_recognition
from PIL import Image
import numpy as np

app = Flask(__name__)

@app.route('/recognize_faces', methods=['POST'])
def recognize_faces():
    # Check if files are in the request
    if 'images[]' not in request.files:
        return jsonify({'error': 'No images uploaded'})

    # Get the uploaded files
    files = request.files.getlist('images[]')

    results = []

    for file in files:
        try:
            # Read the image file
            img = Image.open(file)

            # Convert image to RGB (face_recognition requires RGB images)
            img = img.convert('RGB')

            # Convert image to numpy array
            img_np = np.array(img)

            # Find face locations in the image
            face_locations = face_recognition.face_locations(img_np)

            # Count the number of faces found
            num_faces = len(face_locations)

            # Append the result to the results list
            results.append({'filename': file.filename, 'num_faces': num_faces})
        except Exception as e:
            results.append({'filename': file.filename, 'error': str(e)})

    return jsonify({'results': results})

if __name__ == '__main__':
    app.run(debug=True)
