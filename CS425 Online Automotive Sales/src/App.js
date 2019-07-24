import React, { Component } from 'react';
import Login from './Login'

import CustomerView from './customer'
import DealerView from './dealer'

class App extends Component {
  constructor() {
    super();
		this.state = {
			account: null
		};
  }

	componentDidMount() {
	}

	loginCallback = account => {
		console.log(account)
		this.setState({account});
	}

	render() {

		const { account } = this.state;
		if (account == null) {
			return <Login cb={this.loginCallback} />
		}


		if (account.type === "customer") {
			return <CustomerView account={account} />
		}

		if (account.type === "dealer") {
			return <DealerView account={account} />
		}

    return (
			<div className="App">
				<pre>{JSON.stringify(account, null, 2)}</pre>
      </div>
    );
  }
}

export default App;
