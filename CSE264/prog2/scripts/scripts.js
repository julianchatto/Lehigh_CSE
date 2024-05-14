/**
 * Global variables
 */
var cells = document.querySelectorAll('td'); // All cells in puzzle table
var blankLocation = 16; // location of the blank cell
const orgState = ["1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15", ""]; // Current state of the puzzle
const orgStateAsText = ["one", "two", "three", "four", "five", "six", "seven", "eight", "nine", "ten", "eleven", "twelve", "thirteen", "fourteen", "fifteen", "blank"];

/**
 *  Event listeners
 */

/**
 * Event listener for each cell's click action
 * 1. Remove highlighting from all cells
 * 2. Add highlighting to the clicked cell
 * 3. If the clicked cell is adjacent to the blank cell and the puzzle is solved, highlight all cells
 */
cells.forEach(function(cell) {
    cell.addEventListener('click', function() {
        if (!isSolved()) {
            var clickedIndex = parseInt(this.closest('tr').rowIndex) * 4 + parseInt(this.cellIndex) + 1; // from: https://stackoverflow.com/questions/45656949/how-to-return-the-row-and-column-index-of-a-table-cell-by-clicking

            if (isBlankAjacent(clickedIndex) && isSolved()) {
                highlightAll();   
            }
        }
    });
});

/**
 * Event listener for the reset button
 * 1. Remove highlighting from all cells (in the case that the puzzle was solved)
 * 2. Update the cells with the original state of the puzzle
 */
document.getElementById("reset").addEventListener("click", function() {
    removeHighlightAll();
    updateCells(orgState, orgStateAsText);
});

/**
 * Event listener for the scramble button
 * 1. Remove highlighting from all cells (in the case that the puzzle was solved)
 * 2. Randomize the current state of the puzzle
 * 3. Update the cells with the current state of the puzzle
 */
document.getElementById("scramble").addEventListener("click", function() {
    removeHighlightAll();
    
    // Fisher-Yates shuffle from: https://stackoverflow.com/questions/2450954/how-to-randomize-shuffle-a-javascript-array
    var newState = [...orgState];  // copy of the original state
    var newStateAsText = [...orgStateAsText];
    do {
        let currentIndex = orgState.length,  randomIndex;
        while (currentIndex > 0) {
            randomIndex = Math.floor(Math.random() * currentIndex);
            currentIndex--;
            [newState[currentIndex], newState[randomIndex]] = [newState[randomIndex], newState[currentIndex]];
            [newStateAsText[currentIndex], newStateAsText[randomIndex]] = [newStateAsText[randomIndex], newStateAsText[currentIndex]];
        }
        blankLocation = newState.indexOf("")  + 1; // update the location of the blank cell
    } while(!isSolvable(newState));
    updateCells(newState, newStateAsText);
});

/**
 * Function to update the cells with a new puzzle
 * @param {ArrayList<String>} arr for the array to change the cells to
 * @param {ArrayList<String>} arrAsText for the array to change the cells to as text
 */
function updateCells(arr, arrAsText) {
    for (let i = 0; i < 16; i++) {
        cells[i].classList.remove(cells[i].classList);
        cells[i].classList.add(arrAsText[i]);
        cells[i].innerHTML = arr[i];
        if (arr[i] == "") { // update the location of the blank cell
            blankLocation = i + 1;
        }
    }
}

/**
 * Function to highlight all cells in the puzzle indicating a win
 */
function highlightAll() {
    cells.forEach(function(cell) {
        cell.classList.add('highlight');
    });
}

/**
 * Function to remove highlighting from all cells in the puzzle
 */
function removeHighlightAll() {
    cells.forEach(function(cell) {
        cell.classList.remove('highlight');
    });
}

