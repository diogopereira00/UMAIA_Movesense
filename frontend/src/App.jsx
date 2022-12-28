import { useState } from "react";
import React from "react";
import { BrowserRouter, Routes, Route } from "react-router-dom";

import reactLogo from "./assets/react.svg";
import "./App.css";
import Login from "./pages/Login";

function App() {
	const [count, setCount] = useState(0);

	return (
		<BrowserRouter>
			<Routes>
				<Route path="/login" element={<Login />} />
			</Routes>
		</BrowserRouter>
	);
}

export default App;
