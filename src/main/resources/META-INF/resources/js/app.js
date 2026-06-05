const API = '/api';
let token = localStorage.getItem('token');
let cliente = JSON.parse(localStorage.getItem('cliente') || 'null');
let chartDashboard = null;
let chartConsumoFull = null;
let featureFlags = { simuladorEconomia: false };

const $ = (sel) => document.querySelector(sel);
const $$ = (sel) => document.querySelectorAll(sel);

const TIPOS_SOLICITACAO = {
    RELIGACAO: 'Religação',
    SEGUNDA_VIA: 'Segunda via',
    TROCA_TITULARIDADE: 'Troca de titularidade',
    RECLAMACAO: 'Reclamação',
    INFORMACAO_CONSUMO: 'Informação de consumo',
    OUTROS: 'Outros'
};

document.addEventListener('DOMContentLoaded', init);

function init() {
    if (token && cliente) {
        checkSession().then((ok) => (ok ? showApp() : showLogin()));
    } else {
        showLogin();
    }

    $('#login-form').addEventListener('submit', onLogin);
    $('#btn-logout').addEventListener('click', onLogout);
    $$('.nav-item').forEach((btn) => btn.addEventListener('click', () => navigate(btn.dataset.page)));
    $('#btn-nova-solicitacao').addEventListener('click', abrirNovaSolicitacao);
    $('#btn-simular-economia')?.addEventListener('click', executarSimulacao);
    $('#reducao-slider')?.addEventListener('input', (e) => {
        $('#reducao-valor').textContent = e.target.value + '%';
    });
    $('#modal-cancel').addEventListener('click', closeModal);
    $('.modal-backdrop').addEventListener('click', closeModal);
}

async function parseError(res, fallback) {
    const err = await res.json().catch(() => ({ message: fallback }));
    return new Error(err.message || err.details?.join(', ') || fallback);
}

async function api(path, options = {}) {
    const headers = { 'Content-Type': 'application/json', ...options.headers };
    if (token) headers['Authorization'] = `Bearer ${token}`;
    const res = await fetch(`${API}${path}`, { ...options, headers });
    if (res.status === 401) {
        clearSession();
        throw new Error('Sessão expirada. Faça login novamente.');
    }
    if (!res.ok) {
        throw await parseError(res, 'Erro na requisição');
    }
    if (res.status === 204) return null;
    return res.json();
}

async function loginApi(identificador, senha) {
    const res = await fetch(`${API}/auth/login`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ identificador, senha })
    });
    if (!res.ok) {
        throw await parseError(res, 'CPF/e-mail ou senha inválidos');
    }
    return res.json();
}

async function checkSession() {
    if (!token) return false;
    const res = await fetch(`${API}/cliente/me`, {
        headers: { Authorization: `Bearer ${token}` }
    });
    if (res.status === 401) {
        clearSession();
        return false;
    }
    return res.ok;
}

function showLogin() {
    $('#login-view').classList.add('active');
    $('#app-view').classList.remove('active');
}

function showApp() {
    $('#login-view').classList.remove('active');
    $('#app-view').classList.add('active');
    $('#user-name').textContent = cliente.nome.split(' ')[0];
    $('#user-instalacao').textContent = cliente.numeroInstalacao;
    loadFeatureFlags().then(() => navigate('dashboard'));
}

async function loadFeatureFlags() {
    try {
        const info = await api('/info');
        featureFlags = info.features || { simuladorEconomia: false };
        const nav = $('#nav-simulador-economia');
        if (nav) nav.classList.toggle('hidden', !featureFlags.simuladorEconomia);
    } catch (_) {
        featureFlags = { simuladorEconomia: false };
    }
}

async function onLogin(e) {
    e.preventDefault();
    const errEl = $('#login-error');
    errEl.classList.add('hidden');
    try {
        const data = await loginApi(
            $('#identificador').value.trim(),
            $('#senha').value
        );
        token = data.token;
        cliente = data.cliente;
        localStorage.setItem('token', token);
        localStorage.setItem('cliente', JSON.stringify(cliente));
        showApp();
        toast('Bem-vindo(a), ' + cliente.nome.split(' ')[0] + '!');
    } catch (err) {
        errEl.textContent = err.message;
        errEl.classList.remove('hidden');
    }
}

function clearSession() {
    token = null;
    cliente = null;
    localStorage.removeItem('token');
    localStorage.removeItem('cliente');
}

async function onLogout() {
    try { await api('/auth/logout', { method: 'POST' }); } catch (_) {}
    clearSession();
    showLogin();
}

