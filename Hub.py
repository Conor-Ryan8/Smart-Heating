import socket
import time
import _thread
import datetime
import Adafruit_DHT
from rpi_rf import RFDevice

AWS = '34.245.213.25'

Temp = 0
Humid = 0
IdealTemp = 20

def getTemp():
    
    global Temp
    global Humid
    
    Server = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)
    
    #temp values for tracking updates
    CurrentTemp = Temp
    CurrentHumid = Humid
    
    while True:
        
        #attempt to get data from the sensor module
        h1, t1 = Adafruit_DHT.read_retry(Adafruit_DHT.DHT11,27)
        
        #convert to integers
        t = int(t1)
        h = int(h1)
        
        #checks if humid is between 0 and 99 for the first reading taken.
        #for every other reading check if the value is also between 0 and 99, check if the value has changed, and had not fluctuated more than 25% indicating a sensor error
        if h >= 0 and h < 100 and CurrentHumid == 0 or h >= 0 and h< 100 and h != CurrentHumid and h < CurrentHumid*1.25 and h > CurrentHumid*0.75:
            
            #update relevent values
            Humid = h
            CurrentHumid = h          
            print("Humidity at " + str(h) + "%")
       
        #checks if temp is between 0 and 99 for the first reading taken.
        #for every other reading check if the value is also between 0 and 99, check if the value has changed, and had not fluctuated more than 25% indicating a sensor error
        if t >= 0 and t < 100 and t != CurrentTemp and CurrentTemp == 0 or t >= 0 and t < 100 and t != CurrentTemp and t < CurrentTemp*1.25 and t > CurrentTemp*0.75:

            #update relevent values 
            Temp = t
            CurrentTemp = t
            print("Temperature is " + str(t) + " °C")
            
            
        send = '5,'+ str(Temp) + ',' + str(Humid)
        Server.sendto(send.encode(),(AWS,9998))           
        time.sleep(10)
        
        
def deviceControl():
    
    HeaterON = 5330371
    HeaterOFF = 5330380
    BlanketON = 5330691
    BlanketOFF = 5330700
    
    Server = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)        
    Server.bind(("0.0.0.0",9999))
    Radio = RFDevice(17)
    PIN = 189
    send = '9'
    
    while True:
    
        Server.sendto(send.encode(),(AWS,9998))
        Server.settimeout(1)
        
        try:
            
            data = repr(Server.recvfrom(19))
    
        except Server.Timeouterror:
            
            break        

        a,HeaterStatus,BlanketStatus,b,c,d,e,f,g,h = data.split(",")
                   
        Radio.enable_tx()
        
        if HeaterStatus.startswith('0') or Temp > IdealTemp:            
            
            Radio.tx_code(HeaterOFF, 1, PIN)
            print('Radio: Heater OFF sent')
            
        elif HeaterStatus.startswith('1') and Temp <= IdealTemp:           
                
            Radio.tx_code(HeaterON, 1, PIN)
            print('Radio: Heater ON sent')
            
        time.sleep(3)
            
        if BlanketStatus.startswith('0'):        
                
            Radio.tx_code(BlanketOFF, 1, PIN)
            print('Radio: Blanket OFF sent')
                
        elif BlanketStatus.startswith('1'):            
                
            Radio.tx_code(BlanketON, 1, PIN)
            print('Radio: Blanket ON sent')
        
        time.sleep(3)
        
_thread.start_new_thread(getTemp,())
_thread.start_new_thread(deviceControl,())
