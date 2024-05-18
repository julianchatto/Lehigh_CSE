import axios from 'axios';

const backendUrl = "http://127.0.0.1:4567";


export default axios.create({
    baseURL: backendUrl
});