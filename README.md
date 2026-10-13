# The Mind Director

A local, keyboard-driven **livestream director and game-management application** for a four-player game of **The Mind**. The application is intended to run on a **Linux HP Victus laptop**, with **OBS Studio** handling the actual broadcast and **JavaFX** providing a private dashboard for a fifth person: the coordinator/director.

> **Development order is deliberate:** **(1) input engine and state machine → (2) OBS using mock pictures → (3) JavaFX dashboard → (4) real cameras and live previews → (5) OCR.** The initial input/state-machine implementation is in place and currently prints intended OBS actions to the console. Next, replace those prints with real OBS integration using mock pictures; camera plumbing and OCR come later.

## 1. Purpose and scope

The director must be able to change shots quickly, monitor the four players' privately held cards, manually manage the game, and eventually trigger card recognition. The same director action must work whether initiated by a **physical keyboard shortcut** or by a **JavaFX button**.

The intended hardware and workflow are:

- **Four players** at a table, plus **one coordinator** operating the production.
- **One Linux laptop**, **one ordinary keyboard**, **two monitors**: OBS on one, JavaFX director dashboard on the other. A dedicated macro pad, second computer, or custom USB controller is **not required**.
- **Nine logical video feeds**: four player/reaction cameras, four small *card cameras* aimed at each player's hand, and one overhead table camera.
- **OBS Studio** composes and streams the broadcast; **Java** decides what should be displayed; **JavaFX** displays controls, game state, and private camera previews.
- **Zoom PodTrak P4** is available for audio. Microphone shopping/wiring is separate from the director engine; four independent XLR inputs do not imply four isolated USB audio channels in OBS.

### What the software must do

1. Interpret director shortcuts and mouse clicks identically.
2. Maintain an explicit input mode with predictable selection, confirmation, and cancellation.
3. Display one of five **public OBS scene layouts**: overhead; overhead plus one player; chosen card cameras; two-player duel; full-screen player.
4. Maintain a **private** JavaFX view of all four card cameras and editable player/game information.
5. Update stream-safe graphical overlays using a drawing/rendering interface.
6. Later, capture a still image from a chosen card camera and recognize card numbers, with manual review and correction.
7. Degrade gracefully if OBS disconnects or individual cameras fail.

### Non-goals for the initial version

- Automatic recognition or continuous analysis of all video feeds.
- Automatic enforcement of every rule of The Mind.
- Web backend, cloud infrastructure, database, distributed services, or a second laptop.
- Real cameras in milestones 1–3; polished automatic OCR in milestones 1–4.
- Fully automated audio mixing.

---

## 2. Authoritative development roadmap

**Progress:** Milestone 1 has a working initial implementation: inputs and state-machine decisions currently print the intended OBS actions instead of controlling OBS. Complete any remaining milestone-1 acceptance tests as needed while beginning milestone 2. Milestones 1–3 must run with **no webcams plugged in**.

| Order | Milestone | What works at the end | External dependency |
|---|---|---|---|
| **1** | **Engine: inputs + state machine** *(initial implementation complete)* | Inputs and state transitions produce console output describing intended OBS actions; acceptance tests remain the completion gate | None; OBS mocked |
| **2** | **OBS with mock pictures** | Real OBS connection; five layouts switched with fake player/card/overhead images; generated mock overlays | OBS Studio only |
| **3** | **JavaFX dashboard** | Functional, visually coherent director UI; clickable controls; mock previews; manual state/card editing; real OBS status | JavaFX + OBS; no cameras |
| **4** | **Cameras + live feeds** | All nine physical feeds usable in OBS; four private live card previews in JavaFX; captures work | Cameras/hubs/capture pipeline |
| **5** | **OCR integration** | `R → player` captures, recognizes, displays confidence/results, allows correction; never auto-reveals | OCR model/engine |

Each phase should preserve the public interfaces and tests established earlier. No throwaway keyboard engine or second set of JavaFX-only business logic.

---

## 3. Keybindings and interaction contract

The physical keyboard and the JavaFX buttons are simply two **adapters** to the same controller. Neither is allowed to directly modify OBS or game state independently.

### 3.1 Mode/key reference

| Input | Action | Confirmation / cancellation |
|---|---|---|
| `A` | Immediately show the **overhead-only** view | Immediate; no Enter required |
| `C` | Start **CARD** selection | Toggle `1–4`, then `Enter` to display the chosen card camera(s); zero selected means **all four**; `Esc` cancels |
| `S` | Start **SINGLE** selection | Pick player `1–4`, `Enter` displays **overhead + that player**; `Esc` cancels |
| `D` | Start **DUEL** selection | Toggle players `1–4`; `Enter` displays **exactly two player/reaction cameras**; `Esc` cancels |
| `1`–`4` while idle | Select a player for the normal fullscreen shot | `Enter` shows the most recently selected player; `Esc` cancels |
| `R` | Enter **READ** mode | Each `1–4` immediately requests a **private still capture + recognition** for that player's card camera; `Esc` exits READ mode |
| `Enter` | Confirm the active selection | Invalid/incomplete selections must not switch the broadcast |
| `Esc` | Cancel/exit the current selection mode | Shared controller behavior; must not arbitrarily switch OBS scenes |
| Unexpected key while selecting | Cancel current selection (configurable policy) | No partial OBS update; safe return to idle |

