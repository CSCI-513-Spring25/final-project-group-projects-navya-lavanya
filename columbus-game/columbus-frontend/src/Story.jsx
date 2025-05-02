import React, { useState, useEffect } from "react";
import "./Story.css";

const scenes = [
    "Columbus dreams of discovering a new route to India",
    "He sets sailing and curious to explore the",
    "The ocean is vast and mysterious; food is running low",
    "A crew member spots sea monsters in the fog",
    "They navigate through whirlpools and treacherous rocks",
    "Columbus calms the fearful crew with inspiring words",
    "They encounter a pirate fleet demanding treasure",
    "After a storm, they discover a mysterious map",
    "The treasure island is within reach, but guarded fiercely",
    "Columbus prepares to take the final leap for glory"
  ];

export default function Story({ onComplete }) {
  const [sceneIndex, setSceneIndex] = useState(0);
  const [displayedText, setDisplayedText] = useState("");
  const audioRef = React.useRef(null);

useEffect(() => {
  if (audioRef.current) {
    audioRef.current.play().catch((e) => {
      console.warn("Autoplay blocked; will play on user interaction");
    });
  }
}, []);

useEffect(() => {
  const scene = scenes[sceneIndex];
  if (!scene || scene.length === 0) return;

  let charIndex = 1;
  setDisplayedText(scene[0]);

  const interval = setInterval(() => {
    if (charIndex < scene.length) {
      const nextChar = scene[charIndex];
      if (nextChar !== undefined) {
        setDisplayedText((prev) => prev + nextChar);
      }
      charIndex++;
    } else {
      clearInterval(interval);
    }
  }, 40);

  return () => clearInterval(interval);
}, [sceneIndex]);

  const nextScene = () => {
    if (sceneIndex < scenes.length - 1) {
      setSceneIndex(sceneIndex + 1);
    } else {
      onComplete();
    }
  };

  const currentImage = `/assets/story/${sceneIndex + 1}.jpg`;

  return (
    <>
      <audio ref={audioRef} src="/assets/audio/bg-music.mp3" loop playsInline />
      <div className="story-container">
        <div
          className="story-background"
          style={{ backgroundImage: `url(${currentImage})` }}
        ></div>

        <h1 className="story-title">Christopher Columbus Adventure</h1>

        <div className="story-chat-box">
          <div className="chat-bubble">{displayedText}</div>
          <div className="story-buttons">
            <button
              onClick={() => {
                audioRef.current?.play();
                nextScene();
              }}
            >
              {sceneIndex < scenes.length - 1 ? "Next" : "Start Game"}
            </button>
            <button
              onClick={() => {
                audioRef.current?.play();
                onComplete();
              }}
            >
              Skip
            </button>
          </div>
        </div>
      </div>
    </>
  );
}