#!/usr/bin/python
import socket
import time
import _thread
import datetime
import Adafruit_DHT

#connection data
Server = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)      
Conor = "192.168.1.6"
Jason = "192.168.1.46"

#devices and values
MainHeatStatus = 0
BedHeatStatus = 0
BlanketStatus = 0
LightStatus = 0
MainTemp = 0
MainHumid = 0
BedTemp = 0
BedHumid = 0

def listen():

    #bind it to port 9998 and accept data from all IP addresses
    Server.bind(("0.0.0.0",9998))
    
    #create a loop
    while True:
        
        #try to get data from the socket
        data = repr(Server.recvfrom(3))
        
        #check the message recieved
        if data[3] == '1':
            togglemain()
        if data[3] == '2':
            togglebed()
        if data[3] == '3':
            toggleblanket()
        if data[3] == '4':
            togglelight()
        
        #update devices after a status change  
        update()
        
def getMainSensor():
    
    global MainTemp
    global MainHumid
    #temp values for tracking updates
    CurrentMainTemp = MainTemp
    CurrentMainHumid = MainHumid
    
    while True:
        
        #attempt to get data from the sensor module
        h, t = Adafruit_DHT.read_retry(Adafruit_DHT.DHT11,17)
        #convert to integers
        temp = int(t)
        humid = int(h)
        
        #checks if humid is between 0 and 99 for the first reading taken.
        #for every other reading check if the value is also between 0 and 99, check if the value has changed, and had not fluctuated more than 25% indicating a sensor error
        if humid >= 0 and humid < 100 and CurrentMainHumid == 0 or humid >= 0 and humid < 100 and humid != CurrentMainHumid and humid < CurrentMainHumid*1.25 and humid > CurrentMainHumid*0.75:
             
            #update relevent values 
            MainHumid = humid
            CurrentMainHumid = humid
            #print status message indicating an update
            print("Living Room Humidity at " + str(humid) + "%")
            #trigger the update
            update()
         
        #checks if temp is between 0 and 99 for the first reading taken.
        #for every other reading check if the value is also between 0 and 99, check if the value has changed, and had not fluctuated more than 25% indicating a sensor error
        if temp >= 0 and temp < 100 and temp != CurrentMainTemp and CurrentMainTemp == 0 or temp >= 0 and temp < 100 and temp != CurrentMainTemp and temp < CurrentMainTemp*1.25 and temp > CurrentMainTemp*0.75:

            #update relevent values 
            MainTemp = temp
            CurrentMainTemp = temp
            #print status message indicating an update
            print("Living Room is " + str(temp) + " °C")
            update()

#start listening and sensor threads
_thread.start_new_thread(listen, ())
_thread.start_new_thread(getMainSensor, ())
#startup complete status message
print ("Online at",datetime.datetime.now().strftime("%I:%M:%S %p"))

def update():
    
    global BedHeatStatus
    global MainHeatStatus
    global BlanketStatus
    global LightStatus
    global MainTemp 
    global MainHumid
    global BedTemp
    global BedHumid
    
    #format a string containing all data, seperated by ,
    Status = str(MainHeatStatus) +  ',' + str(BedHeatStatus) + ',' + str(BlanketStatus) + ',' + str(LightStatus) + ',' + str(MainTemp).zfill(2) + ',' + str(MainHumid).zfill(2)  + ',' + str(BedTemp).zfill(2)  + ',' + str(BedHumid).zfill(2) 
    
    #Send to both devices
    Server.sendto(Status.encode(),(Conor,9999))
    Server.sendto(Status.encode(),(Jason,9999))

def togglemain():
    
    global MainHeatStatus
    
    if MainHeatStatus == 1:
        MainHeatStatus = 0
        print("Switched off Living room heater at",datetime.datetime.now().strftime("%I:%M:%S %p"))
    elif MainHeatStatus == 0:
        MainHeatStatus = 1
        print("Switched on Living room heater at",datetime.datetime.now().strftime("%I:%M:%S %p"))
        
