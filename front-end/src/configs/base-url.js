import axios from 'axios';

const setBaseURL = () => {
  // axios.defaults.baseURL = 'https://grampus.herokuapp.com/';
   axios.defaults.baseURL = 'http://10.11.1.200:8081';
};

export default setBaseURL;

