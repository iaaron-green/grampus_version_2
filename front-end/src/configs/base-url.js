import axios from 'axios';

const setBaseURL = () => {
  axios.defaults.baseURL = 'http://10.11.1.155:8081/';
  // axios.defaults.baseURL = 'http://localhost:8080';
};

export default setBaseURL;

