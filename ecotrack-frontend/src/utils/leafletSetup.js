import L from 'leaflet';
import markerIcon2x from 'leaflet/dist/images/marker-icon-2x.png';
import markerIcon from 'leaflet/dist/images/marker-icon.png';
import markerShadow from 'leaflet/dist/images/marker-shadow.png';
import 'leaflet/dist/leaflet.css';

// Vite bundles Leaflet's default marker images under a hashed path that
// Leaflet's own CSS can't find at runtime unless we point it back at the
// bundled URLs explicitly - this is the standard fix for "broken marker
// icon" when using Leaflet through a bundler. Importing this file once
// (from either map component) applies the fix for the whole app, since
// ES module imports are cached / only run once.
delete L.Icon.Default.prototype._getIconUrl;
L.Icon.Default.mergeOptions({
  iconRetinaUrl: markerIcon2x,
  iconUrl: markerIcon,
  shadowUrl: markerShadow,
});
