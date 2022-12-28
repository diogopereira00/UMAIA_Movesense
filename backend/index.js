const express = require("express");
const cors = require("cors");
const router = require("./routes/router.js");
const PORT = process.env.PORT || 3000;

const app = express();
app.use(cors());

app.use(express.json({ limit: '50mb' }));
app.use(express.urlencoded({ limit: '50mb', extended: true }));
app.use("/api", router);

app.listen(PORT, () => console.log("Server running on PORT " + PORT));
