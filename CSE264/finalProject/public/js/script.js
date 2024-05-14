let canvas, ctx, imageArray;
const imagePath = "../images/";
// Array of image file names
const imageFiles = [
    "1.svg", // 0: 1 mine touching tile
    "2.svg", // 1: 2 mines touching tile
    "3.svg", // 2: 3 mines touching tile
    "4.svg", // 3: 4 mines touching tile
    "5.svg", // 4: 5 mines touching tile
    "6.svg", // 5: 6 mines touching tile
    "7.svg", // 6: 7 mines touching tile
    "8.svg", // 7: 8 mines touching tile
    "blank.svg", // 8: revealed tile
    "flag.svg", // 9: placed flag
    "mine.svg", // 10: mine shown if a mine is revealed from clicking a different mine
    "mine_red.svg", // 11: mine shown if a mine is clicked
    "mine_wrong.svg", // 12: mine shown if a flag is placed on a non-mine tile
    "notRevealed.svg", // 13: not revealed tile
    "clock-0.svg", // 14: clock 0
    "clock-1.svg", // 15: clock 1
    "clock-2.svg", // 16: clock 2
    "clock-3.svg", // 17: clock 3
    "clock-4.svg", // 18: clock 4
    "clock-5.svg", // 19: clock 5
    "clock-6.svg", // 20: clock 6
    "clock-7.svg", // 21: clock 7
    "clock-8.svg", // 22: clock 8
    "clock-9.svg", // 23: clock 9
    "nums_background.svg", // 24: background for the number images
    "clock--.svg", // 25: clock -
];
$(document).ready(function() {
	/**
	 * Ajax call to load the artirecords in the table
	 */
	$( () => {
		$.ajax(
            "/records",
            {
                type: "GET",
                url: "/records",
                success: function(data) {
                    fillRecordsTable(data);
                }
            }
        );
	});

});
/**
 * Function to create a promise to create all the images
 * @param {String} src for the source of the string 
 * @returns a promise for the specified image
 */
function loadImage(src) {
    return new Promise((resolve, reject) => {
        const img = new Image();
        img.onload = () => resolve(img);
        img.onerror = () => reject(new Error("Failed to load image at " + src));
        img.src = src;
        img.width = 50;
        img.height = 50;
    });
}

// Map each image file name to a full path and wrap it with the loadImage function
const imagePromises = imageFiles.map((file) => loadImage(imagePath + file));

// Use Promise.all to wait for all images to be loaded before proceeding
let loadedImages = Promise.all(imagePromises)
    .then((images) => {
        // All images are loaded and can be used here;
        return images;
    }).catch((error) => {
        console.error("Error loading images", error);
    });
let cols, rows, mines;
document.addEventListener("DOMContentLoaded", function () {
    canvas = document.getElementById('board');
    ctx = canvas.getContext('2d');
    
    // set the size of the canvas
    ctx.canvas.width = window.screen.width * 0.8;
    ctx.canvas.height = window.innerHeight;

    // set the size of the input fields
    cols = document.getElementById('columns');
    cols.min = 6;
    cols.max = Math.floor(window.screen.width*.8/50);
    rows = document.getElementById('rows');
    rows.min = 1;
    rows.max = Math.floor(window.innerHeight/50) - 2;
    mines = document.getElementById('mines');
    mines.min = 1;
    mines.max = Math.floor((window.screen.width*.8/50) * (window.innerHeight/50)) - 1;


    loadedImages.then(images => {
        imageArray = images;
        mineSweeper(); // start the game
    }).catch(error => {
        console.error("Error while loading images after DOM loaded", error);
    });

});

let board_with_mines = [];
let board = []; 
let mineCount = 10;
let minesLeft = mineCount;
let numRows = 9;
let numCols = 9;
let numTiles = 81;
let selectedImage = [-1, -1];
let set = false;
let ones = 0;
let tens = 0;
let hundreds = 0;
let timerInterval = null;
let canPlay = true;
let records;
let seconds = 0; // Record start time

