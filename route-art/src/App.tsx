import { useRef, useEffect, useState } from 'react'
import mapboxgl from 'mapbox-gl'

import 'mapbox-gl/dist/mapbox-gl.css';

import './App.css'
import coordinatesJSON from './converted_coordinates.json';

const INITIAL_CENTER: [number, number] = [
  10.7521,
  59.9303,
]
const INITIAL_ZOOM: number = 12

const coordinates = coordinatesJSON as { type: string; coordinates: number[][][] }
console.log(coordinates.coordinates.slice(0, 2))
console.log([
  [
    [10, 59],
    [10.1, 59.1],
    [10.2, 59.1]
  ],
  [
    [9, 58],
    [9.9, 58.9]
  ]
])

function App() {
  const mapRef = useRef<mapboxgl.Map | null>(null)
  const mapContainerRef = useRef<HTMLDivElement | null>(null)

  const [center, setCenter] = useState<[number, number]>(INITIAL_CENTER)
  const [zoom, setZoom] = useState<number>(INITIAL_ZOOM)

  // One-time useEffect
  useEffect(() => {
    mapboxgl.accessToken = process.env.REACT_APP_MAPBOX_TOKEN || ''
    mapRef.current = new mapboxgl.Map({
      container: mapContainerRef.current || '',
      style: 'mapbox://styles/mapbox/streets-v12',
      center: center,
      zoom: zoom,
    })

    mapRef.current.on('move', () => {
      if (mapRef.current != null) {
        const mapCenter = mapRef.current.getCenter()
        const mapZoom = mapRef.current.getZoom()

        setCenter([mapCenter.lng, mapCenter.lat])
        setZoom(mapZoom)
      }
    })

    mapRef.current.on('load', () => {
      console.log(coordinates.coordinates)
      if (mapRef.current != null) {
        mapRef.current.addSource('route', {
          type: 'geojson',
          data: {
            type: 'Feature',
            properties: {},
            geometry: {
              type: 'MultiLineString',
              coordinates: coordinates.coordinates
            }
          }
        });

        mapRef.current.addLayer({
          id: 'route',
          type: 'line',
          source: 'route',
          layout: {
            'line-join': 'round',
            'line-cap': 'round'
          },
          paint: {
            'line-color': '#ff0000',
            'line-width': 1
          }
        });
      }
    })


    return () => {
      if (mapRef.current) {
        mapRef.current.remove()
      }
    }
  }, [])

  const handleButtonClick = () => {
    if (mapRef.current != null) {
      mapRef.current.flyTo({
        center: INITIAL_CENTER,
        zoom: INITIAL_ZOOM
      })
    }
  }



  return (
    <>
      <div className="sidebar">
        Longitude: {center[0].toFixed(4)} | Latitude: {center[1].toFixed(4)} | Zoom: {zoom.toFixed(2)}
      </div>
      <button className='reset-button' onClick={handleButtonClick}>
        Reset
      </button>
      <div style={{ height: '100%' }} id='map-container' ref={mapContainerRef} />
    </>
  );
}

export default App;
