import datetime
from django.http import HttpResponse
from django.shortcuts import render
from django.template import Template, Context
from . import models
from django.db.models import Count
import json
import os
import requests
import logging
from time import sleep
import MySQLdb
import itertools

BASE_DIR = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))

def home(request):
    today = datetime.datetime.today()
    wellcome_message = "Hello, world. Today is " + str(today)
    return HttpResponse(wellcome_message)

def get_device_data(deviceID):
    host = '192.168.0.130'
    dbuser = "trackerdbuser"
    password = "passworddb"
    database = "devicetracker"
    db = MySQLdb.connect(user=dbuser, db=database, passwd=password, host=host)
    cursor = db.cursor()
    select_device_query = "SELECT * FROM devices WHERE DeviceID = '" + deviceID + "'"""
    cursor.execute(select_device_query)
    db.commit()
    results = cursor.fetchone()
    db.close()
    return results

def get_tracking_data(deviceID):
    host = '192.168.0.20'
    dbuser = "trackerdbuser"
    password = "passworddb"
    database = "devicetracker"
    select_tracking_query = "SELECT id, deviceID, longitude, latitude FROM tracker WHERE DeviceID LIKE '" + deviceID + "'"""
    dbconnection = MySQLdb.connect(user=dbuser, db=database, passwd=password, host=host)
    tracking_cursor = dbconnection.cursor()
    tracking_cursor.execute(select_tracking_query)
    results_query = tracking_cursor.fetchall()
    data = []
    for result in results_query:
        id = result[0]
        deviceID = result[1]
        longitude = result[2]
        latitude = result[3]
        parsed_data = {"deviceID": deviceID, "lon":longitude, "lat": latitude }
        data.append(parsed_data)
    return data

def map(request, deviceid):
    if deviceid:
        deviceID = deviceid
    template_path = BASE_DIR.__str__() + "/GeoIPMap/templates/map.html"
    doc_ext = open(template_path)
    mapgeoip = Template(doc_ext.read())
    doc_ext.close()
    data = ""
    #forced device for test
    #deviceID = "emulator4"
    dbtrack = get_tracking_data(deviceID)
    dbtrack_json = json.dumps(dbtrack)
    context = {"data": dbtrack_json}
    return render(request, template_path, context)

def geoip(request):
    template_path = BASE_DIR.__str__() + "/GeoIPMap/templates/geoip.html"
    doc_ext = open(template_path)
    geoip = Template(doc_ext.read())
    doc_ext.close()
    now = datetime.date.today()
    #activate this line on production only
    #ip_address_client = str(request.META.get("REMOTE_ADDR"))
    #for testing activate your own
    ip_address_client = "62.83.150.17"
    client_ip_request = "http://ip-api.com/json/" + ip_address_client
    url_request = requests.get(client_ip_request)
    text = url_request.text
    data = json.loads(text)
    log_file(data)
    if data["countryCode"]:
        country_code = data["countryCode"]
    if data["country"]:
        country_name = data["country"].lower()
    flag_link = "https://flagsworld.org/img/cflags/" + country_name +  "-flag.png"
    add_flag = {"flag": flag_link}
    data.update(add_flag)
    log_file(str(request.META.get("REMOTE_ADDR")))
    return render(request, template_path, data)

def about(request):
    template_path = BASE_DIR.__str__() + "/GeoIPMap/templates/about.html"
    doc_ext = open(template_path)
    doc_ext.close()
    return render(request, template_path)

def log_file(data):
    logging.basicConfig(filename='geoip.log', filemode='w', format='%(name)s - %(levelname)s - %(message)s')
    logging.warning(str(data))

def usersettings(request):
    template_path = BASE_DIR.__str__() + "/GeoIPMap/templates/usersettings.html"
    doc_ext = open(template_path)
    doc_ext.close()
    track = models.Tracker.objects.all()
    context = {'Tracker': track}
    return render(request, template_path, context)    

def tracker(request):
    template_path = BASE_DIR.__str__() + "/GeoIPMap/templates/tracker.html"
    doc_ext = open(template_path)
    doc_ext.close()
    tracks = models.Tracker.objects.all().order_by('-id')[:150]
    context = {'Tracker': tracks}
    return render(request, template_path, context)

def devices(request):
    template_path = BASE_DIR.__str__() + "/GeoIPMap/templates/devices.html"
    doc_ext = open(template_path)
    doc_ext.close()
    devices = models.Devices.objects.all()
    context = {'Devices': devices}
    return render(request, template_path, context)