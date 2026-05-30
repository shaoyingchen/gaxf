import React, { useState, useEffect } from 'react';
import { 
  Camera, AlertTriangle, CheckCircle, BarChart3, 
  LayoutDashboard, Settings, Search, Bell, User, MapPin, 
  Play, Filter, Maximize2, List, Grid3X3, Grid2X2, 
  ChevronRight, MoreVertical, X, Upload, MessageSquare,
  ArrowRight, Activity, Database, Smartphone, HardDrive,
  Info, ChevronLeft, Palette, Video, Eye, Clock, FileText, Send
} from 'lucide-react';

// --- 主题配置表 ---
const themes = {
  default: {
    name: '经典蓝',
    bg: 'bg-[#020617]',
    panel: 'bg-slate-900/40 border-blue-500/20',
    accent: 'text-blue-400',
    accentBg: 'bg-blue-600',
    accentBorder: 'border-blue-500/30',
    glow: 'bg-blue-600/5',
    gradient: 'from-blue-400 to-indigo-300',
    chart: 'bg-blue-500/30',
    btnHover: 'hover:bg-blue-500/20'
  },
  crimson: {
    name: '威严红',
    bg: 'bg-[#1a0505]',
    panel: 'bg-red-950/20 border-red-500/20',
    accent: 'text-red-400',
    accentBg: 'bg-red-600',
    accentBorder: 'border-red-500/30',
    glow: 'bg-red-600/5',
    gradient: 'from-red-400 to-orange-300',
    chart: 'bg-red-500/30',
    btnHover: 'hover:bg-red-500/20'
  },
  midnight: {
    name: '极客黑',
    bg: 'bg-[#0a0a0c]',
    panel: 'bg-zinc-900/60 border-zinc-700/50',
    accent: 'text-zinc-100',
    accentBg: 'bg-zinc-700',
    accentBorder: 'border-zinc-600/50',
    glow: 'bg-zinc-400/5',
    gradient: 'from-zinc-100 to-zinc-500',
    chart: 'bg-zinc-600/30',
    btnHover: 'hover:bg-zinc-800'
  }
};

// --- 子组件：分析研判 (Dashboard) ---
const Dashboard = ({ theme }) => {
  const t = themes[theme];
  return (
    <div className="space-y-6 animate-in fade-in duration-500">
      <div className="grid grid-cols-1 md:grid-cols-4 gap-6">
        {[
          { label: '点位总数', value: '12,840', change: '+12%', icon: Database, color: 'blue' },
          { label: '今日预警', value: '156', change: '-5%', icon: AlertTriangle, color: 'amber' },
          { label: '在线率', value: '99.2%', change: '+0.2%', icon: Activity, color: 'emerald' },
          { label: '整改率', value: '88.5%', change: '+2.4%', icon: CheckCircle, color: 'indigo' },
        ].map((item, i) => (
          <div key={i} className={`${t.panel} border p-5 rounded-2xl backdrop-blur-md`}>
            <div className="flex justify-between items-start mb-4">
              <div className={`p-2 bg-white/5 rounded-lg ${t.accent}`}>
                <item.icon size={20} />
              </div>
              <span className={`text-xs ${item.change.startsWith('+') ? 'text-emerald-400' : 'text-red-400'}`}>
                {item.change}
              </span>
            </div>
            <p className="text-slate-400 text-sm">{item.label}</p>
            <p className={`text-2xl font-bold mt-1 tracking-tight text-slate-100`}>{item.value}</p>
          </div>
        ))}
      </div>
      <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
        <div className={`${t.panel} border p-6 rounded-2xl h-80 flex flex-col`}>
          <h3 className="font-medium mb-6 flex items-center gap-2 text-slate-200"><BarChart3 size={18} className={t.accent} /> 督察违规趋势</h3>
          <div className="flex-1 flex items-end gap-3 px-2">
            {[40, 25, 60, 80, 55, 90, 70, 45, 30, 85, 65, 50].map((h, i) => (
              <div key={i} className={`${t.chart} hover:opacity-80 transition-all rounded-t-sm relative group`} style={{ height: `${h}%` }}>
                <div className="absolute -top-8 left-1/2 -translate-x-1/2 bg-slate-700 text-[10px] px-1 rounded hidden group-hover:block">{h}</div>
              </div>
            ))}
          </div>
          <div className="flex justify-between mt-4 text-[10px] text-slate-500 font-mono">
            <span>01月</span><span>04月</span><span>08月</span><span>12月</span>
          </div>
        </div>
        <div className={`${t.panel} border p-6 rounded-2xl h-80`}>
          <h3 className="font-medium mb-6 flex items-center gap-2 text-slate-200"><Smartphone size={18} className={t.accent} /> 终端在线分布</h3>
          <div className="space-y-4">
            {['办公区监控', '办案区', '服务大厅', '外部卡口'].map((name, i) => (
              <div key={i} className="space-y-1">
                <div className="flex justify-between text-xs text-slate-400">
                  <span>{name}</span>
                  <span>{85 - i * 15}%</span>
                </div>
                <div className="h-2 bg-slate-700/50 rounded-full overflow-hidden">
                  <div className={`h-full bg-gradient-to-r ${t.gradient} rounded-full`} style={{ width: `${85 - i * 15}%` }}></div>
                </div>
              </div>
            ))}
          </div>
        </div>
      </div>
    </div>
  );
};

