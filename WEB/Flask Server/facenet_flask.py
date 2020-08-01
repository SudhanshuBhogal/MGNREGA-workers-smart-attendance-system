from PIL import Image
import numpy as np
from mtcnn import MTCNN
import tensorflow as tf

def get_embedding(pillow):
    image = pillow.convert('RGB')
    pixels = np.asarray(image)
    detector = MTCNN()
    results = detector.detect_faces(pixels)
    x1, y1, width, height = results[0]['box']
    x1, y1 = abs(x1), abs(y1)
    x2, y2 = x1 + width, y1 + height
    face = pixels[y1:y2, x1:x2]
    image = Image.fromarray(face)
    image = image.resize((160,160))
    face_array = np.asarray(image, dtype=np.uint8)
    face_array = np.reshape(face_array, (1, 160, 160, 3))
    face_array = (face_array - 128.0) / 128.0
    interpretor = tf.lite.Interpreter(model_path="./facenet.tflite")
    interpretor.allocate_tensors()
    input_details = interpretor.get_input_details()
    output_details = interpretor.get_output_details()
    input_data = face_array.astype(np.float32)
    interpretor.set_tensor(input_details[0]['index'],input_data)
    interpretor.invoke()
    output_data = interpretor.get_tensor(output_details[0]['index'])
    return output_data

if (__name__ == "__main__"):
    print("got here!")
    image = Image.open('./123.png')
    print(get_embedding(image))
