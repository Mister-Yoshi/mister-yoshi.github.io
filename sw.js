// HS App Service Worker
const CACHE_NAME = 'hs-app-v1';

self.addEventListener('install', (event) => {
    console.log('HS Service Worker geïnstalleerd.');
});

self.addEventListener('fetch', (event) => {
    // Dit zorgt ervoor dat de app door de PWA-check komt.
    // Voor nu doet hij nog niets speciaals, maar hij is wel verplicht.
    event.respondWith(fetch(event.request).catch(() => {
        return new Response('HS App is offline.');
    }));
});