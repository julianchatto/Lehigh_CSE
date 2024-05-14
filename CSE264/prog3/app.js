// Julian Chattopadhyay juc226

const express = require("express");
const path = require("path");
const td = new Date();

var month = td.getMonth() + 1;
var year = td.getFullYear();

const app = express();

app.set("views", path.resolve(__dirname, "views"));
app.set("view engine", "ejs");

app.use(express.static(
  	path.resolve(__dirname, "public")
));


function genCalendar(m, y, req, res) {
	const monthNames = ["", "January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"];
	month = m;
	year = y;

	// If your feeling clever, come up with a more streamlined way to write this function
	function calcLastDayOfMonth(m) {
		let lastDay = 0;
		if (m === 4 || m === 6 || m === 9 || m === 11)
		lastDay = 30;
		else if (m !== 2)
		lastDay = 31;
		else if (year % 4 === 0 && (year % 100 !== 0 || year % 400 === 0))
		lastDay = 29;
		else
		lastDay = 28;
		return lastDay;
	}

	function isToday(m,d,y) {
		const today = new Date();
		return m == today.getMonth()+1 && y == today.getFullYear() && d == today.getDate();
	}

	function getDayOfWeek(m, d, y) {
		return new Date(y, m - 1, d).getDay() + 1;
	}

	/* Code to create calendar rows and cells goes here */
	

	var startDay = getDayOfWeek(month, 1, year);
	var numDays = calcLastDayOfMonth(month);
	var newRow = 0;
	var calendar_string = "<tr>";
	
	for (let i = 1; i < startDay; i++, newRow++) { // Add empty cells for the days before the first of the month
		calendar_string += "<th></th>";
	}
	for (let i = 1; i <= numDays; i++, newRow++) { // Add cells for the days of the month
		if (newRow % 7 === 0) { // Start a new row every 7 days
			calendar_string += "</tr><tr>";
		}
		if (isToday(month, i, year)) { // Highlight today's date
			calendar_string += `<td class="today">${i}</td>`;
		} else {
			calendar_string += `<td>${i}</td>`;
		}
	}
	while (newRow % 7 !== 0) { // Add empty cells for the days after the last of the month
		calendar_string += "<td></td>";
		newRow++;
	}

	// Close the last row
	calendar_string += "</tr>";

	const header_string = monthNames[month] + " " + year;

	res.render("index", {
		header: header_string,
		calendar: calendar_string
	});
}

app.get("/calendar", function(req, res) {
	if (req.query.month && req.query.year) {
		var newMonth = parseInt(req.query.month);
		var newYear = parseInt(req.query.year);
		if (newMonth < 1 || newMonth > 12) {
			newMonth = td.getMonth() + 1;
			newYear = td.getFullYear();
		}
		genCalendar(newMonth, newYear, req, res);
	} else {
		genCalendar(td.getMonth() + 1, td.getFullYear(), req, res);
	}
});

app.get("/backmonth", function(req, res) {
	if (month === 1) {
		genCalendar(12, year - 1, req, res);
	} else {
		genCalendar(month - 1, year, req, res);
	}
});

app.get("/forwardmonth", function(req, res) {
	if (month === 12) {
		genCalendar(1, year + 1, req, res);
	} else {
		genCalendar(month + 1, year, req, res);
	}
});

app.get("/backyear", function(req, res) {
	genCalendar(month, year - 1, req, res);
});

app.get("/forwardyear", function(req, res) {
	genCalendar(month, year + 1, req, res);
});

app.listen(3000, function() {
	console.log("View Calendar: http://localhost:3000/calendar");
});