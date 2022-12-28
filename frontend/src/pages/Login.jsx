import React, { useEffect } from "react";
import { Link, useNavigate } from "react-router-dom";
import axios from "axios";

import {
	Box,
	Button,
	Center,
	ChakraProvider,
	extendTheme,
	Flex,
	Heading,
	Image,
	InputLeftElement,
	InputRightElement,
	Text,
	VStack,
} from "@chakra-ui/react";
import { Formik } from "formik";
import { ViewIcon, ViewOffIcon } from "@chakra-ui/icons";
import * as Yup from "yup";
import { ToastContainer, toast } from "react-toastify";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import { faLock, faUser } from "@fortawesome/free-solid-svg-icons";
import { loginRoute } from "../utils/APIRoutes";
import logo from "../assets/logo.png";
import TextField from "../components/TextField";
import "react-toastify/dist/ReactToastify.css";

export default function Login() {
	const [show, setShow] = React.useState(false);
	const handleClick = () => setShow(!show);

	const navigate = useNavigate();
	const toastOptions = {
		position: "bottom-center",
		autoClose: 3000,
		closeOnClick: true,
		draggable: true,
		theme: "dark",
	};
	useEffect(() => {
		if (localStorage.getItem("user")) {
			navigate("/");
		}
	}, []);

	const theme = extendTheme({
		config: {
			useSystemColorMode: true,
			initialColorMode: "dark",
		},
	});

	async function validateUser(values) {
		console.log("in validation", loginRoute);
		let { username, password } = values;
		await axios
			.post(loginRoute, {
				username,
				password,
			})
			.then((response) => {
				console.log(response);
				const { data } = response;
				if (response.status === 200) {
					delete data.password;
					localStorage.setItem("user", JSON.stringify(data.user));
					navigate("/");
				}
			})
			.catch((error) => {
				try {
					if (error.response.status === 401) console.log(error);
					console.log("Erro");

					toast.error(error.response.data.msg, {
						position: "bottom-center",
						autoClose: 3000,
						hideProgressBar: false,
						closeOnClick: true,
						pauseOnHover: true,
						draggable: true,
						progress: undefined,
						theme: "dark",
					});
				} catch {
					console.log("Erro");
				}
			});

		// console.log(data);
		// if (data.status === true) {
		// 	delete data.password;
		// 	localStorage.setItem("user", JSON.stringify(data.user));
		// 	navigate("/");
		// }
		// if (data.status === false) {
		// 	// toast.error(data.msg, toastOptions);
		// 	toast.error(data.msg, toastOptions);
		// }
	}
	return (
		<>
			<ChakraProvider theme={theme}>
				<Formik
					initialValues={{ username: "", password: "" }}
					validationSchema={Yup.object({
						password: Yup.string(),
						email: Yup.string().email("Por favor, introduza um email válido."),
					})}
					onSubmit={(values, actions) => {
						// alert(JSON.stringify(values, null, 2));
						validateUser(values);
						console.log(actions);
						actions.setFieldValue("password", "");
					}}
					autoComplete="off">
					{(formik) => (
						<VStack as="form" mx="auto" w={{ base: "90%", md: 500 }} h="100vh" justifyContent="center" onSubmit={formik.handleSubmit}>
							<Box width="28rem" backgroundColor="#00000076" borderRadius="2rem" padding="3rem 3rem">
								<Center pb={7}>
									<Flex>
										<Image maxH="6rem" src={logo} />
										<Heading as="h4" size="xl" color="white" alignSelf="center">
											MOVESENSE
										</Heading>
									</Flex>
								</Center>
								<Box pb={7}>
									<TextField
										label="Username"
										values={formik.values.username}
										leftElement={
											<InputLeftElement pointerEvents="none">
												<FontAwesomeIcon size="1x" className="azul" icon={faUser} />
											</InputLeftElement>
										}
										fontWeight="medium"
										id="username"
										placeholder="Introduza o seu username"
										type="username"
										tabIndex={0}
										name="username"
									/>

									<TextField
										label="Palavra-Passe"
										values={formik.values.password}
										leftElement={
											<InputLeftElement pointerEvents="none">
												<FontAwesomeIcon size="1x" className="azul" icon={faLock} />
											</InputLeftElement>
										}
										rightElement={
											<InputRightElement width="4rem">
												<Button size="sm" onClick={handleClick}>
													{show ? <ViewOffIcon /> : <ViewIcon fontSize="1.2em" />}
												</Button>
											</InputRightElement>
										}
										fontWeight="medium"
										id="password"
										placeholder="Introduza a sua palavra-passe"
										type={show ? "text" : "password"}
										tabIndex={0}
										name="password"
									/>
								</Box>

								<Button
									w="full"
									bg="#0a72e7"
									color="white"
									rounded="md"
									_hover={{
										transform: "translateY(-2px)",
										boxShadow: "lg",
									}}
									type="submit">
									INICIAR SESSÃO
								</Button>
								<Center pt={5}>
									<Flex alignContent="center">
										<Text fontSize="md" fontWeight="medium">
											NÃO TENS CONTA?
										</Text>
										<Text fontSize="md" ml="0.3rem" fontWeight="bold" color="#00c6ff">
											<Link to="/register">REGISTA-TE</Link>
										</Text>
									</Flex>
								</Center>
							</Box>
						</VStack>
					)}
				</Formik>
			</ChakraProvider>
			<ToastContainer />
		</>
	);
}
