# The House That Listens
The House That Listens è un gioco di ruolo ispirato all'horror e al concetto di **escape room**, sviluppato in **Java 25** con JavaFX per il corso di Metodologie di Programmazione dell'Università di Camerino. 

Il gioco prevede un giocatore principale che entra in una **casa abbandonata** che lo intrappola al suo interno, costringendolo ad esplorare le sue stanze in cerca di una via d'uscita; esso è caratterizzato da tre statistiche, **Lucidità**, **Composure** e **Caution**, che determinano la vita del giocatore e le abilità che possiede durante la sessione di gioco. Durante l'esplorazione della casa il giocatore ha l'obiettivo di trovare tre **frammenti di memorie** che saranno necessari per scappare dalla casa, ma sarà ostacolato da una **presenza** misteriosa che risiede nella casa e cercherà di impedire la sua fuga; la presenza è caratterizzata da una soglia di **attenzione** che determina il suo movimento verso il giocatore, e che può aumentare ogni volta che il giocatore compie specifiche azioni che producono **rumore** come spostarsi o cercare oggetti in una stanza.
## Requisiti

* **JDK 25**
* JavaFX e le altre dipendenze sono gestite tramite Gradle.

## Come eseguire

Clonare il repository

```bash
git clone https://github.com/sarasgattoni/MDP.git
cd MDP
```

### Linux / macOS

```bash
./gradlew build
./gradlew run
```

### Windows PowerShell

```powershell
.\gradlew build
.\gradlew run
```
oppure dalla cartella del progetto:

```
.\gradlew run
```

## Test

La suite JUnit copre le principali regole del dominio, il movimento della Presence, le azioni del giocatore, la decoy, la persistenza e il caricamento della casa.

Linux / macOS:

```bash
./gradlew test
```

Windows PowerShell:

```powershell
.\gradlew test
```

## Comandi di gioco

L'interfaccia viene utilizzata principalmente tramite **mouse**.

| Azione | Effetto |
| --- | --- |
| **Move** | si sposta in una stanza adiacente |
| **Search** | cerca il contenuto della stanza; con sufficiente Caution può essere silenziosa |
| **Listen** | ottiene informazioni sulla Presence senza produrre rumore, ma entra in cooldown subito dopo |
| **Catch Breath** | recupera Lucidity dopo un fallimento in confrontation, solo quando le condizioni lo permettono |
| **Place Decoy** | piazza una decoy nella stanza corrente |
| **Activate Decoy** | attiva a distanza la decoy piazzata |
| **Face Presence** | affronta direttamente la Presence usando Composure |
| **Hide** | tenta di fuggire usando Caution |
| **Speak Name** | azione finale disponibile nella stanza conclusiva dopo aver recuperato tutte le Memory |

## Flusso di gioco

`Menu` → `New Game / Continue` → `Exploration` → eventuale `Confrontation` → ritorno all'esplorazione → `Final Room` → vittoria oppure perdita per esaurimento della Lucidity.

Durante l'esplorazione:

* il rumore modifica l'**Attention** della Presence;
* la Presence si muove verso la sorgente del rumore secondo il percorso minimo;
* le Memory permettono di migliorare **Composure** o **Caution**;
* la decoy può essere piazzata e attivata per attirare temporaneamente la Presence in un'altra stanza.

## Architettura (in breve)
Il progetto adotta una separazione ispirata al pattern **MVC** e mantiene la logica di gioco indipendente da JavaFX.

Il package base è:

```text
it.unicam.cs.mpgc.rpg125676
```

Le responsabilità principali sono organizzate così:

- **model**: dominio del gioco, stato, entità, casa, turni, Attention e movimento della Presence;
- **command**: azioni del giocatore, divise tra esplorazione e confrontation;
- **controller**: coordinamento tra input, sessione, motore e view;
- **view**: interfaccia JavaFX, componenti grafici e navigazione;
- **persistence**: salvataggi, leaderboard e caricamento della casa.

La View può leggere oggetti del dominio per rappresentarne lo stato, ma le regole di gioco restano nel model e nelle action. Il core non importa JavaFX ed è quindi testabile indipendentemente dall'interfaccia grafica.

