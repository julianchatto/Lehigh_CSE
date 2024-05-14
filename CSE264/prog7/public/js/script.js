const socket = io.connect('http://vesta.cse.lehigh.edu:3000');

document.addEventListener("DOMContentLoaded", function () {
    // resolve the promises
    loadedImages.then(images => {
        imageArray = images;
        candyCrush(); // start the game
    }).catch(error => {
        console.error("Error while loading images after DOM loaded", error);
    });

});

/**
 * Function to handle the game logic
 * Sets up all the event listeners and socket listeners
 */
function candyCrush() {
    let grid = "", id = "", filteredusername = "";
    const canvas = document.getElementById("board");
    const loginButton = document.getElementById("login");
    const chatButton = document.getElementById("chatbutton");

    // socket emits
    loginButton.addEventListener('click', function() {
        var loginField = document.getElementById('loginfield');
        if (loginField.value !== "") { // check if username is empty
            socket.emit("login",  loginField.value);
            loginField.value = "";
        } else {
            window.alert("Username cannot be empty");
        }
        
    });
    chatButton.addEventListener('click', function() {
        if (id !== "") { // check if user is logged in
            var msgField = document.getElementById('chatsend');
            if (msgField.value !== "") { // check if message is empty
                const msg = {id: id, message: msgField.value};
                msgField.value = "";
                socket.emit("chatsend", msg);
            } else {
                window.alert("Message cannot be empty");
            }
        } else {
            window.alert("You need to login first");
        }
    });

    // socket listeners
    socket.on('loginresponse', function(datavalue) { 
        id = datavalue.id;
        filteredusername = datavalue.filteredusername;
    });
    socket.on('chatbroadcast', function(datavalue) {
        updateChat(datavalue);
    });
    socket.on('gridupdate', function(datavalue) {
        grid = datavalue;
        displayGrid(canvas, grid);
    });
    socket.on('playerslistupdate', function(datavalue) {
        displayPlayerList(datavalue);
    });
    
    let selectedImage = [-1, -1]; // coordinates of the selected image
    let droppedImage = [-1, -1]; // coordinates of the dropped image

    /**
     * Function to handle the mouse move event
     */
    var handleMouseMove = function(event) { 
        displayGrid(canvas, grid);
        var ctx = canvas.getContext("2d");
        var currentImage = imageArray[grid[selectedImage[0]][selectedImage[1]] - 1];
        ctx.drawImage(currentImage, event.clientX, event.clientY);
    };
    /**
     * Function to add the event listener for mouse move
     */
    function addMouseMove() {
        canvas.addEventListener('mousemove', handleMouseMove);
    }
    /**
     * Function to remove the event listener for mouse move
     */
    function removeMouseMove() {
        canvas.removeEventListener('mousemove', handleMouseMove);
    }

    // canvas event listeners
    canvas.addEventListener('mousedown', function(event) {
        if (id !== "") { // check if user is logged in
            // calculate the selected image coordinates
            selectedImage[0] = Math.floor(event.clientY / 50); 
            selectedImage[1] = Math.floor(event.clientX / 50);  
            addMouseMove(); // add mouse move handler
        } else {
            window.alert("You need to login first");
        }
    });
    canvas.addEventListener('mouseup', function(event) {
        if (id !== "") { // check if user is logged in
            removeMouseMove(); // remove mouse move handler
            // calculate the dropped image coordinates
            droppedImage[0] = Math.floor(event.clientY / 50);
            droppedImage[1] = Math.floor(event.clientX / 50);
            // swap images
            swap(grid, selectedImage, droppedImage);
            // redisplay the grid
            displayGrid(canvas, grid); 
            // emit the imageswap event
            socket.emit("imageswap", {id: id, image1Col:selectedImage[1], image1Row:selectedImage[0], image2Col:droppedImage[1], image2Row:droppedImage[0]});
        } else {
            window.alert("You need to login first");
        }          
    });
}

/**
 * Function to swap to images
 * @param {Number[][]} grid for the grid array 
 * @param {Number[]} selected for the coordinates of the selected image
 * @param {Number[]} dropped for the coordinates of the dropped image
 */
function swap(grid, selected, dropped) {
    if ((Math.abs(selected[0] - dropped[0]) === 1 && Math.abs(selected[1] - dropped[1]) === 0) || (Math.abs(selected[0] - dropped[0]) === 0 && Math.abs(selected[1] - dropped[1]) === 1)) {
        let temp = grid[selected[0]][selected[1]];
        grid[selected[0]][selected[1]] = grid[dropped[0]][dropped[1]];
        grid[dropped[0]][dropped[1]] = temp;
    }
}

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
    });
}

// Array of image file names
const imageFiles = [
  "appleGreen.png",
  "appleRed.png",
  "cherry.png",
  "grape.png",
  "orange.png",
  "strawberry.png",
  "watermelon.png",
];

// Path to the images directory
const imagePath = "../images/";

// Map each image file name to a full path and wrap it with the loadImage function
const imagePromises = imageFiles.map((file) => loadImage(imagePath + file));
let imageArray = [];

// Use Promise.all to wait for all images to be loaded before proceeding
let loadedImages = Promise.all(imagePromises)
    .then((images) => {
        // All images are loaded and can be used here;
        return images;
    }).catch((error) => {
        console.error("Error loading images", error);
    });

/**
 * Function to update the grid
 * @param {Canvas} canvas 
 * @param {Number[][]} grid for the grid array
 */
function displayGrid(canvas, grid) {
    const ctx = canvas.getContext("2d");
    ctx.clearRect(0, 0, canvas.width, canvas.height); // clear the canvas
    // update the canvas with the new grid
    for (let i = 0; i < 8; i++) {
        for (let j = 0; j < 10; j++) {
            ctx.drawImage(imageArray[grid[i][j] - 1], j * 50, i * 50);
        }
    }
}

/**
 * Function to update the player list
 * @param {String[]} plist 
 */
function displayPlayerList(plist) {
    const list = document.getElementById("leaderboardBody");
    let rows = "";
    for (let i = 0; i < plist.length; i++) {
        rows += `<tr><th>${plist[i].name}</th><th>${plist[i].score}</th></tr>`;
    }
    list.innerHTML = rows;

}

/**
 * Function to add a message to the chat
 * @param {String} msg for the chat message
 */
function updateChat(msg) {
    const list = document.getElementById("chat-messages");
    const item = document.createElement('li');
    item.textContent = msg;
    list.appendChild(item);
}