**Priority rule:** Mode-start shortcuts (`A/C/S/D/R`) should have explicit, tested behavior even if another selection is active. Recommended policy: `A` is an immediate overhead command; `C/S/D/R` *replace* the pending selection with a fresh mode. An unrelated/invalid key cancels the pending mode. This must be implemented in one central place, not differently in each handler.

**Important distinction:** `C` is an *intentional public broadcast* of card-camera views, whereas `R` is *private inspection/recognition only*. Invoking `R` **must never change the public OBS scene**. Do not allow private OCR text/images into overlays unless a separate, explicit public-output action is designed later.

### 3.2 Example sequences

```text
A                 → immediately switch to overhead
C Enter           → show all four card cameras
C 1 3 Enter       → show card cameras for players 1 and 3
C 1 1 Enter       → toggle player 1 on/off → show all four
S 2 Enter         → overhead + reaction camera of player 2
D 1 3 Enter       → two-player split: players 1 and 3
D 1 Enter         → invalid (only one chosen); do not switch
4 Enter           → fullscreen reaction camera of player 4
R 2               → privately capture/read card camera 2
R 2 4             → two private recognition requests; still READ
R Esc             → exit READ mode
C 2 Esc           → abandon selection; retain current OBS shot
```

For repeated digits in `S`/idle, the latest player selection wins; repeated digits in `C`/`D` toggle membership. The allowed player ID range is `1..4`, and all selections should be range-checked.

### 3.3 Input modes

The originally proposed enum is valid, but the semantics should be explicit:

```java
public enum InputMode {
    NONE,       // optional: global director controls disarmed
    LISTENING,  // armed/idle: waiting for a command or fullscreen player choice
    ALL,        // transient A command; not normally a persistent selection mode
    SINGLE,     // S: overhead + one player
    CARD,       // C: choose zero-to-four public card camera views
    DUEL,       // D: choose exactly two reaction cameras
    READ        // R: private per-player still capture/OCR
}
```

**Suggested simplification:** Keep `NONE` only if it means **director keyboard input disabled**, and `LISTENING` for **armed idle**. Otherwise merge them into `IDLE`. Treat `ALL` as an immediate action (or a short-lived mode transition), not as a state that can accidentally swallow the next digit. These are *representation decisions*, not additional requirements.

---

## 4. Architecture and ownership

```text
  JNativeHook global key events         JavaFX button actions
                |                               |
                v                               v
        KeyboardHandler                    UI Controller
                \                               /
                 \       same commands         /
                  v                           v
                         InputController
                        (current InputMode,
                         cancel, transitions)
                                 |
                          active ModeHandler
                                 |
                         DirectorCommand
                                 |
                         DirectorService
                         /      |       \
                        v       v        v
                  GameService  OBS     Draw/Overlay
                        |     gateway      service
                        v       |            |
                   DirectorState|            v
                        |       v       output assets
                        +--> JavaFX VM     for OBS

           Camera capture/previews ──> JavaFX (private)
                        |
                   snapshot capture ──> OCR (phase 5)
```

### 4.1 Keep these three states separate

- **`GameState`**: the actual game: players, level/round, lives, shurikens, played cards, known/confirmed cards, game progress. No `InputMode` field.
- **`InputController` state**: coordinator interaction: current mode, pending selected player(s), whether shortcuts are armed. This is not game state.
- **`DirectorState`**: the currently requested public scene, OBS connectivity, public scene selection, and director-visible statuses. This is operational/production state.

If convenient, each mode handler can own its *temporary* selection; the controller remains the only owner of the **active mode**.

### 4.2 Do not use `static InputMode` in an abstract handler

Handlers sharing a static mutable field creates hidden global state, couples independent handlers, complicates tests, and lets handlers bypass transition/reset semantics. A **small `InputController` is justified**, even if it only owns one `mode` field and delegates a handful of methods. It should be boring.

Prefer `ModeHandler` to the name `EventHandler` so it does not collide conceptually with `javafx.event.EventHandler`.

Suggested contract:

```java
public interface ModeHandler {
    default void onEnter() {}
    default void onExit() {}
    void handlePlayerPick(int playerIndex);
    void handleReturn();
}
```

The controller handles Escape and transitions. If handlers must tell the controller whether an Enter was valid or the mode should close, evolve `handleReturn()` to return a small `HandlerResult`/`Optional<DirectorCommand>` instead of letting every handler mutate the active mode directly.

### 4.3 Command boundary

Recommended commands are intentionally plain Java records, for example:

```java
sealed interface DirectorCommand {
    record ShowOverhead() implements DirectorCommand {}
    record ShowTableWithPlayer(int player) implements DirectorCommand {}
    record ShowCards(Set<Integer> players) implements DirectorCommand {}
    record ShowDuel(Set<Integer> players) implements DirectorCommand {}
    record ShowFullscreenPlayer(int player) implements DirectorCommand {}
    record ReadCards(int player) implements DirectorCommand {}
}
```

The handlers decide **what** the director wants, and `DirectorService` decides **how** to execute it through `ObsGateway`, `DrawService`, `GameService`, etc. This gives you one testable command pipeline regardless of where an action originates.

