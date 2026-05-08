import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { Form, Input, Button, message } from 'antd';
import { UserOutlined, LockOutlined, SafetyCertificateOutlined } from '@ant-design/icons';
import { adminLogin } from '@/api/auth';
import useAuthStore from '@/store/useAuthStore';

export default function AdminLoginPage() {
  const navigate = useNavigate();
  const setLoginData = useAuthStore((s) => s.setLoginData);
  const [loading, setLoading] = useState(false);

  const onFinish = async (values: { username: string; password: string }) => {
    setLoading(true);
    try {
      const data = await adminLogin(values.username, values.password);
      setLoginData(data);
      message.success('登录成功');
      navigate('/dashboard', { replace: true });
    } catch (err: unknown) {
      message.error((err as Error).message || '登录失败');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div style={{
      display: 'flex',
      justifyContent: 'center',
      alignItems: 'center',
      minHeight: '100vh',
      background: 'var(--bg-dark)',
    }}>
      <div className="anim-fade-up" style={{ width: '100%', maxWidth: 400, padding: '0 24px' }}>
        {/* Header */}
        <div style={{ textAlign: 'center', marginBottom: 40 }}>
          <div style={{
            width: 56,
            height: 56,
            borderRadius: 16,
            background: 'linear-gradient(135deg, var(--scarlet) 0%, var(--ember) 100%)',
            display: 'inline-flex',
            alignItems: 'center',
            justifyContent: 'center',
            marginBottom: 20,
            boxShadow: '0 4px 20px rgba(220,38,38,0.3)',
          }}>
            <SafetyCertificateOutlined style={{ fontSize: 28, color: 'white' }} />
          </div>
          <h2 style={{
            fontSize: 24,
            fontWeight: 700,
            color: 'var(--text-inverse)',
            marginBottom: 6,
          }}>
            管理后台
          </h2>
          <div style={{ color: 'rgba(255,255,255,0.4)', fontSize: 14 }}>
            Flash-Sale 管理员登录
          </div>
        </div>

        {/* Form card */}
        <div style={{
          background: 'var(--bg-dark-lighter)',
          borderRadius: 20,
          padding: 32,
          border: '1px solid rgba(255,255,255,0.06)',
        }}>
          <Form onFinish={onFinish} size="large" layout="vertical">
            <Form.Item
              name="username"
              rules={[{ required: true, message: '请输入用户名' }]}
            >
              <Input
                prefix={<UserOutlined style={{ color: '#71717A' }} />}
                placeholder="管理员用户名"
              />
            </Form.Item>
            <Form.Item
              name="password"
              rules={[{ required: true, message: '请输入密码' }]}
            >
              <Input.Password
                prefix={<LockOutlined style={{ color: '#71717A' }} />}
                placeholder="密码"
              />
            </Form.Item>
            <Form.Item style={{ marginTop: 8, marginBottom: 0 }}>
              <Button
                type="primary"
                htmlType="submit"
                block
                loading={loading}
                style={{
                  height: 48,
                  fontSize: 16,
                  fontWeight: 700,
                  borderRadius: 12,
                  background: 'linear-gradient(135deg, var(--scarlet) 0%, var(--ember) 100%)',
                  border: 'none',
                  boxShadow: '0 4px 16px rgba(220,38,38,0.25)',
                }}
              >
                登 录
              </Button>
            </Form.Item>
          </Form>
        </div>

        {/* Footer */}
        <div style={{ textAlign: 'center', marginTop: 24, color: 'rgba(255,255,255,0.25)', fontSize: 12 }}>
          Flash-Sale 管理后台
        </div>
      </div>
    </div>
  );
}
