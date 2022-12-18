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

router.get('/studies/:user_id', userMiddleware.isLoggedIn, (req, res, next) => {
	var userID = req.params.user_id;
	console.log(userID)
	var studie = db.query(`SELECT * FROM user_studies WHERE user_id =${db.escape(userID)}`, (err, result) => {
		if (err) {
			throw err;
			return res.status(400).send({
				msg: err,
			});
		}
		if (!result.length) {
			return res.status(401).send({
				msg: "Não existe nenhum user com esse id",
			});
		}

		console.log(result[0]);

		return res.status(200).send({
			studiesId: result.map(res => res.study_id),
		});
	})

});
//Retornatodos os estudos e dados dos estudos do user
router.get('/studies/allInfo/:user_id', userMiddleware.isLoggedIn, (req, res, next) => {
	var userID = req.params.user_id;
	console.log(userID)
	var studie = db.query(`SELECT studies.id as 'studies_id', studies.name as 'studies_name', studies.description as 'studies_description',studies.start_date as 'studies_startdate', studies.end_date as 'studies_enddate', studies.version as 'studies_version',
	surveys.id as 'surveys_id', surveys.title as 'surveys_title', surveys.description as 'surveys_description', surveys.expected_time as 'surveys_expected_time', surveys.created_at as 'surveys_created_at', surveys.updated_at as 'surveys_updated_at',
	sections.id as 'sections_id', sections.name as 'sections_name', 
	questions.id as 'questions_id', questions.text as 'questions_text', questions.question_type_id as 'questions_type_id', 
	question_options.*
	FROM user_studies
	INNER JOIN studies ON user_studies.study_id = studies.id
	INNER JOIN surveys ON studies.id = surveys.study_id
	INNER JOIN sections ON surveys.id = sections.survey_id
	INNER JOIN questions ON sections.id = questions.section_id
	INNER JOIN question_options ON question_options.question_id = questions.id
	WHERE user_studies.user_id = ${db.escape(userID)}
	`, (err, result) => {
		if (err) {
			throw err;
			return res.status(400).send({
				msg: err,
			});
		}
		if (!result.length) {
			return res.status(401).send({
				msg: "Não existe nenhum user com esse id",
			});
		}

		console.log(result[0]);
		// Execute the query (assuming the result is stored in a variable called "result")
		const studies = new Map();
		const optionsMap = new Map();

		for (const row of result) {
			console.log(row);
			const { studies_id, studies_name, studies_description, studies_startdate, studies_enddate, studies_version, surveys_id, surveys_title, surveys_description, surveys_expected_time, surveys_created_at, surveys_updated_at, name,
				sections_id, sections_name, questions_id, questions_text, questions_type_id, start_time, end_time, version, title, expected_time, created_at, updated_at, question_id, text, question_type_id, section_id, section_name, option_id } = row;

			if (!studies.has(studies_id)) {
				studies.set(studies_id, {
					study_id: studies_id,
					study_name: studies_name,
					study_description: studies_description,
					study_description: studies_startdate,
					study_description: studies_enddate,
					study_version: studies_version,
					surveys: []
				});
			}

			let survey = studies.get(studies_id).surveys.find(survey => survey.id === surveys_id);
			if (!survey) {
				survey = {
					surveys_id: surveys_id,
					survey_title: surveys_title,
					survey_description: surveys_description,
					survey_expected_time: surveys_expected_time,
					survey_created_at: surveys_created_at,
					survey_updated_at: surveys_updated_at,
					sections: []
				};
				studies.get(studies_id).surveys.push(survey);
			}

			let section = survey.sections.find(section => section.id === section_id);
			if (!section) {
				section = {
					section_id: sections_id,
					section_name: sections_name,
					questions: []
				};
				survey.sections.push(section);
			}

			// Check if the options map has an entry for the current question
			if (!optionsMap.has(question_id)) {
				optionsMap.set(question_id, new Set());
			}

			// Add the option to the set, if an option with the same id does not already exist in the set
			const optionsSet = optionsMap.get(question_id);
			if (!optionsSet.has(option_id)) {
				optionsSet.add(option_id);
			}

			// Convert the set to an array and assign it to the options property of the question object
			const options = Array.from(optionsSet).map(id => ({ option_id: id }));
			const newQuestion = {
				question_id: questions_id,
				question_text: questions_text,
				question_type_id: questions_type_id,
				options
			};

			// Check if the question already exists in the "questions" array
			const existingQuestion = section.questions.find(q => q.question_id === questions_id);
			if (existingQuestion) {
				// Update the options of the existing question
				existingQuestion.options = options;
			} else {
				// Add the new question to the "questions" array
				section.questions.push(newQuestion);
			}

		}

		// Convert the map to an array and return it as a JSON object
		res.send(Array.from(studies.values()));

	})

});

router.get('/surveys/:user_id', userMiddleware.isLoggedIn, (req, res, next) => {
	var userID = req.params.user_id;
	console.log(userID)

	var query = `SELECT user_surveys.user_id, studies.name, survey_id, surveys.study_id, surveys.title, surveys.description, user_surveys.start_time, user_surveys.end_time, user_surveys.isCompleted, surveys.expected_time FROM user_surveys 
	INNER JOIN surveys on user_surveys.survey_id = surveys.id 
	INNER JOIN studies on studies.id = surveys.study_id 
	WHERE user_surveys.user_id =${db.escape(userID)}`

	//ta a retornar user_id, name, survey_id, title, description, start_time, end_time, expected_time

	var user = db.query(query, (err, result) => {
		if (err) {
			throw err;
			return res.status(400).send({
				msg: err,
			});
		}
		if (!result.length) {
			return res.status(401).send({
				msg: "Não existe nenhum user com esse id",
			});
		}

		console.log(result[0]);




		return res.status(200).send({ surveys: result });
	})

});




router.get('/secret-route', userMiddleware.isLoggedIn, (req, res, next) => {
	console.log(req.userData);
	res.send('This is the secret content. Only logged in users can see that!');
});


router.post("/addAccData", userMiddleware.isLoggedIn, (req, res, next) => {
	console.log(req.headers)
	let accs = JSON.parse(req.body.jsonString);




	db.query(`INSERT IGNORE INTO acc_table (id_on_phone, user_id, x, y, z, timestamp, created_at) VALUES ?`,
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

	db.query(`INSERT IGNORE INTO gyro_table (id_on_phone, user_id, x, y, z, timestamp, created_at) VALUES ?`,
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

	db.query(`INSERT IGNORE INTO magn_table (id_on_phone, user_id, x, y, z, timestamp, created_at) VALUES ?`,
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

	db.query(`INSERT IGNORE INTO ecg_table (id_on_phone, user_id, data, timestamp, created_at) VALUES ?`,
		[ecgs.map(ecg => [ecg.id, ecg.userID, ecg.data, ecg.timestamp, new Date(ecg.created).toISOString().slice(0, 19).replace('T', ' ')])],
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

	db.query(`INSERT IGNORE INTO hr_table (id_on_phone, user_id, average, rr_data, created_at) VALUES ?`,
		[hrs.map(hr => [hr.id, hr.userID, hr.average, hr.rrData, new Date(hr.created).toISOString().slice(0, 19).replace('T', ' ')])],
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