function navigate(page) {
    $$('.nav-item').forEach((b) => b.classList.toggle('active', b.dataset.page === page));
    $$('.page').forEach((p) => p.classList.toggle('active', p.id === `page-${page}`));
    const loaders = {
        dashboard: loadDashboard,
        faturas: loadFaturas,
        consumo: loadConsumo,
        solicitacoes: loadSolicitacoes,
        notificacoes: loadNotificacoes,
        conta: loadConta,
        'simulador-economia': loadSimuladorEconomia
    };
    loaders[page]?.();
}

function formatMoney(v) {
    return Number(v).toLocaleString('pt-BR', { style: 'currency', currency: 'BRL' });
}

function formatDate(d) {
    if (!d) return '—';
    const [y, m, day] = d.split('-');
    return `${day}/${m}/${y}`;
}

function formatDateTime(d) {
    if (!d) return '—';
    return new Date(d).toLocaleString('pt-BR');
}

function labelStatus(s) {
    const map = { PENDENTE: 'Pendente', PAGA: 'Paga', VENCIDA: 'Vencida', ABERTA: 'Aberta', EM_ANDAMENTO: 'Em andamento', CONCLUIDA: 'Concluída' };
    return map[s] || s;
}

async function loadDashboard() {
    try {
        const d = await api('/dashboard');
        updateNotifBadge(d.notificacoesNaoLidas);
        $('#stats-grid').innerHTML = `
            <div class="stat-card"><span class="label">Total pendente</span><div class="value">${formatMoney(d.totalPendente)}</div></div>
            <div class="stat-card"><span class="label">Faturas em aberto</span><div class="value">${d.faturasPendentes}</div></div>
            <div class="stat-card"><span class="label">Consumo atual</span><div class="value">${d.consumoAtualKwh} kWh</div></div>
            <div class="stat-card"><span class="label">Média da região</span><div class="value">${d.mediaRegiaoKwh} kWh</div></div>
            <div class="stat-card"><span class="label">Solicitações abertas</span><div class="value">${d.solicitacoesAbertas}</div></div>
        `;
        renderProximaFatura(d.proximaFatura);
        renderChart('#chart-consumo', d.historicoConsumo, chartDashboard, (c) => { chartDashboard = c; });
    } catch (err) { toast(err.message, true); }
}

function renderProximaFatura(f) {
    const el = $('#proxima-fatura');
    if (!f) {
        el.innerHTML = '<p style="color:var(--text-muted)">Nenhuma fatura pendente no momento.</p>';
        return;
    }
    el.innerHTML = `
        <p><strong>Referência:</strong> ${f.referencia}</p>
        <p><strong>Vencimento:</strong> ${formatDate(f.dataVencimento)}</p>
        <p><strong>Valor:</strong> ${formatMoney(f.valor)}</p>
        <p><strong>Consumo:</strong> ${f.consumoKwh} kWh</p>
        <span class="status status-${f.status}">${labelStatus(f.status)}</span>
        ${f.status !== 'PAGA' ? `<button class="btn btn-primary btn-sm" style="margin-top:1rem" onclick="pagarFatura(${f.id})">Pagar agora</button>` : ''}
    `;
}

function renderChart(canvasSel, historico, existingChart, setChart) {
    const ctx = $(canvasSel);
    if (!ctx || !historico?.length) return;
    const labels = [...historico].reverse().map((h) => h.referencia);
    const consumo = [...historico].reverse().map((h) => h.consumoKwh);
    const media = [...historico].reverse().map((h) => h.mediaRegiao);
    if (existingChart) existingChart.destroy();
    const chart = new Chart(ctx, {
        type: 'bar',
        data: {
            labels,
            datasets: [
                { label: 'Seu consumo', data: consumo, backgroundColor: '#0d6e4f' },
                { label: 'Média região', data: media, backgroundColor: '#f5a623' }
            ]
        },
        options: { responsive: true, plugins: { legend: { position: 'bottom' } }, scales: { y: { beginAtZero: true } } }
    });
    setChart(chart);
}

async function loadFaturas() {
    try {
        const faturas = await api('/faturas');
        $('#faturas-list').innerHTML = `
            <table>
                <thead><tr><th>Referência</th><th>Vencimento</th><th>Valor</th><th>kWh</th><th>Status</th><th>Ações</th></tr></thead>
                <tbody>${faturas.map((f) => `
                    <tr>
                        <td>${f.referencia}</td>
                        <td>${formatDate(f.dataVencimento)}</td>
                        <td>${formatMoney(f.valor)}</td>
                        <td>${f.consumoKwh}</td>
                        <td><span class="status status-${f.status}">${labelStatus(f.status)}</span></td>
                        <td>${f.status !== 'PAGA' ? `<button class="btn btn-primary btn-sm" onclick="pagarFatura(${f.id})">Pagar</button>` : '—'}</td>
                    </tr>
                `).join('')}</tbody>
            </table>`;
    } catch (err) { toast(err.message, true); }
}

