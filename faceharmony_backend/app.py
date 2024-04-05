from datetime import datetime
from flask import Flask, request, jsonify
from cluster_algo import get_the_cluster_image
import os

app = Flask(__name__)
UPLOAD_FOLDER = 'static/uploads'
ALLOWED_EXTENSIONS = {'png', 'jpg', 'jpeg', 'gif'}
app.config['UPLOAD_FOLDER'] = UPLOAD_FOLDER



def allowed_file(filename):
    return '.' in filename and filename.rsplit('.', 1)[1].lower() in ALLOWED_EXTENSIONS


def get_date_time_for_naming():
    (dt, micro) = datetime.utcnow().strftime('%Y%m%d%H%M%S.%f').split('.')
    dt_dateTime = "%s%03d" % (dt, int(micro) / 1000)
    return dt_dateTime


@app.route('/recognize', methods=['POST'])
def recognize_faces():
    # Check if files are in the request
    if 'images' not in request.files:
        return jsonify({'error': 'No images uploaded'})

    # Get the uploaded files
    files = request.files.getlist('images')

    # results = []
    folder_name = get_date_time_for_naming()

    folder_dir = os.path.join(app.config['UPLOAD_FOLDER'], folder_name)
    os.makedirs(folder_dir,exist_ok=True)
    images_path = os.path.join(folder_dir,'images')
    os.makedirs(images_path,exist_ok=True)
    for file in files:
        if file and allowed_file(file.filename):
            filename = os.path.join(images_path, file.filename)
            file.save(filename)
    no_of_cluster,result_folder_path=get_the_cluster_image(images_path,folder_dir)
    context = {
        "status":True,
        "Number of different faces":no_of_cluster,
        "cluster_images_folder_path": f"{result_folder_path}"
    }
    return jsonify(context)


if __name__ == '__main__':
    app.run(debug=True)
