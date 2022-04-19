package sample;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;


/**
 * Classe Main qui permet de démarrer le programme
 *
 * @author Christophe Verreault
 */
public class Main extends Application {

    @Override
    /**
     * Démarre le programme et affiche la fenêtre
     *
     * @param primaryStage Composant racine de la fenêtre
     */
    public void start(Stage primaryStage) throws Exception{
        AnchorPane root = FXMLLoader.load(getClass().getResource("sample.fxml"));

        //Applique le titre de la fenêtre
        primaryStage.setTitle("VERC - Lecteur Mp3");

        //Dimentions initiales de la fenêtre
        primaryStage.setScene(new Scene(root, 600, 400));

        //Montre la fenêtre
        primaryStage.show();
    }

    /**
     * Classe main qui démarre le programme
     *
     * @param args Arguments de programme
     * */
    public static void main(String[] args) {
        launch(args);
    }
}