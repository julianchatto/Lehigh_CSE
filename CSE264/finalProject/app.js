// Julian Chattopadhyay juc226

const express = require("express");
const path = require("path");
const fs = require("fs");
const app = express();

let data;

app.use(express.static(
  path.resolve(__dirname, "public")
));

/**
 * Get routes for reciving a win
 */
app.get("/win", (req, res) => {
	const result = req.query;
	let found = false; // checking if the board with rows/cols/mines already exists
	for (let i = 0; i < data.length; i++) {	
		if (data[i].row == result.rows && data[i].col == result.cols && data[i].mines == result.mines) {
			if (((Number)(data[i].time)) > ((Number)(result.time))) { // checks if there is a faster time
				data[i].time = result.time;
			}
			found = true;
		}
	}
	if (found) {
		fs.writeFile('./records.txt', getDataAsString(), (err) => {}); // updates the file if there is a new record 
	} else { // if the board does not exist, add it to the records
		data.push({row:result.rows, col:result.cols, mines:result.mines, time:result.time});
		fs.appendFile('./records.txt', `${result.rows}|${result.cols}|${result.mines}|${result.time}\r\n`, (err) => {
			if (err) {
				console.error(err);
			}
		});
	}
    res.json(data);
});
/**
 * Function to return the data as a string
 * @returns data as a string
 */
function getDataAsString() {
	let ret = '';
	data.forEach((record) => {
		ret += `${record.row}|${record.col}|${record.mines}|${record.time}\r\n`;
	});
	return ret;

}

/**
 * Get route for records
 */
app.get("/records", (req, res) => {
	getRecords((err, results) => {
		if (err) {
			console.error(err);
		} else {
			data = results;
			
			res.json(data);
		};
	});
});

/**
 * Function to get records from the file
 * @param {} callback 
 */
function getRecords(callback) {
	let ret = [];
	fs.readFile('./records.txt', 'utf8', (err, data) => {
		if (err) {
			return 'Error getting records:';
		}
		
		data.split("\r\n").forEach(line => {
			if (line) {
				const parts = line.split('|');
				ret.push({row: parts[0], col: parts[1], mines: parts[2], time: parts[3]});
			}
		});
		callback(null, ret); // Callback with the result
	});
}

app.listen(3000, () => console.log("Starting Minesweeper on http://localhost:3000"));