import { useEffect, useState, useRef, useCallback } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import {
  Tag,
  Button,
  Input,
  Spin,
  message,
  Modal,
  Result,
} from 'antd';
import { ArrowLeftOutlined, ReloadOutlined, ClockCircleOutlined, ShoppingOutlined } from '@ant-design/icons';
import dayjs from 'dayjs';
import { getSeckillDetail, getSeckillStock } from '@/api/goods';
import { getCaptchaUrl, getToken, execute, getResult } from '@/api/seckill';
import type { SeckillGoodsDTO } from '@/types';
import useAuthStore from '@/store/useAuthStore';
import CountdownTimer from '@/components/CountdownTimer';

type Status = 'upcoming' | 'active' | 'ended';

function getStatus(item: SeckillGoodsDTO): Status {
  const now = dayjs();
  if (now.isBefore(dayjs(item.startTime))) return 'upcoming';
  if (now.isAfter(dayjs(item.endTime))) return 'ended';
  return 'active';
}

export default function GoodsDetailPage() {
  const { id } = useParams<{ id: string }>();
  const navigate = useNavigate();
  const user = useAuthStore((s) => s.user);

  const [goods, setGoods] = useState<SeckillGoodsDTO | null>(null);
  const [stock, setStock] = useState<number>(0);
  const [loading, setLoading] = useState(true);
  const [captchaSrc, setCaptchaSrc] = useState('');
  const [captchaAnswer, setCaptchaAnswer] = useState('');
  const [seckilling, setSeckilling] = useState(false);
  const [modalOpen, setModalOpen] = useState(false);
  const [resultStatus, setResultStatus] = useState<number>(0);
  const [resultMsg, setResultMsg] = useState('');
  const [resultOrderId, setResultOrderId] = useState<number | null>(null);

  const stockTimerRef = useRef<ReturnType<typeof setInterval>>(undefined);
  const pollTimerRef = useRef<ReturnType<typeof setInterval>>(undefined);
  const pollCountRef = useRef(0);

  const goodsId = Number(id);

  const captchaAbortRef = useRef<AbortController>(undefined);

  const loadCaptcha = useCallback(() => {
    if (!user) return;
    captchaAbortRef.current?.abort();
    const controller = new AbortController();
    captchaAbortRef.current = controller;
    const url = getCaptchaUrl(user.id, goodsId);
    fetch(url, { signal: controller.signal })
      .then((res) => res.blob())
      .then((blob) => {
        if (!controller.signal.aborted) {
          setCaptchaSrc(URL.createObjectURL(blob));
          setCaptchaAnswer('');
        }
      })
      .catch(() => {});
  }, [user, goodsId]);

  useEffect(() => {
    const load = async () => {
      setLoading(true);
      try {
        const [detail, stockNum] = await Promise.all([
          getSeckillDetail(goodsId),
          getSeckillStock(goodsId),
        ]);
        setGoods(detail);
        setStock(stockNum);
      } catch {
        message.error('加载失败');
      } finally {
        setLoading(false);
      }
    };
    load();
  }, [goodsId]);

  useEffect(() => {
    if (!goods || !user) return;
    const status = getStatus(goods);
    if (status === 'active') {
      loadCaptcha();
      stockTimerRef.current = setInterval(async () => {
        try {
          const s = await getSeckillStock(goodsId);
          setStock(s);
        } catch {
          /* ignore */
        }
      }, 5000);
    }
    return () => {
      if (stockTimerRef.current) clearInterval(stockTimerRef.current);
      captchaAbortRef.current?.abort();
    };
  }, [goods, user, goodsId, loadCaptcha]);

  useEffect(() => {
    return () => {
      if (pollTimerRef.current) clearInterval(pollTimerRef.current);
    };
  }, []);

  const handleSeckill = async () => {
    if (!user || !goods) return;
    if (!captchaAnswer) {
      message.warning('请输入验证码');
      return;
    }

    setSeckilling(true);
    try {
      const token = await getToken(user.id, goodsId, captchaAnswer);
      await execute({ userId: user.id, seckillGoodsId: goodsId, token });

      setModalOpen(true);
      setResultStatus(0);
      setResultMsg('排队中，请稍候...');
      pollCountRef.current = 0;

      pollTimerRef.current = setInterval(async () => {
        pollCountRef.current++;
        if (pollCountRef.current > 30) {
          clearInterval(pollTimerRef.current);
          setResultStatus(-1);
          setResultMsg('请稍后在订单中查看');
          return;
        }
        try {
          const r = await getResult(user.id, goodsId);
          setResultStatus(r.status);
          setResultMsg(r.message);
          if (r.status === 1) {
            setResultOrderId(r.orderId);
            clearInterval(pollTimerRef.current);
          } else if (r.status === -1) {
            clearInterval(pollTimerRef.current);
          }
        } catch {
          /* continue polling */
        }
      }, 2000);
    } catch (err: unknown) {
      const error = err as Error & { code?: number };
      message.error(error.message || '秒杀失败');
      loadCaptcha();
    } finally {
      setSeckilling(false);
    }
  };

  if (loading || !goods) {
    return (
      <div className="page-loading">
        <Spin size="large" />
        <span className="loading-text">加载商品详情...</span>
      </div>
    );
  }

  const status = getStatus(goods);

  return (
    <div className="anim-fade-up">
      <div className="back-btn" onClick={() => navigate(-1)}>
        <ArrowLeftOutlined /> 返回列表
      </div>

      {/* Hero card */}
      <div className="detail-hero anim-scale-in">
        {goods.goodsImg ? (
          <img className="detail-hero-img" src={goods.goodsImg} alt={goods.goodsName} />
        ) : (
          <div className="detail-hero-placeholder">
            <ShoppingOutlined />
          </div>
        )}

        {/* Price bar */}
        <div className="detail-price-bar">
          <span className="sale-price">
            <span className="yen">¥</span>
            {goods.seckillPrice}
          </span>
          <span className="original-price">原价 ¥{goods.goodsPrice}</span>
        </div>

        {/* Info area */}
        <div className="detail-info">
          <h1>{goods.goodsName}</h1>

          {/* Status card */}
          <div className="status-card">
            <div className="status-card-item">
              <span className="label">活动状态</span>
              {status === 'upcoming' && <Tag color="blue">即将开始</Tag>}
              {status === 'active' && <Tag color="red">抢购中</Tag>}
              {status === 'ended' && <Tag>已结束</Tag>}
            </div>
            <div className="status-card-item">
              <span className="label">剩余库存</span>
              <span className="value">{stock} 件</span>
            </div>
            <div className="status-card-item">
              <span className="label">
                <ClockCircleOutlined style={{ marginRight: 4 }} />
                活动时间
              </span>
              <span className="value" style={{ fontSize: 13, fontWeight: 500 }}>
                {dayjs(goods.startTime).format('MM/DD HH:mm')} ~ {dayjs(goods.endTime).format('MM/DD HH:mm')}
              </span>
            </div>
            {status === 'upcoming' && (
              <div className="status-card-item">
                <span className="label">倒计时</span>
                <CountdownTimer targetTime={goods.startTime} />
              </div>
            )}
            {status === 'active' && (
              <div className="status-card-item">
                <span className="label">距结束</span>
                <CountdownTimer targetTime={goods.endTime} />
              </div>
            )}
          </div>

          {/* Captcha */}
          {status === 'active' && user && (
            <div className="captcha-area">
              <img
                src={captchaSrc}
                alt="验证码"
                onClick={loadCaptcha}
              />
              <Button
                icon={<ReloadOutlined />}
                size="small"
                onClick={loadCaptcha}
                style={{ borderRadius: 8 }}
              />
              <Input
                placeholder="输入验证码答案"
                value={captchaAnswer}
                onChange={(e) => setCaptchaAnswer(e.target.value)}
                style={{ width: 160, borderRadius: 8 }}
                onPressEnter={handleSeckill}
              />
            </div>
          )}

          {/* CTA button */}
          <Button
            type="primary"
            danger
            size="large"
            block
            disabled={status !== 'active' || stock <= 0 || !user}
            loading={seckilling}
            onClick={handleSeckill}
            className={`seckill-btn ${status === 'active' && stock > 0 ? 'btn-active' : ''}`}
          >
            {status === 'upcoming'
              ? '即将开始'
              : status === 'ended'
                ? '已结束'
                : stock <= 0
                  ? '已抢完'
                  : '立即抢购'}
          </Button>
        </div>
      </div>

      {/* Result modal */}
      <Modal
        open={modalOpen}
        footer={null}
        closable={resultStatus !== 0}
        onCancel={() => {
          setModalOpen(false);
          if (pollTimerRef.current) clearInterval(pollTimerRef.current);
        }}
      >
        {resultStatus === 0 && (
          <Result icon={<Spin size="large" />} title="排队中" subTitle={resultMsg} />
        )}
        {resultStatus === 1 && (
          <Result
            status="success"
            title="秒杀成功！"
            subTitle={resultMsg}
            extra={
              <Button type="primary" onClick={() => navigate(`/order/${resultOrderId}`)}>
                查看订单
              </Button>
            }
          />
        )}
        {resultStatus === -1 && (
          <Result
            status="error"
            title="秒杀失败"
            subTitle={resultMsg}
            extra={<Button onClick={() => setModalOpen(false)}>关闭</Button>}
          />
        )}
      </Modal>
    </div>
  );
}
