import socket
import time
import _thread
import datetime
import Adafruit_DHT
import paho.mqtt.client as mqtt
import paho.mqtt.publish as publish
import RPi.GPIO as GPIO
GPIO.setwarnings(False)
from rpi_rf import RFDevice

MQTT_SERVER = "localhost"
TEMP_PATH = "Temp"
HUMID_PATH = "Humid"
HEAT_PATH = "Heat"
LIGHT_PATH = "Light"
BLANKET_PATH = "Blanket"
TOGGLE_PATH = "Toggle"

Temp = 0
Humid = 0
Heat = 1
Blanket = 0
Light = 1

def ControlSockets():
    
    global Light
    global Heat
    global Blanket
    
    Radio = RFDevice(17)
    Radio.enable_tx()

    HeatOn = 5330227
    HeatOff = 5330236
    LightOn = 5330691
    LightOff = 5330700
    BlanketOn = 5330371
    BlanketOff = 5330380
    UnusedOn = 5332227
    UnusedOff = 5332236
    
    PIN = 189
    
    while True:
        
        if  Heat == 0:            
            Radio.tx_code(HeatOff, 1, PIN)
        
        elif Heat == 1:           
            Radio.tx_code(HeatOn, 1, PIN)
        
        time.sleep(1)
        
        if  Light == 0:            
            Radio.tx_code(LightOff, 1, PIN)
        
        elif Light == 1:            
            Radio.tx_code(LightOn, 1, PIN)
            
        time.sleep(1)
            
        if  Blanket == 0:           
            Radio.tx_code(BlanketOff, 1, PIN)
        
        elif Blanket == 1:            
            Radio.tx_code(BlanketOn, 1, PIN)
        
        time.sleep(1)

def SensorReadings():
    
    global Temp
    global Humid
    
    #temp values for tracking updates
    CTemp = Temp
    CHumid = Humid
    
    while True:
        
        #attempt to get data from the sensor module
        X, Y = Adafruit_DHT.read_retry(Adafruit_DHT.DHT22,27)
        #convert to integers
        T = int(Y)
        H = int(X)
        
        #If between 0 and 99 and the first reading, or between 0 and 99 and the reading has changed but not more than 25%
        if H >= 0 and H < 100 and CHumid == 0 or H >= 0 and H < 100 and H < CHumid*1.25 and H > CHumid*0.75:
             
            #update relevent values 
            Humid = H
            CHumid = H
            
            #print status message indicating an update
            print("Living Room Humidity at " + str(H) + "%")            
            publish.single(HUMID_PATH, str(H), hostname=MQTT_SERVER)
         
        #If between 0 and 99 and the first reading, or between 0 and 99 and the reading has changed but not more than 25%
        if T >= 0 and T < 100 and CTemp == 0 or T >= 0 and T < 100 and T < CTemp*1.25 and T > CTemp*0.75:

            #update relevent values 
            Temp = T
            CTemp = T
            
            #print status message indicating an update
            print("Living Room is " + str(T) + " °C")           
            publish.single(TEMP_PATH, str(T), hostname=MQTT_SERVER)            
            
        time.sleep(10)
            
def PublishDevices():
    
    global Heat
    global Light
    global Blanket
    
    while True:       
        publish.single(HEAT_PATH, str(Heat), hostname=MQTT_SERVER)
        publish.single(LIGHT_PATH, str(Light), hostname=MQTT_SERVER)
        publish.single(BLANKET_PATH, str(Blanket), hostname=MQTT_SERVER)
        time.sleep(1)
           
#start listening and sensor threads
_thread.start_new_thread(ControlSockets, ())
_thread.start_new_thread(PublishDevices, ())
_thread.start_new_thread(SensorReadings, ())

#startup complete status message
print ("Online at",datetime.datetime.now().strftime("%I:%M:%S %p"))

def on_connect(client, userdata, flags, rc):
     
    client.subscribe(TOGGLE_PATH)
    
def on_message(client, userdata, msg):
    
    print(str(msg.payload))
    Request = str(msg.payload)
    
    if Request == 1:        
        ToggleHeat()
        
    if Request == 1:        
        ToggleLight()
        
    if Request == 1:        
        ToggleBlanket()
        
client = mqtt.Client()
client.on_connect = on_connect
client.on_message = on_message
client.connect(MQTT_SERVER, 1883, 60)
client.loop_forever()
