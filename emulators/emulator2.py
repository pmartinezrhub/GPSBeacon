#json example data from api
#{'status': 'success', 'country': 'United Kingdom', 'countryCode': 'GB', 'region': 'ENG', 'regionName': 'England', 'city': 'Ilford', 'zip': 'IG6',
# 'lat': 51.5877, 'lon': 0.0784, 'timezone': 'Europe/London', 'isp': 'Jisc Services Limited', 'org': 'Hertfordshire Internet Training Project', 'as': 'AS786 Jisc Services Limited', 'query': '212.121.29.229'}
#/bin/python
import requests
import random 
from time import sleep

lat = 43.097
lon = 0.0784

while True:
    #res = requests.post('https://68975a3e13650be08630b26c6f9a1a24.m.pipedream.net/', data = {'IP':ip_random})

    #data = {'lat': lat, 'lon': lon}
    data = (str(lat) + "," + str(lon))
    print("sendindg data: " + str(data))
    with open('position.txt', 'w')as position_file:
        position_file.write(str(data))
    position_file.close()
    lon = round(lon + 0.0005, 4)
    #print(res)
    sleep(10)