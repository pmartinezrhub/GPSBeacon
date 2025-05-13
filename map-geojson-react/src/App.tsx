import { useState, useEffect } from 'react';
import MapaGeoJSON from './MapGeoJSON';

function App() {
  const [geojsonData, setGeojsonData] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    const fetchGeoJSON = async () => {
      try {
        setLoading(true);
        const response = await fetch('http://127.0.0.1:8000/route.geojson');
        
        if (!response.ok) {
          throw new Error(`HTTP error! status: ${response.status}`);
        }
        
        const data = await response.json();
        setGeojsonData(data);
        setError(null);
      } catch (err) {
        console.error('Error fetching GeoJSON:', err);
        setError('Error al cargar los datos del mapa');
      } finally {
        setLoading(false);
      }
    };

    fetchGeoJSON();
  }, []);

  return (
    <div className="App" style={{ display: 'flex', flexDirection: 'column', height: '100vh' }}>
      <h6 style={{ padding: '10px', margin: 0 }}>Visualizador de GeoJSON en React</h6>
      
      {loading ? (
        <p style={{ padding: '20px' }}>Cargando datos del mapa...</p>
      ) : error ? (
        <p style={{ padding: '20px', color: 'red' }}>{error}</p>
      ) : (
        <div style={{ flex: 1 }}>
          <MapaGeoJSON geojsonData={geojsonData} />
        </div>
      )}
    </div>
  );
}

export default App;