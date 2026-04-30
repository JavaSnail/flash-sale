import { useEffect, useState } from 'react';
import dayjs from 'dayjs';

interface Props {
  targetTime: string;
  onEnd?: () => void;
  prefix?: string;
  mini?: boolean;
}

export default function CountdownTimer({ targetTime, onEnd, prefix, mini }: Props) {
  const [parts, setParts] = useState({ h: '00', m: '00', s: '00' });
  const [ended, setEnded] = useState(false);

  useEffect(() => {
    const update = () => {
      const diff = dayjs(targetTime).diff(dayjs(), 'second');
      if (diff <= 0) {
        setParts({ h: '00', m: '00', s: '00' });
        setEnded(true);
        onEnd?.();
        return false;
      }
      setParts({
        h: String(Math.floor(diff / 3600)).padStart(2, '0'),
        m: String(Math.floor((diff % 3600) / 60)).padStart(2, '0'),
        s: String(diff % 60).padStart(2, '0'),
      });
      return true;
    };

    if (!update()) return;
    const timer = setInterval(() => {
      if (!update()) clearInterval(timer);
    }, 1000);
    return () => clearInterval(timer);
  }, [targetTime, onEnd]);

  const digitCls = mini ? 'countdown-digit mini' : 'countdown-digit';
  const sepCls = mini ? 'countdown-sep mini' : 'countdown-sep';

  if (ended) {
    return (
      <span className="countdown-wrap">
        {prefix}
        <span className={digitCls}>00</span>
        <span className={sepCls}>:</span>
        <span className={digitCls}>00</span>
        <span className={sepCls}>:</span>
        <span className={digitCls}>00</span>
      </span>
    );
  }

  return (
    <span className="countdown-wrap">
      {prefix && <span style={{ marginRight: 6, fontSize: 13, color: '#71717A' }}>{prefix}</span>}
      <span className={digitCls}>{parts.h}</span>
      <span className={sepCls}>:</span>
      <span className={digitCls}>{parts.m}</span>
      <span className={sepCls}>:</span>
      <span className={digitCls}>{parts.s}</span>
    </span>
  );
}
