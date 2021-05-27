import requests as reqs
from time import sleep
import uuid 
import sys
import json
import random 
from time import sleep 

poisson = [  #Poisson timestamp (seconds)
            1.7853773096121572,
            1.050623355012524,
            7.367115206246219,
            10.195900318435996,
            0.1313203360796599,
            2.7995622344136493,
            15.639586567653367,
            0.43695708008420003,
            15.437156440709,
            3.001443879092571,
            0.09138390104637,
            1.3527166694378343,
            0.18049067860751916,
            0.9383143769068678,
            2.8481780925183426,
            7.851887282102183,
            9.484690538045259,
            3.3560872640076207,
            14.387048076824176,
            5.414875000954557,
            12.734460659042162,
            0.26670062905455644,
            14.731176090507553,
            10.638588631994596,
            8.032846851528877,
            0.7359280527457238,
            4.441829629258449,
            4.262709230923075,
            4.613727612112144,
            0.15718310940266672,
            1.7407434163154107,
            4.133616696084268,
            9.556337011142991,
            1.0605586791004407,
            16.86386642798054,
            14.902802503596906,
            0.6451004315854716,
            3.828908118833776,
            4.889548857079212,
            13.895049938680426,
            4.528981376306326,
            14.085945914697072,
            1.3516982908345552,
            5.235503867328259,
            7.119287000111961,
            4.030056872134728,
            21.412470979390914,
            7.106479349323326,
            0.1296200597961048,
            6.068743469062804 ]

#request sender function
def requestSender(workLoad, desWorker, desWorker_IP, desWorkerPort, number_request):
    print("Sending request to " + desWorker + " with WorkLoad: " + str(workLoad))
    #sleep(5)
    csePoa = "http://" + desWorker_IP + ":8181" 
    cseName = desWorker
    cseId = desWorker + "-id"
    x = 0
    while(x < int(number_request)):
        serviceId = str(uuid.uuid4())
        content = [
                    { 'SERVICE': "DetectImage"},
                    { 'SERVICEID':serviceId },
                    { 'NOAWORKER':0 },
                    { 'DTSOURCE' : desWorker_IP },
                    { 'WORKLOAD': workLoad },
                    { 'NSERVICE' : x}

                    ]

        data = {
        'm2m:cin':{
            'rn':'service' + serviceId,
            'cnf':'application/text',
            'con': str(content)
                      
            }
        }
        print(x, " Destination: " + csePoa+'/~/'+cseId+'/'+cseName+'/'+'SERVICE')
    
        try:
            response = reqs.post(csePoa+'/~/'+cseId+'/'+cseName+'/'+'SERVICE', data = json.dumps(data),
                            headers = {
                                'X-M2M-Origin':'admin:admin',
                                'Accept':'application/json',
                                'Content-Type':'application/json;ty=4'
                            })
        except Exception as e:
            raise Exception(str(e))
            print("Error sending request...")
        sleep(10)
        x+=1
        
    
        

#main

def main():
    option = 1
    while(1):
        print("*********************************************")
        print("***************REQUEST SENDER****************")
        try:
            print("Fixed Time or Poisson?")
            print("1 for Fixed Time, 2 for Poisson")
            option = int(input("Option: "))
            if(option == 1):
            
                workLoad = int(input("Number of workload: "))
                desWorker = 'worker-1'
                desWorker_IP = '127.0.0.1'
                desWorkerPort = '8181'
                number_Request = input("Number of request: ")
            else:   
                workLoad = int(input("Number of workload: "))
                desWorker = 'worker-1'
                desWorker_IP = '127.0.0.1'
                desWorkerPort = '8181'
                number_Request = len(poisson)
        except Exception as e:
            print("Wrong type of data")
            print(str(e))
            
        try:
            requestSender(workLoad = workLoad, desWorker = desWorker, desWorker_IP = desWorker_IP, desWorkerPort = desWorkerPort, number_request = number_Request)
        except Exception as e:
            print(str(e))
        


if __name__ == '__main__':
    main()