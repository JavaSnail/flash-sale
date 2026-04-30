import React from 'react';
import ReactDOM from 'react-dom/client';
import { ConfigProvider } from 'antd';
import zhCN from 'antd/locale/zh_CN';
import App from './App';
import './styles/global.css';

const fontFamily = "-apple-system, 'PingFang SC', 'Microsoft YaHei', 'Helvetica Neue', sans-serif";

ReactDOM.createRoot(document.getElementById('root')!).render(
  <React.StrictMode>
    <ConfigProvider
      locale={zhCN}
      theme={{
        token: {
          colorPrimary: '#DC2626',
          colorLink: '#DC2626',
          colorLinkHover: '#B91C1C',
          borderRadius: 10,
          fontFamily,
          fontSize: 14,
          colorBgContainer: '#FFFFFF',
          colorBgLayout: '#FAF9F6',
          controlHeight: 40,
          colorText: '#18181B',
          colorTextSecondary: '#71717A',
        },
        components: {
          Button: {
            primaryShadow: 'none',
            fontWeight: 600,
            borderRadius: 10,
          },
          Input: {
            borderRadius: 10,
            paddingInline: 14,
          },
          Card: {
            borderRadiusLG: 16,
          },
          Table: {
            borderRadius: 12,
            headerBg: '#FAF9F6',
          },
          Tag: {
            borderRadiusSM: 100,
          },
          Modal: {
            borderRadiusLG: 20,
          },
          Message: {
            borderRadiusLG: 12,
          },
        },
      }}
    >
      <App />
    </ConfigProvider>
  </React.StrictMode>,
);
