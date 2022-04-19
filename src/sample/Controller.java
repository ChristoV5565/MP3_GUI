package sample;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.media.MediaPlayer;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;

/**
 * Classe contenant les fonctions associées aux contrôles dans la fenêtre du programme
 *
 * @author Christophe Verreault
 */
public class Controller {
    @FXML
    /**Panneau racine*/
    public AnchorPane panneauRoot;

    /**Bouton permettant de commencer la lecture du morceau de musique choisi*/
    public Button play;

    /**Bouton permettant d'arrêter la lecrure du morceau de musique choisi*/
    public Button pause;

    /**Bouton permettant de faire jouer la prochaine chanson*/
    public Button avancer;

    /**Bouton permettant de faire jouer la chanson précédente*/
    public Button reculer;

    /**Indique la chanson prête à être jouée par le lecteur*/
    public Label enJeu;

    /**Menu contextuel permettant de supprimer une chanson de la liste*/
    public ContextMenu supMenu;

    /**Item dans le menu contextuel de suppression*/
    public MenuItem supMenuItem;

    /**Permet de visualiser les chansons disponibles pour la lecture*/
    public static TableView tableau;

    /**Morceau individuel de musique*/
    public File musique;

    /**Lien absolu vers le fichier de musique*/
    public String lien;

    /**Objet représentant une chanson, incluant informations sur le fichier et objet media*/
    public Chanson objetChanson;

    /**Permet de faire jouer un morceau de musique*/
    public MediaPlayer player;

    /**Liste de toutes les chansons*/
    public static ObservableList<Chanson> chansons = FXCollections.observableArrayList();

    /**Index de la chanson qui joue ou qui va jouer si play est cliqué*/
    public int positionActuelle = 0;

    /**Liste de tous les fichiers sélectionnés par le filechooser*/
    public List<File> listeFichier;

    /**
     * Constructeur par défaut sans arguments du contrôleur
     */
    public Controller() {
    }

    /**
     * Associe tous les contrôles à leurs fonctions respectives lors de l'ouverture de la fenêtre
     */
    public void initialize() {
        //Génère le tableView qui contiendra les chansons
        genererTab();

        //Active ou désactive les boutons selont s'il sont requis
        updateBoutons();

        //Instanciation de l'item dans le menu contextuel
        supMenuItem = new MenuItem("Supprimer");

        //Action lorsque le bouton Supprimer du contextmenu est cliqué
        supMenuItem.setOnAction(actionEvent -> {
            try
            {
                //Sélection de l'index de la chanson à supprimer
                int idSup = tableau.getSelectionModel().getSelectedIndex();

                //On recule la position si une chanson plus haut dans la liste est retirée
                if(positionActuelle > idSup)
                {
                    positionActuelle --;
                }
                else if(positionActuelle == idSup && player.getStatus() == MediaPlayer.Status.PLAYING)
                {
                    //La chanson à supprimer joue en ce moment et elle est arrêtée avant d'être retirée
                    player.stop();
                    positionActuelle --;
                }

                //La chanson est supprimée du tableau et de la liste de chansons
                tableau.getItems().remove(tableau.getSelectionModel().getSelectedIndex());

                //Puisqu'une chanson est retirée, le nombre total diminue
                Chanson.reculerNombreChansons();

                //Toutes les positions des chansons en dessous de celle qui a été retirée
                for(Chanson c : chansons)
                {
                    if(c.getPosition() > idSup+1)
                    {
                        c.updatePosition();
                    }
                }

                //Tableau et boutons mis à jour
                updateTab();
                updateBoutons();
            }
            catch (IndexOutOfBoundsException e) //Le menu contextuel est cliqué sans sélection
            {
                Alert a = new Alert(AlertType.INFORMATION);
                a.setTitle("Erreur!");
                a.setHeaderText("Aucune chanson sélectionnée");
                a.show();
            }
        });

        //Le menu contextuel est associé au tableau
        supMenu = new ContextMenu(supMenuItem);
        tableau.setContextMenu(supMenu);

        //Le bouton pause est désactivé tant qu'une chanson n'est pas cours de lecture
        pause.setDisable(true);

        //Permet de faire jouer une chanson si elle est double-cliquée
        tableau.setOnMouseClicked(mouseEvent -> {
            if (mouseEvent.getClickCount() == 2)
            {
                try {
                    positionActuelle = tableau.getSelectionModel().getSelectedIndex();

                    //Évite une exception si player est entrain de jouer une chanson
                    try
                    {
                        player.stop();
                    }
                    catch( NullPointerException e)
                    {
                        //Ne rien faire; le player est déjà arrêté
                    }

                    //Joue la chanson
                    jouer();

                    updateBoutons();
                } catch (IndexOutOfBoundsException e) { //L'utilisateur a double-cliqué ailleurs que sur une chanson
                    //Affiche une erreur
                    Alert a = new Alert(AlertType.ERROR);
                    a.setTitle("Erreur!");
                    a.setHeaderText("Vous n'avez pas sélectionné de chanson!");
                    a.show();
                }
            }
        });

        //Action lorsque le bouton play est cliqué
        play.setOnAction((actionEvent) -> {
            //Joue la chanson
            jouer();
            play.setDisable(true);
            pause.setDisable(false);
        });

        //Action lorsque le bouton pause est cliqué
        pause.setOnAction((actionEvent) -> {
            player.pause();
            play.setDisable(false);
            pause.setDisable(true);

            //updateBoutons();
        });

        //Action lorsque le bouton avancer est cliqué
        avancer.setOnAction(actionEvent -> {
            //Teste si une chanson est lue en ce moment
            if(!Objects.isNull(player) && player.getStatus() == MediaPlayer.Status.PLAYING)
            {
                //La chanson en cours de lecture est arrêtée avant d'en faire jouer une autre
                player.stop();
            }

            //Avance la position du lecteur
            positionActuelle ++;

            //Joue la chanson
            jouer();

            updateBoutons();
        });

        //Action lorsque le bouton avancer est cliqué
        reculer.setOnAction(actionEvent -> {

            //Teste si une chanson est lue en ce moment
            if(!Objects.isNull(player) && player.getStatus() == MediaPlayer.Status.PLAYING)
            {
                //La chanson en cours de lecture est arrêtée avant d'en faire jouer une autre
                player.stop();
            }

            //La position du lecteur est reculée
            positionActuelle --;

            jouer();

            updateBoutons();
        });
    }

