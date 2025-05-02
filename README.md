# React + Vite

This template provides a minimal setup to get React working in Vite with HMR and some ESLint rules.

Currently, two official plugins are available:

- [@vitejs/plugin-react](https://github.com/vitejs/vite-plugin-react/blob/main/packages/plugin-react) uses [Babel](https://babeljs.io/) for Fast Refresh
- [@vitejs/plugin-react-swc](https://github.com/vitejs/vite-plugin-react/blob/main/packages/plugin-react-swc) uses [SWC](https://swc.rs/) for Fast Refresh

## Expanding the ESLint configuration

If you are developing a production application, we recommend using TypeScript with type-aware lint rules enabled. Check out the [TS template](https://github.com/vitejs/vite/tree/main/packages/create-vite/template-react-ts) for information on how to integrate TypeScript and [`typescript-eslint`](https://typescript-eslint.io) in your project.

//Team Mates : 

Name : Navyasree Dadireddy
ZID : Z2049349

Name : Lavanya Neela
ZID : Z2003960

Backend (Java):
1)Factory Pattern: Used OctopusFactory and SharkFactory to dynamically generate different sea monsters, supporting easy extension of enemy types.

2)Strategy Pattern: Implemented distinct movement strategies in Octopus (chases Columbus) and Shark (hunts nearest PirateShip) by extending the abstract SeaMonster class.

3)Composite Pattern: Used TileGroup and Whirlpool implementing TileComponent to manage multiple interactive tiles (like whirlpools) uniformly across the grid.

Frontend (React + TypeScript):
1)Rendered a dynamic 20x20 grid based on live game state, visually mapping each tile (Columbus, pirates, monsters, whirlpools, etc.) using condition-based rendering.

2)Integrated backend REST APIs (/state, /move, /reset, /newgame, /nextlevel) using fetch() for real-time updates and controls.

3)Displayed in-game notifications like nearby threats, game over messages, and heart pickups by parsing backend logs and showing them in a UI log panel.

4)Enabled player interaction through movement buttons or arrow keys, triggering API calls and re-rendering the board after each action.

5)Created a story-driven experience where visual feedback, character movement, and tile effects aligned with the gameplay logic and level progression.


Lavanya :                                   NavyaSree : 

SingletonPattern                            FactoryDesignPattern
DecoratorPattern                            StartegyDesignPattern
Observer Pattern                            CompositeDesignPattern
Front-end , UI                              Front-end - Story context
Test -case - GameStateTest                  UMLs
PPT                                         Sequence diagram
Documentation                               Test-Case-OceanGrid
                                  
                               