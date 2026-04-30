import { useEffect, useState } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { Form, Input, InputNumber, Button, Space, Spin, message } from 'antd';
import { getGoods, createGoods, updateGoods } from '@/api/goods';

export default function GoodsFormPage() {
  const { id } = useParams<{ id: string }>();
  const navigate = useNavigate();
  const [form] = Form.useForm();
  const [loading, setLoading] = useState(false);
  const [submitting, setSubmitting] = useState(false);
  const isEdit = !!id;

  useEffect(() => {
    if (!id) return;
    setLoading(true);
    getGoods(Number(id))
      .then((data) => {
        form.setFieldsValue({
          goodsName: data.goodsName,
          goodsImg: data.goodsImg,
          goodsPrice: data.goodsPrice,
          goodsStock: data.goodsStock,
        });
      })
      .catch(() => message.error('加载商品详情失败'))
      .finally(() => setLoading(false));
  }, [id, form]);

  const onFinish = async (values: {
    goodsName: string;
    goodsImg: string;
    goodsPrice: number;
    goodsStock: number;
  }) => {
    setSubmitting(true);
    try {
      if (isEdit) {
        await updateGoods(Number(id), values);
        message.success('更新成功');
      } else {
        await createGoods(values);
        message.success('创建成功');
      }
      navigate('/goods');
    } catch (err: unknown) {
      message.error((err as Error).message || '操作失败');
    } finally {
      setSubmitting(false);
    }
  };

  if (loading) {
    return (
      <div className="page-loading">
        <Spin size="large" />
      </div>
    );
  }

  return (
    <div>
      <h2 style={{ fontSize: 20, fontWeight: 700, marginBottom: 28, letterSpacing: -0.3 }}>
        {isEdit ? '编辑商品' : '创建商品'}
      </h2>
      <Form
        form={form}
        layout="vertical"
        onFinish={onFinish}
        style={{ maxWidth: 560 }}
        requiredMark={false}
      >
        <Form.Item
          name="goodsName"
          label={<span style={{ fontWeight: 600 }}>商品名称</span>}
          rules={[
            { required: true, message: '请输入商品名称' },
            { max: 100, message: '最多100个字符' },
          ]}
        >
          <Input placeholder="请输入商品名称" />
        </Form.Item>

        <Form.Item
          name="goodsImg"
          label={<span style={{ fontWeight: 600 }}>商品图片 URL</span>}
        >
          <Input placeholder="请输入商品图片URL" />
        </Form.Item>

        <Form.Item
          name="goodsPrice"
          label={<span style={{ fontWeight: 600 }}>价格</span>}
          rules={[{ required: true, message: '请输入商品价格' }]}
        >
          <InputNumber min={0.01} precision={2} style={{ width: '100%' }} prefix="¥" placeholder="请输入价格" />
        </Form.Item>

        <Form.Item
          name="goodsStock"
          label={<span style={{ fontWeight: 600 }}>库存</span>}
          rules={[{ required: true, message: '请输入库存数量' }]}
        >
          <InputNumber min={0} precision={0} style={{ width: '100%' }} placeholder="请输入库存数量" />
        </Form.Item>

        <Form.Item style={{ marginTop: 8 }}>
          <Space size={12}>
            <Button
              type="primary"
              htmlType="submit"
              loading={submitting}
              style={{ borderRadius: 10, fontWeight: 600, paddingInline: 28 }}
            >
              提交
            </Button>
            <Button
              onClick={() => navigate('/goods')}
              style={{ borderRadius: 10 }}
            >
              取消
            </Button>
          </Space>
        </Form.Item>
      </Form>
    </div>
  );
}