    /**
     * Ouvre un dialogue filechooser qui permet de chosir des fichiers mp3 individuels
     */
    public void ouvrirFichier()
    {
        //Instanciation de l'objet
        FileChooser fc = new FileChooser();

        //Ajouter titre
        fc.setTitle("Choisir un fichier...");

        //Ajouter filtre d'extension de fichier
        fc.getExtensionFilters().add(new ExtensionFilter("Fichiers Audio ", new String[]{"*.mp3"}));

        //Ouverture du dialogue. Le résultat (liste de URI) est mis dans la variable listeFichier
        try
        {
            //Collecte des Fichiers dans la liste
            listeFichier = fc.showOpenMultipleDialog(panneauRoot.getScene().getWindow());

            //Itération sur le contenu de la liste
            for(File f : listeFichier)
            {
                //Rajoute chaque chanson à la liste
                musique = new File(f.toURI());
                lien = musique.toURI().toString();
                objetChanson = new Chanson(lien);
                chansons.add(objetChanson);
            }

            play.setDisable(false);
        }
        catch (NullPointerException e)
        {
            Alert a = new Alert(AlertType.ERROR);
            a.setTitle("Erreur!");
            a.setHeaderText("Aucun fichier choisi.");
            a.show();
        }

        updateBoutons();
    }

    /**
     * Ouvre un dialogue filechooser qui permet de chosir un dossier et d'en extraire les fichiers mp3
     */
    public void ouvrirRepertoire()
    {
        //Instanciation de l'objet
        DirectoryChooser dc = new DirectoryChooser();

        //Ajouter titre
        dc.setTitle("Choisir un dossier...");

        //Ouverture du dialogue. Le résultat (liste de URI) est mis dans la variable listeFichier
        try
        {
            int fichierstrouve = 0;

            File fichier = dc.showDialog(panneauRoot.getScene().getWindow());
            //Collecte des Fichiers dans la liste
            listeFichier = Arrays.stream(fichier.listFiles()).toList();

            //Itération sur le contenu de la liste
            for(File f : listeFichier)
            {
                //Extrait les extensions des noms de fichiers
                //Source : https://stackoverflow.com/questions/3571223/how-do-i-get-the-file-extension-of-a-file-in-java
                String extension = "";

                int i = f.toString().lastIndexOf('.');
                if (i > 0) {
                    extension = f.toString().substring(i+1);
                }

                //Ne sélectionne que les fichiers avec une extension mp3
                if(extension.equals("mp3"))
                {
                    //Rajoute chaque chanson à la liste
                    musique = new File(f.toURI());
                    lien = musique.toURI().toString();
                    objetChanson = new Chanson(lien);
                    chansons.add(objetChanson);

                    fichierstrouve ++;
                }
            }

            //Aucun fichier mp3 trouvé dans le répertoire
            if(fichierstrouve == 0)
            {
                Alert a = new Alert(AlertType.INFORMATION);
                a.setTitle("Erreur!");
                a.setHeaderText("Aucun fichier trouvé.");
                a.show();
            }

            play.setDisable(false);
        }
        catch (NullPointerException e)
        {
            Alert a = new Alert(AlertType.ERROR);
            a.setTitle("Erreur!");
            a.setHeaderText("Aucun répertoire choisi.");
            a.show();
        }

        updateBoutons();
    }