/**
 * Function to determine if the clicked cell is adjacent to the blank cell
 * 1. Get the adjacent cells to the clicked cell (getSwappable)
 * 2. If the blank cell is in the list of adjacent cells, swap the clicked cell with the blank cell
 * @param {Integer} clickedIndex for the clicked cell index
 * @returns true if the clicked cell is adjacent to the blank cell, false otherwise 
 */
function isBlankAjacent(clickedIndex) {
    var retVal = false;
    getSwappable(clickedIndex).forEach(function(index) {
        if (index == blankLocation) {
            swap(clickedIndex);
            retVal =  true;
        }
    });  
    return retVal
}

/**
 * Function to get the index of the adjacent cells to the clicked cell to where a cell could be moved
 * @param {Integer} index for the clicked cell index
 * @returns {Array<Integer>} for the array of the adjacent cells to the clicked cell
 */
function getSwappable(index) {
    if ((index - 1) % 4 == 0) { // left edge
        return [index - 4, index + 1, index + 4].filter(filterArrayBounds);
    } else if (index % 4 == 0) { // right edge
        return [index - 4, index - 1, index + 4].filter(filterArrayBounds);
    }
    return [index - 4, index - 1, index + 1, index + 4].filter(filterArrayBounds); // all other cells
}

/**
 * Function to filter the array bounds
 * @param {Integer} index for the cell index
 * @returns true if the index is within the bounds of the puzzle, false otherwise
 */
function filterArrayBounds(index) {
    return index > 0 && index < 17;
}

/**
 * Function to swap the blanked cell and clicked cell
 * @param {Integer} index of the clicked cell
 */
function swap(index) {
    var cell = cells[index - 1];
    var blank = cells[blankLocation - 1];
    cells[blankLocation - 1].innerHTML = cells[index - 1].innerHTML;
    cells[index - 1].innerHTML = '';
    blank.classList.remove('blank');
    blank.classList.add(cell.classList[0]);
    cell.classList.remove(cell.classList[0]);
    cell.classList.add('blank');
    blankLocation = index;
}

/**
 * Function to determine if the puzzle is solved
 * The puzzle is solved if the array is in the natural ordering [1-15] with the blank cell at the end
 * @returns true if the puzzle is solved, false otherwise
 */
function isSolved() {
    // return early if the blank cell is not last because it is not possible to be solved
    if(blankLocation != 16) { 
        return false;
    }
    // check if the cells are in natural order [1-15]
    var previous = parseInt(cells[0].innerHTML);
    for (let i = 1; i < 15; i++) {
        if (previous > parseInt(cells[i].innerHTML)) {
            return false;
        }
        previous++
    }
    return true;
}

/* The following code was written using the characteristics of a solvable board described: https://www.geeksforgeeks.org/check-instance-15-puzzle-solvable/ */

/**
 * Function to determine if a given board is solvable
 * A given board is solvable if the row from the bottom is even and the number of inversions is odd or vice versa
 * @param {ArrayList<String>} arr for the array of the puzzle 
 * @returns true if the puzzle is solvable, false otherwise
 */
function isSolvable(arr) {
    if (blankLocation <= 4 || (blankLocation >= 9 && blankLocation <= 12)) { // even row from bottom
        return numInversions(arr) % 2 == 1; // must be odd 
    } 
    return numInversions(arr) % 2 == 0; // odd row from bottom
}

/**
 * Function to determine the number of inversions of a given board
 * An inversion is when j > i && arr[i] > arr[j]
 * @param {ArrayList<String>} arr for the array of the puzzle
 * @returns the number of inversions of a board
 */
function numInversions(arr) {
    var inversions = 0;
    for (let i = 0; i < 16; i++) {
        if (arr[i] == "") { // skip if blank tile
            continue;
        }
        var previous = parseInt(arr[i]);
        for (let j = i + 1; j < 16; j++) {
            if (arr[i] == "") { // skip if blank tile
                continue;
            }
            if (previous > parseInt(arr[j])) { // inversion
                inversions++;
            }
        }
    }
    return inversions;
}   