import axios from 'axios'
// 引入 Element Plus 的消息提示组件，用来弹报错框
import { ElMessage } from 'element-plus'

// 创建一个 axios 实例
const request = axios.create({
    // 全局装配
    baseURL: 'http://localhost:8090', 
    timeout: 5000 // 快递超时时间：5秒，如果后端 5 秒没响应，直接报错
})

// 请求拦截器
request.interceptors.request.use(config => {
    config.headers['Content-Type'] = 'application/json;charset=utf-8';
    
    // 添加token到请求头
    const token = localStorage.getItem('token');
    if (token) {
        config.headers['Authorization'] = 'Bearer ' + token;
    }
    
    return config
}, error => {
    return Promise.reject(error)
})

// 响应拦截器
request.interceptors.response.use(
    response => {
        // response.data 就是你在后端写的那个 Result<T> 对象！！！
        let res = response.data;
        
        // 判断后端的 code 是不是 200 (成功)
        if (res.code === 200) {
            // 🌟 核心魔法：如果是 200，我们直接把 Result 里的 data 掏出来给前端页面！
            // 这样 Vue 页面里就不用再写繁琐的 res.data.data 了。
            return res.data; 
        } else {
            // 如果后端返回的不是 200 (比如 500 异常)，直接在网页右上角弹一个红色的警告框！
            ElMessage.error(res.msg ? res.msg : '后端系统异常')
            return Promise.reject(res.msg)
        }
    },
    error => {
        // 处理401未授权错误
        if (error.response && error.response.status === 401) {
            ElMessage.error('登录已过期，请重新登录');
            // 清除本地存储的token和用户信息
            localStorage.removeItem('token');
            localStorage.removeItem('user');
            localStorage.removeItem('menus');
            // 跳转到登录页
            window.location.href = '/login';
            return Promise.reject(error);
        }
        
        console.error('网络请求错误:', error)
        ElMessage.error('网络连接失败，请检查后端服务是否启动！')
        return Promise.reject(error)
    }
)

export default request