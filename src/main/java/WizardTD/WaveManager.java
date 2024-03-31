package WizardTD;

import processing.data.JSONArray;
import processing.data.JSONObject;
import processing.core.PApplet;

/**
 * Manages game waves, including wave timing, progression and text display.
 */
public class WaveManager {
    
    public int waveNumber, monsterIndex, FPS, currentMonsterQuantity;
    public double prewavePause, waveDuration, countdown;
    public JSONObject currentWave, nextWave, details, currentMonsters;
    public boolean preWaveState, firstWave, wavesDone;
    public JSONArray waves, monsters;

    /**
     * Creates WaveManager instance to handle waves of current game level.
     * 
     * @param details JSON Object containing wave details.
     * @param FPS Frames per second for the game.
     */
    public WaveManager(JSONObject details, int FPS) {
        this.preWaveState = true;
        this.waveNumber = 0;
        this.monsterIndex = 0;
        this.firstWave = true;
        this.details = details;
        this.FPS = FPS;
        this.waves = details.getJSONArray("waves");
        this.currentWave = waves.getJSONObject(waveNumber);
        this.nextWave = waves.getJSONObject(waveNumber + 1);
        this.monsters = currentWave.getJSONArray("monsters");
        this.currentMonsters = monsters.getJSONObject(monsterIndex);
        this.currentMonsterQuantity = 0;        
        
        
    }

    /**
     * Manages timing of the different game waves.
     * 
     * @param framesPassed The number of frames that have passed since the beginning of the level.
     */
    public void waveTimer(int framesPassed) {
        
        // If game just started, only need pre wave pause for countdown
        if (framesPassed == 0) {
                this.prewavePause = currentWave.getDouble("pre_wave_pause");
                this.countdown = this.prewavePause;
            }

        // Pre wave pause for beginning of game
        else if (firstWave) {
            if (framesPassed == prewavePause * FPS) {
                firstWave = false;
            
            // Counting down every second, only if at least one second has passed
            } else if (framesPassed >= 60 && framesPassed % 60 == 0) {
                countdown--;
            }
        }
        else if (countdown <= 0) {
            this.prewavePause = nextWave.getInt("pre_wave_pause");
            this.waveDuration = currentWave.getInt("duration");
            this.countdown =  prewavePause + waveDuration;
        }
            
        else {
            if (framesPassed % 60 == 0) {
                countdown--;

                if (countdown <= 0) {
                    // Move on to next wave, unless there are no more waves left
                    if (!this.currentWave.equals(this.nextWave)) {
                        
                        this.currentWave = waves.getJSONObject(waveNumber++);
                        this.monsters = currentWave.getJSONArray("monsters");
                        this.monsterIndex = 0;
                        this.currentMonsters = monsters.getJSONObject(monsterIndex);
                        this.currentMonsterQuantity = 0;
                        preWaveState = false;

                        // Check if there is any more waves after current wave
                        if (waveNumber  < waves.size() - 1) {
                            this.nextWave = waves.getJSONObject(waveNumber + 1);
                        }
                    } else {
                        wavesDone = true;
                    }
                    
                } else if (countdown <= prewavePause) {
                    // Setting pre wave state back to true once duration of wave is over
                    preWaveState = true;
                }
                
            }
        }  
            
    }

    /**
     * Calculates the time interval in seconds monsters should spawn at.
     * 
     * @return The time interval calculated.
     */
    public float durationBetweenMonsters() {
        return currentWave.getFloat("duration")/totalMonstersInWave();
    }

    /**
     * Writes wave timer onto the window.
     * 
     * @param app The PApplet window for rendering.
     */
    public void writeWaveText(PApplet app) {
        app.fill(0);
        app.textSize(20);
        
        if (waveNumber + 1 <= waves.size()) {
            String message = "Wave " + (waveNumber + 1) + " starts: " + (int)countdown;
            app.text(message, 10, 25);
        }

    } 
    
    /**
     * Moves on to next element in monsters array within wave array if applicable.
     */
    public void nextMonsterElement() {
        if (monsterIndex != monsters.size() - 1) {
            this.currentMonsters = monsters.getJSONObject(++monsterIndex);
        }

        currentMonsterQuantity = 0;

    } 
    
    /**
     * Calculates the total number of monsters within the current wave.
     * 
     * @return The number of monsters to spawn in current wave.
     */
    public int totalMonstersInWave() {
        int total = 0;
        for (int i = 0; i < monsters.size(); i++) {
            total += monsters.getJSONObject(i).getInt("quantity");
        }

        return total;
    }

    /**
     * Increments the index for the monster array.
     */
    public void incrementMonsterQuantity() {
        this.currentMonsterQuantity++;
    }

    public double getCountdown() {
        return countdown;
    }
}