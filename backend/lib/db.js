// lib/db.js

const mysql = require("mysql");
const connection = mysql.createPool({
	host: "127.0.0.1",
	user: "admin",
	database: "movesense",
	port: 3306,
	password: "ismai2022",
});
// connection.connect();
module.exports = connection;
