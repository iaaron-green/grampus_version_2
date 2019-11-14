import axios from 'axios';

const setBaseURL = () => {
  axios.defaults.baseURL = 'https://grampus.herokuapp.com/';
  // axios.defaults.baseURL = 'http://localhost:8080';
};

export default setBaseURL;

