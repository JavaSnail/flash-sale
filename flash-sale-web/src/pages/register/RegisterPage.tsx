import { useState } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import { Form, Input, Button, message } from 'antd';
import { MobileOutlined, LockOutlined, UserOutlined, ThunderboltFilled } from '@ant-design/icons';
import { register } from '@/api/user';

export default function RegisterPage() {
  const navigate = useNavigate();
  const [loading, setLoading] = useState(false);

  const onFinish = async (values: {
    phone: string;
    password: string;
    nickname?: string;
  }) => {
    setLoading(true);
    try {
      await register(values.phone, values.password, values.nickname);
      message.success('注册成功');
      navigate('/login', { replace: true });
    } catch (err: unknown) {
      message.error((err as Error).message || '注册失败');
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
          <ThunderboltFilled /> 加入 Flash-Sale
        </div>
        <div className="auth-brand-title anim-fade-up anim-delay-2">
          创建你的
          <br />
          <span className="highlight">专属账号</span>
        </div>
        <div className="auth-brand-sub anim-fade-up anim-delay-3">
          只需几步，即可开始你的极速抢购之旅。
          <br />
          精选好物，限时特惠。
        </div>
      </div>

      {/* Right form panel */}
      <div className="auth-form-panel">
        <div className="auth-form-wrapper anim-fade-up anim-delay-3">
          <h2>创建账号</h2>
          <div className="subtitle">填写以下信息完成注册</div>

          <Form onFinish={onFinish} size="large" layout="vertical">
            <Form.Item
              name="phone"
              rules={[
                { required: true, message: '请输入手机号' },
                { pattern: /^1\d{10}$/, message: '请输入正确的手机号' },
              ]}
            >
              <Input prefix={<MobileOutlined style={{ color: '#A1A1AA' }} />} placeholder="手机号" />
            </Form.Item>
            <Form.Item
              name="password"
              rules={[
                { required: true, message: '请输入密码' },
                { min: 6, message: '密码至少6位' },
              ]}
            >
              <Input.Password prefix={<LockOutlined style={{ color: '#A1A1AA' }} />} placeholder="密码" />
            </Form.Item>
            <Form.Item
              name="confirmPassword"
              dependencies={['password']}
              rules={[
                { required: true, message: '请确认密码' },
                ({ getFieldValue }) => ({
                  validator(_, value) {
                    if (!value || getFieldValue('password') === value) {
                      return Promise.resolve();
                    }
                    return Promise.reject(new Error('两次密码不一致'));
                  },
                }),
              ]}
            >
              <Input.Password prefix={<LockOutlined style={{ color: '#A1A1AA' }} />} placeholder="确认密码" />
            </Form.Item>
            <Form.Item name="nickname">
              <Input prefix={<UserOutlined style={{ color: '#A1A1AA' }} />} placeholder="昵称（选填）" />
            </Form.Item>
            <Form.Item style={{ marginTop: 8 }}>
              <Button
                type="primary"
                htmlType="submit"
                block
                loading={loading}
                style={{ height: 48, fontSize: 16, fontWeight: 700, borderRadius: 12 }}
              >
                注 册
              </Button>
            </Form.Item>
          </Form>

          <div className="switch-link">
            已有账号？ <Link to="/login">去登录</Link>
          </div>
        </div>
      </div>
    </div>
  );
}
