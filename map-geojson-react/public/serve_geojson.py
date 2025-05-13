from flask import Flask, make_response
from flask_cors import CORS

app = Flask(__name__)
CORS(app)  # Habilita CORS correctamente

@app.route('/route.geojson')
def serve_geojson():
    try:
        with open('route.geojson', 'r', encoding='utf-8') as f:
            data = f.read()
        response = make_response(data)
        response.headers['Content-Type'] = 'application/json'
        return response
    except FileNotFoundError:
        return make_response({'error': 'Archivo no encontrado'}, 404)

if __name__ == '__main__':
    app.run(port=8000)