Tra i principali pattern utilizzati:

- **Command** per le azioni del giocatore;
- **Strategy** per movimento della Presence e gestione dell'Attention;
- **Template Method** per condividere le validazioni tra famiglie di azioni;
- **Factory / Composition Root** in `GameFactory`;
- **Null Object** tramite `EmptyContent`;
- **Repository** per separare la logica applicativa dalla tecnologia di persistenza.

Per architettura, pattern, modello di dominio e motivazioni delle scelte progettuali vedere la [Wiki](https://github.com/sarasgattoni/MDP/wiki).

## Persistenza

Il progetto utilizza tecnologie differenti in base al tipo di dato:

- il **salvataggio della partita** utilizza Java Serialization tramite `SaveRepository`;
- la **leaderboard** viene salvata in JSON tramite Gson e `LeaderboardRepository`;
- la struttura della casa è definita nella risorsa versionata `src/main/resources/data/house.json`.

I file generati durante l'esecuzione, come salvataggi e leaderboard, sono dati di runtime e non vengono versionati nel repository.

## Dichiarazione sull'uso di strumenti di AI

Durante lo sviluppo è stato utilizzato **ChatGPT** come strumento di supporto per alcuni aspetti.

In particolare è stato utilizzato per:

* discutere dell'attuabilità di alcune dinamiche di gioco pensate in una prima bozza teorica della struttura prima dell'implementazione;
* analizzare errori di compilazione e problemi emersi durante refactoring e merge Git;
* proporre casi di test e controlli di regressione per alcune meccaniche di gioco;
* verificare la coerenza tra regole di gioco e il rispetto dei criteri richiesti;
* migliorare alcune immagini utilizzate come risorse grafiche del gioco.

Le proposte fornite dall'AI sono state comprese, verificate, adattate e testate manualmente prima di essere integrate. 

## Asset grafici

Le immagini utilizzate per rappresentare la casa e le stanze sono state selezionate e successivamente **rielaborate con strumenti di AI** per uniformarne stile, atmosfera e risoluzione.

L'intervento ha riguardato soprattutto:
- aumento della risoluzione;
- adattamento delle immagini all'utilizzo nell'interfaccia.
### Fonti
| Asset / ambiente | Fonte della reference | Utilizzo |
| --- | --- | --- |
| Living Room | [Pinterest](https://pin.it/W7jhvMGNX) | Reference visiva successivamente rielaborata |
| Hallway | [Pinterest](https://pin.it/5RHPfyLhs) | Reference visiva successivamente rielaborata |
| Entrance | Adobe Stock, asset `#2027131760` | Reference visiva successivamente rielaborata |
| Parents Bedroom | [Pinterest](https://pin.it/3Fx998OKg) | Reference visiva successivamente rielaborata |
| Atrio | [Pinterest](https://pin.it/4hb9aaHEB) | Reference visiva successivamente rielaborata |
| Pantry | [Pinterest](https://pin.it/4DBjUYhJs) | Reference visiva successivamente rielaborata |
| Child bedroom | [Pinterest](https://pin.it/XILy5F1Me) | Reference visiva successivamente rielaborata |
| Study | [Pinterest](https://pin.it/JrekJZqoR) | Reference visiva successivamente rielaborata |
| Kitchen | [Pinterest](https://pin.it/4wEYoxDRT) | Reference visiva successivamente rielaborata |
| Locked room | [Pinterest](https://pin.it/4zvUethTk) | Reference visiva successivamente rielaborata |
| Villa | [Magnific](https://www.magnific.com/free-ai-image/haunted-house-with-spooky-aesthetic-cinematic-style_236211023.htm#fromView=keyword&page=3&position=9&uuid=861e4fc6-30d3-4902-80a4-69d5772be46b&track=ais_hybrid&query=Horror+house+png) | Reference visiva successivamente rielaborata |
| Stairs | [Pinterest](https://pin.it/7iBPFSq2Y) | Reference visiva successivamente rielaborata |
| Bathroom | [Pinterest](https://pin.it/4BBaaay7r) | Reference visiva successivamente rielaborata |