**Pragmatic exception:** Calling an OBS/drawing interface directly from a mode handler can work in an early prototype, provided the handler uses *interfaces* and no UI controllers, has deterministic reset behavior, and never mutates `GameState` arbitrarily. Prefer returning commands for long-term maintainability.

### 4.4 Threading rules

- JNativeHook callbacks do **not** run on the JavaFX Application Thread.
- OBS WebSocket callbacks and OCR workers are asynchronous.
- Serialize state-machine transitions and mutable director state on **one executor**. For the initial app, dispatching input to `Platform.runLater(...)` once JavaFX exists is workable; in phase 1 use a single-threaded test-friendly dispatcher. Avoid introducing inconsistent thread ownership between phases.
- Never block the JavaFX thread on OBS network requests, frame acquisition, overlay rendering, or OCR.
- Publish immutable snapshots/observables to JavaFX; update JavaFX properties only on the FX thread.
- Debounce key auto-repeat when appropriate (especially repeated mode keys and toggled player picks).

### 4.5 Global keyboard safety

JNativeHook is the proposed global input adapter, **subject to runtime verification on Linux Mint and the chosen X11/Wayland session**. Global hooks are not uniformly supported across Wayland compositors. Validate this **in milestone 1**, on the actual laptop, before committing to it.

A global listener will also see keys typed into other applications. Include an explicit **armed/disarmed** state, avoid taking over normal typing unexpectedly, and suspend director shortcuts while editing card numbers or other text fields. Register and unregister the native hook cleanly on application start/exit. UI buttons must remain usable if the global hook cannot be installed.

---

## 5. Proposed repository structure

One **Maven Java application** with packages by responsibility; **no microservices or unnecessary Maven submodules**.

```text
the-mind-director/
├── README.md
├── pom.xml
├── .gitignore
├── config/
│   ├── application.yml               # app-level settings and key policies
│   ├── obs.yml                       # OBS websocket, canvas, source mapping
│   └── cameras.yml                   # logical IDs → physical capture devices (phase 4)
├── scripts/
│   ├── check-cameras.sh              # lsusb/v4l2 camera diagnostics (phase 4)
│   └── setup-mock-obs.md             # phase-2 OBS setup procedure
├── assets/
│   └── mock/
│       ├── overhead.png
│       ├── player-1.png ... player-4.png
│       └── card-1.png ... card-4.png
├── src/
│   ├── main/
│   │   ├── java/com/cevapinxile/minddirector/
│   │   │   ├── Main.java              # entry point / lifecycle
│   │   │   ├── DirectorApplication.java
│   │   │   ├── config/
│   │   │   │   ├── AppConfig.java
│   │   │   │   ├── ObsConfig.java
│   │   │   │   └── CameraConfig.java
│   │   │   ├── model/
│   │   │   │   ├── Player.java
│   │   │   │   ├── GameState.java
│   │   │   │   ├── DirectorState.java
│   │   │   │   ├── SceneRequest.java
│   │   │   │   └── CameraId.java
│   │   │   ├── input/
│   │   │   │   ├── InputMode.java
│   │   │   │   ├── DirectorKey.java
│   │   │   │   ├── InputController.java
│   │   │   │   ├── ModeHandler.java
│   │   │   │   ├── KeyboardHandler.java
│   │   │   │   └── handlers/
│   │   │   │       ├── ListeningHandler.java
│   │   │   │       ├── SingleHandler.java
│   │   │   │       ├── CardHandler.java
│   │   │   │       ├── DuelHandler.java
│   │   │   │       └── ReadHandler.java
│   │   │   ├── director/
│   │   │   │   ├── DirectorCommand.java
│   │   │   │   ├── DirectorService.java
│   │   │   │   ├── SceneDirector.java
│   │   │   │   └── GameService.java
│   │   │   ├── obs/
│   │   │   │   ├── ObsGateway.java      # interface used by engine/tests
│   │   │   │   ├── ObsWebSocketClient.java
│   │   │   │   ├── ObsSceneComposer.java
│   │   │   │   ├── ObsSourceRegistry.java
│   │   │   │   └── ObsEventListener.java
│   │   │   ├── overlay/
│   │   │   │   ├── DrawService.java     # interface / rendering operations
│   │   │   │   ├── OverlayService.java
│   │   │   │   ├── OverlayModel.java
│   │   │   │   └── OverlayRenderer.java
│   │   │   ├── camera/                # phase 4
│   │   │   │   ├── CameraService.java
│   │   │   │   ├── CameraFeed.java
│   │   │   │   ├── CameraPreviewService.java
│   │   │   │   └── FrameCaptureService.java
│   │   │   ├── recognition/           # phase 5
│   │   │   │   ├── CardRecognitionService.java
│   │   │   │   ├── CardDetector.java
│   │   │   │   ├── NumberRecognizer.java
│   │   │   │   └── RecognitionResult.java
│   │   │   └── ui/                    # phase 3
│   │   │       ├── MainController.java
│   │   │       ├── CameraGridController.java
│   │   │       ├── PlayerCardController.java
│   │   │       └── DirectorViewModel.java
│   │   └── resources/
│   │       ├── fxml/
│   │       │   ├── main.fxml
│   │       │   └── player-card.fxml
│   │       ├── css/director.css
│   │       └── images/
│   └── test/java/com/cevapinxile/minddirector/
│       ├── input/InputControllerTest.java
│       ├── input/CardHandlerTest.java
│       ├── input/DuelHandlerTest.java
│       ├── director/DirectorServiceTest.java
│       ├── obs/ObsSceneComposerTest.java
│       └── recognition/CardRecognitionServiceTest.java
└── docs/
    ├── obs-scenes.md                 # scene/source contract (phase 2)
    ├── camera-topology.md            # physical mapping (phase 4)
    └── privacy-and-failure-modes.md
```

