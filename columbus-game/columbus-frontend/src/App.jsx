import React, { useState, useEffect } from 'react';
import GameBoard from './GameBoard';
import Story from './Story';

export default function App() {
  const [showStory, setShowStory] = useState(true); // this controls the flow

  useEffect(() => {
    const storySeen = localStorage.getItem("storySeen");
    if (storySeen === "true") {
      setShowStory(false);
    }
  }, []);

  const handleStoryComplete = () => {
    localStorage.setItem("storySeen", "true");
    setShowStory(false);
  };

  return (
    <div>
      {showStory ? <Story onComplete={handleStoryComplete} /> : <GameBoard />}
    </div>
  );
}