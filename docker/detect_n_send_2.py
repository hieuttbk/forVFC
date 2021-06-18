import time 
from time import sleep
import os 
from os import path
from multiprocessing import Process

import cv2
import numpy as np
import math 
from skimage import transform
from skimage import io
from keras.models import model_from_json
from keras.preprocessing.image import img_to_array
from keras.preprocessing.image import load_img

#import for sending http post
import requests as reqs
import sys
import json

# def detect_n_send(arg = []):
#load arguments
arg=sys.argv[1:]
print(arg)
csePoa = arg[0].strip()
if csePoa[-1] == '/':
    csePoa = csePoa[:-1]
cseId = arg[1].strip()
cseName = arg[2].strip()
print(arg[3].strip())
commandId = int(arg[3].strip())
serviceId = arg[4].strip()
startImage = int(arg[5].strip())
endImage = int(arg[6].strip())
Time = arg[7:]

cseIp = csePoa.strip("http://").split(":")[0]
#load model
json_file= open(str(os.getcwd()) +  '/MODEL/mAlexNetParking.json','r')
loaded_model_json= json_file.read()
json_file.close()
Loaded_Model = model_from_json(loaded_model_json)
#Load weights into new model
Loaded_Model.load_weights(str(os.getcwd()) + '/MODEL/mAlexNetParking.h5')
print("Loaded Model")
slotStatus = {}

#start timestamp
start_time = time.time()

for i in range(startImage, endImage + 1):
    Predict_Slot=[]
    images = []
    img = cv2.imread(str('/home/pi/data/cut_image/'+serviceId+'/image'+str(i)+ '.jpg'))
    if img is None:
        raise Exception("Image does not exist")
    images.append(transform.resize(img, (150,150,3)))
    images = np.array(images)


    if(Loaded_Model.predict(images)[0][0]>0.8):
        Predict_Slot.append(1)
    #print("BusySlot")#%s" % (images))
        #filename_array += ["BusySlot: %s" % (filename)]
        slotStatus['slotNumber'+str(i)] = 'Busy'

    else:
        Predict_Slot.append(0)
    #print("FreeSlot")# %s" % (images))
        slotStatus['slotNumber'+str(i)] = 'Free'

    #os.system('rm -f ' + '/usr/share/data/cut_image/image'+str(i)+ '_' + serviceId + '.jpg')
#end timestamp
deltaT5 = time.time() - start_time
print('Tproc =', deltaT5)
deltaT5 = '{:.2f}'.format(deltaT5*1000)
TIME = {}
t = 1
for i in Time:
    TIME['deltaT'+str(t)] = i.strip()
    t+=1
TIME['deltaT'+str(t)] = str(deltaT5) #process time 
x = json.dumps(TIME)
y = json.dumps(slotStatus)

resultFileName = str("/home/pi/data/docker/result.txt")
resultFile = open(resultFileName,"a")
resultFile.write(str(endImage) + "\t" + deltaT5 + "\n")

content = [
                { 'SERVICEID':serviceId },
                { 'SERVICE': "DETECT"},
                { 'COMMANDID':commandId },
                { 'DTSOURCE' : cseIp},
                { 'TIME':json.loads(x) },
                { 'SLOTSTATUS':json.loads(y) }

        ]

data = {
    'm2m:cin':{
        'rn':'service_result' + str(time.time()),
        'cnf':'application/text',
        'con': str(content)
                
    }
}

response = reqs.post(csePoa+'/~/'+cseId+'/'+cseName+'/'+'RESULT', data = json.dumps(data),
                    headers = {
                        'X-M2M-Origin':'admin:admin',
                        'Accept':'application/json',
                        'Content-Type':'application/json;ty=4'
                    })
if response.status_code == 201:
    print('result sent!')
else:
    raise Exception("Error sending result")

#Deleting pulled data
os.system("rm -rf " + "/home/pi/data/cut_image/"+serviceId)
os.system("rm -f " + "/home/pi/data/cut_image/image"+str(startImage)+"-"+str(endImage)+".zip")


# def main():
# commandDetectPath = "/home/pi/data/command/command_Detect.txt"
# print("Running")
# while True:
    # if path.exists(commandDetectPath):
        # print("found command")
        # command = open(commandDetectPath,"r")
        # lines = command.readlines()
        # command.close()
        # os.system("rm -f " + commandDetectPath)
        # try:
            # newProcess = Process(target=detect_n_send, args=(lines,))
            # newProcess.start()
        # except Exception as e:
            # print(str(e))
        
    
    # sleep(0.1)
    



# if __name__=="__main__":
# main()