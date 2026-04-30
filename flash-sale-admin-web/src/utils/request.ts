import axios from 'axios';
import type { AxiosResponse } from 'axios';
import type { Result } from '@/types';
import { ErrorCode } from '@/types';

const request = axios.create({
  baseURL: '',
  timeout: 10000,
});

// 请求拦截器：注入 token
request.interceptors.request.use((config) => {
  const token = localStorage.getItem('admin_token');
  if (token) {
    config.headers.Authorization = token;
  }
  return config;
});

// 响应拦截器：统一处理 Result<T>
request.interceptors.response.use(
  (response: AxiosResponse<Result<unknown>>) => {
    const result = response.data;
    if (result.code === ErrorCode.SUCCESS) {
      return result.data as never;
    }
    if (result.code === ErrorCode.UNAUTHORIZED) {
      localStorage.removeItem('admin_token');
      window.location.href = '/login';
      return Promise.reject(new Error(result.msg));
    }
    const error = new Error(result.msg) as Error & { code: number };
    error.code = result.code;
    return Promise.reject(error);
  },
  () => {
    return Promise.reject(new Error('网络异常，请检查网络连接'));
  },
);

export default request;
