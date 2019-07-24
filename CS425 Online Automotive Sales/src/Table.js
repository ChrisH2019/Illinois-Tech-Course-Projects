import React, { Component } from 'react';
import { Table } from 'semantic-ui-react';

export default class Table_ extends Component {
	render() {
		return (
			<Table celled>
				<Table.Header>
					<Table.Row>
						{this.props.cols.map((col, i) => <Table.HeaderCell key={i}>{col}</Table.HeaderCell>)}
					</Table.Row>
				</Table.Header>


				<Table.Body>
					{this.props.rows.map((row, r) => {
						return (<Table.Row key={r}>
							{this.props.cols.map((col, c) => <Table.Cell key={c}>{row[col]}</Table.Cell>)}
							</Table.Row>)
					})}
				</Table.Body>
			</Table>
		);
	}
}
