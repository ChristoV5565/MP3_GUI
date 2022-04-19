//Ce fichier a été regénéré à partir d'un .class.

package sample;

import javafx.collections.MapChangeListener;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.media.Media;
import javafx.util.Duration;

/**
 * Objet chanson qui représente un morceau de musique jouable par un mediaplayer ainsi que les informations contenues
 * par son fichier
 *
 * @author Christophe Verreault
 */
public class Chanson {
    /**Nombre total de chansons permettant de numéroter les chansons*/
    public static int nombreChansons = 0;

    //Informations de base sur la chanson
    /**Position de la chanson dans le lecteur*/
    private int position;

    /**Titre de la chanson inscrit dans les métadonnées*/
    private String titre;

    /**Album de la chanson inscrit dans les métadonnées*/
    private String album;

    /**Artiste de la chanson inscrit dans les métadonnées*/
    private String artiste;

    /**Durée en millisecondes de la chanson*/
    private Duration duree;

    /**Objet media permettant de faire jouer la chanson*/
    private Media media;

    /**
     * Constructeur de la classe chanson
     *
     * @param s Lien physique vers le fichier de musique
     */
    public Chanson(String s) {
        nombreChansons ++;
        position = nombreChansons;
        media = new Media(s);

        //Durée
        duree = media.getDuration();

        //Remplit les informations de l'objet lors de la réception de celles du fichier

        media.getMetadata().addListener((MapChangeListener<? super String, ? super Object>) (change) -> {
            if (change.wasAdded()) {

                //Cette section a été reconstruite à partir d'un .class d'un projet corrompu.

                String var2 = ((String)change.getKey()).toString();
                byte resultat = -1;
                switch(var2.hashCode()) {
                    case -1409097913:
                        if (var2.equals("artist")) {
                             resultat= 2;
                        }
                        break;
                    case 92896879:
                        if (var2.equals("album")) {
                             resultat= 1;
                        }
                        break;
                    case 110371416:
                        if (var2.equals("title")) {
                             resultat= 0;
                        }
                }

                switch (resultat) {
                    case 0:
                        titre = change.getValueAdded().toString();
                        break;
                    case 1:
                        album = change.getValueAdded().toString();
                        break;
                    case 2:
                        artiste = change.getValueAdded().toString();
                }
            }


            //Permet de mettre à jour le tableau une fois les données reçues
            Controller.updateTab();
        });
    }

    /**
     * Retourne le titre de la chanson
     *
     * @return String contenant le titre de la chanson
     * */
    public String getTitre() {
        return titre;
    }

    /**
     * Permet de changer le titre de la chanson
     *
     * @param titre Nouveau titre de la chanson
     * */
    public void setTitre(String titre) {
        titre = titre;
    }

    /**
     * Retourne l'artiste de la chanson
     *
     * @return String contenant l'artiste de la chanson
     * */
    public String getArtiste() {
        return artiste;
    }

    /**
     * Permet de changer l'artiste de la chanson
     *
     * @param artiste Nouvel artiste de la chanson
     * */
    public void setArtiste(String artiste) {
        artiste = artiste;
    }

    /**
     * Retourne l'album de la chanson
     *
     * @return String contenant l'album de la chanson
     * */
    public String getAlbum() {
        return album;
    }

    /**
     * Permet de changer l'album de la chanson
     *
     * @param album Nouvel album de la chanson
     * */
    public void setAlbum(String album) {
        album = album;
    }

    /**
     * Retourne le média de la chanson
     *
     * @return Media Objet media du fichier contenant la chanson
     * */
    public Media getMedia() {
        return media;
    }

    /**
     * Permet de changer le média de la chanson
     *
     * @param media nouvel Objet media du fichier contenant la chanson
     * */
    public void setMedia(Media media) {
        media = media;
    }

    /**
     * Permet de changer la position de la chanson
     *
     * @param position Nouvelle position de la chanson
     * */
    public void setPosition(int position)
    {
        position = position;
    }

    /**
     * Retourne la position de la chanson
     *
     * @return int Position de la chanson
     * */
    public int getPosition()
    {
        return position;
    }

    /**Recule la position de la chanson dans le classement*/
    public void updatePosition()
    {
        position --;
    }

    /**Diminue le nombre de chansons*/
    public static void reculerNombreChansons()
    {
        nombreChansons --;
    }
}
