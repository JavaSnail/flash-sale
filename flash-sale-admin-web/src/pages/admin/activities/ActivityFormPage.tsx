import { useEffect, useState } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { Form, InputNumber, DatePicker, Select, Button, Space, Spin, message } from 'antd';
import dayjs from 'dayjs';
import { getGoodsList, getSeckillGoods, createSeckillGoods, updateSeckillGoods } from '@/api/goods';
import type { GoodsDTO } from '@/types';

const { RangePicker } = DatePicker;

export default function ActivityFormPage() {
  const { id } = useParams<{ id: string }>();
  const navigate = useNavigate();
  const [form] = Form.useForm();
  const [loading, setLoading] = useState(false);
  const [submitting, setSubmitting] = useState(false);
  const [goodsList, setGoodsList] = useState<GoodsDTO[]>([]);
  const isEdit = !!id;

  useEffect(() => {
    getGoodsList()
      .then(setGoodsList)
      .catch(() => message.error('加载商品列表失败'));
  }, []);

  useEffect(() => {
    if (!id) return;
    setLoading(true);
    getSeckillGoods(Number(id))
      .then((data) => {
        form.setFieldsValue({
          goodsId: data.goodsId,
          seckillPrice: data.seckillPrice,
          stockCount: data.stockCount,
          timeRange: [dayjs(data.startTime), dayjs(data.endTime)],
        });
      })
      .catch(() => message.error('加载秒杀商品详情失败'))
      .finally(() => setLoading(false));
  }, [id, form]);

  const onFinish = async (values: {
    goodsId: number;
    seckillPrice: number;
    stockCount: number;
    timeRange: [dayjs.Dayjs, dayjs.Dayjs];
  }) => {
    setSubmitting(true);
    try {
      const payload = {
        goodsId: values.goodsId,
        seckillPrice: values.seckillPrice,
        stockCount: values.stockCount,
        startTime: values.timeRange[0].toISOString(),
        endTime: values.timeRange[1].toISOString(),
      };
      if (isEdit) {
        await updateSeckillGoods(Number(id), payload);
      } else {
        await createSeckillGoods(payload);
      }
      message.success(isEdit ? '更新成功' : '创建成功');
      navigate('/activities');
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
        {isEdit ? '编辑秒杀商品' : '创建秒杀商品'}
      </h2>
      <Form
        form={form}
        layout="vertical"
        onFinish={onFinish}
        style={{ maxWidth: 560 }}
        requiredMark={false}
      >
        <Form.Item
          name="goodsId"
          label={<span style={{ fontWeight: 600 }}>关联商品</span>}
          rules={[{ required: true, message: '请选择关联商品' }]}
        >
          <Select
            placeholder="请选择商品"
            showSearch
            optionFilterProp="label"
            disabled={isEdit}
            options={goodsList.map((g) => ({
              value: g.id,
              label: `${g.goodsName}（¥${g.goodsPrice.toFixed(2)}，库存 ${g.goodsStock}）`,
            }))}
          />
        </Form.Item>

        <Form.Item
          name="seckillPrice"
          label={<span style={{ fontWeight: 600 }}>秒杀价格</span>}
          rules={[{ required: true, message: '请输入秒杀价格' }]}
        >
          <InputNumber min={0.01} precision={2} style={{ width: '100%' }} prefix="¥" placeholder="请输入秒杀价格" />
        </Form.Item>

        <Form.Item
          name="stockCount"
          label={<span style={{ fontWeight: 600 }}>库存数量</span>}
          rules={[{ required: true, message: '请输入库存数量' }]}
        >
          <InputNumber min={1} precision={0} style={{ width: '100%' }} placeholder="请输入库存数量" />
        </Form.Item>

        <Form.Item
          name="timeRange"
          label={<span style={{ fontWeight: 600 }}>秒杀时间</span>}
          rules={[{ required: true, message: '请选择秒杀时间' }]}
        >
          <RangePicker showTime style={{ width: '100%' }} />
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
              onClick={() => navigate('/activities')}
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
