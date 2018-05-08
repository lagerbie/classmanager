package thot.labo;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import thot.utils.Utilities;

/**
 * Gestion des tags au format MP3.
 *
 * @author Fabrice Alleau
 * @version 1.8.4
 */
public class TagList {
    /**
     * Clé pour le titre
     */
    public static final String TITLE = "ETT";
    /**
     * Clé pour l'auteur
     */
    public static final String AUTHOR = "AUT";
    /**
     * Clé pour l'auteur
     */
    public static final String ARTIST = "EAR";
    /**
     * Clé pour l'album
     */
    public static final String ALBUM = "EAL";
    /**
     * Clé pour les commentaires
     */
    public static final String COMMENT = "comment";
    /**
     * Clé pour la piste
     */
    public static final String TRACK = "track";
    /**
     * Clé pour le genre
     */
    public static final String GENRE = "genre";
    /**
     * Clé pour l'année
     */
    public static final String YEAR = "year";
    /**
     * Clé pour l'information
     */
    public static final String INFORMATION = "INF";
    /**
     * Clé pour l'image
     */
    public static final String IMAGE = "IMG";
    /**
     * Identifiant pour un genre non détérminé
     */
    public static final int GENRE_NONE = 255;

    /**
     * Stockage des différentes valeurs des tags
     */
    private Map<String, String> metadata;

    /**
     * Initialisation.
     *
     * @since version 1.00
     */
    public TagList() {
        metadata = new HashMap<>(4);
    }

    /**
     * Ajoute ou modifie un tag existant.
     *
     * @param key le type du tag.
     * @param value la valeur du tag.
     */
    public void putTag(String key, String value) {
        metadata.put(key, value);
    }

    /**
     * Retourne la valeur du tag.
     *
     * @param key le type du tag.
     *
     * @return la valeur du tag ou null si le tag n'existe pas.
     */
    public String getTag(String key) {
        return metadata.get(key);
    }

    /**
     * Supprime un tag de la liste.
     *
     * @param key le type du tag.
     *
     * @return la valeur précédente du tag ou null si le tag n'existait pas.
     */
    public String removeTag(String key) {
        return metadata.remove(key);
    }

    /**
     * Supprime tous les tags de la liste.
     */
    public void removeAll() {
        metadata.clear();
    }

    /**
     * Retourne la liste toutes les types de tags contenus dans la liste.
     *
     * @return la liste des types de tags affectés.
     */
    public Set<String> getTagKeys() {
        return metadata.keySet();
    }

    /**
     * Retourne si la liste des tags est vide.
     *
     * @return si la liste des tags est vide.
     */
    public boolean isEmpty() {
        return metadata.isEmpty();
    }

    /**
     * Compare la liste de tags avec une une autre liste.
     *
     * @param tags la liste avec laquelle il faut comparer.
     *
     * @return si les deux listes ont le même contenu.
     */
    public boolean isIndenticTo(TagList tags) {
        Set<String> keys = tags.getTagKeys();
        if (metadata.size() != keys.size()) {
            return false;
        }

        String value;
        String currentValue;
        for (String key : keys) {
            if (!metadata.containsKey(key)) {
                //clé non présente
                return false;
            } else {
                value = tags.getTag(key);
                currentValue = getTag(key);
                if ((value == null && currentValue != null) || (value != null && currentValue == null) || !currentValue
                        .contentEquals(value)) {
                    return false;
                }
            }
        }

        return true;
    }

