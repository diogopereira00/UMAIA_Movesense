
const userMiddleware = require("../middleware/users.js");

const router = require("express").Router();

const { register, login } = require("../controllers/userController");
const { userStudies, options, questionsOptions, questionTypes, studiesAllInfoUser, surveysUser, addUserSurvey, teste } = require("../controllers/surveyController");
const { addAccData, addGyroData, addMagnData, addECGData, addHRData, addTempData } = require("../controllers/sensorsController");
const { studyVersion } = require("../controllers/surveyController");


router.post("/sign-up", userMiddleware.validateRegister, register)
router.post("/login", login)


router.get('/studies/:user_id', userMiddleware.isLoggedIn, userStudies)
router.get('/options', userMiddleware.isLoggedIn, options)
router.get('/questions/options', userMiddleware.isLoggedIn, questionsOptions)
router.get('/questionsTypes', userMiddleware.isLoggedIn, questionTypes)
router.get('/studies/allInfo/:user_id', userMiddleware.isLoggedIn, studiesAllInfoUser)
router.get('/surveys/:user_id', userMiddleware.isLoggedIn, surveysUser)
router.get('/studies/:study_id/version', userMiddleware.isLoggedIn, studyVersion)
router.post("/studies/add/userSurveys", userMiddleware.isLoggedIn, addUserSurvey)
router.post("/teste", userMiddleware.isLoggedIn, teste)


router.post("/addAccData", userMiddleware.isLoggedIn, addAccData)
router.post("/addGyroData", userMiddleware.isLoggedIn, addGyroData)
router.post("/addMagnData", userMiddleware.isLoggedIn, addMagnData)
router.post("/addECGData", userMiddleware.isLoggedIn, addECGData)
router.post("/addTempData", userMiddleware.isLoggedIn, addTempData)
router.post("/addHRData", userMiddleware.isLoggedIn, addHRData)
module.exports = router;
