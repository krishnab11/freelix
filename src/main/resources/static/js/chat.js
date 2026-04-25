/**
 * Freelix Chat — AJAX Polling + File Upload + Reactions
 * Variables injected by Thymeleaf in chat.html:
 *   projectId, receiverId, currentUserId, lastId
 */

document.addEventListener('DOMContentLoaded', function () {
    const chatMessages = document.getElementById('chatMessages');
    const chatForm     = document.getElementById('chatForm');
    const messageInput = document.getElementById('messageInput');
    const fileInput    = document.getElementById('fileInput');

    if (!chatMessages) return;

    // Scroll to bottom on load
    scrollToBottom();

    // ── Send message (text + optional file) via FormData ─────────────────────
    if (chatForm) {
        chatForm.addEventListener('submit', function (e) {
            e.preventDefault();
            const content = messageInput.value.trim();
            const file    = fileInput && fileInput.files[0];

            if (!content && !file) return;

            const fd = new FormData();
            fd.append('projectId',  projectId);
            fd.append('receiverId', receiverId);
            if (content) fd.append('content', content);
            if (file)    fd.append('file', file);

            const sendBtn = document.getElementById('sendBtn');
            if (sendBtn) sendBtn.disabled = true;

            fetch('/chat/send', { method: 'POST', body: fd })
                .then(res => res.json())
                .then(msg => {
                    appendMessage(msg, true);
                    messageInput.value = '';
                    if (fileInput) fileInput.value = '';
                    if (typeof clearAttach === 'function') clearAttach();
                    lastId = msg.id;
                    scrollToBottom();
                })
                .catch(err => console.error('Send error:', err))
                .finally(() => { if (sendBtn) sendBtn.disabled = false; });
        });
    }

    // ── Poll for new messages every 2 seconds ─────────────────────────────────
    if (receiverId && receiverId !== 0) {
        setInterval(function () {
            fetch(`/chat/poll?projectId=${projectId}&lastId=${lastId}`)
                .then(res => res.json())
                .then(messages => {
                    if (messages.length > 0) {
                        messages.forEach(msg => {
                            appendMessage(msg, msg.senderId === currentUserId);
                            lastId = msg.id;
                        });
                        scrollToBottom();
                    }
                })
                .catch(err => console.error('Poll error:', err));
        }, 2000);
    }

    // ── Poll notification / message badge every 30 seconds ───────────────────
    setInterval(function () {
        fetch('/notifications/count')
            .then(r => r.json())
            .then(data => {
                document.querySelectorAll('.notif-badge').forEach(el => {
                    el.textContent = data.count;
                    el.style.display = data.count > 0 ? 'inline' : 'none';
                });
            })
            .catch(() => {});
    }, 30000);

    // ── Build message bubble ──────────────────────────────────────────────────
    function appendMessage(msg, isSent) {
        const wrapper = document.createElement('div');
        wrapper.id = 'msg-' + msg.id;
        wrapper.className = `d-flex align-items-end gap-2 msg-row ${isSent ? 'flex-row-reverse' : ''}`;

        const avatar = document.createElement('div');
        avatar.className = 'avatar-circle avatar-sm';
        avatar.textContent = msg.senderName ? msg.senderName.charAt(0).toUpperCase() : '?';

        const outerDiv = document.createElement('div');
        outerDiv.style.cssText = 'position:relative;max-width:70%;min-width:80px;';

        const bubble = document.createElement('div');
        bubble.className = `chat-bubble ${isSent ? 'bubble-sent' : 'bubble-received'}`;

        // Text content
        if (msg.content) {
            const text = document.createElement('div');
            text.textContent = msg.content;
            bubble.appendChild(text);
        }

        // Attachment
        if (msg.attachmentUrl) {
            const attachDiv = document.createElement('div');
            attachDiv.className = 'bubble-attachment mt-2';
            const imgExts = ['.jpg', '.jpeg', '.png', '.gif', '.webp'];
            const isImage = imgExts.some(ext => (msg.attachmentName || '').toLowerCase().includes(ext));
            if (isImage) {
                const img = document.createElement('img');
                img.src = msg.attachmentUrl;
                img.style.maxWidth = '220px';
                img.style.borderRadius = '.5rem';
                attachDiv.appendChild(img);
            } else {
                const link = document.createElement('a');
                link.href = msg.attachmentUrl;
                link.target = '_blank';
                link.className = 'pdf-link';
                link.innerHTML = `<i class="bi bi-file-earmark-pdf"></i> ${msg.attachmentName || 'File'}`;
                attachDiv.appendChild(link);
            }
            bubble.appendChild(attachDiv);
        }

        // Reaction pill
        if (msg.reaction) {
            const pill = document.createElement('div');
            pill.className = 'reaction-pill';
            pill.textContent = msg.reaction;
            outerDiv.appendChild(pill);
        }

        const time = document.createElement('small');
        time.className = 'bubble-time';
        time.textContent = formatTime(msg.sentAt);
        bubble.appendChild(time);

        // Emoji picker button
        const reactBtn = document.createElement('button');
        reactBtn.className = 'btn btn-link p-0 react-btn';
        reactBtn.setAttribute('data-id', msg.id);
        reactBtn.style.cssText = 'font-size:.75rem;color:#888;opacity:0;transition:opacity .2s;';
        reactBtn.textContent = '😊';
        reactBtn.onclick = function() { toggleEmojiPicker(this); };

        outerDiv.appendChild(bubble);
        outerDiv.appendChild(reactBtn);

        // Emoji picker popup
        const picker = document.createElement('div');
        picker.className = 'emoji-picker-popup';
        picker.id = 'ep-' + msg.id;
        ['👍','❤️','😂','😮','😢','🔥'].forEach(e => {
            const sp = document.createElement('span');
            sp.textContent = e;
            sp.setAttribute('data-msg-id', msg.id);
            sp.setAttribute('data-emoji', e);
            sp.onclick = function() { sendReaction(this); };
            picker.appendChild(sp);
        });
        outerDiv.appendChild(picker);

        wrapper.appendChild(avatar);
        wrapper.appendChild(outerDiv);
        chatMessages.appendChild(wrapper);

        // Hover reveal
        wrapper.addEventListener('mouseenter', () => reactBtn.style.opacity = '1');
        wrapper.addEventListener('mouseleave', () => reactBtn.style.opacity = '0');
    }

    function scrollToBottom() {
        chatMessages.scrollTop = chatMessages.scrollHeight;
    }

    function formatTime(dateStr) {
        if (!dateStr) return '';
        try {
            const d = new Date(dateStr);
            return d.getHours().toString().padStart(2, '0') + ':' +
                   d.getMinutes().toString().padStart(2, '0');
        } catch (e) {
            return dateStr;
        }
    }
});