window.pagarFatura = function (id) {
    openModal('Pagar fatura', `
        <p>Simule o pagamento da fatura selecionada.</p>
        <label>Forma de pagamento</label>
        <select id="forma-pagamento">
            <option value="PIX">PIX</option>
            <option value="Cartão de crédito">Cartão de crédito</option>
            <option value="Débito automático">Débito automático</option>
            <option value="Boleto">Boleto</option>
        </select>
    `, async () => {
        try {
            await api(`/faturas/${id}/pagar`, {
                method: 'POST',
                body: JSON.stringify({ formaPagamento: $('#forma-pagamento').value })
            });
            closeModal();
            toast('Pagamento registrado com sucesso!');
            loadFaturas();
            loadDashboard();
        } catch (err) { toast(err.message, true); }
    });
};

async function loadConsumo() {
    try {
        const consumo = await api('/consumo');
        renderChart('#chart-consumo-full', consumo, chartConsumoFull, (c) => { chartConsumoFull = c; });
        $('#consumo-table').innerHTML = `
            <table style="margin-top:1.5rem">
                <thead><tr><th>Referência</th><th>Seu consumo</th><th>Média região</th><th>Diferença</th></tr></thead>
                <tbody>${consumo.map((c) => {
                    const diff = c.consumoKwh - c.mediaRegiao;
                    const cls = diff > 0 ? 'color:var(--danger)' : 'color:var(--success)';
                    return `<tr>
                        <td>${c.referencia}</td>
                        <td>${c.consumoKwh} kWh</td>
                        <td>${c.mediaRegiao} kWh</td>
                        <td style="${cls}">${diff > 0 ? '+' : ''}${diff} kWh</td>
                    </tr>`;
                }).join('')}</tbody>
            </table>`;
    } catch (err) { toast(err.message, true); }
}

async function loadSolicitacoes() {
    try {
        const lista = await api('/solicitacoes');
        $('#solicitacoes-list').innerHTML = lista.length ? `
            <table>
                <thead><tr><th>Protocolo</th><th>Tipo</th><th>Descrição</th><th>Status</th><th>Data</th></tr></thead>
                <tbody>${lista.map((s) => `
                    <tr>
                        <td>${s.protocolo}</td>
                        <td>${TIPOS_SOLICITACAO[s.tipo] || s.tipo}</td>
                        <td>${s.descricao}</td>
                        <td><span class="status status-${s.status}">${labelStatus(s.status)}</span></td>
                        <td>${formatDateTime(s.criadoEm)}</td>
                    </tr>
                `).join('')}</tbody>
            </table>` : '<p class="card">Nenhuma solicitação registrada.</p>';
    } catch (err) { toast(err.message, true); }
}

function abrirNovaSolicitacao() {
    const options = Object.entries(TIPOS_SOLICITACAO).map(([k, v]) => `<option value="${k}">${v}</option>`).join('');
    openModal('Nova solicitação', `
        <label>Tipo</label>
        <select id="sol-tipo">${options}</select>
        <label>Descrição</label>
        <textarea id="sol-desc" rows="4" placeholder="Descreva sua solicitação..." required></textarea>
    `, async () => {
        const desc = $('#sol-desc').value.trim();
        if (!desc) { toast('Informe uma descrição', true); return; }
        try {
            await api('/solicitacoes', {
                method: 'POST',
                body: JSON.stringify({ tipo: $('#sol-tipo').value, descricao: desc })
            });
            closeModal();
            toast('Solicitação registrada!');
            loadSolicitacoes();
        } catch (err) { toast(err.message, true); }
    });
}

async function loadNotificacoes() {
    try {
        const lista = await api('/notificacoes');
        const naoLidas = lista.filter((n) => !n.lida).length;
        updateNotifBadge(naoLidas);
        $('#notificacoes-list').innerHTML = lista.map((n) => `
            <div class="notif-item ${n.lida ? '' : 'unread'}" data-id="${n.id}">
                <h4>${n.titulo}</h4>
                <p>${n.mensagem}</p>
                <time>${formatDateTime(n.criadoEm)}</time>
            </div>
        `).join('') || '<p class="card">Sem notificações.</p>';

        $$('.notif-item').forEach((el) => el.addEventListener('click', async () => {
            if (!el.classList.contains('unread')) return;
            try {
                await api(`/notificacoes/${el.dataset.id}/ler`, { method: 'POST' });
                el.classList.remove('unread');
                updateNotifBadge(Math.max(0, (parseInt($('#badge-notif').textContent) || 0) - 1));
            } catch (_) {}
        }));
    } catch (err) { toast(err.message, true); }
}

