import dev.robocode.tankroyale.botapi.*;

// ------------------------------------------------------------------
// OE-Hello-World
// ------------------------------------------------------------------
// The first bot created by Keith D'Atrio
// For the Zoom internal Robocode Battle Tank Royale
// Overengineered Hello World Tank Bot
//
// ------------------------------------------------------------------

public class OE-Hello-World extends Bot {

    // The main method starts the Helo World bot
    public static void main(String[] args) {
        new OE-Hello-World().start();
    }


    // Constructor, which loads the bot config file
    OE-Hello-World() {
        super(BotInfo.fromFile("oe-hello-world.json"));
    }
    
}