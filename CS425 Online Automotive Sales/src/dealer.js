import React, { Component } from 'react';
import { Container, Header, Button, Select, Input } from 'semantic-ui-react';
import Table from './Table'
import query from './query'
import EditAccount from './EditAccount'

export default class CustomerView extends Component {
	constructor(props) {
		super(props);
		this.state = {
			account: props.account,
			inventories: null,

			sold: null,

			brands: null,
			models: null,
			options: null,

			new_brand_id: "",
			new_model_id: "",
			new_option_id: "",
			new_number: 10,
		}
	}



	update_data = async _ => {
		const { account } = this.state;
		const inventories = await query(`
			SELECT *
			FROM has_inventories h
			NATURAL JOIN inventories
			NATURAL JOIN brands
			NATURAL JOIN models
			NATURAL JOIN dealers
			NATURAL JOIN options
			WHERE dealer_id = $1;
		`, [account.dealer_id]);

		const sold = await query(`
			SELECT *
			FROM has_vehicles
			NATURAL JOIN customers
			NATURAL JOIN vehicles
			NATURAL JOIN brands
			NATURAL JOIN models
			NATURAL JOIN options
			WHERE dealer_id = $1;
		`, [this.state.account.dealer_id])

		console.log(sold)
		const options = await query(`SELECT * FROM options;`);
		const brand_models = await query(`SELECT * FROM has_models NATURAL JOIN models NATURAL JOIN brands;`)
		const newstate = { inventories, options, brand_models, sold };
		this.setState(newstate);
	}


	async componentDidMount() {
		this.update_data();
	}


	free_spaces = () =>
		this.state.inventories.reduce((c, i) => c - i.amount, this.state.account.parking_spaces)


	buyMore = async inv => {
		let amount = NaN;

		do {
			const resp = prompt("how many cars?", "0")
			amount = parseInt(resp, 10)
			if (isNaN(amount)) alert("Please enter a number");
		} while (isNaN(amount));

		const current_spaces = this.free_spaces();

		if (current_spaces - amount < 0) {
			alert("you cannot buy any more cars. Not enough spaces!");
			return;
		}

		await query(`
			UPDATE inventories
				SET amount = amount + $1
			WHERE inventory_id = $2;
		`, [amount, inv.inventory_id]);
		await this.update_data();
	}


	buyNew = async _ => {

		const { new_number, new_brand_id, new_model_id, new_option_id } = this.state;
		if (this.free_spaces() - new_number < 0) {
			alert("You dont have enough parking spaces")
			return
		}

		const inv = await query(`INSERT INTO inventories (amount) VALUES ($1) RETURNING *;`, [new_number])
		console.log(inv);
		await query(`
			INSERT INTO has_inventories
			(inventory_id, brand_id, model_id, option_id, dealer_id)
			VALUES ($1, $2, $3, $4, $5);
		`, [inv[0].inventory_id, new_brand_id, new_model_id, new_option_id, this.state.account.dealer_id]);
		this.update_data();
	}

	render() {
		const { account, inventories } = this.state;
		if (inventories === null) {

			return <div>Loading...</div>
		}

		const inv = inventories.map(i => {
			return {...i, actions: <Button content="Buy More" onClick={() => this.buyMore(i)}/>}
		});




		const selections = {}
		selections.options = this.state.options.map(opt => ({
			key: opt.option_id,
			value: opt.option_id,
			// make the text a bit more human readable
			text: opt.color + " exterior, " + opt.engine + " " + opt.transmission + " engine"
		}))

		selections.brand_models = this.state.brand_models.map(mod => ({
			key: "(" + mod.model_id + ", " + mod.brand_id + ")",
			value: mod,
			text: mod.brand_name + " " + mod.model_name
		}))


		console.log("sold", this.state.sold);

		return (
			<Container>
				<br/>
				<Header as="h1" content={"Dealer: " + account.dealer_name}/>
				<hr/>
				<Header as="h3" content={"Inventories"}/>
				<Table
					rows={inv}
					cols={['amount', 'brand_name', 'model_name', 'body_style', 'color', 'engine', 'transmission', 'actions']} />

				<Header as="h3" content={"Sold"}/>
				<Table
					rows={this.state.sold}
					cols={['vin', 'customer_name', 'brand_name', 'model_name', 'body_style', 'color', 'engine', 'transmission']} />

				<hr/>
				<Header as="h3" content={"Buy More Cars for your lot"}/>
				<Header as="h4" content={"Parking Spaces left: " + this.free_spaces()}/>

				<hr />


				<Select
					placeholder="Brand/Model"
					options={selections.brand_models}
					onChange={async (_, { value }) => {
						this.setState({
							new_brand_id: value.brand_id,
							new_model_id: value.brand_id,
						})
					}}
				/>
				<Select
					placeholder="Options"
					options={selections.options}
					onChange={async (_, { value }) => {
						console.log(value)
						this.setState({
							new_option_id: value,
						})
					}}
				/>

			<Input
				type="number"
				value={this.state.new_number}
				onChange={(_, {value}) => {
					this.setState({
						new_number: value
					})
				}}
			/>


			{this.state.new_brand_id !== "" &&
					this.state.new_model_id !== "" &&
					this.state.new_option_id !== "" &&
					this.state.new_number > 0 &&
					<Button content="Buy" onClick={this.buyNew}/>}

				<hr/>
				<EditAccount account={account} />
			</Container>
		)
	}
}
