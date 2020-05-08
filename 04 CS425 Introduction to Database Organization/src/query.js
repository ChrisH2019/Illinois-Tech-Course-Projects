import axios from 'axios'

export default async function(text, values) {
	const { data } = await axios.post('/query', {text: text.trim(), values})
	return data;
}