    /**
     * Ajoute les colonnes au tableau et les associe avec les champs de la classe chanson
     */
    public void genererTab()
    {
        //Génère le tableview
        TableColumn<Chanson, Integer> colPos = new TableColumn<>("Position"); //Colonne Position
        colPos.setMinWidth(59);
        colPos.setCellValueFactory(new PropertyValueFactory<>("position"));

        TableColumn<Chanson, Integer> colArt = new TableColumn<>("Artiste"); //Colonne Artiste
        colArt.setMinWidth(142);
        colArt.setCellValueFactory(new PropertyValueFactory<>("artiste"));

        TableColumn<Chanson, Integer> colAlb = new TableColumn<>("Album"); //Colonne Album
        colAlb.setMinWidth(142);
        colAlb.setCellValueFactory(new PropertyValueFactory<>("album"));

        TableColumn<Chanson, Integer> colTit = new TableColumn<>("Titre"); //Colonne Titre
        colTit.setMinWidth(142);
        colTit.setCellValueFactory(new PropertyValueFactory<>("titre"));

        tableau = new TableView<Chanson>();
        tableau.getColumns().addAll(colPos, colArt, colAlb, colTit);
        panneauRoot.getChildren().add(tableau);
        panneauRoot.setBottomAnchor(tableau, 44.0);
        panneauRoot.setTopAnchor(tableau, 24.0);
        panneauRoot.setRightAnchor(tableau, 0.0);
        panneauRoot.setLeftAnchor(tableau, 0.0);

        tableau.setItems(chansons);
    }

    /**
     * Permet de mettre à jour le tableau
     */
    public static void updateTab()
    {
        tableau.refresh();
    }

    /**
     * Permet de bloquer les boutons avancer et reculer lorsqu'ils ne servent à rien;
     * Soit quand le lecteur en est à la première ou à la dernière chanson
     */
    public void updateBoutons()
    {
        int taille = chansons.size();

        //Le lecteur est à la première position de la liste
        if(positionActuelle == 0 && taille > 0)
        {
            //Ne peut pas reculer
            reculer.setDisable(true);
        }
        else //Le lecteur n'est pas à la première position de la liste
        {
            //Peut reculer
            reculer.setDisable(false);
        }

        //Le lecteur est à la dernière position de la liste
        if(positionActuelle == taille - 1)
        {
            //Ne peut pas avancer
            avancer.setDisable(true);
        }
        else //Le lecteur n'est pas à la dernière position de la liste
        {
            //Peut avancer
            avancer.setDisable(false);
        }

        //Le player n'est pas nul, mais il ne joue pas de musique
        if(!Objects.isNull(player) && player.getStatus() == MediaPlayer.Status.STOPPED && taille > 0)
        {
            pause.setDisable(true);
            play.setDisable(false);
        }

        //Le programme n'a aucune chanson dans sa liste
        if(taille == 0)
        {
            enJeu.setText("Lecture : Aucune chanson");
            positionActuelle = 0;

            pause.setDisable(true);
            play.setDisable(true);
            avancer.setDisable(true);
            reculer.setDisable(true);
        }
        else
        {
            enJeu.setText("Lecture : " + (positionActuelle + 1));
        }
    }

    /**
     * Fait jouer une chanson selon l'index dans positionActuelle
     */
    public void jouer()
    {
        player = new MediaPlayer(((Chanson)chansons.get(positionActuelle)).getMedia());

        player.setOnReady(() -> {
            player.play();
            play.setDisable(true);
            pause.setDisable(false);
        });
    }

    /**
     * Permet de quitter le programme à l'aide de l'option du menu
     */
    public void quitter() {
        System.exit(0);
    }
}
