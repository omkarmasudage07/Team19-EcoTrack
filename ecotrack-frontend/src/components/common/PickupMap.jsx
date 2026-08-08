import React from 'react';
import { MapContainer, TileLayer, Marker, Popup } from 'react-leaflet';
import { FiNavigation } from 'react-icons/fi';
import '../../utils/leafletSetup';

/**
 * Read-only pickup location view for Recycler/Admin screens. If the
 * Citizen didn't use the map when scheduling (latitude/longitude are
 * null), this renders nothing rather than a broken/empty map.
 */
const PickupMap = ({ latitude, longitude, label = 'Pickup Location', height = 240 }) => {
  if (latitude == null || longitude == null) {
    return (
      <div className="small text-muted fst-italic">
        No map location was set for this pickup - only the typed address is available.
      </div>
    );
  }

  const googleMapsUrl = `https://www.google.com/maps/dir/?api=1&destination=${latitude},${longitude}`;

  return (
    <div>
      <div className="rounded-3 overflow-hidden border mb-2" style={{ height }}>
        <MapContainer
          center={[latitude, longitude]}
          zoom={15}
          style={{ height: '100%', width: '100%' }}
          dragging={true}
          scrollWheelZoom={false}
        >
          <TileLayer
            attribution='&copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a> contributors'
            url="https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png"
          />
          <Marker position={[latitude, longitude]}>
            <Popup>{label}</Popup>
          </Marker>
        </MapContainer>
      </div>
      <a
        href={googleMapsUrl}
        target="_blank"
        rel="noopener noreferrer"
        className="btn btn-sm btn-outline-success d-inline-flex align-items-center gap-2"
      >
        <FiNavigation size={14} /> Navigate with Google Maps
      </a>
    </div>
  );
};

export default PickupMap;
