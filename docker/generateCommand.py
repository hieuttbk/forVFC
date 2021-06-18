import sys
import os 
from time import sleep
from os import path 


commandFileName = str("command_"+sys.argv[1]+".txt")
	
commandFile = open(commandFileName, "a")

for arg in sys.argv[2:]:
    commandFile.write(arg + "\n")

commandFile.close()
command = "sudo cp " + commandFileName + " /home/pi/data/command"
rm = "sudo rm -f " + commandFileName
os.system(command) #copy file to command data folder
os.system("sudo cp " + commandFileName + " /home/pi/data")
os.system(rm) #remove existing file