function updateNotifBadge(count) {
    const badge = $('#badge-notif');
    if (count > 0) {
        badge.textContent = count;
        badge.classList.remove('hidden');
    } else {
        badge.classList.add('hidden');
    }
}

async function loadSimuladorEconomia() {
    if (!featureFlags.simuladorEconomia) {
        toast('Simulador de Economia desabilitado.', true);
        navigate('dashboard');
        return;
    }
    try {
        const resumo = await api('/simulador-economia/resumo');
        $('#simulador-dicas').innerHTML = renderDicas(resumo.dicas);
        await executarSimulacao();
    } catch (err) { toast(err.message, true); }
}

function renderDicas(dicas) {
    return (dicas || []).map((d) => `
        <div class="dica-item ${d.prioridade}">
            <h4>${d.titulo}</h4>
            <p>${d.descricao}</p>
        </div>
    `).join('') || '<p style="color:var(--text-muted)">Sem dicas no momento.</p>';
}

async function executarSimulacao() {
    const reducao = parseInt($('#reducao-slider')?.value || '10', 10);
    try {
        const r = await api('/simulador-economia/simular', {
            method: 'POST',
            body: JSON.stringify({ reducaoPercentual: reducao })
        });
        $('#simulador-dicas').innerHTML = renderDicas(r.dicas);
        $('#simulacao-resultado').innerHTML = `
            <div class="sim-result">
                <div><span class="label">Consumo atual</span><div class="highlight">${r.consumoAtualKwh} kWh</div></div>
                <div><span class="label">Consumo simulado</span><div class="highlight">${r.consumoSimuladoKwh} kWh</div></div>
                <div><span class="label">Conta estimada hoje</span><div>${formatMoney(r.valorAtualEstimado)}</div></div>
                <div><span class="label">Conta simulada</span><div>${formatMoney(r.valorSimuladoEstimado)}</div></div>
            </div>
            <p style="margin-top:1rem"><strong>Economia mensal:</strong> ${formatMoney(r.economiaMensal)}</p>
            <p><strong>Economia anual estimada:</strong> <span style="color:var(--primary);font-weight:700">${formatMoney(r.economiaAnual)}</span></p>
        `;
    } catch (err) { toast(err.message, true); }
}

async function loadConta() {
    try {
        const c = await api('/cliente/me');
        cliente = c;
        localStorage.setItem('cliente', JSON.stringify(cliente));
        $('#conta-detalhes').innerHTML = `
            <div class="conta-field"><label>Nome</label><p>${c.nome}</p></div>
            <div class="conta-field"><label>CPF</label><p>${formatCpf(c.cpf)}</p></div>
            <div class="conta-field"><label>E-mail</label><p>${c.email}</p></div>
            <div class="conta-field"><label>Telefone</label><p>${c.telefone}</p></div>
            <div class="conta-field"><label>Instalação</label><p>${c.numeroInstalacao}</p></div>
            <div class="conta-field"><label>Endereço</label><p>${c.endereco}</p></div>
            <div class="conta-field"><label>Cidade/UF</label><p>${c.cidade} - ${c.uf}</p></div>
            <div class="conta-field"><label>CEP</label><p>${c.cep}</p></div>
            <div class="conta-field"><label>Tarifa</label><p>${c.tipoTarifa}</p></div>
        `;
    } catch (err) { toast(err.message, true); }
}

function formatCpf(cpf) {
    if (cpf.length !== 11) return cpf;
    return cpf.replace(/(\d{3})(\d{3})(\d{3})(\d{2})/, '$1.$2.$3-$4');
}

let modalConfirmHandler = null;

function openModal(title, bodyHtml, onConfirm) {
    $('#modal-title').textContent = title;
    $('#modal-body').innerHTML = bodyHtml;
    modalConfirmHandler = onConfirm;
    $('#modal').classList.remove('hidden');
    $('#modal-confirm').onclick = async () => { if (modalConfirmHandler) await modalConfirmHandler(); };
}

function closeModal() {
    $('#modal').classList.add('hidden');
    modalConfirmHandler = null;
}

function toast(msg, isError = false) {
    const el = $('#toast');
    el.textContent = msg;
    el.style.background = isError ? 'var(--danger)' : 'var(--primary-dark)';
    el.classList.remove('hidden');
    setTimeout(() => el.classList.add('hidden'), 3500);
}
