# 🚆 Hoggynesische Spoorwegen (HS) · Chemin de fer Hoggynésiens

De website van de **Hoggynesische Spoorwegen**: een startpagina voor de browser met live vertrektijden vanaf Assen, snelle links naar kaartjes en reizen, en een paar losse HS-tools.

*Le site des Chemins de fer Hoggynésiens : une page d'accueil bilingue avec les départs en direct depuis Assen, des liens vers les billets et quelques outils HS.*

🔗 **Live:** <https://mister-yoshi.github.io>

---

## De startpagina (`index.html`)

Een app-achtige pagina in de HS-huisstijl: rood, blauw en grijs, met het treintje dat over de rails onder de kop rijdt. Alles op de pagina is tweetalig (Nederlands en Frans). Met de knoppen **NL / FR** kies je Nederlands, Frans of allebei.

Je wisselt van tabblad via de zijbalk (op de telefoon een balk onderin) of met de toetsen **1–5**. Met **/** spring je naar de zoekbalk.

### 🏠 Start
- Begroeting die past bij het tijdstip, met datum en klok
- **Zoekbalk**: zoekt tijdens het typen in alle links van de pagina en anders via Google (bedienbaar met de pijltjestoetsen)
- **Volgende trein vanaf Assen**: tijd, bestemming, spoor en hoe lang het nog duurt. Als de treindienst voorbij is, zie je de eerste trein van de ochtend
- **Netwerkstatus** van de HS, met de knop Beheer
- Snelle links, onder andere Treinposities, NS, NMBS, SNCF Connect en DB

### 🚉 Vertrek
- **Live vertrekstaat van station Assen**, met echte NS-data:
  - tijd, vertraging, bestemming, spoor (met een opvallend bordje bij een spoorwijziging) en soort trein (IC of SPR)
  - **materieel** per trein, zoals `VIRM-6` of `ICM-3 + ICM-4`, met een waarschuwing als de trein korter is dan normaal
  - meldingen van NS en het aantal verstoringen, met een link naar ns.nl/storingen
  - **"Dienst voorbij"**: na de laatste trein verschijnt een rustig scherm met de eerste trein van de ochtend
- **Live busborden** voor *Busstation Marsdijk* en *Noorderstaete* in Assen (via drgl.nl)
- Bewust **zonder animaties**: vaste maten en vaste rijen, zodat er niets verspringt. Ververst elke minuut
- Zijn er geen gegevens, dan staat er "Geen live data". De pagina toont nooit verzonnen vertrektijden

### 🎫 Kaartjes
- Kaartjes kopen bij **NS**, **NMBS** en **HS Ticketing**

### 🌍 Internationaal
- **NMBS / B-Europe**, **NS International**, **Eurostar**, **GoVolta**, **Ouigo** en **SNCF Connect**
- Bij NMBS en Eurostar krijg je eerst een korte **tip** voordat je naar de website gaat

### 🧰 Diensten
- **HS Bijstand**, Gemini, GitHub en de **statusbalk**

### ⚙️ Overig
- **Statusbalk met beheer**: kies *operationeel*, *storing*, *werkzaamheden* of *staking*, eventueel met een eigen tekst in het Nederlands en het Frans. De keuze wordt in de browser opgeslagen
- **Weer en locatie** via GPS (Open-Meteo en OpenStreetMap), elke 10 minuten bijgewerkt
- **Achtergrond**: een willekeurige foto uit de map [`achtergronden/`](achtergronden/)
- Werkt op desktop en op de telefoon

---

## Andere pagina's in deze repo

| Bestand | Wat het is |
| --- | --- |
| [`HS_DMI`](HS_DMI) | **HS DMI**: simulator van een ETCS-cabinescherm, met snelheidsbewaking, remingrijpen en overgangen tussen ETCS en HTC (cabinesein) |
| [`hs_qr_ticketing_styled.html`](hs_qr_ticketing_styled.html) | **HS QR Ticketing**: maakt en scant QR-treinkaartjes |
| [`HS_MentaleBijstand.html`](HS_MentaleBijstand.html) | **HS / CFH Mentale Bijstand**: pagina over mentaal welzijn |
| [`hs_omloop_generator_v5.html`](hs_omloop_generator_v5.html) | **HS Omloop Generator v5**: maakt materieelomlopen |
| [`index2.html`](index2.html) | Oudere versie van de startpagina |
| `manifest.json`, `sw.js` | Onderdelen voor installatie als app (PWA) |

---

## Hoe de live treindata werkt

De NS-API heeft een sleutel nodig, en die mag niet in een openbare repo staan. Daarom loopt alles via een **Cloudflare Worker**:

```
startpagina  →  Cloudflare Worker (met NS-sleutel als secret)  →  NS Reisinformatie API + Virtual Train API
```

- De Worker haalt de vertrektijden van Assen (`ASN`) op en vraagt per trein het materieel op
- Als er geen treinen meer rijden, zoekt de Worker de eerste trein van de ochtend op
- De Worker accepteert alleen verzoeken van `https://mister-yoshi.github.io`. Open je `index.html` rechtstreeks vanaf je computer, dan zie je daarom "Geen live data"
- In de HTML staat alleen het adres van de Worker, **nooit de NS-sleutel**

---

© 2026 Hoggynesische Spoorwegen · Chemin de fer Hoggynésiens · Gemaakt met Gemini & Claude
