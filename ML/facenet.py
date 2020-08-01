import numpy as np
import tensorflow as tf
import mtcnn
from PIL import Image
from mtcnn import MTCNN

def extract_face(filename,required_size=(160,160)):
  image = Image.open(filename)
  image = image.convert('RGB')
  pixels = np.asarray(image)
  # mean = np.mean(pixels)
  # std = np.std(pixels)
  # std_adj = np.maximum(std,1.0/np.sqrt(pixels.size))
  # y = np.multiply(np.subtract(pixels, mean), 1/std_adj)
  detector = MTCNN()
  results = detector.detect_faces(pixels)
  x1,y1,width,height = results[0]['box']
  x1,y1 = abs(x1),abs(y1)
  x2,y2 = x1+width,y1+height
  face = pixels[y1:y2,x1:x2]
  image = Image.fromarray(face)
  image = image.resize(required_size)
  face_array = np.asarray(image,dtype=np.uint8)
  face_array = np.reshape(face_array,(1,160,160,3))
  face_array = (face_array-128.0)/128.0
  return face_array
  
pixels = extract_face('/content/drive/My Drive/dataset_1/Keshav/IMG_8800.JPG')
  
def get_embedding(filename):
  face_array = extract_face(filename)
  interpretor = tf.lite.Interpreter(model_path="facenet.tflite")
  interpretor.allocate_tensors()
  input_details = interpretor.get_input_details()
  output_details = interpretor.get_output_details()
  input_data = face_array.astype(np.float32)
  interpretor.set_tensor(input_details[0]['index'],input_data)
  interpretor.invoke()
  output_data = interpretor.get_tensor(output_details[0]['index'])
  return output_data
  
embedding = get_embedding('/content/drive/My Drive/dataset_1/Keshav/IMG_8800.JPG')
embedding.shape
  
database = {}
database['Sushant'] = get_embedding('drive/My Drive/dataset_1/Sushant/IMG_20191223_201636_2.jpg')
database['Keshav'] = get_embedding('drive/My Drive/dataset_1/Keshav/IMG_8880.JPG')
database['Anand'] = get_embedding('anand.jpeg')
database['Ashish'] = get_embedding('ashish.jpeg')
database['Shah Rukh Khan'] = get_embedding('download.jpeg')
database['Nakul'] = get_embedding('/content/drive/My Drive/facenet mobile/dataset/nakul/nakul.jpeg')
database['Sudhanshu'] = get_embedding('/content/drive/My Drive/facenet mobile/dataset/sudhanshu/sudhanshu.jpeg')

def face_recognition(filename):
  embed = get_embedding(filename)
  min_dist = 100
  for (name,embedding) in database.items():
    dist = np.linalg.norm(np.subtract(embed,embedding))
    print(name,dist)
    if dist<min_dist:
      min_dist = dist
      identity = name
    # if min_dist>7:
    #   identity = "Not found"
  print(identity)
  
face_recognition('/content/drive/My Drive/facenet mobile/dataset/srk/stest_2.jpeg')
