const express = require("express");
const cors = require("cors");
const router = require("./routes/router.js");
const PORT = process.env.PORT || 3000;
const bodyParser = require("body-parser");

const app = express();
app.use(cors());

app.use(express.json({ limit: "1gb" }));
app.use(express.urlencoded({ limit: "1gb", extended: true }));

app.use(bodyParser.json());

app.use("/api", router);

app.listen(PORT, () => console.log("Server running on PORT " + PORT));
