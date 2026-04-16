import { useEffect, useState } from "react";
import axios from "axios";

function App() {
  const [earthquakes, setEarthquakes] = useState([]);
  const [minMagnitude, setMinMagnitude] = useState("");
  const [after, setAfter] = useState("");

  const API = "http://localhost:8080/api/earthquakes";

  // load all earthquakes (with optional filters)
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

  // fetch from external API + store in backend
  const fetchAndStore = async () => {
    try {
      await axios.post(`${API}/fetch`);
      loadEarthquakes();
    } catch (err) {
      console.error("Error fetching earthquakes:", err);
    }
  };

  // delete earthquake
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
        <button onClick={fetchAndStore}>
          Fetch Earthquakes
        </button>

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

        {/* Table */}
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
      </div>
  );
}

export default App;