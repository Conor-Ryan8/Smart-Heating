import socket
import time
import thread
import datetime

#devices and values
MainHeatStatus = 0
BedHeatStatus = 0
BlanketStatus = 0
LightStatus = 0
MainTemp = 0
MainHumid = 0
BedTemp = 0
BedHumid = 0
Source = '0'

def listen():

        global BedTemp
        global BedHumid
        global Source

        Listen = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)
        Listen.setsockopt(socket.SOL_SOCKET, socket.SO_REUSEADDR, 1)

        #bind it to port 9998 and accept data from all IP addresse
        Listen.bind(("0.0.0.0",9998))

        #create a loop
        while True:

                #try to get data from the socket
                data, address = Listen.recvfrom(7)

                #check the message recieved
                if data.startswith('1'):
                        togglemain()
                if data.startswith('2'):
                        togglebed()
                if data.startswith('3'):
                        toggleblanket()
                if data.startswith('4'):
                        togglelight()
                if data.startswith('5'):
                        x,temp,humid = data.split(",")
                        BedTemp = temp
                        BedHumid = humid
                if data == '9':
                        Source = address
                        thread.start_new_thread(updatedevice, ())

def updatedevice():

        global BedHeatStatus
        global MainHeatStatus
        global BlanketStatus
        global LightStatus
        global MainTemp
        global MainHumid
        global BedTemp
        global BedHumid
        global Source

        Send = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)
        Send.setsockopt(socket.SOL_SOCKET, socket.SO_REUSEADDR, 1)

        Status = str(MainHeatStatus) + ',' + str(BedHeatStatus) + ',' + str(BlanketStatus) + ',' + str(LightStatus) + ',' + str(MainTemp).zfill(2) + ',' + str(MainHumi$
        Send.sendto(Status.encode(),Source)
        print('Sent device data' + Status + ' To: ' + str(Source))

def togglemain():

        global MainHeatStatus

        if MainHeatStatus == 1:

                MainHeatStatus = 0
                print("Switched off the living room heater.")

                MainHeatStatus = 1
                print("Switched on the living room heater.")

def togglebed():

        global BedHeatStatus

        if BedHeatStatus == 1:

                BedHeatStatus = 0
                print("Switched off the bedroom heater.")

        elif BedHeatStatus == 0:

                BedHeatStatus = 1
                print("Switched on the bedroom heater.")

def toggleblanket():

        global BlanketStatus

        if BlanketStatus == 1:

                BlanketStatus = 0
                print("Switched off the electric blanket.")

        elif BlanketStatus == 0:
                BlanketStatus = 1
                print("Switched on the electric blanket.")

def togglelight():

        global LightStatus

        if LightStatus == 1:

                LightStatus = 0
                print("Switched off the lamp.")

thread.start_new_thread(listen,())

while True:

        time.sleep(600)
