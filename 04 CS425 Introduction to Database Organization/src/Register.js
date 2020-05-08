import React, { Component } from 'react';
import {
	Form,
  Container,
  Header,
} from 'semantic-ui-react'
import query from './query'
import md5 from 'md5'

export default class Register extends Component {
  constructor(props) {
    super(props);
		this.state = {
			account: {},
			fields: [],
			type: "customer"
		};
		this.cb = props.cb;
	}

	submit = async () => {
		const { account } = this.state;
		const columns = Object.keys(account);
		const values = columns.map(c =>
			c === "password" ? md5(account[c]) : account[c]);
		await query(`
			INSERT INTO ${this.state.type}s
			(${columns.join(", ")})
			VALUES (${values.map((v, i) => "$" + (i+1)).join(", ")})
		`, values)
		window.location.reload();
	}


	updatetype = async type => {
		console.log(type)

		const fields = {
			"dealer": ["dealer_name", "email", "password"],
			"customer": ["customer_name", "email", "password"],
		}
		this.state.account = {}
		const newa = {};
		fields[type].forEach(f => newa[f] = null)
		this.setState({account: newa, type, fields: fields[type]})
	}


	handleChange = (e, { name, value }) =>
		this.setState({
			account: {
				...this.state.account,
				[name]: value
			}
		})


	typechange = (e, {value}) =>
		this.updatetype(value)


	componentDidMount() {
		this.updatetype("customer");
	}

  render() {
		return (
			<Container text>
				<hr />
				<Header as="h1" textAlign="center">Register</Header>
				<Form onSubmit={this.submit} >



					{this.state.fields.map(field => {
						return (
							<Form.Field>
								<Form.Input
									key={field}
									type={field === "password" ? "password" : "text"}
									name={field}
									onChange={this.handleChange}
									placeholder={field}
									value={this.state.account[field]} />
							</Form.Field>
						);
					})}


					<Form.Field>
						Account Type:
					</Form.Field>

					{["customer", "dealer"].map(type => {
						return (
							<Form.Field>
								<Form.Radio
									label={type}
									name="type"
									value={type}
									checked={this.state.type === type}
									onChange={this.typechange}
								/>
							</Form.Field>
						)
					})}

				<Form.Field>
					<Form.Button content="Register" />
				</Form.Field>
				</Form>
    </Container>
    );
  }
}
