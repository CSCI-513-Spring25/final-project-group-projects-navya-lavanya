package com.columbusadventure;

import fi.iki.elonen.NanoHTTPD;

// GameServer is a lightweight HTTP server that listens for client requests
// and interacts with the game state accordingly.
public class GameServer extends NanoHTTPD {

    // Get the singleton instance of the game's current state
    private static final GameState state = GameState.getInstance();

    // Constructor: starts the server on port 8080
    public GameServer() {
        super(8080);
    }

    // Handles incoming HTTP requests from the frontend
    @Override
    public Response serve(IHTTPSession session) {
        String uri = session.getUri(); // Get the request path
        Response response;

        // Endpoint to retrieve the current game state as JSON
        if (session.getMethod() == Method.GET && "/state".equals(uri)) {
            response = newFixedLengthResponse(state.toJson());

            // Endpoint to move Columbus in a specified direction (e.g., /move/up)
        } else if (session.getMethod() == Method.POST && uri.startsWith("/move/")) {
            String direction = uri.substring("/move/".length());
            state.moveColumbus(direction); // Move Columbus based on direction

            // After Columbus moves, update all sea monsters
            for (SeaMonster sm : state.getSeaMonsters()) {
                if (sm instanceof Octopus || sm instanceof Shark) {
                    sm.move(state);
                }
            }

            response = newFixedLengthResponse(state.toJson());

            // Endpoint to reset the game (either full reset or level retry)
        } else if (session.getMethod() == Method.POST && "/reset".equals(uri)) {
            if (session.getParms().containsKey("newGame") && "true".equals(session.getParms().get("newGame"))) {
                state.newGame(); // Start a brand-new game
            } else {
                state.reset(false); // Retry the current level
            }
            response = newFixedLengthResponse(state.toJson());

            // Endpoint to advance to the next level
        } else if (session.getMethod() == Method.POST && "/nextlevel".equals(uri)) {
            state.nextLevel();
            response = newFixedLengthResponse(state.toJson());

            // Endpoint to start a brand-new game manually
        } else if (session.getMethod() == Method.POST && "/newgame".equals(uri)) {
            state.newGame();
            response = newFixedLengthResponse(state.toJson());

            // If none of the above endpoints match, return 404
        } else {
            response = newFixedLengthResponse(Response.Status.NOT_FOUND, MIME_PLAINTEXT, "Not found");
        }

        // Allow cross-origin requests from the frontend
        response.addHeader("Access-Control-Allow-Origin", "*");
        return response;
    }

    // Main method to start the HTTP server
    public static void main(String[] args) {
        GameServer server = new GameServer();
        try {
            server.start(NanoHTTPD.SOCKET_READ_TIMEOUT, false); // Start the server with a read timeout
            System.out.println("Server started at http://localhost:8080");
        } catch (Exception e) {
            System.err.println("Couldn't start server:\n" + e);
        }
    }
}
