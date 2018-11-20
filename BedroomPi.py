import socket
import time
import _thread
import datetime
import Adafruit_DHT
from rpi_rf import RFDevice

Heater = 0
Blanket = 0
Temp = 00
Humid = 00
IdealTemp = 20

AWS = '34.245.213.25'

def getTemp():
    
    global Temp
    global Humid
	
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

        #checks if temp is between 0 and 99 for the first reading taken.
        #for every other reading check if the value is also between 0 and 99, check if the value has changed, and had not fluctuated more than 25% indicating a sensor error
        if t >= 0 and t < 100 and t != CurrentTemp and CurrentTemp == 0 or t >= 0 and t < 100 and t != CurrentTemp and t < CurrentTemp*1.25 and t > CurrentTemp*0.75:

            #update relevent values 
            Temp = t
            CurrentTemp = t
            
        time.sleep(5)
            
def deviceControl():

    global Heater
    global Blanket

    HeaterON = 5330371
    HeaterOFF = 5330380
    BlanketON = 5330691
    BlanketOFF = 5330700

    Radio = RFDevice(17)
    

    while True:
        
        Radio.enable_tx()
        PIN = 189

        if Heater == '0' or Temp > IdealTemp:

            Radio.tx_code(HeaterOFF, 1, PIN)
            print('Sent - HeaterOFF')
            
        elif Heater == '1' and Temp <= IdealTemp:

            Radio.tx_code(HeaterON, 1, PIN)
            print('Sent - HeaterON')
            
        time.sleep(2)

        if Blanket == '0':
            Radio.tx_code(BlanketOFF, 1, PIN)
            print('Sent - BlanketOFF')

        elif Blanket == '1':

            Radio.tx_code(BlanketON, 1, PIN)
            print('Sent - BlanketON')

        time.sleep(2)

def sync():

    global Temp
    global Humid
    global Heater
    global Blanket

    Send = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)

    while True:

        data = '5,' + str(Temp) + ',' + str(Humid)
        Send.sendto(data.encode(),(AWS,9998))
        time.sleep(1)
        data = repr(Send.recvfrom(19))
        a,Heater,Blanket,b,c,d,e,f,g,h = data.split(",")
        print('Sync complete! Heater: ' +Heater+' Blanket: '+Blanket+'  Temperature: '+str(Temp)+'Degrees  Humidity: '+str(Humid)+'%')
        time.sleep(1)
        
_thread.start_new_thread(getTemp,())
_thread.start_new_thread(deviceControl,())
_thread.start_new_thread(sync,())