    /**
     * Ecrit les tags à la fin du fichier au format mp3.
     * <p>
     * Cette méthode peut à la fois écrire les tags ID3 et Lyrics3.
     *
     * @param file le fichier mp3.
     *
     * @throws IOException
     */
    public void writeTagsToMp3(File file) throws IOException {
        String title = getTag(TITLE);
        String artist = getTag(ARTIST);
        String album = getTag(ALBUM);
        String author = getTag(AUTHOR);
        String information = getTag(INFORMATION);

        String year = getTag(YEAR);
        String image = getTag(IMAGE);
        String comment = getTag(COMMENT);
        String track = getTag(TRACK);
        String genre = getTag(GENRE);

        if (title == null) {
            title = "";
        } else if (title.length() > 250) {
            title = title.substring(0, 250);
        }

        if (artist == null) {
            artist = "";
        } else if (artist.length() > 250) {
            artist = artist.substring(0, 250);
        }

        if (album == null) {
            album = "";
        } else if (album.length() > 250) {
            album = album.substring(0, 250);
        }

        if (author == null) {
            author = "";
        } else if (author.length() > 250) {
            author = author.substring(0, 250);
        }

        if (information == null) {
            information = "";
        } else if (information.length() > 99999) {
            information = information.substring(0, 99999);
        }

        if (year == null) {
            year = "";
        } else if (year.length() > 4) {
            year = year.substring(0, 4);
        }

        if (image == null) {
            image = "";
        } else if (image.length() > 99999) {
            image = image.substring(0, 99999);
        }

        if (comment == null) {
            comment = "";
        }
        if (track == null) {
            track = "";
        }

        // open the file for writing
        RandomAccessFile outputStream = new RandomAccessFile(file, "rw");
        // make sure old tags written are overwritten
        long start = findTagStart(file);
        outputStream.seek(start);
        int length = 0; // running total of length written so far
        if (title.length() > 30 || artist.length() > 30 || album.length() > 30 || information.length() > 0
                || author.length() > 0 || image.length() > 0) {
            // we need to write an extended tag
            outputStream.writeBytes("LYRICSBEGIN"); //tag starts with this
            length += 11;
            if (information.length() > 0) { // information
                outputStream.writeBytes(INFORMATION);
                outputStream.writeBytes(pad(information.length(), 5)); //fiveDigitNum(information.length()));
                outputStream.writeBytes(information);
                length += 8 + information.length();
            }
            if (author.length() > 0) { // author
                outputStream.writeBytes(AUTHOR);
                outputStream.writeBytes(pad(author.length(), 5)); //fiveDigitNum(author.length()));
                outputStream.writeBytes(author);
                length += 8 + author.length();
            }
            if (image.length() > 0) { // image links
                outputStream.writeBytes(IMAGE);
                outputStream.writeBytes(pad(image.length(), 5)); //fiveDigitNum(image.length()));
                outputStream.writeBytes(image);
                length += 8 + image.length();
            }
            if (title.length() > 30) { // track name
                outputStream.writeBytes(TITLE);
                outputStream.writeBytes(pad(title.length(), 5)); //fiveDigitNum(trackName.length()));
                outputStream.writeBytes(title);
                length += 8 + title.length();
            }
            if (artist.length() > 30) { // artist name
                outputStream.writeBytes(ARTIST);
                outputStream.writeBytes(pad(artist.length(), 5)); //fiveDigitNum(artistName.length()));
                outputStream.writeBytes(artist);
                length += 8 + artist.length();
            }
            if (album.length() > 30) { // album name
                outputStream.writeBytes(ALBUM);
                outputStream.writeBytes(pad(album.length(), 5)); //fiveDigitNum(albumName.length()));
                outputStream.writeBytes(album);
                length += 8 + album.length();
            }
            outputStream.writeBytes(pad(length, 6)); //sixDigitNum(length)); // length of lyrics3 tag
            outputStream.writeBytes("LYRICS200"); // end of lyrics3 tag
        }

        outputStream.writeBytes("TAG"); // begining of Id3 tag
        if (title.length() > 30) { // track name
            outputStream.writeBytes(title.substring(0, 30));
        } else {
            outputStream.writeBytes(title);
            for (int i = title.length(); i < 30; i++) { // padded
                outputStream.writeBytes(" ");
            }
        }
        if (artist.length() > 30) {
            outputStream.writeBytes(artist.substring(0, 30));
        } else {
            outputStream.writeBytes(artist);
            for (int i = artist.length(); i < 30; i++) {
                outputStream.writeBytes(" ");
            }
        }
        if (album.length() > 30) {
            outputStream.writeBytes(album.substring(0, 30));
        } else {
            outputStream.writeBytes(album);
            for (int i = album.length(); i < 30; i++) {
                outputStream.writeBytes(" ");
            }
        }
        if (year.length() > 4) {
            outputStream.writeBytes(year.substring(0, 4));
        } else {
            outputStream.writeBytes(year);
            for (int i = year.length(); i < 4; i++) {
                outputStream.writeBytes(" ");
            }
        }
        int trackNum = Utilities.parseStringAsInt(track);
        if (trackNum > 255) {
            trackNum = -1;
        }

        if (trackNum != -1) {
            if (comment.length() > 28) {
                outputStream.writeBytes(comment.substring(0, 28));
            } else {
                outputStream.writeBytes(comment);
                for (int i = comment.length(); i < 28; i++) {
                    outputStream.writeBytes(" ");
                }
            }
            outputStream.write(0);
            outputStream.write(trackNum);
        } else {
            if (comment.length() > 30) {
                outputStream.writeBytes(comment.substring(0, 30));
            } else {
                outputStream.writeBytes(comment);
                for (int i = comment.length(); i < 30; i++) {
                    outputStream.writeBytes(" ");
                }
            }
        }

        int genreNumber = Utilities.parseStringAsInt(genre);
        if (genreNumber > 255 || genreNumber < 0) {
            genreNumber = GENRE_NONE;
        }
        outputStream.write(genreNumber); // genre not written in ASCII
        // set the length of the file to the end of what we just wrote
        outputStream.setLength(outputStream.getFilePointer());
        outputStream.close(); // close the file for writing
    }

