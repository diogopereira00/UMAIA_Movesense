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
						access_token : token,
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


  router.post("/teste", (req, res, next) => {
	


	return res.status(401).send({
		msg: "Username or password is incorrect!",
	});
});
module.exports = router;
