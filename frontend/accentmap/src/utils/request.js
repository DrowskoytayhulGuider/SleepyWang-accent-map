import axios from 'axios'

const request = axios.create({
    timeout: 50000
})

request.interceptors.request.use(config => {
    config.baseURL = '/api'
    // 'http://localhost:8080/api'
    return config
})

export default request