function mineSweeper() {
    generateBoard([-1, -1]);
    // Draw the image on the canvas
    drawBoard();
    // canvas event listeners
    canvas.addEventListener('mousedown', function(event) {
        if (event.button !== 0) return; // if there is a right click skip the handler
        if (!canPlay) return; // if the user has won/lost do nothing
        // calculate the selected image coordinates
        if (event.clientY <= 100) return; // if the user clicks outside of the board
        selectedImage[0] = Math.floor((event.clientY - 100) / 50);
        selectedImage[1] = Math.floor(event.clientX / 50); 
        if (selectedImage[0] >= numRows || selectedImage[1] >= numCols) return; // if the user clicks outside of the board
        if (!set) { // first move
            set = true;
            generateBoard(selectedImage);
            startClock();
            drawBoard();
        }

        const board_val = board[selectedImage[0]][selectedImage[1]] 
        if (board_val === 9 || board_val === 8) return; // if there is flag or is already blank do nothing.

        let val = board_with_mines[selectedImage[0]][selectedImage[1]]; // get the value of the selected image (see if there is a bomb)
        if (val === 13) { // selected cell is not a mine
            updateBoard();
            if (isBoardSolved()) { // board is solved
                stopClock(false);
                minesLeft = 0;
                finishBoard(); // finish the board
                sendWin(); // send win to server
            }
        } else if (val === 10) { // user clicked on a mine
            board[selectedImage[0]][selectedImage[1]] = 11; // mine
            stopClock(false); // stop the clock
            canPlay = false;
            endGame();
        }
    });
    canvas.addEventListener('contextmenu', function(event) {
        event.preventDefault();
        if (!canPlay) return; // if the use has won/lost do nothing
        if (event.clientY <= 100) return; // if the user clicks outside of the board

        // calculate the selected image coordinates
        selectedImage[0] = Math.floor((event.clientY - 100) / 50);
        selectedImage[1] = Math.floor(event.clientX / 50); 
        const board_val = board[selectedImage[0]][selectedImage[1]]; // get the value of the selected image 

        if (board_val == 13) { // if the cell is not revealed
            board[selectedImage[0]][selectedImage[1]] = 9; 
            minesLeft--;
        } else if (board_val == 9) { // if the cell is a flag
            board[selectedImage[0]][selectedImage[1]] = 13; 
            minesLeft++;
        }

        drawBoard();
    });
    document.getElementById('newBoard').addEventListener('click', function() {
        numRows = (Number) (rows.value);
        numCols = (Number) (cols.value);
        // validate the input
        if (numCols == null || numCols == '' || numCols < 6) {
            numCols = 6;
        } 
        if (numCols > (Number) (cols.max)) {
            numCols = (Number) (cols.max);
        }
        if (numRows === null || numRows === '' || numRows < 1) {
            numRows = 1;
        }
        if (numRows > (Number) (rows.max)) {
            numRows = (Number) (rows.max);
        }
        numTiles = numRows * numCols;
        mineCount = ((Number)(mines.value));
        if (mineCount == null || mineCount == '' ||  mineCount < 1 ) {
            mineCount = 1;
        }
        if (mineCount > numTiles) {
            mineCount = 1;
        }
        minesLeft = mineCount;
        stopClock(true);
        generateBoard([-1, -1]);
        drawBoard();
        set = false;
        canPlay = true;
        fillRecordsTable(records);
    });
    
}

/**
 * Function to generate random board 
 * @param {Number[]} clickedTile for the tile that was clicked 
 * if the tile is -1 then the board is generated without mines because we want to wait for the first click
 */