This is the **target structure**, not a requirement to create empty files on day one. Add later-phase packages when their corresponding milestone begins. Place application code under your preferred organization namespace if `com.cevapinxile` is not the final package name.

---

## 6. Milestone 1 — engine: input handling and state machine

**Goal:** All sequences work with **no OBS, no JavaFX window, no cameras**. The current implementation uses console output to describe the OBS actions that would be performed. Keep that output useful as a debugging aid while connecting the same engine to OBS in milestone 2.

### Implement

1. `InputMode`, `DirectorKey`, `ModeHandler`, and `InputController`.
2. Individual handlers for idle/listening, single, card, duel, and read.
3. Shared Escape/reset policy and exact mode-transition rules.
4. The central `DirectorCommand` pipeline and `DirectorService`.
5. `GameState` and `GameService`, initially minimal but independent of director input mode.
6. In-memory/fake `ObsGateway`, `DrawService`, and `FrameCaptureService` interfaces. Record calls in tests rather than opening devices.
7. A `KeyboardHandler` adapter using JNativeHook; also expose a simple method such as `inputController.accept(DirectorKey)` to support simulated keys and, later, JavaFX actions.
8. Input serialization, repeat-key handling, startup/shutdown of the native hook, and an enable/disable switch for global shortcuts.

### Acceptance tests

- [ ] `A` emits one `ShowOverhead` command immediately.
- [ ] `C Enter` emits a `ShowCards` command containing all four card IDs.
- [ ] `C 1 3 Enter` emits exactly card feeds 1 and 3.
- [ ] `C 1 1 Enter` restores empty selection, interpreted as all four.
- [ ] `S 2 Enter` emits `ShowTableWithPlayer(2)`.
- [ ] `D 1 3 Enter` emits `ShowDuel({1,3})`; one or three selections do **not** emit it.
- [ ] `4 Enter` from idle emits `ShowFullscreenPlayer(4)`.
- [ ] `R 2 4` emits two private read requests and does not change public scene state.
- [ ] Escape and invalid input discard temporary selections and preserve the currently broadcast scene.
- [ ] Entering a mode twice starts from a predictable, fresh selection.
- [ ] Invalid player IDs are rejected without crashes or OBS updates.
- [ ] Repeated physical key-down notifications do not unintentionally toggle a player twice.
- [ ] Keyboard and simulated GUI actions yield identical state transitions/commands.
- [ ] State-machine tests do not load JavaFX, OBS, OpenCV, or native cameras.
- [ ] Global keyboard input is verified on the target Linux desktop session, or the limitation is documented with UI controls as a fallback.

**Current status:** The state machine is implemented and prints intended OBS behavior to the console. This establishes the initial engine milestone; the acceptance tests above should still be verified rather than assumed complete.

**Definition of done:** You can demonstrate every shortcut in a console/test harness, including all edge cases, and inspect the exact commands emitted. No camera purchase or OBS layout work is needed for this phase.

---

## 7. Milestone 2 — OBS integration with mock pictures

**Goal:** Replace the current console-only OBS action output with calls to the **real OBS WebSocket API**, using **static pictures** instead of all nine cameras. Keep the existing input/state-machine behavior unchanged; every shortcut should select and compose the correct public shot.

### OBS connection

- Use OBS Studio with its built-in **obs-websocket** support (OBS 28+; commonly `ws://127.0.0.1:4455`, configured locally).
- `ObsWebSocketClient` owns connection, authentication, timeouts, request/response correlation, and reconnect handling.
- `ObsSourceRegistry` maps logical sources to OBS source names and **scene-item IDs**. OBS source names, scene names, and per-scene item IDs are not interchangeable.
- `ObsSceneComposer` owns public-scene selection, source visibility, layout/transforms, and safe change ordering.
- OBS settings, credentials, and filenames must not be hard-coded into mode handlers or committed as secrets.

### Logical mock sources

| Logical ID | Mock asset | Later actual device |
|---|---|---|
| `OVERHEAD` | `assets/mock/overhead.png` | Top-down table camera |
| `PLAYER_1` … `PLAYER_4` | `assets/mock/player-1.png` … | Four reaction/player webcams |
| `CARD_1` … `CARD_4` | `assets/mock/card-1.png` … | Four private card webcams |

Use visibly different labels/colors in the generated images so accidental source swaps are obvious. Mock assets should match the **same logical source IDs** that physical cameras will use in phase 4; swapping implementation must not change the input engine.

### Five public scene layouts

