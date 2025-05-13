import React from 'react';
import { MapContainer, TileLayer, GeoJSON } from 'react-leaflet';
import 'leaflet/dist/leaflet.css';

const MapaGeoJSON = ({ geojsonData }) => {
  return (
    <MapContainer 
      center={[42.89, -8.09]} 
      zoom={13} 
      style={{ height: '600px', width: '800px' }}
    >
      <TileLayer
        url="https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png"
        attribution='&copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a> contributors'
      />
      {geojsonData && <GeoJSON data={geojsonData} />}
    </MapContainer>
  );
};

export default MapaGeoJSON;