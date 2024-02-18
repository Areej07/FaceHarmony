import requests

# URL of the Flask API endpoint
api_url = 'http://localhost:5000/recognize'

# Path to the image file you want to recognize
image_file = 'image_to_recognize.jpg'

# Send a POST request to the Flask API endpoint with the image file
with open(image_file, 'rb') as file:
    files = {'image': file}
    response = requests.post(api_url, files=files)

# Check if the request was successful
if response.status_code == 200:
    result = response.json()['result']
    print('Face recognition result:', result)
else:
    print('Error:', response.text)