def togglebed(): 
    
    global BedHeatStatus
    if BedHeatStatus == 1:
        BedHeatStatus = 0
        print("Switched off Bedroom heater at",datetime.datetime.now().strftime("%I:%M:%S %p"))
    elif BedHeatStatus == 0:
        BedHeatStatus = 1
        print("Switched on Bedroom heater at",datetime.datetime.now().strftime("%I:%M:%S %p"))
        
def toggleblanket():
    
    global BlanketStatus
    if BlanketStatus == 1:
        BlanketStatus = 0
        print("Switched off the Electric blanket at",datetime.datetime.now().strftime("%I:%M:%S %p"))
    elif BlanketStatus == 0:
        BlanketStatus = 1
        print("Switched on the Electric blanket at",datetime.datetime.now().strftime("%I:%M:%S %p"))
        
def togglelight():
    
    global LightStatus
    if LightStatus == 1:
        LightStatus = 0
        print("Switched off the Lamp at",datetime.datetime.now().strftime("%I:%M:%S %p"))
    elif LightStatus == 0:
        LightStatus = 1
        print("Switched on the Lamp at",datetime.datetime.now().strftime("%I:%M:%S %p"))


        
        if humid >= 0 & humid < 100:
            
            MainHumid = humid
            update()
                      
        if temp >= 0 & temp < 100:
           
            MainTemp = temp
            update()
           
_thread.start_new_thread(listen, ())
_thread.start_new_thread(getmain, ())
print ("Online at",datetime.datetime.now().strftime("%I:%M:%S %p"))

def update():
    
    global BedHeatStatus
    global MainHeatStatus
    global BlanketStatus
    global LightStatus
    global MainTemp 
    global MainHumid
    global BedTemp
    global BedHumid
    
    server = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)      
    Status = str(MainHeatStatus) +  ',' + str(BedHeatStatus) + ',' + str(BlanketStatus) + ',' + str(LightStatus) + ',' + str(MainTemp).zfill(2) + ',' + str(MainHumid).zfill(2)  + ',' + str(BedTemp).zfill(2)  + ',' + str(BedHumid).zfill(2) 
    server.sendto(Status.encode(),(Conor, Port))
    server.sendto(Status.encode(),(Jason, Port))

def togglemain():
    
    global MainHeatStatus
    if MainHeatStatus == 1:
        MainHeatStatus = 0
        print("Switched off Living room heater at",datetime.datetime.now().strftime("%I:%M:%S %p"))
    elif MainHeatStatus == 0:
        MainHeatStatus = 1
        print("Switched on Living room heater at",datetime.datetime.now().strftime("%I:%M:%S %p"))
        
def togglebed(): 
    
    global BedHeatStatus
    if BedHeatStatus == 1:
        BedHeatStatus = 0
        print("Switched off Bedroom heater at",datetime.datetime.now().strftime("%I:%M:%S %p"))
    elif BedHeatStatus == 0:
        BedHeatStatus = 1
        print("Switched on Bedroom heater at",datetime.datetime.now().strftime("%I:%M:%S %p"))
        
def toggleblanket():
    
    global BlanketStatus
    if BlanketStatus == 1:
        BlanketStatus = 0
        print("Switched off the Electric blanket at",datetime.datetime.now().strftime("%I:%M:%S %p"))
    elif BlanketStatus == 0:
        BlanketStatus = 1
        print("Switched on the Electric blanket at",datetime.datetime.now().strftime("%I:%M:%S %p"))
        
def togglelight():
    
    global LightStatus
    if LightStatus == 1:
        LightStatus = 0
        print("Switched off the Lamp at",datetime.datetime.now().strftime("%I:%M:%S %p"))
    elif LightStatus == 0:
        LightStatus = 1
        print("Switched on the Lamp at",datetime.datetime.now().strftime("%I:%M:%S %p"))