    /**
     * Cherche la position dans le fichier MP3 où les tags ID3 ou Lyrics commencent.
     *
     * @return la position dans le fichier où commencent les tags.
     *
     * @throws UnsupportedEncodingException
     * @throws IOException
     */
    private long findTagStart(File file) throws UnsupportedEncodingException, IOException {
        // see the getLyrics3 tag for documentation.  Methods are very similar.
        String ascii;
        byte[] data;
        int position;
        int lyricsLength;

        RandomAccessFile fileStream = new RandomAccessFile(file, "r");
        long start = fileStream.length();
        fileStream.seek(fileStream.length() - 128);

        data = new byte[3];
        fileStream.read(data);
        ascii = new String(data, "ASCII");

        if (ascii.compareTo("TAG") == 0) {
            start = fileStream.length() - 128;
            fileStream.seek(fileStream.length() - 137);
            data = new byte[9];
            fileStream.read(data);
            ascii = new String(data, "ASCII");

            if (ascii.compareTo("LYRICSEND") == 0) {
                fileStream.seek(fileStream.length() - 5237);
                data = new byte[5100];
                fileStream.read(data);
                ascii = new String(data, "ASCII");
                position = ascii.indexOf("LYRICSBEGIN");
                if (position != -1) {
                    start = fileStream.length() - 5237 + position;
                }
            } else if (ascii.compareTo("LYRICS200") == 0) {
                try {
                    fileStream.seek(fileStream.length() - 143);
                    data = new byte[6];
                    fileStream.read(data);
                    ascii = new String(data, "ASCII");
                    lyricsLength = Integer.parseInt(ascii, 10);
                    fileStream.seek(fileStream.length() - 143 - lyricsLength);
                    data = new byte[lyricsLength];
                    fileStream.read(data);
                    ascii = new String(data, 0, 11, "ASCII");
                    if (ascii.compareTo("LYRICSBEGIN") != 0) {
                        start = fileStream.length() - 128;
                    } else {
                        start = fileStream.length() - 143 - lyricsLength;
                    }
                } catch (NumberFormatException e) {
                    start = fileStream.length() - 128;
                }
            }
        }
        return start;
    }

    /**
     * Pads an integer with leading zeros to the given length. if the integer is already long enough the string
     * representation of the number is returned which may be longer than the given length.
     *
     * @param a integer to pad.
     * @param length the desired length of the string.
     *
     * @return padded string representation of the integer.
     */
    private String pad(int a, int length) {
        StringBuilder builder = new StringBuilder(length);
        String s = "" + a;
        for (int i = s.length(); i < length; i++) {
            builder.append("0");
        }
        builder.append(a);
        return builder.toString();
    }

}
