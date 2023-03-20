package ch.epfl.javelo.gui;

import javafx.animation.FadeTransition;
import javafx.animation.PauseTransition;
import javafx.animation.SequentialTransition;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.util.Duration;

/**
 * gère l'affichage de messages d'erreur
 *
 * @author Evan Massonnet (346642)
 */

public final class ErrorManager {

    private final VBox errorBox;
    private final SequentialTransition transitions;


    public ErrorManager(){

        errorBox = new VBox();
        errorBox.getStylesheets().add("error.css");
        errorBox.setMouseTransparent(true);


        FadeTransition fadeIn = new FadeTransition(Duration.seconds(0.2), errorBox);
        fadeIn.setFromValue(0);
        fadeIn.setToValue(0.8);

        PauseTransition pause = new PauseTransition(Duration.seconds(2));

        FadeTransition fadeOut = new FadeTransition(Duration.seconds(0.5), errorBox);
        fadeOut.setFromValue(0.8);
        fadeOut.setToValue(0);

        transitions = new SequentialTransition(fadeIn, pause, fadeOut);

    }

    /**
     * retournant le panneau sur lequel apparaissent les messages d'erreur
     * @return errorBox
     */
    public Pane pane(){
        return errorBox;
    }

    /**
     * prenant en argument une chaîne de caractères représentant un message d'erreur
     * et le faisant apparaître temporairement à l'écran, accompagné d'un son indiquant l'erreur.
     * @param errorString la phrase a afficher
     */
    public void displayError(String errorString){
        Text text = new javafx.scene.text.Text(errorString);
        errorBox.getChildren().clear();
        errorBox.getChildren().add(text);

        java.awt.Toolkit.getDefaultToolkit().beep();

        transitions.stop();     //si une autre animation est deja lancé, on l'arrete
        transitions.play();



    }

}
