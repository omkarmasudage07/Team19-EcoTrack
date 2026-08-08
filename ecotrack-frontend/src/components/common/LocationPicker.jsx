import React, { useState, useCallback } from 'react';
import { MapContainer, TileLayer, Marker, useMapEvents } from 'react-leaflet';
import { FiMapPin, FiLoader } from 'react-icons/fi';
import '../../utils/leafletSetup';

const DEFAULT_CENTER = [19.076, 72.8777]; // Mumbai - just a sensible default, not tied to any user data
const DEFAULT_ZOOM = 12;

/**
 * Turns a lat/lng into a human-readable address using OpenStreetMap's
 * free Nominatim reverse-geocoding API. No API key needed - this is the
 * same free service Leaflet/OSM users typically pair with map picking.
 */
async function reverseGeocode(lat, lng) {
  const res = await fetch(
    `https://nominatim.openstreetmap.org/reverse?format=jsonv2&lat=${lat}&lon=${lng}`,
    { headers: { Accept: 'application/json' } }
  );
  if (!res.ok) throw new Error('Reverse geocoding failed');
  const data = await res.json();
  return data.display_name || '';
}

const ClickHandler = ({ onPick }) => {
  useMapEvents({
    click(e) {
      onPick(e.latlng.lat, e.latlng.lng);
    },
  });
  return null;
};

/**
 * Renders a click-to-drop-a-pin map. Calling `onLocationSelected` with
 * { latitude, longitude, address } every time the pin moves - the parent
 * form decides what to do with that (e.g. autofill the address field).
 */
const LocationPicker = ({ latitude, longitude, onLocationSelected, height = 320 }) => {
  const [position, setPosition] = useState(
    latitude && longitude ? [latitude, longitude] : null
  );
  const [geocoding, setGeocoding] = useState(false);

  const handlePick = useCallback(
    async (lat, lng) => {
      setPosition([lat, lng]);
      setGeocoding(true);
      let address = '';
      try {
        address = await reverseGeocode(lat, lng);
      } catch (e) {
        // Reverse geocoding is a convenience, not a requirement - if the
        // free Nominatim service is unreachable, the Citizen can still
        // type their address by hand. We just skip auto-fill silently.
      } finally {
        setGeocoding(false);
      }
      onLocationSelected({ latitude: lat, longitude: lng, address });
    },
    [onLocationSelected]
  );

  return (
    <div>
      <div className="rounded-3 overflow-hidden border" style={{ height }}>
        <MapContainer
          center={position || DEFAULT_CENTER}
          zoom={position ? 15 : DEFAULT_ZOOM}
          style={{ height: '100%', width: '100%' }}
        >
          <TileLayer
            attribution='&copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a> contributors'
            url="https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png"
          />
          <ClickHandler onPick={handlePick} />
          {position && <Marker position={position} />}
        </MapContainer>
      </div>
      <div className="small text-muted mt-2 d-flex align-items-center gap-1">
        {geocoding ? (
          <>
            <FiLoader size={14} className="spin" /> Looking up address...
          </>
        ) : (
          <>
            <FiMapPin size={14} />
            {position
              ? `Pin set at ${position[0].toFixed(5)}, ${position[1].toFixed(5)}`
              : 'Click anywhere on the map to drop a pin at your pickup location'}
          </>
        )}
      </div>
    </div>
  );
};

export default LocationPicker;
