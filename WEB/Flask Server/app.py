# import dotenv
import os, io, sys
from flask import Flask, render_template, request, jsonify
from PIL import Image
import numpy as np
import base64
import re
from io import BytesIO
from io import StringIO
# import edScript2
from flask_cors import CORS

# dotenv.load_dotenv()

# CK = os.getenv('CONSUMER_KEY')


app = Flask(__name__)
CORS(app)

# @app.route('/emotify',methods = ['POST'])
# def get_image():
#     print("Reached here in flask2")
#     image_b64 = request.values['imageBase64']
#     image_data = re.sub('^data:image/.+;base64,', '', image_b64)
#     image_PIL = Image.open(BytesIO(base64.b64decode(image_data)))

#     #just for checking if the image is received.
#     image_np = np.array(image_PIL)
#     print(f'Image received in the dest route: {image_np.shape}')

#     emotion = edScript2.detect_emotion(image_PIL)
#     return jsonify({'status':'status ok please','emotion':emotion})



@app.route('/')
def home():
    return render_template('index.html')

@app.after_request
def after_request(response):
    print("log: setting cors" , file = sys.stderr)
    response.headers.add('Access-Control-Allow-Origin', '*')
    response.headers.add('Access-Control-Allow-Headers', 'Content-Type,Authorization')
    response.headers.add('Access-Control-Allow-Methods', 'GET,PUT,POST,DELETE')
    return response

if (__name__ == "__main__"):
    app.run(threaded=False,port=5010)