# 🚆 Hoggynesische Spoorwegen (HS) · Chemin de fer Hoggynésiens

De website van de **Hoggynesische Spoorwegen**: een startpagina voor de browser met live vertrektijden vanaf Assen, snelle links naar kaartjes en reizen, en een paar losse HS-tools.

*Le site des Chemins de fer Hoggynésiens : une page d'accueil bilingue avec les départs en direct depuis Assen, des liens vers les billets et quelques outils HS.*

🔗 **Live:** <https://mister-yoshi.github.io>

---

## De startpagina (`index.html`)

Een app-achtige pagina in de HS-huisstijl: rood, blauw en grijs, met het treintje dat over de rails onder de kop rijdt. Alles op de pagina is tweetalig (Nederlands en Frans). Met de knoppen **NL / FR** kies je Nederlands, Frans of allebei.

Je wisselt van tabblad via de zijbalk (op de telefoon een balk onderin) of met de toetsen **1–7**. Met **/** spring je naar de zoekbalk.

### 🏠 Start
- Begroeting die past bij het tijdstip, met datum en klok
- **Zoekbalk**: zoekt tijdens het typen in alle links van de pagina en anders via Google (bedienbaar met de pijltjestoetsen)
- **Volgende trein vanaf Assen**: tijd, bestemming, spoor en hoe lang het nog duurt. Als de treindienst voorbij is, zie je de eerste trein van de ochtend
- **Netwerkstatus** van de HS, met de knop Beheer
- Snelle links, onder andere Treinposities, NS, NMBS, SNCF Connect en DB

### 🚉 Vertrek
- Drie onderdelen als knoppen bovenaan: **Trein**, **Bus** en **Storingen** (met het aantal storingen). Je laatste keuze wordt onthouden, en een storing rond Assen staat als balk boven de vertrekstaat
- **Live vertrekstaat**, standaard station Assen. Met **📍 GPS** kies je het dichtstbijzijnde station, met **↺ Assen** ga je terug (keuze wordt onthouden). Echte NS-data:
  - tijd, vertraging, bestemming, spoor (met een opvallend bordje bij een spoorwijziging) en soort trein (IC of SPR)
  - **tik op een trein** voor alle tussenhaltes met tijden, vertraging en sporen
  - **materieel** per trein, zoals `VIRM-6` of `ICM-3 + ICM-4`, met een waarschuwing als de trein korter is dan normaal
  - meldingen van NS en het aantal verstoringen, met een link naar ns.nl/storingen
  - **"Dienst voorbij"**: na de laatste trein verschijnt een rustig scherm met de eerste trein van de ochtend
- **Storingen en werkzaamheden van NS**: actuele storingen (rood) en werkzaamheden (oranje), met oorzaak, verwachte duur, extra reistijd en vervangend vervoer. Meldingen rond Assen staan bovenaan
- **Live busborden** in dezelfde stijl als de vertrekstaat, standaard voor *Busstation Marsdijk* en *Noorderstaete* in Assen. Tik op een busrit voor de tussenhaltes en het ritnummer. Met **Wijzig halte** zoek en kies je zelf een andere bushalte (data via [Transitous](https://transitous.org/sources/), open data o.a. van OVapi/NDOV)
- Bewust **zonder animaties**: vaste maten en vaste rijen, zodat er niets verspringt. Ververst elke minuut
- Zijn er geen gegevens, dan staat er "Geen live data". De pagina toont nooit verzonnen vertrektijden

### 🗺️ Kaart
- **Live kaart met alle treinen die NS volgt**, elke 10 seconden bijgewerkt (posities via de Virtual Train API van NS)
- Kleuren per soort: geel IC, blauw SPR, grijs overig, met een pijltje voor de rijrichting. Filter op IC, SPR of Overig
- Tik op een trein voor het treinnummer en de snelheid, de **tussenhaltes** of de trein openen in de **OBIS**
- Slepen, scrollen of knijpen om te zoomen, knoppen voor Assen en je eigen locatie. Stations verschijnen bij inzoomen
- Kaarttegels © OpenStreetMap-bijdragers (standaard OSM-tegels, donker gemaakt in de browser)

### 🧭 Reisplanner
- **Reis plannen met de NS-API**: van station naar station, op vertrek- of aankomsttijd
- Stations zoeken tijdens het typen (ook Belgische en Duitse stations), met knop ⇅ om om te wisselen. De laatste keuze wordt onthouden
- Per reis: vertrek- en aankomsttijd met vertraging, reisduur, aantal overstappen, soorten treinen en drukte
- Open een reis voor alle delen: tijden, sporen (met spoorwijziging), richting, overstaptijd, meldingen en vervallen treinen
- Keuze **Trein** of **Bus, tram en metro**: de tweede planner (via Transitous) plant alleen met bus, tram, metro en veer (treinen via de NS-planner), met per rit een knop **Tussenhaltes**
- Knoppen **Eerder** en **Later**
- Net als Vertrek zonder animaties

### 🎫 Kaartjes
- Kaartjes kopen bij **NS** en **NMBS**

### 🌍 Internationaal
- **NMBS / B-Europe**, **NS International**, **Eurostar**, **GoVolta**, **Ouigo** en **SNCF Connect**
- Bij NMBS en Eurostar krijg je eerst een korte **tip** voordat je naar de website gaat

### 🧰 Diensten
- **HS Bijstand**, **HS Ticketing**, **HS DMI** (cabinescherm-simulator), Gemini, GitHub en de **statusbalk**

### ⚙️ Overig
- **Statusbalk in twee helften**: links de HS-storingen (CFH in het Frans), rechts de actuele NS-storingen (groen: geen storingen, oranje: storingen, rood: landelijke verstoring). Tik op de NS-helft voor alle storingen
- **Statusbalk met beheer**: kies *operationeel*, *storing*, *werkzaamheden* of *staking*, eventueel met een eigen tekst in het Nederlands en het Frans. De keuze wordt in de browser opgeslagen
- **Weer en locatie** via GPS (Open-Meteo en OpenStreetMap), elke 10 minuten bijgewerkt
- **Achtergrond**: een willekeurige foto uit de map [`achtergronden/`](achtergronden/)
- Werkt op desktop en op de telefoon

---

## Andere pagina's in deze repo

| Bestand | Wat het is |
| --- | --- |
| [`hs_in_dienst.html`](hs_in_dienst.html) | **HS In dienst**: in en uit dienst melden, overstappen tellen, pauzes. Met GPS vind je het dichtstbijzijnde station en kies je de trein waarop je overstapt uit de live NS-vertrektijden |
| [`hs_obis.html`](hs_obis.html) | **HS OBIS & RIS**: reizigersscherm in de trein (volgende halte, aankomst, spoor, aansluitingen, NL/FR). Met **NS live** vul je een treinnummer in en toont het scherm de echte rit: huidige halte, volgende haltes, eindstation en echte aansluitende treinen. Treinen van andere vervoerders krijgen ST (Stoptrein) of S (Sneltrein); bij een Arriva-trein verschijnt een eigen scherm in Arriva-stijl. Werkt ook als `hs_obis.html?trein=1634` |
| [`HS_DMI.html`](HS_DMI.html) | **HS DMI**: simulator van een ETCS-cabinescherm, met snelheidsbewaking, remingrijpen, overgangen tussen ETCS en HTC (cabinesein), stroomafnemer en wisseling van tractiesysteem, geluiden, treintypes ICNG, TGV Réseau, TGV Duplex (tot 320 km/h) en ICMm (alleen HTC), Nederlands, Frans en Engels, en een cabinemodus voor het aanraakscherm |
| [`hs_qr_ticketing_styled.html`](hs_qr_ticketing_styled.html) | **HS Ticketing**: maakt ondertekende QR-kaartjes en controleert ze. De hele pagina zit achter het Diensten-slot (code of passkey) |
| [`kaartje.html`](kaartje.html) | Foutpagina die opent als een HS-kaartje met een gewone camera wordt gescand |
| [`HS_MentaleBijstand.html`](HS_MentaleBijstand.html) | **HS / CFH Mentale Bijstand**: pagina over mentaal welzijn |
| [`hs_omloop_generator_v5.html`](hs_omloop_generator_v5.html) | **HS Omloop Generator v5**: maakt materieelomlopen |
| `manifest.json`, `sw.js` | Onderdelen voor installatie als app (PWA) |

---

## Hoe de live treindata werkt

De NS-API heeft een sleutel nodig, en die mag niet in een openbare repo staan. Daarom loopt alles via een **Cloudflare Worker**:

```
startpagina  →  Cloudflare Worker (met NS-sleutel als secret)  →  NS Reisinformatie API + Virtual Train API
```

- De Worker haalt de vertrektijden van Assen (`ASN`) op en vraagt per trein het materieel op
- Voor de OBIS levert de Worker de rit met soort trein en eindstation (`?journey=`) en vertrektijden per station via UIC-code (`?uic=`)
- Voor HS In dienst levert de Worker vertrektijden van elk station (`?station=CODE`) en stations met coördinaten
- Voor de bus zoekt de Worker haltes (`?bushalte=…`) en vertrektijden (`?bus=…`) op bij Transitous
- Voor de kaart haalt de Worker de live treinposities op (`?vehicles=1`)
- Voor de tussenhaltes vraagt de Worker de rit op (`?journey=<treinnummer>`)
- Voor de storingen haalt de Worker de actieve meldingen op (`?disruptions=1`)
- Voor de reisplanner levert de Worker de stationslijst (`?stations=1`) en reisadviezen (`?from=…&to=…`)
- Als er geen treinen meer rijden, zoekt de Worker de eerste trein van de ochtend op
- De Worker accepteert alleen verzoeken van `https://mister-yoshi.github.io`. Open je `index.html` rechtstreeks vanaf je computer, dan zie je daarom "Geen live data"
- In de HTML staat alleen het adres van de Worker, **nooit de NS-sleutel**
- De code van het tabblad **Diensten** staat ook in de Worker (secret `DIENSTEN_CODE`). Pas je die aan in Cloudflare, dan geldt de nieuwe code meteen op elk apparaat
- **Diensten** kan ook met een passkey (vingerafdruk, gezicht of schermvergrendeling). De Worker controleert de handtekening en bewaart alleen de publieke sleutels, in een KV-namespace met de koppelnaam `HS_KV`. De code blijft werken als reserve
- **HS Ticketing** laat kaartjes ondertekenen door de Worker (`?tsign`, ECDSA P-256). De privé-sleutel maakt de Worker zelf aan en bewaart hem in `HS_KV`; de pagina haalt alleen de publieke sleutel op (`?tpub`) en controleert daarmee, ook offline

---

## Licentie en bronnen

De code in deze repo valt onder de [MIT-licentie](LICENSE).

Live gegevens komen van de NS-API's (vertrektijden, materieel, reisadvies, storingen) en van [Transitous](https://transitous.org/sources/) (bustijden en haltes, open data o.a. van OVapi/NDOV). Weer via Open-Meteo, plaatsnaam via OpenStreetMap/Nominatim.

---

© 2026 Hoggynesische Spoorwegen · Chemin de fer Hoggynésiens · Gemaakt met Gemini & Claude
