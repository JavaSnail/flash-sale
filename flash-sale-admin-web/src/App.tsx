import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom';

import AdminGuard from '@/components/AdminGuard';
import AdminLayout from '@/layouts/AdminLayout';

import AdminLoginPage from '@/pages/admin/login/AdminLoginPage';
import DashboardPage from '@/pages/admin/dashboard/DashboardPage';
import GoodsListPage from '@/pages/admin/goods/GoodsListPage';
import GoodsFormPage from '@/pages/admin/goods/GoodsFormPage';
import ActivityListPage from '@/pages/admin/activities/ActivityListPage';
import ActivityFormPage from '@/pages/admin/activities/ActivityFormPage';
import OrderListPage from '@/pages/admin/orders/OrderListPage';
import PaymentListPage from '@/pages/admin/payments/PaymentListPage';
import WarmupPage from '@/pages/admin/warmup/WarmupPage';

export default function App() {
  return (
    <BrowserRouter>
      <Routes>
        {/* 登录页 */}
        <Route path="/login" element={<AdminLoginPage />} />

        {/* 管理后台 — 需要登录 */}
        <Route element={<AdminGuard />}>
          <Route element={<AdminLayout />}>
            <Route path="/dashboard" element={<DashboardPage />} />
            <Route path="/goods" element={<GoodsListPage />} />
            <Route path="/goods/create" element={<GoodsFormPage />} />
            <Route path="/goods/edit/:id" element={<GoodsFormPage />} />
            <Route path="/activities" element={<ActivityListPage />} />
            <Route path="/activities/create" element={<ActivityFormPage />} />
            <Route path="/activities/edit/:id" element={<ActivityFormPage />} />
            <Route path="/orders" element={<OrderListPage />} />
            <Route path="/payments" element={<PaymentListPage />} />
            <Route path="/warmup" element={<WarmupPage />} />
          </Route>
        </Route>

        {/* 兜底 */}
        <Route path="*" element={<Navigate to="/dashboard" replace />} />
      </Routes>
    </BrowserRouter>
  );
}
