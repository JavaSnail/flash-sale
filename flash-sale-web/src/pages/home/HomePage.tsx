import { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { Button, Spin, Empty, message } from 'antd';
import { FireFilled } from '@ant-design/icons';
import dayjs from 'dayjs';
import { getSeckillList } from '@/api/goods';
import type { SeckillGoodsDTO } from '@/types';
import CountdownTimer from '@/components/CountdownTimer';

type GoodsStatus = 'upcoming' | 'active' | 'ended';

function getStatus(item: SeckillGoodsDTO): GoodsStatus {
  const now = dayjs();
  if (now.isBefore(dayjs(item.startTime))) return 'upcoming';
  if (now.isAfter(dayjs(item.endTime))) return 'ended';
  return 'active';
}

function sortGoods(list: SeckillGoodsDTO[]): SeckillGoodsDTO[] {
  const order: Record<GoodsStatus, number> = { active: 0, upcoming: 1, ended: 2 };
  return [...list].sort((a, b) => order[getStatus(a)] - order[getStatus(b)]);
}

export default function HomePage() {
  const navigate = useNavigate();
  const [goods, setGoods] = useState<SeckillGoodsDTO[]>([]);
  const [loading, setLoading] = useState(true);

  const load = async () => {
    setLoading(true);
    try {
      const data = await getSeckillList();
      setGoods(sortGoods(data));
    } catch {
      message.error('加载失败，请重试');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    load();
  }, []);

  if (loading) {
    return (
      <div className="page-loading">
        <Spin size="large" />
        <span className="loading-text">加载秒杀商品中...</span>
      </div>
    );
  }

  if (goods.length === 0) {
    return (
      <div style={{ paddingTop: 80 }}>
        <Empty description="暂无秒杀活动">
          <Button type="primary" onClick={load}>刷新重试</Button>
        </Empty>
      </div>
    );
  }

  return (
    <div className="anim-fade-up">
      {/* Section header */}
      <div className="section-header">
        <div className="icon-badge">
          <FireFilled />
        </div>
        <h2>限时秒杀</h2>
      </div>

      {/* Goods grid */}
      <div className="goods-grid">
        {goods.map((item, idx) => {
          const status = getStatus(item);
          return (
            <div
              key={item.id}
              className={`goods-card anim-fade-up anim-delay-${Math.min(idx + 1, 8)}`}
              onClick={() => navigate(`/goods/${item.id}`)}
            >
              {/* Image */}
              <div className="goods-card-img">
                {item.goodsImg ? (
                  <img src={item.goodsImg} alt={item.goodsName} />
                ) : (
                  <div className="placeholder-icon">
                    <FireFilled />
                  </div>
                )}
                <div className={`goods-card-badge ${status}`}>
                  {status === 'active' && '抢购中'}
                  {status === 'upcoming' && '即将开始'}
                  {status === 'ended' && '已结束'}
                </div>
              </div>

              {/* Body */}
              <div className="goods-card-body">
                <div className="goods-card-name">{item.goodsName}</div>
                <div className="goods-card-price">
                  <span className="sale">
                    <span className="yen">¥</span>
                    {item.seckillPrice}
                  </span>
                  <span className="original">¥{item.goodsPrice}</span>
                </div>
                <div className="goods-card-meta">
                  {status === 'active' && (
                    <span className="goods-card-stock">剩余 {item.stockCount} 件</span>
                  )}
                  {status === 'upcoming' && (
                    <CountdownTimer targetTime={item.startTime} prefix="距开始 " mini />
                  )}
                  {status === 'ended' && (
                    <span className="goods-card-stock" style={{ color: '#A1A1AA' }}>已结束</span>
                  )}
                  <Button
                    type="primary"
                    size="small"
                    danger={status === 'active'}
                    disabled={status !== 'active'}
                    style={{ borderRadius: 100, fontWeight: 600, fontSize: 12, height: 28, padding: '0 14px' }}
                  >
                    {status === 'active' ? '立即抢购' : status === 'upcoming' ? '即将开始' : '已结束'}
                  </Button>
                </div>
              </div>
            </div>
          );
        })}
      </div>
    </div>
  );
}
