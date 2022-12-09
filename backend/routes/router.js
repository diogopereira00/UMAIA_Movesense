const bcrypt = require("bcryptjs");
const uuid = require("uuid");
const jwt = require("jsonwebtoken");
const db = require("../lib/db.js");
const userMiddleware = require("../middleware/users.js");

const router = require("express").Router();


router.post("/sign-up", userMiddleware.validateRegister, (req, res, next) => {
	db.query(`SELECT * FROM users WHERE LOWER(username) = LOWER(${db.escape(req.body.username)});`, (err, result) => {
		if (result.length) {
			return res.status(409).send({
				msg: "This username is already in use!",
			});
		} else {
			// username is available
			bcrypt.hash(req.body.password, 10, (err, hash) => {
				if (err) {
					return res.status(500).send({
						msg: err,
					});
				} else {
					// has hashed pw => add to database
					db.query(
						`INSERT INTO users (id, username, password, created_at,last_login) VALUES ('${uuid.v4()}', ${db.escape(
							req.body.username
						)}, ${db.escape(hash)}, now(), now())`,
						(err, result) => {
							if (err) {
								throw err;
								return res.status(400).send({
									msg: err,
								});
							}
							return res.status(201).send({
								msg: "Registered!",
							});
						}
					);
				}
			});
		}
	});
});

router.post("/login", (req, res, next) => {
	console.log(req)
	db.query(`SELECT * FROM users WHERE username = ${db.escape(req.body.username)};`, (err, result) => {
		// user does not exists
		if (err) {
			throw err;
			return res.status(400).send({
				msg: err,
			});
		}
		if (!result.length) {
			console.log("aqi")
			return res.status(401).send({
				msg: "Username or password is incorrect!",
			});
		}
		// check password
		bcrypt.compare(req.body.password, result[0]["password"], (bErr, bResult) => {
			// wrong password
			if (bErr) {
				throw bErr;
				return res.status(401).send({
					msg: "Username or password is incorrect!",
				});
			}
			if (bResult) {
				const token = jwt.sign(
					{
						username: result[0].username,
						userId: result[0].id,
					},
					"SECRETKEY",
					{
						expiresIn: "7d",
					}
				);
				db.query(`UPDATE users SET last_login = now() WHERE id = '${result[0].id}'`);
				return res.status(200).send({
					msg: "Logged in!",
					user: {
						id: result[0].id,
						username: result[0].username,
						created_at: result[0].created_at,
						last_login: result[0].last_login,
						access_token: token,
					},
				});
			}
			return res.status(401).send({
				msg: "Username or password is incorrect!",
			});
		});
	});
});

router.get('/secret-route', userMiddleware.isLoggedIn, (req, res, next) => {
	console.log(req.userData);
	res.send('This is the secret content. Only logged in users can see that!');
});


router.post("/addAccData", userMiddleware.isLoggedIn, (req, res, next) => {
	console.log(req.headers)
	let accs = JSON.parse(req.body.jsonString);

	db.query(`INSERT INTO acc_table (id_on_phone, user_id, x, y, z, timestamp, created_at) VALUES ?`,
		[accs.map(acc => [acc.id, acc.userID, acc.x, acc.y, acc.z, acc.timestamp, new Date(acc.created).toISOString().slice(0, 19).replace('T', ' ')])],
		(err, ress) => {
			if (err) {
				throw err;
				return res.status(401).send({
					msg: "Erro",
				});
			}
			return res.status(200).send({
				msg: "Dados adicionados",
			});
		}
	);
});

router.post("/addGyroData", userMiddleware.isLoggedIn, (req, res, next) => {
	console.log(req.headers)
	let gyros = JSON.parse(req.body.jsonString);

	db.query(`INSERT INTO gyro_table (id_on_phone, user_id, x, y, z, timestamp, created_at) VALUES ?`,
		[gyros.map(gyro => [gyro.id, gyro.userID, gyro.x, gyro.y, gyro.z, gyro.timestamp, new Date(gyro.created).toISOString().slice(0, 19).replace('T', ' ')])],
		(err, ress) => {
			if (err) {
				throw err;
				return res.status(401).send({
					msg: "Erro",
				});
			}
			return res.status(200).send({
				msg: "Dados adicionados",
			});
		}
	);
});

router.post("/addMagnData", userMiddleware.isLoggedIn, (req, res, next) => {
	console.log(req.headers)
	let magns = JSON.parse(req.body.jsonString);

	db.query(`INSERT INTO magn_table (id_on_phone, user_id, x, y, z, timestamp, created_at) VALUES ?`,
		[magns.map(magn => [magn.id, magn.userID, magn.x, magn.y, magn.z, magn.timestamp, new Date(magn.created).toISOString().slice(0, 19).replace('T', ' ')])],
		(err, ress) => {
			if (err) {
				throw err;
				return res.status(401).send({
					msg: "Erro",
				});
			}
			return res.status(200).send({
				msg: "Dados adicionados",
			});
		}
	);
});

router.post("/addECGData", userMiddleware.isLoggedIn, (req, res, next) => {
	console.log(req.headers)
	let ecgs = JSON.parse(req.body.jsonString);

	db.query(`INSERT INTO ecg_table (id_on_phone, user_id, data, timestamp, created_at) VALUES ?`,
		[ecgs.map(ecg => [ecg.id, ecg.userID, ecg.data, ecg.timestamp, new Date(ecg.created), toISOString().slice(0, 19).replace('T', ' ')])],
		(err, ress) => {
			if (err) {
				throw err;
				return res.status(401).send({
					msg: "Erro",
				});
			}
			return res.status(200).send({
				msg: "Dados adicionados",
			});
		}
	);
});


router.post("/addHRData", userMiddleware.isLoggedIn, (req, res, next) => {
	console.log(req.headers)
	let hrs = JSON.parse(req.body.jsonString);

	db.query(`INSERT INTO hr_table (id_on_phone, user_id, average, rr_data, created_at) VALUES ?`,
		[hrs.map(hr => [hr.id, hr.userID, hr.average, hr.rrData, new Date(hr.created), toISOString().slice(0, 19).replace('T', ' ')])],
		(err, ress) => {
			if (err) {
				throw err;
				return res.status(401).send({
					msg: "Erro",
				});
			}
			return res.status(200).send({
				msg: "Dados adicionados",
			});
		}
	);
});
module.exports = router;
