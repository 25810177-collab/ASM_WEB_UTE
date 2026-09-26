<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<c:set var="ctx" value="${pageContext.request.contextPath}" />
<c:set var="currentRole" value="${empty sessionScope.userRole ? (empty sessionScope.user ? 'GUEST' : sessionScope.user.role) : sessionScope.userRole}" />
<c:set var="currentUserFullName" value="${empty sessionScope.user ? 'Khách' : sessionScope.user.fullName}" />
<c:set var="currentUserId" value="${empty sessionScope.user ? 'guest' : sessionScope.user.id}" />

<!-- AI ASSISTANT ACADEMIC EDUCATION WIDGET -->
<div id="uteAiChatWidget" 
     class="fixed bottom-6 right-6 z-[9999] font-sans antialiased select-none"
     data-user-role="${currentRole}" 
     data-user-name="${currentUserFullName}" 
     data-user-id="${currentUserId}"
     data-context-path="${ctx}">

    <!-- Custom CSS Animations for Academic Education Theme -->
    <style>
        @keyframes academicHaloPulse {
            0%, 100% { transform: scale(1); opacity: 0.3; }
            50% { transform: scale(1.15); opacity: 0.65; }
        }
        @keyframes academicCapFloat {
            0%, 100% { transform: translateY(0px) rotate(0deg); }
            50% { transform: translateY(-3px) rotate(2deg); }
        }
        @keyframes academicTasselSway {
            0%, 100% { transform: rotate(0deg); }
            50% { transform: rotate(-12deg); }
        }
        @keyframes academicStarShine {
            0%, 100% { transform: scale(0.85); opacity: 0.5; }
            50% { transform: scale(1.15); opacity: 1; filter: drop-shadow(0 0 6px rgba(253, 224, 71, 0.9)); }
        }
        .academic-halo {
            animation: academicHaloPulse 3.5s cubic-bezier(0.4, 0, 0.6, 1) infinite;
        }
        .academic-cap-float {
            animation: academicCapFloat 3s ease-in-out infinite;
        }
        .academic-tassel {
            transform-origin: 20px 8px;
            animation: academicTasselSway 2.4s ease-in-out infinite alternate;
        }
        .academic-star {
            animation: academicStarShine 2s ease-in-out infinite;
        }
    </style>

    <!-- Floating Trigger Button with Academic Graduation Cap Emblem -->
    <div class="relative group flex items-center justify-center">
        <!-- Ambient Glowing Blue Aura -->
        <span class="academic-halo absolute inset-0 rounded-full bg-gradient-to-tr from-sky-400 via-[#006DA8] to-indigo-600 blur-md pointer-events-none"></span>

        <button id="uteAiToggleBtn" 
                type="button" 
                aria-label="Mở Trợ lý Đồ án UTE"
                class="relative flex items-center justify-center w-14 h-14 rounded-full bg-gradient-to-tr from-[#005584] via-[#006DA8] to-[#0284c7] text-white shadow-xl hover:shadow-2xl hover:scale-108 active:scale-95 transition-all duration-300 border border-white/30">

            <!-- Animated Academic Education Graduation Cap & Knowledge Icon -->
            <div class="academic-cap-float relative w-8 h-8 flex items-center justify-center pointer-events-none">
                <svg class="w-8 h-8 text-white drop-shadow-[0_2px_8px_rgba(0,0,0,0.25)]" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round">
                    <!-- Graduation Cap Diamond Top -->
                    <path d="M22 10v6M2 10l10-5 10 5-10 5z" fill="rgba(255,255,255,0.15)"></path>
                    <!-- Skullcap Base -->
                    <path d="M6 12v5c3 3 9 3 12 0v-5"></path>
                    <!-- Cap Tassel with Sway animation -->
                    <g class="academic-tassel">
                        <path d="M22 10v5" stroke="#fde047" stroke-width="2"></path>
                        <circle cx="22" cy="15.5" r="1" fill="#fde047"></circle>
                    </g>
                </svg>

                <!-- Small Glowing Academic Knowledge Sparkle -->
                <svg class="academic-star absolute -top-1 right-0 w-2.5 h-2.5 text-amber-300" viewBox="0 0 24 24" fill="currentColor">
                    <path d="M12 0L14.4 9.6L24 12L14.4 14.4L12 24L9.6 14.4L0 12L9.6 9.6L12 0Z" />
                </svg>
            </div>

            <!-- Online green badge -->
            <span class="absolute top-0 right-0 flex h-3 w-3">
                <span class="animate-ping absolute inline-flex h-full w-full rounded-full bg-emerald-400 opacity-75"></span>
                <span class="relative inline-flex rounded-full h-3 w-3 bg-emerald-400 border-2 border-white"></span>
            </span>
        </button>
    </div>

    <!-- Clean Chatbox Modal / Window -->
    <div id="uteAiChatBox" 
         class="hidden fixed sm:absolute bottom-0 right-0 sm:bottom-18 sm:right-0 w-screen sm:w-[400px] h-[100dvh] sm:h-[560px] bg-white sm:rounded-2xl shadow-2xl sm:border sm:border-slate-200 flex flex-col overflow-hidden transition-all duration-200 z-[10000]">
        
        <!-- Clean Minimalist Header with Academic Cap Branding -->
        <div class="px-4 py-3.5 bg-gradient-to-r from-[#005584] via-[#006DA8] to-[#0284c7] text-white flex items-center justify-between shrink-0 shadow-sm">
            <div class="flex items-center gap-2.5">
                <div class="w-8 h-8 rounded-full bg-white/20 flex items-center justify-center shrink-0 border border-white/30">
                    <!-- Academic Graduation Cap Icon in Header -->
                    <svg class="w-4.5 h-4.5 text-white" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                        <path d="M22 10v6M2 10l10-5 10 5-10 5z"></path>
                        <path d="M6 12v5c3 3 9 3 12 0v-5"></path>
                    </svg>
                </div>
                <div>
                    <h4 class="font-bold text-sm leading-tight">Trợ lý Học thuật UTE</h4>
                    <p class="text-[11px] text-sky-100 flex items-center gap-1.5 mt-0.5">
                        <span class="w-1.5 h-1.5 rounded-full bg-emerald-400"></span>
                        <span id="uteAiUserRoleLabel">Kết nối CSDL thật</span>
                    </p>
                </div>
            </div>

            <!-- Header Action Controls -->
            <div class="flex items-center gap-1">
                <button type="button" 
                        id="uteAiClearChatBtn" 
                        title="Làm mới cuộc trò chuyện" 
                        class="p-2 rounded-lg text-sky-100 hover:text-white hover:bg-white/10 transition-colors">
                    <svg class="w-4 h-4" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                        <path d="M3 6h18"></path>
                        <path d="M19 6v14c0 1-1 2-2 2H7c-1 0-2-1-2-2V6"></path>
                        <path d="M8 6V4c0-1 1-2 2-2h4c1 0 2 1 2 2v2"></path>
                    </svg>
                </button>
                <button type="button" 
                        id="uteAiCloseBtn" 
                        title="Đóng" 
                        class="p-2 rounded-lg text-sky-100 hover:text-white hover:bg-white/10 transition-colors">
                    <svg class="w-4 h-4" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                        <line x1="18" y1="6" x2="6" y2="18"></line>
                        <line x1="6" y1="6" x2="18" y2="18"></line>
                    </svg>
                </button>
            </div>
        </div>

        <!-- Messages Container -->
        <div id="uteAiMessagesContainer" class="flex-1 p-4 overflow-y-auto space-y-3 bg-slate-50/60 scroll-smooth text-xs select-text">
            <!-- Messages inserted dynamically via JS -->
        </div>

        <!-- Typing Indicator -->
        <div id="uteAiTypingIndicator" class="hidden px-4 py-2 bg-slate-50 border-t border-slate-100 flex items-center gap-2 text-slate-400 text-xs shrink-0">
            <div class="flex items-center gap-1">
                <span class="w-1.5 h-1.5 rounded-full bg-[#006DA8] animate-bounce" style="animation-delay: 0ms"></span>
                <span class="w-1.5 h-1.5 rounded-full bg-[#006DA8] animate-bounce" style="animation-delay: 150ms"></span>
                <span class="w-1.5 h-1.5 rounded-full bg-[#006DA8] animate-bounce" style="animation-delay: 300ms"></span>
            </div>
            <span class="text-[11px] text-slate-500 font-medium">Đang truy vấn CSDL thật...</span>
        </div>

        <!-- Suggested Prompt Chips -->
        <div id="uteAiPromptChips" class="px-3 py-2 bg-white border-t border-slate-100 flex items-center gap-1.5 overflow-x-auto no-scrollbar shrink-0">
            <!-- Chips dynamically loaded based on user role -->
        </div>

        <!-- Chat Input Form -->
        <div class="p-3 bg-white border-t border-slate-200 shrink-0">
            <form id="uteAiChatForm" class="flex items-center gap-2" onsubmit="return false;">
                <input type="text" 
                       id="uteAiChatInput" 
                       autocomplete="off"
                       placeholder="Hỏi về nhóm, đề tài, điểm, hạn chót..." 
                       class="flex-1 px-3.5 py-2.5 bg-slate-100 text-slate-800 text-xs rounded-xl border border-slate-200 focus:border-[#006DA8] focus:bg-white focus:outline-none transition-all placeholder:text-slate-400" />

                <button type="submit" 
                        id="uteAiSendBtn" 
                        aria-label="Gửi"
                        class="w-9 h-9 rounded-xl bg-[#006DA8] hover:bg-[#005584] text-white flex items-center justify-center transition-all shrink-0 active:scale-95">
                    <svg class="w-4 h-4" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                        <line x1="22" y1="2" x2="11" y2="13"></line>
                        <polygon points="22 2 15 22 11 13 2 9 22 2"></polygon>
                    </svg>
                </button>
            </form>
        </div>
    </div>
</div>

<!-- Load Dedicated AI Chatbox Logic Script -->
<script src="${pageContext.request.contextPath}/assets/js/ai-chatbox.js?v=2.2.0"></script>