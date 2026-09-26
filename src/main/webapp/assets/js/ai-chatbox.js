/**
 * ASM_WEB_UTE - HCMUTE Academic AI Assistant Engine v2.0
 * Direct Real-Time Database Querying & Minimalist Modern UI
 */
(function () {
  'use strict';

  // DOM Elements
  const widget = document.getElementById('uteAiChatWidget');
  if (!widget) return;

  const toggleBtn = document.getElementById('uteAiToggleBtn');
  const chatBox = document.getElementById('uteAiChatBox');
  const closeBtn = document.getElementById('uteAiCloseBtn');
  const clearBtn = document.getElementById('uteAiClearChatBtn');
  const messagesContainer = document.getElementById('uteAiMessagesContainer');
  const typingIndicator = document.getElementById('uteAiTypingIndicator');
  const promptChips = document.getElementById('uteAiPromptChips');
  const chatForm = document.getElementById('uteAiChatForm');
  const chatInput = document.getElementById('uteAiChatInput');
  const roleLabel = document.getElementById('uteAiUserRoleLabel');

  // Metadata from DOM
  const userRole = (widget.dataset.userRole || 'GUEST').toUpperCase();
  const userId = widget.dataset.userId || 'guest';
  const userName = widget.dataset.userName || 'Bạn';
  const ctx = widget.dataset.contextPath || '';

  // Storage keys (Role & User scoped to refresh conversation upon role switch)
  const STORAGE_KEY = `hcmute_ai_chat_${userRole}_${userId}`;
  const OPEN_STATE_KEY = 'hcmute_ai_chat_open_v2';
  const ACTIVE_ROLE_USER_KEY = 'hcmute_ai_active_role_user';

  const roleNames = {
    ADMIN: 'Quản trị viên',
    DEAN: 'Ban Chủ nhiệm Khoa',
    DEPARTMENT_HEAD: 'Trưởng bộ môn',
    LECTURER: 'Giảng viên',
    STUDENT: 'Sinh viên',
    GUEST: 'Khách'
  };

  if (roleLabel) {
    roleLabel.textContent = `${roleNames[userRole] || userRole} • CSDL Thật`;
  }

  /* ──────────────────────────────────────────────────────────────────
     PROMPT CHIPS PER ROLE (Tailored to real database queries)
     ────────────────────────────────────────────────────────────────── */
  const roleChips = {
    STUDENT: [
      { text: '🎯 Xem điểm & Nhận xét HĐ', query: 'xem điểm và nhận xét hội đồng' },
      { text: '👥 Nhóm của tôi', query: 'thông tin nhóm của tôi' },
      { text: '📚 Đề tài đã đăng ký', query: 'đề tài đã đăng ký' },
      { text: '📑 Tiến độ báo cáo tuần', query: 'tiến độ báo cáo' },
      { text: '⏳ Hạn chót đợt đăng ký', query: 'thời hạn đợt đăng ký' }
    ],
    LECTURER: [
      { text: '👨‍🏫 Đề tài do tôi đề xuất', query: 'danh sách đề tài đề xuất' },
      { text: '⚖️ Nhiệm vụ Hội đồng bảo vệ', query: 'lịch chấm hội đồng bảo vệ' },
      { text: '⏳ Hạn chót đợt đăng ký', query: 'thời hạn đợt đăng ký' },
      { text: '📋 Quy trình đồ án 6 bước', query: 'quy trình làm đồ án' }
    ],
    ADMIN: [
      { text: '📊 Thống kê toàn hệ thống', query: 'thống kê hệ thống' },
      { text: '⏳ Đợt đăng ký hiện hành', query: 'thời hạn đợt đăng ký' },
      { text: '📋 Quy trình đồ án 6 bước', query: 'quy trình làm đồ án' }
    ],
    DEAN: [
      { text: '📊 Thống kê đề tài toàn khoa', query: 'thống kê hệ thống' },
      { text: '⏳ Đợt đăng ký hiện hành', query: 'thời hạn đợt đăng ký' },
      { text: '📋 Quy trình đồ án 6 bước', query: 'quy trình làm đồ án' }
    ],
    GUEST: [
      { text: '🔍 Tìm đề tài AI / Web', query: 'tìm đề tài AI' },
      { text: '⏳ Hạn chót đợt đăng ký', query: 'thời hạn đợt đăng ký' },
      { text: '📋 Quy trình 6 bước làm đồ án', query: 'quy trình làm đồ án' }
    ]
  };

  /* ──────────────────────────────────────────────────────────────────
     API CALL: QUERY REAL BACKEND DATABASE
     ────────────────────────────────────────────────────────────────── */
  async function queryRealBackendData(userQuery) {
    try {
      const endpoint = `${ctx}/api/ai/query`;
      const formData = new URLSearchParams();
      formData.append('query', userQuery);
      formData.append('path', window.location.pathname);

      const response = await fetch(endpoint, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/x-www-form-urlencoded;charset=UTF-8'
        },
        body: formData.toString()
      });

      if (response.ok) {
        const json = await response.json();
        if (json && json.answer) {
          return json.answer;
        }
      }
    } catch (e) {
      console.warn('Lỗi kết nối API AI CSDL thật, chuyển sang bộ xử lý nội bộ:', e);
    }

    // Fallback nếu API backend tạm thời bận
    return fallbackLocalAnswer(userQuery);
  }

  function fallbackLocalAnswer(query) {
    const raw = query.toLowerCase();
    if (raw.includes('quy trình') || raw.includes('bước')) {
      return `🎓 **Quy trình 6 bước Quản lý Đồ án HCMUTE:**\n\n` +
        `1. **Mở đợt & Đề xuất đề tài:** Khoa thông báo kế hoạch, Giảng viên nộp danh mục đề tài.\n` +
        `2. **Lập nhóm sinh viên:** Tối đa 03 thành viên, nhóm trưởng đại diện đăng ký.\n` +
        `3. **Đăng ký đề tài & Duyệt:** Nhóm gửi đơn đăng ký, GVHD xét duyệt.\n` +
        `4. **Thực hiện & Báo cáo:** Sinh viên nộp báo cáo tuần định kỳ theo dõi tiến độ.\n` +
        `5. **Sơ duyệt & Lập hội đồng:** GVHD duyệt đủ điều kiện, Khoa lập Hội đồng bảo vệ.\n` +
        `6. **Bảo vệ & Chấm điểm:** Hội đồng chấm điểm thang 10 độc lập và ghi nhận xét.`;
    }
    return `Tôi đã ghi nhận câu hỏi: **"${query}"**. Hãy thử bấm các nút gợi ý bên dưới để tra cứu dữ liệu thật về nhóm, đề tài hoặc điểm hội đồng nhé!`;
  }

  /* ──────────────────────────────────────────────────────────────────
     FORMAT TEXT (Safe Markdown rendering)
     ────────────────────────────────────────────────────────────────── */
  function formatMessageText(text) {
    let formatted = (text || '')
      .replace(/&/g, '&amp;')
      .replace(/</g, '&lt;')
      .replace(/>/g, '&gt;');

    // Bold **text**
    formatted = formatted.replace(/\*\*(.*?)\*\*/g, '<strong class="font-bold text-slate-900">$1</strong>');

    // Italic *text*
    formatted = formatted.replace(/\*(.*?)\*/g, '<em class="text-slate-600">$1</em>');

    // Code `code`
    formatted = formatted.replace(/`(.*?)`/g, '<code class="px-1.5 py-0.5 rounded bg-sky-100 text-sky-800 font-mono text-[10px]">$1</code>');

    // Paragraphs & newlines
    formatted = formatted.replace(/\n\n/g, '<div class="h-2"></div>');
    formatted = formatted.replace(/\n/g, '<br/>');

    return formatted;
  }

  /* ──────────────────────────────────────────────────────────────────
     UI INTERACTION: APPEND MESSAGE
     ────────────────────────────────────────────────────────────────── */
  function appendMessage(sender, text, shouldSave = true) {
    const isBot = sender === 'bot';
    const msgEl = document.createElement('div');
    msgEl.className = `flex gap-2 ${isBot ? 'items-start' : 'items-start justify-end'} text-xs`;

    if (isBot) {
      msgEl.innerHTML = `
        <div class="w-6 h-6 rounded-full bg-[#006DA8] text-white flex items-center justify-center shrink-0 mt-0.5 shadow-xs">
          <svg class="w-3.5 h-3.5" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
            <path d="M22 10v6M2 10l10-5 10 5-10 5z"></path>
            <path d="M6 12v5c3 3 9 3 12 0v-5"></path>
          </svg>
        </div>
        <div class="max-w-[88%]">
          <div class="bg-white p-3 rounded-2xl rounded-tl-xs border border-slate-200 shadow-xs text-slate-700 leading-relaxed">
            ${formatMessageText(text)}
          </div>
        </div>
      `;
    } else {
      msgEl.innerHTML = `
        <div class="max-w-[88%]">
          <div class="bg-[#006DA8] p-3 rounded-2xl rounded-tr-xs text-white shadow-xs leading-relaxed text-left">
            ${formatMessageText(text)}
          </div>
        </div>
      `;
    }

    messagesContainer.appendChild(msgEl);
    messagesContainer.scrollTop = messagesContainer.scrollHeight;

    if (shouldSave) {
      saveMessageHistory(sender, text);
    }
  }

  /* ──────────────────────────────────────────────────────────────────
     STORAGE & PERSISTENCE
     ────────────────────────────────────────────────────────────────── */
  function saveMessageHistory(sender, text) {
    try {
      let history = JSON.parse(sessionStorage.getItem(STORAGE_KEY) || '[]');
      history.push({ sender, text });
      if (history.length > 40) history = history.slice(-40);
      sessionStorage.setItem(STORAGE_KEY, JSON.stringify(history));
    } catch (e) {
      console.warn('Could not save chat history', e);
    }
  }

  function loadMessageHistory() {
    try {
      const history = JSON.parse(sessionStorage.getItem(STORAGE_KEY) || '[]');
      if (history.length > 0) {
        history.forEach(item => appendMessage(item.sender, item.text, false));
        return true;
      }
    } catch (e) {
      console.warn('Could not load chat history', e);
    }
    return false;
  }

  function clearMessageHistory() {
    sessionStorage.removeItem(STORAGE_KEY);
    messagesContainer.innerHTML = '';
    sendWelcomeMessage();
  }

  /* ──────────────────────────────────────────────────────────────────
     WELCOME MESSAGE & CHIPS
     ────────────────────────────────────────────────────────────────── */
  function sendWelcomeMessage() {
    const roleTitle = roleNames[userRole] || userRole;
    let welcome = `Xin chào **${userName}** (${roleTitle})! 👋\n\n` +
      `Tôi là **Trợ lý Đồ án UTE**, đã kết nối trực tiếp với **Cơ sở dữ liệu thật** của hệ thống.\n\n` +
      `Bạn có thể bấm các câu hỏi nhanh bên dưới để tra cứu thông tin chính xác từ CSDL:`;

    appendMessage('bot', welcome, true);
  }

  function renderPromptChips() {
    promptChips.innerHTML = '';
    const chips = roleChips[userRole] || roleChips.GUEST;

    chips.forEach(c => {
      const chipBtn = document.createElement('button');
      chipBtn.type = 'button';
      chipBtn.className = 'whitespace-nowrap px-3 py-1.5 rounded-lg bg-slate-100 hover:bg-sky-50 text-slate-700 hover:text-[#006DA8] text-[11px] font-medium border border-slate-200 transition-colors shrink-0';
      chipBtn.textContent = c.text;
      chipBtn.addEventListener('click', () => {
        handleUserQuery(c.query);
      });
      promptChips.appendChild(chipBtn);
    });
  }

  /* ──────────────────────────────────────────────────────────────────
     QUERY HANDLER WITH REAL DATABASE CALL
     ────────────────────────────────────────────────────────────────── */
  async function handleUserQuery(queryText) {
    if (!queryText || !queryText.trim()) return;

    // 1. User message
    appendMessage('user', queryText.trim(), true);
    chatInput.value = '';

    // 2. Show typing
    typingIndicator.classList.remove('hidden');
    messagesContainer.scrollTop = messagesContainer.scrollHeight;

    // 3. Query Real Backend Data
    const realResponse = await queryRealBackendData(queryText);

    // 4. Hide typing & show response
    typingIndicator.classList.add('hidden');
    appendMessage('bot', realResponse, true);
  }

  /* ──────────────────────────────────────────────────────────────────
     TOGGLE & EVENT LISTENERS
     ────────────────────────────────────────────────────────────────── */
  function toggleChatBox(forceOpen = null) {
    const isHidden = chatBox.classList.contains('hidden');
    const shouldOpen = forceOpen !== null ? forceOpen : isHidden;

    if (shouldOpen) {
      chatBox.classList.remove('hidden');
      sessionStorage.setItem(OPEN_STATE_KEY, '1');
      messagesContainer.scrollTop = messagesContainer.scrollHeight;
      setTimeout(() => chatInput.focus(), 150);
    } else {
      chatBox.classList.add('hidden');
      sessionStorage.removeItem(OPEN_STATE_KEY);
    }
  }

  toggleBtn.addEventListener('click', (e) => {
    e.preventDefault();
    toggleChatBox();
  });

  closeBtn.addEventListener('click', () => toggleChatBox(false));

  clearBtn.addEventListener('click', () => {
    if (confirm('Làm mới cuộc trò chuyện này?')) {
      clearMessageHistory();
    }
  });

  chatForm.addEventListener('submit', (e) => {
    e.preventDefault();
    handleUserQuery(chatInput.value);
  });

  /* ──────────────────────────────────────────────────────────────────
     INIT (Detects role switch and refreshes conversation)
     ────────────────────────────────────────────────────────────────── */
  function init() {
    renderPromptChips();

    const currentActiveIdentity = `${userRole}:${userId}`;
    const previousActiveIdentity = sessionStorage.getItem(ACTIVE_ROLE_USER_KEY);

    // If role or user has changed compared to last visit, start a fresh conversation!
    if (previousActiveIdentity && previousActiveIdentity !== currentActiveIdentity) {
      sessionStorage.setItem(ACTIVE_ROLE_USER_KEY, currentActiveIdentity);
      messagesContainer.innerHTML = '';
      sendWelcomeMessage();
    } else {
      sessionStorage.setItem(ACTIVE_ROLE_USER_KEY, currentActiveIdentity);
      const hadHistory = loadMessageHistory();
      if (!hadHistory) {
        sendWelcomeMessage();
      }
    }

    if (sessionStorage.getItem(OPEN_STATE_KEY) === '1') {
      chatBox.classList.remove('hidden');
      messagesContainer.scrollTop = messagesContainer.scrollHeight;
    }
  }

  if (document.readyState === 'loading') {
    document.addEventListener('DOMContentLoaded', init);
  } else {
    init();
  }
})();
