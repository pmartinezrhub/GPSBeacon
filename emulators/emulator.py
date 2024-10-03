#json example data from api
#{'status': 'success', 'country': 'United Kingdom', 'countryCode': 'GB', 'region': 'ENG', 'regionName': 'England', 'city': 'Ilford', 'zip': 'IG6',
# 'lat': 51.5877, 'lon': 0.0784, 'timezone': 'Europe/London', 'isp': 'Jisc Services Limited', 'org': 'Hertfordshire Internet Training Project', 'as': 'AS786 Jisc Services Limited', 'query': '212.121.29.229'}
#/bin/python
import requests
import random 
from time import sleep

while True:
    ip_random =  ".".join(map(str, (random.randint(0, 255) 
            for _ in range(4))))

    #res = requests.post('https://68975a3e13650be08630b26c6f9a1a24.m.pipedream.net/', data = {'IP':ip_random})
    '''res = requests.post('http://127.0.0.1:8088', data = {'IP':ip_random})'''
    print("sending data: " + str({'IP':ip_random}))
    with open('position.txt', 'w')as position_file:
        position_file.write(ip_random)
    position_file.close()
    #print(res)
    #sleep(5)

