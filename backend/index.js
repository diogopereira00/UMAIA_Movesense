const express = require("express");
const cors = require("cors");
const router = require("./routes/router.js");
const PORT = process.env.PORT || 3000;

const app = express();

app.use(express.json());
app.use("/api", router);

app.listen(PORT, () => console.log("Server running on PORT " + PORT));
