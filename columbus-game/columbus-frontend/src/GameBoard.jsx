// GameBoard.jsx
// Main component for the Columbus Adventure game UI and logic
import React, { useEffect, useState, useCallback, useRef } from "react";
import axios from "axios";
import "./GameBoard.css";

const TILE_IMAGES = {
  OCEAN: "/assets/images/ocean.png",
  ISLAND: "/assets/images/island.jpg",
  COLUMBUS: "/assets/images/columbus.png",
  PIRATE: "/assets/images/pirate.png",
  WHIRLPOOL: "/assets/images/whirlpool.png",
  OCTOPUS: "/assets/images/octopus.png",
  SHARK: "/assets/images/shark.png",
  HEALTH: "/assets/images/heart.jpg",
  TREASURE: "/assets/images/treasure.png",
  OPPONENT: "/assets/images/opponent.jpg",
};

function GameBoard() {
  // --- GAME STATE VARIABLES ---
  // grid: 2D array representing the current game board tiles
  const [grid, setGrid] = useState([]);
  // logs: Array of latest backend log messages (game events)
  const [logs, setLogs] = useState([]);
  // previousLogs: Accumulated logs and cell info for Captain Rador log panel
  const [previousLogs, setPreviousLogs] = useState([]);
  // currentLevel: Current level number, persisted in localStorage
  const [currentLevel, setCurrentLevel] = useState(() => parseInt(localStorage.getItem('currentLevel')) || 1);
  // lives: Number of lives left, persisted in localStorage
  const [lives, setLives] = useState(() => parseInt(localStorage.getItem('lives')) || 3);
  // score: Player's score, persisted in localStorage
  const [score, setScore] = useState(() => parseInt(localStorage.getItem('score')) || 0);
  // highScore: Highest score achieved, persisted in localStorage
  const [highScore, setHighScore] = useState(() => parseInt(localStorage.getItem('highScore')) || 0);
  // levelUpPending: Used to trigger UI update after victory/level up
  const [levelUpPending, setLevelUpPending] = useState(false);

  // --- AUDIO REFS AND STATE ---
  // Refs for background music and sound effect audio elements
  const backgroundMusicRef = useRef(null);
  const sailSound = useRef(null);
  const teleportSound = useRef(null);
  const defeatSound = useRef(null);
  const victorySound = useRef(null);
  const warningSound = useRef(null);
  // backgroundMusicOn: Controls whether background music is playing
  const [backgroundMusicOn, setBackgroundMusicOn] = useState(true);
  // soundEffectsOn: Controls whether sound effects are enabled
  const [soundEffectsOn, setSoundEffectsOn] = useState(true);
  // volume: Controls audio volume for music and effects
  const [volume, setVolume] = useState(0.5);
  // showSettings: Toggles visibility of the settings panel UI
  const [showSettings, setShowSettings] = useState(false);

  // --- GAME STATUS DERIVED STATE ---
  // isGameOver: True if the game has ended (victory or defeat)
  const isGameOver = logs.some(log =>
    log.includes("Game Over") ||
    log.includes("treasure") ||
    log.includes("attacked by a pirate") ||
    log.includes("eaten by a sea monster") ||
    log.includes("opponent reached the treasure")
  );

  // --- CORE LOGIC HELPERS ---
  /**
   * Finds the current position of Columbus in the grid.
   * @param {string[][]} grid - The game grid.
   * @returns {{row: number, col: number}|null} The row/col of Columbus, or null if not found.
   */
  const findColumbusPosition = (grid) => {
    for (let i = 0; i < grid.length; i++) {
      for (let j = 0; j < grid[i].length; j++) {
        if (grid[i][j] === "COLUMBUS") {
          return { row: i, col: j };
        }
      }
    }
    return null;
  };

  /**
   * Plays a sound effect if sound effects are enabled.
   * Uses a clone to allow overlapping playback of the same sound.
   * @param {React.RefObject<HTMLAudioElement>} soundRef
   */
  const playSoundEffect = (soundRef) => {
    if (soundEffectsOn && soundRef.current) {
      const clone = soundRef.current.cloneNode(true); // Clone the sound for overlap
      clone.volume = soundRef.current.volume;         // Maintain volume
      clone.play();                                   // Play the clone independently
    }
  };

  // --- FETCH GAME STATE FROM BACKEND ---
  /**
   * Fetches the latest game state (grid and logs) from the backend.
   * Updates the grid, logs, previousLogs, and plays sound effects for events.
   */
  const fetchGame = useCallback(async () => {
    try {
      const res = await axios.get("http://localhost:8080/state");
      console.log("✅ Backend responded with:", res.data);
      setGrid(res.data.grid);
      setLogs(res.data.log || []);
      // Find Columbus position, log adjacent cells for debugging and Captain Rador
      const pos = findColumbusPosition(res.data.grid || []);
      if (pos) {
        const { row, col } = pos;
        const current = res.data.grid[row][col];
        const top = row > 0 ? res.data.grid[row - 1][col] : "OUT_OF_BOUNDS";
        const bottom = row < res.data.grid.length - 1 ? res.data.grid[row + 1][col] : "OUT_OF_BOUNDS";
        const left = col > 0 ? res.data.grid[row][col - 1] : "OUT_OF_BOUNDS";
        const right = col < res.data.grid[0].length - 1 ? res.data.grid[row][col + 1] : "OUT_OF_BOUNDS";

        console.log(`🚢 Columbus Location: [${row},${col}]`);
        console.log(`🔵 Current Cell: ${current}`);
        console.log(`⬆️ Top Cell: ${top}`);
        console.log(`⬇️ Bottom Cell: ${bottom}`);
        console.log(`⬅️ Left Cell: ${left}`);
        console.log(`➡️ Right Cell: ${right}`);

        setPreviousLogs((prev) => [
          ...prev,
          `🚢 Columbus at [${row},${col}]`,
          `🔵 Current: ${current}`,
          `⬆️ Top: ${top}`,
          `⬇️ Bottom: ${bottom}`,
          `⬅️ Left: ${left}`,
          `➡️ Right: ${right}`,
        ]);
      }
      setPreviousLogs((prev) => [...prev, ...(res.data.log || [])]);
      // Only update grid and logs, not frontend currentLevel/lives/score/highScore
      // Level up UI bump if pending
      if (levelUpPending) {
        setCurrentLevel(prev => prev + 1);
        setLevelUpPending(false);
      }
      // Play relevant sound effects based on the most recent game event
      if (res.data.log && res.data.log.length > 0) {
        const lastLog = res.data.log[res.data.log.length - 1].toLowerCase();
        // Play victory sound if Columbus found the treasure
        if (lastLog.includes("columbus found the treasure")) {
          playSoundEffect(victorySound);
        // Play defeat sound for various defeat conditions
        } else if (
          lastLog.includes("game over") ||
          lastLog.includes("attacked by a pirate") ||
          lastLog.includes("eaten by a sea monster") ||
          lastLog.includes("opponent reached the treasure")
        ) {
          playSoundEffect(defeatSound);
        // Play sail sound for movement
        } else if (lastLog.includes("sail")) {
          playSoundEffect(sailSound);
        // Play teleport sound for whirlpool/teleport events
        } else if (lastLog.includes("teleport") || lastLog.includes("fell into a whirlpool")) {
          playSoundEffect(teleportSound);
        // Play warning sound if warnings or whirlpools are detected
        } else if (lastLog.includes("warning") || lastLog.includes("whirlpool")) {
          playSoundEffect(warningSound);
        }
      }
    } catch (err) {
      console.error("❌ Backend fetch error:", err);
    }
  }, [soundEffectsOn, levelUpPending]);

  // --- REACT HOOKS ---

  // Effect: Handles background music playback and volume changes
  useEffect(() => {
    // Setup and control background music playback and volume
    if (backgroundMusicRef.current) {
      backgroundMusicRef.current.loop = true;
      backgroundMusicRef.current.volume = volume;
      backgroundMusicRef.current.muted = !backgroundMusicOn;
      if (backgroundMusicOn) {
        backgroundMusicRef.current.play().catch(() => {});
      } else {
        backgroundMusicRef.current.pause();
      }
    }
  }, [backgroundMusicOn, volume]);

  // Effect: Fetches the latest game state from the backend on mount and when fetchGame changes
  useEffect(() => {
    fetchGame();
  }, [fetchGame]);

  // Effect: Handles keyboard arrow key presses for player movement.
  // Listens for Arrow keys, computes movement, and sends move to backend.
  useEffect(() => {
    /**
     * Handles arrow key presses for Columbus movement.
     * Sends move to backend if valid, plays sounds, and updates lives if needed.
     */
    const handleKey = async (e) => {
      let dir = null;
      let nextX = -1;
      let nextY = -1;

      // Prevent default scroll with arrow keys
      if (
        e.key === "ArrowUp" ||
        e.key === "ArrowDown" ||
        e.key === "ArrowLeft" ||
        e.key === "ArrowRight"
      ) {
        e.preventDefault();
      }

      // Determine direction and coordinate delta from arrow key
      if (e.key === "ArrowUp") {
        dir = "UP";
        nextX = -1;
        nextY = 0;
      } else if (e.key === "ArrowDown") {
        dir = "DOWN";
        nextX = 1;
        nextY = 0;
      } else if (e.key === "ArrowLeft") {
        dir = "LEFT";
        nextX = 0;
        nextY = -1;
      } else if (e.key === "ArrowRight") {
        dir = "RIGHT";
        nextX = 0;
        nextY = 1;
      }

      if (dir) {
        // Find Columbus's current position in the grid
        let columbusX = -1;
        let columbusY = -1;
        for (let i = 0; i < grid.length; i++) {
          for (let j = 0; j < grid[i].length; j++) {
            if (grid[i][j] === "COLUMBUS") {
              columbusX = i;
              columbusY = j;
              break;
            }
          }
          if (columbusX !== -1) break;
        }

        if (columbusX !== -1 && columbusY !== -1) {
          const newX = columbusX + nextX;
          const newY = columbusY + nextY;

          // Check if the new position is within the grid bounds
          if (
            newX >= 0 &&
            newX < grid.length &&
            newY >= 0 &&
            newY < grid[0].length
          ) {
            const nextTile = grid[newX][newY];
            // Block movement onto ISLAND tiles
            if (nextTile !== "ISLAND") {
              // Play sail sound immediately if moving onto OCEAN
              if (nextTile === "OCEAN") {
                playSoundEffect(sailSound);
              }
              // Play teleport sound immediately if moving onto WHIRLPOOL
              if (nextTile === "WHIRLPOOL") {
                playSoundEffect(teleportSound);
              }
              // Gain a life when moving onto a HEALTH tile
              if (nextTile === "HEALTH") {
                setLives(prevLives => prevLives + 1);
              }

              // --- Analyze adjacent tiles for danger before moving ---
              // Play warning sound if any adjacent tile is dangerous
              const top = (columbusX > 0) ? grid[columbusX - 1][columbusY] : "OUT_OF_BOUNDS";
              const bottom = (columbusX < grid.length - 1) ? grid[columbusX + 1][columbusY] : "OUT_OF_BOUNDS";
              const left = (columbusY > 0) ? grid[columbusX][columbusY - 1] : "OUT_OF_BOUNDS";
              const right = (columbusY < grid[0].length - 1) ? grid[columbusX][columbusY + 1] : "OUT_OF_BOUNDS";
              // If any adjacent cell is non-safe (non-ocean, non-island, non-treasure, non-health)
              const dangerousTiles = [top, bottom, left, right].filter(tile =>
                tile !== "OCEAN" && tile !== "ISLAND" && tile !== "TREASURE" && tile !== "HEALTH" && tile !== "OUT_OF_BOUNDS"
              );
              if (dangerousTiles.length > 0) {
                playSoundEffect(warningSound);
              }
              // ------------------------------------------------------

              // Send movement to backend and update game state
              await axios.post(`http://localhost:8080/move/${dir}`);
              fetchGame();
            } else {
              // Blocked by ISLAND
              console.log("Blocked! Cannot move onto an ISLAND.");
              return;
            }
          }
        }
      }
    };

    window.addEventListener("keydown", handleKey);
    return () => window.removeEventListener("keydown", handleKey);
  }, [fetchGame, grid]);

  // Log current grid state for debugging
  console.log("👀 Current grid state:", grid);

  return (
    <>
      {/* --- AUDIO ELEMENTS (hidden) --- */}
      {/* These provide background music and sound effects for the game */}
      <audio ref={backgroundMusicRef} src="/assets/audio/bg-music.mp3" />
      <audio ref={sailSound} src="/assets/audio/sail.m4a" />
      <audio ref={teleportSound} src="/assets/audio/teleport.mp3" />
      <audio ref={defeatSound} src="/assets/audio/defeat.mp3" />
      <audio ref={victorySound} src="/assets/audio/victory.mp3" />
      <audio ref={warningSound} src="/assets/audio/warning.mp3" />

      {/* --- TOP INFO PANEL (game title, stats, settings) --- */}
      {/* Displays the game title, level, stats, and settings button */}
      <div className="top-info">
        <h1 className="game-title">Christopher Columbus Adventure 🌊🚢</h1>
        <h2 className="level">Level: {currentLevel}</h2>
        <div className="stats">
          <div className="stat-item">❤️ Lives: {lives}</div>
          <div className="stat-item">🏆 Score: {score}</div>
        </div>
        <h3 className="high-score">🥇 High Score: {highScore}</h3>
        {/* Settings button (toggles settings panel) */}
        <button
          className="settings-gear"
          title="Settings"
          onClick={() => setShowSettings(!showSettings)}
          aria-label="Toggle Settings"
        >
          ⚙️
        </button>
      </div>

      {/* --- SETTINGS PANEL (audio controls) --- */}
      {/* Panel for toggling background music, sound effects, and volume */}
      {showSettings && (
        <div className="settings-panel">
          <h2>Settings</h2>
          <div className="setting-item">
            <label>
              <input
                type="checkbox"
                checked={backgroundMusicOn}
                onChange={() => setBackgroundMusicOn(!backgroundMusicOn)}
              />
              Background Music
            </label>
          </div>
          <div className="setting-item">
            <label>
              <input
                type="checkbox"
                checked={soundEffectsOn}
                onChange={() => setSoundEffectsOn(!soundEffectsOn)}
              />
              Sound Effects
            </label>
          </div>
          <div className="setting-item">
            <label>
              Volume:{" "}
              <input
                type="range"
                min="0"
                max="1"
                step="0.01"
                value={volume}
                onChange={(e) => setVolume(parseFloat(e.target.value))}
              />
            </label>
          </div>
        </div>
      )}

      {/* --- MAIN GAME CONTAINER --- */}
      <div className="game-container">
        {/* --- GRID PANEL (shows the game grid visually) --- */}
        {/* The grid is blurred if the game is over */}
        <div className={`grid-panel ${logs.some(log => log.includes("Game Over")) ? 'blur' : ''}`}>
          {Array.isArray(grid) && grid.length > 0 ? (
            grid.map((row, rowIndex) => (
              <div className="row" key={rowIndex}>
                {row.map((cell, colIndex) => (
                  <img
                    key={colIndex}
                    src={TILE_IMAGES[cell] || TILE_IMAGES.OCEAN}
                    alt={cell}
                    className="tile"
                  />
                ))}
              </div>
            ))
          ) : (
            <p>⏳ Waiting for grid...</p>
          )}
          {/* --- GAME OVER / VICTORY OVERLAY --- */}
          {/* Shows a styled overlay when the game ends, with retry/next/new game options */}
          {logs.some(log => log.includes("Game Over")) && (
            <>
              <style>{`
                .game-end-overlay {
                  position: absolute;
                  top: 0;
                  left: 0;
                  right: 0;
                  bottom: 0;
                  width: 100%;
                  height: 100%;
                  background: radial-gradient(ellipse at center, rgba(0, 10, 20, 0.9), rgba(0, 0, 0, 0.95));
                  display: flex;
                  justify-content: center;
                  align-items: center;
                  z-index: 9999;
                  animation: fadeInZoom 0.8s ease-out forwards;
                  backdrop-filter: blur(4px);
                }

                .game-end-content {
                  text-align: center;
                  padding: 40px 30px;
                  border-radius: 16px;
                  background: rgba(255, 255, 255, 0.05);
                  box-shadow: 0 0 20px rgba(0, 255, 255, 0.4);
                  animation: contentDrop 0.8s ease-out;
                  max-width: 90%;
                  width: 420px;
                }

                .victory-title {
                  font-size: 3.5rem;
                  color: #00ffe0;
                  text-shadow: 0 0 25px #00ffe0;
                  margin-bottom: 20px;
                }

                .defeat-title {
                  font-size: 3.5rem;
                  color: #ff4c4c;
                  text-shadow: 0 0 25px #ff4c4c;
                  margin-bottom: 20px;
                }

                .victory-sub, .defeat-sub {
                  font-size: 1.3rem;
                  color: #ffffff;
                  margin-bottom: 30px;
                  line-height: 1.6;
                }

                .overlay-buttons {
                  display: flex;
                  justify-content: center;
                  gap: 20px;
                  flex-wrap: wrap;
                }

                .overlay-btn {
                  padding: 12px 28px;
                  font-size: 1.1rem;
                  font-weight: bold;
                  border: none;
                  border-radius: 8px;
                  cursor: pointer;
                  transition: all 0.3s ease;
                  font-family: 'Courier New', Courier, monospace;
                }

                .overlay-btn.retry {
                  background-color: #00ffc3;
                  color: #003a33;
                }

                .overlay-btn.cancel {
                  background-color: #ff4c4c;
                  color: #fff;
                }

                .overlay-btn.retry:hover {
                  background-color: #00ddb0;
                }

                .overlay-btn.cancel:hover {
                  background-color: #e33;
                }

                @keyframes fadeInZoom {
                  0% { opacity: 0; transform: scale(0.8); }
                  100% { opacity: 1; transform: scale(1); }
                }

                @keyframes contentDrop {
                  0% { transform: translateY(-50px); opacity: 0; }
                  100% { transform: translateY(0); opacity: 1; }
                }
              `}</style>
              <div className="game-end-overlay">
                <div className="game-end-content">
                  {/* Victory and defeat messages based on logs */}
                  {logs.find(log => log.includes("Columbus found the treasure")) && (
                    <>
                      <h1 className="victory-title">🏆 VICTORY!</h1>
                      <p className="victory-sub">Columbus discovered the treasure!</p>
                    </>
                  )}
                  {logs.find(log => log.includes("Columbus was attacked by a pirate")) && (
                    <>
                      <h1 className="defeat-title">💀 DEFEAT!</h1>
                      <p className="defeat-sub">Game Over! Columbus was attacked by a pirate!</p>
                    </>
                  )}
                  {logs.find(log => log.includes("Sea Monster attacked Columbus")) && (
                    <>
                      <h1 className="defeat-title">💀 DEFEAT!</h1>
                      <p className="defeat-sub">💀 Game Over! Columbus was eaten by a sea monster!</p>
                    </>
                  )}
                  {logs.find(log => log.includes("opponent reached the treasure")) && (
                    <>
                      <h1 className="defeat-title">💀 DEFEAT!</h1>
                      <p className="defeat-sub">The opponent reached the treasure first!</p>
                    </>
                  )}
                  {/* Overlay buttons: Next Level, Retry, or New Game */}
                  <div className="overlay-buttons">
                    {logs.find(log => log.includes("Columbus found the treasure")) ? (
                      <button
                        className="overlay-btn retry"
                        onClick={async () => {
                          await axios.post("http://localhost:8080/nextlevel");
                          setPreviousLogs([]);
                          setLogs([]);
                          const newLevel = currentLevel + 1;
                          setCurrentLevel(newLevel);
                          const newScore = score + 20;
                          setScore(newScore);
                          setLives(3);
                          // Update highScore if needed
                          let updatedHighScore = highScore;
                          if (newScore > highScore) {
                            setHighScore(newScore);
                            updatedHighScore = newScore;
                          }
                          // Update localStorage
                          localStorage.setItem('currentLevel', newLevel);
                          localStorage.setItem('lives', 3);
                          localStorage.setItem('score', newScore);
                          localStorage.setItem('highScore', updatedHighScore);
                          fetchGame();
                        }}
                      >
                        ⏭️ Next Level
                      </button>
                    ) : (
                      lives > 1 ? (
                        <button
                          className="overlay-btn retry"
                          onClick={async () => {
                            const newLives = lives - 1;
                            setLives(prev => prev - 1);
                            localStorage.setItem('lives', newLives);
                            await axios.post("http://localhost:8080/reset");
                            setPreviousLogs([]);
                            setLogs([]);
                            fetchGame();
                          }}
                        >
                          🔁 Retry
                        </button>
                      ) : (
                        <button
                          className="overlay-btn retry"
                          onClick={async () => {
                            setCurrentLevel(1);
                            setLives(3);
                            setScore(0);
                            setPreviousLogs([]);
                            setLogs([]);
                            setGrid([]);
                            localStorage.setItem('currentLevel', 1);
                            localStorage.setItem('lives', 3);
                            localStorage.setItem('score', 0);
                            await axios.post("http://localhost:8080/reset");
                            fetchGame();
                          }}
                        >
                          🆕 New Game
                        </button>
                      )
                    )}
                  </div>
                </div>
              </div>
            </>
          )}
        </div>
        {/* --- LOG PANEL (Captain Rador) --- */}
        {/* Panel showing all previous logs and cell info for the player */}
        <div className="logs">
          <h2>Captain Rador</h2>
          <div className="log-scroll">
            {previousLogs.length === 0 ? (
              <div className="alert-log">👋 Welcome aboard, Captain Rador!</div>
            ) : (
              previousLogs.map((log, index) => (
                <div key={index} className="alert-log">{log}</div>
              ))
            )}
          </div>
        </div>
      </div>

      {/* --- STYLES FOR SETTINGS PANEL AND GAME END OVERLAY --- */}
      <style>{`
        .settings-gear {
          position: absolute;
          top: 12px;
          right: 12px;
          background: transparent;
          border: none;
          font-size: 1.8rem;
          cursor: pointer;
          color: #00ffe0;
          text-shadow: 0 0 8px #00ffe0;
          transition: transform 0.3s ease;
          z-index: 10000;
        }
        .settings-gear:hover {
          transform: rotate(20deg);
          color: #00bfa5;
          text-shadow: 0 0 12px #00bfa5;
        }
        .settings-panel {
          position: fixed;
          top: 60px;
          right: 20px;
          background: rgba(0, 20, 40, 0.9);
          border: 2px solid #00ffe0;
          border-radius: 12px;
          padding: 20px 25px;
          width: 280px;
          color: #00ffe0;
          font-family: 'Courier New', Courier, monospace;
          box-shadow: 0 0 15px #00ffe0;
          z-index: 10000;
          user-select: none;
          animation: fadeInZoom 0.4s ease forwards;
        }
        .settings-panel h2 {
          margin-top: 0;
          margin-bottom: 15px;
          font-size: 1.5rem;
          text-align: center;
          text-shadow: 0 0 10px #00ffe0;
        }
        .setting-item {
          margin-bottom: 15px;
          font-size: 1.1rem;
          display: flex;
          align-items: center;
          justify-content: space-between;
        }
        .setting-item label {
          cursor: pointer;
          flex-grow: 1;
        }
        .setting-item input[type="checkbox"] {
          margin-right: 10px;
          transform: scale(1.3);
          cursor: pointer;
          accent-color: #00ffe0;
        }
        .setting-item input[type="range"] {
          width: 120px;
          cursor: pointer;
          accent-color: #00ffe0;
          vertical-align: middle;
        }
      `}</style>
    </>
  );
}

// Export the main GameBoard component
export default GameBoard;