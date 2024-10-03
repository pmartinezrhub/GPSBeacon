from http.server import BaseHTTPRequestHandler, HTTPServer
from time import sleep
import MySQLdb
import logging
from termcolor import colored

class S(BaseHTTPRequestHandler):

    def _set_response(self):
        self.send_response(200)
        self.send_header('Content-type', 'text/html')
        self.end_headers()

    def do_GET(self):
        logging.info("GET request,\nPath: %s\nHeaders:\n%s\n", str(self.path), str(self.headers))
        self._set_response()
        self.wfile.write("GET request for {}".format(self.path).encode('utf-8'))

    def database_update(self, data, deviceID, location):
        longitude = location.split(",")[0]
        latitude = location.split(",")[1]
        host = '192.168.0.20'
        port = 3306
        dbuser = "trackerdbuser"
        password = "passworddb"
        database = "devicetracker"
        select_device_query = "SELECT * FROM devices WHERE DeviceID = '" + deviceID + "'"""
        insert_device_query = "INSERT INTO devices (deviceID, location, lastseen) VALUES('" + deviceID + "','" + location +"', CURRENT_TIMESTAMP())"
        update_device_query = "UPDATE devices SET location='" + location + "', lastseen=CURRENT_TIMESTAMP() WHERE deviceID ='" + deviceID + "'"""
        insert_tracker_query = "INSERT INTO tracker(deviceID, latitude, longitude, logtime) VALUES('" + deviceID +  "','" + latitude + "','" + longitude +"', CURRENT_TIMESTAMP())"
        print("Updating database[+]")
        try:
            db_connection = MySQLdb.connect(user=dbuser, db=database, passwd=password, host=host)
        except Exception as e:
            print (e)
        try:
            cursor = db_connection.cursor()
            cursor.execute(select_device_query)
            db_connection.commit()
            records = cursor.fetchall() 
            rowCount = len(records) 
            if rowCount == 0: 
                cursor.execute(insert_device_query)
                db_connection.commit()             
                print("New ID " + deviceID +" added to DB[+]")
            else:
                latitude = location.split(",")[0]
                longitude = location.split(",")[1]
                if (latitude != "0.0" and longitude != "0.0"):
                    cursor.execute(update_device_query)
                    db_connection.commit()
                else:
                    print(colored("Duplicated device with Coords 0,0", "blue"))
            #anycase insert the track               
            cursor.execute(insert_tracker_query)
            db_connection.commit()
            print("Update " + data + " in tracker[+]")
            db_connection.close()

        except Exception as e:
            print(e)


    def do_POST(self):
        content_length = int(self.headers['Content-Length']) # <--- Gets the size of data
        post_data = self.rfile.read(content_length) # <--- Gets the data itself
        #logging.info("POST request,\nPath: %s\nHeaders:\n%s\n\nBody:\n%s\n",str(self.path), str(self.headers), post_data.decode('utf-8'))
        self._set_response()
        self.wfile.write("POST request for {}".format(self.path).encode('utf-8'))
        data = post_data.decode("utf-8") 
        #print(data)
        data_split = data.split(":")
        if (len(data_split) == 3):
            print(colored(str(data), 'yellow'))
            deviceID = data_split[0]
            latitude = data_split[2]
            longitude = data_split[1]
            location = latitude + "," + longitude
            self.database_update(data, deviceID, location)
            print("################################################################")
        else:
            print("[-] wrong data")
            print(colored(str(data), 'red'))
        


def run(server_class=HTTPServer, handler_class=S, address="192.168.0.20", port=8088):
    logging.basicConfig(level=logging.INFO)
    server_address = (address, port)
    httpd = server_class(server_address, handler_class)
    logging.info('Starting httpd...\n')
    try:
        httpd.serve_forever()
    except KeyboardInterrupt:
        pass
    httpd.server_close()
    logging.info('Stopping httpd...\n')

if __name__ == '__main__':
    from sys import argv
    if len(argv) == 2:
        run(port=int(argv[1]))
    else:
        run()
