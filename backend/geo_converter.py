import pandas as pd
from pyproj import Proj, Transformer
from threading import Thread, Lock
import multiprocessing
from typing import List
import json

df = pd.read_csv("vegnett.csv", sep=";")
data = df["GEO.WKT"]

utm_proj = Proj('epsg:32633')  # UTM Zone 33N
wgs84_proj = Proj('epsg:4326')  # (latitude/longitude)
transformer = Transformer.from_proj(utm_proj, wgs84_proj)
def convert_coordinates(data: List[str], start: int, end: int):
    for i in range(start, end):
        row = data[i]
        coords = row.replace("LINESTRING Z(", "").replace(")", "").split(", ")
        
        lat_lon = []
        for coord in coords:
            x, y, _ = map(float, coord.split())
            lon, lat = transformer.transform(x, y)
            lat_lon.append((lat, lon))
        
        add_lat_lons(lat_lon)



lock = Lock()
lat_lons = []
def add_lat_lons(lat_lon: list):
    lock.acquire()
    try:
        lat_lons.append(lat_lon)
    finally:
        lock.release()



n = len(data)
k = multiprocessing.cpu_count()
chunk = n // k
threads = []
for i in range(k-1):
    t = Thread(target=convert_coordinates, args=[data, (i*chunk), ((i+1)*chunk)])
    threads.append(t)
    t.start()
t = Thread(target=convert_coordinates, args=[data, ((k-1)*chunk), n])
threads.append(t)
t.start()

print("All threads started")

for t in threads:
    t.join()

print("Done")

with open("converted_coordinates.json", mode="w") as file:
    geojson_data = {
        "type": "MultiLineString",
        "coordinates": [
            [[lat, lon] for lat, lon in lat_lon] for lat_lon in lat_lons
        ]
    }
    json.dump(geojson_data, file, indent=4)