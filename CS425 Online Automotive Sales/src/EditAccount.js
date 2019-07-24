import React, { Component } from 'react';
import {
	Form,
  Container,
  Header,
} from 'semantic-ui-react'
import query from './query'
import md5 from 'md5'

export default class EditAccount extends Component {
  constructor(props) {
    super(props);
		this.state = {
			account: props.account,
			fields: [],
		};
		console.log(this.state)
		this.cb = props.cb;
	}

	fields = {
		"dealer": ["dealer_name", "street_number", "street_name", "apt_number", "city", "state", "zip", "phone", "parking_spaces", "email"],
		"customer": ["customer_name", "street_number", "street_name", "apt_number", "city", "state", "zip", "phone", "annual_income", "email"],
	}

	submit = async () => {
		const { account } = this.state;
		const columns = this.fields[this.state.account.type];
		const values = columns.map(c => account[c]);


		await query(`
			UPDATE ${this.state.account.type}s
			SET ${columns.map((col, i) => col + " = $" + (i + 1)).join(", ")}
			WHERE ${this.state.account.type}_id = '${this.state.account.type === "dealer" ? this.state.account.dealer_id : this.state.account.customer_id}';
			`, values)
	}


	handleChange = (e, { name, value }) =>
		this.setState({
			account: {
				...this.state.account,
				[name]: value
			}
		})

  render() {
		return (
			<Container text>
				<hr />
				<Header as="h1" textAlign="center">Edit Account</Header>
				<Form onSubmit={this.submit} >



					{this.fields[this.state.account.type].map(field => {
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
					<Form.Button content="Save Changes" />
				</Form.Field>
				</Form>
    </Container>
    );
  }
}
