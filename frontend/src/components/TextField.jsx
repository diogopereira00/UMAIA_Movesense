/* eslint-disable react/jsx-props-no-spreading */
import { FormControl, FormErrorMessage, FormLabel, Input, InputGroup, InputLeftElement, InputRightElement, Text } from "@chakra-ui/react";
import { faCircleExclamation } from "@fortawesome/free-solid-svg-icons";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import { useField } from "formik";
import React from "react";

const TextField = function Textf({ label, id, leftElement, rightElement, ...props }) {
	const [field, meta] = useField(props);

	return (
		<FormControl isInvalid={meta.error && meta.touched}>
			<FormLabel
				pt={3}
				onClick={() => {
					document.getElementById({ id }).focus();
				}}>
				{label}
			</FormLabel>

			<InputGroup size="lg">
				<InputLeftElement pointerEvents="none">{leftElement}</InputLeftElement>
				<Input {...field} {...props} />

				{rightElement !== null ? <InputRightElement>{rightElement}</InputRightElement> : ""}
			</InputGroup>
			<FormErrorMessage>
				<FontAwesomeIcon size="lg" icon={faCircleExclamation} />
				<Text fontWeight="bold" ml={2}>
					{meta.error}
				</Text>
			</FormErrorMessage>
		</FormControl>
	);
};

export default TextField;
