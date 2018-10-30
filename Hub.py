##!/usr/bin/python
import socket
import time
import _thread
import datetime
import Adafruit_DHT

Conor = "192.168.1.6"
Jason = "192.168.1.46"
Port = 9999

MainHeatStatus = 0
BedHeatStatus = 0
BlanketStatus = 0
LightStatus = 0
MainTemp = 0
MainHumid = 0
BedTemp = 0
BedHumid = 0
       
def listen():

    server = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)
    server.bind(("0.0.0.0",9998))
    
    while True:
        
        data = repr(server.recvfrom(5))
        if data[3] == '1':
            togglemain()
        if data[3] == '2':
            togglebed()
        if data[3] == '3':
            toggleblanket()
        if data[3] == '4':
            togglelight()           
        update()
        
def getmain():
    
    global MainTemp
    global MainHumid
    
    while True:
        
        sensor = Adafruit_DHT.DHT11
        pin = 17
        h, t = Adafruit_DHT.read_retry(sensor, pin)
        temp = int(t)
        humid = int(h)
        
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
