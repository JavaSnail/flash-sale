import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom';

import AuthGuard from '@/components/AuthGuard';
import CLayout from '@/layouts/CLayout';

import LoginPage from '@/pages/login/LoginPage';
import RegisterPage from '@/pages/register/RegisterPage';
import HomePage from '@/pages/home/HomePage';
import GoodsDetailPage from '@/pages/goods/GoodsDetailPage';
import OrderDetailPage from '@/pages/order/OrderDetailPage';
import PayPage from '@/pages/pay/PayPage';
import ProfilePage from '@/pages/profile/ProfilePage';

export default function App() {
  return (
    <BrowserRouter>
      <Routes>
        {/* 公开路由 */}
        <Route path="/login" element={<LoginPage />} />
        <Route path="/register" element={<RegisterPage />} />

        {/* C 端 — 需要登录 */}
        <Route element={<AuthGuard />}>
          <Route element={<CLayout />}>
            <Route path="/" element={<HomePage />} />
            <Route path="/goods/:id" element={<GoodsDetailPage />} />
            <Route path="/order/:id" element={<OrderDetailPage />} />
            <Route path="/pay/:orderId" element={<PayPage />} />
            <Route path="/me" element={<ProfilePage />} />
          </Route>
        </Route>

        {/* 兜底 */}
        <Route path="*" element={<Navigate to="/" replace />} />
      </Routes>
    </BrowserRouter>
  );
}