| Scene template | Content | Command |
|---|---|---|
| `MIND_OVERHEAD` | Overhead fills output | `ShowOverhead` |
| `MIND_TABLE_PLAYER` | Overhead plus one selected reaction camera | `ShowTableWithPlayer(p)` |
| `MIND_CARDS` | All four or chosen card feeds in a legible dynamic grid | `ShowCards(players)` |
| `MIND_DUEL` | Exactly two chosen reaction cameras, balanced split | `ShowDuel({p1,p2})` |
| `MIND_PLAYER` | One chosen reaction camera full-screen | `ShowFullscreenPlayer(p)` |

Default to a **1920×1080 output canvas**, with layout math isolated in `SceneDirector`/`ObsSceneComposer` so resolution can be changed later. An overhead-plus-player shot should keep the table meaningfully visible (for example, player picture-in-picture). Cards can use 1-up / 2-up / 3-up / 4-up grid strategies.

The mock player/card/overhead sources must all be registered in OBS, although the public stream only displays those explicitly selected by the director. Private camera inspection and OCR outputs must not accidentally be attached to always-on public overlays.

### Overlays and drawing

Provide a `DrawService`/`OverlayService` API whose implementation can render **mock game graphics** (e.g., current level, lives, shurikens, player labels) as transparent PNGs or equivalent OBS-compatible assets. Keep a **public overlay model** separate from unconfirmed private card values. Avoid relying on editing static image files while OBS is showing partially written assets; write atomically and then notify/refresh the OBS source.

### Reliability rules

- Queue or serialize scene requests; stale responses must not overwrite a newer selection.
- Prefer updating a **non-live scene/bank** and switching after composition to avoid viewers seeing intermediate layouts. If a layout must be edited while on-air, treat flicker/partial reveals as a defect; scene banking can be introduced if needed.
- If OBS disconnects, do not block input; display a clear disconnected status and retry/reconcile state after reconnect.
- No failed scene update should be reported as successfully live.
- Test source-name mappings and per-scene IDs after importing/duplicating scene collections.

### Acceptance tests

- [ ] All five scene templates are created and operational with images only.
- [ ] All documented input sequences switch to the correct public scene and source combination.
- [ ] Rapid requests end at the **most recently requested** scene without flicker or accidental reveals.
- [ ] Generated graphics update in OBS when public game-state values change.
- [ ] The OBS disconnected/reconnected path is tested.
- [ ] `R` remains private and causes **no** broadcast scene or public overlay change.
- [ ] No physical webcam is required or opened.

**Definition of done:** The coordinator can direct a convincing demo stream made entirely of labeled still pictures, using the actual OBS application and WebSocket link.

---

## 8. Milestone 3 — JavaFX dashboard and manual control

**Goal:** Produce the actual director UI using **mock previews and fake camera status**, connected to the same engine and the working phase-2 OBS integration. Only then add physical video.

### Dashboard layout

Suggested two-monitor workflow:

- **Monitor 1:** OBS Studio program/preview and streaming controls.
- **Monitor 2:** JavaFX private director dashboard, intended to remain visible to the fifth coordinator.

UI areas:

1. **Status/header:** connected/disconnected OBS, active public shot, current input mode, shortcuts armed/disarmed, selection-in-progress, errors.
2. **Four player tiles:** player name, mock card-camera preview, detection/status area, manually editable card list, capture/refresh control placeholder.
3. **Game panel:** level/round, lives, shurikens, played cards, player status, buttons to adjust values manually.
4. **Director controls:** large buttons for overhead, card selection, table+player, duel, read mode, confirm, cancel, and digits/player choices.
5. **Feedback area:** which two duel players are selected, pending scene confirmation, log of command results, OBS connection and camera placeholders.

Make the selected/pending state obvious: the coordinator must distinguish “I picked player 3” from “player 3 is already on air.”

### Input unification

```java
// Physical keyboard adapter:
inputController.accept(DirectorKey.DUEL);

// JavaFX button:
duelButton.setOnAction(event ->
    inputController.accept(DirectorKey.DUEL));
```

Likewise, `player3Button` should submit the **same** `PLAYER_3` key/semantic event the keyboard adapter emits. Keep shortcuts, button labels, and the visible mode indicator synchronized from one state snapshot. JavaFX controllers must not call `obs.showDuel(...)` directly or implement their own selection rules.

**Text-editing safety:** Since the keyboard listener is global, card number editing must temporarily suspend director shortcuts (or narrowly filter events from editable fields where possible). A backspace in the manual card editor must never accidentally cancel a selection, and a digit typed into a player name must not switch the show.

### JavaFX threading and performance

Use JavaFX observable properties for view updates; dispatch external callbacks onto the FX thread. Avoid creating images or performing network/capture work in UI event handlers. The eventual private previews should use reusable `ImageView` nodes; at this milestone, those display static mock images.

### Acceptance tests

- [ ] Four player tiles, mock card previews, game state, and all director controls are visible and usable on the second monitor.
- [ ] Keyboard shortcuts and UI buttons yield the same output, selection indicators, and OBS behavior.
- [ ] Manual card edits and level/lives/shuriken changes update the private model and approved public graphics as appropriate.
- [ ] No hidden card value appears in a public overlay without explicit approval.
- [ ] Text fields do not trigger global scene shortcuts while being edited.
- [ ] OBS reconnect/disconnect is accurately shown in the UI.
- [ ] UI remains responsive under repeated scene switches and mock data updates.
- [ ] App can launch and operate **without any camera connected**.