// --- 子组件：点位管理 ---
const PointsManagement = ({ theme }) => {
  const t = themes[theme];
  return (
    <div className="grid grid-cols-12 gap-6 h-full animate-in slide-in-from-right duration-500">
      <div className={`${t.panel} border col-span-3 rounded-2xl p-4 flex flex-col`}>
        <div className="relative mb-4">
          <Search className="absolute left-3 top-2.5 text-slate-500" size={16} />
          <input className="w-full bg-slate-900/50 border border-slate-700/50 rounded-lg py-2 pl-10 pr-4 text-sm focus:outline-none focus:border-blue-500 text-slate-200" placeholder="搜索区域/机构..." />
        </div>
        <div className="flex-1 overflow-y-auto text-sm space-y-1">
          {['市公安局', '第一分局', '第二分局', '高新派出所', '城南派出所'].map((dept, i) => (
            <div key={i} className={`p-2 rounded-lg cursor-pointer flex items-center justify-between transition-colors ${i === 0 ? `bg-white/5 ${t.accent}` : 'text-slate-400 hover:bg-slate-700/50 hover:text-slate-200'}`}>
              <span className="flex items-center gap-2"> <MapPin size={14} /> {dept}</span>
              <ChevronRight size={14} />
            </div>
          ))}
        </div>
      </div>
      <div className={`${t.panel} border col-span-9 rounded-2xl overflow-hidden`}>
        <table className="w-full text-left">
          <thead className="bg-white/5 text-slate-400 text-xs border-b border-white/5 uppercase tracking-wider">
            <tr>
              <th className="p-4 font-semibold">点位名称</th>
              <th className="p-4 font-semibold">编号</th>
              <th className="p-4 font-semibold">类型</th>
              <th className="p-4 font-semibold">状态</th>
              <th className="p-4 font-semibold">操作</th>
            </tr>
          </thead>
          <tbody className="text-sm divide-y divide-white/5">
            {[1,2,3,4,5,6,7].map((i) => (
              <tr key={i} className="hover:bg-white/5 group">
                <td className={`p-4 flex items-center gap-2 text-slate-300 group-hover:${t.accent} transition-colors`}><Camera size={16} className="text-slate-500" /> 1号楼办证大厅_{i}</td>
                <td className="p-4 text-slate-500 font-mono">SN-88234-0{i}</td>
                <td className="p-4"><span className="px-2 py-0.5 bg-slate-700/50 border border-slate-600/30 rounded text-[10px] text-slate-400">枪机</span></td>
                <td className="p-4"><span className="text-emerald-400 flex items-center gap-1.5 text-xs"><span className="w-1.5 h-1.5 bg-emerald-500 rounded-full animate-pulse"></span> 在线</span></td>
                <td className="p-4"><button className={`${t.accent} hover:opacity-70 font-medium`}>编辑</button></td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </div>
  );
};

// --- 子组件：视频点播 ---
const VideoOnDemand = ({ theme }) => {
  const t = themes[theme];
  const [gridSize, setGridSize] = useState(4); // 1, 4, 9

  return (
    <div className="grid grid-cols-12 gap-6 h-full animate-in zoom-in-95 duration-500">
      {/* 资源树 */}
      <div className={`${t.panel} border col-span-3 rounded-2xl p-4 flex flex-col`}>
        <h3 className="text-sm font-bold mb-4 flex items-center gap-2"><List size={16} /> 资源目录</h3>
        <div className="space-y-2 overflow-y-auto pr-2">
          {['办公大楼', '窗口中心', '监控机房', '外部周界'].map((folder, i) => (
            <div key={i}>
              <div className="flex items-center gap-2 text-xs text-slate-300 p-2 hover:bg-white/5 rounded cursor-pointer">
                 <ChevronRight size={14} className={i === 0 ? 'rotate-90' : ''} />
                 <span>{folder}</span>
              </div>
              {i === 0 && (
                <div className="ml-6 mt-1 space-y-1 border-l border-white/10 pl-2">
                  {[1, 2, 3, 4].map(v => (
                    <div key={v} className="text-[11px] p-1.5 hover:text-blue-400 cursor-pointer flex items-center gap-2">
                      <Video size={12} className="text-slate-500" />
                      高清枪机-F{v}-00{v}
                    </div>
                  ))}
                </div>
              )}
            </div>
          ))}
        </div>
      </div>
      
      {/* 播放器区域 */}
      <div className="col-span-9 flex flex-col gap-4">
        {/* 控制工具条 */}
        <div className={`${t.panel} border rounded-xl p-2 flex justify-between items-center px-4`}>
           <div className="flex items-center gap-4">
             <button onClick={() => setGridSize(1)} className={`p-1.5 rounded ${gridSize === 1 ? t.accentBg : 'hover:bg-white/5'}`}><Maximize2 size={16}/></button>
             <button onClick={() => setGridSize(4)} className={`p-1.5 rounded ${gridSize === 4 ? t.accentBg : 'hover:bg-white/5'}`}><Grid2X2 size={16}/></button>
             <button onClick={() => setGridSize(9)} className={`p-1.5 rounded ${gridSize === 9 ? t.accentBg : 'hover:bg-white/5'}`}><Grid3X3 size={16}/></button>
           </div>
           <div className="flex items-center gap-3">
             <span className="text-[10px] text-slate-500 uppercase tracking-widest">Live Streaming Mode</span>
             <div className="w-2 h-2 bg-red-500 rounded-full animate-ping"></div>
           </div>
        </div>

        {/* 视频网格 */}
        <div className={`flex-1 grid gap-2 ${gridSize === 1 ? 'grid-cols-1' : gridSize === 4 ? 'grid-cols-2' : 'grid-cols-3'}`}>
          {Array.from({length: gridSize}).map((_, i) => (
            <div key={i} className={`${t.panel} border rounded-lg overflow-hidden relative group flex items-center justify-center bg-black`}>
               <div className="absolute top-2 left-2 px-2 py-0.5 bg-black/60 rounded text-[10px] text-white z-10">Cam-00{i+1}</div>
               <Play size={48} className="text-white/20 group-hover:text-white/40 transition-all cursor-pointer" />
               <div className="absolute bottom-0 left-0 w-full h-1 bg-white/10 group-hover:bg-blue-500/50 transition-all"></div>
            </div>
          ))}
        </div>
      </div>
    </div>
  );
};

// --- 子组件：预警管理 ---
const AlertManagement = ({ theme }) => {
  const t = themes[theme];
  return (
    <div className="space-y-6 animate-in slide-in-from-bottom duration-500">
      <div className="flex justify-between items-center">
        <div className="flex gap-2">
          {['全部预警', '未处理', '误报', '已确认'].map((f, i) => (
            <button key={i} className={`px-4 py-1.5 rounded-full text-xs border ${i === 0 ? t.accentBg + ' border-transparent' : 'border-white/10 hover:bg-white/5'}`}>
              {f}
            </button>
          ))}
        </div>
        <button className="flex items-center gap-2 text-xs text-slate-400 hover:text-white transition-colors">
          <Filter size={14} /> 筛选
        </button>
      </div>

      <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
        {[
          { type: '未按规定佩戴单警装备', level: '中危', time: '14:20:33', loc: '办案区2号室' },
          { type: '办公区域吸烟行为', level: '低危', time: '13:15:02', loc: '3层公共休息区' },
          { type: '非法闯入核心机房', level: '高危', time: '12:00:15', loc: '核心机房' },
          { type: '离岗超时预警', level: '低危', time: '11:45:10', loc: '1层咨询台' },
          { type: '办案室人员单独滞留', level: '高危', time: '10:30:22', loc: '讯问室B' },
          { type: '未穿戴警服执法', level: '中危', time: '09:12:44', loc: '接待大厅' },
        ].map((alert, i) => (
          <div key={i} className={`${t.panel} border rounded-2xl overflow-hidden group hover:scale-[1.02] transition-all`}>
            <div className="h-40 bg-slate-800 relative flex items-center justify-center overflow-hidden">
               <div className="absolute inset-0 bg-gradient-to-t from-black/80 to-transparent z-10"></div>
               <div className="w-full h-full bg-slate-700 animate-pulse"></div>
               <div className={`absolute top-3 right-3 px-2 py-0.5 rounded text-[10px] font-bold z-20 ${
                 alert.level === '高危' ? 'bg-red-500 text-white' : alert.level === '中危' ? 'bg-amber-500 text-black' : 'bg-blue-500 text-white'
               }`}>
                 {alert.level}
               </div>
               <div className="absolute bottom-3 left-3 z-20">
                 <p className="text-[10px] text-slate-300 flex items-center gap-1"><Clock size={10} /> {alert.time}</p>
                 <p className="text-xs font-bold text-white">{alert.type}</p>
               </div>
            </div>
            <div className="p-4 flex justify-between items-center">
              <div className="flex items-center gap-2 text-[11px] text-slate-400">
                <MapPin size={12} className={t.accent} /> {alert.loc}
              </div>
              <button className={`px-3 py-1 rounded text-[11px] font-bold ${t.accentBg} text-white`}>核实</button>
            </div>
          </div>
        ))}
      </div>
    </div>
  );
};

// --- 子组件：整改反馈 ---
const FeedbackFlow = ({ theme }) => {
  const t = themes[theme];
  return (
    <div className="grid grid-cols-12 gap-6 h-full animate-in slide-in-from-right duration-500">
      {/* 待整改列表 */}
      <div className="col-span-4 space-y-4">
        <h3 className="text-sm font-bold flex items-center gap-2 text-slate-300 px-2"><Send size={16} className={t.accent} /> 待处理任务</h3>
        {[1, 2, 3].map(i => (
          <div key={i} className={`${t.panel} border rounded-xl p-4 border-l-4 ${theme === 'crimson' ? 'border-l-red-500' : 'border-l-blue-500'} cursor-pointer hover:bg-white/5 transition-all`}>
            <div className="flex justify-between mb-2">
              <span className="text-[10px] bg-white/10 px-2 py-0.5 rounded text-slate-400">#ZG-202400{i}</span>
              <span className="text-[10px] text-amber-500 font-bold italic underline">待反馈</span>
            </div>
            <h4 className="text-sm font-bold text-white mb-1">点位 Cam-00{i} 离岗异常整改</h4>
            <p className="text-xs text-slate-500 mb-3">督察中心下发：请相关责任人核实离岗原因并提交文字说明。</p>
            <div className="flex items-center justify-between text-[10px] text-slate-400">
               <span className="flex items-center gap-1"><User size={10} /> 督察员 A</span>
               <span>2024-04-20 10:00</span>
            </div>
          </div>
        ))}
      </div>

      {/* 详情与反馈编辑器 */}
      <div className="col-span-8 flex flex-col gap-4">
         <div className={`${t.panel} border rounded-2xl p-6 flex-1 flex flex-col`}>
            <div className="flex justify-between items-start border-b border-white/5 pb-4 mb-4">
               <div>
                 <h2 className="text-lg font-bold text-white">任务详情：#ZG-2024001</h2>
                 <p className="text-xs text-slate-500 mt-1">关联点位：盐城市局一楼大厅东侧枪机</p>
               </div>
               <div className="flex gap-2">
                  <button className="p-2 bg-white/5 rounded-lg hover:bg-white/10 transition-colors"><MoreVertical size={16}/></button>
               </div>
            </div>
            
            <div className="flex-1 space-y-6">
              <div className="bg-white/5 p-4 rounded-xl border border-white/5 italic text-sm text-slate-400 leading-relaxed">
                "经视频回溯分析，该员在10:20-10:45期间未出现在工位，疑似离岗。请分局督察部门核实是否履行请假手续或执行其他紧急任务。"
              </div>

              <div className="space-y-3">
                 <label className="text-xs font-bold text-slate-500 uppercase">提交反馈说明</label>
                 <textarea className="w-full bg-black/40 border border-white/10 rounded-xl p-4 text-sm focus:outline-none focus:border-blue-500 h-32 placeholder:text-slate-700" placeholder="在此输入整改核实结果..."></textarea>
              </div>

              <div className="grid grid-cols-4 gap-4">
                 <div className="aspect-square bg-slate-800 rounded-lg flex flex-col items-center justify-center text-slate-600 border border-dashed border-white/10 hover:border-blue-500 hover:text-blue-500 cursor-pointer transition-all">
                    <Upload size={24} />
                    <span className="text-[10px] mt-2">上传附件</span>
                 </div>
              </div>
            </div>

            <div className="mt-6 flex justify-end gap-3">
               <button className="px-6 py-2 bg-white/5 hover:bg-white/10 rounded-xl text-sm font-bold transition-all">暂存草稿</button>
               <button className={`px-8 py-2 ${t.accentBg} text-white rounded-xl text-sm font-bold shadow-lg shadow-blue-500/10 hover:scale-105 transition-all`}>正式提交反馈</button>
            </div>
         </div>
      </div>
    </div>
  );
};

// --- 主应用组件 ---
const App = () => {
  const [activeTab, setActiveTab] = useState('dashboard');
  const [currentTime, setCurrentTime] = useState(new Date());
  const [theme, setTheme] = useState('default');

  useEffect(() => {
    const timer = setInterval(() => setCurrentTime(new Date()), 1000);
    return () => clearInterval(timer);
  }, []);

  const cycleTheme = () => {
    const keys = Object.keys(themes);
    const currentIndex = keys.indexOf(theme);
    const nextIndex = (currentIndex + 1) % keys.length;
    setTheme(keys[nextIndex]);
  };

  const formatDate = (date) => {
    const options = { year: 'numeric', month: '2-digit', day: '2-digit', weekday: 'long' };
    const dateStr = date.toLocaleDateString('zh-CN', options).replace(/\//g, '/');
    const timeStr = date.toLocaleTimeString('zh-CN', { hour12: false });
    return `${dateStr} ${timeStr}`;
  };

  const navItems = [
    { id: 'dashboard', icon: LayoutDashboard, label: '分析研判' },
    { id: 'points', icon: MapPin, label: '点位管理' },
    { id: 'video', icon: Video, label: '视频点播' },
    { id: 'alerts', icon: AlertTriangle, label: '预警管理' },
    { id: 'feedback', icon: FileText, label: '整改反馈' },
    { id: 'screen', icon: Maximize2, label: '大屏设计' },
  ];

  const t = themes[theme];

  // 路由分发
  const renderContent = () => {
    switch (activeTab) {
      case 'dashboard': return <Dashboard theme={theme} />;
      case 'points': return <PointsManagement theme={theme} />;
      case 'video': return <VideoOnDemand theme={theme} />;
      case 'alerts': return <AlertManagement theme={theme} />;
      case 'feedback': return <FeedbackFlow theme={theme} />;
      default: return <Dashboard theme={theme} />;
    }
  };

  return (
    <div className={`flex h-screen ${t.bg} text-slate-200 overflow-hidden font-sans select-none transition-colors duration-700`}>
      
      {/* 侧边栏 */}
      <aside className={`w-64 ${t.panel} border-r backdrop-blur-2xl flex flex-col z-20 transition-colors duration-700`}>
        <div className="p-4">
          <div className={`bg-white/5 rounded-2xl border border-white/5 p-4 flex items-center justify-between shadow-xl`}>
            <div className="flex items-center space-x-3">
              <div className={`${t.accentBg} p-2 rounded-xl shadow-lg transition-all`}>
                <div className="w-5 h-5 border-2 border-white/50 rounded-sm"></div>
              </div>
              <div>
                <h1 className="text-base font-bold tracking-wider text-white">数智督察</h1>
                <p className="text-[10px] text-slate-500 font-medium">智能·全流程平台</p>
              </div>
            </div>
            <button className="p-1.5 hover:bg-slate-700/50 text-slate-500 rounded-lg transition-colors">
              <ChevronLeft size={16} />
            </button>
          </div>
        </div>

        <nav className="flex-1 px-4 py-4 space-y-1 overflow-y-auto custom-scrollbar">
          <div className="px-4 text-[10px] font-bold text-slate-500 uppercase tracking-[0.2em] mb-4 flex items-center gap-2 opacity-60">
            <span className={`w-1 h-1 ${t.accentBg} rounded-full`}></span> 核心工作台
          </div>
          {navItems.map((item) => (
            <button
              key={item.id}
              onClick={() => setActiveTab(item.id)}
              className={`w-full flex items-center space-x-3 px-4 py-3.5 rounded-xl transition-all duration-300 group ${
                activeTab === item.id 
                  ? `bg-white/5 ${t.accent} border border-white/10 shadow-lg` 
                  : 'hover:bg-white/5 text-slate-500 hover:text-slate-300'
              }`}
            >
              <item.icon size={18} className={activeTab === item.id ? t.accent : "text-slate-500 group-hover:text-white transition-colors"} />
              <span className="font-bold text-sm tracking-wide">{item.label}</span>
            </button>
          ))}
        </nav>

        <div className="p-6">
          <div className={`bg-white/5 border-white/5 rounded-xl p-4 border`}>
             <div className="text-[10px] text-slate-500 font-bold mb-2 uppercase tracking-widest text-center">当前主题: {t.name}</div>
             <div className="flex justify-center gap-1.5 mt-2">
                {Object.keys(themes).map(k => (
                  <div key={k} className={`w-2 h-2 rounded-full ${k === theme ? 'ring-2 ring-white ring-offset-1 ring-offset-black' : ''}`} style={{backgroundColor: themes[k].accentBg.replace('bg-', '')}}></div>
                ))}
             </div>
          </div>
        </div>
      </aside>

      {/* 主界面 */}
      <main className="flex-1 flex flex-col relative overflow-hidden">
        
        {/* 顶部 Header */}
        <header className="p-4 z-10">
          <div className={`${t.panel} backdrop-blur-2xl rounded-[2.5rem] border px-10 py-6 flex items-center justify-between relative overflow-hidden group transition-all duration-700`}>
            
            {/* 背景发光装饰线 */}
            <div className={`absolute top-0 left-0 w-full h-[1px] bg-gradient-to-r from-transparent via-white/20 to-transparent opacity-50`}></div>
            
            {/* 标题区 */}
            <div className="flex flex-col relative">
              <div className="flex items-center gap-4">
                <h2 className={`text-3xl font-black tracking-tight bg-gradient-to-r ${t.gradient} bg-clip-text text-transparent drop-shadow-sm transition-all duration-700`}>
                  盐城公安智能视频督察平台
                </h2>
              </div>
              <div className="text-xs text-slate-500 mt-1.5 flex items-center gap-4 font-semibold tracking-wide">
                <span className={`hover:${t.accent} transition-colors cursor-default`}>实时态势感知</span>
                <span className="w-1 h-1 bg-slate-700 rounded-full"></span>
                <span className={`hover:${t.accent} transition-colors cursor-default`}>高效协同处置</span>
                <span className="w-1 h-1 bg-slate-700 rounded-full"></span>
                <span className="hover:text-red-400 transition-colors cursor-default">红色预警响应机制</span>
              </div>
            </div>

            {/* 右侧信息与图标区 */}
            <div className="flex items-center space-x-6">
              {/* 时间胶囊 */}
              <div className={`bg-black/40 px-6 py-2.5 rounded-full border border-white/5 shadow-inner transition-all`}>
                <span className={`text-sm font-mono font-bold ${t.accent} tracking-wider opacity-90`}>
                  {formatDate(currentTime)}
                </span>
              </div>

              {/* 图标组 */}
              <div className="flex items-center gap-3">
                <button 
                  onClick={cycleTheme}
                  title="切换显示主题"
                  className={`w-11 h-11 rounded-full border flex items-center justify-center transition-all shadow-lg group/theme relative bg-white/5 border-white/10 hover:border-white/30 text-white`}
                >
                  <Palette size={20} />
                  <div className={`absolute inset-0 rounded-full blur-md opacity-40 animate-pulse ${t.accentBg}`}></div>
                </button>

                <button className={`w-11 h-11 rounded-full bg-white/5 border border-white/10 text-slate-400 flex items-center justify-center hover:text-white transition-all`}>
                  <Bell size={20} />
                </button>
                
                <button className={`w-11 h-11 rounded-full bg-white/5 border border-white/10 text-slate-400 flex items-center justify-center hover:text-white transition-all`}>
                  <User size={20} />
                </button>

                <div className="h-8 w-[1px] bg-slate-700/50 mx-2"></div>
                <span className="px-3 py-1 bg-red-500/10 text-red-500 border border-red-500/20 rounded text-[10px] font-black uppercase tracking-tighter cursor-pointer hover:bg-red-500/20 transition-all">
                  原有后台
                </span>
              </div>
            </div>
          </div>
        </header>

        {/* 二级菜单切换区 (同步 activeTab) */}
        <div className="px-10 flex gap-4 mb-4">
          {navItems.slice(0, 5).map((item, i) => (
            <button 
              key={item.id} 
              onClick={() => setActiveTab(item.id)}
              className={`flex items-center gap-2.5 px-7 py-3 rounded-2xl text-sm font-bold transition-all border duration-500 ${
                activeTab === item.id 
                ? `bg-white/5 ${t.accent} ${t.accentBorder} shadow-lg shadow-white/5` 
                : 'bg-white/5 text-slate-500 hover:text-slate-300 border-transparent hover:border-white/10'
              }`}
            >
              <div className={`p-1 rounded-md ${activeTab === item.id ? 'bg-white/10' : 'bg-slate-800'}`}>
                <item.icon size={14}/>
              </div>
              {item.label}
            </button>
          ))}
        </div>

        {/* 内容展示区 */}
        <div className="flex-1 overflow-y-auto p-8 pt-4 relative custom-scrollbar">
           {renderContent()}
        </div>
      </main>

      {/* 背景装饰光晕 */}
      <div className={`fixed top-[-5%] right-[-5%] w-[50%] h-[50%] blur-[120px] rounded-full -z-10 animate-pulse transition-colors duration-1000 ${t.glow}`}></div>
      <div className="fixed bottom-[-10%] left-[20%] w-[30%] h-[30%] bg-white/5 blur-[120px] rounded-full -z-10"></div>

      <style>{`
        .custom-scrollbar::-webkit-scrollbar { width: 5px; }
        .custom-scrollbar::-webkit-scrollbar-track { background: transparent; }
        .custom-scrollbar::-webkit-scrollbar-thumb { background: rgba(255, 255, 255, 0.05); border-radius: 10px; }
        .custom-scrollbar::-webkit-scrollbar-thumb:hover { background: rgba(255, 255, 255, 0.1); }
      `}</style>
    </div>
  );
};

export default App;