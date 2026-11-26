// HS Startpagina – service worker
// Pagina's: eerst netwerk, bij geen verbinding de laatst bewaarde versie.
// Live data (Worker, NS, kaarttegels, weer) wordt nooit bewaard: alleen eigen bestanden.
const CACHE = 'hs-v4';
const SHELL = ['./', 'index.html', 'manifest.json', 'images/icon-192.png', 'images/icon-512.png', 'images/apple-touch-icon.png'];

self.addEventListener('install', e => {
  e.waitUntil(caches.open(CACHE).then(c => c.addAll(SHELL)).then(() => self.skipWaiting()));
});

self.addEventListener('activate', e => {
  e.waitUntil(caches.keys()
    .then(ks => Promise.all(ks.filter(k => k !== CACHE).map(k => caches.delete(k))))
    .then(() => self.clients.claim()));
});

const OFFLINE = `<!doctype html><html lang="nl"><meta charset="utf-8"><meta name="viewport" content="width=device-width,initial-scale=1">
<title>HS – offline</title><body style="margin:0;min-height:100vh;display:grid;place-items:center;background:#060b18;color:#eef3fc;font-family:system-ui,sans-serif;text-align:center">
<div><h1 style="margin:0 0 8px">Geen verbinding</h1><p style="margin:0;color:#9aaec8">Pas de connexion</p>
<button onclick="location.reload()" style="margin-top:20px;font:inherit;padding:10px 18px;border-radius:10px;border:0;background:#4d7cff;color:#fff">Opnieuw · Réessayer</button></div>`;

self.addEventListener('fetch', e => {
  const r = e.request, u = new URL(r.url);
  if (r.method !== 'GET' || u.origin !== location.origin) return; // Worker, tegels, API's: gewoon netwerk
  const page = r.mode === 'navigate' || /\.html?$/.test(u.pathname) || u.pathname.endsWith('/');
  if (page) {
    // altijd bij de server navragen (GitHub Pages laat browsers pagina's anders 10 min bewaren)
    e.respondWith(fetch(u.href, { cache: 'no-cache', credentials: 'same-origin' }).then(res => {
      if (res.ok) { const c = res.clone(); caches.open(CACHE).then(k => k.put(r, c)); }
      return res;
    }).catch(() => caches.match(r, { ignoreSearch: true })
      .then(m => m || (/(\/|index\.html)$/.test(u.pathname) ? caches.match('index.html') : null))
      .then(m => m || new Response(OFFLINE, { headers: { 'Content-Type': 'text/html; charset=utf-8' } }))));
    return;
  }
  // Eigen bestanden (iconen, afbeeldingen): bewaarde versie tonen, op de achtergrond verversen.
  // Achtergronden (groot) niet bewaren.
  if (u.pathname.includes('/achtergronden/')) return;
  e.respondWith(caches.match(r).then(m => {
    const net = fetch(r).then(res => {
      if (res.ok) { const c = res.clone(); caches.open(CACHE).then(k => k.put(r, c)); }
      return res;
    });
    return m || net;
  }));
});