**Definition of done:** A fifth coordinator could run the whole mock production from JavaFX and keyboard, without reading a console or touching OBS source properties during the show.

---

## 9. Milestone 4 — physical cameras, live feeds, and previews

**Goal:** Substitute real capture for all nine logical mock sources while keeping the engine, UI commands, and scene types unchanged.

### Planned camera inventory

| Quantity | Role | Initial budget idea | Priority |
|---|---|---|---|
| 4 | Player/reaction webcams | Cheap “2K” USB models (~€11 each; verify true output) | Smooth player motion |
| 4 | Card/hand webcams | Very inexpensive USB 480p models (~€6 each) | Readable cards/stable close-up |
| 1 | Overhead table webcam | Prefer genuine 4K if affordable; avoid interpolated specs | Table-wide view and cropping |
| **9** | **Total video feeds** | | **All nine must be accessible in OBS** |

These are target price points, **not confirmed product specifications or a guaranteed final hardware budget**.

### USB bandwidth and hub topology

- Nine connected devices **enumerating** under Linux is not proof that nine video streams will work simultaneously.
- A USB 2.0 upstream link is nominally **480 Mb/s shared**; cascading hubs does not create extra upstream bandwidth.
- Cheap webcams often expose uncompressed `YUYV`; favor genuine **MJPEG** modes when available, because simultaneous streams may otherwise exceed available bandwidth and USB scheduling resources.
- A USB-C connector does not prove USB 3 transfer speed; a USB 2-only hub remains USB 2 over USB-C.
- Prefer multiple **externally powered USB 3 hubs plugged into separate laptop ports** over one long daisy-chain. This improves power distribution and may improve bandwidth, but ports may still share a controller; inspect the topology.
- A useful starting distribution is four card cams on one hub, three player cams on another, and remaining player + overhead on another. Plug a bandwidth-heavy overhead camera into its best available port if required.
- Test every camera at its **actual simultaneous capture format, resolution, and FPS**. A truthful 480p feed may be more usable than a fake “2K” webcam with poor exposure or a 4K mode at very low FPS.

Linux discovery commands:

```bash
lsusb -t
v4l2-ctl --list-devices
v4l2-ctl -d /dev/video0 --list-formats-ext
v4l2-ctl -d /dev/video0 --list-ctrls
```

Install `v4l-utils` if `v4l2-ctl` is missing. `/dev/video0` is an **example only**; identify the correct device first. Prefer stable `/dev/v4l/by-id/` or `/dev/v4l/by-path/` mappings where available, not blindly assigning player IDs by volatile `/dev/videoN` numbers.

### One capture owner per physical camera

**Critical architectural decision for this milestone:** OBS and JavaFX should **not independently open the same cheap USB video device** and assume it supports concurrent capture. Both need access to the four card cameras, and all nine must remain available to OBS.

Evaluate two implementations before choosing:

1. **Recommended to evaluate: shared capture pipeline**, e.g., GStreamer opens each card camera once, then distributes frames to OBS and JavaFX/Java capture. OBS can directly capture the other five broadcast-only cameras. Prefer efficient local transport/virtual camera or stream endpoints; assess overhead and synchronization.
2. **Simpler prototype path:** OBS owns card cameras and Java receives preview/screenshot frames through an OBS-supported mechanism. Verify achievable preview FPS, latency, screenshot request cost, and suitability for OCR; periodic OBS screenshots are **not automatically a good live-preview transport**.

The architecture must provide a `CameraService` / `FrameCaptureService` abstraction so the choice does not leak into mode handlers or the UI. Do a **one-camera proof of concept first**, then scale to four card feeds, then nine total feeds.

### Camera and privacy contracts

- Player/reaction cameras are public-shot sources; card cameras are **private by default**, except when the director explicitly uses the `C` public card-camera scene.
- JavaFX private previews stay on the coordinator's monitor; do not screen-capture that window into the broadcast.
- `R` capture is private, even if the selected public scene currently shows another feed.
- If a camera vanishes, show a clear unavailable indicator and keep unrelated feeds and the director UI alive.
- Camera mapping must be verified physically: `CARD_2` must show player 2's cards, not a device that moved after a reboot.

### Acceptance tests

- [ ] Stable logical-to-physical mapping for all nine cameras.
- [ ] All nine feeds are simultaneously usable **at the chosen format/FPS** in OBS; not merely listed as USB devices.
- [ ] Four card feeds appear live in JavaFX with acceptable lag and no OBS device conflict.
- [ ] Overhead, single, card, duel, and fullscreen scenes show the correct live devices.
- [ ] A snapshot from each card feed can be saved and visually inspected without OCR.
- [ ] Disconnect/reconnect of one camera does not crash the app or reassign players silently.
- [ ] CPU, USB bandwidth, capture FPS, OBS dropped frames, and temperature remain acceptable during a realistic rehearsal.
- [ ] Public/private boundaries hold during scene switches and failed captures.

