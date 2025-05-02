#### FRONTEND #######
# 1. Clone the repository (skip if already cloned)
navigato columbus-frontend
navigate to "/COLUMBUS/columbus-game/columbus-frontend"

# 2. Install dependencies
npm install

# 3. Run the development server
npm run dev

# 4. Open your browser
http://localhost:5173/


###### (Additional usefull commands )-No need to run every time#######
                                        rm -rf node_modules
                                        rm -rf .vite
                                        npm install
                                        npm run dev
###### BACKEND #######

# 1. Navigate to the backend directory
cd columbus-backend

# 2. Clean and install the project
mvn clean install

# 3. Run the backend server

mvn clean compile exec:java