function generateBoard(clickedTile) {
    if (clickedTile[0] === -1) {
        board = [];
        board_with_mines = [];
        for (let i = 0; i < numRows; i++) {
            let arr = [];
            let arr_mines = [];
            for (let j = 0; j < numCols; j++) {
                arr.push(13);
                arr_mines.push(13);
            }
            board.push(arr);
            board_with_mines.push(arr_mines);
        }
        return;
    }

    let mines = mineCount;
    if (mines >= numTiles) {
        mines = numTiles - 1;
        mineCount = mines;
    }
    while (mines) {
        let x = (Number) (Math.floor(Math.random() * numRows));
        let y = (Number) (Math.floor(Math.random() * numRows));
        if (board_with_mines[x][y] === 13 && x !== clickedTile[0] && y !== clickedTile[1]) {
            board_with_mines[x][y] = 10;
            mines--;
        }
    }
}

/**
 * Function send the win to the server
 */
function sendWin() {
    
    let datatosend = {
                rows: numRows,
                cols: numCols,
                mines: mineCount,
                time: ones + tens * 10 + hundreds * 100
            }
    console.log(datatosend);

    $.ajax(
        "/win",
        {
            type: "GET",
            url: "/win",
            data: datatosend,
            success: function(data) {
                fillRecordsTable(data);
            }
        }
    );
}

/**
 * Function to fill the records table with the data
 * @param {String} data for the data from the server 
 */
function fillRecordsTable(data) {
    records = data;
    let table = document.getElementById('recordsTableContent');
    table.innerHTML = '';
    let input = "";
    records.forEach((row) => {
        if (((Number)(row.row))=== numRows && ((Number)(row.col)) === numCols && ((Number)(row.mines)) === mineCount) { // current board 
            input += `<tr style="background-color:pink;"><td>${row.row}</td><td>${row.col}</td><td>${row.mines}</td><td>${row.time}</td></tr>`;
        } else {
            input += `<tr><td>${row.row}</td><td>${row.col}</td><td>${row.mines}</td><td>${row.time}</td></tr>`;
        }
    });
    table.innerHTML = input;
}

/**
 * Function to draw the minesweeper board 
 * @param {Number[][]} board for the minesweeper board
 */
function drawBoard() {
    ctx.clearRect(0, 0, canvas.width, canvas.height);
    ctx.fillStyle = 'silver';
    let offset = numCols * 50;
    ctx.fillRect(0, 0, offset, 100);

    mines_ones = Math.abs(minesLeft % 10);
    mines_tens = Math.floor(Math.abs(minesLeft / 10)) % 10;
    mines_hundreds = Math.floor(Math.abs(minesLeft / 100)) % 10;
    if (seconds > 999) {
        ones = 9;
        tens = 9;
        hundreds = 9;
    }
    // if the number of flags is negative
    if (minesLeft < 0) {
        if (minesLeft < -99) {
            mines_ones = 9;
            mines_tens = 9;
            mines_hundreds = 11;
        } else if (minesLeft < -9) {
            mines_hundreds = 11;
        } else {
            mines_tens = 11;
            mines_hundreds = 0;
        }
    }
    // draw the timer and the number of flags/mines
    ctx.drawImage(imageArray[24], 0, 0, 150, 100);
    ctx.drawImage(imageArray[14 + mines_hundreds], 8, 8, 39, 85);
    ctx.drawImage(imageArray[14 + mines_tens], 56, 8, 39, 85);
    ctx.drawImage(imageArray[14 + mines_ones], 103, 8, 39, 85);
    ctx.drawImage(imageArray[24], offset - 150, 0, 150, 100);
    ctx.drawImage(imageArray[14 + hundreds], offset - 150 + 8, 8, 39, 85);
    ctx.drawImage(imageArray[14 + tens], offset - 150 + 56, 8, 39, 85);
    ctx.drawImage(imageArray[14 + ones], offset - 150 + 103, 8, 39, 85);
    for(var i = 0; i < numRows; i++) {
        for(var j = 0; j < numCols; j++) {
            ctx.drawImage(imageArray[board[i][j]], j*50, i*50 + 100 , 50, 50);
        }
    }
}

