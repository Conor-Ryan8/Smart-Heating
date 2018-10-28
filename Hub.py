#!/usr/bin/python
import socket
import time
import _thread
import datetime


PhoneA = "192.168.1.6"
PhoneB = "192.168.1.46"
Port = 9999

MainHeat = '0'
BedHeat = '0'
Blanket = '0'

print ("Starting..............")

def broadcast ():
    global BedHeat
    global MainHeat
    global Blanket
    server = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)
    
    while True:       
        Status = MainHeat +  ',' + BedHeat + ',' +Blanket
        server.sendto(str(Status).encode(),(PhoneA, Port))
        server.sendto(str(Status).encode(),(PhoneB, Port))       
        time.sleep(0.2)
        
def subscribe():
    
    global BedHeat
    global MainHeat
    global Blanket
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
               
_thread.start_new_thread(broadcast,())
_thread.start_new_thread(subscribe, ())
print ("Online at",datetime.datetime.now().strftime("%I:%M:%S %p"))

def togglemain():   
    global MainHeat
    if MainHeat == '1':
        MainHeat = '0'
        print("Switched off Living room heater at",datetime.datetime.now().strftime("%I:%M:%S %p"))
    elif MainHeat == '0':
        MainHeat = '1'
        print("Switched on Living room heater at",datetime.datetime.now().strftime("%I:%M:%S %p"))
        
def togglebed():  
    global BedHeat
    if BedHeat == '1':
        BedHeat = '0'
        print("Switched off Bedroom heater at",datetime.datetime.now().strftime("%I:%M:%S %p"))
    elif BedHeat == '0':
        BedHeat = '1'
        print("Switched on Bedroom heater at",datetime.datetime.now().strftime("%I:%M:%S %p"))
        
def toggleblanket(): 
    global Blanket
    if Blanket == '1':
        Blanket = '0'
        print("Switched off the Electric blanket at",datetime.datetime.now().strftime("%I:%M:%S %p"))
    elif Blanket == '0':
        Blanket = '1'
        print("Switched on the Electric blanket at",datetime.datetime.now().strftime("%I:%M:%S %p"))

