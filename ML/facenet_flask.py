def get_embedding(Pillow):
    face_array = face_array = np.asarray(Pillow,dtype=np.uint8)
    face_array = np.reshape(face_array,(1,160,160,3))
    face_array = (face_array-128.0)/128.0
    interpretor = tf.lite.Interpreter(model_path="/content/drive/My Drive/facenet mobile/facenet.tflite")
    interpretor.allocate_tensors()
    input_details = interpretor.get_input_details()
    output_details = interpretor.get_output_details()
    input_data = face_array.astype(np.float32)
    interpretor.set_tensor(input_details[0]['index'],input_data)
    interpretor.invoke()
    output_data = interpretor.get_tensor(output_details[0]['index'])
    return output_data