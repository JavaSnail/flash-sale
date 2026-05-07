import axios from 'axios';
import type { AxiosResponse } from 'axios';
import { ErrorCode, type Result } from '@/types';
import useAuthStore from '@/store/useAuthStore';

const request = axios.create({
  baseURL: '',
  timeout: 10000,
});

// 请求拦截器：注入 token
request.interceptors.request.use((config) => {
  const token = useAuthStore.getState().token;
  if (token) {
    config.headers.Authorization = token;
  }
  return config;
});

let redirecting = false;

function handleUnauthorized() {
  useAuthStore.getState().logout();
  if (!redirecting) {
    redirecting = true;
    window.location.href = '/login';
  }
}

// 响应拦截器：统一处理 Result<T>
request.interceptors.response.use(
  (response: AxiosResponse<Result<unknown>>) => {
    const result = response.data;
    if (result.code === ErrorCode.SUCCESS) {
      return result.data as never;
    }
    if (result.code === ErrorCode.UNAUTHORIZED) {
      handleUnauthorized();
      return Promise.reject(new Error(result.msg));
    }
    const error = new Error(result.msg) as Error & { code: number };
    error.code = result.code;
    return Promise.reject(error);
  },
  (error) => {
    // HTTP 401 状态码（网关或下游直接返回 401）
    if (error.response?.status === 401) {
      handleUnauthorized();
      return Promise.reject(new Error('登录已过期'));
    }
    // 尝试从响应体中提取业务错误信息
    if (error.response?.data?.code !== undefined) {
      const result = error.response.data as Result<unknown>;
      if (result.code === ErrorCode.UNAUTHORIZED) {
        handleUnauthorized();
      }
      const bizError = new Error(result.msg || '请求失败') as Error & { code: number };
      bizError.code = result.code;
      return Promise.reject(bizError);
    }
    return Promise.reject(new Error('网络异常，请检查网络连接'));
  },
);

export default request;