**Definition of done:** The same director keyboard/UI that operated still pictures now directs **real, simultaneous video**, with four trustworthy private previews and still-capture capability.

---

## 10. Milestone 5 — card recognition (OCR)

**Goal:** Read numbers from each player's cards **on demand**, not continuously, and let the coordinator verify/correct the result before it becomes trusted game data.

### Trigger flow

```text
R            → enter private READ mode
2            → request card-camera-2 snapshot
               → find cards/number regions
               → recognize candidate numbers
               → return numbers + confidence + snapshot reference
               → show candidates in player-2 panel
               → coordinator confirms or edits
4            → repeat for player 4
Esc          → exit READ mode
```

`FrameCaptureService` must return a still from the correct logical camera; `CardRecognitionService` should not know about JNativeHook, button layouts, or OBS scene names.

### Processing responsibilities

- `CardDetector`: detect card(s), crop/rectify, check visibility/blur, separate multiple cards if possible.
- `NumberRecognizer`: extract printed numbers; choose OCR engine/model only after collecting real examples from the **actual cameras, lighting, distance, angle, and card design**.
- `CardRecognitionService`: orchestrate async capture/processing, preserve player ID, expose results and errors.
- `RecognitionResult`: candidate numbers, optional per-number confidence, timestamp/player ID, and perhaps crop/snapshot references.
- `GameService`: store **confirmed** cards and manual overrides. Uncertain OCR results should remain *suggestions*, not silently overwrite accepted values.

The Mind uses numbered cards (typically **1–100**): constrain candidates to the game's valid range and avoid accepting duplicate/implausible detections without review. Recognizing several fanned or partly occluded cards is harder than reading one isolated numeral; report **partial/uncertain** results rather than inventing missing values.

Possible implementations include OpenCV with a Java wrapper, or a Python/OpenCV OCR worker connected through a simple local protocol. Choose based on accuracy and packaging, not before real camera captures exist. Cache/retain a small set of test images only with deliberate privacy controls.

### OCR reliability and safety

- Run capture/recognition off the JavaFX thread.
- Allow a newer request to supersede/cancel an older pending result for the same player.
- Support retry and manual correction without re-entering READ mode.
- Record whether a number is **manual**, **OCR suggestion**, or **confirmed**.
- Keep recognition results out of public OBS output unless deliberately approved.
- Test low light, glare, tilted cards, hands occluding corners, movement blur, and multiple visible numbers.

### Acceptance tests

- [ ] `R 1` through `R 4` capture from the correct private cameras.
- [ ] Results contain recognized candidate(s) and confidence or an explicit failure status.
- [ ] Coordinator can confirm, replace, or delete a detected number.
- [ ] Results cannot accidentally overwrite a later manual edit.
- [ ] OCR does not block scene switching or stall JavaFX.
- [ ] `Esc` exits READ mode; keyboard/UI continue working normally.
- [ ] Recognition alone never changes the broadcast scene or reveals private cards.
- [ ] Actual image test cases exist for each player camera and common failure conditions.

**Definition of done:** On-demand recognition is reliable enough to assist a human director, with manual correction always available and the public broadcast untouched by private reads.

---

## 11. The Mind game-state model

Keep this model **separate from director interaction**. Start simple; extend only when the UI/overlay needs it.

Potential fields:

```text
GameState
├── players[1..4]
│   ├── name
│   ├── card values (with OCR/manual/confirmed status)
│   └── optional connection/status data
├── level / round
├── lives
├── shurikens
├── playedCards
├── game phase (optional)
└── game events/history (optional)
```

`GameService` should own validated operations such as `setPlayerCards`, `confirmCard`, `playCard`, `changeLevel`, `adjustLives`, and `adjustShurikens`. Keep rules automation intentionally modest at first. The user interface can edit those values directly through service commands; overlay updates should consume **sanitized public game-state projections**, not the entire object containing all hidden hands.

Two important distinctions:

- **The game state** says *what has happened in the game*.
- **The director state** says *what is currently on the public broadcast*.
- **The input state** says *what keyboard/UI command the coordinator is currently constructing*.

A card might be recognized privately without being officially accepted as a game-state value or being publicly shown.

---

## 12. OBS and rendering interface contracts

Keep concrete OBS network calls behind a narrow gateway. Example conceptual API:

```java
interface ObsGateway {
    CompletionStage<Void> showOverhead();
    CompletionStage<Void> showTableWithPlayer(int player);
    CompletionStage<Void> showCards(Set<Integer> players);
    CompletionStage<Void> showDuel(Set<Integer> players);
    CompletionStage<Void> showFullscreenPlayer(int player);
    CompletionStage<Boolean> isConnected();
}

interface DrawService {
    CompletionStage<Void> updatePublicOverlay(PublicOverlayModel model);
}
```

The exact signatures can change. Key principle: **input handlers should not know WebSocket request names or image-file paths**. Keep a mock `ObsGateway` and mock `DrawService` usable in engine tests and stage-3 UI previews.

The public overlay model must never implicitly expose `GameState.players[*].privateCards`. Use explicit public projections/DTOs. Avoid accidental leakage from debug logging, temporary preview files, desktop/window capture, or an OBS scene that contains private JavaFX content.

