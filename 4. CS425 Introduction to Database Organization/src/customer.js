import React, { Component } from 'react';
import { Container, Header, Button } from 'semantic-ui-react';
import Table from './Table'
import query from './query'
import EditAccount from './EditAccount'


export default class CustomerView extends Component {
	constructor(props) {
		super(props);
		this.state = {
			account: props.account,
			owned: null,
			options: null,
		}
	}


	updateData = async () => {
		const owned = await query(`
			SELECT *
			FROM has_vehicles
			NATURAL JOIN customers
			NATURAL JOIN vehicles
			NATURAL JOIN brands
			NATURAL JOIN models
			NATURAL JOIN options
			WHERE customer_id = $1;
		`, [this.state.account.customer_id])

		const options = await query(`
			SELECT *
			FROM has_inventories h
			NATURAL JOIN inventories
			NATURAL JOIN brands
			NATURAL JOIN models
			NATURAL JOIN dealers
			NATURAL JOIN options
			WHERE amount > 0;
		`);

		this.setState({ owned, options });
	}

	componentDidMount() {
		this.updateData();
	}


	buyVehicle = async vehicle => {
		function makevin() {
			var text = "";
			var possible = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";

			for (var i = 0; i < 17; i++)
				text += possible.charAt(Math.floor(Math.random() * possible.length));

			return text;
		}

		const new_vehicle = {
			'vin': makevin(),
			'brand_id': vehicle.brand_id,
			'model_id': vehicle.model_id,
			'option_id': vehicle.option_id,
			'manufacturer_id': vehicle.manufacturer_id,
			'customer_id': this.state.account.customer_id,
			'dealer_id': vehicle.dealer_id,
			'inventory_id': vehicle.inventory_id
		}


		const columns = Object.keys(new_vehicle);
		const col_vals = columns.map(c => new_vehicle[c]);
		await query(`
			INSERT INTO vehicles (${columns.join(", ")})
			VALUES (${columns.map((_, i) => "$" + (i+1)).join(", ")});
		`, col_vals)


		await query(`
			INSERT INTO has_vehicles (customer_id, vin)
			VALUES ($1, $2);
		`, [new_vehicle.customer_id, new_vehicle.vin])

		await query(`
			UPDATE inventories SET amount = amount - 1
			WHERE inventory_id = $1;
		`, [vehicle.inventory_id])

		await this.updateData();
	}

	render() {
		const { account, owned, options } = this.state;
		if (owned === null || options === null) {
			return <div>Loading...</div>
		}

		const cars = options.map(i => {
			return {...i, actions: <Button content="Purchase" onClick={() => this.buyVehicle(i)}/>}
		});

		return (
			<Container>
				<br/>
				<Header as="h1" content={"Customer: " + account.customer_name}/>
				<hr/>
				<Header as="h3" content="Your vehicles"/>
				<Table
					rows={owned}
					cols={['vin', 'brand_name', 'model_name', 'body_style', 'color', 'engine', 'transmission']} />
				<hr/>

				<Header as="h3" content="Purchase a vehicle"/>
				<Table
					rows={cars}
					cols={['amount', 'dealer_name', 'brand_name', 'model_name', 'body_style', 'color', 'engine', 'transmission', 'actions']} />

				<hr/>
				<EditAccount account={account} />
			</Container>
		);
	}
}
