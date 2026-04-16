import { useEffect, useState } from "react";
import axios from "axios";

import "leaflet/dist/leaflet.css";
import { MapContainer, TileLayer, Marker, Popup } from "react-leaflet";
import L from "leaflet";


delete L.Icon.Default.prototype._getIconUrl;

L.Icon.Default.mergeOptions({
  iconRetinaUrl:
      "https://unpkg.com/leaflet@1.9.4/dist/images/marker-icon-2x.png",
  iconUrl:
      "https://unpkg.com/leaflet@1.9.4/dist/images/marker-icon.png",
  shadowUrl:
      "https://unpkg.com/leaflet@1.9.4/dist/images/marker-shadow.png",
});

function App() {
  const [earthquakes, setEarthquakes] = useState([]);
  const [minMagnitude, setMinMagnitude] = useState("");
  const [after, setAfter] = useState("");

  const API = "http://localhost:8080/api/earthquakes";

  const loadEarthquakes = async () => {
    try {
      const params = {};

      if (minMagnitude !== "") {
        params.minMagnitude = minMagnitude;
      }

      if (after !== "") {
        params.after = after;
      }

      const res = await axios.get(API, { params });
      setEarthquakes(res.data);
    } catch (err) {
      console.error("Error loading earthquakes:", err);
    }
  };

  const fetchAndStore = async () => {
    try {
      await axios.post(`${API}/fetch`);
      loadEarthquakes();
    } catch (err) {
      console.error("Error fetching earthquakes:", err);
    }
  };

  const deleteEarthquake = async (id) => {
    try {
      await axios.delete(`${API}/${id}`);
      loadEarthquakes();
    } catch (err) {
      console.error("Error deleting earthquake:", err);
    }
  };

  useEffect(() => {
    loadEarthquakes();
  }, []);

  return (
      <div style={{ padding: "20px" }}>
        <h1>Earthquakes 🌍</h1>

        {/* Fetch button */}
        <button onClick={fetchAndStore}>Fetch Earthquakes</button>

        {/* Filters */}
        <div style={{ marginTop: "20px" }}>
          <h3>Filters</h3>

          <input
              type="number"
              placeholder="Min magnitude"
              value={minMagnitude}
              onChange={(e) => setMinMagnitude(e.target.value)}
          />

          <input
              type="datetime-local"
              value={after}
              onChange={(e) => setAfter(e.target.value)}
              style={{ marginLeft: "10px" }}
          />

          <button onClick={loadEarthquakes} style={{ marginLeft: "10px" }}>
            Apply Filters
          </button>

          <button
              onClick={() => {
                setMinMagnitude("");
                setAfter("");
                loadEarthquakes();
              }}
              style={{ marginLeft: "10px" }}
          >
            Clear
          </button>
        </div>

        {/* TABLE */}
        <table border="1" cellPadding="10" style={{ marginTop: "20px" }}>
          <thead>
          <tr>
            <th>ID</th>
            <th>Magnitude</th>
            <th>Location</th>
            <th>Date</th>
            <th>Action</th>
          </tr>
          </thead>

          <tbody>
          {earthquakes.length === 0 ? (
              <tr>
                <td colSpan="5">No data</td>
              </tr>
          ) : (
              earthquakes.map((eq) => (
                  <tr key={eq.id}>
                    <td>{eq.id}</td>
                    <td>{eq.magnitude}</td>
                    <td>{eq.place}</td>
                    <td>{eq.time}</td>
                    <td>
                      <button onClick={() => deleteEarthquake(eq.id)}>
                        Delete
                      </button>
                    </td>
                  </tr>
              ))
          )}
          </tbody>
        </table>

        {/* MAP */}
        <h2 style={{ marginTop: "30px" }}>Map 🌍</h2>

        <MapContainer
            center={[20, 0]}
            zoom={2}
            style={{ height: "500px", width: "100%" }}
        >
          <TileLayer
              url="https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png"
              attribution="&copy; OpenStreetMap contributors"
          />

          {earthquakes
              .filter((eq) => eq.latitude && eq.longitude)
              .map((eq) => (
                  <Marker
                      key={eq.id}
                      position={[eq.latitude, eq.longitude]}
                  >
                    <Popup>
                      <b>{eq.place}</b> <br />
                      Magnitude: {eq.magnitude} <br />
                      Depth: {eq.depth}
                    </Popup>
                  </Marker>
              ))}
        </MapContainer>
      </div>
  );
}

export default App;