/**
 * Function to start the clock
 */
function startClock() {
    seconds = 0; // Record start time
    timerInterval = setInterval(() => {
        seconds++;
        ones = seconds % 10;
        tens = Math.floor(seconds / 10) % 10;
        hundreds = Math.floor(seconds / 100) % 10;
        drawBoard(); // Update the canvas with the current time
    }, 1000); // Update every second
}

/**
 * Function to stop the clock
 * @param {Boolean} newGame 
 */
function stopClock(newGame) {
    if (timerInterval) {
        clearInterval(timerInterval); // Stop the timer
        timerInterval = null; // Clear the interval ID
    }
    if (newGame) {
        ones = 0;
        tens = 0;
        hundreds = 0;
    } 
}


/**
 * Function to replace any mines that don't have a flag with
 * a flag. This function is called when the board is solved
 */
function finishBoard() {
    for(var i = 0; i < board.length; i++) {
        for(var j = 0; j < board[i].length; j++) {
            if (board_with_mines[i][j] === 10) {
                board[i][j] = 9;
            }
        }
    }
    drawBoard();
}

/**
 * Function to check if the board is solved
 * @returns true if the board is solved, false otherwise
 */
function isBoardSolved() {
    let count = 0; // count the number of revealed tiles
    for(var i = 0; i < board.length; i++) {
        for(var j = 0; j < board[i].length; j++) {
            if (board[i][j] !== 13 && board[i][j] !== 9) { // not a flag, number, or mine
                count++;
            } 
        }
    }
    if (count === (numTiles - mineCount)) {
        canPlay = false;
        return true;
    }
    return false;
}

/**
 * Function called in the event that the user presses a mine
 * Replaces any tiles that don't have a flag with a mine 
 * Replaces any incorrect flags with a red_flag 
 */
function endGame() {
    for(var i = 0; i < board.length; i++) {
        for(var j = 0; j < board[i].length; j++) {
            if (board[i][j] === 9 && board_with_mines[i][j] !== 10) { // if the cell is a flag and there is no mine
                board[i][j] = 12;
            } else if (board_with_mines[i][j] === 10 && board[i][j] !== 11 && board[i][j] !== 9) { // if the cell is a mine and it is not flagged and not the one that was clicked
                board[i][j] = 10;
            }
        }
    } 
    drawBoard();
}

/**
 * DFS function to reveal the board
 * Solved on https://www.leetcode.com
 */
function updateBoard() {
    /**
     * DFS function to reveal the board
     * @param {Number} i for the row
     * @param {Number} j for the col
     * @returns 
     */
    function dfs(i, j) {
        if (!board[i][j]) return;

        if (board_with_mines[i][j] === 10) {
            board[i][j] = 11;
            return;
        }
        if (board[i][j] !== 13) return;

        const mines = countMines(i, j); // Count the number of mines touching the cell
        
        if (mines) { 
            board[i][j] = mines - 1;
            return;
        } else {
            // If we haven't got mines, check another cell
            board[i][j] = 8;
            for (let x = Math.max(i - 1, 0); x < Math.min(i + 2, board.length); x++) {
                for (let y = Math.max(j - 1, 0); y < Math.min(j + 2, board[x].length); y++) {
                    dfs(x, y);
                }
            }
        }
    }

    /**
     * Function to determine the number of mines touching the cell
     * @param {Number} i for the row
     * @param {*} j for the col
     * @returns the number of mines touching the cell
     */
    function countMines(x, y) {
        let mines = 0;
        for (let i = Math.max(x - 1, 0); i < Math.min(x + 2, board.length); i++) {
            for (let j = Math.max(y - 1, 0); j < Math.min(y + 2, board[i].length); j++) {
                if (board_with_mines[i][j] === 10) {
                    mines++;
                }
            }
        }
        return mines;
    }

    dfs(selectedImage[0], selectedImage[1]);
    drawBoard();
}