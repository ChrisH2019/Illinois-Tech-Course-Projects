const express = require('express');
const path = require('path');
const logger = require('morgan');
const cookieParser = require('cookie-parser');
const bodyParser = require('body-parser');
const pg = require('pg');

// connect to the postgres server with our super secure
// authentication information
const client = new pg.Client({
	user: "dbadmin",
	host: "cs425.nickw.io",
	database: "cs425",
	password: "password",
	port: 5432
});

client.connect(() => console.log("Connected to DB"));

const app = express();

// uncomment after placing your favicon in /public
//app.use(favicon(path.join(__dirname, 'public', 'favicon.ico')));
app.use(logger('dev'));
app.use(bodyParser.json());
app.use(bodyParser.urlencoded({ extended: false }));
app.use(cookieParser());
app.use(express.static(path.join(__dirname, 'build')));

app.post("/query", async (req, res) => {
	const r = await client.query(req.body.text, req.body.values);
	res.send(r.rows);
});

app.get('*', (req, res) => {
  res.sendFile('build/index.html', { root: global });
});

// catch 404 and forward to error handler
app.use(function(req, res, next) {
  var err = new Error('Not Found');
  err.status = 404;
  next(err);
});

// error handler
app.use(function(err, req, res, next) {
  // set locals, only providing error in development
  res.locals.message = err.message;
  res.locals.error = req.app.get('env') === 'development' ? err : {};

  // render the error page
  res.status(err.status || 500);
  res.render('error');
});

module.exports = app;
