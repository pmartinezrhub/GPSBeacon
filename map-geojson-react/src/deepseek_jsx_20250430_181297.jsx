import React, { useState } from 'react';
import MapaGeoJSON from './MapaGeoJSON';

// Ejemplo de datos GeoJSON
const ejemploGeoJSON = {
  type: "FeatureCollection",
  features: [
    {
      type: "Feature",
      properties: {},
      geometry: {
        type: "Point",
        coordinates: [-0.09, 51.505]
      }
    },
    {
      type: "Feature",
      properties: {},
      geometry: {
        type: "LineString",
        coordinates: [
          [-0.1, 51.5],
          [-0.08, 51.51]
        ]
      }
    }
  ]
};

function App() {
  const [geojsonData, setGeojsonData] = useState(ejemploGeoJSON);

  return (
    <div className="App">
      <h1>Visualizador de GeoJSON en React</h1>
      <MapaGeoJSON geojsonData={geojsonData} />
    </div>
  );
}

export default App;