---

## 13. Testing strategy and release gates

Prefer **JUnit 5** with lightweight fakes/mocks; the state-machine core should not require native libraries. Add test fixtures for keyboard traces and expected command outputs.

| Layer | Main test focus | First introduced |
|---|---|---|
| Input/controller unit tests | transitions, Escape, repeat/toggle, invalid selection, command emission | 1 |
| Service tests with fake gateways | calls, game-state updates, public/private separation | 1 |
| OBS integration tests | scenes/sources, WebSocket reconnect, rapid switching | 2 |
| JavaFX interaction tests/manual checklist | buttons equal keys, fields not hijacked, UI feedback | 3 |
| Camera soak/reconnect tests | all nine feeds at once, stable device mapping, preview lag | 4 |
| OCR image regression tests | real image fixtures, confidence, partial/failed scans | 5 |

Recommended demo gates:

1. **Engine demo:** print the command stream for all shortcuts, prove cancellation and invalid input.
2. **OBS mock demo:** a complete directed mock broadcast with labeled images and live overlay changes.
3. **JavaFX demo:** run every OBS command from both global keys and dashboard buttons; edit game state.
4. **Hardware rehearsal:** nine concurrent streams, four private previews, no dropped/reassigned feeds.
5. **OCR rehearsal:** correct-player captures, editable numbers, zero private-to-public leaks.

Don't make a phase “done” because the happy path works once. Run its acceptance list and keep it green when implementing later phases.

---

## 14. Technology choices and dependencies

| Concern | Preferred starting point | Notes |
|---|---|---|
| Language/build | Java (modern LTS, e.g. 21 or 25 if supported), Maven | One desktop application; no Spring Boot required |
| UI | JavaFX + FXML/CSS | Second-monitor coordinator dashboard |
| Global shortcuts | JNativeHook | Verify X11/Wayland support and disarming on target host |
| OBS automation | OBS 28+ `obs-websocket` v5 protocol | Use client library or thin WebSocket wrapper; version-pin |
| Rendered graphics | Java2D/JavaFX image rendering to PNG, or OBS-compatible graphics | Keep behind `DrawService` |
| Linux capture | V4L2; optionally GStreamer distribution | Decide architecture after one-camera sharing proof |
| OCR | OpenCV + OCR solution selected from camera samples | Implement last; manual edits are essential |
| Tests | JUnit 5 | Core tests must be headless and fast |
| Configuration | YAML/properties (implementation choice) | Do not commit OBS passwords |

Version numbers and setup commands should be pinned in `pom.xml`/build documentation once actual library compatibility is confirmed; do not silently assume JNativeHook/JavaFX/OBS native integration works on every desktop environment.

### Local run workflow by milestone

```text
Phase 1: Build and run headless tests → exercise keyboard/command harness
Phase 2: Start OBS with mock scene collection → run engine → verify scenes
Phase 3: Start OBS → launch JavaFX dashboard → operate with keys/buttons
Phase 4: Connect/test cameras and shared capture → start OBS → launch app
Phase 5: Same as phase 4 + OCR worker/engine → verify private READ mode
```

A future `README` update should add the exact `mvn` commands, package versions, OBS scene-collection export, native setup, and production launcher **after those pieces actually exist**. This document does not pretend the repository already contains a working implementation.

---

## 15. Practical risks and decisions to defer

| Decision / risk | Policy |
|---|---|
| **InputMode enum** | Preserve `NONE/LISTENING/ALL/SINGLE/CARD/DUEL/READ` if useful; clarify whether `NONE` means disarmed, and treat `ALL` as immediate |
| **Where active mode lives** | `InputController`, never `GameState` or static mutable handler field |
| **How JavaFX buttons work** | They submit the same semantic events as the keyboard adapter |
| **JNativeHook on Linux** | Prove it on the actual session in phase 1; provide disarm and UI fallback |
| **OBS scene layout** | Define five templates; use static pictures first, camera sources later |
| **Private cards** | JavaFX/OCR remain private; only explicit `C` can put card feeds on air |
| **Shared webcam access** | Defer until phase 4; test OBS ownership vs shared GStreamer capture |
| **Hub/bandwidth topology** | Validate with all cameras streaming simultaneously, not listing only |
| **Camera FPS/resolution** | Prefer stable/clear simultaneous capture over nominal marketing resolution |
| **OCR technology** | Choose after saving representative frames; human confirmation required |
| **Audio** | Keep P4 / microphone setup independent of this repo's engine milestones |
| **Persistence** | Not needed initially; optional settings/game-session save later |
| **Streaming output** | OBS remains authoritative for actual broadcast and recording |

## 16. Final success criteria

The finished application lets one coordinator run a four-player The Mind livestream from a **single Linux laptop and keyboard**, while OBS and JavaFX are visible on separate monitors. The coordinator can instantly cut to overhead, select a player, compose duels, choose card camera grids, edit game information, and privately inspect card-camera feeds. All nine cameras remain available to OBS, the four card previews work in JavaFX, and OCR can suggest card values without replacing human control or leaking private information.

**Above all: build and prove the engine first, then OBS with pictures, then JavaFX, then real cameras, and OCR last.**
