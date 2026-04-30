import { useState } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import { Form, Input, Button, message } from 'antd';
import { MobileOutlined, LockOutlined, ThunderboltFilled } from '@ant-design/icons';
import { login } from '@/api/user';
import useAuthStore from '@/store/useAuthStore';

export default function LoginPage() {
  const navigate = useNavigate();
  const setToken = useAuthStore((s) => s.setToken);
  const [loading, setLoading] = useState(false);

  const onFinish = async (values: { phone: string; password: string }) => {
    setLoading(true);
    try {
      const token = await login(values.phone, values.password);
      setToken(token);
      message.success('登录成功');
      navigate('/', { replace: true });
    } catch (err: unknown) {
      message.error((err as Error).message || '登录失败');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="auth-page">
      {/* Left brand panel */}
      <div className="auth-brand-panel anim-fade-in">
        <div className="brand-grid" />
        <div className="auth-brand-badge anim-fade-up anim-delay-1">
          <ThunderboltFilled /> 高并发秒杀系统
        </div>
        <div className="auth-brand-title anim-fade-up anim-delay-2">
          <span className="highlight">Flash</span>-Sale
          <br />
          限时秒杀
        </div>
        <div className="auth-brand-sub anim-fade-up anim-delay-3">
          毫秒级响应，百万级并发。
          <br />
          每一次抢购，都是一场极速体验。
        </div>
      </div>

      {/* Right form panel */}
      <div className="auth-form-panel">
        <div className="auth-form-wrapper anim-fade-up anim-delay-3">
          <h2>欢迎回来</h2>
          <div className="subtitle">登录你的账号以继续</div>

          <Form onFinish={onFinish} size="large" layout="vertical">
            <Form.Item
              name="phone"
              rules={[
                { required: true, message: '请输入手机号' },
                { pattern: /^1\d{10}$/, message: '请输入正确的手机号' },
              ]}
            >
              <Input
                prefix={<MobileOutlined style={{ color: '#A1A1AA' }} />}
                placeholder="手机号"
              />
            </Form.Item>
            <Form.Item
              name="password"
              rules={[{ required: true, message: '请输入密码' }]}
            >
              <Input.Password
                prefix={<LockOutlined style={{ color: '#A1A1AA' }} />}
                placeholder="密码"
              />
            </Form.Item>
            <Form.Item style={{ marginTop: 8 }}>
              <Button
                type="primary"
                htmlType="submit"
                block
                loading={loading}
                style={{ height: 48, fontSize: 16, fontWeight: 700, borderRadius: 12 }}
              >
                登 录
              </Button>
            </Form.Item>
          </Form>

          <div className="switch-link">
            还没有账号？ <Link to="/register">立即注册</Link>
          </div>
        </div>
      </div>
    </div>
  );
}
