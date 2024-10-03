#json example data from api
#{'status': 'success', 'country': 'United Kingdom', 'countryCode': 'GB', 'region': 'ENG', 'regionName': 'England', 'city': 'Ilford', 'zip': 'IG6',
# 'lat': 51.5877, 'lon': 0.0784, 'timezone': 'Europe/London', 'isp': 'Jisc Services Limited', 'org': 'Hertfordshire Internet Training Project', 'as': 'AS786 Jisc Services Limited', 'query': '212.121.29.229'}

#{'deviceID': 'emulator3_fakevdev', 'lat': '42.891665', 'lon': '-8.533984'}


#/bin/python
import requests
import random 
from time import sleep

deviceID = "emulator4"
#42.891665, -8.533984
lat = 42.891665
lon = -8.533984
url = "http://192.168.0.20:8088"
walk = 1
max_walk_distance = 20

while True:
    walk = random.randint(1, 16)
    #random_move_lat =  round(random.uniform(-0.00005, 0.00005), 4)
    #random_move_lng =  round(random.uniform(-0.00005, 0.00005), 4)
    random_change = random.randint(0, (max_walk_distance/2))
    for i in range(0, (max_walk_distance - random_change)):
        #res = requests.post('https://68975a3e13650be08630b26c6f9a1a24.m.pipedream.net/', data = {'IP':ip_random})
        data = (deviceID + ":" + str(lat) + ":" + str(lon))
        print("sendindg data to : " + url + " " + str(data))
        try:
            requests.post(url, data) 
        except Exception as e:
            print(str(e))
        #walking person should advance 0.0005

        if walk == 1:
            lon = round(lon + 0.00005, 8)
            print("direction E")
        if walk == 2:
            lon = round(lon - 0.00005, 8)
            print("direction O")
        if walk == 3:
            lat = round(lat + 0.00005, 8)    
            print("direction N")
        if walk == 4:
            lat = round(lat - 0.00005, 8)    
            print("direction S")
        if walk == 5:
            lon = round(lon + 0.00005, 8)
            lat = round(lat + 0.00005, 8)    
            print("direction NE")
        if walk == 6:
            lon = round(lon + 0.00005, 8)
            lat = round(lat - 0.00005, 8)    
            print("direction SE")
        if walk == 7:
            lon = round(lon - 0.00005, 8)
            lat = round(lat + 0.00005, 8)    
            print("direction NO")
        if walk == 8:
            lon = round(lon - 0.00005, 8)
            lat = round(lat - 0.00005, 8)    
            print("direction SW")
        if walk == 9:
            lon = round(lon + 0.00005, 8)
            lat = round(lat + 0.000025, 8)    
            print("direction NE")
        if walk == 10:
            lon = round(lon + 0.000025, 8)
            lat = round(lat + 0.00005, 8)        
            print("direction NE")
        if walk == 11:
            lon = round(lon + 0.00005, 8)
            lat = round(lat - 0.000025, 8)   
            print("direction SE")     
        if walk == 12:
            lon = round(lon + 0.000025, 8)
            lat = round(lat - 0.00005, 8)    
            print("direction SE")    
        if walk == 13:
            lon = round(lon - 0.00005, 8)
            lat = round(lat - 0.000025, 8)  
            print("direction SW")      
        if walk == 14:
            lon = round(lon - 0.000025, 8)
            lat = round(lat - 0.00005, 8)        
            print("direction SW")
        if walk == 15:
            lon = round(lon - 0.00005, 8)
            lat = round(lat + 0.000025, 8)   
            print("direction NO")    
        if walk == 16:
            lon = round(lon - 0.000025, 8)
            lat = round(lat + 0.00005, 8)        
            print("direction NO")
        sleep(5)
    
    
