import React, { Component } from 'react';
import {
	Form,
	Container,
	Header,
} from 'semantic-ui-react'
import query from './query'
import md5 from 'md5'
import Register from './Register'

class Login extends Component {
  constructor(props) {
    super(props);
		this.state = {
			email: "",
			password: "",
			type: "customer"
		};
		this.cb = props.cb;
	}



	componentDidMount() {
	}

	submit = async () => {
		if (this.state.type === "customer" || this.state.type === "dealer") {
			const res = await query(`
				SELECT * FROM ${this.state.type}s
				WHERE (email = $1 AND password = $2);
				`, [this.state.email, md5(this.state.password)])
			if (res.length !== 0) {
				const user = res[0];
				delete user.password;
				user.type = this.state.type;
				this.cb(user);
			} else {
				alert("unknown password");
			}
		} else {
			alert("unknown account type")
		}
	}

	handleChange = (e, { name, value }) =>
		this.setState({ [name]: value })

	typechange = (e, {value}) =>
		this.setState({type: value});

  render() {
		return (
			<Container text>
				<br/>
				<br/>
				<Header as="h1" textAlign="center">Login</Header>
				<Form onSubmit={this.submit} >


					<Form.Field>
						<Form.Input name="email" onChange={this.handleChange} placeholder="Email" value={this.state.email} />
					</Form.Field>


					<Form.Field>
						<Form.Input name="password" onChange={this.handleChange} placeholder="Password" type="password" value={this.state.password} />
					</Form.Field>


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
									onChange={this.handleChange}
								/>
							</Form.Field>
						)
					})}

				<Form.Field>
					<Form.Button content="Login" />
				</Form.Field>

				</Form>
				<Register cb={this.cb} />
    </Container>
    );
  }
}

export default Login;
