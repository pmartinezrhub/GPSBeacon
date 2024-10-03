from django.db import models
from django.utils import timezone

#from django_countries.fields import CountryField

class Tracker(models.Model):
    deviceID = models.CharField(max_length=100)
    latitude = models.FloatField()
    longitude = models.FloatField()
    logtime = models.TimeField()
    class Meta:
        db_table = "tracker"


class Devices(models.Model):
    deviceID = models.CharField(max_length=100)
    location = models.CharField(max_length=100)
    lastseen = models.TimeField()
    class Meta:
        db_table = "devices"
