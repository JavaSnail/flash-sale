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
  (error) => {
    // 尝试从响应体中提取业务错误信息
    if (error.response?.data?.code !== undefined) {
      const result = error.response.data as Result<unknown>;
      if (result.code === ErrorCode.UNAUTHORIZED) {
        localStorage.removeItem('admin_token');
        window.location.href = '/login';
      }
      const bizError = new Error(result.msg || '请求失败') as Error & { code: number };
      bizError.code = result.code;
      return Promise.reject(bizError);
    }
    return Promise.reject(new Error('网络异常，请检查网络连接'));
  },
);

export default request;
